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
package org.netbeans.jpa.modeler.specification.model.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JMenuItem;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Bidirectional;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Direction;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.generator.ui.GenerateCodeDialog;
import org.netbeans.jpa.modeler.source.generator.task.SourceCodeGeneratorTask;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.PModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.openide.util.RequestProcessor;

public class JPAModelerScene extends PModelerScene {

    public JPAModelerScene() {

    }

    private List<IFlowElementWidget> flowElements = new ArrayList<IFlowElementWidget>(); // Linked hashmap to preserve order of inserted elements

//    @Override
//    public void setEdgeWidgetSource(EdgeWidgetInfo edge, NodeWidgetInfo node) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void setEdgeWidgetTarget(EdgeWidgetInfo edge, NodeWidgetInfo node) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    /**
     * @return the flowElements
     */
    public List<IFlowElementWidget> getFlowElements() {
        return flowElements;
    }

    public IFlowElementWidget getFlowElement(String id) {
        IFlowElementWidget widget = null;
        for (IFlowElementWidget flowElementWidget : flowElements) {
            if (flowElementWidget.getId().equals(id)) {
                widget = flowElementWidget;
                break;
            }
        }
        return widget;
    }

    /**
     * @param flowElements the flowElements to set
     */
    public void setFlowElements(List<IFlowElementWidget> flowElements) {
        this.flowElements = flowElements;
    }

    public void removeFlowElement(IFlowElementWidget flowElementWidget) {
        this.flowElements.remove(flowElementWidget);
    }

    public void addFlowElement(IFlowElementWidget flowElementWidget) {
        this.flowElements.add(flowElementWidget);
    }

    @Override
    public IBaseElementWidget findBaseElement(String id) {
        IBaseElementWidget widget = null;
        List<IBaseElementWidget> baseElementWidgets = new ArrayList<IBaseElementWidget>(flowElements);
        for (IBaseElementWidget baseElementWidget : baseElementWidgets) {
            if (baseElementWidget instanceof IFlowNodeWidget) {
                if (((FlowNodeWidget) baseElementWidget).getId().equals(id)) {
                    widget = baseElementWidget;
                    break;
                }
            } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                if (baseElementWidget instanceof RelationFlowWidget) {
                    if (((RelationFlowWidget) baseElementWidget).getId().equals(id)) {
                        widget = baseElementWidget;
                        break;
                    }
                } else if (baseElementWidget instanceof GeneralizationFlowWidget) {
                    if (((GeneralizationFlowWidget) baseElementWidget).getId().equals(id)) {
                        widget = baseElementWidget;
                        break;
                    }
                }
            } else {
                throw new InvalidElmentException("Invalid JPA Element" + baseElementWidget);
            }
        }
        return widget;
    }

    @Override
    public IBaseElementWidget getBaseElement(String id) {
        IBaseElementWidget widget = null;
        List<IBaseElementWidget> baseElementWidgets = new ArrayList<IBaseElementWidget>(flowElements);
        for (IBaseElementWidget baseElementWidget : baseElementWidgets) {
            if (baseElementWidget.getId().equals(id)) {
                widget = baseElementWidget;
                break;
            }
        }
        return widget;
    }

    @Override
    public List<IBaseElementWidget> getBaseElements() {
        List<IBaseElementWidget> baseElementWidgets = new ArrayList<IBaseElementWidget>(flowElements);
        return baseElementWidgets;
    }

    @Override
    public void removeBaseElement(IBaseElementWidget baseElementWidget) {
        if (baseElementWidget instanceof IFlowElementWidget) {
            removeFlowElement((IFlowElementWidget) baseElementWidget);
        }
    }

    @Override
    public void addBaseElement(IBaseElementWidget baseElementWidget) {
        if (baseElementWidget instanceof IFlowElementWidget) {
            addFlowElement((IFlowElementWidget) baseElementWidget);
        }
    }

    @Override
    public void deleteBaseElement(IBaseElementWidget baseElementWidget
    ) {
        EntityMappings entityMappingsSpec = (EntityMappings) this.getModelerFile().getRootElement();
        if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse ref
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                IBaseElement baseElementSpec = flowNodeWidget.getBaseElementSpec();
                if (baseElementWidget instanceof JavaClassWidget) {
                    JavaClassWidget javaClassWidget = (JavaClassWidget) baseElementWidget;
                    if (javaClassWidget.getOutgoingGeneralizationFlowWidget() != null) {
                        javaClassWidget.getOutgoingGeneralizationFlowWidget().remove();
                    }
                    for (GeneralizationFlowWidget generalizationFlowWidget : new CopyOnWriteArrayList<GeneralizationFlowWidget>(javaClassWidget.getIncomingGeneralizationFlowWidgets())) {
                        generalizationFlowWidget.remove();
                    }

                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                        persistenceClassWidget.setLocked(true); //this method is used to prevent from reverse call( Recursion call) //  Source-flow-target any of deletion will delete each other so as deletion prcedd each element locked
                        for (RelationFlowWidget relationFlowWidget : new CopyOnWriteArrayList<RelationFlowWidget>(persistenceClassWidget.getInverseSideRelationFlowWidgets())) {
                            relationFlowWidget.remove();
                        }
                        for (RelationAttributeWidget relationAttributeWidget : persistenceClassWidget.getRelationAttributeWidgets()) {
                            relationAttributeWidget.getRelationFlowWidget().remove();
                        }
                        for (EmbeddedAttributeWidget embeddedAttributeWidget : persistenceClassWidget.getEmbeddedAttributeWidgets()) {
                            embeddedAttributeWidget.getEmbeddableFlowWidget().remove();
                        }

                        if (baseElementWidget instanceof EmbeddableWidget) {
                            EmbeddableWidget embeddableWidget = (EmbeddableWidget) baseElementWidget;
                            for (EmbeddableFlowWidget embeddableFlowWidget : new CopyOnWriteArrayList<EmbeddableFlowWidget>(embeddableWidget.getIncomingEmbeddableFlowWidgets())) {
                                embeddableFlowWidget.remove();
                            }
                        }

                        persistenceClassWidget.setLocked(false);
                    }

                }
                entityMappingsSpec.removeBaseElement(baseElementSpec);
                flowNodeWidget.setFlowElementsContainer(null);
                this.flowElements.remove(flowNodeWidget);
//            } else if (baseElementWidget instanceof SequenceFlowWidget) {
//                SequenceFlowWidget sequenceFlowWidget = ((SequenceFlowWidget) baseElementWidget);
//                TSequenceFlow sequenceFlowSpec = sequenceFlowWidget.getSequenceFlowSpec();
//
//                FlowNodeWidget sourceWidget = sequenceFlowWidget.getSourceNode();
//                TFlowNode sourceSpec = (TFlowNode) sourceWidget.getBaseElementSpec();
//                FlowNodeWidget targetWidget = sequenceFlowWidget.getTargetNode();
//                TFlowNode targetSpec = (TFlowNode) targetWidget.getBaseElementSpec();
//
//                sourceSpec.getOutgoing().remove(sequenceFlowSpec.getId());
//                targetSpec.getIncoming().remove(sequenceFlowSpec.getId());
//
//                sourceWidget.getOutgoingSequenceFlows().remove(sequenceFlowWidget);
//                targetWidget.getIncomingSequenceFlows().remove(sequenceFlowWidget);
//
//                entityMappingsSpec.removeFlowElement(sequenceFlowSpec);
//                sequenceFlowWidget.setFlowElementsContainer(null);
//                this.flowElements.remove(sequenceFlowWidget);
            } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                if (baseElementWidget instanceof RelationFlowWidget) {
                    RelationFlowWidget relationFlowWidget = (RelationFlowWidget) baseElementWidget;
                    relationFlowWidget.setLocked(true);
                    RelationAttributeWidget sourceRelationAttributeWidget = relationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.remove();

                    if (relationFlowWidget instanceof Direction) {
                        if (relationFlowWidget instanceof Unidirectional) {
                            Unidirectional unidirectional = (Unidirectional) relationFlowWidget;
                            unidirectional.getTargetEntityWidget().removeInverseSideRelationFlowWidget(relationFlowWidget);
                        } else if (relationFlowWidget instanceof Bidirectional) {
                            Bidirectional bidirectional = (Bidirectional) relationFlowWidget;
                            RelationAttributeWidget targetRelationAttributeWidget = bidirectional.getTargetRelationAttributeWidget();
                            targetRelationAttributeWidget.remove();
                        }
                    }
                    relationFlowWidget.setLocked(false);

                    relationFlowWidget.setFlowElementsContainer(null);
                    this.flowElements.remove(relationFlowWidget);
                } else if (baseElementWidget instanceof GeneralizationFlowWidget) {
                    GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) baseElementWidget;

                    generalizationFlowWidget.getSubclassWidget().setOutgoingGeneralizationFlowWidget(null);
                    generalizationFlowWidget.getSuperclassWidget().removeIncomingGeneralizationFlowWidget(generalizationFlowWidget);
                    JavaClass javaSubclass = (JavaClass) generalizationFlowWidget.getSubclassWidget().getBaseElementSpec();
                    javaSubclass.setSuperclassId(null);
                    javaSubclass.setSuperclass(null);
//                    generalizationFlowWidget.setSubclassWidget(null); // moved to destroy
//                    generalizationFlowWidget.setSuperclassWidget(null);

                    generalizationFlowWidget.setFlowElementsContainer(null);
                    this.flowElements.remove(generalizationFlowWidget);

                } else if (baseElementWidget instanceof EmbeddableFlowWidget) {
                    EmbeddableFlowWidget embeddableFlowWidget = (EmbeddableFlowWidget) baseElementWidget;
                    embeddableFlowWidget.setLocked(true);
                    EmbeddedAttributeWidget sourceEmbeddedAttributeWidget = embeddableFlowWidget.getSourceEmbeddedAttributeWidget();
                    sourceEmbeddedAttributeWidget.remove();
                    embeddableFlowWidget.getTargetEmbeddableWidget().removeIncomingEmbeddableFlowWidget(embeddableFlowWidget);

                    embeddableFlowWidget.setLocked(false);

                    embeddableFlowWidget.setFlowElementsContainer(null);
                    this.flowElements.remove(embeddableFlowWidget);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }

            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }

        }
    }

    @Override
    public void createBaseElement(IBaseElementWidget baseElementWidget
    ) {
        String baseElementId = "";
        Boolean isExist = false;
        if (baseElementWidget instanceof IFlowElementWidget) {
            this.flowElements.add((IFlowElementWidget) baseElementWidget);
            if (baseElementWidget instanceof IFlowNodeWidget) { //reverse ref
                ((FlowNodeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((FlowNodeWidget) baseElementWidget).getId();
                isExist = ((FlowNodeWidget) baseElementWidget).getNodeWidgetInfo().isExist();
            } else if (baseElementWidget instanceof IFlowEdgeWidget) { //reverse ref
                ((IFlowEdgeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((IFlowEdgeWidget) baseElementWidget).getId();
                isExist = ((PEdgeWidget) baseElementWidget).getEdgeWidgetInfo().isExist();

//                if (baseElementWidget instanceof RelationFlowWidget) {
//                    ((RelationFlowWidget) baseElementWidget).setFlowElementsContainer(this);
//                    baseElementId = ((RelationFlowWidget) baseElementWidget).getId();
//                    isExist = ((RelationFlowWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
//                } else if (baseElementWidget instanceof GeneralizationFlowWidget) {
//                    ((GeneralizationFlowWidget) baseElementWidget).setFlowElementsContainer(this);
//                    baseElementId = ((GeneralizationFlowWidget) baseElementWidget).getId();
//                    isExist = ((GeneralizationFlowWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
//                }else if (baseElementWidget instanceof EmbeddableFlowWidget) {
//                    ((EmbeddableFlowWidget) baseElementWidget).setFlowElementsContainer(this);
//                    baseElementId = ((EmbeddableFlowWidget) baseElementWidget).getId();
//                    isExist = ((EmbeddableFlowWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
//                }
            } else {
                throw new InvalidElmentException("Invalid JPA FlowElement : " + baseElementWidget);
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

        if (!isExist) {

            EntityMappings entityMappingsSpec = (EntityMappings) this.getModelerFile().getRootElement();
            IBaseElement baseElement = null;
            if (baseElementWidget instanceof IFlowElementWidget) {
                if (baseElementWidget instanceof IFlowNodeWidget) {
                    if (baseElementWidget instanceof EntityWidget) {
                        baseElement = new Entity();
                    } else if (baseElementWidget instanceof MappedSuperclassWidget) {
                        baseElement = new MappedSuperclass();
                    } else if (baseElementWidget instanceof EmbeddableWidget) {
                        baseElement = new Embeddable();
                    } else {
                        throw new InvalidElmentException("Invalid JPA Task Element : " + baseElement);
                    }
                } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                    // skip don't need to create spec RelationFlowWidget, GeneralizationFlowWidget,EmbeddableFlowWidget
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
            if (baseElement != null) {
                baseElementWidget.setBaseElementSpec(baseElement);
                baseElement.setId(baseElementId);
                entityMappingsSpec.addBaseElement(baseElement);
                ElementConfigFactory elementConfigFactory = this.getModelerFile().getVendorSpecification().getElementConfigFactory();
                elementConfigFactory.initializeObjectValue(baseElement);
            }

        } else {
            if (baseElementWidget instanceof IFlowElementWidget) {
                if (baseElementWidget instanceof FlowNodeWidget) {
                    FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                    flowNodeWidget.setBaseElementSpec(flowNodeWidget.getNodeWidgetInfo().getBaseElementSpec());
//                } else if (baseElementWidget instanceof SequenceFlowWidget) {
//                    SequenceFlowWidget sequenceFlowWidget = (SequenceFlowWidget) baseElementWidget;//TBF_CODE
//                    baseElementWidget.setBaseElementSpec(sequenceFlowWidget.getEdgeWidgetInfo().getBaseElementSpec());
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        ElementConfigFactory elementConfigFactory = this.getModelerFile().getVendorSpecification().getElementConfigFactory();
        elementConfigFactory.createPropertySet(set, this.getBaseElementSpec(), getPropertyChangeListeners());
//        if (this.getBaseElementSpec() instanceof AccessTypeHandler) {
//            set.put("BASIC_PROP", JPAModelerUtil.getAccessTypeProperty(this, (AccessTypeHandler) this.getBaseElementSpec()));
//        }
    }

    @Override
    public void createVisualPropertySet(ElementPropertySet elementPropertySet
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        JMenuItem generateCode = new JMenuItem("Generate Source Code");
        generateCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenerateCodeDialog dialog = new GenerateCodeDialog(JPAModelerScene.this.getModelerFile().getFileObject());
                dialog.setVisible(true);
                if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                    RequestProcessor processor = new RequestProcessor("jpa/ExportCode"); // NOI18N
                    SourceCodeGeneratorTask task = new SourceCodeGeneratorTask(JPAModelerScene.this.getModelerFile(), dialog.getTargetPoject(), dialog.getSourceGroup());
                    processor.post(task);
                }

            }
        });

//        JMenuItem generateCodeFromDB = new JMenuItem("Generate DB code");
//        generateCodeFromDB.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                GenerateCodeDialog dialog = new GenerateCodeDialog();
//                dialog.setVisible(true);
//                if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
//                    RequestProcessor processor = new RequestProcessor("jpa/ExportCode"); // NOI18N
//                    SourceCodeGeneratorTask task = new SourceCodeGeneratorTask(JPAModelerScene.this.getModelerFile(), dialog.getSourceGroup().getRootFolder());
//                    processor.post(task);
//                }
//
//            }
//        });
//
//        menuList.add(0, generateCodeFromDB);
        menuList.add(0, generateCode);
        menuList.add(1, null);

        return menuList;
    }

    public String getNextClassName() {
        return getNextClassName(null);
    }

    public String getNextClassName(String className) {
        int index = 0;
        if (className == null || className.trim().isEmpty()) {
            className = "class";
        }
        className = Character.toUpperCase(className.charAt(0)) + (className.length() > 1 ? className.substring(1) : "");
        String nextClassName = className + ++index;
        EntityMappings entityMappings = (EntityMappings) this.getBaseElementSpec();

        boolean isExist = true;
        while (isExist) {
            if (entityMappings.isClassExist(nextClassName)) {
                isExist = true;
                nextClassName = className + ++index;
            } else {
                return nextClassName;
            }
        }
        return nextClassName;
    }

}
