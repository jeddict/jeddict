/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

            JavaClass javaClass;
            if (jpaSupport) {
                javaClass = generateEntity(entityMappings, xmlEventReader);
            } else {
                javaClass = generateClass(entityMappings, xmlEventReader);
            }
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

    private JavaClass generateClass(EntityMappings entityMappings, XMLEventReader xmlEventReader) throws XMLStreamException {

        BeanClass rootClass = null;

        Stack<BeanClass> beanClasses = new Stack();
        while (xmlEventReader.hasNext()) {
            boolean clazz = false;
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                String key = startElement.getName().getLocalPart();
                XMLEvent peekEvent = xmlEventReader.peek();
                if (startElement.getAttributes().hasNext()) {
                    clazz = true;
                } else if (peekEvent.isCharacters()) {
                    if (peekEvent.asCharacters().getData().trim().isEmpty()) {
                        xmlEventReader.next();
                        if (xmlEventReader.hasNext()) {
                            peekEvent = xmlEventReader.peek();
                            if (peekEvent.isStartElement()) {
                                clazz = true;
                            }
                        }
                    } else {
                        clazz = false;
                    }
                }
                BeanClass prevBeanClass = beanClasses.isEmpty() ? null : beanClasses.peek();
                if (clazz) {
                    String className = firstUpper(camelCase(key));
                    BeanClass beanClass = createBeanClass(entityMappings, key);
                    reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));
                    if (rootClass == null) {
                        rootClass = beanClass;
                    }
                    beanClasses.push(beanClass);

                    if (startElement.getAttributes().hasNext()) {
                        Iterator attributesItr = startElement.getAttributes();
                        while (attributesItr.hasNext()) {
                            javax.xml.stream.events.Attribute attribute = (javax.xml.stream.events.Attribute) attributesItr.next();
                            createBeanAttribute(beanClass, STRING, attribute.getName().getLocalPart());
                        }
                    }

                    if (prevBeanClass != null) {
                        createOneToManyAssociation(prevBeanClass, beanClass, key);
                    }
                } else {
                    createBeanAttribute(prevBeanClass, STRING, key);
                }
            }
            if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                String key = endElement.getName().getLocalPart();
                BeanClass prevBeanClass = beanClasses.isEmpty() ? null : beanClasses.peek();
                if (prevBeanClass != null && prevBeanClass.getName().equals(firstUpper(camelCase(key)))) {
                    beanClasses.pop();
                }
            }
        }

        return rootClass;
    }

    private Entity generateEntity(EntityMappings entityMappings, XMLEventReader xmlEventReader) throws XMLStreamException {

        Entity rootEntity = null;
        Stack<Entity> entities = new Stack();
        while (xmlEventReader.hasNext()) {
            boolean clazz = false;
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                String key = startElement.getName().getLocalPart();
                XMLEvent peekEvent = xmlEventReader.peek();
                if (startElement.getAttributes().hasNext()) {
                    clazz = true;
                } else if (peekEvent.isCharacters()) {
                    if (peekEvent.asCharacters().getData().trim().isEmpty()) {
                        xmlEventReader.next();
                        if (xmlEventReader.hasNext()) {
                            peekEvent = xmlEventReader.peek();
                            if (peekEvent.isStartElement()) {
                                clazz = true;
                            }
                        }
                    } else {
                        clazz = false;
                    }
                }
                Entity prevEntity = entities.isEmpty() ? null : entities.peek();
                if (clazz) {
                    String className = firstUpper(camelCase(key));
                    Entity entity = createEntity(entityMappings, key);
                    reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));
                    if (rootEntity == null) {
                        rootEntity = entity;
                    }
                    entities.push(entity);

                    if (startElement.getAttributes().hasNext()) {
                        Iterator attributesItr = startElement.getAttributes();
                        while (attributesItr.hasNext()) {
                            javax.xml.stream.events.Attribute attribute = (javax.xml.stream.events.Attribute) attributesItr.next();
                            createBasicAttribute(entity, STRING, attribute.getName().getLocalPart());
                        }
                    }

                    if (prevEntity != null) {
                        createOneToMany(prevEntity, entity, key);
                    }
                } else {
                    createBasicAttribute(prevEntity, STRING, key);
                }
            }
            if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                String key = endElement.getName().getLocalPart();
                Entity prevEntity = entities.isEmpty() ? null : entities.peek();
                if (prevEntity != null && prevEntity.getName().equals(firstUpper(camelCase(key)))) {
                    entities.pop();
                }
            }
        }

        return rootEntity;
    }

}
