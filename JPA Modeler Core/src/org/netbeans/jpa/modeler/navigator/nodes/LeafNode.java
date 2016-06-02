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
package org.netbeans.jpa.modeler.navigator.nodes;

import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public abstract class LeafNode<T extends Object> extends PropertyNode implements TreeChildNode<T> {

    private final CheckableAttributeNode checkableNode;
    private final T baseElementSpec;
    private TreeParentNode parent;

    public LeafNode(IModelerScene modelerScene, T baseElementSpec, Children children, CheckableAttributeNode checkableNode) {
        super(modelerScene, children, Lookups.singleton(checkableNode));
        this.baseElementSpec = baseElementSpec;
        this.checkableNode = checkableNode;
        if (checkableNode != null) {
            checkableNode.setNode(this);
        }
    }


    @Override
    public void createPropertySet(ElementPropertySet set) {
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
}
