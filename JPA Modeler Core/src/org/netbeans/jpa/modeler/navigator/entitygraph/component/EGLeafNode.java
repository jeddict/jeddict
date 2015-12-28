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
package org.netbeans.jpa.modeler.navigator.entitygraph.component;

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.navigator.entitygraph.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGChildNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGParentNode;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class EGLeafNode extends PropertyNode implements EGChildNode {

    private CheckableAttributeNode checkableNode;
    private final AttributeWidget leafAttributeWidget;
    private NamedEntityGraph namedEntityGraph;
    private EGParentNode parent;

    public EGLeafNode(AttributeWidget leafAttributeWidget, NamedEntityGraph namedEntityGraph, Children children, CheckableAttributeNode checkableNode) {
        super(leafAttributeWidget.getModelerScene(), children, Lookups.singleton(checkableNode));
        this.leafAttributeWidget = leafAttributeWidget;
        this.namedEntityGraph = namedEntityGraph;
        this.checkableNode = checkableNode;
        checkableNode.setNode(this);
        init();
    }

    public EGLeafNode(AttributeWidget leafAttributeWidget, NamedEntityGraph namedEntityGraph, Children children) {
        super(leafAttributeWidget.getModelerScene(), children);
        this.leafAttributeWidget = leafAttributeWidget;
        this.namedEntityGraph = namedEntityGraph;
        init();
    }

    private void init() {
        if (leafAttributeWidget instanceof IdAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.ID_ATTRIBUTE_ICON_PATH);
        } else if (leafAttributeWidget instanceof EmbeddedIdAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ID_ATTRIBUTE_ICON_PATH);
        } else if (leafAttributeWidget instanceof BasicAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.BASIC_ATTRIBUTE_ICON_PATH);
        } else if (leafAttributeWidget instanceof BasicCollectionAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON_PATH);
        }else if (leafAttributeWidget instanceof VersionAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.VERSION_ATTRIBUTE_ICON_PATH);
        }else if (leafAttributeWidget instanceof TransientAttributeWidget) {
            this.setIconBaseWithExtension(JPAModelerUtil.TRANSIENT_ATTRIBUTE_ICON_PATH);
        }

        BaseAttribute attribute = (BaseAttribute) leafAttributeWidget.getBaseElementSpec();
        this.setDisplayName(attribute.getName());
        this.setShortDescription(attribute.getName() + " <" + attribute.getAttributeType() + ">");

    }

    @Override
    public String getHtmlDisplayName() {
        Attribute attribute = (Attribute) leafAttributeWidget.getBaseElementSpec();
        String htmlDisplayName = attribute.getName(); //NOI18N
         if (checkableNode!=null && !checkableNode.isSelected()) {
            htmlDisplayName = String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            htmlDisplayName = String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        } 
        return htmlDisplayName;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
//        if (entityWidget.getBaseElementSpec() instanceof AttributeOverrideHandler) {
//            Attribute attributeSpec = (Attribute) leafAttributeWidget.getBaseElementSpec();
//            AttributeOverrideHandler attributeOverrideHandler = (AttributeOverrideHandler) entityWidget.getBaseElementSpec();
//            AttributeOverride attributeOverride = attributeOverrideHandler.getAttributeOverride(attributeSpec.getName());
//            set.createPropertySet(leafAttributeWidget, attributeOverride.getColumn(), leafAttributeWidget.getPropertyChangeListeners(), leafAttributeWidget.getPropertyVisibilityHandlers());
//        }
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
    public EGParentNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(EGParentNode parent) {
        this.parent = parent;
    }

    /**
     * @return the leafAttributeWidget
     */
    public AttributeWidget getLeafAttributeWidget() {
        return leafAttributeWidget;
    }

    /**
     * @return the namedEntityGraph
     */
    @Override
    public NamedEntityGraph getNamedEntityGraph() {
        return namedEntityGraph;
    }

    @Override
    public void refreshView() {
        fireIconChange();
    }
}
