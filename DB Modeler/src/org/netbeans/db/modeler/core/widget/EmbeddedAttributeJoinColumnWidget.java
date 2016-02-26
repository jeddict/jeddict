/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.core.widget;

import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeJoinColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.inDev;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAttributeJoinColumnWidget extends ForeignKeyWidget<DBEmbeddedAttributeJoinColumn> {

    public EmbeddedAttributeJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setDefaultName();
        });

        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (String value) -> {
            setDefaultName();
        });

        PropertyChangeListener propertyChangeListener = (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    errorHandler.throwError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        };
        this.addPropertyChangeListener("table_name", propertyChangeListener);
        this.addPropertyChangeListener("attr_override_table_name", propertyChangeListener);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedAttributeJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    private String evaluateLabel() {
//        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//        Column embeddableColumn = null;
//        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
//        PersistenceBaseAttribute baseRefAttribute = null;
//        if (refAttribute instanceof PersistenceBaseAttribute) {
//            baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
//            embeddableColumn = baseRefAttribute.getColumn();
//        }
//
//        if (StringUtils.isNotBlank(attributeOverride.getColumn().getName())) {
//            return attributeOverride.getColumn().getName();
//        } else if (StringUtils.isNotBlank(embeddableColumn.getName())) {
//            return embeddableColumn.getName();
//        } else {
//            return baseRefAttribute.getDefaultColumnName();
//        }
return null;

    }

    private void setDefaultName() {
//        this.name = evaluateLabel();
//     AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//            attributeOverride.getColumn().setName(null);
inDev();
        setLabel(name);
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                inDev();
//                AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//                attributeOverride.getColumn().setName(this.name);
            }
        } else {
            setDefaultName();
        }
       
        if (SQLKeywords.isSQL99ReservedKeyword(this.getName())) {
            this.getErrorHandler().throwError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBTable tableSpec = (DBTable) this.getTableWidget().getBaseElementSpec();
        if (tableSpec.findColumns(this.getName()).size() > 1) {
            errorHandler.throwError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        } else {
            errorHandler.clearError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
//        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
//        if (refAttribute instanceof PersistenceBaseAttribute) {
//            PersistenceBaseAttribute baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
//            set.createPropertySet("EMBEDDABLE_COLUMN", this, baseRefAttribute.getColumn(), getPropertyChangeListeners());
//        }
//        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//        set.createPropertySet("ATTRIBUTE_OVERRIDE", "ATTR_OVERRIDE", this, attributeOverride.getColumn(), getPropertyChangeListeners());
    }

}
