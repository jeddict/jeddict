/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.widget.context;

import java.awt.event.MouseListener;
import io.github.jeddict.jsonb.modeler.widget.DocumentWidget;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PAINT_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.RESET_ICON;
import io.github.jeddict.jpa.modeler.specification.model.workspace.HighlightWidgetDialog;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.widget.context.ContextPaletteButtonModel;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultContextPaletteModel;
import org.netbeans.modeler.widget.context.base.DefaultPaletteButtonModel;
import org.netbeans.modeler.widget.node.INodeWidget;

public class DocumentContextModel {

    public static ContextPaletteModel getContextPaletteModel(INodeWidget nodeWidget) {
        ContextPaletteModel contextPaletteModel = new DefaultContextPaletteModel(nodeWidget);
        if (nodeWidget instanceof DocumentWidget) {
            DocumentWidget documentWidget = (DocumentWidget) nodeWidget;

            ContextPaletteButtonModel highlightModel = new DefaultPaletteButtonModel();
            contextPaletteModel.getChildren().add(highlightModel);
            highlightModel.setImage(PAINT_ICON.getImage());
            highlightModel.setTooltip("Highlight");
            highlightModel.setPaletteModel(contextPaletteModel);
            highlightModel.setMouseListener(getHighlightWidgetAction(documentWidget));

            ContextPaletteButtonModel resetModel = new DefaultPaletteButtonModel();
            contextPaletteModel.getChildren().add(resetModel);
            resetModel.setImage(RESET_ICON.getImage());
            resetModel.setTooltip("Reset Property Order");
            resetModel.setPaletteModel(contextPaletteModel);
            resetModel.checkVisibility(() -> !documentWidget.getBaseElementSpec().getJavaClass().getJsonbPropertyOrder().isEmpty());
            resetModel.setMouseListener(getResetPropertyOrderAction(documentWidget));
        }
        return contextPaletteModel;
    }

    private static MouseListener getHighlightWidgetAction(final DocumentWidget documentWidget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NBModelerUtil.hideContextPalette(documentWidget.getModelerScene());
                HighlightWidgetDialog widgetDialog = new HighlightWidgetDialog(documentWidget, documentWidget.getTextDesign());
                widgetDialog.setVisible(true);
            }
        };
    }

    private static MouseListener getResetPropertyOrderAction(final DocumentWidget documentWidget) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                documentWidget.getBaseElementSpec().getJavaClass().setJsonbPropertyOrder(null);
                documentWidget.sortNodes();
                NBModelerUtil.hideContextPalette(documentWidget.getModelerScene());
                documentWidget.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);

            }
        };
    }

}
