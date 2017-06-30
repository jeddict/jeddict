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
package org.netbeans.jpa.modeler.core.widget.attribute.relation;

import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperties;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getMapKeyConvertProperty;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getOrderProperty;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertContainerHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertHandler;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class MultiRelationAttributeWidget<E extends MultiRelationAttribute> extends RelationAttributeWidget<E> {

    public MultiRelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    @Override
    public void init() {
        super.init();
        AttributeValidator.scanMapKeyHandlerError(this);
    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        MultiRelationAttribute relationAttribute = this.getBaseElementSpec();
        if (relationAttribute.isOwner()) {
            set.put("JPA_PROP", getOrderProperty(this));
        }
        if (relationAttribute instanceof MapKeyConvertContainerHandler) {//Relation<Embedded, Y>
            set.put("JPA_PROP", getMapKeyConvertProperties(this, this.getModelerScene(), (MapKeyConvertContainerHandler) relationAttribute));
        }
        if (relationAttribute instanceof MapKeyConvertHandler) {//Relation<X, Y>
            set.put("JPA_PROP", getMapKeyConvertProperty(this, this.getModelerScene(), (MapKeyConvertHandler) relationAttribute));
        }
        set.put("ATTR_PROP", PropertiesHandler.getCollectionTypeProperty(this, relationAttribute));
        set.put("ATTR_PROP", PropertiesHandler.getCollectionImplTypeProperty(this, relationAttribute));
        createMapKeyPropertySet(set);
    }

}
