/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.navigator.overrideview;

import java.util.List;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.AttributeOverrideHandler;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class OverrideAttributeChildFactory extends OverrideChildFactory {

    public OverrideAttributeChildFactory(EntityWidget entityWidget) {
        super(entityWidget);
    }

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {
        for (AttributeWidget attributeWidget : entityWidget.getAttributeOverrideWidgets()) {
            attributeWidgets.add(attributeWidget);
        }
        for (AttributeWidget attributeWidget : entityWidget.getEmbeddedOverrideWidgets()) {
            attributeWidgets.add(attributeWidget);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(final AttributeWidget attributeWidget) {
        Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();
        AbstractNode node;
        if (attributeWidget instanceof EmbeddedAttributeWidget) {
            EmbeddedAttributeWidget embeddedAttributeWidget = (EmbeddedAttributeWidget) attributeWidget;
            node = new OverrideEmbeddedRootNode(Children.create(new OverrideEmbeddedAttributeChildFactory(entityWidget, "", embeddedAttributeWidget, embeddedAttributeWidget.getEmbeddableFlowWidget().getTargetEmbeddableWidget()), true));
        } else {
            node = new PropertyNode(entityWidget.getModelerScene(), Children.LEAF) {

                @Override
                public void createPropertySet(ElementPropertySet set) {

                    if (entityWidget.getBaseElementSpec() instanceof AttributeOverrideHandler) {
                        Attribute attributeSpec = (Attribute) attributeWidget.getBaseElementSpec();
                        AttributeOverrideHandler attributeOverrideHandler = (AttributeOverrideHandler) entityWidget.getBaseElementSpec();
                        AttributeOverride attributeOverride = attributeOverrideHandler.getAttributeOverride(attributeSpec.getName());
                        set.createPropertySet(attributeWidget, attributeOverride.getColumn(), attributeWidget.getPropertyChangeListeners(), attributeWidget.getPropertyVisibilityHandlers());
                    }
                }

            };
        }
        node.setDisplayName(attribute.getName());
        node.setShortDescription(attribute.getName());
        node.setIconBaseWithExtension(attributeWidget.getIconPath());
        return node;
    }

}
