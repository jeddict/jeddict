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

import org.netbeans.db.modeler.spec.DBPrimaryKeyJoinColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ColumnHandler;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class PrimaryKeyJoinColumnWidget extends ForeignKeyWidget<DBPrimaryKeyJoinColumn> {

    public PrimaryKeyJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("JoinColumn_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(PrimaryKeyJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected String evaluateName() {
        Id attribute = this.getBaseElementSpec().getAttribute();
        return attribute.getDefaultColumnName();
    }

    @Override
    protected void updateName(String newName) {
        Id attribute = this.getBaseElementSpec().getAttribute();
        ColumnHandler baseAttribute = (ColumnHandler) attribute;
        baseAttribute.getColumn().setName(this.name);
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        Column column = this.getBaseElementSpec().getAttribute().getColumn();
        set.createPropertySet(this, column, getPropertyChangeListeners());
    }

}
