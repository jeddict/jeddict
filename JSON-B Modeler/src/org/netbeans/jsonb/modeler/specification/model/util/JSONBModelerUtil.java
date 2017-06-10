/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.modeler.specification.model.util;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.swing.ImageIcon;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.jsonb.modeler.specification.model.scene.JSONBModelerScene;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceElement;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceItem;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.VIEW_JSONB;
import org.netbeans.jsonb.modeler.core.widget.BranchNodeWidget;
import org.netbeans.jsonb.modeler.core.widget.DocumentWidget;
import org.netbeans.jsonb.modeler.core.widget.GeneralizationFlowWidget;
import org.netbeans.jsonb.modeler.core.widget.JSONNodeWidget;
import org.netbeans.jsonb.modeler.core.widget.LeafNodeWidget;
import org.netbeans.jsonb.modeler.core.widget.ReferenceFlowWidget;
import org.netbeans.jsonb.modeler.spec.JSONBBranchNode;
import org.netbeans.jsonb.modeler.spec.JSONBDocument;
import org.netbeans.jsonb.modeler.spec.JSONBLeafNode;
import org.netbeans.jsonb.modeler.spec.JSONBMapping;
import org.netbeans.jsonb.modeler.spec.JSONBNode;
import org.netbeans.modeler.anchors.CustomRectangularAnchor;
import org.netbeans.modeler.border.ResizeBorder;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.shape.ShapeDesign;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.model.util.PModelerUtil;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.node.NodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class JSONBModelerUtil implements PModelerUtil<JSONBModelerScene> {

    public static String JSON_DOCUMENT_ICON_PATH;
    public static Image JSON_DOCUMENT;

    public static String OBJECT_ICON_PATH;
    public static Image OBJECT_ICON;
    public static String ARRAY_ICON_PATH;
    public static Image ARRAY_ICON;
    public static String TEXT_ICON_PATH;
    public static Image TEXT_ICON;    
    public static String ENUM_ICON_PATH;
    public static Image ENUM_ICON;
    public static String NUMBER_ICON_PATH;
    public static Image NUMBER_ICON;
    public static String DATE_ICON_PATH;
    public static Image DATE_ICON;
    public static String TIME_ICON_PATH;
    public static Image TIME_ICON;
    public static String BOOLEAN_ICON_PATH;
    public static Image BOOLEAN_ICON;
    public static String UNKNOWN_ICON_PATH;
    public static Image UNKNOWN_ICON;
    public static Image TAB_ICON;

    @Override
    public void init() {
        if (JSON_DOCUMENT == null) {
            ClassLoader cl = JSONBModelerUtil.class.getClassLoader();
            JSON_DOCUMENT_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/DOCUMENT.png";
            OBJECT_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/OBJECT.png";
            ARRAY_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/ARRAY.png";
            TEXT_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/TEXT.png";
            ENUM_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/ENUM.png";
            NUMBER_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/NUMBER.png";
            DATE_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/DATE.png";
            TIME_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/TIME.png";
            BOOLEAN_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/BOOLEAN.png";
            UNKNOWN_ICON_PATH = "org/netbeans/jsonb/modeler/resource/image/UNKNOWN.png";
            TAB_ICON = VIEW_JSONB.getImage();
            JSON_DOCUMENT = new ImageIcon(cl.getResource(JSON_DOCUMENT_ICON_PATH)).getImage();
            OBJECT_ICON = new ImageIcon(cl.getResource(OBJECT_ICON_PATH)).getImage();
            ARRAY_ICON = new ImageIcon(cl.getResource(ARRAY_ICON_PATH)).getImage();
            TEXT_ICON = new ImageIcon(cl.getResource(TEXT_ICON_PATH)).getImage();
            ENUM_ICON = new ImageIcon(cl.getResource(ENUM_ICON_PATH)).getImage();
            NUMBER_ICON = new ImageIcon(cl.getResource(NUMBER_ICON_PATH)).getImage();
            DATE_ICON = new ImageIcon(cl.getResource(DATE_ICON_PATH)).getImage();
            TIME_ICON = new ImageIcon(cl.getResource(TIME_ICON_PATH)).getImage();
            BOOLEAN_ICON = new ImageIcon(cl.getResource(BOOLEAN_ICON_PATH)).getImage();
            UNKNOWN_ICON = new ImageIcon(cl.getResource(UNKNOWN_ICON_PATH)).getImage();
        }

    }

    @Override
    public void loadModelerFile(ModelerFile file) throws org.netbeans.modeler.core.exception.ProcessInterruptedException {
        try {
            JSONBModelerScene scene = (JSONBModelerScene) file.getModelerScene();
            scene.startSceneGeneration();

            EntityMappings entityMappings = (EntityMappings) file.getAttributes().get(EntityMappings.class.getSimpleName());
            WorkSpace workSpace = (WorkSpace) file.getAttributes().get(WorkSpace.class.getSimpleName());
            JSONBMapping jsonbMapping = new JSONBMapping(entityMappings, workSpace);
            scene.setBaseElementSpec(jsonbMapping);
            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMappings);

            List<DocumentWidget> documentWidgets = jsonbMapping.getDocuments()
                    .stream()
                    .map(document -> loadDocument(scene, document))
                    .collect(toList());
            documentWidgets.forEach(documentWidget -> loadAttribute(documentWidget));
            documentWidgets.forEach(documentWidget -> loadFlowEdge(documentWidget));
            
            Diagram diagram = entityMappings.getJPADiagram();
            int itemSize;
            long drawItemSize;
            if (diagram != null) {
                itemSize = entityMappings.getJPADiagram().getJPAPlane().getDiagramElement().size();
                drawItemSize = 0;
            } else {
                drawItemSize = entityMappings.getCurrentWorkSpace().getItems()
                        .stream()
                        .filter(item -> item.getLocation() != null)
                        .peek(item -> loadDiagram(scene, item))
                        .count();
                itemSize = entityMappings.getCurrentWorkSpace().getItems().size();
            }
            if (entityMappings.isGenerated() || drawItemSize != itemSize) {
                scene.autoLayout();
            }
            
            scene.commitSceneGeneration();
        } catch (Exception ex) {
            ex.printStackTrace();
            file.handleException(ex);
            throw new ProcessInterruptedException(ex.getMessage());
        }
    }

    private DocumentWidget loadDocument(JSONBModelerScene scene, JSONBDocument document) {
        IModelerDocument modelerDocument = null;
        ModelerDocumentFactory modelerDocumentFactory = scene.getModelerFile().getModelerDiagramModel().getModelerDocumentFactory();
            try {
                modelerDocument = modelerDocumentFactory.getModelerDocument(document);
            } catch (ModelerException ex) {
                scene.getModelerFile().handleException(ex);
            }
            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(document.getJavaClass().getId(), modelerDocument, new Point(0, 0));
            nodeWidgetInfo.setName(document.getName());
            nodeWidgetInfo.setExist(Boolean.TRUE);
            nodeWidgetInfo.setBaseElementSpec(document);
            INodeWidget nodeWidget = scene.createNodeWidget(nodeWidgetInfo);
            if (document.getName() != null) {
                nodeWidget.setLabel(document.getName());
            }
            if (document.isMinimized()) {
                ((PNodeWidget) nodeWidget).setMinimized(true);
            }
            DocumentWidget documentWidget = (DocumentWidget) nodeWidget;
            return documentWidget;
    }
    
    private void loadDiagram(JSONBModelerScene scene, WorkSpaceItem workSpaceItem) {
        DocumentWidget documentWidget = (DocumentWidget) scene.getBaseElement(workSpaceItem.getJavaClass().getId());
        if (documentWidget != null) {
            documentWidget.setPreferredLocation(workSpaceItem.getLocation());
            documentWidget.setTextDesign(workSpaceItem.getJsonbTextDesign());
            for (JSONNodeWidget<? extends JSONBNode> nodeWidget : documentWidget.getAllNodeWidgets()) {
                WorkSpaceElement workSpaceElement = workSpaceItem.getWorkSpaceElementMap().get(nodeWidget.getBaseElementSpec().getAttribute());
                if (workSpaceElement == null) {
                    workSpaceItem.addWorkSpaceElement(workSpaceElement = new WorkSpaceElement(nodeWidget.getBaseElementSpec().getAttribute()));
                }
                nodeWidget.setTextDesign(workSpaceElement.getJsonbTextDesign());
            }
            scene.reinstallColorScheme(documentWidget);
        } else {
            throw new InvalidElmentException("Invalid JSONB Element : " + documentWidget);
        }
    }
    

    private void loadAttribute(DocumentWidget documentWidget) {
        JSONBDocument document = documentWidget.getBaseElementSpec();
        if (document.getNodes() != null) {
                document.getNodes().forEach(node -> {
                    if (node instanceof JSONBBranchNode) {
                        documentWidget.addBranchNode(node.getName(), node);
                    } else if (node instanceof JSONBLeafNode) {
                        documentWidget.addLeafNode(node.getName(), node);
                    }
                });
                documentWidget.sortNodes();
            }
    }
    private void loadFlowEdge(DocumentWidget documentWidget) {
        JSONBModelerScene scene = documentWidget.getModelerScene();
        loadGeneralization(scene, documentWidget);
        for (BranchNodeWidget branchNodeWidget : documentWidget.getBranchNodeWidgets()) {
            loadReferenceEdge(scene, "REFERENCE", documentWidget, branchNodeWidget);
        }
    }

    private void loadReferenceEdge(JSONBModelerScene scene, String contextToolId, DocumentWidget sourceDocumentWidget, BranchNodeWidget branchNodeWidget) {
//       JSONBBranchNode => Source  &&  DocumentWidget => Target
        JSONBBranchNode sourceNode = (JSONBBranchNode) branchNodeWidget.getBaseElementSpec();
        DocumentWidget targetDocumentWidget = (DocumentWidget) scene.getBaseElement(sourceNode.getDocumentReference().getJavaClass().getId());

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourceDocumentWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetDocumentWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourceDocumentWidget, targetDocumentWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourceDocumentWidget, targetDocumentWidget, edgeWidget, branchNodeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourceDocumentWidget, targetDocumentWidget, edgeWidget, null));
    }

    private void loadGeneralization(JSONBModelerScene scene, DocumentWidget documentWidget) {
        JSONBDocument document = documentWidget.getBaseElementSpec();
        if (document.getJavaClass().getSuperclass() != null) {
            DocumentWidget subDocumentWidget = documentWidget;
            DocumentWidget superDocumentWidget = (DocumentWidget) scene.getBaseElement(document.getJavaClass().getSuperclass().getId());
            EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
            edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
            edgeInfo.setSource(subDocumentWidget.getNodeWidgetInfo().getId());
            edgeInfo.setTarget(superDocumentWidget.getNodeWidgetInfo().getId());
            edgeInfo.setType(NBModelerUtil.getEdgeType(subDocumentWidget, superDocumentWidget, "GENERALIZATION"));
            IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
            scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(subDocumentWidget, superDocumentWidget, edgeWidget, null));
            scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(subDocumentWidget, superDocumentWidget, edgeWidget, null));
        }
    }
        
    @Override
    public void saveModelerFile(ModelerFile file) {
        file.getParentFile().getModelerUtil().saveModelerFile(file.getParentFile());
    }
    
    @Override
    public String getContent(ModelerFile file) {
        return file.getParentFile().getModelerUtil().getContent(file.getParentFile());
    }

    @Override
    public INodeWidget updateNodeWidgetDesign(ShapeDesign shapeDesign, INodeWidget inodeWidget) {
        return inodeWidget;
    }

    @Override
    public Anchor getAnchor(INodeWidget inodeWidget) {
        INodeWidget nodeWidget = inodeWidget;
        Anchor sourceAnchor;
        if (nodeWidget instanceof IFlowNodeWidget) {
            sourceAnchor = new CustomRectangularAnchor(nodeWidget, -5, true);
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
    public IPinWidget attachPinWidget(JSONBModelerScene scene, INodeWidget nodeWidget, PinWidgetInfo widgetInfo) {
        IPinWidget widget = null;
        if (widgetInfo.getDocumentId().equals(BranchNodeWidget.class.getSimpleName())) {
            widget = new BranchNodeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(LeafNodeWidget.class.getSimpleName())) {
            widget = new LeafNodeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else {
            throw new InvalidElmentException("Invalid JSONB Element");
        }
        return widget;
    }

    @Override
    public void dettachEdgeSourceAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dettachEdgeTargetAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEdgeSourceAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        edgeWidget.setSourceAnchor(sourcePinWidget.createAnchor());

    }

    @Override
    public void attachEdgeSourceAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, INodeWidget sourceNodeWidget) { //BUG : Remove this method
        edgeWidget.setSourceAnchor(((IPNodeWidget) sourceNodeWidget).getNodeAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        edgeWidget.setTargetAnchor(targetPinWidget.createAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(JSONBModelerScene scene, IEdgeWidget edgeWidget, INodeWidget targetNodeWidget) { //BUG : Remove this method
        edgeWidget.setTargetAnchor(((IPNodeWidget) targetNodeWidget).getNodeAnchor());
    }

    @Override
    public IEdgeWidget attachEdgeWidget(JSONBModelerScene scene, EdgeWidgetInfo widgetInfo) {
        IEdgeWidget edgeWidget = getEdgeWidget(scene, widgetInfo);
        edgeWidget.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setRouter(scene.getRouter());
        ((IFlowEdgeWidget) edgeWidget).setName(widgetInfo.getName());

        return edgeWidget;
    }

    @Override
    public ResizeBorder getNodeBorder(INodeWidget nodeWidget) {
        nodeWidget.setWidgetBorder(NodeWidget.RECTANGLE_RESIZE_BORDER);
        return PNodeWidget.RECTANGLE_RESIZE_BORDER;
    }

    @Override
    public INodeWidget attachNodeWidget(JSONBModelerScene scene, NodeWidgetInfo widgetInfo) {
        IFlowNodeWidget widget = null;
        IModelerDocument modelerDocument = widgetInfo.getModelerDocument();
        switch (modelerDocument.getId()) {
            case "DocumentWidget":
                widget = new DocumentWidget(scene, widgetInfo);
                break;
            default:
                throw new InvalidElmentException("Invalid JSONB Element");
        }
        return (INodeWidget) widget;
    }

    private IEdgeWidget getEdgeWidget(JSONBModelerScene scene, EdgeWidgetInfo edgeWidgetInfo) {
        IEdgeWidget edgeWidget = null;
        switch (edgeWidgetInfo.getType()) {
            case "GENERALIZATION":
                edgeWidget = new GeneralizationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "REFERENCE":
                edgeWidget = new ReferenceFlowWidget(scene, edgeWidgetInfo);
                break;
        }
        return edgeWidget;
    }

    @Override
    public String getEdgeType(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, String connectionContextToolId) {
        String edgeType = connectionContextToolId;
        return edgeType;
    }

    @Override
    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeSourcePinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, BranchNodeWidget sourceBranchNodeWidget) {
        if (edgeWidget instanceof ReferenceFlowWidget 
                && sourceBranchNodeWidget instanceof BranchNodeWidget) {
            ReferenceFlowWidget referenceFlowWidget = (ReferenceFlowWidget) edgeWidget;
            DocumentWidget targetDocumentWidget = (DocumentWidget) targetNodeWidget;
            referenceFlowWidget.setReferenceDocumentWidget(targetDocumentWidget);
            referenceFlowWidget.setBranchNodeWidget(sourceBranchNodeWidget);
            return sourceBranchNodeWidget.getPinWidgetInfo();
        } else if (edgeWidget instanceof GeneralizationFlowWidget) {
                DocumentWidget sourceDocumentWidget = (DocumentWidget) sourceNodeWidget;
                JSONBDocument sourceDocument = (JSONBDocument) sourceDocumentWidget.getBaseElementSpec();
                DocumentWidget targetDocumentWidget = (DocumentWidget) targetNodeWidget;
                JSONBDocument targetDocument = (JSONBDocument) targetDocumentWidget.getBaseElementSpec();
                GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) edgeWidget;
//                sourceDocument.addSuperclass(targetDocument);
                generalizationFlowWidget.setSubclassWidget(sourceDocumentWidget);
                generalizationFlowWidget.setSuperclassWidget(targetDocumentWidget);
                return sourceDocumentWidget.getInternalPinWidgetInfo();
            } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @Override
    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeTargetPinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, DocumentWidget targetDocumentWidget) {
        if(edgeWidget instanceof ReferenceFlowWidget || edgeWidget instanceof GeneralizationFlowWidget) {
            DocumentWidget documentWidget = (DocumentWidget)targetNodeWidget;
            return documentWidget.getInternalPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @Override
    public void loadBaseElement(IBaseElementWidget parentConatiner, Map<IBaseElement, Rectangle> elements) {
        throw new UnsupportedOperationException("CPP not supported in DB Modeler");
    }

    @Override
    public List<IBaseElement> clone(List<IBaseElement> element) {
        throw new UnsupportedOperationException("Clonning not supported in DB Modeler");
    }
}
