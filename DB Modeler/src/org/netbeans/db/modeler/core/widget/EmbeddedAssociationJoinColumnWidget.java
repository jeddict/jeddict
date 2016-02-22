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

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAssociationJoinColumnWidget extends EmbeddedAssociationColumnWidget<DBEmbeddedAssociationJoinColumn> {

    public EmbeddedAssociationJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
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
        pinWidgetInfo.setDocumentId(EmbeddedAssociationJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    private void setDefaultName() {
        String name = getDefaultJoinColumnName();
        updateJoinColumn(null);
        this.name = null;
        setLabel(name);
    }
    
    private void updateJoinColumn(String newName){ 
     JoinColumn column = this.getBaseElementSpec().getJoinColumnOverride();
        column.setName(newName);
    }

    private String getDefaultJoinColumnName() {
        DBTable table = (DBTable) this.getTableWidget().getBaseElementSpec();
        Entity entity = table.getEntity();
        List<Id> id = (List<Id>)entity.getAttributes().getId();
        return entity.getDefaultTableName().toUpperCase() + "_" + id.get(0).getName().toUpperCase();
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                JoinColumn joinColumn = this.getBaseElementSpec().getJoinColumnOverride();
                joinColumn.setName(this.name);
            }
        } else {
            setDefaultName();
        }
        if (SQLKeywords.isSQL99ReservedKeyword(name)) {
            this.getErrorHandler().throwError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBTable tableSpec = (DBTable) this.getTableWidget().getBaseElementSpec();
        if (tableSpec.findColumns(name).size() > 1) {
            errorHandler.throwError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        } else {
            errorHandler.clearError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
//        JoinColumn joinColumn = this.getBaseElementSpec().getJoinColumn();
//        set.createPropertySet("EMBEDDABLE_JOINCOLUMN", this, joinColumn, getPropertyChangeListeners());

        JoinColumn joinColumnOverride = this.getBaseElementSpec().getJoinColumnOverride();
        set.createPropertySet("ASSOCIATION_OVERRIDE", this, joinColumnOverride, getPropertyChangeListeners());
    }
    
}
