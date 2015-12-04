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
package org.netbeans.jpa.modeler.navigator.entitygraph;

import org.netbeans.jpa.modeler.navigator.overrideview.*;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class EGEntityChildFactory extends EGChildFactory {

    public EGEntityChildFactory(EntityWidget entityWidget) {
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
        for (AttributeWidget attributeWidget : entityWidget.getAssociationOverrideWidgets()) {
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
            node = new EGEmbeddedRootNode(Children.create(new EGEmbeddedChildFactory(entityWidget, embeddedAttributeWidget, embeddedAttributeWidget.getEmbeddableFlowWidget().getTargetEmbeddableWidget()), true));
        }  else if (attributeWidget instanceof RelationAttributeWidget) {
            RelationAttributeWidget relationAttributeWidget = (RelationAttributeWidget) attributeWidget;
            IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
            EntityWidget targetEntityWidget = null;
            if (targetElementWidget instanceof EntityWidget) {
                targetEntityWidget = (EntityWidget) targetElementWidget;
            } else if (targetElementWidget instanceof RelationAttributeWidget) {
                RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
            }
            node = new EGEmbeddedRootNode(Children.create(new EGRelationChildFactory(entityWidget, relationAttributeWidget, targetEntityWidget), true));
        } else {
            node = new EGAttributeNode(entityWidget, attributeWidget, Children.LEAF);
        }
        node.setDisplayName(attribute.getName());
        node.setShortDescription(attribute.getName());
        
        if (attributeWidget instanceof RelationAttributeWidget) {
            node.setIconBaseWithExtension(((RelationAttributeWidget) attributeWidget).getIconPath());
        } else if (attributeWidget instanceof IdAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.ID_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof EmbeddedIdAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ID_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof BasicAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.BASIC_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.MULTIVALUE_EMBEDDED_ATTRIBUTE_ICON_PATH);
        }
        return node;
    }

}
