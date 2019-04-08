/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.widget;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;
import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.modeler.signal.SignalManager;
import io.github.jeddict.jpa.modeler.widget.context.NodeContextModel;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import org.netbeans.modeler.anchors.CustomRectangularAnchor;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.modeler.border.ResizeBorder;

public abstract class FlowNodeWidget<E extends FlowNode, S extends IModelerScene> extends PNodeWidget<S> implements IFlowNodeWidget<E> {

    private final SignalManager signalManager;

    public FlowNodeWidget(S scene, NodeWidgetInfo node) {
        super(scene, node);
        signalManager = new SignalManager(this);
        setAnchorGap(4);
        if (!node.isExist()) {
            JeddictLogger.recordJPACreateAction(node.getModelerDocument().getSpecification().getSimpleName());
        }
    }

    @Override
    public void onConnection() {
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        set.createPropertySet(this, this.getBaseElementSpec(), getPropertyChangeListeners(), this.getPropertyVisibilityHandlers());

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
    }

    private E baseElementSpec;

    /**
     * @return the baseElementSpec
     */
    @Override
    public E getBaseElementSpec() {
        return baseElementSpec;
    }

    /**
     * @param baseElementSpec the baseElementSpec to set
     */
    @Override
    public void setBaseElementSpec(E baseElementSpec) {
        this.baseElementSpec = baseElementSpec;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    private Widget flowElementsContainer; //reverse ref

    /**
     * @return the flowElementsContainer
     */
    @Override
    public Widget getFlowElementsContainer() {
        return flowElementsContainer;
    }

    /**
     * @param flowElementsContainer the flowElementsContainer to set
     */
    @Override
    public void setFlowElementsContainer(Widget flowElementsContainer) {
        this.flowElementsContainer = flowElementsContainer;
    }

    protected List<IFlowEdgeWidget> incomingSequenceFlows = new ArrayList<>();
    protected List<IFlowEdgeWidget> outgoingSequenceFlows = new ArrayList<>();

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
            getBaseElementSpec().setName(name);
        } else {
            getBaseElementSpec().setName(null);
        }
    }

    protected ContextPaletteModel contextPaletteModel;

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        if (contextPaletteModel == null) {
            contextPaletteModel = NodeContextModel.getContextPaletteModel(this);
        }
        return contextPaletteModel;
    }

    /**
     * @return the signalManager
     */
    public SignalManager getSignalManager() {
        return signalManager;
    }

    @Override
    public Anchor getAnchor() {
        return new CustomRectangularAnchor(this, 0, true);
    }

    @Override
    public ResizeBorder getNodeBorder() {
        return RECTANGLE_RESIZE_BORDER;
    }

    public abstract String getIconPath();

    public abstract Image getIcon();

}
