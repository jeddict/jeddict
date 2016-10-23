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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
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
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.IAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
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

    public static ContextPaletteModel getContextPaletteModel(IPinWidget pinWidget) {
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(pinWidget);
        addMoveModel(contextPaletteModel, pinWidget);
        addDeleteModel(contextPaletteModel, pinWidget);
        return contextPaletteModel;
    }

    private static void addDeleteModel(ContextPaletteModel contextPaletteModel, IPinWidget pinWidget) {
        ContextPaletteButtonModel deleteModel = new DefaultPaletteButtonModel();
        deleteModel.setImage(Utilities.loadImage("org/netbeans/jpa/modeler/resource/image/delete.png"));
        deleteModel.setTooltip("Delete");
        deleteModel.setPaletteModel(contextPaletteModel);
        deleteModel.setMouseListener(getRemoveWidgetAction(pinWidget));
        contextPaletteModel.getChildren().add(deleteModel);
    }

    private static void addMoveModel(ContextPaletteModel contextPaletteModel, IPinWidget pinWidget) {
        ContextPaletteButtonModel upModel = new DefaultPaletteButtonModel();
        upModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/context/anchor_up.png"));
        upModel.setTooltip("Move Up");
        upModel.setPaletteModel(contextPaletteModel);
        upModel.setMouseListener(getMoveUpWidgetAction(pinWidget, -1));
        upModel.checkVisibility(() -> checkMoveWidgetVisibility(pinWidget, -1));
        contextPaletteModel.getChildren().add(upModel);

        ContextPaletteButtonModel downModel = new DefaultPaletteButtonModel();
        downModel.setImage(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/context/anchor_down.png"));
        downModel.setTooltip("Move Down");
        downModel.setPaletteModel(contextPaletteModel);
        downModel.setMouseListener(getMoveUpWidgetAction(pinWidget, 1));
        downModel.checkVisibility(() -> checkMoveWidgetVisibility(pinWidget, 1));
        contextPaletteModel.getChildren().add(downModel);
    }

    private static MouseListener getMoveUpWidgetAction(final IPinWidget widget, final int distance) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                if (widget instanceof AttributeWidget) {
                    PersistenceClassWidget<ManagedClass> classWidget = ((AttributeWidget) widget).getClassWidget();
                    IAttributes attributes = classWidget.getBaseElementSpec().getAttributes();
                    List list = null;
                    List specList = null;
                    AttributeWidget attributeWidget = (AttributeWidget) widget;

                    if (attributeWidget instanceof IdAttributeWidget) {
                        list = classWidget.getIdAttributeWidgets();
                        specList = ((IPersistenceAttributes) attributes).getId();
                    } else if (attributeWidget instanceof EmbeddedAttributeWidget) {
                        if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
                            list = classWidget.getSingleValueEmbeddedAttributeWidgets();
                            specList = attributes.getEmbedded();
                        } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                            list = classWidget.getMultiValueEmbeddedAttributeWidgets();
                            specList = attributes.getElementCollection();
                        }
                    } else if (attributeWidget instanceof BasicAttributeWidget) {
                        list = classWidget.getBasicAttributeWidgets();
                        specList = attributes.getBasic();
                    } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
                        list = classWidget.getBasicCollectionAttributeWidgets();
                        specList = attributes.getElementCollection();
                    } else if (attributeWidget instanceof RelationAttributeWidget) {
                        if (attributeWidget instanceof OTORelationAttributeWidget) {
                            list = classWidget.getOneToOneRelationAttributeWidgets();
                            specList = attributes.getOneToOne();
                        } else if (attributeWidget instanceof OTMRelationAttributeWidget) {
                            list = classWidget.getOneToManyRelationAttributeWidgets();
                            specList = attributes.getOneToMany();
                        } else if (attributeWidget instanceof MTORelationAttributeWidget) {
                            list = classWidget.getManyToOneRelationAttributeWidgets();
                            specList = attributes.getManyToOne();
                        } else if (attributeWidget instanceof MTMRelationAttributeWidget) {
                            list = classWidget.getManyToManyRelationAttributeWidgets();
                            specList = attributes.getManyToMany();
                        }
                    } else if (attributeWidget instanceof VersionAttributeWidget) {
                        list = classWidget.getVersionAttributeWidgets();
                        specList = ((IPersistenceAttributes) attributes).getVersion();
                    } else if (attributeWidget instanceof TransientAttributeWidget) {
                        list = classWidget.getTransientAttributeWidgets();
                        specList = attributes.getTransient();
                    }
                    int index = list.indexOf(attributeWidget);
                    if ((index == 0 && distance < 0) || (list.size() == index + 1 && distance > 0)) {
                        return;
                    }
                    
                    if ((index == 1 && distance < 0) || (list.size() == index + 2 && distance > 0)) {  //if just before the last/first then hide context palette
                        NBModelerUtil.hideContextPalette(widget.getModelerScene());
                    }
                    Collections.swap(list, index, index + distance);

                    int specIndex = specList.indexOf(attributeWidget.getBaseElementSpec());
                    Collections.swap(specList, specIndex, specIndex + distance);

                    classWidget.sortAttributes();

                }

//                widget.remove(true);
//                NBModelerUtil.hideContextPalette(widget.getModelerScene());
                widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
            }
        };
    }

    private static boolean checkMoveWidgetVisibility(final IPinWidget widget, final int distance) {
        if (widget instanceof AttributeWidget) {
            PersistenceClassWidget<ManagedClass> classWidget = ((AttributeWidget) widget).getClassWidget();
            List list = null;
            AttributeWidget attributeWidget = (AttributeWidget) widget;

            if (attributeWidget instanceof IdAttributeWidget) {
                list = classWidget.getIdAttributeWidgets();
            } else if (attributeWidget instanceof EmbeddedAttributeWidget) {
                if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
                    list = classWidget.getSingleValueEmbeddedAttributeWidgets();
                } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                    list = classWidget.getMultiValueEmbeddedAttributeWidgets();
                }
            } else if (attributeWidget instanceof BasicAttributeWidget) {
                list = classWidget.getBasicAttributeWidgets();
            } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
                list = classWidget.getBasicCollectionAttributeWidgets();
            } else if (attributeWidget instanceof RelationAttributeWidget) {
                if (attributeWidget instanceof OTORelationAttributeWidget) {
                    list = classWidget.getOneToOneRelationAttributeWidgets();
                } else if (attributeWidget instanceof OTMRelationAttributeWidget) {
                    list = classWidget.getOneToManyRelationAttributeWidgets();
                } else if (attributeWidget instanceof MTORelationAttributeWidget) {
                    list = classWidget.getManyToOneRelationAttributeWidgets();
                } else if (attributeWidget instanceof MTMRelationAttributeWidget) {
                    list = classWidget.getManyToManyRelationAttributeWidgets();
                }
            } else if (attributeWidget instanceof VersionAttributeWidget) {
                list = classWidget.getVersionAttributeWidgets();
            } else if (attributeWidget instanceof TransientAttributeWidget) {
                list = classWidget.getTransientAttributeWidgets();
            }

            if (list == null) {
                return false;
            }
            int index = list.indexOf(attributeWidget);
            if ((index == 0 && distance < 0) || (list.size() == index + 1 && distance > 0)) {
                return false;
            }
        }

        return true;

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
                        classWidget.hideInheritancePath();
                        modelerScene.setHighlightedWidget(null);
                    } else {
                        PinContextModel.manageHightlight(modelerScene, widget);
                        classWidget.showInheritancePath();
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
                ((JavaClassWidget) modelerScene.getHighlightedWidget()).hideInheritancePath();
            } else if (modelerScene.getHighlightedWidget() instanceof EmbeddedAttributeWidget) {
                ((EmbeddedAttributeWidget) modelerScene.getHighlightedWidget()).hideCompositionPath();
            }
        }
        modelerScene.setHighlightedWidget(widget);
    }

}
