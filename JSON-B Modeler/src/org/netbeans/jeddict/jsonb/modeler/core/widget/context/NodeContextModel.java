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
package org.netbeans.jeddict.jsonb.modeler.core.widget.context;

import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.DELETE_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.DOWN_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.PAINT_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.UP_ICON;
import org.netbeans.jpa.modeler.specification.model.workspace.HighlightWidgetDialog;
import org.netbeans.jeddict.jsonb.modeler.core.widget.DocumentWidget;
import org.netbeans.jeddict.jsonb.modeler.core.widget.JSONNodeWidget;
import org.netbeans.jeddict.jsonb.modeler.spec.JSONBNode;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultPaletteButtonModel;
import org.netbeans.modeler.widget.pin.IPinWidget;

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

    public static ContextPaletteModel getContextPaletteModel(IPinWidget pinWidget) {
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(pinWidget);
        addMoveModel(contextPaletteModel, pinWidget);
        addHighlightWdgetModel(contextPaletteModel, pinWidget);
        return contextPaletteModel;
    }

    private static void addHighlightWdgetModel(ContextPaletteModel contextPaletteModel, IPinWidget pinWidget) {
        ContextPaletteButtonModel highlightModel = new DefaultPaletteButtonModel();
        highlightModel.setImage(PAINT_ICON.getImage());
        highlightModel.setTooltip("Highlight");
        highlightModel.setPaletteModel(contextPaletteModel);
        highlightModel.setMouseListener(getHighlightWidgetAction(pinWidget));
        contextPaletteModel.getChildren().add(highlightModel);
    }    
    
    private static void addMoveModel(ContextPaletteModel contextPaletteModel, IPinWidget pinWidget) {
        ContextPaletteButtonModel upModel = new DefaultPaletteButtonModel();
        upModel.setImage(UP_ICON);
        upModel.setTooltip("Move Up");
        upModel.setPaletteModel(contextPaletteModel);
        upModel.setMouseListener(getMoveWidgetAction(pinWidget, -1));
        upModel.checkVisibility(() -> checkMoveWidgetVisibility(pinWidget, -1));
        contextPaletteModel.getChildren().add(upModel);

        ContextPaletteButtonModel downModel = new DefaultPaletteButtonModel();
        downModel.setImage(DOWN_ICON);
        downModel.setTooltip("Move Down");
        downModel.setPaletteModel(contextPaletteModel);
        downModel.setMouseListener(getMoveWidgetAction(pinWidget, 1));
        downModel.checkVisibility(() -> checkMoveWidgetVisibility(pinWidget, 1));
        contextPaletteModel.getChildren().add(downModel);
    }

    private static MouseListener getHighlightWidgetAction(final IPinWidget widget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NBModelerUtil.hideContextPalette(widget.getModelerScene());
                HighlightWidgetDialog widgetDialog = new HighlightWidgetDialog(widget, widget.getTextDesign());
                widgetDialog.setVisible(true);
            }
        };
    }
    private static MouseListener getMoveWidgetAction(final IPinWidget widget, final int distance) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (widget instanceof JSONNodeWidget) {
                    JSONNodeWidget nodeWidget = (JSONNodeWidget) widget;
                    DocumentWidget documentWidget = nodeWidget.getDocumentWidget();
                    List<JSONBNode> list = documentWidget.getBaseElementSpec().getNodes();
                  
                    int index = list.indexOf(nodeWidget.getBaseElementSpec());
                    if ((index == 0 && distance < 0) || (list.size() == index + 1 && distance > 0)) {
                        return;
                    }
                    if ((index == 1 && distance < 0) || (list.size() == index + 2 && distance > 0)) {  //if just before the last/first then hide context palette
                        NBModelerUtil.hideContextPalette(widget.getModelerScene());
                    }
                    Collections.swap(list, index, index + distance);
                    
                    documentWidget.getBaseElementSpec()
                            .getJavaClass().setJsonbPropertyOrder(
                                    list.stream()
                                            .map(JSONBNode::getAttribute)
                                            .collect(toList())
                            );
                    documentWidget.sortNodes();
                    
                }
                widget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
            }
        };
    }

    private static boolean checkMoveWidgetVisibility(final IPinWidget widget, final int distance) {
        if (widget instanceof JSONNodeWidget) {
            JSONNodeWidget<JSONBNode> nodeWidget = (JSONNodeWidget<JSONBNode>) widget;
            DocumentWidget documentWidget = nodeWidget.getDocumentWidget();
            List<JSONBNode> list = documentWidget.getBaseElementSpec().getNodes();
            int index = list.indexOf(nodeWidget.getBaseElementSpec());
            if ((index == 0 && distance < 0) || (list.size() == index + 1 && distance > 0)) {
                return false;
            }
        }
        return true;
    }

}
