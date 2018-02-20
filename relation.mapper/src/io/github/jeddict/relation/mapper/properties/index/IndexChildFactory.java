/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.properties.index;

import io.github.jeddict.relation.mapper.properties.tablemember.nodes.TMRootNode;
import io.github.jeddict.relation.mapper.properties.tablemember.nodes.TableMemberChildFactory;
import java.util.Collections;
import io.github.jeddict.relation.mapper.widget.column.ColumnWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderAction;
import org.openide.nodes.Node;


public class IndexChildFactory extends TableMemberChildFactory {

    @Override
    protected Node createNodeForKey(final ColumnWidget columnWidget) {
        IndexNode childNode;
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
        if (parentNode.getBaseElementSpec() != null) {
            if (parentNode instanceof TMRootNode) {
                checkableNode.setSelected(parentNode.getBaseElementSpec().isExist(columnWidget.getName()));
            }
        }

        childNode = new IndexNode(columnWidget, parentNode.getBaseElementSpec(), checkableNode ,Collections.singletonList(OrderAction.class));
        if(checkableNode.isSelected()){
            childNode.setOrder(parentNode.getBaseElementSpec().findColumn(columnWidget.getName()).get().getOrderType());
        }
        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();
       
        return (Node) childNode;
    }

  
}
