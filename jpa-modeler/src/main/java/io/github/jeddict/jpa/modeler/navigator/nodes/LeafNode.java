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
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import io.github.jeddict.jpa.modeler.navigator.nodes.actions.LeafNodeAction;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

public abstract class LeafNode<T extends Object> extends PropertyNode implements TreeChildNode<T> {

    private final CheckableAttributeNode checkableNode;
    private final T baseElementSpec;
    private TreeParentNode parent;
    private List<Class<? extends LeafNodeAction>> actions;
    private List<LeafNodeAction> actionInstances;

    public LeafNode(IModelerScene modelerScene, T baseElementSpec, CheckableAttributeNode checkableNode,List<Class<? extends LeafNodeAction>> actions) {
        super(modelerScene, Children.LEAF, checkableNode == null ? null : Lookups.singleton(checkableNode));
        this.baseElementSpec = baseElementSpec;
        this.checkableNode = checkableNode;
        if (checkableNode != null) {
            checkableNode.setNode(this);
        }
        this.actions=actions;
    }
    
    public LeafNode(IModelerScene modelerScene, T baseElementSpec, CheckableAttributeNode checkableNode) {
        this(modelerScene,baseElementSpec,checkableNode, Collections.EMPTY_LIST);
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
    
    @Override
    public Action[] getActions(boolean context) {
        synchronized(this){
        if (!context) {
            if (actions != null && actionInstances == null) {
                actionInstances = new ArrayList<>();
                actions.forEach((actionClass) -> {
                    try {
                        
                        LeafNodeAction action = actionClass.newInstance();
                        action.setNode(this);
                        actionInstances.add(action);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
            }
            if (actionInstances != null) {
                return actionInstances.toArray(new Action[0]);
            }
        }
        }
        return new Action[0];
    }
}
