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
package io.github.jeddict.jpa.modeler.properties.order.by;

import java.util.Arrays;
import java.util.List;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.TransientAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildNode;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderAction;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.OrderBy;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.openide.nodes.Node;

public class OrderByChildFactory extends TreeChildFactory<OrderBy,AttributeWidget> {

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {
        PersistenceClassWidget<? extends ManagedClass> classWidget = null;
        if (parentNode instanceof OBRootNode) {
            classWidget = ((OBRootNode) parentNode).getRootWidget();
        } else if (parentNode instanceof OBInternalNode) {
            classWidget = ((OBInternalNode) parentNode).getParentWidget();
        }
        if (classWidget != null) {
            for (AttributeWidget attributeWidget : classWidget.getAllSortedAttributeWidgets()) {
                if (attributeWidget instanceof TransientAttributeWidget || (attributeWidget instanceof RelationAttributeWidget && !((RelationAttributeWidget<? extends RelationAttribute>) attributeWidget).getBaseElementSpec().isOwner())) {
                    // skip
                } else {//check for all remaining
                    attributeWidgets.add(attributeWidget);
                }
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(final AttributeWidget attributeWidget) {
        TreeChildNode childNode;
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
        Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();
        String propertyPath = null;
        if (parentNode.getBaseElementSpec() != null) {
            if (parentNode instanceof OBRootNode) {
                propertyPath = attribute.getName();
            } else if (parentNode instanceof OBInternalNode && ((OBInternalNode) parentNode).getPropertyPath() != null) {
                propertyPath = ((OBInternalNode) parentNode).getPropertyPath() + '.' + attribute.getName();
            }
            if (propertyPath != null) {
                checkableNode.setSelected(parentNode.getBaseElementSpec().isExist(propertyPath));
            }
        }
        
        if (attributeWidget instanceof EmbeddedAttributeWidget) {
            EmbeddedAttributeWidget embeddedAttributeWidget = (EmbeddedAttributeWidget) attributeWidget;
            EmbeddableWidget embeddableWidget = embeddedAttributeWidget.getEmbeddableFlowWidget().getTargetEmbeddableWidget();

            OrderByChildFactory childFactory = new OrderByChildFactory();
            childNode = new OBInternalNode(embeddableWidget, embeddedAttributeWidget, parentNode.getBaseElementSpec(), propertyPath, childFactory, checkableNode);
        } else if (attributeWidget instanceof RelationAttributeWidget) {
            RelationAttributeWidget relationAttributeWidget = (RelationAttributeWidget) attributeWidget;
            IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
            EntityWidget targetEntityWidget = null;
            if (targetElementWidget instanceof EntityWidget) {
                targetEntityWidget = (EntityWidget) targetElementWidget;
            } else if (targetElementWidget instanceof RelationAttributeWidget) {
                RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
            }

            OrderByChildFactory childFactory = new OrderByChildFactory();//parentWidget, relationAttributeWidget, targetEntityWidget);
            childNode = new OBInternalNode(targetEntityWidget, relationAttributeWidget, parentNode.getBaseElementSpec(), propertyPath, childFactory, checkableNode);
        } else {
            childNode = new OBLeafNode(attributeWidget, parentNode.getBaseElementSpec(), checkableNode, Arrays.asList(OrderAction.class));
            parentNode.getBaseElementSpec().getOrderType(propertyPath).ifPresent(ot -> ((OBLeafNode)childNode).setOrder(ot));
        }

        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();
        return (Node) childNode;
    }

}
