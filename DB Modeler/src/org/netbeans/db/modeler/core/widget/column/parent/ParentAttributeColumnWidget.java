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
package org.netbeans.db.modeler.core.widget.column.parent;

import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.spec.DBParentAttributeColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class ParentAttributeColumnWidget extends ColumnWidget<DBParentAttributeColumn> {

    public ParentAttributeColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
        this.addPropertyChangeListener("attr_override_table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
    }

    @Override
    protected String evaluateName() {
        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
        Attribute refAttribute = this.getBaseElementSpec().getAttribute();
        if (refAttribute instanceof ColumnHandler) {
            ColumnHandler baseRefAttribute = (ColumnHandler) refAttribute;
            Column column = baseRefAttribute.getColumn();

            if (StringUtils.isNotBlank(attributeOverride.getColumn().getName())) {
                return attributeOverride.getColumn().getName();
            } else if (StringUtils.isNotBlank(column.getName())) {
                return column.getName();
            } else {
                return baseRefAttribute.getDefaultColumnName();
            }
        } else {
            throw new IllegalStateException("Invalid attribute type : " + refAttribute.getClass().getSimpleName());
        }
    }

    @Override
    protected void updateName(String name) {
        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
        attributeOverride.getColumn().setName(name);
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        Attribute refAttribute = this.getBaseElementSpec().getAttribute();
        PersistenceBaseAttribute baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
        set.createPropertySet("COLUMN", this, baseRefAttribute.getColumn(), getPropertyChangeListeners());

        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
        set.createPropertySet("ATTRIBUTE_OVERRIDE", "ATTR_OVERRIDE", this, attributeOverride.getColumn(), getPropertyChangeListeners());
    }
}
