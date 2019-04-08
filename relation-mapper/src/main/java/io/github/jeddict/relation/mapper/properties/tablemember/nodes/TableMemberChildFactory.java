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
package io.github.jeddict.relation.mapper.properties.tablemember.nodes;

import java.util.List;
import io.github.jeddict.relation.mapper.widget.column.ColumnWidget;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.relation.mapper.properties.tablemember.TableMembers;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;
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
