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
package io.github.jeddict.jpa.modeler.properties.entitygraph.nodes;

import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.InternalNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.NamedEntityGraph;
import io.github.jeddict.jpa.spec.NamedSubgraph;
import io.github.jeddict.jpa.spec.extend.Attribute;

public class EGInternalNode extends InternalNode<NamedEntityGraph> {

    private final PersistenceClassWidget parentWidget;//EmbeddableWidget
    private final AttributeWidget parentAttributeWidget; //EmbeddedAttributeWidget

    private NamedSubgraph subgraph;


    public EGInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, NamedEntityGraph namedEntityGraph, NamedSubgraph subgraph, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(namedEntityGraph, childFactory,checkableNode);
        this.parentWidget = parentWidget;
        this.parentAttributeWidget = parentAttributeWidget;
        this.subgraph = subgraph;
    }

    public EGInternalNode(PersistenceClassWidget parentWidget, AttributeWidget parentAttributeWidget, NamedEntityGraph namedEntityGraph, NamedSubgraph subgraph, TreeChildFactory childFactory) {
        this(parentWidget, parentAttributeWidget, namedEntityGraph, subgraph, childFactory, null);
    }

    @Override
    public void init() {
        this.setIconBaseWithExtension(parentAttributeWidget.getIconPath());

        Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
        ManagedClass managedClass = (ManagedClass) parentWidget.getBaseElementSpec();
        this.setDisplayName(attribute.getName());
        this.setShortDescription(attribute.getName() + " <" + managedClass.getName() + ">");
    }

    @Override
    public String getHtmlDisplayName() {
        Attribute attribute = (Attribute) parentAttributeWidget.getBaseElementSpec();
        String htmlDisplayName = attribute.getName() + " : " + attribute.getDataTypeLabel();
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
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
     * @return the parentAttributeWidget
     */
    public AttributeWidget getParentAttributeWidget() {
        return parentAttributeWidget;
    }


    /**
     * @return the subgraph
     */
    public NamedSubgraph getSubgraph() {
        return subgraph;
    }


}
