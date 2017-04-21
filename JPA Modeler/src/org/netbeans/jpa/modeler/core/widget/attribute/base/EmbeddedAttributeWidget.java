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
package org.netbeans.jpa.modeler.core.widget.attribute.base;

import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getConvertProperties;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperties;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperty;
import org.netbeans.jpa.modeler.spec.extend.AssociationOverrideHandler;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.ConvertContainerHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertContainerHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class EmbeddedAttributeWidget<E extends CompositionAttribute> extends BaseAttributeWidget<E> {

    private EmbeddableFlowWidget embeddableFlowWidget;

    public EmbeddedAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
//        this.setImage(JPAModelerUtil.EMBEDDED_ATTRIBUTE);
    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        CompositionAttribute attribute = (CompositionAttribute) this.getBaseElementSpec();

        if (attribute instanceof ConvertContainerHandler) {//means ElementCollectio<Embedded>, Embedded or represent value in map
            set.put("JPA_PROP", getConvertProperties(this.getModelerScene(), (ConvertContainerHandler) attribute));
        }
        if (attribute instanceof MapKeyConvertContainerHandler) {//ElementCollectio<Embedded, Y>
            set.put("JPA_PROP", getMapKeyConvertProperties(this, this.getModelerScene(), (MapKeyConvertContainerHandler) attribute));
        }
        if (attribute instanceof MapKeyConvertHandler) {//ElementCollectio<X, Y>
            set.put("JPA_PROP", getMapKeyConvertProperty(this, this.getModelerScene(), (MapKeyConvertHandler) attribute));
        }
        set.put("JPA_PROP", PropertiesHandler.getAttributeOverridesProperty("AttributeOverrides", "Attribute Overrides", "", this.getModelerScene(), attribute.getAttributeOverride()));
        if (attribute instanceof AssociationOverrideHandler) {
            set.put("JPA_PROP", PropertiesHandler.getAssociationOverridesProperty("AssociationOverrides", "Association Overrides", "", this.getModelerScene(), ((AssociationOverrideHandler) attribute).getAssociationOverride()));
        }
    }


    /**
     * @return the embeddableFlowWidget
     */
    public EmbeddableFlowWidget getEmbeddableFlowWidget() {
        return embeddableFlowWidget;
    }

    /**
     * @param embeddableFlowWidget the embeddableFlowWidget to set
     */
    public void setEmbeddableFlowWidget(EmbeddableFlowWidget embeddableFlowWidget) {
        this.embeddableFlowWidget = embeddableFlowWidget;
    }

    @Override
    public void init() {
        this.getClassWidget().scanDuplicateAttributes(null, this.name);
        validateName(null, this.getName());
        //setAttributeTooltip, visualizeDataType moved to setConnectedSibling :: @init on new compo creation no target class connected 
    }
    
    public void setConnectedSibling(EmbeddableWidget embeddableWidget) {
        CompositionAttribute compositionAttribute = (CompositionAttribute) this.getBaseElementSpec();
        compositionAttribute.setConnectedClass(embeddableWidget.getBaseElementSpec());
        setAttributeTooltip();
        visualizeDataType();
    }

    public void showCompositionPath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
        if (this.getEmbeddableFlowWidget() != null) {
            this.getEmbeddableFlowWidget().setHighlightStatus(true);
            colorScheme.highlightUI(this.getEmbeddableFlowWidget());
            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().showCompositionPath();
        }
    }

    public void hideCompositionPath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
        if (this.getEmbeddableFlowWidget() != null) {
            this.getEmbeddableFlowWidget().setHighlightStatus(false);
            colorScheme.updateUI(this.getEmbeddableFlowWidget(), this.getEmbeddableFlowWidget().getState(), this.getEmbeddableFlowWidget().getState());
            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().hideCompositionPath();
        }
    }

}
