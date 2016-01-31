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
package org.netbeans.db.modeler.specification.model.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.theme.DBColorScheme;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Bidirectional;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Direction;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.DefaultPModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.node.vmd.internal.PFactory;

public class DBModelerScene extends DefaultPModelerScene<DBMapping> {

    @Override
    public void deleteBaseElement(IBaseElementWidget baseElementWidget) {
        DBMapping entityMappingsSpec = this.getBaseElementSpec();
        if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse ref
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                IBaseElement baseElementSpec = flowNodeWidget.getBaseElementSpec();
                if (baseElementWidget instanceof JavaClassWidget) {
                    JavaClassWidget<JavaClass> javaClassWidget = (JavaClassWidget) baseElementWidget;
                    if (javaClassWidget.getOutgoingGeneralizationFlowWidget() != null) {
                        javaClassWidget.getOutgoingGeneralizationFlowWidget().remove();
                    }
                    for (GeneralizationFlowWidget generalizationFlowWidget : new CopyOnWriteArrayList<>(javaClassWidget.getIncomingGeneralizationFlowWidgets())) {
                        generalizationFlowWidget.remove();
                    }

                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget<ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                        persistenceClassWidget.setLocked(true); //this method is used to prevent from reverse call( Recursion call) //  Source-flow-target any of deletion will delete each other so as deletion prcedd each element locked
                        for (RelationFlowWidget relationFlowWidget : new CopyOnWriteArrayList<>(persistenceClassWidget.getInverseSideRelationFlowWidgets())) {
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
                            for (EmbeddableFlowWidget embeddableFlowWidget : new CopyOnWriteArrayList<>(embeddableWidget.getIncomingEmbeddableFlowWidgets())) {
                                embeddableFlowWidget.remove();
                            }
                        }

                        persistenceClassWidget.setLocked(false);
                    }

                }
                entityMappingsSpec.removeBaseElement(baseElementSpec);
                flowNodeWidget.setFlowElementsContainer(null);
                this.flowElements.remove(flowNodeWidget);
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
                    JavaClass javaSuperclass = (JavaClass) generalizationFlowWidget.getSuperclassWidget().getBaseElementSpec();
                    javaSubclass.removeSuperclass(javaSuperclass);

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
    public void createBaseElement(IBaseElementWidget baseElementWidget) {
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
            } else {
                throw new InvalidElmentException("Invalid JPA FlowElement : " + baseElementWidget);
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

        if (!isExist) {

//            IRootElement rootElement = this.getBaseElementSpec();
//            IBaseElement baseElement = null;
//            if (baseElementWidget instanceof IFlowElementWidget) {
//                if (baseElementWidget instanceof IFlowNodeWidget) {
//                    if (baseElementWidget instanceof TableWidget) {
//                        baseElement = new Table(null);
//                    } else {
//                        throw new InvalidElmentException("Invalid JPA Task Element : " + baseElement);
//                    }
//                } else if (baseElementWidget instanceof IFlowEdgeWidget) {
//                    // skip don't need to create spec RelationFlowWidget, GeneralizationFlowWidget,EmbeddableFlowWidget
//                } else {
//                    throw new InvalidElmentException("Invalid JPA Element");
//                }
//            } else {
//                throw new InvalidElmentException("Invalid JPA Element");
//            }
//            if (baseElement != null) {
//                baseElementWidget.setBaseElementSpec(baseElement);
//                baseElement.setId(baseElementId);
//                rootElement.addBaseElement(baseElement);
//                ElementConfigFactory elementConfigFactory = this.getModelerFile().getVendorSpecification().getElementConfigFactory();
//                elementConfigFactory.initializeObjectValue(baseElement);
//            }
        } else if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                flowNodeWidget.setBaseElementSpec(flowNodeWidget.getNodeWidgetInfo().getBaseElementSpec());
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    @Override
    public IColorScheme getColorScheme() {
//        DBMapping entityMappings = this.getBaseElementSpec();
//        if (PFactory.getNetBeans60Scheme().getSimpleName().equals(entityMappings.getTheme())) {
//            return PFactory.getColorScheme(PFactory.getNetBeans60Scheme());
//        }  else if (PFactory.getMetroScheme().getSimpleName().equals(entityMappings.getTheme())) {
//            return PFactory.getColorScheme(PFactory.getMetroScheme());
//        }  else {
//            return PFactory.getColorScheme(PFactory.getMacScheme());
//        }
        return PFactory.getColorScheme(DBColorScheme.class);
    }

    @Override
    public void setColorScheme(Class<? extends IColorScheme> scheme) {
//        DBMapping entityMappings = this.getBaseElementSpec();
//        entityMappings.setTheme(scheme.getSimpleName());
    }

    @Override
    public Map<String, Class<? extends IColorScheme>> getColorSchemes() {
        Map<String, Class<? extends IColorScheme>> colorSchemes = new HashMap<>();
//        colorSchemes.put("Classic",PFactory.getNetBeans60Scheme());
        colorSchemes.put("Default", DBColorScheme.class);
//        colorSchemes.put("Mac", PFactory.getMacScheme());
        return colorSchemes;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() {
        super.init();
    }

}
