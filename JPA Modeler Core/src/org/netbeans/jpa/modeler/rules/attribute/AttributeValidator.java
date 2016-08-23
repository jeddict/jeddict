/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.rules.attribute;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jcode.core.util.JavaUtil;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;

public class AttributeValidator {

    public final static String EMPTY_ATTRIBUTE_NAME = "MSG_EmptyAttributeName";
    public final static String NON_UNIQUE_ATTRIBUTE_NAME = "MSG_NonUniqueAttributeName";
    public final static String NON_UNIQUE_COLUMN_NAME = "MSG_NonUniqueColumnName";
    public final static String INVALID_ATTRIBUTE_NAME = "MSG_InvalidAttrName";
    public final static String ATTRIBUTE_NAME_WITH_JPQL_KEYWORD = "MSG_AttrNamedWithJavaPersistenceQLKeyword";
    public final static String ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrTableNamedWithReservedSQLKeyword";
    public final static String ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrColumnNamedWithReservedSQLKeyword";
    public final static String PRIMARYKEY_INVALID_LOCATION = "MSG_PrimaryKeyInvalidLocation";
    public final static String INVALID_MAPKEY_ATTRIBUTE = "MSG_InvalidMapKeyAttribute";

    public final static String EMBEDDEDID_AND_ID_FOUND = "MSG_EmbeddedIdAndIdFound";
    public final static String MULTIPLE_EMBEDDEDID_FOUND = "MSG_MultipleEmbeddedIdFound";
    public final static String TABLE_NOT_PRESENT = "MSG_TableNotPresent";

    public static void validateEmbeddedIdAndIdFound(PersistenceClassWidget peristenceClassWidget_In) {
        List<JavaClassWidget> javaClassWidgets = peristenceClassWidget_In.getAllSubclassWidgets();
        javaClassWidgets.add(peristenceClassWidget_In);
        for (JavaClassWidget javaClassWidget : javaClassWidgets) {
            if (javaClassWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) javaClassWidget;
                if (persistenceClassWidget.getEmbeddedIdAttributeWidget() != null) {

                    List<JavaClassWidget> superclassWidgets = persistenceClassWidget.getAllSuperclassWidget();
                    boolean errorExist = false;
                    for (JavaClassWidget superclassWidget : superclassWidgets) {
                        if (superclassWidget instanceof PersistenceClassWidget) {
                            PersistenceClassWidget persistenceSuperClassWidget = (PersistenceClassWidget) superclassWidget;
                            if (persistenceSuperClassWidget.getEmbeddedIdAttributeWidget() == null && !persistenceSuperClassWidget.getIdAttributeWidgets().isEmpty()) {
                                errorExist = true;
                                break;
                            }
                        }
                    }
                    if (errorExist) {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().getErrorHandler().throwSignal(AttributeValidator.EMBEDDEDID_AND_ID_FOUND);
                    } else {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().getErrorHandler().clearSignal(AttributeValidator.EMBEDDEDID_AND_ID_FOUND);
                    }

                }
            }
        }
    }

    public static void validateMultipleEmbeddedIdFound(PersistenceClassWidget peristenceClassWidget_In) {
        List<JavaClassWidget> javaClassWidgets = peristenceClassWidget_In.getAllSubclassWidgets();
        javaClassWidgets.add(peristenceClassWidget_In);
        for (JavaClassWidget javaClassWidget : javaClassWidgets) {
            if (javaClassWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) javaClassWidget;
                if (persistenceClassWidget.getEmbeddedIdAttributeWidget() != null) {
                    if (persistenceClassWidget.getAllEmbeddedIdAttributeWidgets().size() > 1) {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().getErrorHandler().throwSignal(AttributeValidator.MULTIPLE_EMBEDDEDID_FOUND);
                    } else {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().getErrorHandler().clearSignal(AttributeValidator.MULTIPLE_EMBEDDEDID_FOUND);
                    }
                }
            }
        }
    }

    public static void scanInheritenceError(EntityWidget entityWidget) {
        if (entityWidget.getInheritenceState() == SINGLETON || entityWidget.getInheritenceState() == ROOT) {
            for (IdAttributeWidget attributeWidget : entityWidget.getIdAttributeWidgets()) {
                attributeWidget.getErrorHandler().clearSignal(AttributeValidator.PRIMARYKEY_INVALID_LOCATION);
            }
        } else {
            for (IdAttributeWidget attributeWidget : entityWidget.getIdAttributeWidgets()) {
                attributeWidget.getErrorHandler().throwSignal(AttributeValidator.PRIMARYKEY_INVALID_LOCATION);
            }
        }

    }
    
    public static void scanMapKeyHandlerError(AttributeWidget attributeWidget) {
        if (attributeWidget.getBaseElementSpec() instanceof MapKeyHandler) {
            if (JavaUtil.isMap(((CollectionTypeHandler) attributeWidget.getBaseElementSpec()).getCollectionType())) {
                MapKeyHandler mapKeyHandler = (MapKeyHandler) attributeWidget.getBaseElementSpec();
                if (mapKeyHandler.getMapKeyType() == MapKeyType.EXT && mapKeyHandler.getMapKeyAttribute() == null) {
                    attributeWidget.getErrorHandler().throwSignal(AttributeValidator.INVALID_MAPKEY_ATTRIBUTE);
                } else if (mapKeyHandler.getMapKeyType() == MapKeyType.NEW && mapKeyHandler.getMapKeyEntity() == null
                        && mapKeyHandler.getMapKeyEmbeddable() == null && StringUtils.isEmpty(mapKeyHandler.getMapKeyAttributeType())) {
                    attributeWidget.getErrorHandler().throwSignal(AttributeValidator.INVALID_MAPKEY_ATTRIBUTE);
                } else {
                    attributeWidget.getErrorHandler().clearSignal(AttributeValidator.INVALID_MAPKEY_ATTRIBUTE);
                }
            }
        }
    }
}
