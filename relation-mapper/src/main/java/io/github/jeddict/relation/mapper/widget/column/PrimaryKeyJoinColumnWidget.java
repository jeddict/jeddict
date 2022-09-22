/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.relation.mapper.spec.DBPrimaryKeyJoinColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.relation.mapper.initializer.ColumnUtil;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class PrimaryKeyJoinColumnWidget extends ForeignKeyWidget<DBPrimaryKeyJoinColumn> {

    public PrimaryKeyJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("PrimaryKeyJoinColumn_name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value));
    }

    @Override
    protected String evaluateName() {
        Id attribute = this.getBaseElementSpec().getAttribute();
        return attribute.getDefaultColumnName();
    }

    @Override
    protected void updateName(String newName) {
        PrimaryKeyJoinColumn column = this.getBaseElementSpec().getJoinColumn();
        column.setName(newName);
        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n) // TODO applicable here ?
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        PrimaryKeyJoinColumn joinColumn = this.getBaseElementSpec().getJoinColumn();
        set.createPropertySet("PK_JOIN_COLUMN", this, joinColumn, getPropertyChangeListeners());
        set.createPropertySet("FOREIGN_KEY", this, joinColumn.getForeignKey() , null);
    }

}
