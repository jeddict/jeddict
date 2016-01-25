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
package org.netbeans.db.modeler.core.widget;

import java.awt.Color;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.core.widget.flow.AbstractEdgeWidget;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav_Gupta
 */
public class ReferenceFlowWidget extends AbstractEdgeWidget<DBModelerScene> {

    private ForeignKeyWidget foreignKeyWidget;
    private ColumnWidget referenceColumnWidget;
    private static final IconAnchorShape ANCHOR_SHAPE = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/db/modeler/resource/image/single-value-anchor-shape.png"), true, 18, 7);

    public ReferenceFlowWidget(DBModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setSourceAnchorShape(ANCHOR_SHAPE);
        setTargetAnchorShape(ANCHOR_SHAPE);
//        setAnchorGap(2);
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    private Color color;
    // private Float size;

    public Sheet.Set getVisualPropertiesSet(Sheet.Set set) throws NoSuchMethodException, NoSuchFieldException {
        set.put(new ElementPropertySupport(this, Color.class, "color", "Color", "The Line Color of the SequenceFlow Element."));
        return set;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
        this.setLineColor(color);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public ForeignKeyWidget getSourceWidget() {
        return foreignKeyWidget;
    }

    @Override
    public ColumnWidget getTargetWidget() {
        return referenceColumnWidget;
    }

    public ForeignKeyWidget getsetForeignKeyWidget() {
        return foreignKeyWidget;
    }

    public void setForeignKeyWidget(ForeignKeyWidget columnWidget) {
        this.foreignKeyWidget = columnWidget;
        if (columnWidget != null) {
            columnWidget.addReferenceFlowWidget(this);
        }
    }

    public ColumnWidget getReferenceColumnWidget() {
        return referenceColumnWidget;
    }

    public void setReferenceColumnWidget(ColumnWidget referenceColumnWidget) {
        this.referenceColumnWidget = referenceColumnWidget;
        if (referenceColumnWidget != null) {
            referenceColumnWidget.removeReferenceFlowWidget(this);
        }
    }

}
