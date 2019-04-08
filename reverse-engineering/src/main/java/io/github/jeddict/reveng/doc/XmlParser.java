/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.reveng.doc;

import static io.github.jeddict.jcode.util.AttributeType.STRING;
import io.github.jeddict.jcode.util.Inflector;
import static io.github.jeddict.jcode.util.StringHelper.camelCase;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getModelerFileVersion;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.openide.util.Exceptions;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author jGauravGupta
 */
public class XmlParser extends DocParser {

    public XmlParser(Consumer<String> reporter, boolean jpaSupport, boolean jsonbSupport, boolean jaxbSupport) {
        super(reporter, jpaSupport, jsonbSupport, jaxbSupport);
    }

    @Override
    public EntityMappings generateModel(EntityMappings entityMappings, Reader reader) throws IOException, ProcessInterruptedException {
        try {
            String progressMsg = getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Diagram_Pre"); //NOI18N;
            reporter.accept(progressMsg);

            String version = getModelerFileVersion();

            if (entityMappings == null) {
                entityMappings = EntityMappings.getNewInstance(version);
                entityMappings.setGenerated();
            }

            XMLEventReader xmlEventReader = parse(reader);

            JavaClass javaClass = generateClass(entityMappings, xmlEventReader, jpaSupport);
            javaClass.setXmlRootElement(jaxbSupport);
            entityMappings.setJaxbSupport(jaxbSupport);

        } catch (XMLStreamException ex) {
            Exceptions.printStackTrace(ex);
        }
        return entityMappings;
    }

    private static XMLEventReader parse(Reader reader) throws XMLStreamException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(reader);
        return xmlEventReader;
    }

    private JavaClass generateClass(EntityMappings entityMappings, XMLEventReader xmlEventReader, boolean entity) throws XMLStreamException {
        Inflector inflector = Inflector.getInstance();
        JavaClass rootClass = null;
        Stack<JavaClass> classes = new Stack();
        Stack<String> wrappers = new Stack(); // JAXB @XmlElementWrapper
        while (xmlEventReader.hasNext()) {
            boolean isClass = false;
            boolean isWrapper = false;
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                String key = startElement.getName().getLocalPart();
                XMLEvent peekEvent = xmlEventReader.peek();
                if (startElement.getAttributes().hasNext()) {
                    isClass = true;
                } else if (peekEvent.isCharacters()) {
                    if (peekEvent.asCharacters().getData().trim().isEmpty()) {
                        xmlEventReader.next();
                        if (xmlEventReader.hasNext()) {
                            peekEvent = xmlEventReader.peek();
                            if (peekEvent.isStartElement()) {
                                StartElement peekStartElement = peekEvent.asStartElement();
                                String peekKey = peekStartElement.getName().getLocalPart();
                                if (inflector.singularize(key).equalsIgnoreCase(peekKey)
                                        && !key.equalsIgnoreCase(peekKey)) {
                                    isWrapper = true;
                                } else {
                                    isClass = true;
                                }
                            }
                        }
                    } else {
                        isClass = false;
                    }
                }
                JavaClass prevClass = classes.isEmpty() ? null : classes.peek();
                if (isClass) {
                    String className = firstUpper(camelCase(key));
                    JavaClass clazz = entity ? createEntity(entityMappings, key) : createBeanClass(entityMappings, key);
                    reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));
                    if (rootClass == null) {
                        rootClass = clazz;
                    }
                    classes.push(clazz);

                    if (startElement.getAttributes().hasNext()) {
                        Iterator attributesItr = startElement.getAttributes();
                        while (attributesItr.hasNext()) {
                            Attribute attribute = (Attribute) attributesItr.next();
                            if (entity) {
                                createBasicAttribute((Entity) clazz, STRING, attribute.getName().getLocalPart());
                            } else {
                                createBeanAttribute((BeanClass) clazz, STRING, attribute.getName().getLocalPart());
                            }
                        }
                    }

                    if (prevClass != null) {
                        if (!wrappers.isEmpty()
                                && inflector.singularize(wrappers.peek()).equalsIgnoreCase(key)) {
                            if (entity) {
                                createOneToMany((Entity) prevClass, (Entity) clazz, wrappers.peek());
                            } else {
                                createOneToManyAssociation((BeanClass) prevClass, (BeanClass) clazz, wrappers.peek());
                            }
                        } else {
                            if (entity) {
                                createOneToMany((Entity) prevClass, (Entity) clazz, key);
                            } else {
                                createOneToManyAssociation((BeanClass) prevClass, (BeanClass) clazz, key);
                            }
                        }
                    }
                } else if (isWrapper) {
                    wrappers.push(key);
                } else {
                    if(entity){
                        createBasicAttribute((Entity) prevClass, STRING, key);
                    } else {
                        createBeanAttribute((BeanClass) prevClass, STRING, key);
                    }
                }
            }
            if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                String key = endElement.getName().getLocalPart();
                JavaClass prevClass = classes.isEmpty() ? null : classes.peek();
                if (prevClass != null && prevClass.getName().equals(firstUpper(camelCase(key)))) {
                    classes.pop();
                }
                String peekWrapper = wrappers.isEmpty() ? null : wrappers.peek();
                if (peekWrapper != null && peekWrapper.equalsIgnoreCase(key)) {
                    wrappers.pop();
                }
            }
        }

        return rootClass;
    }

}
