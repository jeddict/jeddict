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

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeChildNode;
import org.netbeans.jpa.modeler.navigator.tree.component.spec.TreeParentNode;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public class CMLeafNode extends PropertyNode implements TreeChildNode<ClassMembers> {

    private CheckableAttributeNode checkableNode;
    private final AttributeWidget leafAttributeWidget;
    private final ClassMembers classMembers;
    private TreeParentNode parent;

    public CMLeafNode(AttributeWidget leafAttributeWidget, ClassMembers classMembers, Children children, CheckableAttributeNode checkableNode) {
        super(leafAttributeWidget.getModelerScene(), children, Lookups.singleton(checkableNode));
        this.leafAttributeWidget = leafAttributeWidget;
        this.classMembers = classMembers;
        this.checkableNode = checkableNode;
        checkableNode.setNode(this);
    }

    public void init() {
        checkableNode.setEnableWithParent(true);
        this.setIconBaseWithExtension(leafAttributeWidget.getIconPath());

        Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
        if(attribute instanceof BaseAttribute){
        this.setShortDescription(attribute.getName() + " <" + ((BaseAttribute)attribute).getAttributeType() + ">");
        } else {
         this.setShortDescription(attribute.getName() + " <" + ((RelationAttribute)attribute).getTargetEntity()+ ">");   
        }

    }

    private String htmlDisplayName;
    @Override
    public String getHtmlDisplayName() {
        if (htmlDisplayName == null) {
            Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
            htmlDisplayName = attribute.getName() + " : " + attribute.getDataTypeLabel();
            htmlDisplayName = htmlDisplayName.replace("<", "&lt;").replace(">", "&gt;");
        }
        System.out.println("htmlDisplayName : " + htmlDisplayName);
        if (checkableNode != null && !checkableNode.isSelected()) {
            return String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            return String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
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

    /**
     * @return the leafAttributeWidget
     */
    public AttributeWidget getLeafAttributeWidget() {
        return leafAttributeWidget;
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
