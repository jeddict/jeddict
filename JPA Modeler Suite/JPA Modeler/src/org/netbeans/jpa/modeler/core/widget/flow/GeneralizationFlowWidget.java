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
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav_Gupta
 */
public class GeneralizationFlowWidget extends PEdgeWidget implements IFlowEdgeWidget {

    private JavaClassWidget superclassWidget;
    private JavaClassWidget subclassWidget;
    private IBaseElement baseElementSpec;
    private Widget flowElementsContainer;

    public GeneralizationFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setTargetAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/generalization-anchor.png"), true));

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
    protected String id;
    protected String name;
    protected String documentation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
//        if (name != null && !name.trim().isEmpty()) {
//            this.getSequenceFlowSpec().setName(name);
//        } else {
//            this.getSequenceFlowSpec().setName(null);
//        }
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /**
     * @return the flowElementsContainer
     */
    public Widget getFlowElementsContainer() {
        return flowElementsContainer;
    }

    /**
     * @param flowElementsContainer the flowElementsContainer to set
     */
    public void setFlowElementsContainer(Widget flowElementsContainer) {
        this.flowElementsContainer = flowElementsContainer;
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

    /**
     * @return the baseElementSpec
     */
    public IBaseElement getBaseElementSpec() {
        return baseElementSpec;
    }

    /**
     * @param baseElementSpec the baseElementSpec to set
     */
    public void setBaseElementSpec(IBaseElement baseElementSpec) {
        this.baseElementSpec = baseElementSpec;
    }

    /**
     * @return the sequenceFlowSpec
     */
//    public TSequenceFlow getSequenceFlowSpec() {
//        return (TSequenceFlow) baseElementSpec;
//    }
    @Override
    public void init() {

        if (this.getSubclassWidget() instanceof EntityWidget) {
            AttributeValidator.scanInheritenceError((EntityWidget) this.getSubclassWidget());
            ((EntityWidget) this.getSubclassWidget()).scanPrimaryKeyError();
            AttributeValidator.validateMultipleEmbeddedIdFound((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((EntityWidget) this.getSubclassWidget());
        } else if (this.getSubclassWidget() instanceof MappedSuperclassWidget) {
            AttributeValidator.validateMultipleEmbeddedIdFound((MappedSuperclassWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((MappedSuperclassWidget) this.getSubclassWidget());
        }

    }

    @Override
    public void destroy() {
        if (this.getSubclassWidget() instanceof EntityWidget) {
            AttributeValidator.scanInheritenceError((EntityWidget) this.getSubclassWidget());
            ((EntityWidget) this.getSubclassWidget()).scanPrimaryKeyError();
            AttributeValidator.validateMultipleEmbeddedIdFound((EntityWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((EntityWidget) this.getSubclassWidget());
        } else if (this.getSubclassWidget() instanceof MappedSuperclassWidget) {
            AttributeValidator.validateMultipleEmbeddedIdFound((MappedSuperclassWidget) this.getSubclassWidget());
            AttributeValidator.validateEmbeddedIdAndIdFound((MappedSuperclassWidget) this.getSubclassWidget());
        }
        this.setSubclassWidget(null);
        this.setSuperclassWidget(null);

    }

    @Override
    public IFlowNodeWidget getSourceFlowNodeWidget() {
        return getSubclassWidget();
    }

    @Override
    public IFlowNodeWidget getTargetFlowNodeWidget() {
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
