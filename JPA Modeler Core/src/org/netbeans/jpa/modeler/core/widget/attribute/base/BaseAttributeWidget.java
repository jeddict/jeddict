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
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseAttributeWidget<E extends BaseAttribute> extends AttributeWidget<E> {

    public BaseAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("attributeType", (PropertyChangeListener<String>) attributeType -> {
            setAttributeTooltip();
        });
        this.addPropertyChangeListener("nullable", (PropertyChangeListener<Boolean>) nullable -> {
            this.getBaseElementSpec().setFunctionalType(nullable);
        });
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);

        if (this.getBaseElementSpec() instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) this.getBaseElementSpec();
            set.createPropertySet(this, persistenceBaseAttribute.getColumn(), getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        } else if (this instanceof BasicCollectionAttributeWidget) {
            ElementCollection elementCollection = (ElementCollection) this.getBaseElementSpec();
            set.createPropertySet(this, elementCollection.getColumn(), getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        }
//        BasicCollectionAttributeWidget => ElementCollection [Column allowed]
//        MultiValueEmbeddedAttributeWidget => ElementCollection [Column not allowed]
//        set.put("BASIC_PROP", getValidationProperty());
        
        createBeanValidationPropertySet(set);
    }
    
    public void createBeanValidationPropertySet(ElementPropertySet set){
        set.deleteGroup("CONSTRAINTS");
        this.getBaseElementSpec().getNewConstraints().stream().forEach((constraint) -> {
            set.createPropertySet(this, constraint, getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        });
    }

}
