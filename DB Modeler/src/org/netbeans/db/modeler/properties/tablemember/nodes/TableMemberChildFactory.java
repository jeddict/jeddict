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
package org.netbeans.db.modeler.properties.tablemember.nodes;

import java.util.List;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.core.widget.table.TableWidget;
import org.netbeans.db.modeler.properties.tablemember.TableMembers;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.openide.nodes.Node;

public class TableMemberChildFactory extends TreeChildFactory<TableMembers, ColumnWidget> {

    @Override
    protected boolean createKeys(List<ColumnWidget> attributeWidgets) {
        TableWidget<? extends DBTable> tableWidget = null;
        if (parentNode instanceof TMRootNode) {
            tableWidget = ((TMRootNode) parentNode).getRootWidget();
        } 
        if (tableWidget != null) {
            attributeWidgets.addAll(tableWidget.getPrimaryKeyColumnWidgets());
            attributeWidgets.addAll(tableWidget.getColumnWidgets());
            attributeWidgets.addAll(tableWidget.getForeignKeyWidgets());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(final ColumnWidget columnWidget) {
        TMLeafNode childNode;
        CheckableAttributeNode checkableNode = new CheckableAttributeNode();
        if (parentNode.getBaseElementSpec() != null) {
            if (parentNode instanceof TMRootNode) {
                checkableNode.setSelected(parentNode.getBaseElementSpec().isExist(columnWidget.getName()));
            }
        }

        childNode = new TMLeafNode(columnWidget, parentNode.getBaseElementSpec(), checkableNode, null);

        childNode.setParent(parentNode);
        parentNode.addChild(childNode);
        childNode.init();

        return (Node) childNode;
    }

  
}
