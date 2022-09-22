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
package io.github.jeddict.jpa.modeler.properties.classmember.nodes;

import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.RootNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import io.github.jeddict.jpa.spec.extend.JavaClass;

public class CMRootNode extends RootNode<ClassMembers> {


    private final JavaClassWidget<? extends JavaClass> classWidget;

    public CMRootNode(JavaClassWidget<? extends JavaClass> classWidget, ClassMembers classMembers, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(classMembers, childFactory, checkableNode);
        this.classWidget = classWidget;

   }

    public JavaClassWidget getRootWidget() {
        return classWidget;
    }

    @Override
    public void init() {
        JavaClass managedClass = classWidget.getBaseElementSpec();
        setDisplayName(managedClass.getClazz());
        setShortDescription(managedClass.getClazz());
        setIconBaseWithExtension(classWidget.getIconPath());
    }

}
