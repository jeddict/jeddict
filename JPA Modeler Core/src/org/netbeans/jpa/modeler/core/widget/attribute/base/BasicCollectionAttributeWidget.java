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
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON_PATH;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class BasicCollectionAttributeWidget extends BaseAttributeWidget<ElementCollection> {

    public BasicCollectionAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(getIcon());
    }
    
    @Override
    public void init() {
        super.init();
        AttributeValidator.scanMapKeyHandlerError(this);
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        set.put("BASIC_PROP", PropertiesHandler.getFetchTypeProperty(this.getModelerScene(), (FetchTypeHandler) this.getBaseElementSpec()));

        ElementCollection elementCollectionSpec = this.getBaseElementSpec();

        set.put("BASIC_PROP", PropertiesHandler.getCollectionTypeProperty(this, elementCollectionSpec));
        set.createPropertySet(this, elementCollectionSpec.getCollectionTable());
        set.put("COLLECTION_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("CollectionTable_JoinColumns", "Join Columns", "", this.getModelerScene(), elementCollectionSpec.getCollectionTable().getJoinColumn()));
        createMapKeyPropertySet(set);
    }

    @Override
    public String getIconPath() {
      return BASIC_COLLECTION_ATTRIBUTE_ICON_PATH;
    }
    
    @Override
    public Image getIcon(){
        return BASIC_COLLECTION_ATTRIBUTE;
    }

}
