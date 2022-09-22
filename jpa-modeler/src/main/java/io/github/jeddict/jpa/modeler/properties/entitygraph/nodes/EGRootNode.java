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

import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.RootNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.NamedEntityGraph;

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