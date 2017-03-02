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
package org.netbeans.jpa.modeler.properties.entitygraph;

import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.properties.entitygraph.nodes.EGInternalNode;
import org.netbeans.jpa.modeler.properties.entitygraph.nodes.EGLeafNode;
import org.netbeans.jpa.modeler.properties.entitygraph.nodes.EGRootNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildNode;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.NamedAttributeNode;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedSubgraph;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.openide.nodes.Node;

public class ExecutionEGChildFactory extends TreeChildFactory<NamedEntityGraph, AttributeWidget> {

    private final boolean loadGraph;

    public ExecutionEGChildFactory(boolean loadGraph) {
        this.loadGraph = loadGraph;
    }

    @Override
    protected boolean createKeys(List<AttributeWidget> attributeWidgets) {

        PersistenceClassWidget<? extends ManagedClass> classWidget = null;

        if (parentNode instanceof EGRootNode) {
            classWidget = ((EGRootNode) parentNode).getRootWidget();
        } else if (parentNode instanceof EGInternalNode) {
            classWidget = ((EGInternalNode) parentNode).getParentWidget();
        }

        if (classWidget != null) {
            for (AttributeWidget attributeWidget : classWidget.getAllAttributeWidgets()) {
                if (attributeWidget instanceof IdAttributeWidget || attributeWidget instanceof EmbeddedIdAttributeWidget || attributeWidget instanceof EmbeddedAttributeWidget || attributeWidget instanceof VersionAttributeWidget) {
                    attributeWidgets.add(attributeWidget);
                } else if (attributeWidget instanceof TransientAttributeWidget) {
                    // skip
                } else { //check for all remaining
                    Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();

                    NamedAttributeNode namedAttributeNode = null;
                    if (parentNode instanceof EGRootNode) {
                        namedAttributeNode = parentNode.getBaseElementSpec().findNamedAttributeNode(attribute.getName());
                    } else if (parentNode instanceof EGInternalNode && ((EGInternalNode) parentNode).getSubgraph() != null) {//if sub graph not exist for relation node
                        namedAttributeNode = ((EGInternalNode) parentNode).getSubgraph().findNamedAttributeNode(attribute.getName());
                    }

                    if (namedAttributeNode != null) {
                        attributeWidgets.add(attributeWidget);
                    } else if (loadGraph && attribute instanceof FetchTypeHandler) {
                        FetchType fetch = ((FetchTypeHandler) attribute).getFetch();
                        if (fetch != null) {
                            if (fetch == FetchType.EAGER) {
                                attributeWidgets.add(attributeWidget);
                            }
                        } else if (attribute instanceof Basic || attribute instanceof OneToOne || attribute instanceof ManyToOne) {
                            attributeWidgets.add(attributeWidget);
                        }

                    }
                }
            }
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(final AttributeWidget attributeWidget) {
        TreeChildNode childNode;
        NamedSubgraph subgraph = null;

        Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();
        NamedAttributeNode namedAttributeNode = null;
        if (parentNode instanceof EGRootNode) {
            namedAttributeNode = parentNode.getBaseElementSpec().findNamedAttributeNode(attribute.getName());
        } else if (parentNode instanceof EGInternalNode && ((EGInternalNode) parentNode).getSubgraph() != null) {
            namedAttributeNode = ((EGInternalNode) parentNode).getSubgraph().findNamedAttributeNode(attribute.getName());
        }

        if (namedAttributeNode != null) {
            if (namedAttributeNode.getSubgraph() != null && !namedAttributeNode.getSubgraph().isEmpty()) {
                subgraph = parentNode.getBaseElementSpec().findSubgraph(namedAttributeNode.getSubgraph());
            }
        }

        if (attributeWidget instanceof EmbeddedAttributeWidget) {
            EmbeddedAttributeWidget embeddedAttributeWidget = (EmbeddedAttributeWidget) attributeWidget;
            EmbeddableWidget embeddableWidget = embeddedAttributeWidget.getEmbeddableFlowWidget().getTargetEmbeddableWidget();
            ExecutionEGChildFactory childFactory = new ExecutionEGChildFactory(loadGraph);//parentWidget, embeddedAttributeWidget, embeddableWidget );
            childNode = new EGInternalNode(embeddableWidget, embeddedAttributeWidget, parentNode.getBaseElementSpec(), subgraph, childFactory);
        } else if (attributeWidget instanceof RelationAttributeWidget) {
            RelationAttributeWidget relationAttributeWidget = (RelationAttributeWidget) attributeWidget;
            IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
            EntityWidget targetEntityWidget = null;
            if (targetElementWidget instanceof EntityWidget) {
                targetEntityWidget = (EntityWidget) targetElementWidget;
            } else if (targetElementWidget instanceof RelationAttributeWidget) {
                RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
            }

            ExecutionEGChildFactory childFactory = new ExecutionEGChildFactory(loadGraph);//parentWidget, relationAttributeWidget, targetEntityWidget);
            childNode = new EGInternalNode(targetEntityWidget, relationAttributeWidget, parentNode.getBaseElementSpec(), subgraph, childFactory);
        } else {
            childNode = new EGLeafNode(attributeWidget, parentNode.getBaseElementSpec());
        }
        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();

        return (Node) childNode;
    }

}
