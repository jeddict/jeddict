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
package io.github.jeddict.jpa.modeler.properties.classmember.nodes;

import java.util.List;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import org.openide.nodes.Node;

public class ClassMemberChildFactory extends TreeChildFactory<ClassMembers,AttributeWidget> {

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {
        JavaClassWidget<? extends JavaClass> classWidget = null;
        if (parentNode instanceof CMRootNode) {
            classWidget = ((CMRootNode) parentNode).getRootWidget();
        }
        if (classWidget != null) {
            attributeWidgets.addAll(classWidget.getAllSortedAttributeWidgets());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(final AttributeWidget attributeWidget) {
        CMLeafNode childNode;
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
        Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();

        if (parentNode.getBaseElementSpec() != null) {
            checkableNode.setSelected(parentNode.getBaseElementSpec().isExist(attribute));
        }

        childNode = new CMLeafNode(attributeWidget, parentNode.getBaseElementSpec(), checkableNode);
        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();
        return (Node) childNode;
    }

}
