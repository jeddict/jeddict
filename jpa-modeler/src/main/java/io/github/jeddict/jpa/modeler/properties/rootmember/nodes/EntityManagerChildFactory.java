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
package io.github.jeddict.jpa.modeler.properties.rootmember.nodes;

import java.util.List;
import java.util.function.Predicate;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import java.util.Comparator;
import org.netbeans.modeler.specification.model.document.IModelerScene;
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
            List<JavaClass> javaClassList = scene.getBaseElementSpec().getJavaClass();
            javaClassList.sort(Comparator.comparing(JavaClass::getName));
            javaClasses.addAll(javaClassList);
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
