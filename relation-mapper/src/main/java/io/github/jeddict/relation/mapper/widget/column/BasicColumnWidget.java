/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.widget.column;

import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ColumnHandler;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class BasicColumnWidget<E extends DBColumn<Attribute>> extends ColumnWidget<E> {

    public BasicColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof ColumnHandler) {  // cover PersistenceBaseAttribute + ElementCollection
            ColumnHandler baseAttribute = (ColumnHandler) attribute;
            set.createPropertySet(this, baseAttribute.getColumn(), getPropertyChangeListeners());
            this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value));
            this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
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
            baseAttribute.getColumn().setName(newName);
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
    }
}
