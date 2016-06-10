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

import java.awt.Image;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class MultiValueEmbeddedAttributeWidget extends EmbeddedAttributeWidget<ElementCollection> {

    private EmbeddableFlowWidget embeddableFlowWidget;

    public MultiValueEmbeddedAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(JPAModelerUtil.MULTI_VALUE_EMBEDDED_ATTRIBUTE);
    }
    
    @Override
    public String getIconPath() {
        return MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return MULTI_VALUE_EMBEDDED_ATTRIBUTE;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);

        ElementCollection elementCollectionSpec = this.getBaseElementSpec();
        set.put("BASIC_PROP", PropertiesHandler.getCollectionTypeProperty(this, elementCollectionSpec));

        set.createPropertySet(this, elementCollectionSpec.getCollectionTable());
        set.put("COLLECTION_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("CollectionTable_JoinColumns", "Join Columns", "", this.getModelerScene(), elementCollectionSpec.getCollectionTable().getJoinColumn()));

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
