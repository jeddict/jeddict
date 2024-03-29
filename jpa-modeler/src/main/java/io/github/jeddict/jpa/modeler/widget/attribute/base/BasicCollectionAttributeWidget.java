/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON_PATH;
import io.github.jeddict.jpa.modeler.properties.PropertiesHandler;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getConvertProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperties;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getOrderProperty;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.FetchTypeHandler;
import java.awt.Image;
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
        ElementCollection elementCollectionSpec = this.getBaseElementSpec();
        set.put("JPA_PROP", getConvertProperty(this, this.getModelerScene(), elementCollectionSpec));
        set.put("JPA_PROP", getMapKeyConvertProperties(this, this.getModelerScene(), elementCollectionSpec));
        set.put("JPA_PROP", getMapKeyConvertProperty(this, this.getModelerScene(), elementCollectionSpec));
        set.put("JPA_PROP", getOrderProperty(this));
        set.put("JPA_PROP", PropertiesHandler.getFetchTypeProperty(this.getModelerScene(), (FetchTypeHandler) this.getBaseElementSpec()));
        set.put("ATTR_PROP", PropertiesHandler.getCollectionTypeProperty(this, elementCollectionSpec));
        set.put("ATTR_PROP", PropertiesHandler.getCollectionImplTypeProperty(this, elementCollectionSpec));
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
        return BASIC_COLLECTION_ATTRIBUTE_ICON;
    }

}
