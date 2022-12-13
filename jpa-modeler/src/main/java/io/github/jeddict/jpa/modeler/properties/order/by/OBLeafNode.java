/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import java.util.List;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.LeafNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.actions.LeafNodeAction;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderTypeColumn;
import io.github.jeddict.jpa.spec.OrderBy;
import io.github.jeddict.jpa.spec.OrderType;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;

public class OBLeafNode extends LeafNode<OrderBy> implements OrderTypeColumn {

    private OrderType order;
    private final AttributeWidget leafAttributeWidget;

    public OBLeafNode(AttributeWidget leafAttributeWidget, OrderBy orderBy, CheckableAttributeNode checkableNode, List<Class<? extends LeafNodeAction>> actions) {
        super(leafAttributeWidget.getModelerScene(), orderBy, checkableNode, actions);
        this.leafAttributeWidget = leafAttributeWidget;
    }

    @Override
    public void init() {
        this.setIconBaseWithExtension(leafAttributeWidget.getIconPath());

        Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
        if (attribute instanceof BaseAttribute) {
            this.setShortDescription(attribute.getName() + " <" + ((BaseAttribute) attribute).getAttributeType() + ">");
        } else {
            this.setShortDescription(attribute.getName() + " <" + ((RelationAttribute) attribute).getTargetEntity() + ">");
        }
    }
    
    private String htmlDisplayName;

    @Override
    public String getDisplayName() {
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

    @Override
    public String getHtmlDisplayName() {
        String tab = "            @";  // \t or float:right not working :(
        String name = getDisplayName();
        String template = name.substring(0, name.length() - 7) + "%s" + name.substring(name.length() - 7);//"<font color='#BBBBBB' size='10px;' >%s</font>";
        if (order == OrderType.DESC) {
            name = String.format(template, tab + "DESC");
        } else {
            name = String.format(template, tab + "ASC");
        }
        return name;
    }

    /**
     * @return the order
     */
    @Override
    public OrderType getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    @Override
    public void setOrder(OrderType order) {
        this.order = order;
    }

    /**
     * @return the leafAttributeWidget
     */
    public AttributeWidget getLeafAttributeWidget() {
        return leafAttributeWidget;
    }

}
