/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget.attribute.base;

import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.scene.vmd.PModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class EmbeddedAttributeWidget extends BaseAttributeWidget {

    private EmbeddableFlowWidget embeddableFlowWidget;

    public EmbeddedAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setIcon(JPAModelerUtil.EMBEDDED_ATTRIBUTE);
    }

    public static PinWidgetInfo create(String id, String name) {
        PinWidgetInfo pinWidgetInfo = AttributeWidget.create(id, name);
        pinWidgetInfo.setDocumentId(EmbeddedAttributeWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    /**
     * @return the embeddableFlowWidget
     */
    public EmbeddableFlowWidget getEmbeddableFlowWidget() {
        return embeddableFlowWidget;
    }

    /**
     * @param embeddableFlowWidget the embeddableFlowWidget to set
     */
    public void setEmbeddableFlowWidget(EmbeddableFlowWidget embeddableFlowWidget) {
        this.embeddableFlowWidget = embeddableFlowWidget;
    }

    public void setConnectedSibling(EmbeddableWidget embeddableWidget) {
        CompositionAttribute compositionAttribute = (CompositionAttribute) this.getBaseElementSpec();
        compositionAttribute.setConnectedClassId(embeddableWidget.getId());
    }

    public void showCompositionPath() {
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
        if (this.getEmbeddableFlowWidget() != null) {
            this.getEmbeddableFlowWidget().setHighlightStatus(true);
            colorScheme.highlightUI(this.getEmbeddableFlowWidget());
            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().showCompositionPath();
        }
    }

    public void hideCompositionPath() {
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
        if (this.getEmbeddableFlowWidget() != null) {
            this.getEmbeddableFlowWidget().setHighlightStatus(false);
            colorScheme.updateUI(this.getEmbeddableFlowWidget(), this.getEmbeddableFlowWidget().getState(), this.getEmbeddableFlowWidget().getState());
            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().hideCompositionPath();
        }
    }

}
