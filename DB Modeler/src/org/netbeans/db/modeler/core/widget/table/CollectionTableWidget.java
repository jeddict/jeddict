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
package org.netbeans.db.modeler.core.widget.table;

import java.awt.Image;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.db.modeler.spec.DBCollectionTable;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.COLLECTION_TABLE;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.COLLECTION_TABLE_ICON_PATH;
import org.netbeans.jpa.modeler.rules.entity.ClassValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.CollectionTable;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class CollectionTableWidget extends TableWidget<DBCollectionTable> {

    public CollectionTableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("CollectionTable_name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setName(value);
            setLabel(name);
        });
    }

    private void setDefaultName() {
        ElementCollection attribute = this.getBaseElementSpec().getAttribute();
        this.name = getDefaultCollectionTableName();
        attribute.getCollectionTable().setName(null);
        setLabel(name);
    }

    @Override
    public void setName(String newName) {
        if (StringUtils.isNotBlank(newName)) {
            this.name = newName.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                ElementCollection attribute = this.getBaseElementSpec().getAttribute();
                attribute.getCollectionTable().setName(this.name);
            }
        } else {
            setDefaultName();
        }

        if (SQLKeywords.isSQL99ReservedKeyword(CollectionTableWidget.this.getName())) {
            this.getSignalManager().fire(ERROR, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getSignalManager().clear(ERROR, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBMapping mapping = CollectionTableWidget.this.getModelerScene().getBaseElementSpec();
        if (mapping.findAllTable(CollectionTableWidget.this.getName()).size() > 1) {
            getSignalManager().fire(ERROR, ClassValidator.NON_UNIQUE_TABLE_NAME);
        } else {
            getSignalManager().clear(ERROR, ClassValidator.NON_UNIQUE_TABLE_NAME);
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        ElementCollection attribute = this.getBaseElementSpec().getAttribute();
        CollectionTable collectionTable = attribute.getCollectionTable();
        set.createPropertySet(this, collectionTable, getPropertyChangeListeners());
        set.createPropertySet("FOREIGN_KEY", this, collectionTable.getForeignKey() , null);
    }

    private String getDefaultCollectionTableName() {
        Entity entity = this.getBaseElementSpec().getEntity();
        ElementCollection attribute = this.getBaseElementSpec().getAttribute();
        return entity.getDefaultTableName().toUpperCase() + "_" + attribute.getName().toUpperCase();
    }

    
    @Override
    public String getIconPath() {
        return COLLECTION_TABLE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return COLLECTION_TABLE;
    }

}
