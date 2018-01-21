/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.rootmember.nodes;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.RootNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;

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
