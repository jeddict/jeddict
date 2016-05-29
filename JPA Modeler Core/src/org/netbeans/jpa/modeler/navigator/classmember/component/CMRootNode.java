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
package org.netbeans.jpa.modeler.navigator.classmember.component;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeChildFactory;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeChildNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeParentNode;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

//       CMChildFactory
//             |
//             |
//             |
//             |
//           CMNode
//         /   |  \
//        /    |   \
//       /     |    \
//      /      |     \
//     /       |      \
//  Widget   Graph    CheckableAttributeNode
public class CMRootNode extends AbstractNode implements TreeParentNode<ClassMembers> {

    private CheckableAttributeNode checkableNode;

    private final PersistenceClassWidget widget;
    private final ClassMembers classMembers;

    private final List<TreeChildNode<ClassMembers>> childList = new ArrayList<>();

    public CMRootNode(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget, ClassMembers classMembers, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(Children.create(childFactory, true), Lookups.singleton(checkableNode));
        this.widget = persistenceClassWidget;
        this.checkableNode = checkableNode;
        this.classMembers = classMembers;
        checkableNode.setNode(this);
        childFactory.setParentNode(this);

        checkableNode.setSelected(true);

        ManagedClass managedClass = persistenceClassWidget.getBaseElementSpec();
        setDisplayName(managedClass.getClazz());
        setShortDescription(managedClass.getClazz());
        setIconBaseWithExtension(persistenceClassWidget.getIconPath());
    }

    public PersistenceClassWidget getRootWidget() {
        return widget;
    }

    @Override
    public void addChild(TreeChildNode child) {
        childList.add(child);
    }

    @Override
    public void removeChild(TreeChildNode child) {
        childList.remove(child);
    }

    @Override
    public CheckableAttributeNode getCheckableNode() {
        return checkableNode;
    }

    /**
     * @return the childList
     */
    @Override
    public List<TreeChildNode<ClassMembers>> getChildList() {
        return childList;
    }

    /**
     * @return the classMembers
     */
    @Override
    public ClassMembers getBaseElementSpec() {
        return classMembers;
    }

    @Override
    public void refreshView() {
        fireIconChange();
    }

}
