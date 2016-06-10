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
package org.netbeans.db.modeler.core.widget.column;

import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class BasicColumnWidget extends ColumnWidget<DBColumn<Attribute>> {

    public BasicColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);//(PropertyChangeListener<String>)
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
            set.createPropertySet(this, baseAttribute.getColumn(), getPropertyChangeListeners());
        }
    }

    @Override
    protected String evaluateName() {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof ColumnHandler) {
            return ((ColumnHandler) attribute).getDefaultColumnName();
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
    }

    @Override
    protected void updateName(String newName) {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof ColumnHandler) {
            ColumnHandler baseAttribute = (ColumnHandler) attribute;
            baseAttribute.getColumn().setName(this.name);
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
    }
}
