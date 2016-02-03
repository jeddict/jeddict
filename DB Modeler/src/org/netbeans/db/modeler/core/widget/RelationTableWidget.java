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
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.spec.DBRelationTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class RelationTableWidget extends TableWidget<DBRelationTable> {

    public RelationTableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("JoinTable_name", (PropertyChangeListener<String>) (String value) -> {
            setName(value);
            setLabel(name);
        });
    }

    private void setDefaultName() {
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
        this.name = getDefaultJoinTableName();
        attribute.getJoinTable().setName(null);
        setLabel(name);//setLabel escaped by inplace editor in case of blank value
    }

    @Override
    public void setName(String name) {

        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
                attribute.getJoinTable().setName(this.name);
            }
            
        } else {
          setDefaultName();
        }
        if (SQLKeywords.isSQL99ReservedKeyword(RelationTableWidget.this.getName())) {
            this.getErrorHandler().throwError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBMapping mapping = RelationTableWidget.this.getModelerScene().getBaseElementSpec();
        if (mapping.findAllTable(RelationTableWidget.this.getName()).size() > 1) {
            getErrorHandler().throwError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
        } else {
            getErrorHandler().clearError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
        JoinTable joinTable = attribute.getJoinTable();
        set.createPropertySet(this, joinTable, getPropertyChangeListeners());
    }
    
    private String getDefaultJoinTableName(){
        Entity entity = this.getBaseElementSpec().getEntity();
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
            if(attribute.isOwner()){
            return entity.getTableName().toUpperCase() + "_" + attribute.getConnectedEntity().getTableName().toUpperCase();
            } else {
                 return attribute.getConnectedEntity().getTableName().toUpperCase() + "_" + entity.getTableName().toUpperCase();
            }
    }

}
