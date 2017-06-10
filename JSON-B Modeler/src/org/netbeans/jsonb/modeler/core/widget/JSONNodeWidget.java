/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.modeler.core.widget;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.core.widget.FlowPinWidget;
import static org.netbeans.jsonb.modeler.properties.PropertiesHandler.getJsonbTypeAdapter;
import static org.netbeans.jsonb.modeler.properties.PropertiesHandler.getJsonbTypeDeserializer;
import static org.netbeans.jsonb.modeler.properties.PropertiesHandler.getJsonbTypeSerializer;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jsonb.modeler.core.widget.context.DocumentContextModel;
import org.netbeans.jsonb.modeler.core.widget.context.NodeContextModel;
import org.netbeans.jsonb.modeler.spec.JSONBNode;
import org.netbeans.jsonb.modeler.specification.model.scene.JSONBModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class JSONNodeWidget<E extends JSONBNode> extends FlowPinWidget<E, JSONBModelerScene> {

    private final List<ReferenceFlowWidget> referenceFlowWidget = new ArrayList<>();

    public JSONNodeWidget(JSONBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        Attribute attribute = this.getBaseElementSpec().getAttribute();

        //JsonbDateFormat and JsonbNumberFormat is also used at EntityMapping and JavaClass level so custom VisibilityHandler is required here
        PropertyVisibilityHandler dateVisibilityHandler = () -> {
            boolean result = !attribute.getJsonbTransient() && attribute.getJsonbDateFormat().isSupportedFormat(attribute.getDataTypeLabel());
            if (result) {
                attribute.setJsonbNumberFormat(null);
            } else {
                attribute.setJsonbDateFormat(null);
            }
            return result;
        };
        this.addPropertyVisibilityHandler("date_value", dateVisibilityHandler);
        this.addPropertyVisibilityHandler("date_locale", dateVisibilityHandler);

        PropertyVisibilityHandler numberVisibilityHandler = () -> {
            boolean result = !attribute.getJsonbTransient() && attribute.getJsonbNumberFormat().isSupportedFormat(attribute.getDataTypeLabel());
            if (result) {
                attribute.setJsonbDateFormat(null);
            } else {
                attribute.setJsonbNumberFormat(null);
            }
            return result;
        };
        this.addPropertyVisibilityHandler("number_value", numberVisibilityHandler);
        this.addPropertyVisibilityHandler("number_locale", numberVisibilityHandler);
        
        this.addPropertyChangeListener("jsonbTransient", (PropertyChangeListener<Boolean>)(oldValue, newValue) -> {
            Font font = getPinNameWidget().getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.STRIKETHROUGH, newValue);
            getPinNameWidget().setFont(new Font(attributes));
        });
        
        super.createPropertySet(set);
        JPAModelerScene parentScene = (JPAModelerScene) this.getModelerScene().getModelerFile().getParentFile().getModelerScene();
        set.put("JSONB_PROP", getJsonbTypeAdapter(attribute, this, parentScene));
        set.put("JSONB_PROP", getJsonbTypeSerializer(attribute, this, parentScene));
        set.put("JSONB_PROP", getJsonbTypeDeserializer(attribute, this, parentScene));
    }


    public void setDatatypeTooltip() {
        this.setToolTipText(this.getBaseElementSpec().getAttribute().getDataTypeLabel());
    }

    @Override
    public void setLabel(String label) {
        if (StringUtils.isNotBlank(label)) {
            this.setPinName(this.getModelerScene().transferPropertyName(label));
        }
    }

    @Override
    public void init() {
        this.setImage(getIcon());
        validateName(this.getName());
        setDatatypeTooltip();
    }

    @Override
    public void destroy() {
    }

    public DocumentWidget getDocumentWidget() {
        return (DocumentWidget) this.getPNodeWidget();
    }

    public boolean addReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return getReferenceFlowWidget().add(flowWidget);
    }

    public boolean removeReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return getReferenceFlowWidget().remove(flowWidget);
    }

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        if (contextPaletteModel == null) {
            contextPaletteModel = NodeContextModel.getContextPaletteModel(this);
        }
        return contextPaletteModel;
    }

    /**
     * @return the referenceFlowWidget
     */
    public List<ReferenceFlowWidget> getReferenceFlowWidget() {
        return referenceFlowWidget;
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                updateName(this.name);
            }
        } else {
            setDefaultName();
        }
        validateName(this.name);
    }

    /**
     * Called when developer delete value
     */
    protected void setDefaultName() {
        this.name = evaluateName();
        if (this.getModelerScene().getModelerFile().isLoaded()) {
            updateName(null);
        }
        setLabel(name);
    }

    protected void updateName(String newName) {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        attribute.setJsonbProperty(newName);
    }
    
    protected String evaluateName() {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        return attribute.getName();
    }
    
    @Override // to return attribute name instead of property display strategy label
    public String getName() {
        return evaluateName();
    }
    
    protected void validateName(String name) {
//        JSONBDocument documentSpec = (JSONBDocument) this.getDocumentWidget().getBaseElementSpec();
//        if (documentSpec.findColumns(name).size() > 1) {
//            getSignalManager().fire(ERROR, AttributeValidator.NON_UNIQUE_COLUMN_NAME);
//        } else {
//            getSignalManager().clear(ERROR, AttributeValidator.NON_UNIQUE_COLUMN_NAME);
//        }
    }
    

}
