/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.order.by;

import java.util.Arrays;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildNode;
import org.netbeans.jpa.modeler.properties.order.type.OrderAction;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.OrderBy;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
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
