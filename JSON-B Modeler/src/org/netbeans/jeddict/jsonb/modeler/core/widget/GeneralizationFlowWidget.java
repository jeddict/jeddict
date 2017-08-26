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
package org.netbeans.jeddict.jsonb.modeler.core.widget;

import org.netbeans.jeddict.jsonb.modeler.specification.model.scene.JSONBModelerScene;
import org.netbeans.jpa.modeler.core.widget.flow.AbstractEdgeWidget;
import static org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget.GENERALIZATION_ANCHOR_SHAPE;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class GeneralizationFlowWidget extends AbstractEdgeWidget<JSONBModelerScene> {

    private DocumentWidget superclassWidget;
    private DocumentWidget subclassWidget;

    public GeneralizationFlowWidget(JSONBModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setTargetAnchorShape(GENERALIZATION_ANCHOR_SHAPE);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        this.setSubclassWidget(null);
        this.setSuperclassWidget(null);
    }

    @Override
    public IFlowNodeWidget getSourceWidget() {
        return getSubclassWidget();
    }

    @Override
    public IFlowNodeWidget getTargetWidget() {
        return getSuperclassWidget();
    }

    /**
     * @return the superclassWidget
     */
    public DocumentWidget getSuperclassWidget() {
        return superclassWidget;
    }

    /**
     * @param superclassWidget the superclassWidget to set
     */
    public void setSuperclassWidget(DocumentWidget superclassWidget) {
        this.superclassWidget = superclassWidget;
        if (superclassWidget != null) {
            superclassWidget.addIncomingGeneralizationFlowWidget(this);
        }
    }

    /**
     * @return the subclassWidget
     */
    public DocumentWidget getSubclassWidget() {
        return subclassWidget;
    }

    /**
     * @param subclassWidget the subclassWidget to set
     */
    public void setSubclassWidget(DocumentWidget subclassWidget) {
        this.subclassWidget = subclassWidget;
        if (subclassWidget != null) {
            subclassWidget.setOutgoingGeneralizationFlowWidget(this);
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

}
