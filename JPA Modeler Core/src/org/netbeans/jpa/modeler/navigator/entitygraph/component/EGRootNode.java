/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.navigator.entitygraph.component;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.navigator.entitygraph.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.EGChildFactory;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGChildNode;
import org.netbeans.jpa.modeler.navigator.entitygraph.component.spec.EGParentNode;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

//       EGChildFactory
//             |
//             | 
//             |
//             |
//           EGNode
//         /   |  \
//        /    |   \
//       /     |    \
//      /      |     \
//     /       |      \
//  Widget   Graph    CheckableAttributeNode  
    
public class EGRootNode extends AbstractNode implements EGParentNode {

    private CheckableAttributeNode checkableNode;
            
    private final EntityWidget widget;
    private final NamedEntityGraph namedEntityGraph;

    private final List<EGChildNode> childList = new ArrayList<>();

    public EGRootNode(EntityWidget entityWidget,NamedEntityGraph namedEntityGraph, EGChildFactory childFactory,CheckableAttributeNode checkableNode) {
        super(Children.create(childFactory, true), Lookups.singleton(checkableNode));
        this.widget = entityWidget;
        this.checkableNode = checkableNode;
        this.namedEntityGraph = namedEntityGraph;
        checkableNode.setNode(this);
        childFactory.setParentNode(this);
        
        checkableNode.setSelected(true);
        
        Entity entity = entityWidget.getBaseElementSpec();
        setDisplayName(entity.getClazz());
        setShortDescription(entity.getClazz());
        setIconBaseWithExtension(JPAModelerUtil.ENTITY_ICON_PATH);
    }
    

    public EGRootNode(EntityWidget entityWidget,NamedEntityGraph namedEntityGraph, EGChildFactory childFactory) {
        super(Children.create(childFactory, true));
        this.widget = entityWidget;
        this.namedEntityGraph = namedEntityGraph;
        childFactory.setParentNode(this);
        
        Entity entity = entityWidget.getBaseElementSpec();
        setDisplayName(entity.getClazz());
        setShortDescription(entity.getClazz());
        setIconBaseWithExtension(JPAModelerUtil.ENTITY_ICON_PATH);
    }

    public EntityWidget getRootWidget() {
        return widget;
    }

    @Override
    public void addChild(EGChildNode child) {
        childList.add(child);
    }

    @Override
    public void removeChild(EGChildNode child) {
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
    public List<EGChildNode> getChildList() {
        return childList;
    }

    /**
     * @return the namedEntityGraph
     */
    @Override
    public NamedEntityGraph getNamedEntityGraph() {
        return namedEntityGraph;
    }

    @Override
    public void refreshView() {
       fireIconChange();
    }
}
