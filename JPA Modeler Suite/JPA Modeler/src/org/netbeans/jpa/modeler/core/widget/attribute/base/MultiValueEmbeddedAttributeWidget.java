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

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav Gupta
 */
public class MultiValueEmbeddedAttributeWidget extends EmbeddedAttributeWidget {

    private EmbeddableFlowWidget embeddableFlowWidget;

    public MultiValueEmbeddedAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setIcon(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/multi-value-embedded.gif"));
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);

        ElementCollection elementCollectionSpec = (ElementCollection) this.getBaseElementSpec();
        ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
        elementConfigFactory.createPropertySet(set, elementCollectionSpec.getCollectionTable());
        set.put("COLLECTION_TABLE_PROP", JPAModelerUtil.getJoinColumnsProperty("CollectionTable_JoinColumns", "Join Columns", "", this.getModelerScene(), elementCollectionSpec.getCollectionTable().getJoinColumn()));

    }

    public static PinWidgetInfo create(String id, String name) {
        PinWidgetInfo pinWidgetInfo = AttributeWidget.create(id, name);
        pinWidgetInfo.setDocumentId(MultiValueEmbeddedAttributeWidget.class.getSimpleName());
        return pinWidgetInfo;
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

}
