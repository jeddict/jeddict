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
package io.github.jeddict.relation.mapper.properties.tablemember.nodes;

import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.relation.mapper.properties.tablemember.TableMembers;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.RootNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildFactory;

public class TMRootNode extends RootNode<TableMembers> {


    private final TableWidget<? extends DBTable> tableWidget;

    public TMRootNode(TableWidget<? extends DBTable> tableWidget, TableMembers tableMembers, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(tableMembers, childFactory, checkableNode);
        this.tableWidget = tableWidget;

   }

    public TableWidget<? extends DBTable> getRootWidget() {
        return tableWidget;
    }

    @Override
    public void init() {
        DBTable table = tableWidget.getBaseElementSpec();
        setDisplayName(table.getName());
        setShortDescription(table.getName());
        setIconBaseWithExtension(tableWidget.getIconPath());
    }

}
