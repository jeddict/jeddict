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
package io.github.jeddict.jpa.modeler.navigator.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

//       CMChildFactory
//             |
//             |
//             |
//             |
//           CMNode
//         /   |  \
//        /    |   \
//       /     |    \
//      /      |     \
//     /       |      \
//  Widget   Graph    CheckableAttributeNode
public abstract class RootNode<T extends Object> extends AbstractNode implements TreeParentNode<T> {

    private final CheckableAttributeNode checkableNode;
    private final List<TreeChildNode<T>> childList = new ArrayList<>();
    private final T baseElementSpec;

    public RootNode(T baseElementSpec, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(Children.create(childFactory, true), checkableNode == null ? null : Lookups.singleton(checkableNode));
        this.checkableNode = checkableNode;
        this.baseElementSpec = baseElementSpec;
        if (checkableNode != null) {
            checkableNode.setNode(this);
        }
        childFactory.setParentNode(this);
    }

    @Override
    public void addChild(TreeChildNode child) {
        childList.add(child);
    }

    @Override
    public void removeChild(TreeChildNode child) {
        childList.remove(child);
    }

    @Override
    public CheckableAttributeNode getCheckableNode() {
        return checkableNode;
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
        return baseElementSpec;
    }

    @Override
    public void refreshView() {
        fireIconChange();
    }
    
    @Override
    public Action[] getActions(boolean context) {
          return new Action[0];
    }

}
