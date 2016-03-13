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
package org.netbeans.db.modeler.core.widget.column.embedded;

import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeJoinColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.inDev;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAttributeJoinColumnWidget extends ForeignKeyWidget<DBEmbeddedAttributeJoinColumn> {

    public EmbeddedAttributeJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });

        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);
        this.addPropertyChangeListener("attr_override_table_name", (PropertyChangeListener<String>) this::validateTableName);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedAttributeJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected void updateName(String name) {
        //     AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//            attributeOverride.getColumn().setName(null);
//        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n)
    }

    @Override
    protected String evaluateName() {
        //        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//        Column embeddableColumn = null;
//        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
//        PersistenceBaseAttribute baseRefAttribute = null;
//        if (refAttribute instanceof PersistenceBaseAttribute) {
//            baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
//            embeddableColumn = baseRefAttribute.getColumn();
//        }
//
//        if (StringUtils.isNotBlank(attributeOverride.getColumn().getName())) {
//            return attributeOverride.getColumn().getName();
//        } else if (StringUtils.isNotBlank(embeddableColumn.getName())) {
//            return embeddableColumn.getName();
//        } else {
//            return baseRefAttribute.getDefaultColumnName();
//        }
        return null;
    }

    @Override
    protected boolean prePersistName() {
        inDev();
        return false;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
//        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
//        if (refAttribute instanceof PersistenceBaseAttribute) {
//            PersistenceBaseAttribute baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
//            set.createPropertySet("EMBEDDABLE_COLUMN", this, baseRefAttribute.getColumn(), getPropertyChangeListeners());
//        }
//        AttributeOverride attributeOverride = this.getBaseElementSpec().getAttributeOverride();
//        set.createPropertySet("ATTRIBUTE_OVERRIDE", "ATTR_OVERRIDE", this, attributeOverride.getColumn(), getPropertyChangeListeners());
    }

}
