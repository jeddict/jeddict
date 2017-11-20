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

import org.apache.commons.lang3.StringUtils;
import org.netbeans.db.modeler.spec.DBDiscriminatorColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class DiscriminatorColumnWidget extends ColumnWidget<DBDiscriminatorColumn> {

    public DiscriminatorColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value)); 
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        DiscriminatorColumn discriminatorColumn = this.getBaseElementSpec().getAttribute();
        set.createPropertySet(this, discriminatorColumn, getPropertyChangeListeners());
    }

    @Override
    protected String evaluateName() {
        DiscriminatorColumn discriminatorColumn = this.getBaseElementSpec().getAttribute();
        if (StringUtils.isBlank(discriminatorColumn.getName())) {
            return "DTYPE";
        } else {
            return discriminatorColumn.getName();
        }
    }

    @Override
    protected void updateName(String name) {
        DiscriminatorColumn discriminatorColumn = this.getBaseElementSpec().getAttribute();
        discriminatorColumn.setName(name);
    }
}
