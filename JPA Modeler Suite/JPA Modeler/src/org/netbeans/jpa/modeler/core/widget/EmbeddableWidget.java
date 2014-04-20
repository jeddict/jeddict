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
package org.netbeans.jpa.modeler.core.widget;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

public class EmbeddableWidget extends PersistenceClassWidget {

    public EmbeddableWidget(IModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
    }

    private List<EmbeddableFlowWidget> incomingEmbeddableFlowWidgets = new ArrayList<EmbeddableFlowWidget>();  //no need for reverse relation

    @Override
    public void init() {
        Embeddable embeddable = (Embeddable) this.getBaseElementSpec();
        if (embeddable.getAttributes() == null) {
            embeddable.setAttributes(new EmbeddableAttributes());
//            addNewIdAttribute("id");
//            sortAttributes();
        }
        if (embeddable.getClazz() == null || embeddable.getClazz().isEmpty()) {
            embeddable.setClazz(((JPAModelerScene) this.getModelerScene()).getNextClassName("Embeddable_"));
        }
        setName(embeddable.getClazz());
        setLabel(embeddable.getClazz());

    }

//    @Override
//    protected List<JMenuItem> getPopupMenuItemList() {
//        List<JMenuItem> menuList = super.getPopupMenuItemList();
////
////        JMenuItem addBasicAttr = new JMenuItem("Add Basic Attribute");
////        addBasicAttr.addActionListener(new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent e) {
////                addNewBasicAttribute(getNextAttributeName());
////                EmbeddableWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
////
////            }
////        });
////        JMenuItem addBasicCollectionAttr = new JMenuItem("Add Basic Collection Attribute");
////        addBasicCollectionAttr.addActionListener(new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent e) {
////                addNewBasicCollectionAttribute(getNextAttributeName());
////                EmbeddableWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
////
////            }
////        });
////        JMenuItem addTransientAttr = new JMenuItem("Add Transient Attribute");
////        addTransientAttr.addActionListener(new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent e) {
////                addNewTransientAttribute(getNextAttributeName());
////                EmbeddableWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
////            }
////        });
////        ;
////        menuList.add(0, addBasicAttr);
////
////        menuList.add(1, addBasicCollectionAttr);
////        menuList.add(2, addTransientAttr);
////        menuList.add(3, null);
//
//        return menuList;
//    }
    /**
     * @return the incomingEmbeddableFlowWidgets
     */
    public List<EmbeddableFlowWidget> getIncomingEmbeddableFlowWidgets() {
        return incomingEmbeddableFlowWidgets;
    }

    /**
     * @param incomingEmbeddableFlowWidgets the incomingEmbeddableFlowWidgets to
     * set
     */
    public void setIncomingEmbeddableFlowWidgets(List<EmbeddableFlowWidget> incomingEmbeddableFlowWidgets) {
        this.incomingEmbeddableFlowWidgets = incomingEmbeddableFlowWidgets;
    }

    public void addIncomingEmbeddableFlowWidget(EmbeddableFlowWidget incomingEmbeddableFlowWidget) {
        this.incomingEmbeddableFlowWidgets.add(incomingEmbeddableFlowWidget);
    }

    public void removeIncomingEmbeddableFlowWidget(EmbeddableFlowWidget incomingEmbeddableFlowWidget) {
        this.incomingEmbeddableFlowWidgets.remove(incomingEmbeddableFlowWidget);
    }

    @Override
    public String getInheritenceState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    An embeddable class (including an embeddable class within another embeddable class) that is contained within an element collection must not contain an element collection, nor may it contain a relationship to an entity other than a many-to-one or one-to-one relationship
    public List<AttributeWidget> getAttributeOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<AttributeWidget>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof EmbeddableWidget) {
            attributeWidgets.addAll(((EmbeddableWidget) classWidget).getAttributeOverrideWidgets());
        }
        attributeWidgets.addAll(getBasicAttributeWidgets());
        attributeWidgets.addAll(getBasicCollectionAttributeWidgets());
        attributeWidgets.addAll(this.getSingleValueEmbeddedAttributeWidgets());
        attributeWidgets.addAll(this.getMultiValueEmbeddedAttributeWidgets());
        return attributeWidgets;
    }

    public List<AttributeWidget> getAssociationOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<AttributeWidget>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof EmbeddableWidget) {
            attributeWidgets.addAll(((EmbeddableWidget) classWidget).getAssociationOverrideWidgets());
        }
        attributeWidgets.addAll(this.getOneToOneRelationAttributeWidgets());
        attributeWidgets.addAll(this.getOneToManyRelationAttributeWidgets());
        attributeWidgets.addAll(this.getManyToOneRelationAttributeWidgets());
        attributeWidgets.addAll(this.getManyToManyRelationAttributeWidgets());
        attributeWidgets.addAll(this.getSingleValueEmbeddedAttributeWidgets());
        attributeWidgets.addAll(this.getMultiValueEmbeddedAttributeWidgets());
        return attributeWidgets;
    }

}
