/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.jcode.util.AttributeType.BOOLEAN_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.DOUBLE_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.FLOAT_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.INT_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.LONG_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.STRING;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getModelerFileVersion;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import static org.openide.util.NbBundle.getMessage;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author jGauravGupta
 */
public class YmlParser extends DocParser {

    public YmlParser(Consumer<String> reporter, boolean jpaSupport, boolean jsonbSupport, boolean jaxbSupport) {
        super(reporter, jpaSupport, jsonbSupport, jaxbSupport);
    }

    @Override
    public EntityMappings generateModel(EntityMappings entityMappings, Reader reader) throws IOException, ProcessInterruptedException {
        String progressMsg = getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Diagram_Pre"); //NOI18N;
        reporter.accept(progressMsg);

        String version = getModelerFileVersion();

        if (entityMappings == null) {
            entityMappings = EntityMappings.getNewInstance(version);
            entityMappings.setGenerated();
        }

        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(reader);
        String className = "RootClass";
        if (root.size() == 1) {
            String key = root.keySet().iterator().next();
            Object value = root.values().iterator().next();
            if (value instanceof Map) {
                className = key;
                root = (Map<String, Object>) value;
            }
        }

        JavaClass javaClass;
        if (jpaSupport) {
            javaClass = generateEntity(entityMappings, className, root);
        } else {
            javaClass = generateClass(entityMappings, className, root);
        }
        javaClass.setXmlRootElement(jaxbSupport);
        entityMappings.setJaxbSupport(jaxbSupport);

        return entityMappings;
    }

    private BeanClass generateClass(EntityMappings entityMappings, String className, Map<String, Object> root) {
        reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));

        BeanClass beanClass = createBeanClass(entityMappings, className);

        root.forEach((key, value) -> {
            Attribute baseAttribute = null;
            if (value instanceof String) {
                baseAttribute = createBeanAttribute(beanClass, STRING, key);
            } else if (value instanceof Number) {
                baseAttribute = createBeanAttribute(beanClass, getNumberType((Number) value), key);
            } else if (value instanceof Boolean) {
                baseAttribute = createBeanAttribute(beanClass, BOOLEAN_WRAPPER, key);
            } else if (value instanceof List) {
                List list = (List) value;
                if (!list.isEmpty()) {
                    Object element = list.get(0);
                    if (element instanceof String) {
                        baseAttribute = createBeanCollection(beanClass, STRING, key);
                    } else if (element instanceof Number) {
                        baseAttribute = createBeanCollection(beanClass, getNumberType((Number) element), key);
                    } else if (element instanceof Boolean) {
                        baseAttribute = createBeanCollection(beanClass, BOOLEAN_WRAPPER, key);
                    } else if (element instanceof Map) {
                        baseAttribute = createOneToManyAssociation(beanClass, generateClass(entityMappings, key, (Map) element), key);
                    } else {
                        baseAttribute = createBeanCollection(beanClass, STRING, key);
                    }
                } else {
                    baseAttribute = createBeanCollection(beanClass, STRING, key);
                }
            } else if (value instanceof Map) {
                baseAttribute = createOneToOneAssociation(beanClass, generateClass(entityMappings, key, (Map) value), key);
            } else {
                baseAttribute = createBeanAttribute(beanClass, STRING, key);
            }
        });
        return beanClass;
    }

    private Entity generateEntity(EntityMappings entityMappings, String className, Map<String, Object> root) {
        reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));

        Entity entity = createEntity(entityMappings, className);
        root.forEach((key, value) -> {
            Attribute baseAttribute;
            if (value instanceof String) {
                baseAttribute = createBasicAttribute(entity, STRING, key);
            } else if (value instanceof Number) {
                baseAttribute = createBasicAttribute(entity, getNumberType((Number) value), key);
            } else if (value instanceof Boolean) {
                baseAttribute = createBasicAttribute(entity, BOOLEAN_WRAPPER, key);
            } else if (value instanceof List) {
                List list = (List) value;
                if (!list.isEmpty()) {
                    Object element = list.get(0);
                    if (element instanceof String) {
                        baseAttribute = createElementCollection(entity, STRING, key);
                    } else if (element instanceof Number) {
                        baseAttribute = createElementCollection(entity, getNumberType((Number) element), key);
                    } else if (element instanceof Boolean) {
                        baseAttribute = createElementCollection(entity, BOOLEAN_WRAPPER, key);
                    } else if (element instanceof Map) {
                        baseAttribute = createOneToMany(entity, generateEntity(entityMappings, key, (Map) element), key);
                    } else {
                        baseAttribute = createElementCollection(entity, STRING, key);
                    }
                } else {
                    baseAttribute = createElementCollection(entity, STRING, key);
                }
            } else if (value instanceof Map) {
                baseAttribute = createOneToOne(entity, generateEntity(entityMappings, key, (Map) value), key);
            } else {
                baseAttribute = createBasicAttribute(entity, STRING, key);
            }
        });
        entityMappings.addEntity(entity);
        return entity;
    }

    private String getNumberType(Number number) {
        String type = LONG_WRAPPER;
        if (number instanceof Integer) {
            type = INT_WRAPPER;
        } else if (number instanceof Long) {
            type = LONG_WRAPPER;
        } else if (number instanceof Float) {
            type = FLOAT_WRAPPER;
        } else if (number instanceof Double) {
            type = DOUBLE_WRAPPER;
        }
        return type;
    }


}
