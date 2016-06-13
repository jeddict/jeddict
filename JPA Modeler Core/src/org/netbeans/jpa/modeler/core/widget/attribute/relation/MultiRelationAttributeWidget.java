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

import java.util.Map;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class MultiRelationAttributeWidget<E extends MultiRelationAttribute> extends RelationAttributeWidget<E> {

    public MultiRelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
        @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
         PropertyVisibilityHandler mapKeyVisibilityHandler = () -> {
            Attribute attribute = this.getBaseElementSpec();
            if(attribute instanceof CollectionTypeHandler){
                String classname = ((CollectionTypeHandler)attribute).getCollectionType();
                    try {
                        return Map.class.isAssignableFrom(Class.forName(classname));
                    } catch (ClassNotFoundException ex) { }
            }
            return false;
        };
         
        MultiRelationAttribute relationAttribute = this.getBaseElementSpec();
        set.put("BASIC_PROP", PropertiesHandler.getCollectionTypeProperty(this, relationAttribute));
        set.put("BASIC_PROP", PropertiesHandler.getMapKeyProperty(this, relationAttribute,mapKeyVisibilityHandler));
        set.put("BASIC_PROP", PropertiesHandler.getFieldTypeProperty("mapKeyFieldType", "MapKey Field Type", "", this));
        
        this.addPropertyVisibilityHandler("mapKeyType", mapKeyVisibilityHandler);
        this.addPropertyVisibilityHandler("mapKeyFieldType", () -> {
            Attribute attribute = this.getBaseElementSpec();
            return mapKeyVisibilityHandler.isVisible() && attribute instanceof MultiRelationAttribute && ((MultiRelationAttribute)attribute).getMapKeyType() == MapKeyType.NEW;
        });
        
    }

}
