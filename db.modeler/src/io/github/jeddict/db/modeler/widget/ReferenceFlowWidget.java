/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.db.modeler.widget;

import io.github.jeddict.db.modeler.spec.DBForeignKey;
import io.github.jeddict.db.modeler.initializer.DBModelerScene;
import static java.awt.Color.BLACK;
import org.netbeans.api.visual.anchor.AnchorShape;
import io.github.jeddict.jpa.modeler.widget.flow.AbstractEdgeWidget;
import org.netbeans.modeler.anchorshape.DiamondAnchorShape;
import org.netbeans.modeler.anchorshape.crow.OneAndOneCrowShape;
import org.netbeans.modeler.anchorshape.crow.OneOrMoreCrowShape;
import org.netbeans.modeler.anchorshape.crow.ZeroOrMoreCrowShape;
import org.netbeans.modeler.anchorshape.crow.ZeroOrOneCrowShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class ReferenceFlowWidget extends AbstractEdgeWidget<DBModelerScene> {

    private ForeignKeyWidget foreignKeyWidget;
    private ColumnWidget referenceColumnWidget;
    private static AnchorShape ZERO_MORE;
    private static AnchorShape ZERO_ONE;
    private static AnchorShape ONE_MORE;
    private static AnchorShape ONE_ONE;

    public ReferenceFlowWidget(DBModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
    }

    @Override
    public void init() {
        ZERO_MORE = new ZeroOrMoreCrowShape(8);
        ONE_MORE = new OneOrMoreCrowShape(8);

        ZERO_ONE = new ZeroOrOneCrowShape(4);
        ONE_ONE = new OneAndOneCrowShape(5);

        foreignKeyWidget.setAnchorGap(2);
        referenceColumnWidget.setAnchorGap(2);

        setTargetAnchorShape(ONE_ONE);
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    @Override
    public ForeignKeyWidget getSourceWidget() {
        return foreignKeyWidget;
    }

    @Override
    public ColumnWidget getTargetWidget() {
        return referenceColumnWidget;
    }

    public ForeignKeyWidget getForeignKeyWidget() {
        return foreignKeyWidget;
    }

    public void setForeignKeyWidget(ForeignKeyWidget columnWidget) {
        this.foreignKeyWidget = columnWidget;
//        if (columnWidget != null) {
//            columnWidget.addReferenceFlowWidget(this);
//        }
    }

    public ColumnWidget getReferenceColumnWidget() {
        return referenceColumnWidget;
    }

    public void setReferenceColumnWidget(ColumnWidget referenceColumnWidget) {
        this.referenceColumnWidget = referenceColumnWidget;
//        if (referenceColumnWidget != null) {
//            referenceColumnWidget.addReferenceFlowWidget(this);
//        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceColumnWidget) {
        if (sourceNodeWidget instanceof TableWidget
                && targetNodeWidget instanceof TableWidget
                && sourceColumnWidget instanceof ForeignKeyWidget) {
            ReferenceFlowWidget referenceFlowWidget = this;
            TableWidget targetTableWidget = (TableWidget) targetNodeWidget;
            DBForeignKey sourceColumn = (DBForeignKey) sourceColumnWidget.getPinWidgetInfo().getBaseElementSpec();
            ColumnWidget targetColumnWidget = targetTableWidget.findColumnWidget(sourceColumn.getReferenceColumn().getId());
            referenceFlowWidget.setReferenceColumnWidget(targetColumnWidget);
            referenceFlowWidget.setForeignKeyWidget((ForeignKeyWidget) sourceColumnWidget);
            return sourceColumnWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getTargetPinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget targetColumnWidget) {
        if (sourceNodeWidget instanceof TableWidget
                && targetNodeWidget instanceof TableWidget
                && targetColumnWidget instanceof ColumnWidget) {
            return targetColumnWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
