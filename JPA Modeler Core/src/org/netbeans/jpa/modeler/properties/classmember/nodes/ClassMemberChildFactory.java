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
package org.netbeans.jpa.modeler.properties.classmember.nodes;

import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class ClassMemberChildFactory extends TreeChildFactory<ClassMembers,AttributeWidget> {

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {
        PersistenceClassWidget<? extends ManagedClass> classWidget = null;
        if (parentNode instanceof CMRootNode) {
            classWidget = ((CMRootNode) parentNode).getRootWidget();
        } else if (parentNode instanceof CMInternalNode) {
            classWidget = ((CMInternalNode) parentNode).getParentWidget();
        }
        if (classWidget != null) {
            attributeWidgets.addAll(classWidget.getAllAttributeWidgets());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(final AttributeWidget attributeWidget) {
        CMLeafNode childNode;
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
//        Attribute attribute = null;
        Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();

//        boolean isPK = attributeWidget instanceof IdAttributeWidget || attributeWidget instanceof EmbeddedIdAttributeWidget || attributeWidget instanceof VersionAttributeWidget;
        if (parentNode.getBaseElementSpec() != null) {
            boolean exist = false;
            if (parentNode instanceof CMRootNode) {
                exist = parentNode.getBaseElementSpec().isExist(attribute);
//            } else if (parentNode instanceof CMInternalNode && ((CMInternalNode) parentNode).getAttribute() != null) {
//                exist = ((CMInternalNode) parentNode).getAttribute().findNamedAttributeNode(attribute.getName());
            }

            if (exist) {
                checkableNode.setSelected(Boolean.TRUE);
//                if (exist.getSubgraph() != null && !exist.getSubgraph().isEmpty()) {
//                    subgraph = parentNode.getBaseElementSpec().findSubgraph(exist.getSubgraph());
//                }
            }
        }

        childNode = new CMLeafNode(attributeWidget, parentNode.getBaseElementSpec(), Children.LEAF, checkableNode);

        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();

        return (Node) childNode;
    }

}
