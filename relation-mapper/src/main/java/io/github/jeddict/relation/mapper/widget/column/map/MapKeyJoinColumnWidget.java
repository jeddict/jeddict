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
package io.github.jeddict.relation.mapper.widget.column.map;

import io.github.jeddict.relation.mapper.widget.column.ForeignKeyWidget;
import io.github.jeddict.relation.mapper.spec.DBMapKeyJoinColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.relation.mapper.initializer.ColumnUtil;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class MapKeyJoinColumnWidget extends ForeignKeyWidget<DBMapKeyJoinColumn> {

    public MapKeyJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    

    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        JoinColumn joinColumn = this.getBaseElementSpec().getJoinColumn();
        set.createPropertySet("JOIN_COLUMN", this, joinColumn, null);
        set.createPropertySet("FOREIGN_KEY", this, joinColumn.getForeignKey() , null);
    }
    
    @Override
    protected void updateName(String name) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumn();
        column.setName(name);
        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n) // TODO applicable here ?
    }

    @Override
    protected String evaluateName() {
//        DBTable table = (DBTable) this.getTableWidget().getBaseElementSpec();
////        Id id = (Id) this.getBaseElementSpec().getReferenceColumn().getAttribute();
////        return JoinColumnWidget.evaluateName(table, id);
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof MapKeyHandler) {
            return ((MapKeyHandler) attribute).getDefaultMapKeyColumnName();
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
    }

}
