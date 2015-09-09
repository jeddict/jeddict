/**
 * Copyright [2013] Gaurav Gupta
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.properties.inheritence.InheritencePanel;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;
import org.openide.util.ImageUtilities;

public class EntityWidget extends PrimaryKeyContainerWidget {

    public EntityWidget(IModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyVisibilityHandler("inheritence", new PropertyVisibilityHandler<String>() {
            @Override
            public boolean isVisible() {
                GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
                List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();
                if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
                    outgoingGeneralizationFlowWidget = null;
                }
                if (outgoingGeneralizationFlowWidget != null || !incomingGeneralizationFlowWidgets.isEmpty()) {
                    return true;
                }
                return false;
            }

        });
        
       this.addPropertyChangeListener("abstract", new PropertyChangeListener<Boolean>() {
            @Override
            public void changePerformed(Boolean _abstract) {
                changeAbstractionIcon(_abstract);
            }
        });
        

    }

    @Override
    public void init() {
        Entity entity = (Entity) this.getBaseElementSpec();
        if (entity.getAttributes() == null) {
            entity.setAttributes(new Attributes());
            addNewIdAttribute("id");
            sortAttributes();
        }

        if (entity.getClazz() == null || entity.getClazz().isEmpty()) {
            entity.setClazz(((JPAModelerScene) this.getModelerScene()).getNextClassName("Entity_"));
        }
        setName(entity.getClazz());
        setLabel(entity.getClazz());
        changeAbstractionIcon(entity.getAbstract());
        scanPrimaryKeyError();
//        
    }
    
    private void changeAbstractionIcon(Boolean _abstract){
        System.out.println(EntityWidget.this.getName() + " + ABSTRACT  : " + _abstract);
        if(_abstract){
//            System.out.println("JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH" + ImageUtilities.loadImage(JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH));
                    EntityWidget.this.setNodeImage(ImageUtilities.loadImage(JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH));
                } else {
//            System.out.println("JPAModelerUtil.ENTITY_ICON_PATH" + ImageUtilities.loadImage(JPAModelerUtil.ENTITY_ICON_PATH));
                   EntityWidget.this.setNodeImage(ImageUtilities.loadImage(JPAModelerUtil.ENTITY_ICON_PATH));
//                 this.getNodeWidgetInfo().getModelerDocument().setImage();
        
        }
       
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        Entity entity = (Entity) this.getBaseElementSpec();
        if (entity.getTable() == null) {
            entity.setTable(new Table());
        }
        
        set.createPropertySet( this , entity.getTable(), getPropertyChangeListeners());

        if (entity instanceof InheritenceHandler) {
            set.put("BASIC_PROP", getInheritenceProperty());
        }
        set.put("BASIC_PROP", JPAModelerUtil.getNamedQueryProperty("NamedQueries", "Named Queries", "", this.getModelerScene(), entity.getNamedQuery()));
        set.put("BASIC_PROP", JPAModelerUtil.getNamedNativeQueryProperty("NamedNativeQueries", "Named Native Queries", "", this.getModelerScene(), entity.getNamedNativeQuery()));
        set.put("BASIC_PROP", JPAModelerUtil.getNamedStoredProcedureQueryProperty("NamedStoredProcedureQueries", "Named StoredProcedure Queries", "", this.getModelerScene(), entity));
        
    }

    private EmbeddedPropertySupport getInheritenceProperty() {

        GenericEmbedded entity = new GenericEmbedded("inheritence", "Inheritence", "");
        try {
            entity.setEntityEditor(new InheritencePanel(this.getModelerScene().getModelerFile(), EntityWidget.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setDataListener(new EmbeddedDataListener<InheritenceHandler>() {
            private InheritenceHandler classSpec;
            private String displayName = null;

            @Override
            public void init() {
                classSpec = (InheritenceHandler) EntityWidget.this.getBaseElementSpec();
            }

            @Override
            public InheritenceHandler getData() {
                if (classSpec.getInheritance() == null) {
                    classSpec.setInheritance(new Inheritance());
                }
                if (classSpec.getDiscriminatorColumn() == null) {
                    classSpec.setDiscriminatorColumn(new DiscriminatorColumn());
                }
                return classSpec;
            }

            @Override
            public void setData(InheritenceHandler classSpec) {
                EntityWidget.this.setBaseElementSpec((IBaseElement) classSpec);
            }

            @Override
            public String getDisplay() {

                GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
                List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();

                if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
                    outgoingGeneralizationFlowWidget = null;
                }
//                String type;
//                if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "SINGLETON";
//                } else
                if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "LEAF";
                    EntityWidget superEntityWidget = (EntityWidget) EntityWidget.this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
                    InheritenceHandler superClassSpec = (InheritenceHandler) superEntityWidget.getBaseElementSpec();
                    if (superClassSpec.getInheritance() != null && superClassSpec.getInheritance().getStrategy() != null) {
                        return superClassSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "ROOT";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "BRANCH";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else {
                    return "";
                }
            }

        });
        return new EmbeddedPropertySupport(this.getModelerScene().getModelerFile(), entity);
    }

    @Override
    public String getInheritenceState() {
        GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
        List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();
        if (outgoingGeneralizationFlowWidget != null && outgoingGeneralizationFlowWidget.getSuperclassWidget() != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
            outgoingGeneralizationFlowWidget = null;
        }
        String type;
        if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "SINGLETON";
        } else if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "LEAF";
        } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "ROOT";
        } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "BRANCH";
        } else {
            type = "";
        }
        return type;
    }

//    @Override
    public void scanPrimaryKeyError() {
        
        String inheritenceState = this.getInheritenceState();
        if ("SINGLETON".equals(inheritenceState) || "ROOT".equals(inheritenceState)) {
            // Issue Fix #6041 Start
            if (this.getAllIdAttributeWidgets().isEmpty() && this.isCompositePKPropertyAllow() == CompositePKProperty.NONE) {
                throwError(EntityValidator.NO_PRIMARYKEY_EXIST);
            } else {
                clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
            }
            // Issue Fix #6041 End
        } else {
            clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
        }
    }
    
    
        @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        JMenuItem addEntityGraph;
        addEntityGraph = new JMenuItem("Add Entity Graph");
        addEntityGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component parentComponent = (Component)EntityWidget.this.getModelerScene().getModelerPanelTopComponent();
                String name = JOptionPane.showInputDialog(parentComponent, "Please enter entity graph name : ", null);
                openDiagram(name);
            }
        });
        menuList.add(0, addEntityGraph);
        return menuList;
    }
    
    
     public void openDiagram(String entityGraphId) {
        String path = this.getModelerScene().getModelerPanelTopComponent().getToolTipText();
        JPAFileActionListener fileAction = new JPAFileActionListener(this.getModelerScene().getModelerFile().getModelerFileDataObject());
        fileAction.openModelerFile(entityGraphId,entityGraphId,entityGraphId + " > " + path);
    }

}
