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
package org.netbeans.db.modeler.core.widget.column.embeddedid;

import org.netbeans.db.modeler.core.widget.column.embedded.*;
import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.inDev;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedIdAttributeColumnWidget extends ColumnWidget<DBEmbeddedAttributeColumn> {

    public EmbeddedIdAttributeColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
//        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
//            setMultiPropertyName(value);
//        });
//
//        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (String value) -> {
//            setMultiPropertyName(value);
//        });
//        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);
//        this.addPropertyChangeListener("attr_override_table_name", (PropertyChangeListener<String>) this::validateTableName);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedIdAttributeColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
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
    }
}
