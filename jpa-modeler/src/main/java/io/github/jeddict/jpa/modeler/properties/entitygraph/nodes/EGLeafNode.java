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
package io.github.jeddict.jpa.modeler.properties.entitygraph.nodes;

import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.LeafNode;
import io.github.jeddict.jpa.spec.NamedEntityGraph;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class EGLeafNode extends LeafNode<NamedEntityGraph> {

    private final AttributeWidget leafAttributeWidget;

    public EGLeafNode(AttributeWidget leafAttributeWidget, NamedEntityGraph namedEntityGraph, CheckableAttributeNode checkableNode) {
        super(leafAttributeWidget.getModelerScene(), namedEntityGraph, checkableNode);
        this.leafAttributeWidget = leafAttributeWidget;
    }

    public EGLeafNode(AttributeWidget leafAttributeWidget, NamedEntityGraph namedEntityGraph) {
        this(leafAttributeWidget, namedEntityGraph, null);
    }

    @Override
    public void init() {
        this.setIconBaseWithExtension(leafAttributeWidget.getIconPath());
        BaseAttribute attribute = (BaseAttribute) leafAttributeWidget.getBaseElementSpec();
        this.setDisplayName(attribute.getName());
        this.setShortDescription(attribute.getName() + " <" + attribute.getAttributeType() + ">");

    }

    @Override
    public String getHtmlDisplayName() {
        Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
        String htmlDisplayName = attribute.getName() + " : " + attribute.getDataTypeLabel();
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
            htmlDisplayName = String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            htmlDisplayName = String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
        return htmlDisplayName;
    }

    /**
     * @return the leafAttributeWidget
     */
    public AttributeWidget getLeafAttributeWidget() {
        return leafAttributeWidget;
    }

}
