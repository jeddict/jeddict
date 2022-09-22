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

import static io.github.jeddict.jcode.util.AttributeType.BOOLEAN;
import static io.github.jeddict.jcode.util.AttributeType.DOUBLE;
import static io.github.jeddict.jcode.util.AttributeType.INT;
import static io.github.jeddict.jcode.util.AttributeType.LONG;
import static io.github.jeddict.jcode.util.AttributeType.STRING;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getModelerFileVersion;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.FALSE;
import static jakarta.json.JsonValue.ValueType.TRUE;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author jGauravGupta
 */
public class JsonParser extends DocParser {

    public JsonParser(Consumer<String> reporter, boolean jpaSupport, boolean jsonbSupport, boolean jaxbSupport) {
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

        JsonReader jsonReader = Json.createReader(reader);
        JsonObject jsonObject = jsonReader.readObject();
        JavaClass javaClass;
        if (jpaSupport) {
            javaClass = generateEntity(entityMappings, "RootClass", jsonObject);
        } else {
            javaClass = generateClass(entityMappings, "RootClass", jsonObject);
        }
        javaClass.setXmlRootElement(jaxbSupport);
        entityMappings.setJaxbSupport(jaxbSupport);

        return entityMappings;
    }

    private BeanClass generateClass(EntityMappings entityMappings, String className, JsonObject jsonObject) {
        reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));

        BeanClass beanClass = createBeanClass(entityMappings, className);
        jsonObject.forEach((key, value) -> {
            Attribute baseAttribute;
            if (value instanceof JsonString) {
                baseAttribute = createBeanAttribute(beanClass, STRING, key);
            } else if (value instanceof JsonNumber) {
                baseAttribute = createBeanAttribute(beanClass, getJsonNumberType((JsonNumber) value), key);
            } else if (value instanceof JsonArray) {
                JsonArray jsonArray = (JsonArray) value;
                if (!jsonArray.isEmpty()) {
                    JsonValue arrayElement = jsonArray.get(0);
                    if (arrayElement instanceof JsonString) {
                        baseAttribute = createBeanCollection(beanClass, STRING, key);
                    } else if (arrayElement instanceof JsonNumber) {
                        baseAttribute = createBeanCollection(beanClass, getJsonNumberType((JsonNumber) arrayElement), key);
                    } else if (arrayElement instanceof JsonObject) {
                        baseAttribute = createOneToManyAssociation(beanClass, generateClass(entityMappings, key, (JsonObject) arrayElement), key);
                    } else {
                        baseAttribute = createBeanCollection(beanClass, STRING, key);
                    }
                } else {
                    baseAttribute = createBeanCollection(beanClass, STRING, key);
                }
            } else if (value instanceof JsonObject) {
                baseAttribute = createOneToOneAssociation(beanClass, generateClass(entityMappings, key, (JsonObject) value), key);
            } else if (value instanceof JsonValue) {
                if (((JsonValue) value).getValueType() == TRUE || ((JsonValue) value).getValueType() == FALSE) {
                    baseAttribute = createBeanAttribute(beanClass, BOOLEAN, key);
                } else {
                    baseAttribute = createBeanAttribute(beanClass, STRING, key);
                }
            } else {
                baseAttribute = createBeanAttribute(beanClass, STRING, key);
            }
        });
        return beanClass;
    }

    private Entity generateEntity(EntityMappings entityMappings, String className, JsonObject jsonObject) {
        reporter.accept(getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Parsing", className));

        Entity entity = createEntity(entityMappings, className);
        jsonObject.forEach((key, value) -> {
            Attribute baseAttribute;
            if (value instanceof JsonString) {
                baseAttribute = createBasicAttribute(entity, STRING, key);
            } else if (value instanceof JsonNumber) {
                baseAttribute = createBasicAttribute(entity, getJsonNumberType((JsonNumber) value), key);
            } else if (value instanceof JsonArray) {
                JsonArray jsonArray = (JsonArray) value;
                if (!jsonArray.isEmpty()) {
                    JsonValue arrayElement = jsonArray.get(0);
                    if (arrayElement instanceof JsonString) {
                        baseAttribute = createElementCollection(entity, STRING, key);
                    } else if (arrayElement instanceof JsonNumber) {
                        baseAttribute = createElementCollection(entity, getJsonNumberType((JsonNumber) arrayElement), key);
                    } else if (arrayElement instanceof JsonObject) {
                        baseAttribute = createOneToMany(entity, generateEntity(entityMappings, key, (JsonObject) arrayElement), key);
                    } else {
                        baseAttribute = createElementCollection(entity, STRING, key);
                    }
                } else {
                    baseAttribute = createElementCollection(entity, STRING, key);
                }
            } else if (value instanceof JsonObject) {
                baseAttribute = createOneToOne(entity, generateEntity(entityMappings, key, (JsonObject) value), key);
            } else if (value instanceof JsonValue) {
                if (((JsonValue) value).getValueType() == TRUE || ((JsonValue) value).getValueType() == FALSE) {
                    baseAttribute = createBasicAttribute(entity, BOOLEAN, key);
                } else {
                    baseAttribute = createBasicAttribute(entity, STRING, key);
                }
            } else {
                baseAttribute = createBasicAttribute(entity, STRING, key);
            }
        });
        return entity;
    }

    private String getJsonNumberType(JsonNumber jsonNumber) {
        String type = LONG;
        if (null != jsonNumber.getClass().getSimpleName()) {
            switch (jsonNumber.getClass().getSimpleName()) {
                case "JsonIntNumber":
                    type = INT;
                    break;
                case "JsonLongNumber":
                    type = LONG;
                    break;
                default:
                    type = DOUBLE;
                    break;
            }
        }
        return type;
    }

}
