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

import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.specification.model.document.IPModelerScene;
import org.netbeans.modeler.specification.model.document.core.IFlowEdge;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;

public abstract class AbstractEdgeWidget<S extends IPModelerScene> extends PEdgeWidget<S> implements IFlowEdgeWidget<IFlowEdge> {

    private Widget flowElementsContainer;
    private IFlowEdge baseElementSpec;

    public AbstractEdgeWidget(S scene, EdgeWidgetInfo edge) {
        super(scene, edge);

    }

    /**
     * @return the baseElementSpec
     */
    @Override
    public IFlowEdge getBaseElementSpec() {
        return baseElementSpec;
    }

    /**
     * @param baseElementSpec the baseElementSpec to set
     */
    @Override
    public void setBaseElementSpec(IFlowEdge baseElementSpec) {
        this.baseElementSpec = baseElementSpec;
    }
    protected String id;
    protected String name;
    protected String documentation;

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

//    private LayerWidget preLayerWidget;
//
//    /**
//     * @return the preLayerWidget
//     */
//    public LayerWidget getPreLayerWidget() {
//        return preLayerWidget;
//    }
//
//    /**
//     * @param preLayerWidget the preLayerWidget to set
//     */
//    public void setPreLayerWidget(LayerWidget preLayerWidget) {
//        this.preLayerWidget = preLayerWidget;
//    }
    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return null;
    }

    @Override
    public void createVisualPropertySet(ElementPropertySet elementPropertySet) {
    }
}
