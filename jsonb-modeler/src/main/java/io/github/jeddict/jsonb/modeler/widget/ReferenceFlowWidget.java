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

import org.netbeans.api.visual.anchor.AnchorShape;
import io.github.jeddict.jsonb.modeler.initializer.JSONBModelerScene;
import io.github.jeddict.jpa.modeler.widget.flow.AbstractEdgeWidget;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.MultiRelationAttribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import org.netbeans.modeler.anchorshape.crow.OneAndOneCrowShape;
import org.netbeans.modeler.anchorshape.crow.OneOrMoreCrowShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class ReferenceFlowWidget extends AbstractEdgeWidget<JSONBModelerScene> {

    private BranchNodeWidget branchNodeWidget;
    private DocumentWidget referenceDocumentWidget;
    private static AnchorShape MULTI_VALUE;
    private static AnchorShape SINGLE_VALUE;

    public ReferenceFlowWidget(JSONBModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
    }

    @Override
    public void init() {
        MULTI_VALUE = new OneOrMoreCrowShape(8);
        SINGLE_VALUE = new OneAndOneCrowShape(5);
        branchNodeWidget.setAnchorGap(2);
        referenceDocumentWidget.setAnchorGap(2);
        if (branchNodeWidget.getBaseElementSpec().getAttribute() instanceof RelationAttribute) {
            RelationAttribute relationAttribute = (RelationAttribute) branchNodeWidget.getBaseElementSpec().getAttribute();
            if (relationAttribute instanceof MultiRelationAttribute) {
                setSourceAnchorShape(MULTI_VALUE);
            } else {
                setSourceAnchorShape(SINGLE_VALUE);
            }
        } else if (branchNodeWidget.getBaseElementSpec().getAttribute() instanceof ElementCollection) {
            setSourceAnchorShape(MULTI_VALUE);
        }
        setTargetAnchorShape(SINGLE_VALUE);
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    @Override
    public BranchNodeWidget getSourceWidget() {
        return branchNodeWidget;
    }

    @Override
    public DocumentWidget getTargetWidget() {
        return referenceDocumentWidget;
    }

    public BranchNodeWidget getBranchNodeWidget() {
        return branchNodeWidget;
    }

    public void setBranchNodeWidget(BranchNodeWidget branchNodeWidget) {
        this.branchNodeWidget = branchNodeWidget;
        if (branchNodeWidget != null) {
            branchNodeWidget.addReferenceFlowWidget(this);
        }
    }

    public DocumentWidget getReferenceDocumentWidget() {
        return referenceDocumentWidget;
    }

    public void setReferenceDocumentWidget(DocumentWidget referenceDocumentWidget) {
        this.referenceDocumentWidget = referenceDocumentWidget;
    }

    @Override
    public void destroy() {
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceAttributeWidget) {
        if (sourceAttributeWidget instanceof BranchNodeWidget) {
            BranchNodeWidget sourceBranchNodeWidget = (BranchNodeWidget) sourceAttributeWidget;
            ReferenceFlowWidget referenceFlowWidget = this;
            DocumentWidget targetDocumentWidget = (DocumentWidget) targetNodeWidget;
            referenceFlowWidget.setReferenceDocumentWidget(targetDocumentWidget);
            referenceFlowWidget.setBranchNodeWidget(sourceBranchNodeWidget);
            return sourceBranchNodeWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
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
