/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.core.widget.flow;

import java.awt.Color;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.core.widget.column.IReferenceColumnWidget;
import org.netbeans.db.modeler.core.widget.column.IPrimaryKeyWidget;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.core.widget.flow.AbstractEdgeWidget;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.anchorshape.crow.OneAndOneCrowShape;
import org.netbeans.modeler.anchorshape.crow.OneOrMoreCrowShape;
import org.netbeans.modeler.anchorshape.crow.ZeroOrMoreCrowShape;
import org.netbeans.modeler.anchorshape.crow.ZeroOrOneCrowShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author Gaurav_Gupta
 */
public class ReferenceFlowWidget extends AbstractEdgeWidget<DBModelerScene> {

    private ForeignKeyWidget<DBColumn> foreignKeyWidget;
    private IReferenceColumnWidget referenceColumnWidget;
    private static AnchorShape ZERO_MORE;// = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/db/modeler/resource/image/MORE_ZERO.png"), true, 18, 7);
    private static AnchorShape ZERO_ONE;// = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/db/modeler/resource/image/ZERO_ONE.png"), true, 18, 7);
    private static AnchorShape ONE_MORE;// = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/db/modeler/resource/image/MORE_ONE.png"), true, 18, 7);
    private static AnchorShape ONE_ONE;// = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/db/modeler/resource/image/ONE_ONE.png"), true, 18, 7);

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

        if (foreignKeyWidget.getBaseElementSpec().getAttribute() instanceof RelationAttribute) {
            RelationAttribute relationAttribute = (RelationAttribute) foreignKeyWidget.getBaseElementSpec().getAttribute();
            if (foreignKeyWidget.getBaseElementSpec().isAllowNull()) {
                if (relationAttribute instanceof MultiRelationAttribute) {
                    setSourceAnchorShape(ZERO_MORE);
                } else {
                    setSourceAnchorShape(ZERO_ONE);
                }
            } else if (relationAttribute instanceof MultiRelationAttribute) {
                setSourceAnchorShape(ONE_MORE);
            } else {
                setSourceAnchorShape(ONE_ONE);
            }
        } else if (foreignKeyWidget.getBaseElementSpec().getAttribute() instanceof ElementCollection) {
            setSourceAnchorShape(ONE_MORE);
        }

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
        return (ColumnWidget) referenceColumnWidget;
    }

    public ForeignKeyWidget getForeignKeyWidget() {
        return foreignKeyWidget;
    }

    public void setForeignKeyWidget(ForeignKeyWidget columnWidget) {
        this.foreignKeyWidget = columnWidget;
        if (columnWidget != null) {
            columnWidget.addReferenceFlowWidget(this);
        }
    }

    public IReferenceColumnWidget getReferenceColumnWidget() {
        return referenceColumnWidget;
    }

    public void setReferenceColumnWidget(IReferenceColumnWidget referenceColumnWidget) {
        this.referenceColumnWidget = referenceColumnWidget;
        if (referenceColumnWidget != null) {
            referenceColumnWidget.addReferenceFlowWidget(this);
        }
    }

    @Override
    public void destroy() {
    }

}
