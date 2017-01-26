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
package org.netbeans.db.modeler.properties.index;

import org.netbeans.db.modeler.properties.tablemember.nodes.*;
import java.util.List;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.actions.LeafNodeAction;
import org.netbeans.jpa.modeler.spec.OrderType;
import org.netbeans.db.modeler.properties.tablemember.TableMembers;
import org.netbeans.jpa.modeler.properties.order.type.OrderTypeColumn;

public class IndexNode extends TMLeafNode implements OrderTypeColumn {

    private OrderType order;

    public IndexNode(ColumnWidget leafAttributeWidget, TableMembers tableMembers, CheckableAttributeNode checkableNode, List<Class<? extends LeafNodeAction>> actions) {
        super(leafAttributeWidget, tableMembers, checkableNode, actions);
    }
    
    
    @Override
    public String getHtmlDisplayName() {
        String tab = "            @" ;  // \t or float:right not working :(
        String name = super.getHtmlDisplayName();
        String template = name.substring(0, name.length()-7) +"%s"+ name.substring(name.length()-7);//"<font color='#BBBBBB' size='10px;' >%s</font>";
        if(order == OrderType.DESC){
            name = String.format(template, tab + "DESC") ;
        } else {
            name = String.format(template, tab + "ASC") ;
        }
        return name;
    }

    /**
     * @return the order
     */
    public OrderType getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(OrderType order) {
        this.order = order;
    }

}
