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
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class BasicColumnWidget extends ColumnWidget<DBColumn> {

    public BasicColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setName(value);
            setLabel(name);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    errorHandler.throwError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
    }
    
    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(BasicColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }
        

    @Override
    public void createPropertySet(ElementPropertySet set) {
            Attribute attribute = this.getBaseElementSpec().getAttribute();
            if (attribute instanceof PersistenceBaseAttribute) {
                PersistenceBaseAttribute baseAttribute = (PersistenceBaseAttribute) attribute;
                if (baseAttribute.getColumn() == null) {
                    baseAttribute.setColumn(new Column());
                }
                set.createPropertySet(this, baseAttribute.getColumn(), getPropertyChangeListeners());
            }
    }
    
       private void setDefaultName() {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof PersistenceBaseAttribute) {
            this.name = ((PersistenceBaseAttribute) attribute).getDefaultColumnName();
            ((PersistenceBaseAttribute) attribute).getColumn().setName(null);
        } else if (attribute instanceof ElementCollection) {
            this.name = ((ElementCollection) attribute).getDefaultColumnName();
            ((ElementCollection) attribute).getColumn().setName(null);
        }
        setLabel(name);
    }

           @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");

            if (this.getModelerScene().getModelerFile().isLoaded()) {
                Attribute attribute = this.getBaseElementSpec().getAttribute();

                if (attribute instanceof PersistenceBaseAttribute) {
                    PersistenceBaseAttribute baseAttribute = (PersistenceBaseAttribute) attribute;
                    baseAttribute.getColumn().setName(this.name);
                }
            }
        } else {
            setDefaultName();
        }
        if (SQLKeywords.isSQL99ReservedKeyword(BasicColumnWidget.this.getName())) {
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

}
