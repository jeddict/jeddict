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
package org.netbeans.db.modeler.core.widget;

import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBForeignKey;
import org.netbeans.db.modeler.spec.DBParentAssociationColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public abstract class ParentAssociationColumnWidget<E extends DBParentAssociationColumn> extends ForeignKeyWidget<E> {

    public ParentAssociationColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("ass_override_column_name", (PropertyChangeListener<String>) (String value) -> {
            setMultiPropertyName(value);
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) this::validateTableName);
        this.addPropertyChangeListener("ass_override_table_name",(PropertyChangeListener<String>)this::validateTableName);
    }
    
        @Override
    protected void updateName(String name) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumnOverride();
        column.setName(name);
        IPrimaryKeyWidget primaryKeyWidget = this.getReferenceFlowWidget().get(0).getReferenceColumnWidget();//TODO get(n)
        if(primaryKeyWidget.getTableWidget().getPrimaryKeyWidgets().size() > 1){
            column.setReferencedColumnName(primaryKeyWidget.getName());
            syncronizeCompositeKeyJoincolumn(((PrimaryKeyWidget)primaryKeyWidget).getTableWidget(),this.getTableWidget());
        }
    }

    /**
     * Exception Description: 
     * The @JoinColumns on the annotated element [method get] from the entity class [class Employee] is incomplete. 
     * When the source entity class uses a composite primary key, a @JoinColumn must be specified for each join column using the 
     * @JoinColumns. Both the name and the referencedColumnName elements must be specified in each such @JoinColumn.
     */
    private void syncronizeCompositeKeyJoincolumn(TableWidget sourceTableWidget,final TableWidget targetTableWidget){
        for(Object widget :sourceTableWidget.getPrimaryKeyWidgets()){
            IPrimaryKeyWidget primaryKeyWidget =  (IPrimaryKeyWidget)widget;
            Optional<ReferenceFlowWidget> optionalReferenceFlowWidget = primaryKeyWidget.getReferenceFlowWidget().stream().filter(r ->  r.getForeignKeyWidget().getTableWidget()==targetTableWidget).findFirst();
            if(optionalReferenceFlowWidget.isPresent()){
                ForeignKeyWidget foreignKeyWidget = optionalReferenceFlowWidget.get().getForeignKeyWidget();
                JoinColumn joinColumn;
                if (foreignKeyWidget instanceof ParentAssociationColumnWidget) {
                    joinColumn = ((DBParentAssociationColumn) foreignKeyWidget.getBaseElementSpec()).getJoinColumnOverride();
                } else {
                    joinColumn = ((DBForeignKey) foreignKeyWidget.getBaseElementSpec()).getJoinColumn();
                }
                if (StringUtils.isEmpty(joinColumn.getReferencedColumnName())) {
                    joinColumn.setReferencedColumnName(primaryKeyWidget.getName());
                }
                if (StringUtils.isEmpty(joinColumn.getName())) {
                    joinColumn.setName(foreignKeyWidget.getName());
                }
            }
        }
    }
    
    
    
        @Override
    public void createPropertySet(ElementPropertySet set) {
        set.createPropertySet("PARENT_JOINCOLUMN", this, this.getBaseElementSpec().getJoinColumn(), getPropertyChangeListeners());
        set.createPropertySet("ASSOCIATION_OVERRIDE", this, this.getBaseElementSpec().getJoinColumnOverride(), getPropertyChangeListeners());
    }
}
