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
package io.github.jeddict.jpa.modeler.navigator.nodes;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public abstract class InternalNode<T extends Object> extends AbstractNode implements TreeParentNode<T>, TreeChildNode<T> {

    private CheckableAttributeNode checkableNode;
    private final T classMembers;

    private TreeParentNode<T> parent;
    private final List<TreeChildNode<T>> childList = new ArrayList<>();

    public InternalNode(T baseElementSpec, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(Children.create(childFactory, true), checkableNode==null?null:Lookups.singleton(checkableNode));
        this.checkableNode = checkableNode;
        this.classMembers = baseElementSpec;
        if (checkableNode != null) {
            checkableNode.setNode(this);
        }
        childFactory.setParentNode(this);
    }

    public InternalNode(T classMembers, TreeChildFactory childFactory) {
        this(classMembers, childFactory, null);
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
    public TreeParentNode<T> getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(TreeParentNode<T> parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(TreeChildNode<T> child) {
        getChildList().add(child);
    }

    @Override
    public void removeChild(TreeChildNode<T> child) {
        getChildList().remove(child);
    }

    /**
     * @return the childList
     */
    @Override
    public List<TreeChildNode<T>> getChildList() {
        return childList;
    }

    /**
     * @return the classMembers
     */
    @Override
    public T getBaseElementSpec() {
        return classMembers;
    }

    @Override
    public void refreshView() {
        fireIconChange();
    }
}
