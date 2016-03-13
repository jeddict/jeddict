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
package org.netbeans.jpa.modeler.core.widget.flow;

import java.awt.Color;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class EmbeddableFlowWidget extends AbstractEdgeWidget<JPAModelerScene> {

    private EmbeddableWidget targetEmbeddableWidget;
    private EmbeddedAttributeWidget sourceEmbeddedAttributeWidget;

    public EmbeddableFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
//        setAnchorGap(0);

    }

    private Color color;

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
    public IFlowNodeWidget getSourceWidget() {
        throw new UnsupportedOperationException("Not supported yet."); //  return sourceNode;
    }

    @Override
    public IFlowNodeWidget getTargetWidget() {
        return getTargetEmbeddableWidget();
    }

    /**
     * @return the targetEmbeddableWidget
     */
    public EmbeddableWidget getTargetEmbeddableWidget() {
        return targetEmbeddableWidget;
    }

    /**
     * @param targetEmbeddableWidget the targetEmbeddableWidget to set
     */
    public void setTargetEmbeddableWidget(EmbeddableWidget targetEmbeddableWidget) {
        this.targetEmbeddableWidget = targetEmbeddableWidget;
    }

    /**
     * @return the sourceEmbeddableAttributeWidget
     */
    public EmbeddedAttributeWidget getSourceEmbeddedAttributeWidget() {
        return sourceEmbeddedAttributeWidget;
    }

    /**
     * @param sourceEmbeddedAttributeWidget the sourceEmbeddableAttributeWidget
     * to set
     */
    public void setSourceEmbeddedAttributeWidget(EmbeddedAttributeWidget sourceEmbeddedAttributeWidget) {
        this.sourceEmbeddedAttributeWidget = sourceEmbeddedAttributeWidget;
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

}
