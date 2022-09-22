/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.widget.context;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.function.Function;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LayerWidget;
import static io.github.jeddict.jpa.modeler.Constant.*;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.ROOT;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.MappedSuperclassWidget;
import io.github.jeddict.jpa.modeler.widget.BeanClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.MultiValueEmbeddableFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.SingleValueEmbeddableFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BMTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BMTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BOTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UMTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UMTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UOTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UOTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BMTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BMTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BOTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UMTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UMTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UOTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UOTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.CREATE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.DELETE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.MTMR_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.MTOR_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.OTMR_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.OTOR_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PAINT_ICON;
import io.github.jeddict.jpa.modeler.specification.model.workspace.HighlightWidgetDialog;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import static org.netbeans.modeler.widget.context.ContextActionType.CONNECT;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.action.ConnectAction;
import org.netbeans.modeler.widget.context.action.ContextPaletteConnectDecorator;
import org.netbeans.modeler.widget.context.action.SceneConnectProvider;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultGroupButtonModel;
import org.netbeans.modeler.widget.context.base.DefaultPaletteButtonModel;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.GENERALIZATION_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.VERSION_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.TRANSIENT_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ID_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BASIC_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.COMPOSITION_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.UNI_DIRECTIONAL_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BI_DIRECTIONAL_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PK_UNI_DIRECTIONAL_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PK_BI_DIRECTIONAL_ICON;

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
                if (widget instanceof JavaClassWidget) {
                    JavaClassWidget classWidget = (JavaClassWidget) widget;
                    classWidget.createPinWidget(addAttributeModel.getId());
                    widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
                }
            }
        };
    }

    public static ContextPaletteModel getContextPaletteModel(INodeWidget nodeWidget) {
        JPAModelerScene scene = (JPAModelerScene)nodeWidget.getModelerScene();
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(nodeWidget);
        ContextPaletteButtonModel generalizationConnectionModel = new DefaultPaletteButtonModel();
        generalizationConnectionModel.setId("GENERALIZATION");
        generalizationConnectionModel.setImage(GENERALIZATION_ICON);
        generalizationConnectionModel.setTooltip("Generalization (Drag from Concrete to Abstract)");
        generalizationConnectionModel.setPaletteModel(contextPaletteModel);
        generalizationConnectionModel.setContextActionType(CONNECT);
        generalizationConnectionModel.setWidgetActions(getConnectActions(scene, generalizationConnectionModel.getId(),
                    e -> new GeneralizationFlowWidget(scene, e)));
        contextPaletteModel.getChildren().add(generalizationConnectionModel);

        ContextPaletteButtonModel addWrapperAttributeModel = getContextPaletteGroupButtonModel("Add attributes",
                CREATE_ICON.getImage(), contextPaletteModel);
        contextPaletteModel.getChildren().add(addWrapperAttributeModel);

        ContextPaletteButtonModel[] addAttributeSubModelList = null;

        if (nodeWidget instanceof PersistenceClassWidget) {
            ContextPaletteButtonModel addIdAttributeModel = getContextPaletteButtonModel(ID_ATTRIBUTE, "Id Attribute",
                    ID_ATTRIBUTE_ICON, contextPaletteModel);
            addIdAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addIdAttributeModel));
            ContextPaletteButtonModel addBasicAttributeModel = getContextPaletteButtonModel(BASIC_ATTRIBUTE, "Basic Attribute",
                    BASIC_ATTRIBUTE_ICON, contextPaletteModel);
            addBasicAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicAttributeModel));
            ContextPaletteButtonModel addBasicCollectionAttributeModel = getContextPaletteButtonModel(BASIC_COLLECTION_ATTRIBUTE, "Basic ElementCollection Attribute",
                    BASIC_COLLECTION_ATTRIBUTE_ICON, contextPaletteModel);
            addBasicCollectionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBasicCollectionAttributeModel));
            ContextPaletteButtonModel addTransientAttributeModel = getContextPaletteButtonModel(TRANSIENT_ATTRIBUTE, "Transient Attribute",
                    TRANSIENT_ATTRIBUTE_ICON, contextPaletteModel);
            addTransientAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addTransientAttributeModel));
            ContextPaletteButtonModel addVersionAttributeModel = getContextPaletteButtonModel(VERSION_ATTRIBUTE, "Version Attribute",
                    VERSION_ATTRIBUTE_ICON, contextPaletteModel);
            addVersionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addVersionAttributeModel));

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
            }
        } else if (nodeWidget instanceof BeanClassWidget) {
            ContextPaletteButtonModel addBeanAttributeModel = getContextPaletteButtonModel(BEAN_ATTRIBUTE, "Attribute",
                    BASIC_ATTRIBUTE_ICON, contextPaletteModel);
            addBeanAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBeanAttributeModel));
            ContextPaletteButtonModel addBeanCollectionAttributeModel = getContextPaletteButtonModel(BEAN_COLLECTION_ATTRIBUTE, "Collection Attribute",
                    BASIC_COLLECTION_ATTRIBUTE_ICON, contextPaletteModel);
            addBeanCollectionAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addBeanCollectionAttributeModel));
            ContextPaletteButtonModel addTransientAttributeModel = getContextPaletteButtonModel(TRANSIENT_ATTRIBUTE, "Transient Attribute",
                    TRANSIENT_ATTRIBUTE_ICON, contextPaletteModel);
            addTransientAttributeModel.setMouseListener(getAddWidgetAction(nodeWidget, addTransientAttributeModel));

            addAttributeSubModelList = new ContextPaletteButtonModel[]{addBeanAttributeModel, addBeanCollectionAttributeModel, addTransientAttributeModel};
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        for (ContextPaletteButtonModel addAttributeModel_TMP : addAttributeSubModelList) {
            addWrapperAttributeModel.getChildren().add(addAttributeModel_TMP);
        }

        ContextPaletteButtonModel compositionConnectionModel = getContextPaletteGroupButtonModel("Embedded (Drag to Embeddable)",
                COMPOSITION_ATTRIBUTE_ICON, contextPaletteModel);

        ContextPaletteButtonModel singleValueEmbeddedConnectionModal = getContextPaletteButtonModel(SINGLE_EMBEDDABLE_RELATION, "Single Value Embeddable Connection (Drag to Embeddable)",
                SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON, contextPaletteModel);
        singleValueEmbeddedConnectionModal.setContextActionType(CONNECT);
        singleValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(scene, singleValueEmbeddedConnectionModal.getId(),
                    e -> new SingleValueEmbeddableFlowWidget(scene, e)));
        compositionConnectionModel.getChildren().add(singleValueEmbeddedConnectionModal);

        ContextPaletteButtonModel collectionValueEmbeddedConnectionModal = getContextPaletteButtonModel(MULTI_EMBEDDABLE_RELATION, "Multi Value Embeddable Connection (Drag to Embeddable)",
                MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON, contextPaletteModel);
        collectionValueEmbeddedConnectionModal.setContextActionType(CONNECT);
        collectionValueEmbeddedConnectionModal.setWidgetActions(getConnectActions(scene, collectionValueEmbeddedConnectionModal.getId(),
                    e -> new MultiValueEmbeddableFlowWidget(scene, e)));
        compositionConnectionModel.getChildren().add(collectionValueEmbeddedConnectionModal);

        if (nodeWidget instanceof EntityWidget
                || nodeWidget instanceof MappedSuperclassWidget
                || nodeWidget instanceof EmbeddableWidget) {
            contextPaletteModel.getChildren().add(compositionConnectionModel);
        }

        if (nodeWidget instanceof EntityWidget) {

            ContextPaletteButtonModel connectionOTOModel = new DefaultGroupButtonModel();
            connectionOTOModel.setId(OTO_RELATION);
            connectionOTOModel.setImage(OTOR_ICON);
            connectionOTOModel.setTooltip("One To One Relation");
            connectionOTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionOTOModel);

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId(UOTO_RELATION);
            connectionUOTOModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(scene, connectionUOTOModel.getId(),
                    e -> new UOTORelationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionBOTOModel = new DefaultPaletteButtonModel();
            connectionBOTOModel.setId(BOTO_RELATION);
            connectionBOTOModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBOTOModel.setTooltip("Bidirectional One To One Relation");
            connectionBOTOModel.setPaletteModel(contextPaletteModel);
            connectionBOTOModel.setContextActionType(CONNECT);
            connectionBOTOModel.setWidgetActions(getConnectActions(scene, connectionBOTOModel.getId(),
                    e -> new BOTORelationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionBOTOModel);

            ContextPaletteButtonModel connectionPKUOTOModel = new DefaultPaletteButtonModel();
            connectionPKUOTOModel.setId(PKUOTO_RELATION);
            connectionPKUOTOModel.setImage(PK_UNI_DIRECTIONAL_ICON);
            connectionPKUOTOModel.setTooltip("Unidirectional One To One Primary Key Relation");
            connectionPKUOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUOTOModel.setContextActionType(CONNECT);
            connectionPKUOTOModel.setWidgetActions(getConnectActions(scene, connectionPKUOTOModel.getId(),
                    e -> new UOTORelationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionPKUOTOModel);

            ContextPaletteButtonModel connectionPKBOTOModel = new DefaultPaletteButtonModel();
            connectionPKBOTOModel.setId(PKBOTO_RELATION);
            connectionPKBOTOModel.setImage(PK_BI_DIRECTIONAL_ICON);
            connectionPKBOTOModel.setTooltip("Bidirectional One To One Primary Key Relation");
            connectionPKBOTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBOTOModel.setContextActionType(CONNECT);
            connectionPKBOTOModel.setWidgetActions(getConnectActions(scene, connectionPKBOTOModel.getId(),
                    e -> new BOTORelationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionPKBOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId(UOTM_RELATION);
            connectionOTMModel.setImage(OTMR_ICON);
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(scene, connectionOTMModel.getId(),
                    e -> new UOTMRelationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionMTOModel = new DefaultGroupButtonModel();
            connectionMTOModel.setId(MTO_RELATION);
            connectionMTOModel.setImage(MTOR_ICON);
            connectionMTOModel.setTooltip("Many To One Relation");
            connectionMTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTOModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId(UMTO_RELATION);
            connectionUMTOModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(scene, connectionUMTOModel.getId(),
                    e -> new UMTORelationFlowWidget(scene, e)));
            connectionMTOModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionBMTOModel = new DefaultPaletteButtonModel();
            connectionBMTOModel.setId(BMTO_RELATION);
            connectionBMTOModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBMTOModel.setTooltip("Bidirectional Many To One Relation");
            connectionBMTOModel.setPaletteModel(contextPaletteModel);
            connectionBMTOModel.setContextActionType(CONNECT);
            connectionBMTOModel.setWidgetActions(getConnectActions(scene, connectionBMTOModel.getId(),
                    e -> new BMTORelationFlowWidget(scene, e)));
            connectionMTOModel.getChildren().add(connectionBMTOModel);

            ContextPaletteButtonModel connectionPKUMTOModel = new DefaultPaletteButtonModel();
            connectionPKUMTOModel.setId(PKUMTO_RELATION);
            connectionPKUMTOModel.setImage(PK_UNI_DIRECTIONAL_ICON);
            connectionPKUMTOModel.setTooltip("Unidirectional Many To One Primary Key Relation");
            connectionPKUMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKUMTOModel.setContextActionType(CONNECT);
            connectionPKUMTOModel.setWidgetActions(getConnectActions(scene, connectionPKUMTOModel.getId(),
                    e -> new UMTORelationFlowWidget(scene, e)));
            connectionMTOModel.getChildren().add(connectionPKUMTOModel);

            ContextPaletteButtonModel connectionPKBMTOModel = new DefaultPaletteButtonModel();
            connectionPKBMTOModel.setId(PKBMTO_RELATION);
            connectionPKBMTOModel.setImage(PK_BI_DIRECTIONAL_ICON);
            connectionPKBMTOModel.setTooltip("Bidirectional Many To One Primary Key Relation");
            connectionPKBMTOModel.setPaletteModel(contextPaletteModel);
            connectionPKBMTOModel.setContextActionType(CONNECT);
            connectionPKBMTOModel.setWidgetActions(getConnectActions(scene, connectionPKBMTOModel.getId(),
                    e -> new BMTORelationFlowWidget(scene, e)));
            connectionMTOModel.getChildren().add(connectionPKBMTOModel);

            ContextPaletteButtonModel connectionMTMModel = new DefaultGroupButtonModel();
            connectionMTMModel.setId(MTM_RELATION);
            connectionMTMModel.setImage(MTMR_ICON);
            connectionMTMModel.setTooltip("Many To Many Relation");
            connectionMTMModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTMModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId(UMTM_RELATION);
            connectionUMTMModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(scene, connectionUMTMModel.getId(),
                    e -> new UMTMRelationFlowWidget(scene, e)));
            connectionMTMModel.getChildren().add(connectionUMTMModel);

            ContextPaletteButtonModel connectionBMTMModel = new DefaultPaletteButtonModel();
            connectionBMTMModel.setId(BMTM_RELATION);
            connectionBMTMModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBMTMModel.setTooltip("Bidirectional Many To Many Relation");
            connectionBMTMModel.setPaletteModel(contextPaletteModel);
            connectionBMTMModel.setContextActionType(CONNECT);
            connectionBMTMModel.setWidgetActions(getConnectActions(scene, connectionBMTMModel.getId(),
                    e -> new BMTMRelationFlowWidget(scene, e)));
            connectionMTMModel.getChildren().add(connectionBMTMModel);

        } else if (nodeWidget instanceof MappedSuperclassWidget || nodeWidget instanceof EmbeddableWidget) {

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId(UOTO_RELATION);
            connectionUOTOModel.setImage(OTOR_ICON);
            connectionUOTOModel.setTooltip("Unidirectional One To One Relation");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(scene, connectionUOTOModel.getId(),
                    e -> new UOTORelationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId(UOTM_RELATION);
            connectionOTMModel.setImage(OTMR_ICON);
            connectionOTMModel.setTooltip("Unidirectional One To Many Relation");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(scene, connectionOTMModel.getId(),
                    e -> new UOTMRelationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId(UMTO_RELATION);
            connectionUMTOModel.setImage(MTOR_ICON);
            connectionUMTOModel.setTooltip("Unidirectional Many To One Relation");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(scene, connectionUMTOModel.getId(),
                    e -> new UMTORelationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId(UMTM_RELATION);
            connectionUMTMModel.setImage(MTMR_ICON);
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Relation");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(scene, connectionUMTMModel.getId(),
                    e -> new UMTMRelationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionUMTMModel);

        } else if (nodeWidget instanceof BeanClassWidget) {

            ContextPaletteButtonModel connectionOTOModel = new DefaultGroupButtonModel();
            connectionOTOModel.setId(OTO_ASSOCIATION);
            connectionOTOModel.setImage(OTOR_ICON);
            connectionOTOModel.setTooltip("One To One Association");
            connectionOTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionOTOModel);

            ContextPaletteButtonModel connectionUOTOModel = new DefaultPaletteButtonModel();
            connectionUOTOModel.setId(UOTO_ASSOCIATION);
            connectionUOTOModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUOTOModel.setTooltip("Unidirectional One To One Association");
            connectionUOTOModel.setPaletteModel(contextPaletteModel);
            connectionUOTOModel.setContextActionType(CONNECT);
            connectionUOTOModel.setWidgetActions(getConnectActions(scene, connectionUOTOModel.getId(),
                    e -> new UOTOAssociationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionUOTOModel);

            ContextPaletteButtonModel connectionBOTOModel = new DefaultPaletteButtonModel();
            connectionBOTOModel.setId(BOTO_ASSOCIATION);
            connectionBOTOModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBOTOModel.setTooltip("Bidirectional One To One Association");
            connectionBOTOModel.setPaletteModel(contextPaletteModel);
            connectionBOTOModel.setContextActionType(CONNECT);
            connectionBOTOModel.setWidgetActions(getConnectActions(scene, connectionBOTOModel.getId(),
                    e -> new BOTOAssociationFlowWidget(scene, e)));
            connectionOTOModel.getChildren().add(connectionBOTOModel);

            ContextPaletteButtonModel connectionOTMModel = new DefaultPaletteButtonModel();
            connectionOTMModel.setId(UOTM_ASSOCIATION);
            connectionOTMModel.setImage(OTMR_ICON);
            connectionOTMModel.setTooltip("Unidirectional One To Many Association");
            connectionOTMModel.setPaletteModel(contextPaletteModel);
            connectionOTMModel.setContextActionType(CONNECT);
            connectionOTMModel.setWidgetActions(getConnectActions(scene, connectionOTMModel.getId(),
                    e -> new UOTMAssociationFlowWidget(scene, e)));
            contextPaletteModel.getChildren().add(connectionOTMModel);

            ContextPaletteButtonModel connectionMTOModel = new DefaultGroupButtonModel();
            connectionMTOModel.setId(MTO_ASSOCIATION);
            connectionMTOModel.setImage(MTOR_ICON);
            connectionMTOModel.setTooltip("Many To One Association");
            connectionMTOModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTOModel);

            ContextPaletteButtonModel connectionUMTOModel = new DefaultPaletteButtonModel();
            connectionUMTOModel.setId(UMTO_ASSOCIATION);
            connectionUMTOModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUMTOModel.setTooltip("Unidirectional Many To One Association");
            connectionUMTOModel.setPaletteModel(contextPaletteModel);
            connectionUMTOModel.setContextActionType(CONNECT);
            connectionUMTOModel.setWidgetActions(getConnectActions(scene, connectionUMTOModel.getId(),
                    edgeWidgetInfo -> new UMTOAssociationFlowWidget(scene, edgeWidgetInfo)));
            connectionMTOModel.getChildren().add(connectionUMTOModel);

            ContextPaletteButtonModel connectionBMTOModel = new DefaultPaletteButtonModel();
            connectionBMTOModel.setId(BMTO_ASSOCIATION);
            connectionBMTOModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBMTOModel.setTooltip("Bidirectional Many To One Association");
            connectionBMTOModel.setPaletteModel(contextPaletteModel);
            connectionBMTOModel.setContextActionType(CONNECT);
            connectionBMTOModel.setWidgetActions(getConnectActions(scene, connectionBMTOModel.getId(),
                    edgeWidgetInfo -> new BMTOAssociationFlowWidget(scene, edgeWidgetInfo)));
            connectionMTOModel.getChildren().add(connectionBMTOModel);

            ContextPaletteButtonModel connectionMTMModel = new DefaultGroupButtonModel();
            connectionMTMModel.setId(MTM_ASSOCIATION);
            connectionMTMModel.setImage(MTMR_ICON);
            connectionMTMModel.setTooltip("Many To Many Association");
            connectionMTMModel.setPaletteModel(contextPaletteModel);
            contextPaletteModel.getChildren().add(connectionMTMModel);

            ContextPaletteButtonModel connectionUMTMModel = new DefaultPaletteButtonModel();
            connectionUMTMModel.setId(UMTM_ASSOCIATION);
            connectionUMTMModel.setImage(UNI_DIRECTIONAL_ICON);
            connectionUMTMModel.setTooltip("Unidirectional Many To Many Association");
            connectionUMTMModel.setPaletteModel(contextPaletteModel);
            connectionUMTMModel.setContextActionType(CONNECT);
            connectionUMTMModel.setWidgetActions(getConnectActions(scene, connectionUMTMModel.getId(),
                    edgeWidgetInfo -> new UMTMAssociationFlowWidget(scene, edgeWidgetInfo)));
            connectionMTMModel.getChildren().add(connectionUMTMModel);

            ContextPaletteButtonModel connectionBMTMModel = new DefaultPaletteButtonModel();
            connectionBMTMModel.setId(BMTM_ASSOCIATION);
            connectionBMTMModel.setImage(BI_DIRECTIONAL_ICON);
            connectionBMTMModel.setTooltip("Bidirectional Many To Many Association");
            connectionBMTMModel.setPaletteModel(contextPaletteModel);
            connectionBMTMModel.setContextActionType(CONNECT);
            connectionBMTMModel.setWidgetActions(getConnectActions(scene, connectionBMTMModel.getId(),
                    edgeWidgetInfo -> new BMTMAssociationFlowWidget(scene, edgeWidgetInfo)));
            connectionMTMModel.getChildren().add(connectionBMTMModel);
        }

        ContextPaletteButtonModel highlightModel = new DefaultPaletteButtonModel();
        contextPaletteModel.getChildren().add(highlightModel);
        highlightModel.setImage(PAINT_ICON.getImage());
        highlightModel.setTooltip("Highlight");
        highlightModel.setPaletteModel(contextPaletteModel);
        highlightModel.setMouseListener(getHighlightWidgetAction(nodeWidget));

        ContextPaletteButtonModel deleteModel = new DefaultPaletteButtonModel();
        contextPaletteModel.getChildren().add(deleteModel);
        deleteModel.setImage(DELETE_ICON.getImage());
        deleteModel.setTooltip("Delete");
        deleteModel.setPaletteModel(contextPaletteModel);
        deleteModel.setMouseListener(getRemoveWidgetAction(nodeWidget));

        return contextPaletteModel;
    }
    
    private static WidgetAction[] getConnectActions(
            IModelerScene scene, 
            String connectionContextToolId,
            Function<EdgeWidgetInfo, IEdgeWidget> edgeWidgetFunction) {
        SceneConnectProvider connector = new SceneConnectProvider(connectionContextToolId, edgeWidgetFunction);
        LayerWidget layer = scene.getInterractionLayer();
        WidgetAction action = new ConnectAction(new ContextPaletteConnectDecorator(), layer, connector);
        WidgetAction[] retVal = new WidgetAction[]{action};
        return retVal;
    }

    private static MouseListener getHighlightWidgetAction(final INodeWidget widget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NBModelerUtil.hideContextPalette(widget.getModelerScene());
                HighlightWidgetDialog widgetDialog = new HighlightWidgetDialog(widget, widget.getTextDesign());
                widgetDialog.setVisible(true);
            }
        };
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

}
