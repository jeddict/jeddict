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
package org.netbeans.jpa.modeler.core.widget;

import java.awt.Image;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.context.PinContextModel;
import org.netbeans.jpa.modeler.spec.extend.FlowPin;
import org.netbeans.modeler.specification.model.document.IPModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowPinWidget;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.PinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class FlowPinWidget<E extends FlowPin, S extends IPModelerScene> extends PinWidget<S> implements IFlowPinWidget<E> {

    protected final ErrorHandler errorHandler;
    
    public FlowPinWidget(S scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (String value) -> {
            setName(value);
            
            if (value != null && !value.trim().isEmpty()) {
                FlowPinWidget.this.setLabel(value);
            } else {
                FlowPinWidget.this.setLabel("");
            }
        });
        errorHandler = new ErrorHandler(this);
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
             FlowPinWidget.this.getBaseElementSpec().setName(name);
        } else {
             FlowPinWidget.this.getBaseElementSpec().setName(null);
        }

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

    @Override
    public void createPropertySet(ElementPropertySet set) {
        
        set.createPropertySet( this , this.getBaseElementSpec(), getPropertyChangeListeners());
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

    private ContextPaletteModel contextPaletteModel;

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        if (contextPaletteModel == null) {
            contextPaletteModel = PinContextModel.getContextPaletteModel(this);
        }
        return contextPaletteModel;
    }

    /**
     * @return the errorHandler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
