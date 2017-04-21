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
package org.netbeans.db.modeler.core.widget.column.map;

import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.spec.DBMapKeyJoinColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.ColumnUtil;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class MapKeyJoinColumnWidget extends ForeignKeyWidget<DBMapKeyJoinColumn> {

    public MapKeyJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
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
