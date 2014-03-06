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
package org.netbeans.jpa.modeler.specification.model.engine;

import java.awt.Point;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.engine.ModelerDiagramEngine;
import static org.netbeans.modeler.core.engine.ModelerDiagramEngine.alignStrategyProvider;
import org.netbeans.modeler.provider.NodeWidgetSelectProvider;
import org.netbeans.modeler.provider.node.move.MoveAction;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.tool.DesignerTools;
import org.netbeans.modeler.widget.node.INodeWidget;

public class JPADiagramEngine extends ModelerDiagramEngine {

    public JPADiagramEngine() {
    }
    private static final MoveProvider MOVE_PROVIDER_DEFAULT = new MoveProvider() {
        private boolean locationChanged = false;
        private Point original;

        public void movementStarted(Widget widget) {
            INodeWidget nodeWidget = (INodeWidget) widget;
            NBModelerUtil.hideContextPalette(nodeWidget.getModelerScene());
            locationChanged = false;
        }

        public void movementFinished(Widget widget) {
            INodeWidget nodeWidget = (INodeWidget) widget;
            NBModelerUtil.showContextPalette(nodeWidget.getModelerScene(), nodeWidget);
            if (locationChanged) {
                ((IModelerScene) widget.getScene()).getModelerPanelTopComponent().changePersistenceState(false);
            }
            locationChanged = false;
        }

        public Point getOriginalLocation(Widget widget) {
            original = widget.getPreferredLocation();
            return original;
        }

        public void setNewLocation(Widget widget, Point location) {
            widget.setPreferredLocation(location);
            if (original != null) {
                locationChanged = true;
            }
        }
    };

    @Override
    public void setNodeWidgetAction(final INodeWidget nodeWidget) {
//        IModelerScene modelerScene = nodeWidget.getModelerScene();
//        WidgetAction doubleClickAction = new DoubleClickAction(new DoubleClickProvider() {
//            @Override
//            public void onDoubleClick(Widget widget, Point point, boolean bln) {
//                nodeWidget.showProperties();
//                nodeWidget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//            }
//        });
        WidgetAction selectAction = ActionFactory.createSelectAction(new NodeWidgetSelectProvider(nodeWidget.getModelerScene()));
//        WidgetAction moveAction = new MoveAction(nodeWidget,
//                null, new MultiMoveProvider(nodeWidget.getModelerScene()),
//                alignStrategyProvider, alignStrategyProvider);
        WidgetAction moveAction = new MoveAction(nodeWidget,
                null, MOVE_PROVIDER_DEFAULT,
                alignStrategyProvider, alignStrategyProvider);
        WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction(nodeWidget.getPopupMenuProvider());
//        NodeWidgetResizeProvider nodeWidgetResizeProvider = new NodeWidgetResizeProvider();
//        WidgetAction resizeAction = ActionFactory.createResizeAction(nodeWidgetResizeProvider, nodeWidgetResizeProvider);
        WidgetAction snapMoveAction = ActionFactory.createMoveAction(ActionFactory.createSnapToGridMoveStrategy(5, 5), null);

        WidgetAction.Chain selectActionTool = nodeWidget.createActions(DesignerTools.SELECT);
//        selectActionTool.addAction(doubleClickAction);
        selectActionTool.addAction(selectAction);
        selectActionTool.addAction(moveAction);
//        selectActionTool.addAction(ActionFactory.createMoveAction());//BUG : above move interrupt gui view
        selectActionTool.addAction(getScene().createWidgetHoverAction());
        selectActionTool.addAction(popupMenuAction);
//        selectActionTool.addAction(resizeAction);
        selectActionTool.addAction(snapMoveAction);

    }
}
