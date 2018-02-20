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

import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.PersistenceBaseAttribute;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
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
        this.addPropertyChangeListener("attributeType", (PropertyChangeListener<String>) (oldValue,attributeType) -> {
            setAttributeTooltip();
        });
        this.addPropertyChangeListener("nullable", (PropertyChangeListener<Boolean>) (oldValue,nullable) -> {
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

    }
    
}
