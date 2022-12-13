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
package io.github.jeddict.jsonb.modeler.widget;

import io.github.jeddict.jsonb.modeler.initializer.JSONBModelerScene;
import io.github.jeddict.jpa.modeler.widget.flow.AbstractEdgeWidget;
import static io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget.GENERALIZATION_ANCHOR_SHAPE;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

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

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceAttributeWidget) {
        DocumentWidget sourceDocumentWidget = (DocumentWidget) sourceNodeWidget;
        DocumentWidget targetDocumentWidget = (DocumentWidget) targetNodeWidget;
        GeneralizationFlowWidget generalizationFlowWidget = this;
        generalizationFlowWidget.setSubclassWidget(sourceDocumentWidget);
        generalizationFlowWidget.setSuperclassWidget(targetDocumentWidget);
        return sourceDocumentWidget.getInternalPinWidgetInfo();
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getTargetPinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget targetPinWidget) {
         DocumentWidget documentWidget = (DocumentWidget) targetNodeWidget;
        return documentWidget.getInternalPinWidgetInfo();
    }

}
