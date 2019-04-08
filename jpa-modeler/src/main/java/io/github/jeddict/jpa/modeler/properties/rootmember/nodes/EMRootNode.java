/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.properties.rootmember.nodes;

import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.RootNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;

public class EMRootNode extends RootNode<EntityMappings> {

    private final JPAModelerScene scene;

    public EMRootNode(JPAModelerScene scene, EntityMappings entityMappings, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(entityMappings, childFactory, checkableNode);
        this.scene = scene;
    }

    public JPAModelerScene getRootWidget() {
        return scene;
    }

    @Override
    public void init() {
        EntityMappings entityMappings = scene.getBaseElementSpec();
        setDisplayName(entityMappings.getPackage());
        setShortDescription(entityMappings.getPackage());
        setIconBaseWithExtension(JPAModelerUtil.PACKAGE_ICON_PATH);
    }

}
