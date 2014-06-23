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

import java.awt.Rectangle;
import java.awt.event.MouseListener;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
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
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

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

    private static ContextPaletteButtonModel getContextPaletteGroupButtonModel(String tooltip, String imagePath, ContextPaletteModel contextPaletteModel) {
        ContextPaletteButtonModel contextPaletteButtonModel = new DefaultGroupButtonModel();
        contextPaletteButtonModel.setImage(ImageUtilities.loadImage(imagePath));
        contextPaletteButtonModel.setTooltip(tooltip);
        contextPaletteButtonModel.setPaletteModel(contextPaletteModel);
        return contextPaletteButtonModel;
    }

    private static ContextPaletteButtonModel getContextPaletteButtonModel(String id, String tooltip, String imagePath, ContextPaletteModel contextPaletteModel) {
        ContextPaletteButtonModel contextPaletteButtonModel = new DefaultGroupButtonModel();
        contextPaletteButtonModel.setId(id);
        contextPaletteButtonModel.setImage(ImageUtilities.loadImage(imagePath));
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
                    if ("ID_ATTRIBUTE".equals(addAttributeModel.getId())) {
                        persistenceClassWidget.addNewIdAttribute(persistenceClassWidget.getNextAttributeName("id"));
                    } else if ("BASIC_ATTRIBUTE".equals(addAttributeModel.getId())) {
                        persistenceClassWidget.addNewBasicAttribute(persistenceClassWidget.getNextAttributeName());
                    } else if ("BASIC_COLLECTION_ATTRIBUTE".equals(addAttributeModel.getId())) {
                        persistenceClassWidget.addNewBasicCollectionAttribute(persistenceClassWidget.getNextAttributeName());
                    } else if ("TRANSIENT_ATTRIBUTE".equals(addAttributeModel.getId())) {
                        persistenceClassWidget.addNewTransientAttribute(persistenceClassWidget.getNextAttributeName());
                    } else if ("VERSION_ATTRIBUTE".equals(addAttributeModel.getId())) {
                        persistenceClassWidget.addNewVersionAttribute(persistenceClassWidget.getNextAttributeName());
                    } else {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
                    }
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
        generalizationConnectionModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/generalization.png"));
        generalizationConnectionModel.setTooltip("Generalization (Drag from Concrete to Abstract)");
        generalizationConnectionModel.setPaletteModel(contextPaletteModel);
        generalizationConnectionModel.setContextActionType(ContextActionType.CONNECT);
        generalizationConnectionModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), generalizationConnectionModel.getId()));
        contextPaletteModel.getChildren().add(generalizationConnectionModel);

        ContextPaletteButtonModel addAttributeModel = getContextPaletteGroupButtonModel("Add attributes",
                "org/netbeans/jpa/modeler/resource/image/add-element.png", contextPaletteModel);
        contextPaletteModel.getChildren().add(addAttributeModel);

        ContextPaletteButtonModel addIdAttributeModel = getContextPaletteButtonModel("ID_ATTRIBUTE", "Id Attribute",
                "org/netbeans/jpa/modeler/resource/image/id-attribute.png", contextPaletteModel);
        addIdAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addIdAttributeModel));
        ContextPaletteButtonModel addBasicAttributeModel = getContextPaletteButtonModel("BASIC_ATTRIBUTE", "Basic Attribute",
                "org/netbeans/jpa/modeler/resource/image/basic-attribute.png", contextPaletteModel);
        addBasicAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicAttributeModel));
        ContextPaletteButtonModel addBasicCollectionAttributeModel = getContextPaletteButtonModel("BASIC_COLLECTION_ATTRIBUTE", "Basic ElementCollection Attribute",
                "org/netbeans/jpa/modeler/resource/image/basic-collection-attribute.png", contextPaletteModel);
        addBasicCollectionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicCollectionAttributeModel));
        ContextPaletteButtonModel addTransientAttributeModel = getContextPaletteButtonModel("TRANSIENT_ATTRIBUTE", "Transient Attribute",
                "org/netbeans/jpa/modeler/resource/image/transient-attribute.png", contextPaletteModel);
        addTransientAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addTransientAttributeModel));
        ContextPaletteButtonModel addVersionAttributeModel = getContextPaletteButtonModel("VERSION_ATTRIBUTE", "Version Attribute",
                "org/netbeans/jpa/modeler/resource/image/version-attribute.png", contextPaletteModel);
        addVersionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addVersionAttributeModel));

        ContextPaletteButtonModel[] addAttributeSubModelList = null;
        if (nodeWidget instanceof EntityWidget) {
            if ("ROOT".equals(((EntityWidget) nodeWidget).getInheritenceState()) || "SINGLETON".equals(((EntityWidget) nodeWidget).getInheritenceState())) {
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
                "org/netbeans/jpa/modeler/resource/image/composition.png", contextPaletteModel);
        contextPaletteModel.getChildren().add(compositionConnectionModel);

        ContextPaletteButtonModel singleValueEmbeddedConnectionModal = getContextPaletteButtonModel("SINGLE_EMBEDDABLE_RELATION", "Single Value Embeddable Connection (Drag to Embeddable)",
                "org/netbeans/jpa/modeler/resource/image/single-value-embedded.gif", contextPaletteModel);
        singleValueEmbeddedConnectionModal.setContextActionType(ContextActionType.CONNECT);
        singleValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), singleValueEmbeddedConnectionModal.getId()));
        compositionConnectionModel.getChildren().add(singleValueEmbeddedConnectionModal);

        ContextPaletteButtonModel collectionValueEmbeddedConnectionModal = getContextPaletteButtonModel("MULTI_EMBEDDABLE_RELATION", "Multi Value Embeddable Connection (Drag to Embeddable)",
                "org/netbeans/jpa/modeler/resource/image/multi-value-embedded.gif", contextPaletteModel);
        collectionValueEmbeddedConnectionModal.setContextActionType(ContextActionType.CONNECT);
        collectionValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), collectionValueEmbeddedConnectionModal.getId()));
        compositionConnectionModel.getChildren().add(collectionValueEmbeddedConnectionModal);

        if (nodeWidget instanceof EntityWidget) {

            ContextPaletteButtonModel connectionOTOModel = new DefaultGroupButtonModel();
            connectionOTOModel.setId("OTO_RELATION");
            connectionOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/one-to-one.gif"));
            connectionOTOModel.setTooltip("One To One Relation");
            connectionOTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionOTOModel);

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId("UOTO_RELATION");
            connectionUOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/uni.png"));
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionBOTOModel = new DefaultPaletteButtonModel();
            connectionBOTOModel.setId("BOTO_RELATION");
            connectionBOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/bi.png"));
            connectionBOTOModel.setTooltip("Bidirectional One To One Relation");
            connectionBOTOModel.setPaletteModel(contextPaletteModel);
            connectionBOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionBOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionBOTOModel);

            ContextPaletteButtonModel connectionPKUOTOModel = new DefaultPaletteButtonModel();
            connectionPKUOTOModel.setId("PKUOTO_RELATION");
            connectionPKUOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/pk-uni.png"));
            connectionPKUOTOModel.setTooltip("Unidirectional One To One Primary Key Relation");
            connectionPKUOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKUOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionPKUOTOModel);

            ContextPaletteButtonModel connectionPKBOTOModel = new DefaultPaletteButtonModel();
            connectionPKBOTOModel.setId("PKBOTO_RELATION");
            connectionPKBOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/pk-bi.png"));
            connectionPKBOTOModel.setTooltip("Bidirectional One To One Primary Key Relation");
            connectionPKBOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKBOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKBOTOModel.getId()));
            connectionOTOModel.getChildren().add(connectionPKBOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId("UOTM_RELATION");
            connectionOTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/one-to-many.gif"));
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionOTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionMTOModel = new DefaultGroupButtonModel();
            connectionMTOModel.setId("MTO_RELATION");
            connectionMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/many-to-one.gif"));
            connectionMTOModel.setTooltip("Many To One Relation");
            connectionMTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTOModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId("UMTO_RELATION");
            connectionUMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/uni.png"));
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionBMTOModel = new DefaultPaletteButtonModel();
            connectionBMTOModel.setId("BMTO_RELATION");
            connectionBMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/bi.png"));
            connectionBMTOModel.setTooltip("Bidirectional Many To One Relation");
            connectionBMTOModel.setPaletteModel(contextPaletteModel);
            connectionBMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionBMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionBMTOModel);

            ContextPaletteButtonModel connectionPKUMTOModel = new DefaultPaletteButtonModel();
            connectionPKUMTOModel.setId("PKUMTO_RELATION");
            connectionPKUMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/pk-uni.png"));
            connectionPKUMTOModel.setTooltip("Unidirectional Many To One Primary Key Relation");
            connectionPKUMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKUMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionPKUMTOModel);

            ContextPaletteButtonModel connectionPKBMTOModel = new DefaultPaletteButtonModel();
            connectionPKBMTOModel.setId("PKBMTO_RELATION");
            connectionPKBMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/pk-bi.png"));
            connectionPKBMTOModel.setTooltip("Bidirectional Many To One Primary Key Relation");
            connectionPKBMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionPKBMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionPKBMTOModel.getId()));
            connectionMTOModel.getChildren().add(connectionPKBMTOModel);

            ContextPaletteButtonModel connectionMTMModel = new DefaultGroupButtonModel();
            connectionMTMModel.setId("MTM_RELATION");
            connectionMTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/many-to-many.gif"));
            connectionMTMModel.setTooltip("Many To Many Relation");
            connectionMTMModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTMModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId("UMTM_RELATION");
            connectionUMTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/uni.png"));
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTMModel.getId()));
            connectionMTMModel.getChildren().add(connectionUMTMModel);

            ContextPaletteButtonModel connectionBMTMModel = new DefaultPaletteButtonModel();
            connectionBMTMModel.setId("BMTM_RELATION");
            connectionBMTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/bi.png"));
            connectionBMTMModel.setTooltip("Bidirectional Many To Many Relation");
            connectionBMTMModel.setPaletteModel(contextPaletteModel);
            connectionBMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionBMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionBMTMModel.getId()));
            connectionMTMModel.getChildren().add(connectionBMTMModel);

        } else if (nodeWidget instanceof MappedSuperclassWidget || nodeWidget instanceof EmbeddableWidget) {

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId("UOTO_RELATION");
            connectionUOTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/one-to-one.gif"));
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUOTOModel.getId()));
            contextPaletteModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId("UOTM_RELATION");
            connectionOTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/one-to-many.gif"));
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionOTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId("UMTO_RELATION");
            connectionUMTOModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/many-to-one.gif"));
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTOModel.getId()));
            contextPaletteModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId("UMTM_RELATION");
            connectionUMTMModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/many-to-many.gif"));
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(ContextActionType.CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(nodeWidget.getModelerScene(), connectionUMTMModel.getId()));
            contextPaletteModel.getChildren().add(connectionUMTMModel);

        }

        ContextPaletteButtonModel deleteModel = new DefaultPaletteButtonModel();
        contextPaletteModel.getChildren().add(deleteModel);
        deleteModel.setImage(Utilities.loadImage("org/netbeans/jpa/modeler/resource/image/delete.png"));
        deleteModel.setTooltip("Delete");
        deleteModel.setPaletteModel(contextPaletteModel);
        deleteModel.setMouseListener(getRemoveWidgetAction(nodeWidget));
        return contextPaletteModel;
    }

    private static WidgetAction[] getConnectActions(IModelerScene scene, String connectionContextToolId) {
        WidgetAction[] retVal = new WidgetAction[0];
        SceneConnectProvider connector = new SceneConnectProvider(connectionContextToolId);
        LayerWidget layer = scene.getInterractionLayer();
        WidgetAction action = new ConnectAction(new ContextPaletteConnectDecorator(), layer, connector);
        retVal = new WidgetAction[]{action};
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
//                        entityWidget.hideInheritencePath();
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
//                        entityWidget.showInheritencePath();
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
