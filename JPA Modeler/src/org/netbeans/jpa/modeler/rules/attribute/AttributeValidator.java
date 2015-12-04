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
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;

public class AttributeValidator {

    public final static String EMPTY_ATTRIBUTE_NAME = "MSG_EmptyAttributeName";
    public final static String NON_UNIQUE_ATTRIBUTE_NAME = "MSG_NonUniqueAttributeName";
    public final static String ATTRIBUTE_NAME_WITH_JPQL_KEYWORD = "MSG_AttrNamedWithJavaPersistenceQLKeyword";
    public final static String ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrTableNamedWithReservedSQLKeyword";
    public final static String ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrColumnNamedWithReservedSQLKeyword";
    public final static String PRIMARYKEY_INVALID_LOCATION = "MSG_PrimaryKeyInvalidLocation";

    public final static String EMBEDDEDID_AND_ID_FOUND = "MSG_EmbeddedIdAndIdFound";
    public final static String MULTIPLE_EMBEDDEDID_FOUND = "MSG_MultipleEmbeddedIdFound";

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
                            PersistenceClassWidget persistenceSuiperClassWidget = (PersistenceClassWidget) superclassWidget;
                            if (persistenceSuiperClassWidget.getEmbeddedIdAttributeWidget() == null && !persistenceSuiperClassWidget.getIdAttributeWidgets().isEmpty()) {
                                errorExist = true;
                                break;
                            }
                        }
                    }
                    if (errorExist) {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().throwError(AttributeValidator.EMBEDDEDID_AND_ID_FOUND);
                    } else {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().clearError(AttributeValidator.EMBEDDEDID_AND_ID_FOUND);
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
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().throwError(AttributeValidator.MULTIPLE_EMBEDDEDID_FOUND);
                    } else {
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().clearError(AttributeValidator.MULTIPLE_EMBEDDEDID_FOUND);
                    }
                }
            }
        }
    }

    public static void scanInheritenceError(EntityWidget entityWidget) {
        if ("SINGLETON".equals(entityWidget.getInheritenceState()) || "ROOT".equals(entityWidget.getInheritenceState())) {
            for (IdAttributeWidget attributeWidget : entityWidget.getIdAttributeWidgets()) {
                attributeWidget.clearError(AttributeValidator.PRIMARYKEY_INVALID_LOCATION);
            }
        } else {
            for (IdAttributeWidget attributeWidget : entityWidget.getIdAttributeWidgets()) {
                attributeWidget.throwError(AttributeValidator.PRIMARYKEY_INVALID_LOCATION);
            }
        }

    }
}
