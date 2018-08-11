/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.initializer;

import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.VIEW_JSONB;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.design.Bounds;
import io.github.jeddict.jpa.spec.design.Diagram;
import io.github.jeddict.jpa.spec.design.DiagramElement;
import io.github.jeddict.jpa.spec.design.Shape;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.jpa.spec.workspace.WorkSpaceElement;
import io.github.jeddict.jpa.spec.workspace.WorkSpaceItem;
import io.github.jeddict.jsonb.modeler.spec.JSONBBranchNode;
import io.github.jeddict.jsonb.modeler.spec.JSONBDocument;
import io.github.jeddict.jsonb.modeler.spec.JSONBLeafNode;
import io.github.jeddict.jsonb.modeler.spec.JSONBMapping;
import io.github.jeddict.jsonb.modeler.spec.JSONBNode;
import io.github.jeddict.jsonb.modeler.widget.BranchNodeWidget;
import io.github.jeddict.jsonb.modeler.widget.DocumentWidget;
import io.github.jeddict.jsonb.modeler.widget.GeneralizationFlowWidget;
import io.github.jeddict.jsonb.modeler.widget.JSONNodeWidget;
import io.github.jeddict.jsonb.modeler.widget.ReferenceFlowWidget;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import javax.swing.ImageIcon;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.util.IModelerUtil;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;

public class JSONBModelerUtil implements IModelerUtil<JSONBModelerScene> {

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
            JSON_DOCUMENT_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/DOCUMENT.png";
            OBJECT_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/OBJECT.png";
            ARRAY_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/ARRAY.png";
            TEXT_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/TEXT.png";
            ENUM_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/ENUM.png";
            NUMBER_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/NUMBER.png";
            DATE_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/DATE.png";
            TIME_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/TIME.png";
            BOOLEAN_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/BOOLEAN.png";
            UNKNOWN_ICON_PATH = "io/github/jeddict/jsonb/modeler/resource/image/UNKNOWN.png";
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
            if (diagram != null && !diagram.getJPAPlane().getDiagramElement().isEmpty()) {
                diagram.getJPAPlane().getDiagramElement()
                        .forEach(diagramElement -> loadDiagram(scene, diagramElement));
                itemSize = diagram.getJPAPlane().getDiagramElement().size();
                drawItemSize = 0;
            } else {
                drawItemSize = entityMappings.getCurrentWorkSpace().getItems()
                        .stream()
                        .peek(item -> loadDiagram(scene, item))
                        .filter(item -> item.getLocation() != null)
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
            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(modelerDocument, new Point(0, 0));
            nodeWidgetInfo.setId(document.getJavaClass().getId());
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
        
    @Deprecated
    private void loadDiagram(JSONBModelerScene scene, DiagramElement diagramElement) {
        if (diagramElement instanceof Shape) {
            Shape shape = (Shape) diagramElement;
            Bounds bounds = shape.getBounds();
            Widget widget = (Widget) scene.getBaseElement(shape.getElementRef());
            if (widget != null) {
                if (widget instanceof INodeWidget) {
                    INodeWidget nodeWidget = (INodeWidget) widget;
                    Point location = new Point((int) bounds.getX(), (int) bounds.getY());
                    nodeWidget.setPreferredLocation(location);
                    scene.reinstallColorScheme(nodeWidget);
                } else {
                    throw new InvalidElmentException("Invalid JSON Element : " + widget);
                }
            }
        }
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
            loadReferenceEdge(scene, "REFERENCE", e -> new ReferenceFlowWidget(scene, e),
                         documentWidget, branchNodeWidget);
        }
    }

    private void loadReferenceEdge(JSONBModelerScene scene, 
            String contextToolId, 
            Function<EdgeWidgetInfo, IEdgeWidget> edgeWidgetFunction,
            DocumentWidget sourceDocumentWidget, 
            BranchNodeWidget branchNodeWidget) {
//       JSONBBranchNode => Source  &&  DocumentWidget => Target
        JSONBBranchNode sourceNode = branchNodeWidget.getBaseElementSpec();
        DocumentWidget targetDocumentWidget = (DocumentWidget) scene.getBaseElement(sourceNode.getDocumentReference().getJavaClass().getId());

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(edgeWidgetFunction);
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourceDocumentWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetDocumentWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(contextToolId);
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(sourceDocumentWidget, targetDocumentWidget, branchNodeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(sourceDocumentWidget, targetDocumentWidget, null));
    }

    private void loadGeneralization(JSONBModelerScene scene, DocumentWidget documentWidget) {
        JSONBDocument document = documentWidget.getBaseElementSpec();
        if (document.getJavaClass().getSuperclass() != null) {
            DocumentWidget subDocumentWidget = documentWidget;
            DocumentWidget superDocumentWidget = (DocumentWidget) scene.getBaseElement(document.getJavaClass().getSuperclass().getId());
            EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(e -> new GeneralizationFlowWidget(scene, e));
            edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
            edgeInfo.setSource(subDocumentWidget.getNodeWidgetInfo().getId());
            edgeInfo.setTarget(superDocumentWidget.getNodeWidgetInfo().getId());
            edgeInfo.setType("GENERALIZATION");
            IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
            scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(subDocumentWidget, superDocumentWidget, null));
            scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(subDocumentWidget, superDocumentWidget, null));
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
    public void loadBaseElement(IBaseElementWidget parentConatiner, Map<IBaseElement, Rectangle> elements) {
        throw new UnsupportedOperationException("CPP not supported in DB Modeler");
    }

    @Override
    public List<IBaseElement> clone(List<IBaseElement> element) {
        throw new UnsupportedOperationException("Clonning not supported in DB Modeler");
    }
}
