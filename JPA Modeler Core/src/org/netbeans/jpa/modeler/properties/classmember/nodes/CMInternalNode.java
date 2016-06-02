/**
 * Copyright [2016] Gaurav Gupta
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

import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.InternalNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;

@Deprecated
public class CMInternalNode extends InternalNode<ClassMembers> {

    private final PersistenceClassWidget parentWidget;//EmbeddableWidget
    private final AttributeWidget parentAttributeWidget; //EmbeddedAttributeWidget
    private Attribute attribute;

    public CMInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, ClassMembers classMembers, Attribute attribute, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(classMembers, childFactory, checkableNode);
        this.parentWidget = parentWidget;
        this.parentAttributeWidget = parentAttributeWidget;
        this.attribute = attribute;
    }

    public CMInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, ClassMembers classMembers, Attribute attribute, TreeChildFactory childFactory) {
        this(parentWidget, parentAttributeWidget, classMembers, attribute, childFactory, null);
    }

    public void init() {
        setIconBaseWithExtension(parentAttributeWidget.getIconPath());
        Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
        ManagedClass managedClass = (ManagedClass) parentWidget.getBaseElementSpec();
        this.setShortDescription(attribute.getName() + " <" + managedClass.getName() + ">");
    }

    private String htmlDisplayName;

    @Override
    public String getHtmlDisplayName() {
        if (htmlDisplayName == null) {
            Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
            htmlDisplayName = attribute.getName() + " : " + attribute.getDataTypeLabel();
            htmlDisplayName = htmlDisplayName.replace("<", "&lt;").replace(">", "&gt;");
        }
        System.out.println("htmlDisplayName : " + htmlDisplayName);
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
            return String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            return String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
    }

    public PersistenceClassWidget getParentWidget() {
        return parentWidget;
    }

    /**
     * @return the parentAttributeWidget
     */
    public AttributeWidget getParentAttributeWidget() {
        return parentAttributeWidget;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

}
