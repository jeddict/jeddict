/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget.flow.relation;

import java.awt.Color;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.AbstractEdgeWidget;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.nodes.Sheet;

public abstract class RelationFlowWidget extends AbstractEdgeWidget {

    private RelationAttributeWidget sourceRelationAttributeWidget;

    public RelationFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        this.addPropertyChangeListener("name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String value) {
                setName(value);
                RelationFlowWidget.this.setLabel(name);
            }
        });
        setAnchorGap(4);
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    /**
     * @return the sourceRelationAttributeWidget
     */
    public RelationAttributeWidget getSourceRelationAttributeWidget() {
        return sourceRelationAttributeWidget;
    }

    /**
     * @param sourceRelationAttributeWidget the sourceRelationAttributeWidget to
     * set
     */
    public void setSourceRelationAttributeWidget(RelationAttributeWidget sourceRelationAttributeWidget) {
        this.sourceRelationAttributeWidget = sourceRelationAttributeWidget;
    }

//
    @Override
    public RelationAttributeWidget getSourceWidget() {
        return sourceRelationAttributeWidget;
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

}
