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
import org.netbeans.db.modeler.spec.DBParentAssociationColumn;
import org.netbeans.db.modeler.spec.DBParentColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public abstract class ParentAssociationColumnWidget<E extends DBParentAssociationColumn> extends ForeignKeyWidget<E> {

    public ParentAssociationColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("ass_override_column_name", (PropertyChangeListener<String>) (String value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);
        this.addPropertyChangeListener("ass_override_table_name",(PropertyChangeListener<String>)this::validateTableName);
    }
    
        @Override
    protected void updateName(String name) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumnOverride();
        column.setName(name);
    }

        @Override
    public void createPropertySet(ElementPropertySet set) {
        set.createPropertySet("PARENT_JOINCOLUMN", this, this.getBaseElementSpec().getJoinColumn(), getPropertyChangeListeners());
        set.createPropertySet("ASSOCIATION_OVERRIDE", this, this.getBaseElementSpec().getJoinColumnOverride(), getPropertyChangeListeners());
    }
}
