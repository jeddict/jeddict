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
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class BaseTableWidget extends TableWidget<DBTable> {
    
    public BaseTableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String value) -> {
             setName(value);
        });
    }
    
    private void setDefaultName() {
        Entity entity = this.getBaseElementSpec().getEntity();
        this.name = entity.getDefaultTableName();
        entity.getTable().setName(null);
        setLabel(name);
    }
    
    @Override
    public void setName(String name) {
        
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                Entity entity = this.getBaseElementSpec().getEntity();
                entity.getTable().setName(this.name);
            }
        } else {
           setDefaultName(); 
        }
        
        if (SQLKeywords.isSQL99ReservedKeyword(BaseTableWidget.this.getName())) {
            this.getErrorHandler().throwError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }
        
        DBMapping mapping = BaseTableWidget.this.getModelerScene().getBaseElementSpec();
        if (mapping.findAllTable(BaseTableWidget.this.getName()).size() > 1) {
            getErrorHandler().throwError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
        } else {
            getErrorHandler().clearError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
        }
        
    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        Entity entity = this.getBaseElementSpec().getEntity();
        set.createPropertySet(this, entity.getTable(), getPropertyChangeListeners());
    }
    
}
