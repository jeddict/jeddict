/**
 * Copyright [2016] Gaurav Gupta
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

import java.util.List;
import java.util.function.Predicate;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.openide.nodes.Node;

public class EntityManagerChildFactory extends TreeChildFactory<EntityMappings ,JavaClass> {

    private final Predicate<JavaClass> filterSelectable;

    public EntityManagerChildFactory(Predicate<JavaClass> filterSelectable) {
        this.filterSelectable = filterSelectable;
    }
    
    @Override
    protected boolean createKeys(List<JavaClass> javaClasses) {
        JPAModelerScene scene = null;
        if (parentNode instanceof EMRootNode) {
            scene = ((EMRootNode) parentNode).getRootWidget();
        }
        if (scene != null) {
            javaClasses.addAll(scene.getBaseElementSpec().getJavaClass());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(final JavaClass javaClass) {
        JPAModelerScene scene = null;
        if (parentNode instanceof EMRootNode) {
            scene = ((EMRootNode) parentNode).getRootWidget();
        }
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
        checkableNode.setSelected(filterSelectable.test(javaClass));

        EMLeafNode childNode = new EMLeafNode(javaClass, scene, parentNode.getBaseElementSpec(), checkableNode);
        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();
        return (Node) childNode;
    }

}
