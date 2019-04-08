/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.widget.column.embedded;

import io.github.jeddict.util.StringUtils;
import io.github.jeddict.relation.mapper.widget.column.ColumnWidget;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAttributeColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.inDev;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ColumnHandler;
import io.github.jeddict.jpa.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAttributeColumnWidget<E extends DBEmbeddedAttributeColumn> extends ColumnWidget<E> {

    public EmbeddedAttributeColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        
    }

    @Override
    protected String evaluateName() {
        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
        Attribute refAttribute = this.getBaseElementSpec().getAttribute();
        if (this.getBaseElementSpec().getAttribute() instanceof ElementCollection) {
            inDev();
        }
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
    protected void updateName(String newName) {
        if (this.getBaseElementSpec().getAttribute() instanceof ElementCollection) {
            inDev();
        } else {
            AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
            attributeOverride.getColumn().setName(newName);
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        Attribute refAttribute = this.getBaseElementSpec().getAttribute();
        if (refAttribute instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
            set.createPropertySet("EMBEDDABLE_COLUMN", this, baseRefAttribute.getColumn(), getPropertyChangeListeners());

            AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
            set.createPropertySet("ATTRIBUTE_OVERRIDE", "ATTR_OVERRIDE", this, attributeOverride.getColumn(), getPropertyChangeListeners());
        } else if (this.getBaseElementSpec().getAttribute() instanceof ElementCollection) {
            //in dev
        }
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (oldValue, value) -> setMultiPropertyName(value));
        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (oldValue, value) -> setMultiPropertyName(value));
        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
        this.addPropertyChangeListener("attr_override_table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
    }
}
