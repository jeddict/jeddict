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
package org.netbeans.jpa.modeler.core.widget.context;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.BASIC_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.BI_DIRECTIONAL;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.COMPOSITION_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.CREATE_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.DELETE_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.GENERALIZATION;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.ID_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MTMR_SOURCE_ANCHOR_SHAPE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MTOR_SOURCE_ANCHOR_SHAPE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.OTMR_SOURCE_ANCHOR_SHAPE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.OTOR_SOURCE_ANCHOR_SHAPE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.PK_BI_DIRECTIONAL;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.PK_UNI_DIRECTIONAL;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.SINGLE_VALUE_EMBEDDED_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.TRANSIENT_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.UNI_DIRECTIONAL;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.VERSION_ATTRIBUTE;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.context.ContextActionType;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.action.ConnectAction;
import org.netbeans.modeler.widget.context.action.ContextPaletteConnectDecorator;
import org.netbeans.modeler.widget.context.action.SceneConnectProvider;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultGroupButtonModel;
import org.netbeans.modeler.widget.context.base.DefaultPaletteButtonModel;
import org.netbeans.modeler.widget.node.INodeWidget;

public class NodeContextModel {

    public static MouseListener getPopupMenuLstener(final javax.swing.JPopupMenu addWidgetPopupMenu) {

        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Rectangle rec = evt.getComponent().getBounds();
                addWidgetPopupMenu.show(evt.getComponent(), (int) (rec.getX() + rec.getWidth()), 0);
            }
        };
    }

    private static ContextPaletteButtonModel getContextPaletteGroupButtonModel(String tooltip, Image image, ContextPaletteModel contextPaletteModel) {
        ContextPaletteButtonModel contextPaletteButtonModel = new DefaultGroupButtonModel();
        contextPaletteButtonModel.setImage(image);
        contextPaletteButtonModel.setTooltip(tooltip);
        contextPaletteButtonModel.setPaletteModel(contextPaletteModel);
        return contextPaletteButtonModel;
    }

    private static ContextPaletteButtonModel getContextPaletteButtonModel(String id, String tooltip, Image image, ContextPaletteModel contextPaletteModel) {
        ContextPaletteButtonModel contextPaletteButtonModel = new DefaultGroupButtonModel();
        contextPaletteButtonModel.setId(id);
        contextPaletteButtonModel.setImage(image);
        contextPaletteButtonModel.setTooltip(tooltip);
        contextPaletteButtonModel.setPaletteModel(contextPaletteModel);
        return contextPaletteButtonModel;
    }

    private static MouseListener getAddWidgetAction(final INodeWidget widget, final ContextPaletteButtonModel addAttributeModel) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (widget instanceof PersistenceClassWidget) {
                    PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) widget;
                    persistenceClassWidget.createPinWidget(addAttributeModel.getId());
                    widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
                }
            }
        };
    }

    public static ContextPaletteModel getContextPaletteModel(INodeWidget nodeWidget) {
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(nodeWidget);

//        ContextPaletteButtonModel setFocusModel = new DefaultPaletteButtonModel();
//        contextPaletteModel.getChildren().add(setFocusModel);
//        setFocusModel.setImage(Utilities.loadImage("org/netbeans/jpa/modeler/resource/image/attribute-association-override.png"));
//        setFocusModel.setTooltip("Attribute/Association Override");
//        setFocusModel.setPaletteModel(contextPaletteModel);
//        setFocusModel.setMouseListener(PinContextModel.getFocusWidgetAction(nodeWidget));
        ContextPaletteButtonModel generalizationConnectionModel = new DefaultPaletteButtonModel();
        generalizationConnectionModel.setId("GENERALIZATION");
        generalizationConnectionModel.setImage(GENERALIZATION);
        generalizationConnectionModel.setTooltip("Generalization (Drag from Concrete to Abstract)");
        generalizationConnectionModel.setPaletteModel(contextPaletteModel);
        generalizationConnectionModel.setContextActionType(ContextActionType.CONNECT);
        generalizationConnectionModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), generalizationConnectionModel.getId()));
        contextPaletteModel.getChildren().add(generalizationConnectionModel);

        ContextPaletteButtonModel addAttributeModel = getContextPaletteGroupButtonModel("Add attributes",
                CREATE_ICON.getImage(), contextPaletteModel);
        contextPaletteModel.getChildren().add(addAttributeModel);

        ContextPaletteButtonModel addIdAttributeModel = getContextPaletteButtonModel("ID_ATTRIBUTE", "Id Attribute",
                ID_ATTRIBUTE, contextPaletteModel);
        addIdAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addIdAttributeModel));
        ContextPaletteButtonModel addBasicAttributeModel = getContextPaletteButtonModel("BASIC_ATTRIBUTE", "Basic Attribute",
                BASIC_ATTRIBUTE, contextPaletteModel);
        addBasicAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicAttributeModel));
        ContextPaletteButtonModel addBasicCollectionAttributeModel = getContextPaletteButtonModel("BASIC_COLLECTION_ATTRIBUTE", "Basic ElementCollection Attribute",
               BASIC_COLLECTION_ATTRIBUTE, contextPaletteModel);
        addBasicCollectionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicCollectionAttributeModel));
        ContextPaletteButtonModel addTransientAttributeModel = getContextPaletteButtonModel("TRANSIENT_ATTRIBUTE", "Transient Attribute",
               TRANSIENT_ATTRIBUTE, contextPaletteModel);
        addTransientAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addTransientAttributeModel));
        ContextPaletteButtonModel addVersionAttributeModel = getContextPaletteButtonModel("VERSION_ATTRIBUTE", "Version Attribute",
                VERSION_ATTRIBUTE, contextPaletteModel);
        addVersionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addVersionAttributeModel));

        ContextPaletteButtonModel[] addAttributeSubModelList = null;
        if (nodeWidget instanceof EntityWidget) {
            if (((EntityWidget) nodeWidget).getInheritanceState() == ROOT || ((EntityWidget) nodeWidget).getInheritanceState() == SINGLETON) {
                addAttributeSubModelList = new ContextPaletteButtonModel[]{addIdAttributeModel, addBasicAttributeModel, addBasicCollectionAttributeModel, addTransientAttributeModel, addVersionAttributeModel};
            } else {
                addAttributeSubModelList = new ContextPaletteButtonModel[]{addBasicAttributeModel, addBasicCollectionAttributeModel, addTransientAttributeModel, addVersionAttributeModel};
            }
        } else if (nodeWidget instanceof MappedSuperclassWidget) {
            addAttributeSubModelList = new ContextPaletteButtonModel[]{addIdAttributeModel, addBasicAttributeModel, addBasicCollectionAttributeModel, addTransientAttributeModel, addVersionAttributeModel};
        } else if (nodeWidget instanceof EmbeddableWidget) {
            addAttributeSubModelList = new ContextPaletteButtonModel[]{addBasicAttributeModel, addBasicCollectionAttributeModel, addTransientAttributeModel};
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
        }
        for (ContextPaletteButtonModel addAttributeModel_TMP : addAttributeSubModelList) {
            addAttributeModel.getChildren().add(addAttributeModel_TMP);
        }

        ContextPaletteButtonModel compositionConnectionModel = getContextPaletteGroupButtonModel("Embedded (Drag to Embeddable)",
                COMPOSITION_ATTRIBUTE, contextPaletteModel);
        contextPaletteModel.getChildren().add(compositionConnectionModel);

        ContextPaletteButtonModel singleValueEmbeddedConnectionModal = getContextPaletteButtonModel("SINGLE_EMBEDDABLE_RELATION", "Single Value Embeddable Connection (Drag to Embeddable)",
                SINGLE_VALUE_EMBEDDED_ATTRIBUTE, contextPaletteModel);
        singleValueEmbeddedConnectionModal.setContextActionType(ContextActionType.CONNECT);
        singleValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), singleValueEmbeddedConnectionModal.getId()));
        compositionConnectionModel.getChildren().add(singleValueEmbeddedConnectionModal);

        ContextPaletteButtonModel collectionValueEmbeddedConnectionModal = getContextPaletteButtonModel("MULTI_EMBEDDABLE_RELATION", "Multi Value Embeddable Connection (Drag to Embeddable)",
                MULTI_VALUE_EMBEDDED_ATTRIBUTE, contextPaletteModel);
        collectionValueEmbeddedConnectionModal.setContextActionType(ContextActionType.CONNECT);
        collectionValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), collectionValueEmbeddedConnectionModal.getId()));
        compositionConnectionModel.getChildren().add(collectionValueEmbeddedConnectionModal);

        if (nodeWidget instanceof EntityWidget) {

            ContextPaletteButtonModel connectionOTOModel = new DefaultGroupButtonModel();
            connectionOTOModel.setId("OTO_RELATION");
            connectionOTOModel.setImage(OTOR_SOURCE_ANCHOR_SHAPE);
            connectionOTOModel.setTooltip("One To One Relation");
            connectionOTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionOTOModel);

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId("UOTO_RELATION");
            connectionUOTOModel.setImage(UNI_DIRECTIONAL);
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionBOTOModel = new DefaultPaletteButtonModel();
            connectionBOTOModel.setId("BOTO_RELATION");
            connectionBOTOModel.setImage(BI_DIRECTIONAL);
            connectionBOTOModel.setTooltip("Bidirectional One To One Relation");
            connectionBOTOModel.setPaletteModel(contextPaletteModel);
            connectionBOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionBOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionBOTOModel);

            ContextPaletteButtonModel connectionPKUOTOModel = new DefaultPaletteButtonModel();
            connectionPKUOTOModel.setId("PKUOTO_RELATION");
            connectionPKUOTOModel.setImage(PK_UNI_DIRECTIONAL);
            connectionPKUOTOModel.setTooltip("Unidirectional One To One Primary Key Relation");
            connectionPKUOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKUOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionPKUOTOModel);

            ContextPaletteButtonModel connectionPKBOTOModel = new DefaultPaletteButtonModel();
            connectionPKBOTOModel.setId("PKBOTO_RELATION");
            connectionPKBOTOModel.setImage(PK_BI_DIRECTIONAL);
            connectionPKBOTOModel.setTooltip("Bidirectional One To One Primary Key Relation");
            connectionPKBOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKBOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKBOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionPKBOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId("UOTM_RELATION");
            connectionOTMModel.setImage(OTMR_SOURCE_ANCHOR_SHAPE);
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionOTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionMTOModel = new DefaultGroupButtonModel();
            connectionMTOModel.setId("MTO_RELATION");
            connectionMTOModel.setImage(MTOR_SOURCE_ANCHOR_SHAPE);
            connectionMTOModel.setTooltip("Many To One Relation");
            connectionMTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTOModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId("UMTO_RELATION");
            connectionUMTOModel.setImage(UNI_DIRECTIONAL);
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionBMTOModel = new DefaultPaletteButtonModel();
            connectionBMTOModel.setId("BMTO_RELATION");
            connectionBMTOModel.setImage(BI_DIRECTIONAL);
            connectionBMTOModel.setTooltip("Bidirectional Many To One Relation");
            connectionBMTOModel.setPaletteModel(contextPaletteModel);
            connectionBMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionBMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionBMTOModel);

            ContextPaletteButtonModel connectionPKUMTOModel = new DefaultPaletteButtonModel();
            connectionPKUMTOModel.setId("PKUMTO_RELATION");
            connectionPKUMTOModel.setImage(PK_UNI_DIRECTIONAL);
            connectionPKUMTOModel.setTooltip("Unidirectional Many To One Primary Key Relation");
            connectionPKUMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKUMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionPKUMTOModel);

            ContextPaletteButtonModel connectionPKBMTOModel = new DefaultPaletteButtonModel();
            connectionPKBMTOModel.setId("PKBMTO_RELATION");
            connectionPKBMTOModel.setImage(PK_BI_DIRECTIONAL);
            connectionPKBMTOModel.setTooltip("Bidirectional Many To One Primary Key Relation");
            connectionPKBMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKBMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKBMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionPKBMTOModel);

            ContextPaletteButtonModel connectionMTMModel = new DefaultGroupButtonModel();
            connectionMTMModel.setId("MTM_RELATION");
            connectionMTMModel.setImage(MTMR_SOURCE_ANCHOR_SHAPE);
            connectionMTMModel.setTooltip("Many To Many Relation");
            connectionMTMModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTMModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId("UMTM_RELATION");
            connectionUMTMModel.setImage(UNI_DIRECTIONAL);
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTMModel.getId()));
            connectionMTMModel.getChildren().add(connectionUMTMModel);

            ContextPaletteButtonModel connectionBMTMModel = new DefaultPaletteButtonModel();
            connectionBMTMModel.setId("BMTM_RELATION");
            connectionBMTMModel.setImage(BI_DIRECTIONAL);
            connectionBMTMModel.setTooltip("Bidirectional Many To Many Relation");
            connectionBMTMModel.setPaletteModel(contextPaletteModel);
            connectionBMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionBMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBMTMModel.getId()));
            connectionMTMModel.getChildren().add(connectionBMTMModel);

        } else if (nodeWidget instanceof MappedSuperclassWidget || nodeWidget instanceof EmbeddableWidget) {

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId("UOTO_RELATION");
            connectionUOTOModel.setImage(OTOR_SOURCE_ANCHOR_SHAPE);
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUOTOModel.getId()));
            contextPaletteModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId("UOTM_RELATION");
            connectionOTMModel.setImage(OTMR_SOURCE_ANCHOR_SHAPE);
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionOTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId("UMTO_RELATION");
            connectionUMTOModel.setImage(MTOR_SOURCE_ANCHOR_SHAPE);
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTOModel.getId()));
            contextPaletteModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId("UMTM_RELATION");
            connectionUMTMModel.setImage(MTMR_SOURCE_ANCHOR_SHAPE);
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionUMTMModel);

        }

        ContextPaletteButtonModel deleteModel = new DefaultPaletteButtonModel();
        contextPaletteModel.getChildren().add(deleteModel);
        deleteModel.setImage(DELETE_ICON.getImage());
        deleteModel.setTooltip("Delete");
        deleteModel.setPaletteModel(contextPaletteModel);
        deleteModel.setMouseListener(getRemoveWidgetAction(nodeWidget));
        return contextPaletteModel;
    }

    private static WidgetAction[] getConnectActions(IModelerScene scene, String connectionContextToolId) {
        SceneConnectProvider connector = new SceneConnectProvider(connectionContextToolId);
        LayerWidget layer = scene.getInterractionLayer();
        WidgetAction action = new ConnectAction(new ContextPaletteConnectDecorator(), layer, connector);
        WidgetAction[] retVal = new WidgetAction[]{action};
        return retVal;
    }

    private static MouseListener getRemoveWidgetAction(final INodeWidget widget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                widget.remove(true);
                NBModelerUtil.hideContextPalette(widget.getModelerScene());
                widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
            }
        };
    }

//    private static MouseListener getFocusWidgetAction(final INodeWidget widget) {
//        return new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//
//                if (widget instanceof EntityWidget) {
//                    JPAModelerScene modelerScene = (JPAModelerScene) widget.getModelerScene();
//
//                    EntityWidget entityWidget = (EntityWidget) widget;
//                    if (entityWidget.isSelectedView()) {
////                        for (IBaseElementWidget baseElementWidget : entityWidget.getModelerScene().getBaseElements()) {
//////                            baseElementWidget.setVisible(true);
////                        }
////                        modelerScene.getWidgetHighlightLayer().setOpaque(false);
////                        modelerScene.setRouter(RouterFactory.createOrthogonalSearchRouter(modelerScene.getMainLayer(), modelerScene.getConnectionLayer()));
//
////                        modelerScene.getMainLayer().setVisible(true);
////                        modelerScene.getConnectionLayer().setVisible(true);
//                        entityWidget.hideInheritancePath();
//                        entityWidget.setSelectedView(false);
//                    } else {
////                        for (IBaseElementWidget baseElementWidget : entityWidget.getModelerScene().getBaseElements()) {
//////                            baseElementWidget.setVisible(false);
////                        }
////                        modelerScene.getWidgetHighlightLayer().setOpaque(true);
////                        modelerScene.setRouter(RouterFactory.createOrthogonalSearchRouter(modelerScene.getBoundaryWidgetLayer(), modelerScene.getWidgetHighlightLayer()));
////                        modelerScene.setRouter(RouterFactory.createFreeRouter());
//
////                        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(modelerScene, new GridGraphLayout<NodeWidgetInfo, EdgeWidgetInfo>().setChecker(true));
////                        sceneLayout.invokeLayout();
////                        modelerScene.getMainLayer().setVisible(false);
////                        modelerScene.getConnectionLayer().setVisible(false);
//                        entityWidget.showInheritancePath();
//                        entityWidget.setSelectedView(true);
//                    }
//
//                }
//
////                widget.remove(true);
//                NBModelerUtil.hideContextPalette(widget.getModelerScene());
////                widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//            }
//        };
//    }
}
