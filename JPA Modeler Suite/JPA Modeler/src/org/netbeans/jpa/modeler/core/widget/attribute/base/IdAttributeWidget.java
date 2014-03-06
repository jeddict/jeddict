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

import org.apache.velocity.util.StringUtils;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.properties.idgeneration.IdGeneratorPanel;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav Gupta
 */
public class IdAttributeWidget extends BaseAttributeWidget {

    public IdAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setIcon(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/id-attribute.png"));
    }

    public static PinWidgetInfo create(String id, String name) {
        PinWidgetInfo pinWidgetInfo = AttributeWidget.create(id, name);
        pinWidgetInfo.setDocumentId(IdAttributeWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        set.put("BASIC_PROP", getGeneratorProperty());

    }

    private EmbeddedPropertySupport getGeneratorProperty() {

        GenericEmbedded entity = new GenericEmbedded("generator", "Id Generator", "");
        entity.setEntityEditor(new IdGeneratorPanel(this.getModelerScene().getModelerFile()));

        entity.setDataListener(new EmbeddedDataListener<Id>() {
            private Id idAttribute;
            private String displayName = null;
            private IdAttributeWidget idAttributeWidget = null;

            @Override
            public void init() {
                idAttribute = (Id) IdAttributeWidget.this.getBaseElementSpec();
            }

            @Override
            public Id getData() {
                if (idAttribute.getGeneratedValue() == null) {
                    idAttribute.setGeneratedValue(new GeneratedValue());
                }
                return idAttribute;
            }

            @Override
            public void setData(Id classSpec) {
                IdAttributeWidget.this.setBaseElementSpec((IBaseElement) classSpec);
            }

            @Override
            public String getDisplay() {
                if (idAttribute.getGeneratedValue() != null && idAttribute.getGeneratedValue().getStrategy() != null) {
                    return StringUtils.firstLetterCaps(idAttribute.getGeneratedValue().getStrategy().toString());
                } else if (idAttribute.getGeneratedValue() == null || idAttribute.getGeneratedValue().getStrategy() == null) {
                    return "None";
                } else {
                    return "";
                }
            }

        });
        return new EmbeddedPropertySupport(this.getModelerScene().getModelerFile(), entity);
    }

}
