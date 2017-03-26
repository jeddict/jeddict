/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget.attribute.relation;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getCascadeProperty;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.NANO_DB;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class RelationAttributeWidget<E extends RelationAttribute> extends AttributeWidget<E> {

    public RelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        set.put("JPA_PROP", getCascadeProperty(this));        
        // Issue Fix #6153 Start
        set.put("JPA_PROP", PropertiesHandler.getFetchTypeProperty(this.getModelerScene(), (FetchTypeHandler) this.getBaseElementSpec()));
        // Issue Fix #6153 End
        RelationAttribute relationAttributeSpec = (RelationAttribute) this.getBaseElementSpec();

        if (relationAttributeSpec.isOwner()) {
            if (this.getBaseElementSpec() instanceof JoinColumnHandler) {
                Entity targetEntity = ((RelationAttribute) this.getBaseElementSpec()).getConnectedEntity();
                JoinColumnHandler joinColumnHandlerSpec = (JoinColumnHandler) this.getBaseElementSpec();
                set.put("JOIN_COLUMN_PROP", PropertiesHandler.getJoinColumnsProperty("JoinColumns", "Join Columns", "", this.getModelerScene(), joinColumnHandlerSpec.getJoinColumn(), targetEntity));
            }
            set.createPropertySet(this, relationAttributeSpec.getJoinTable());
            set.put("JOIN_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("JoinTable_JoinColumns", "Join Columns", "", this.getModelerScene(), relationAttributeSpec.getJoinTable().getJoinColumn()));
            set.put("JOIN_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("JoinTable_InverseJoinColumns", "Inverse Join Columns", "", this.getModelerScene(), relationAttributeSpec.getJoinTable().getInverseJoinColumn()));
        }

    }

    @Override
    public void init() {
        this.getClassWidget().scanDuplicateAttributes(null, this.name);
        validateName(null, this.getName());
        //setAttributeTooltip, visualizeDataType moved to setConnectedSibling :: @init on new relationship creation no target class connected
    }

    public void setConnectedSibling(EntityWidget classWidget) {
        RelationAttribute relationAttribute = this.getBaseElementSpec();
        relationAttribute.setConnectedEntity(classWidget.getBaseElementSpec());
        setAttributeTooltip();
        visualizeDataType();
    }

    public void setConnectedSibling(EntityWidget classWidget, RelationAttributeWidget<RelationAttribute> attributeWidget) {
        RelationAttribute relationAttribute = this.getBaseElementSpec();
        relationAttribute.setConnectedEntity(classWidget.getBaseElementSpec());
        relationAttribute.setConnectedAttribute(attributeWidget.getBaseElementSpec());
        setAttributeTooltip();
        visualizeDataType();
    }

    public abstract RelationFlowWidget getRelationFlowWidget();

//    @Override
//    protected List<JMenuItem> getPopupMenuItemList() {
//        List<JMenuItem> menuList = super.getPopupMenuItemList();
//        if (this.getClassWidget().getBaseElementSpec() instanceof Entity) {
//            JMenuItem visDB = new JMenuItem("Nano DB", NANO_DB);
//            visDB.addActionListener((ActionEvent e) -> {
//                ModelerFile file = this.getModelerScene().getModelerFile();
//                DBUtil.openDBViewer(file, DBUtil.isolateEntityMapping(this.getModelerScene().getBaseElementSpec(), (Entity) this.getClassWidget().getBaseElementSpec(), (RelationAttribute) this.getBaseElementSpec()));
//            });
//
//            menuList.add(0, visDB);
//        }
//        return menuList;
//    }
    
    public PersistenceClassWidget getConnectedClassWidget(){
            IFlowElementWidget flowElementWidget = this.getBaseElementSpec().isOwner() ? 
                    this.getRelationFlowWidget().getTargetWidget() : 
                    this.getRelationFlowWidget().getSourceWidget();
            PersistenceClassWidget connectedClassWidget = null;
            if (flowElementWidget instanceof PersistenceClassWidget) {
                connectedClassWidget = (PersistenceClassWidget) flowElementWidget;
            } else if (flowElementWidget instanceof RelationAttributeWidget) {
                connectedClassWidget = (PersistenceClassWidget) ((RelationAttributeWidget) flowElementWidget).getClassWidget();//target can be only Entity && source should be PersistenceClassWidget
            }
            return connectedClassWidget;
    }

}
