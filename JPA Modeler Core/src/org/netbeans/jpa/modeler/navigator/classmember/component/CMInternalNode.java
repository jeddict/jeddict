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
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeChildFactory;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeChildNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeParentNode;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public class CMInternalNode extends AbstractNode implements TreeParentNode<ClassMembers>, TreeChildNode<ClassMembers> {

    private CheckableAttributeNode checkableNode;
    private final PersistenceClassWidget parentWidget;//EmbeddableWidget
    private final AttributeWidget parentAttributeWidget; //EmbeddedAttributeWidget

    private final ClassMembers classMembers;
    private Attribute attribute;

    private TreeParentNode<ClassMembers> parent;
    private final List<TreeChildNode<ClassMembers>> childList = new ArrayList<>();

    public CMInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, ClassMembers classMembers, Attribute attribute, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(Children.create(childFactory, true), Lookups.singleton(checkableNode));
        this.parentWidget = parentWidget;
        this.parentAttributeWidget = parentAttributeWidget;
        this.checkableNode = checkableNode;
        this.classMembers = classMembers;
        this.attribute = attribute;
        checkableNode.setNode(this);
        childFactory.setParentNode(this);
        init();
    }

    public CMInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, ClassMembers classMembers, Attribute attribute, TreeChildFactory childFactory) {
        super(Children.create(childFactory, true));
        this.parentWidget = parentWidget;
        this.parentAttributeWidget = parentAttributeWidget;
        this.classMembers = classMembers;
        this.attribute = attribute;
        childFactory.setParentNode(this);
        init();
    }

    private void init() {
        setIconBaseWithExtension(parentAttributeWidget.getIconPath());
        
        Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
        ManagedClass managedClass = (ManagedClass) parentWidget.getBaseElementSpec();
        this.setDisplayName(attribute.getName());
        this.setShortDescription(attribute.getName() + " <" + managedClass.getName() + ">");
    }

    @Override
    public String getHtmlDisplayName() {
        Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
        String htmlDisplayName = attribute.getName(); //NOI18N
        if (checkableNode != null && !checkableNode.isSelected()) {
            htmlDisplayName = String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            htmlDisplayName = String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
        return htmlDisplayName;
    }

    public PersistenceClassWidget getParentWidget() {
        return parentWidget;
    }

    /**
     * @return the checkableNode
     */
    @Override
    public CheckableAttributeNode getCheckableNode() {
        return checkableNode;
    }

    /**
     * @return the parent
     */
    @Override
    public TreeParentNode<ClassMembers> getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(TreeParentNode<ClassMembers> parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(TreeChildNode<ClassMembers> child) {
        getChildList().add(child);
    }

    @Override
    public void removeChild(TreeChildNode<ClassMembers> child) {
        getChildList().remove(child);
    }

    /**
     * @return the childList
     */
    @Override
    public List<TreeChildNode<ClassMembers>> getChildList() {
        return childList;
    }

    /**
     * @return the parentAttributeWidget
     */
    public AttributeWidget getParentAttributeWidget() {
        return parentAttributeWidget;
    }

    /**
     * @return the classMembers
     */
    @Override
    public ClassMembers getBaseElementSpec() {
        return classMembers;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setSubgraph(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public void refreshView() {
        fireIconChange();
    }
}
