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

import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.ColumnUtil;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class EmbeddedIdDerivedColumnWidget<E extends DBEmbeddedAssociationColumn> extends ForeignKeyWidget<E> {

    public EmbeddedIdDerivedColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
//        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
//            setMultiPropertyName(value);
//        });
//
//        this.addPropertyChangeListener("ass_override_JoinColumn_name", (PropertyChangeListener<String>) (String value) -> {
//            setMultiPropertyName(value);
//        });
//
//        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);
//        this.addPropertyChangeListener("ass_override_table_name", (PropertyChangeListener<String>) this::validateTableName);
    }
    
        public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedIdDerivedColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected void updateName(String newName) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumnOverride();
        column.setName(newName);
        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n)
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        set.createPropertySet("EMBEDDABLE_JOINCOLUMN", this, this.getBaseElementSpec().getJoinColumn(), getPropertyChangeListeners());
        set.createPropertySet("ASSOCIATION_OVERRIDE", this, this.getBaseElementSpec().getJoinColumnOverride(), getPropertyChangeListeners());
    }

    @Override
    protected String evaluateName() {
        return "";
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
