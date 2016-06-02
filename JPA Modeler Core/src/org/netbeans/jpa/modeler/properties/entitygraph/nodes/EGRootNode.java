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
package org.netbeans.jpa.modeler.properties.entitygraph.nodes;

import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.navigator.nodes.RootNode;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;

public class EGRootNode extends RootNode<NamedEntityGraph> {

    private final EntityWidget entityWidget;

    public EGRootNode(EntityWidget entityWidget, NamedEntityGraph namedEntityGraph, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(namedEntityGraph, childFactory, checkableNode);
        this.entityWidget = entityWidget;

        if (checkableNode != null) {
            checkableNode.setSelected(true);
        }

     }

    public EGRootNode(EntityWidget entityWidget, NamedEntityGraph namedEntityGraph, TreeChildFactory childFactory) {
        this(entityWidget, namedEntityGraph, childFactory, null);
    }

    public EntityWidget getRootWidget() {
        return entityWidget;
    }

    @Override
    public void init() {
       Entity entity = entityWidget.getBaseElementSpec();
        setDisplayName(entity.getClazz());
        setShortDescription(entity.getClazz());
        setIconBaseWithExtension(entityWidget.getIconPath());
    }
}