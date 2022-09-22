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
package io.github.jeddict.jpa.modeler.widget.flow;

import java.awt.Color;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.MappedSuperclassWidget;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.GENERALIZATION_ANCHOR;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author Gaurav_Gupta
 */
public class GeneralizationFlowWidget extends AbstractEdgeWidget<JPAModelerScene> {

    private JavaClassWidget superclassWidget;
    private JavaClassWidget subclassWidget;
    public static final IconAnchorShape GENERALIZATION_ANCHOR_SHAPE = new IconAnchorShape(GENERALIZATION_ANCHOR, true);

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
        if (this.getSubclassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getSubclassWidget()).scanKeyError();
            ((EntityWidget) this.getSubclassWidget()).scanDuplicateInheritedAttributes();
            ((EntityWidget) this.getSubclassWidget()).scanDiscriminatorValue();
        }
        if (this.getSuperclassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getSuperclassWidget()).scanDiscriminatorValue();
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
        if (this.getSuperclassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getSuperclassWidget()).scanKeyError();
            ((EntityWidget) this.getSuperclassWidget()).scanDuplicateInheritedAttributes();
            ((EntityWidget) this.getSuperclassWidget()).scanDiscriminatorValue();
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

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceAttributeWidget) {
        GeneralizationFlowWidget edgeWidget = this;
        JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) sourceNodeWidget;
        JavaClass sourceJavaClass = (JavaClass) sourceJavaClassWidget.getBaseElementSpec();
        JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
        JavaClass targetJavaClass = (JavaClass) targetJavaClassWidget.getBaseElementSpec();
        GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) edgeWidget;
        sourceJavaClass.addSuperclass(targetJavaClass);
        generalizationFlowWidget.setSubclassWidget(sourceJavaClassWidget);
        generalizationFlowWidget.setSuperclassWidget(targetJavaClassWidget);
        return sourceJavaClassWidget.getInternalPinWidgetInfo();
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getTargetPinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget targetAttributeWidget) {
        JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
        return targetJavaClassWidget.getInternalPinWidgetInfo();
    }

}
