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
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.nodes.Sheet;

public abstract class RelationFlowWidget extends PEdgeWidget implements IFlowEdgeWidget {

    private RelationAttributeWidget sourceRelationAttributeWidget;
    private IBaseElement baseElementSpec;
    private Widget flowElementsContainer;

    public RelationFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        this.addPropertyChangeListener("name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String value) {
                setName(value);
                RelationFlowWidget.this.setLabel(name);
            }
        });
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
    }

    @Override
    public void destroy() {
    }

    @Override
    public IFlowNodeWidget getSourceFlowNodeWidget() {
        throw new UnsupportedOperationException("Not supported yet."); //  return sourceNode;
    }

    @Override
    public IFlowNodeWidget getTargetFlowNodeWidget() {
        throw new UnsupportedOperationException("Not supported yet."); //  return targetNode;
    }

}
