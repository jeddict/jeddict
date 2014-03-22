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
package org.netbeans.jpa.modeler.specification.model.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.FileUtils;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.MultiValueEmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.SingleValueEmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BMTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BMTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BOTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.MTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.MTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.OTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.OTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UMTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UMTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UOTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UOTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Bidirectional;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Direction;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import org.netbeans.jpa.modeler.spec.AccessType;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.design.Bounds;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.design.DiagramElement;
import org.netbeans.jpa.modeler.spec.design.Edge;
import org.netbeans.jpa.modeler.spec.design.Plane;
import org.netbeans.jpa.modeler.spec.design.Shape;
import org.netbeans.jpa.modeler.spec.extend.AccessTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.anchors.CustomRectangularAnchor;
import org.netbeans.modeler.border.ResizeBorder;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ActionHandler;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ComboBoxListener;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.support.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.netbeans.modeler.shape.ShapeDesign;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.IPModelerScene;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.model.util.PModelerUtil;
import org.netbeans.modeler.validation.jaxb.ValidateJAXB;
import org.netbeans.modeler.widget.edge.EdgeWidget;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.node.NodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class JPAModelerUtil implements PModelerUtil {

    /*---------------------------------Load File Start---------------------------------*/
//    public static void loadJPA(final JPAFile file) {
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                loadJPAImpl(file);
//            }
//        };
//        final RequestProcessor.Task theTask = RP.create(runnable);
//        final ProgressHandle ph = ProgressHandleFactory.createHandle("Loading JPA File...", theTask);
//        theTask.addTaskListener(new TaskListener() {
//            @Override
//            public void taskFinished(org.openide.util.Task task) {
//                ph.finish();
//            }
//        });
//        ph.start();
//        theTask.schedule(0);
//    }
//
    private JAXBContext jpaModelContext;
    private Unmarshaller jpaModelUnmarshaller;
    private Marshaller jpaModelMarshaller;
    private InputOutput io = IOProvider.getDefault().getIO("JPA Modeler Console", false);

    @Override
    public void loadModelerFile(ModelerFile file) {
        try {
            IModelerScene scene = file.getModelerScene();

            File savedFile = file.getFile();
            if (jpaModelContext == null) {
                jpaModelContext = JAXBContext.newInstance(new Class<?>[]{ShapeDesign.class, EntityMappings.class});
            }
            if (jpaModelUnmarshaller == null) {
                jpaModelUnmarshaller = jpaModelContext.createUnmarshaller();
            }
            jpaModelUnmarshaller.setEventHandler(new ValidateJAXB());
            EntityMappings definition_Load = jpaModelUnmarshaller.unmarshal(new StreamSource(savedFile), EntityMappings.class).getValue();

            scene.setRootElementSpec(definition_Load);

            Diagram diagram = definition_Load.getJPADiagram();
            file.getModelerDiagramModel().setDefinitionElement(definition_Load);
            file.getModelerDiagramModel().setRootElement(definition_Load);
            file.getModelerDiagramModel().setDiagramElement(diagram);

            for (IFlowNode flowNode_Load : new CopyOnWriteArrayList<IFlowNode>(definition_Load.getMappedSuperclass())) {
                loadFlowNode(scene, (Widget) scene, flowNode_Load);
            }
            for (IFlowNode flowNode_Load : new CopyOnWriteArrayList<IFlowNode>(definition_Load.getEntity())) {
                loadFlowNode(scene, (Widget) scene, flowNode_Load);
            }

            for (IFlowNode flowNode_Load : new CopyOnWriteArrayList<IFlowNode>(definition_Load.getEmbeddable())) {
                loadFlowNode(scene, (Widget) scene, flowNode_Load);
            }
//            for (IFlowNode flowNode_Load : new CopyOnWriteArrayList<IFlowNode>(definition_Load.getEntity())) {
            loadFlowEdge(scene);
//            }
            for (DiagramElement diagramElement_Tmp : diagram.getJPAPlane().getDiagramElement()) {
                loadDiagram(scene, diagram, diagramElement_Tmp);
            }

            if (definition_Load.isGenerated()) {
                scene.autoLayout();
                definition_Load.setStatus(null);
            }

        } catch (JAXBException e) {
            io.getOut().println("Exception: " + e.toString());
            e.printStackTrace();
//            Exceptions.printStackTrace(e);
            System.out.println("Document XML Not Exist");
        }

    }

    private void loadFlowNode(IModelerScene scene, Widget parentWidget, IFlowNode flowElement) {
        IModelerDocument document = null;
        ModelerDocumentFactory modelerDocumentFactory = scene.getModelerFile().getVendorSpecification().getModelerDocumentFactory();
        if (flowElement instanceof FlowNode) {
            try {
                document = modelerDocumentFactory.getModelerDocument(flowElement);
            } catch (ModelerException ex) {
                Exceptions.printStackTrace(ex);
            }

            SubCategoryNodeConfig subCategoryNodeConfig = scene.getModelerFile().getVendorSpecification().getPaletteConfig().findSubCategoryNodeConfig(document);

            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(flowElement.getId(), subCategoryNodeConfig, new Point(0, 0));
            nodeWidgetInfo.setName(flowElement.getName());
            nodeWidgetInfo.setExist(Boolean.TRUE);//to Load JPA
            nodeWidgetInfo.setBaseElementSpec(flowElement);//to Load JPA
            INodeWidget nodeWidget = (INodeWidget) scene.createNodeWidget(nodeWidgetInfo);
            if (flowElement.getName() != null) {
                nodeWidget.setLabel(flowElement.getName());
            }
            if (flowElement instanceof FlowNode) {
                FlowNode flowNode = (FlowNode) flowElement;
                if (flowNode.isMinimized()) {
                    ((PNodeWidget) nodeWidget).setMinimized(true);
                }
                if (flowElement instanceof JavaClass) {
                    JavaClass entity = (JavaClass) flowElement;
                    PersistenceClassWidget entityWidget = (PersistenceClassWidget) nodeWidget;
                    if (entity.getAttributes() != null) {
                        if (entity.getAttributes() instanceof IPersistenceAttributes) {
                            for (Id id : ((IPersistenceAttributes) entity.getAttributes()).getId()) {
                                entityWidget.addNewIdAttribute(id.getName(), id);
                            }
                        }
                        for (Basic basic : entity.getAttributes().getBasic()) {
                            entityWidget.addNewBasicAttribute(basic.getName(), basic);
                        }
                        for (Transient _transient : entity.getAttributes().getTransient()) {
                            entityWidget.addNewTransientAttribute(_transient.getName(), _transient);
                        }
                        if (entity.getAttributes() instanceof IPersistenceAttributes) {
                            for (Version version : ((IPersistenceAttributes) entity.getAttributes()).getVersion()) {
                                entityWidget.addNewVersionAttribute(version.getName(), version);
                            }
                        }
                        for (Embedded embedded : entity.getAttributes().getEmbedded()) {
                            entityWidget.addNewSingleValueEmbeddedAttribute(embedded.getName(), embedded);
                        }
                        for (ElementCollection elementCollection : entity.getAttributes().getElementCollection()) {
                            if (elementCollection.getConnectedClassId() != null) {
                                entityWidget.addNewMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                            } else {
                                entityWidget.addNewBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                            }
                        }
                        for (OneToOne oneToOne : entity.getAttributes().getOneToOne()) {
                            OTORelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToOneRelationAttribute(oneToOne.getName(), oneToOne);
                            if (oneToOne.getMappedBy() == null) {
                                relationAttributeWidget.setOwner(true);
                            }
                        }
                        for (OneToMany oneToMany : entity.getAttributes().getOneToMany()) {
                            OTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToManyRelationAttribute(oneToMany.getName(), oneToMany);
                            if (oneToMany.getMappedBy() == null) {
                                relationAttributeWidget.setOwner(true);
                            }
                        }
                        for (ManyToOne manyToOne : entity.getAttributes().getManyToOne()) {
                            MTORelationAttributeWidget relationAttributeWidget = entityWidget.addNewManyToOneRelationAttribute(manyToOne.getName(), manyToOne);
                            relationAttributeWidget.setOwner(true);//always
                        }
                        for (ManyToMany manyToMany : entity.getAttributes().getManyToMany()) {
                            MTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewManyToManyRelationAttribute(manyToMany.getName(), manyToMany);
                            if (manyToMany.getMappedBy() == null) {
                                relationAttributeWidget.setOwner(true);
                            }
                        }
                        entityWidget.sortAttributes();
                    }

                }
            }
//            nodeWidget.i
            //clear incomming & outgoing it will added on sequenceflow auto connection
//            ((FlowNode) flowElement).getIncoming().clear();
//            ((FlowNode) flowElement).getOutgoing().clear();

        }
    }

    private void loadFlowEdge(IModelerScene scene) {
        IPModelerScene modelerScene = (IPModelerScene) scene;
        for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
            if (baseElementWidget instanceof JavaClassWidget) {
                JavaClassWidget javaClassWidget = (JavaClassWidget) baseElementWidget;
                loadGeneralization(modelerScene, javaClassWidget);

                if (baseElementWidget instanceof PersistenceClassWidget) {
                    PersistenceClassWidget sourcePersistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                    for (SingleValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getSingleValueEmbeddedAttributeWidgets()) {
                        loadEmbeddedEdge(modelerScene, "SINGLE_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                    }
                    for (MultiValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getMultiValueEmbeddedAttributeWidgets()) {
                        loadEmbeddedEdge(modelerScene, "MULTI_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                    }

                    for (OTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToOneRelationAttributeWidgets()) {
                        loadRelationEdge(modelerScene, "OTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTORelationAttributeWidget.class);
                    }
                    for (OTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToManyRelationAttributeWidgets()) {
                        loadRelationEdge(modelerScene, "OTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                    }
                    for (MTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToOneRelationAttributeWidgets()) {
                        loadRelationEdge(modelerScene, "MTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                    }
                    for (MTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToManyRelationAttributeWidgets()) {
                        loadRelationEdge(modelerScene, "MTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, MTMRelationAttributeWidget.class);
                    }
                }

            }
        }
    }

    private void loadEmbeddedEdge(IPModelerScene scene, String contextToolId, PersistenceClassWidget sourcePersistenceClassWidget, EmbeddedAttributeWidget sourceAttributeWidget) {
        CompositionAttribute sourceEmbeddedAttribute = (CompositionAttribute) sourceAttributeWidget.getBaseElementSpec();
        EmbeddableWidget targetEntityWidget = (EmbeddableWidget) scene.findBaseElement(sourceEmbeddedAttribute.getConnectedClassId());
        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, null));

    }

    private void loadRelationEdge(IPModelerScene scene, String abstractTool, PersistenceClassWidget sourcePersistenceClassWidget, RelationAttributeWidget sourceRelationAttributeWidget, Class<? extends RelationAttributeWidget>... targetRelationAttributeWidgetClass) {
        if (!sourceRelationAttributeWidget.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
            return;
        }
        RelationAttribute sourceRelationAttribute = (RelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec();
        EntityWidget targetEntityWidget = (EntityWidget) scene.findBaseElement(sourceRelationAttribute.getConnectedEntityId());
//                    Entity targetEntity = (Entity) targetEntityWidget.getBaseElementSpec();
        RelationAttributeWidget targetRelationAttributeWidget = null;
//                    RelationAttribute targetRelationAttribute;

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        String contextToolId;
        if (sourceRelationAttribute.getConnectedAttributeId() != null) {
            targetRelationAttributeWidget = targetEntityWidget.findRelationAttributeWidget(sourceRelationAttribute.getConnectedAttributeId(), targetRelationAttributeWidgetClass);
//                        targetRelationAttribute = (RelationAttribute) targetRelationAttributeWidget.getBaseElementSpec();
            contextToolId = "B" + abstractTool;//OTM_RELATION";
        } else {
            contextToolId = "U" + abstractTool;
        }
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceRelationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, targetRelationAttributeWidget));

    }

    private void loadGeneralization(IPModelerScene scene, JavaClassWidget javaClassWidget) {

        JavaClass javaClass = (JavaClass) javaClassWidget.getBaseElementSpec();
        if (javaClass.getSuperclassId() != null) {
            JavaClassWidget subJavaClassWidget = javaClassWidget;
            JavaClassWidget superJavaClassWidget = (JavaClassWidget) scene.findBaseElement(javaClass.getSuperclassId());
            EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
            edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
            edgeInfo.setSource(subJavaClassWidget.getNodeWidgetInfo().getId());
            edgeInfo.setTarget(superJavaClassWidget.getNodeWidgetInfo().getId());

            edgeInfo.setType(NBModelerUtil.getEdgeType(subJavaClassWidget, superJavaClassWidget, "GENERALIZATION"));
            IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

            scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(subJavaClassWidget, superJavaClassWidget, edgeWidget, null));
            scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(subJavaClassWidget, superJavaClassWidget, edgeWidget, null));

        }

    }

    private void loadDiagram(IModelerScene scene, Diagram diagram, DiagramElement diagramElement) {
//       JPAProcessUtil util = new JPAProcessUtil();
        if (diagramElement instanceof Shape) {
            Shape shape = (Shape) diagramElement;
            Bounds bounds = shape.getBounds();
            Widget widget = (Widget) scene.findBaseElement(shape.getElementRef());
            if (widget != null) {
                if (widget instanceof INodeWidget) { //reverse ref
                    INodeWidget nodeWidget = (INodeWidget) widget;
//                  nodeWidget.setPreferredSize(new Dimension((int) bounds.getWidth(), (int) bounds.getHeight()));
                    Point location = new Point((int) bounds.getX(), (int) bounds.getY());
                    nodeWidget.setPreferredLocation(location);
//                    nodeWidget.setActiveStatus(false);//Active Status is used to prevent reloading SVGDocument until complete document is loaded
//                    nodeWidget.setActiveStatus(true);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element : " + widget);
                }
            }
        } else if (diagramElement instanceof Edge) {
//            JPAEdge edge = (JPAEdge) diagramElement;
//            Widget widget = (Widget) scene.getBaseElement(edge.getJPAElement());
//            if (widget != null && widget instanceof EdgeWidget) {
//                if (widget instanceof SequenceFlowWidget) {
//                    SequenceFlowWidget sequenceFlowWidget = (SequenceFlowWidget) widget;
//                    sequenceFlowWidget.setControlPoints(edge.getWaypointCollection(), true);
//                    if (edge.getJPALabel() != null) {
//                        Bounds bound = edge.getJPALabel().getBounds();
////                        sequenceFlowWidget.getLabelManager().getLabelWidget().getParentWidget().setPreferredLocation(bound.toPoint());
//                        sequenceFlowWidget.getLabelManager().getLabelWidget().getParentWidget().setPreferredLocation(
//                                sequenceFlowWidget.getLabelManager().getLabelWidget().convertSceneToLocal(bound.toPoint()));
//                    }
//                } else if (widget instanceof AssociationWidget) {
//                    AssociationWidget associationWidget = (AssociationWidget) widget;
//                    associationWidget.setControlPoints(edge.getWaypointCollection(), true);
//                } else {
//                    throw new InvalidElmentException("Invalid JPA Element");
//                }
////                EdgeWidget edgeWidget = (EdgeWidget)widget;
////                edgeWidget.manageControlPoint();
//
//            }
//
        }
    }

    /*---------------------------------Load File End---------------------------------*/
    /*---------------------------------Save File Satrt---------------------------------*/
//      public static void saveJPA(final JPAFile file) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                saveJPAImpl(file);
//            }
//        };
//        final RequestProcessor.Task theTask = RP.create(runnable);
//        final ProgressHandle ph = ProgressHandleFactory.createHandle("Saving JPA File...", theTask);
//        theTask.addTaskListener(new TaskListener() {
//            @Override
//            public void taskFinished(org.openide.util.Task task) {
//                ph.finish();
//            }
//        });
//        ph.start();
//        theTask.schedule(0);
//    }
//
    @Override
    public void saveModelerFile(ModelerFile file) {
        try {
            updateJPADiagram(file);

            IModelerScene scene = file.getModelerScene();
            EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
            entityMappings.getIdClass().clear();
            for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
                if (baseElementWidget instanceof FlowNodeWidget) {
                    FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                    FlowNode flowNode = (FlowNode) flowNodeWidget.getBaseElementSpec();
                    flowNode.setMinimized(flowNodeWidget.isMinimized());
                    if (baseElementWidget instanceof JavaClassWidget) {
                        JavaClassWidget javaClassWidget = (JavaClassWidget) baseElementWidget;
                        if (javaClassWidget.getOutgoingGeneralizationFlowWidget() != null) { //set inheritence class
                            JavaClass javaClass = (JavaClass) javaClassWidget.getBaseElementSpec();
                            JavaClassWidget superJavaClassWidget = javaClassWidget.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
                            JavaClass superJavaClass = (JavaClass) superJavaClassWidget.getBaseElementSpec();
                            javaClass.setSuperclass(superJavaClass.getClazz());
                        }
                        if (baseElementWidget instanceof PersistenceClassWidget) {
                            if (baseElementWidget instanceof EntityWidget) {
                                EntityWidget entityWidget = (EntityWidget) baseElementWidget;
                                InheritenceHandler classSpec = (InheritenceHandler) entityWidget.getBaseElementSpec();
                                String inheritenceState = entityWidget.getInheritenceState();
                                if (inheritenceState.equals("LEAF") || inheritenceState.equals("SINGLETON")) {
                                    classSpec.setDiscriminatorColumn(null);
                                    classSpec.setInheritance(null);
                                } else if (inheritenceState.equals("ROOT")) {
                                    classSpec.setDiscriminatorValue(null);
                                }
//IdClass
                                Entity entitySpec = (Entity) baseElementWidget.getBaseElementSpec();
                                List<IdAttributeWidget> allIdAttributeWidgets = entityWidget.getAllIdAttributeWidgets();
                                if (allIdAttributeWidgets.size() > 1 && ("ROOT".equals(entityWidget.getInheritenceState()) || "SINGLETON".equals(entityWidget.getInheritenceState()))) {
                                    if (entitySpec.getIdClass() == null) {
                                        IdClass idClass = new IdClass();
                                        idClass.setClazz(entitySpec.getClazz() + "IdClass");
                                        entitySpec.setIdClass(idClass);
                                    }

                                    DefaultClass _class = new DefaultClass();
                                    IdClass idClass = entitySpec.getIdClass();
                                    _class.setClazz(idClass.getClazz());
                                    for (IdAttributeWidget idAttributeWidget : allIdAttributeWidgets) {
                                        Id idSpec = (Id) idAttributeWidget.getBaseElementSpec();
                                        DefaultAttribute attribute = new DefaultAttribute();
                                        attribute.setAttributeType(idSpec.getAttributeType());
                                        attribute.setName(idSpec.getName());
                                        _class.addAttribute(attribute);
                                    }
                                    entityMappings.addIdClass(_class);

                                } else {
                                    entitySpec.setIdClass(null);
                                }
                            }
//                            else if (baseElementWidget instanceof MappedSuperclassWidget) {
//                                MappedSuperclassWidget classWidget = (MappedSuperclassWidget) baseElementWidget;
//                                //IdClass
//                                MappedSuperclass classSpec = (MappedSuperclass) baseElementWidget.getBaseElementSpec();
//                                if (classWidget.getIdAttributeWidgets().size() > 1) {
//                                    if (classSpec.getIdClass() == null) {
//                                        IdClass idClass = new IdClass();
//                                        idClass.setClazz(classSpec.getClazz() + "IdClass");
//                                        classSpec.setIdClass(idClass);
//                                    }
//                                } else {
//                                    classSpec.setIdClass(null);
//                                }
//                            }

                            PersistenceClassWidget entityWidget = (PersistenceClassWidget) baseElementWidget;
                            //Entity entity = (Entity) entityWidget.getBaseElementSpec();
                            for (SingleValueEmbeddedAttributeWidget embeddedAttributeWidget : entityWidget.getSingleValueEmbeddedAttributeWidgets()) {
                                Embedded embedded = (Embedded) embeddedAttributeWidget.getBaseElementSpec();
                                EmbeddableWidget connectedEmbeddableWidget = (EmbeddableWidget) scene.findBaseElement(embedded.getConnectedClassId());
                                JavaClass connectedEmbeddable = (JavaClass) connectedEmbeddableWidget.getBaseElementSpec();
                                embedded.setAttributeType(connectedEmbeddable.getClazz());
                            }
                            for (MultiValueEmbeddedAttributeWidget embeddedAttributeWidget : entityWidget.getMultiValueEmbeddedAttributeWidgets()) {
                                ElementCollection elementCollection = (ElementCollection) embeddedAttributeWidget.getBaseElementSpec();
                                EmbeddableWidget connectedEmbeddableWidget = (EmbeddableWidget) scene.findBaseElement(elementCollection.getConnectedClassId());
                                JavaClass connectedEmbeddable = (JavaClass) connectedEmbeddableWidget.getBaseElementSpec();
                                elementCollection.setAttributeType(connectedEmbeddable.getClazz());
                            }
                            for (OTORelationAttributeWidget otoRelationAttributeWidget : entityWidget.getOneToOneRelationAttributeWidgets()) {
                                OneToOne oneToOne = (OneToOne) otoRelationAttributeWidget.getBaseElementSpec();
                                PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(oneToOne.getConnectedEntityId());
                                JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                                oneToOne.setTargetEntity(connectedEntity.getClazz());
                                String connectedAttributeId = oneToOne.getConnectedAttributeId();
                                if (!otoRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                    RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, OTORelationAttributeWidget.class);
                                    RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                    oneToOne.setMappedBy(relationAttribute.getName());
                                }
                            }
                            for (OTMRelationAttributeWidget otmRelationAttributeWidget : entityWidget.getOneToManyRelationAttributeWidgets()) {
                                OneToMany oneToMany = (OneToMany) otmRelationAttributeWidget.getBaseElementSpec();
                                PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(oneToMany.getConnectedEntityId());
                                JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                                oneToMany.setTargetEntity(connectedEntity.getClazz());
                                String connectedAttributeId = oneToMany.getConnectedAttributeId();
                                if (!otmRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                    RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, MTORelationAttributeWidget.class);
                                    RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                    oneToMany.setMappedBy(relationAttribute.getName());
                                }
                            }
                            for (MTORelationAttributeWidget mtoRelationAttributeWidget : entityWidget.getManyToOneRelationAttributeWidgets()) {
                                ManyToOne manyToOne = (ManyToOne) mtoRelationAttributeWidget.getBaseElementSpec();
                                PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(manyToOne.getConnectedEntityId());
                                JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                                manyToOne.setTargetEntity(connectedEntity.getClazz());
                                //Always Owner no need to set mappedBy
                            }
                            for (MTMRelationAttributeWidget mtmRelationAttributeWidget : entityWidget.getManyToManyRelationAttributeWidgets()) {
                                ManyToMany manyToMany = (ManyToMany) mtmRelationAttributeWidget.getBaseElementSpec();
                                PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(manyToMany.getConnectedEntityId());
                                JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                                manyToMany.setTargetEntity(connectedEntity.getClazz());
                                String connectedAttributeId = manyToMany.getConnectedAttributeId();
                                if (!mtmRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                    RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, MTMRelationAttributeWidget.class);
                                    RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                    manyToMany.setMappedBy(relationAttribute.getName());
                                }
                            }
                        }
                    }
                }

            }

            File savedFile = file.getFile();
            if (jpaModelContext == null) {
                jpaModelContext = JAXBContext.newInstance(new Class<?>[]{ShapeDesign.class, EntityMappings.class});
            }
            if (jpaModelMarshaller == null) {
                jpaModelMarshaller = jpaModelContext.createMarshaller();
            }

            // output pretty printed
            jpaModelMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jpaModelMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");

            jpaModelMarshaller.setEventHandler(new ValidateJAXB());

            jpaModelMarshaller.marshal(file.getDefinitionElement(), System.out);
            StringWriter sw = new StringWriter();
            jpaModelMarshaller.marshal(file.getDefinitionElement(), sw);

            FileUtils.writeStringToFile(savedFile, sw.toString());

        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        // file.get
    }

    public static ShapeDesign getJPAShapeDesign(INodeWidget nodeWidget) {
//        ShapeDesign shapeDesign = new ShapeDesign();
//        shapeDesign.setOuterShapeContext(new OuterShapeContext(
//                new GradientPaint(nodeWidget.getOuterElementStartBackgroundColor(), nodeWidget.getOuterElementStartOffset(),
//                        nodeWidget.getOuterElementEndBackgroundColor(), nodeWidget.getOuterElementEndOffset()),
//                new Border(nodeWidget.getOuterElementBorderColor(), nodeWidget.getOuterElementBorderWidth())));
//        shapeDesign.setInnerShapeContext(new InnerShapeContext(
//                new GradientPaint(nodeWidget.getInnerElementStartBackgroundColor(), nodeWidget.getInnerElementStartOffset(),
//                        nodeWidget.getInnerElementEndBackgroundColor(), nodeWidget.getInnerElementEndOffset()),
//                new Border(nodeWidget.getInnerElementBorderColor(), nodeWidget.getInnerElementBorderWidth())));
//        shapeDesign.beforeMarshal();
        return null;//shapeDesign;
    }

    @Override
    public INodeWidget updateNodeWidgetDesign(ShapeDesign shapeDesign, INodeWidget inodeWidget) {
        PNodeWidget nodeWidget = (PNodeWidget) inodeWidget;
        //ELEMENT_UPGRADE
//        if (shapeDesign != null) {
//            if (shapeDesign.getOuterShapeContext() != null) {
//                if (shapeDesign.getOuterShapeContext().getBackground() != null) {
//                    nodeWidget.setOuterElementStartBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getStartColor());
//                    nodeWidget.setOuterElementEndBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getOuterShapeContext().getBorder() != null) {
//                    nodeWidget.setOuterElementBorderColor(shapeDesign.getOuterShapeContext().getBorder().getColor());
//                    nodeWidget.setOuterElementBorderWidth(shapeDesign.getOuterShapeContext().getBorder().getWidth());
//                }
//            }
//            if (shapeDesign.getInnerShapeContext() != null) {
//                if (shapeDesign.getInnerShapeContext().getBackground() != null) {
//                    nodeWidget.setInnerElementStartBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getStartColor());
//                    nodeWidget.setInnerElementEndBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getInnerShapeContext().getBorder() != null) {
//                    nodeWidget.setInnerElementBorderColor(shapeDesign.getInnerShapeContext().getBorder().getColor());
//                    nodeWidget.setInnerElementBorderWidth(shapeDesign.getInnerShapeContext().getBorder().getWidth());
//                }
//            }
//        }

        return (INodeWidget) nodeWidget;
    }

    public static void updateDiagramFlowElement(Plane plane, Widget widget) {
        //Diagram Model
        if (widget instanceof INodeWidget) { //reverse ref
            INodeWidget nodeWidget = (INodeWidget) widget;

            Rectangle rec = nodeWidget.getSceneViewBound();

            Shape shape = new Shape();
            shape.setBounds(new Bounds(rec));//(new Bounds(flowNodeWidget.getBounds()));
            shape.setElementRef(((IBaseElementWidget) nodeWidget).getId());
            shape.setId(((IBaseElementWidget) nodeWidget).getId() + "_gui");
//            if (nodeWidget.getLabelManager() != null && nodeWidget.getLabelManager().isVisible() && nodeWidget.getLabelManager().getLabel() != null && !nodeWidget.getLabelManager().getLabel().trim().isEmpty()) {
//                Rectangle bound = nodeWidget.getLabelManager().getLabelWidget().getParentWidget().getPreferredBounds();
//                bound = nodeWidget.getLabelManager().getLabelWidget().getParentWidget().convertLocalToScene(bound);
//
//                Rectangle rec_label = new Rectangle(bound.x, bound.y, (int) bound.getWidth(), (int) bound.getHeight());
//
////                JPALabel label = new JPALabel();
////                label.setBounds(new Bounds(rec_label));
////                shape.setJPALabel(label);
//            }
            plane.addDiagramElement(shape);

//            ShapeDesign shapeDesign = null;// JPAShapeDesign XML Location Change Here
//            if (nodeWidget instanceof FlowNodeWidget) {
//                TFlowNode flowNode = (TFlowNode) ((FlowNodeWidget) nodeWidget).getBaseElementSpec();
//                if (flowNode.getExtensionElements() == null) {
//                    flowNode.setExtensionElements(new TExtensionElements());
//                }
//                TExtensionElements extensionElements = flowNode.getExtensionElements();
//                for (Object obj : extensionElements.getAny()) {
//                    if (obj instanceof Element) { //first time save
//                        Element element = (Element) obj;
//                        if ("ShapeDesign".equals(element.getNodeName())) {
//                            shapeDesign = getJPAShapeDesign(nodeWidget);
//                            extensionElements.getAny().remove(obj);
//                            extensionElements.getAny().add(shapeDesign);
//                            break;
//                        }
//                    } else if (obj instanceof ShapeDesign) {
//                        shapeDesign = getJPAShapeDesign(nodeWidget);
//                        extensionElements.getAny().remove(obj);
//                        extensionElements.getAny().add(shapeDesign);
//                        break;
//                    }
//                }
//            }
//            if (shapeDesign == null) {
//                if (nodeWidget instanceof FlowNodeWidget) {
//                    TFlowNode flowNode = (TFlowNode) ((FlowNodeWidget) nodeWidget).getBaseElementSpec();
//                    TExtensionElements extensionElements = flowNode.getExtensionElements();
//                    shapeDesign = getJPAShapeDesign(nodeWidget);
//                    extensionElements.getAny().add(shapeDesign);
//                }
//            }
//            shape.setShapeDesign(getJPAShapeDesign(nodeWidget));
//            if (nodeWidget instanceof SubProcessWidget) {
//                SubProcessWidget subProcessWidget = (SubProcessWidget) nodeWidget;
//                for (FlowElementWidget flowElementChildrenWidget : subProcessWidget.getFlowElements()) {
//                    updateDiagramFlowElement(plane, (Widget) flowElementChildrenWidget);
//                }
//            }
        } else if (widget instanceof EdgeWidget) {
//            EdgeWidget edgeWidget = (EdgeWidget) widget;
//            JPAEdge edge = new JPAEdge();
//            for (java.awt.Point point : edgeWidget.getControlPoints()) {
//                edge.addWaypoint(point);
//            }
//            edge.setJPAElement(((BaseElementWidget) edgeWidget).getId());
//            edge.setId(((BaseElementWidget) edgeWidget).getId() + "_gui");
//
//            if (widget instanceof SequenceFlowWidget) {
//                if (edgeWidget.getLabelManager() != null && edgeWidget.getLabelManager().isVisible() && edgeWidget.getLabelManager().getLabel() != null && !edgeWidget.getLabelManager().getLabel().trim().isEmpty()) {
//                    Rectangle bound = edgeWidget.getLabelManager().getLabelWidget().getParentWidget().getPreferredBounds();
//                    bound = edgeWidget.getLabelManager().getLabelWidget().getParentWidget().convertLocalToScene(bound);
//
//                    Rectangle rec = new Rectangle(bound.x, bound.y, (int) bound.getWidth(), (int) bound.getHeight());
//
//                    JPALabel label = new JPALabel();
//                    label.setBounds(new Bounds(rec));
//                    edge.setJPALabel(label);
//                }
//            }
//            plane.addDiagramElement(edge);

        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    public static void updateJPADiagram(ModelerFile file) {
        Plane plane = ((Diagram) file.getDiagramElement()).getJPAPlane();
        plane.getDiagramElement().clear();
        IModelerScene processScene = file.getModelerScene();
        for (IBaseElementWidget flowElementWidget : processScene.getBaseElements()) {
            updateDiagramFlowElement(plane, (Widget) flowElementWidget);
        }
    }

    /*---------------------------------Save File End---------------------------------*/
    @Override
    public Anchor getAnchor(INodeWidget inodeWidget) {
        INodeWidget nodeWidget = (INodeWidget) inodeWidget;
        Anchor sourceAnchor;
//        NodeWidgetInfo nodeWidgetInfo = nodeWidget.getNodeWidgetInfo();
        if (nodeWidget instanceof IFlowNodeWidget) {
            sourceAnchor = new CustomRectangularAnchor(nodeWidget, 0, true);
        } else {
            throw new InvalidElmentException("Invalid JPA Process Element : " + nodeWidget);
        }
        return sourceAnchor;
    }

    @Override
    public void transformNode(IFlowNodeWidget flowNodeWidget, IModelerDocument document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IPinWidget attachPinWidget(IModelerScene scene, INodeWidget nodeWidget, PinWidgetInfo widgetInfo) {
        IPinWidget widget = null;
        if (widgetInfo.getDocumentId().equals(IdAttributeWidget.class.getSimpleName())) {
            widget = new IdAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(BasicAttributeWidget.class.getSimpleName())) {
            widget = new BasicAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(BasicCollectionAttributeWidget.class.getSimpleName())) {
            widget = new BasicCollectionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(TransientAttributeWidget.class.getSimpleName())) {
            widget = new TransientAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(VersionAttributeWidget.class.getSimpleName())) {
            widget = new VersionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(OTORelationAttributeWidget.class.getSimpleName())) {
            widget = new OTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(OTMRelationAttributeWidget.class.getSimpleName())) {
            widget = new OTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MTORelationAttributeWidget.class.getSimpleName())) {
            widget = new MTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MTMRelationAttributeWidget.class.getSimpleName())) {
            widget = new MTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(SingleValueEmbeddedAttributeWidget.class.getSimpleName())) {
            widget = new SingleValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MultiValueEmbeddedAttributeWidget.class.getSimpleName())) {
            widget = new MultiValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals("INTERNAL")) {
            widget = null; //widget = new PinWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else {
            throw new InvalidElmentException("Invalid JPA Pin Element");
        }
//        ((PNodeWidget) scene.findWidget(nodeWidgetInfo)).attachPinWidget(widget);
        return (IPinWidget) widget;
    }

    @Override
    public void dettachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dettachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        edgeWidget.setSourceAnchor(((IPModelerScene) scene).getPinAnchor(sourcePinWidget));

    }

    @Override
    public void attachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, INodeWidget sourceNodeWidget) { //BUG : Remove this method
        edgeWidget.setSourceAnchor(((IPNodeWidget) sourceNodeWidget).getNodeAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        edgeWidget.setTargetAnchor(((IPModelerScene) scene).getPinAnchor(targetPinWidget));
    }

    @Override
    public void attachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, INodeWidget targetNodeWidget) { //BUG : Remove this method
        edgeWidget.setTargetAnchor(((IPNodeWidget) targetNodeWidget).getNodeAnchor());
    }

    @Override
    public IEdgeWidget attachEdgeWidget(IModelerScene scene, EdgeWidgetInfo widgetInfo) {
        IEdgeWidget edgeWidget = getEdgeWidget(scene, widgetInfo);
        edgeWidget.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setRouter(scene.getRouter());
        ((IFlowEdgeWidget) edgeWidget).setName(widgetInfo.getName());

        return edgeWidget;
    }

    @Override
    public ResizeBorder getNodeBorder(INodeWidget nodeWidget) {
//        if (nodeWidget instanceof EntityWidget) {
        nodeWidget.setWidgetBorder(NodeWidget.RECTANGLE_RESIZE_BORDER);
        return PNodeWidget.RECTANGLE_RESIZE_BORDER;
//        }
//        else {
//            nodeWidget.setWidgetBorder(NodeWidget.CIRCLE_RESIZE_BORDER);
//            return PNodeWidget.CIRCLE_RESIZE_BORDER;
//        }
    }

    @Override
    public INodeWidget attachNodeWidget(IModelerScene scene, NodeWidgetInfo widgetInfo) {
        IFlowNodeWidget widget = null;
        IModelerDocument modelerDocument = widgetInfo.getModelerDocument();
        if (modelerDocument.getId().equals("Entity")) {
            widget = new EntityWidget(scene, widgetInfo);
        } else if (modelerDocument.getId().equals("MappedSuperclass")) {
            widget = new MappedSuperclassWidget(scene, widgetInfo);
        } else if (modelerDocument.getId().equals("Embeddable")) {
            widget = new EmbeddableWidget(scene, widgetInfo);
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

        return (INodeWidget) widget;
    }

    public IEdgeWidget getEdgeWidget(IModelerScene scene, EdgeWidgetInfo edgeWidgetInfo) {
        IEdgeWidget edgeWidget = null;
        if (edgeWidgetInfo.getType().equals("UOTO_RELATION")) {
            edgeWidget = new UOTORelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("BOTO_RELATION")) {
            edgeWidget = new BOTORelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("UOTM_RELATION")) {
            edgeWidget = new UOTMRelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("UMTO_RELATION")) {
            edgeWidget = new UMTORelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("BMTO_RELATION")) {
            edgeWidget = new BMTORelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("UMTM_RELATION")) {
            edgeWidget = new UMTMRelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("BMTM_RELATION")) {
            edgeWidget = new BMTMRelationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("GENERALIZATION")) {
            edgeWidget = new GeneralizationFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("SINGLE_EMBEDDABLE_RELATION")) {
            edgeWidget = new SingleValueEmbeddableFlowWidget(scene, edgeWidgetInfo);
        } else if (edgeWidgetInfo.getType().equals("MULTI_EMBEDDABLE_RELATION")) {
            edgeWidget = new MultiValueEmbeddableFlowWidget(scene, edgeWidgetInfo);
        }
//        else if (edgeWidgetInfo.getType().equals("ENTITY_OTM_RELATION")) {
//            edgeWidget = new OTMRelationFlowWidget(scene, edgeWidgetInfo);
//        }
        return edgeWidget;
    }

    @Override
    public String getEdgeType(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, String connectionContextToolId) {
        String edgeType = null;
//        if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("OTO_RELATION")) {
//            edgeType = "ENTITY_UOTO_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("OTM_RELATION")) {
//            edgeType = "ENTITY_UOTM_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("MTO_RELATION")) {
//            edgeType = "ENTITY_UMTO_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("MTM_RELATION")) {
//            edgeType = "ENTITY_UMTM_RELATION";
//        }
        edgeType = connectionContextToolId;
        return edgeType;
    }

    @Override
    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeSourcePinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, AttributeWidget sourceAttributeWidget) {
        if (sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EntityWidget && edgeWidget instanceof RelationFlowWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            RelationFlowWidget relationFlowWidget = (RelationFlowWidget) edgeWidget;
            RelationAttributeWidget relationAttributeWidget = null;
            if (relationFlowWidget instanceof OTORelationFlowWidget) {
                OTORelationAttributeWidget otoRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otoRelationAttributeWidget = sourcePersistenceWidget.addNewOneToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    otoRelationAttributeWidget = (OTORelationAttributeWidget) sourceAttributeWidget;
                }
                otoRelationAttributeWidget.setOneToOneRelationFlowWidget((OTORelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = otoRelationAttributeWidget;
            } else if (relationFlowWidget instanceof OTMRelationFlowWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otmRelationAttributeWidget = sourcePersistenceWidget.addNewOneToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    otmRelationAttributeWidget = (OTMRelationAttributeWidget) sourceAttributeWidget;
                }
                otmRelationAttributeWidget.setHierarchicalRelationFlowWidget((OTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = otmRelationAttributeWidget;
            } else if (relationFlowWidget instanceof MTORelationFlowWidget) {
                MTORelationAttributeWidget mtoRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtoRelationAttributeWidget = sourcePersistenceWidget.addNewManyToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    mtoRelationAttributeWidget = (MTORelationAttributeWidget) sourceAttributeWidget;
                }
                mtoRelationAttributeWidget.setManyToOneRelationFlowWidget((MTORelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = mtoRelationAttributeWidget;

            } else if (relationFlowWidget instanceof MTMRelationFlowWidget) {
                MTMRelationAttributeWidget mtmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtmRelationAttributeWidget = sourcePersistenceWidget.addNewManyToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    mtmRelationAttributeWidget = (MTMRelationAttributeWidget) sourceAttributeWidget;
                }
                mtmRelationAttributeWidget.setManyToManyRelationFlowWidget((MTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = mtmRelationAttributeWidget;
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            relationFlowWidget.setSourceRelationAttributeWidget(relationAttributeWidget);
            relationAttributeWidget.setOwner(true);
            return relationAttributeWidget.getPinWidgetInfo();

        } else if (edgeWidget instanceof GeneralizationFlowWidget) {
            JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) sourceNodeWidget;
            JavaClass sourceJavaClass = (JavaClass) sourceJavaClassWidget.getBaseElementSpec();
            JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
            GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) edgeWidget;
            sourceJavaClass.setSuperclassId(targetJavaClassWidget.getBaseElementSpec().getId());
            generalizationFlowWidget.setSubclassWidget(sourceJavaClassWidget);
            generalizationFlowWidget.setSuperclassWidget(targetJavaClassWidget);
            return sourceJavaClassWidget.getInternalPinWidgetInfo();
        } else if (edgeWidget instanceof EmbeddableFlowWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
//            JavaClass sourceJavaClass = (JavaClass) sourcePersistenceWidget.getBaseElementSpec();
            EmbeddableWidget targetEmbeddableWidget = (EmbeddableWidget) targetNodeWidget;
            EmbeddableFlowWidget embeddableFlowWidget = (EmbeddableFlowWidget) edgeWidget;
            EmbeddedAttributeWidget embeddedAttributeWidget = null;
            if (edgeWidget instanceof SingleValueEmbeddableFlowWidget) {
                SingleValueEmbeddedAttributeWidget singleValueEmbeddedAttributeWidget;
                if (sourceAttributeWidget == null) {
                    singleValueEmbeddedAttributeWidget = sourcePersistenceWidget.addNewSingleValueEmbeddedAttribute(sourcePersistenceWidget.getNextAttributeName(targetEmbeddableWidget.getName()));
                } else {
                    singleValueEmbeddedAttributeWidget = (SingleValueEmbeddedAttributeWidget) sourceAttributeWidget;
                }
                singleValueEmbeddedAttributeWidget.setEmbeddableFlowWidget(embeddableFlowWidget);
                embeddedAttributeWidget = singleValueEmbeddedAttributeWidget;
            } else if (edgeWidget instanceof MultiValueEmbeddableFlowWidget) {
                MultiValueEmbeddedAttributeWidget multiValueEmbeddedAttributeWidget;
                if (sourceAttributeWidget == null) {
                    multiValueEmbeddedAttributeWidget = sourcePersistenceWidget.addNewMultiValueEmbeddedAttribute(sourcePersistenceWidget.getNextAttributeName(targetEmbeddableWidget.getName()));
                } else {
                    multiValueEmbeddedAttributeWidget = (MultiValueEmbeddedAttributeWidget) sourceAttributeWidget;
                }
                multiValueEmbeddedAttributeWidget.setEmbeddableFlowWidget(embeddableFlowWidget);
                embeddedAttributeWidget = multiValueEmbeddedAttributeWidget;
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            embeddableFlowWidget.setSourceEmbeddedAttributeWidget(embeddedAttributeWidget);
            return embeddedAttributeWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @Override
    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeTargetPinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, RelationAttributeWidget targetRelationAttributeWidget) {
        if (edgeWidget instanceof Direction && edgeWidget instanceof RelationFlowWidget && sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget) {
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            EntityWidget sourceEntityWidget = (EntityWidget) sourceNodeWidget;
            if (edgeWidget instanceof Unidirectional) {
                Unidirectional uRelationFlowWidget = (Unidirectional) edgeWidget;
                uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                targetEntityWidget.addInverseSideRelationFlowWidget((RelationFlowWidget) uRelationFlowWidget);
                if (targetRelationAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                }
                return targetEntityWidget.getInternalPinWidgetInfo();
            } else if (edgeWidget instanceof Bidirectional) {
                if (edgeWidget instanceof BOTORelationFlowWidget) {
                    BOTORelationFlowWidget botoRelationFlowWidget = (BOTORelationFlowWidget) edgeWidget;
                    OTORelationAttributeWidget targetOTORelationAttributeWidget;
                    if (targetRelationAttributeWidget == null) {
                        targetOTORelationAttributeWidget = targetEntityWidget.addNewOneToOneRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                        RelationAttributeWidget sourceOTORelationAttributeWidget = botoRelationFlowWidget.getSourceRelationAttributeWidget();
                        sourceOTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetOTORelationAttributeWidget);
                        targetOTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceOTORelationAttributeWidget);
                    } else {
                        targetOTORelationAttributeWidget = (OTORelationAttributeWidget) targetRelationAttributeWidget;
                    }
                    targetOTORelationAttributeWidget.setOneToOneRelationFlowWidget(botoRelationFlowWidget);
                    botoRelationFlowWidget.setTargetRelationAttributeWidget(targetOTORelationAttributeWidget);

                    return targetOTORelationAttributeWidget.getPinWidgetInfo();

                } else if (edgeWidget instanceof BMTORelationFlowWidget) {
                    BMTORelationFlowWidget bmtoRelationFlowWidget = (BMTORelationFlowWidget) edgeWidget;
                    OTMRelationAttributeWidget targetMTORelationAttributeWidget;
                    if (targetRelationAttributeWidget == null) {
                        targetMTORelationAttributeWidget = targetEntityWidget.addNewOneToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                        RelationAttributeWidget sourceMTORelationAttributeWidget = bmtoRelationFlowWidget.getSourceRelationAttributeWidget();
                        sourceMTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTORelationAttributeWidget);
                        targetMTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTORelationAttributeWidget);
                    } else {
                        targetMTORelationAttributeWidget = (OTMRelationAttributeWidget) targetRelationAttributeWidget;
                    }
                    targetMTORelationAttributeWidget.setHierarchicalRelationFlowWidget(bmtoRelationFlowWidget);
                    bmtoRelationFlowWidget.setTargetRelationAttributeWidget(targetMTORelationAttributeWidget);
                    return targetMTORelationAttributeWidget.getPinWidgetInfo();
                } else if (edgeWidget instanceof BMTMRelationFlowWidget) {
                    BMTMRelationFlowWidget bmtmRelationFlowWidget = (BMTMRelationFlowWidget) edgeWidget;
                    MTMRelationAttributeWidget targetMTMRelationAttributeWidget;
                    if (targetRelationAttributeWidget == null) {
                        targetMTMRelationAttributeWidget = targetEntityWidget.addNewManyToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                        RelationAttributeWidget sourceMTMRelationAttributeWidget = bmtmRelationFlowWidget.getSourceRelationAttributeWidget();
                        sourceMTMRelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTMRelationAttributeWidget);
                        targetMTMRelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTMRelationAttributeWidget);

                    } else {
                        targetMTMRelationAttributeWidget = (MTMRelationAttributeWidget) targetRelationAttributeWidget;
                    }
                    targetMTMRelationAttributeWidget.setManyToManyRelationFlowWidget(bmtmRelationFlowWidget);
                    bmtmRelationFlowWidget.setTargetRelationAttributeWidget(targetMTMRelationAttributeWidget);
                    return targetMTMRelationAttributeWidget.getPinWidgetInfo();
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        } else if (edgeWidget instanceof Direction && (sourceNodeWidget instanceof MappedSuperclassWidget || sourceNodeWidget instanceof EmbeddableWidget) && targetNodeWidget instanceof EntityWidget) {
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            if (edgeWidget instanceof Unidirectional) {
                Unidirectional uRelationFlowWidget = (Unidirectional) edgeWidget;
                uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                if (targetRelationAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                }
                return targetEntityWidget.getInternalPinWidgetInfo();
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        } else if (edgeWidget instanceof GeneralizationFlowWidget && sourceNodeWidget instanceof JavaClassWidget && targetNodeWidget instanceof JavaClassWidget) {
            JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
            return targetJavaClassWidget.getInternalPinWidgetInfo();
        } else if (edgeWidget instanceof EmbeddableFlowWidget && sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EmbeddableWidget) {
            EmbeddableWidget targetEmbeddableWidget = (EmbeddableWidget) targetNodeWidget;
            ((EmbeddableFlowWidget) edgeWidget).setTargetEmbeddableWidget(targetEmbeddableWidget);
            targetEmbeddableWidget.addIncomingEmbeddableFlowWidget((EmbeddableFlowWidget) edgeWidget);
            EmbeddedAttributeWidget sourceEmbeddedAttributeWidget = ((EmbeddableFlowWidget) edgeWidget).getSourceEmbeddedAttributeWidget();
            sourceEmbeddedAttributeWidget.setConnectedSibling(targetEmbeddableWidget);
            return targetEmbeddableWidget.getInternalPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static ComboBoxPropertySupport getAccessTypeProperty(IModelerScene modelerScene, final AccessTypeHandler accessTypeHandlerSpec) {
        ComboBoxListener comboBoxListener = new ComboBoxListener() {
            @Override
            public void setItem(ComboBoxValue value) {
                accessTypeHandlerSpec.setAccess((AccessType) value.getValue());
            }

            @Override
            public ComboBoxValue getItem() {
                if (accessTypeHandlerSpec.getAccess() != null) {
                    return new ComboBoxValue(accessTypeHandlerSpec.getAccess(), accessTypeHandlerSpec.getAccess().value());
                } else {
                    return new ComboBoxValue(AccessType.getDefault(), AccessType.getDefault().value());
                }
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                ComboBoxValue[] values = new ComboBoxValue[]{
                    new ComboBoxValue(AccessType.FIELD, "Field"),
                    new ComboBoxValue(AccessType.PROPERTY, "Property")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "Field";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "accessType", "Access Type", "", comboBoxListener);
    }

    public static ComboBoxPropertySupport getFetchTypeProperty(IModelerScene modelerScene, final FetchTypeHandler fetchTypeHandlerSpec) {
        ComboBoxListener comboBoxListener = new ComboBoxListener() {
            @Override
            public void setItem(ComboBoxValue value) {
                fetchTypeHandlerSpec.setFetch((FetchType) value.getValue());
            }

            @Override
            public ComboBoxValue getItem() {
                return new ComboBoxValue(fetchTypeHandlerSpec.getFetch(), fetchTypeHandlerSpec.getFetch() != null ? fetchTypeHandlerSpec.getFetch().value() : null);
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                ComboBoxValue[] values = new ComboBoxValue[]{
                    new ComboBoxValue(FetchType.EAGER, "Eager"),
                    new ComboBoxValue(FetchType.LAZY, "Lazy")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "fetchType", "Fetch Type", "", comboBoxListener);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, IModelerScene modelerScene, final List<JoinColumn> joinColumnsSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JoinColumnPanel());

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = joinColumnsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<JoinColumn> joinColumns = joinColumnsSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<JoinColumn> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    JoinColumn joinColumn = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = joinColumn;
                    row[1] = joinColumn.getName();
                    row[2] = joinColumn.getReferencedColumnName();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List data) {
                joinColumnsSpec.clear();
                for (Object[] row : (List<Object[]>) data) {
                    joinColumnsSpec.add((JoinColumn) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

}
