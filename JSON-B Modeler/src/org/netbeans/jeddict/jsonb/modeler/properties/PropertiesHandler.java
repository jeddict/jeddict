/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jeddict.jsonb.modeler.properties;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.netbeans.jcode.core.util.JavaIdentifiers;
import org.netbeans.jpa.modeler.properties.extend.ClassSelectionPanel;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.ReferenceClass;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbTypeHandler;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbVisibilityHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.widget.property.IPropertyWidget;
import static org.openide.util.NbBundle.getMessage;

public class PropertiesHandler {

    public static EmbeddedPropertySupport getJsonbTypeAdapter(JsonbTypeHandler typeHandler, IPropertyWidget propertyWidget, JPAModelerScene modelerScene) {
        ModelerFile modelerFile = modelerScene.getModelerFile();
        EntityMappings entityMappings = modelerScene.getBaseElementSpec();

        GenericEmbedded entity = new GenericEmbedded("jsonbTypeAdapter", "Type Adapter", getMessage(PropertiesHandler.class, "INFO_JSONB_TYPE_ADAPTER"));
        entity.setEntityEditor(new ClassSelectionPanel(modelerFile));
        entity.setDataListener(new EmbeddedDataListener<ReferenceClass>() {
            @Override
            public void init() {
            }

            @Override
            public ReferenceClass getData() {
                return typeHandler.getJsonbTypeAdapter();
            }

            @Override
            public void setData(ReferenceClass referenceClass) {
                typeHandler.setJsonbTypeAdapter(referenceClass);
            }

            @Override
            public String getDisplay() {
                ReferenceClass referenceClass = typeHandler.getJsonbTypeAdapter();
                if (referenceClass == null) {
                    return EMPTY;
                } else {
                    return JavaIdentifiers.unqualifyGeneric(referenceClass.getName());
                }
            }

        });
        return new EmbeddedPropertySupport(modelerFile, entity);
    }

    public static EmbeddedPropertySupport getJsonbTypeDeserializer(JsonbTypeHandler typeHandler, IPropertyWidget propertyWidget, JPAModelerScene modelerScene) {
        ModelerFile modelerFile = modelerScene.getModelerFile();
        EntityMappings entityMappings = modelerScene.getBaseElementSpec();

        GenericEmbedded entity = new GenericEmbedded("jsonbTypeDeserializer ", "Deserializer", getMessage(PropertiesHandler.class, "INFO_JSONB_TYPE_DESERIALIZER"));
        entity.setEntityEditor(new ClassSelectionPanel(modelerFile));
        entity.setDataListener(new EmbeddedDataListener<ReferenceClass>() {

            @Override
            public void init() {
            }

            @Override
            public ReferenceClass getData() {
                return typeHandler.getJsonbTypeDeserializer();
            }

            @Override
            public void setData(ReferenceClass referenceClass) {
                typeHandler.setJsonbTypeDeserializer(referenceClass);
            }

            @Override
            public String getDisplay() {
                ReferenceClass referenceClass = typeHandler.getJsonbTypeDeserializer();
                if (referenceClass == null) {
                    return EMPTY;
                } else {
                    return JavaIdentifiers.unqualifyGeneric(referenceClass.getName());
                }
            }

        });
        return new EmbeddedPropertySupport(modelerFile, entity);
    }

    public static EmbeddedPropertySupport getJsonbTypeSerializer(JsonbTypeHandler typeHandler, IPropertyWidget propertyWidget, JPAModelerScene modelerScene) {
        ModelerFile modelerFile = modelerScene.getModelerFile();
        EntityMappings entityMappings = modelerScene.getBaseElementSpec();

        GenericEmbedded entity = new GenericEmbedded("jsonbTypeSerializer", "Serializer", getMessage(PropertiesHandler.class, "INFO_JSONB_TYPE_SERIALIZER"));
        entity.setEntityEditor(new ClassSelectionPanel(modelerFile));
        entity.setDataListener(new EmbeddedDataListener<ReferenceClass>() {

            @Override
            public void init() {
            }

            @Override
            public ReferenceClass getData() {
                return typeHandler.getJsonbTypeSerializer();
            }

            @Override
            public void setData(ReferenceClass referenceClass) {
                typeHandler.setJsonbTypeSerializer(referenceClass);
            }

            @Override
            public String getDisplay() {
                ReferenceClass referenceClass = typeHandler.getJsonbTypeSerializer();
                if (referenceClass == null) {
                    return EMPTY;
                } else {
                    return JavaIdentifiers.unqualifyGeneric(referenceClass.getName());
                }
            }
        });
        return new EmbeddedPropertySupport(modelerFile, entity);
    }

    public static EmbeddedPropertySupport getJsonbVisibility(JsonbVisibilityHandler visibilityHandler, IPropertyWidget propertyWidget, JPAModelerScene modelerScene) {
        ModelerFile modelerFile = modelerScene.getModelerFile();

        GenericEmbedded entity = new GenericEmbedded("jsonbVisibility", "Visibility", getMessage(PropertiesHandler.class, "INFO_JSONB_VISIBILITY"));
        entity.setEntityEditor(new ClassSelectionPanel(modelerFile));
        entity.setDataListener(new EmbeddedDataListener<ReferenceClass>() {

            @Override
            public void init() {
            }

            @Override
            public ReferenceClass getData() {
                return visibilityHandler.getJsonbVisibility();
            }

            @Override
            public void setData(ReferenceClass referenceClass) {
                visibilityHandler.setJsonbVisibility(referenceClass);
            }

            @Override
            public String getDisplay() {
                ReferenceClass referenceClass = visibilityHandler.getJsonbVisibility();
                if (referenceClass == null) {
                    return EMPTY;
                } else {
                    return JavaIdentifiers.unqualifyGeneric(referenceClass.getName());
                }
            }
        });
        return new EmbeddedPropertySupport(modelerFile, entity);
    }

}
