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
package org.netbeans.jpa.modeler.navigator.overrideview;

import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.extend.AssociationOverrideHandler;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class OverrideAssociationChildFactory extends OverrideChildFactory {

    public OverrideAssociationChildFactory(EntityWidget entityWidget) {
        super(entityWidget);
    }

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {
        for (AttributeWidget attributeWidget : entityWidget.getAssociationOverrideWidgets()) {
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
            node = new OverrideEmbeddedRootNode(Children.create(new OverrideEmbeddedAssociationChildFactory(entityWidget, "", embeddedAttributeWidget, embeddedAttributeWidget.getEmbeddableFlowWidget().getTargetEmbeddableWidget()), true));
        } else {
            node = new PropertyNode<JPAModelerScene>(entityWidget.getModelerScene(), Children.LEAF) {

                @Override
                public void createPropertySet(ElementPropertySet set) {

                    if (entityWidget.getBaseElementSpec() instanceof AssociationOverrideHandler) {
                        Attribute attributeSpec = (Attribute) attributeWidget.getBaseElementSpec();
                        AssociationOverrideHandler associationOverrideHandler = (AssociationOverrideHandler) entityWidget.getBaseElementSpec();
                        AssociationOverride associationOverride = associationOverrideHandler.getAssociationOverride(attributeSpec.getName());
                        if (attributeSpec instanceof JoinColumnHandler) {
                            set.put("JOIN_COLUMN_PROP", PropertiesHandler.getJoinColumnsProperty("JoinColumns", "Join Columns", "", this.getModelerScene(), associationOverride.getJoinColumn()));
                        }

                        set.createPropertySet(attributeWidget, associationOverride.getJoinTable());
                        set.put("JOIN_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("JoinTable_JoinColumns", "Join Columns", "", this.getModelerScene(), associationOverride.getJoinTable().getJoinColumn()));
                        set.put("JOIN_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("JoinTable_InverseJoinColumns", "Inverse Join Columns", "", this.getModelerScene(), associationOverride.getJoinTable().getInverseJoinColumn()));

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
