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
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.scene.ModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultGroupButtonModel;
import org.netbeans.modeler.widget.context.base.DefaultPaletteButtonModel;
import org.netbeans.modeler.widget.node.IWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

public class PinContextModel {

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

    private static MouseListener getAddWidgetAction(final IPinWidget widget, final ContextPaletteButtonModel addAttributeModel) {
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

    public static ContextPaletteModel getContextPaletteModel(IPinWidget pinWidget) {
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(pinWidget);

//        ContextPaletteButtonModel setFocusModel = new DefaultPaletteButtonModel();
//        contextPaletteModel.getChildren().add(setFocusModel);
//        setFocusModel.setImage(Utilities.loadImage("org/netbeans/jpa/modeler/resource/image/attribute-association-override.png"));
//        setFocusModel.setTooltip("Attribute/Association Override");
//        setFocusModel.setPaletteModel(contextPaletteModel);
//        setFocusModel.setMouseListener(getFocusWidgetAction(pinWidget));
        ContextPaletteButtonModel deleteModel = new DefaultPaletteButtonModel();
        contextPaletteModel.getChildren().add(deleteModel);
        deleteModel.setImage(Utilities.loadImage("org/netbeans/jpa/modeler/resource/image/delete.png"));
        deleteModel.setTooltip("Delete");
        deleteModel.setPaletteModel(contextPaletteModel);
        deleteModel.setMouseListener(getRemoveWidgetAction(pinWidget));
        return contextPaletteModel;
    }

    private static MouseListener getRemoveWidgetAction(final IPinWidget widget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                widget.remove(true);
                NBModelerUtil.hideContextPalette(widget.getModelerScene());
                widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
            }
        };
    }

    public static MouseListener getFocusWidgetAction(final IWidget widget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JPAModelerScene modelerScene = (JPAModelerScene) widget.getScene();

                if (widget instanceof JavaClassWidget) {
                    JavaClassWidget classWidget = (JavaClassWidget) widget;
                    if (modelerScene.getHighlightedWidget() == classWidget) {
                        classWidget.hideInheritencePath();
                        modelerScene.setHighlightedWidget(null);
                    } else {
                        PinContextModel.manageHightlight(modelerScene, widget);
                        classWidget.showInheritencePath();
                    }
                } else if (widget instanceof SingleValueEmbeddedAttributeWidget) {
                    SingleValueEmbeddedAttributeWidget embeddedAttributeWidget = (SingleValueEmbeddedAttributeWidget) widget;
                    if (modelerScene.getHighlightedWidget() == embeddedAttributeWidget) {
                        embeddedAttributeWidget.hideCompositionPath();
                        modelerScene.setHighlightedWidget(null);
                    } else {
                        PinContextModel.manageHightlight(modelerScene, widget);
                        embeddedAttributeWidget.showCompositionPath();
                    }

                }
                NBModelerUtil.hideContextPalette((IModelerScene) widget.getScene());
            }
        };
    }

    private static void manageHightlight(JPAModelerScene modelerScene, IWidget widget) {
        if (modelerScene.getHighlightedWidget() != null) {
            if (modelerScene.getHighlightedWidget() instanceof JavaClassWidget) {
                ((JavaClassWidget) modelerScene.getHighlightedWidget()).hideInheritencePath();
            } else if (modelerScene.getHighlightedWidget() instanceof EmbeddedAttributeWidget) {
                ((EmbeddedAttributeWidget) modelerScene.getHighlightedWidget()).hideCompositionPath();
            }
        }
        modelerScene.setHighlightedWidget(widget);
    }

}
