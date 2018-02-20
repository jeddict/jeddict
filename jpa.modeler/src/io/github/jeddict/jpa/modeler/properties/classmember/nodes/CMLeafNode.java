/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.LeafNode;
import io.github.jeddict.jpa.spec.bean.AssociationAttribute;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;

public class CMLeafNode extends LeafNode<ClassMembers> {

    private final AttributeWidget leafAttributeWidget;

    public CMLeafNode(AttributeWidget leafAttributeWidget, ClassMembers classMembers, CheckableAttributeNode checkableNode) {
        super(leafAttributeWidget.getModelerScene(), classMembers, checkableNode);
        this.leafAttributeWidget = leafAttributeWidget;
    }

    @Override
    public void init() {
        getCheckableNode().setEnableWithParent(true);
        this.setIconBaseWithExtension(leafAttributeWidget.getIconPath());

        Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
        if(attribute instanceof BaseAttribute){
            this.setShortDescription(attribute.getName() + " <" + ((BaseAttribute) attribute).getAttributeType() + ">");
        } else if (attribute instanceof RelationAttribute) {
            this.setShortDescription(attribute.getName() + " <" + ((RelationAttribute) attribute).getTargetEntity() + ">");
        } else if (attribute instanceof AssociationAttribute) {
            this.setShortDescription(attribute.getName() + " <" + ((AssociationAttribute) attribute).getTargetClass()+ ">");
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
