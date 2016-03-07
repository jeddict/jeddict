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

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class SingleValueEmbeddedAttributeWidget extends EmbeddedAttributeWidget<Embedded> {

    private EmbeddableFlowWidget embeddableFlowWidget;

    public SingleValueEmbeddedAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(JPAModelerUtil.SINGLE_VALUE_EMBEDDED_ATTRIBUTE);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = AttributeWidget.create(id, name, baseElement);
        pinWidgetInfo.setDocumentId(SingleValueEmbeddedAttributeWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    /**
     * @return the embeddableFlowWidget
     */
    @Override
    public EmbeddableFlowWidget getEmbeddableFlowWidget() {
        return embeddableFlowWidget;
    }

    /**
     * @param embeddableFlowWidget the embeddableFlowWidget to set
     */
    @Override
    public void setEmbeddableFlowWidget(EmbeddableFlowWidget embeddableFlowWidget) {
        this.embeddableFlowWidget = embeddableFlowWidget;
    }

}
