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
package org.netbeans.jpa.modeler.core.widget;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.context.ContextModel;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

/**
 *
 *
 *
 *
 */
public abstract class FlowNodeWidget extends PNodeWidget implements IFlowNodeWidget {

    public FlowNodeWidget(IModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
//        this.setNodeType(this.getNodeWidgetInfo().getSubCategoryNodeConfig().getName());
        this.setNodeImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
        this.addPropertyChangeListener("name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String value) {
                setName(value);
                setLabel(value);
            }
        });
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
        elementConfigFactory.createPropertySet(set, this.getBaseElementSpec(), getPropertyChangeListeners(), this.getPropertyVisibilityHandlers());

    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setNodeName(label);
        } else {
            this.setNodeName("");
        }
    }

    @Override
    public void createVisualPropertySet(ElementPropertySet elementPropertySet) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private IBaseElement baseElementSpec;

    /**
     * @return the baseElementSpec
     */
    @Override
    public IBaseElement getBaseElementSpec() {
        return baseElementSpec;
    }

    /**
     * @param baseElementSpec the baseElementSpec to set
     */
    @Override
    public void setBaseElementSpec(IBaseElement baseElementSpec) {
        this.baseElementSpec = baseElementSpec;
    }

    @Override
    public void init() {
        FlowNode flowNode = (FlowNode) this.getBaseElementSpec();
        this.setName(flowNode.getName());
        this.setLabel(flowNode.getName());
    }

    @Override
    public void destroy() {
    }

    private Widget flowElementsContainer; //reverse ref

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

    protected List<IFlowEdgeWidget> incomingSequenceFlows = new ArrayList<IFlowEdgeWidget>();
    protected List<IFlowEdgeWidget> outgoingSequenceFlows = new ArrayList<IFlowEdgeWidget>();

    @Override
    public List<? extends IFlowEdgeWidget> getIncommingFlowEdgeWidget() {
        return incomingSequenceFlows;
    }

    @Override
    public List<? extends IFlowEdgeWidget> getOutgoingFlowEdgeWidget() {
        return outgoingSequenceFlows;
    }

    protected String id;
    protected String name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        if (name != null && !name.trim().isEmpty()) {
            ((FlowNode) FlowNodeWidget.this.getBaseElementSpec()).setName(name);
        } else {
            ((FlowNode) FlowNodeWidget.this.getBaseElementSpec()).setName(null);
        }
    }

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return ContextModel.getContextPaletteModel(this);
    }

}
