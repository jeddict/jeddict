/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.widget.attribute.base;

import java.awt.Image;
import io.github.jeddict.jpa.modeler.widget.flow.EmbeddableFlowWidget;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCollectionImplTypeProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCollectionTypeProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getFetchTypeProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getJoinColumnsProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getOrderProperty;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.FetchTypeHandler;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON;

/**
 *
 * @author Gaurav Gupta
 */
public class MultiValueEmbeddedAttributeWidget extends EmbeddedAttributeWidget<ElementCollection> {

    private EmbeddableFlowWidget embeddableFlowWidget;
    
    @Override
    public void init() {
        super.init();
        AttributeValidator.scanMapKeyHandlerError(this);
    }

    public MultiValueEmbeddedAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON);
    }
    
    @Override
    public String getIconPath() {
        return MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        ElementCollection elementCollectionSpec = this.getBaseElementSpec();
        set.put("JPA_PROP", getOrderProperty(this));
        set.put("JPA_PROP", getFetchTypeProperty(this.getModelerScene(), (FetchTypeHandler) this.getBaseElementSpec()));
        set.put("ATTR_PROP", getCollectionTypeProperty(this, elementCollectionSpec));
        set.put("ATTR_PROP", getCollectionImplTypeProperty(this, elementCollectionSpec));
        set.createPropertySet(this, elementCollectionSpec.getCollectionTable());
        set.put("COLLECTION_TABLE_PROP", getJoinColumnsProperty("CollectionTable_JoinColumns", "Join Columns", "", this.getModelerScene(), elementCollectionSpec.getCollectionTable().getJoinColumn()));
        createMapKeyPropertySet(set);
    }
    /**
     * @return the embeddableFlowWidget
     */
    @Override
    public EmbeddableFlowWidget getEmbeddableFlowWidget() {
        return embeddableFlowWidget;
    }

    /**
     * @param embeddableFlowWidget the embeddableFlowWidget to set
     */
    @Override
    public void setEmbeddableFlowWidget(EmbeddableFlowWidget embeddableFlowWidget) {
        this.embeddableFlowWidget = embeddableFlowWidget;
    }

}
