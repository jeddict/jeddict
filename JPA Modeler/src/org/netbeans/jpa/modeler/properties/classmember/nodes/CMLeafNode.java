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

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.LeafNode;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class CMLeafNode extends LeafNode<ClassMembers> {

    private final AttributeWidget leafAttributeWidget;

    public CMLeafNode(AttributeWidget leafAttributeWidget, ClassMembers classMembers, CheckableAttributeNode checkableNode) {
        super(leafAttributeWidget.getModelerScene(), classMembers, checkableNode);
        this.leafAttributeWidget = leafAttributeWidget;
    }

    public void init() {
        getCheckableNode().setEnableWithParent(true);
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
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
            return String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            return String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
    }

    /**
     * @return the leafAttributeWidget
     */
    public AttributeWidget getLeafAttributeWidget() {
        return leafAttributeWidget;
    }

}
