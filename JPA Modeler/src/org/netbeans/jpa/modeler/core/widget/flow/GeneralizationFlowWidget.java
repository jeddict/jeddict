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
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.GENERALIZATION_ANCHOR;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author Gaurav_Gupta
 */
public class GeneralizationFlowWidget extends AbstractEdgeWidget<JPAModelerScene> {

    private JavaClassWidget superclassWidget;
    private JavaClassWidget subclassWidget;
    private static final IconAnchorShape GENERALIZATION_ANCHOR_SHAPE = new IconAnchorShape(GENERALIZATION_ANCHOR, true);

    public GeneralizationFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setTargetAnchorShape(GENERALIZATION_ANCHOR_SHAPE);
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    /**
     * @return the sourceNode
     */
//    public IFlowNodeWidget getSourceNode() {
//        return sourceNode;
//    }
    /**
     * @param sourceNode the sourceNode to set
     */
    public void setSourceNode(FlowNodeWidget sourceNode) {

    }

    /**
     * @return the targetNode
     */
//    public IFlowNodeWidget getTargetNode() {
//        return targetNode;
//    }
    /**
     * @param targetNode the targetNode to set
     */
    public void setTargetNode(FlowNodeWidget targetNode) {

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

        if (this.getSubclassWidget() instanceof EntityWidget) {
            AttributeValidator.scanInheritanceError((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateMultipleEmbeddedIdFound((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((EntityWidget) this.getSubclassWidget());
        } else if (this.getSubclassWidget() instanceof MappedSuperclassWidget) {
            AttributeValidator.validateMultipleEmbeddedIdFound((MappedSuperclassWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((MappedSuperclassWidget) this.getSubclassWidget());
        }
        //BUG : https://java.net/bugzilla/show_bug.cgi?id=6756 - Diagram collapses on reload when using MappedSuperClass hierarchy
        if (this.getSubclassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getSubclassWidget()).scanKeyError();
            ((EntityWidget) this.getSubclassWidget()).scanDuplicateInheritedAttributes();
            ((EntityWidget) this.getSubclassWidget()).scanDiscriminatorValue();
        }
        
    }

    @Override
    public void destroy() {
        if (this.getSubclassWidget() instanceof EntityWidget) {
            AttributeValidator.scanInheritanceError((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateMultipleEmbeddedIdFound((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((EntityWidget) this.getSubclassWidget());
        } else if (this.getSubclassWidget() instanceof MappedSuperclassWidget) {
            AttributeValidator.validateMultipleEmbeddedIdFound((MappedSuperclassWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((MappedSuperclassWidget) this.getSubclassWidget());
        }
        //BUG : https://java.net/bugzilla/show_bug.cgi?id=6756 - Diagram collapses on reload when using MappedSuperClass hierarchy
        if (this.getSubclassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getSubclassWidget()).scanKeyError();
            ((EntityWidget) this.getSubclassWidget()).scanDuplicateInheritedAttributes();
            ((EntityWidget) this.getSubclassWidget()).scanDiscriminatorValue();
        }
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
    public JavaClassWidget getSuperclassWidget() {
        return superclassWidget;
    }

    /**
     * @param superclassWidget the superclassWidget to set
     */
    public void setSuperclassWidget(JavaClassWidget superclassWidget) {
        this.superclassWidget = superclassWidget;
        if (superclassWidget != null) {
            superclassWidget.addIncomingGeneralizationFlowWidget(this);
        }
    }

    /**
     * @return the subclassWidget
     */
    public JavaClassWidget getSubclassWidget() {
        return subclassWidget;
    }

    /**
     * @param subclassWidget the subclassWidget to set
     */
    public void setSubclassWidget(JavaClassWidget subclassWidget) {
        this.subclassWidget = subclassWidget;
        if (subclassWidget != null) {
            subclassWidget.setOutgoingGeneralizationFlowWidget(this);
        }
    }

}
