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

import io.github.jeddict.relation.mapper.widget.column.ForeignKeyWidget;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAttributeJoinColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.inDev;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAttributeJoinColumnWidget extends ForeignKeyWidget<DBEmbeddedAttributeJoinColumn> {

    public EmbeddedAttributeJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value));
        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value));
        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
        this.addPropertyChangeListener("attr_override_table_name", (PropertyChangeListener<String>) (oldValue, value) -> validateTableName(value));
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
