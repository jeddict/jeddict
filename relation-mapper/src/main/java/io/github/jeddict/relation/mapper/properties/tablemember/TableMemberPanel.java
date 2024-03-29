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
package io.github.jeddict.relation.mapper.properties.tablemember;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.relation.mapper.properties.tablemember.nodes.TMLeafNode;
import io.github.jeddict.relation.mapper.properties.tablemember.nodes.TMRootNode;
import io.github.jeddict.relation.mapper.properties.tablemember.nodes.TableMemberChildFactory;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeChildNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeParentNode;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderTypeColumn;
import io.github.jeddict.jpa.spec.OrderType;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;

public class TableMemberPanel extends GenericEmbeddedEditor<TableMembers> implements ExplorerManager.Provider {

    private TableMemberChildFactory childFactory;
    private ExplorerManager manager;
    private final String title;

    private TableMembers tableMembers;
    private TableWidget<? extends DBTable> tableWidget;
    private TMRootNode node;

    public TableMemberPanel(String title, TableWidget<? extends DBTable> tableWidget) {
        this.tableWidget = tableWidget;
        this.title = title;
    }

    public TableMemberPanel(String title) {
        this.title = title;
    }

    @Override
    public void init() {
        manager = new ExplorerManager();
        initComponents();
    }

    @Override
    public void setValue(TableMembers tableMembers) {
        this.tableMembers = tableMembers;
        SwingUtilities.invokeLater(() -> {
            if (childFactory == null) {
                node = new TMRootNode(getTableWidget(), tableMembers, new TableMemberChildFactory(), new CheckableAttributeNode());
            } else {
                node = new TMRootNode(getTableWidget(), tableMembers, childFactory, new CheckableAttributeNode());
            }
            manager.setRootContext(node);
            node.init();
        });
    }

    public void setValue(Set<OrderbyItem> columns) {
        TableMembers tableMembers_tmp = new TableMembers();
        tableMembers_tmp.setColumns(columns);
        setValue(tableMembers_tmp);
    }

    public void setValue(List<String> columns) {
        TableMembers tableMembers_tmp = new TableMembers();
        Set<OrderbyItem> columnData = new LinkedHashSet<>();
        columns.forEach(c -> columnData.add(new OrderbyItem(c, OrderType.ASC)));
        tableMembers_tmp.setColumns(columnData);
        setValue(tableMembers_tmp);
    }

    @Override
    public TableMembers getValue() {
        tableMembers.getColumns().clear();
        loadTableMember(tableMembers, node);
        return tableMembers;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootLayeredPane = new javax.swing.JLayeredPane();
        outlineView = new OutlineView(getTitle());

        rootLayeredPane.setLayout(new java.awt.GridLayout(1, 0));

        outlineView.setToolTipText(org.openide.util.NbBundle.getMessage(TableMemberPanel.class, "TableMemberPanel.outlineView.toolTipText")); // NOI18N
        rootLayeredPane.add(outlineView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadTableMember(TableMembers tableMembers, TreeNode parentNode) {
        if (parentNode instanceof TreeParentNode) {
            for (TreeNode childNode : ((TreeParentNode<TableMembers>) parentNode).getChildList()) {
                loadColumnNode(tableMembers, childNode);
            }
        }

    }

    private void loadColumnNode(TableMembers tableMembers, TreeNode childNode) {
        if (childNode.getCheckableNode() == null || !childNode.getCheckableNode().isSelected() || !childNode.getCheckableNode().isCheckEnabled()) {
            return;
        }
        if (childNode instanceof TreeChildNode) {
            String column = (String) (((TMLeafNode) childNode).getLeafColumnWidget().getBaseElementSpec()).getName();
            if (childNode instanceof OrderTypeColumn) {
                tableMembers.addColumn(column, ((OrderTypeColumn) childNode).getOrder());
            } else {
                tableMembers.addColumn(column);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane outlineView;
    private javax.swing.JLayeredPane rootLayeredPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the tableWidget
     */
    public TableWidget<? extends DBTable> getTableWidget() {
        return tableWidget;
    }

    /**
     * @param tableWidget the tableWidget to set
     */
    public void setTableWidget(TableWidget<? extends DBTable> tableWidget) {
        this.tableWidget = tableWidget;
    }

    /**
     * @return the childFactory
     */
    public TableMemberChildFactory getChildFactory() {
        return childFactory;
    }

    /**
     * @param childFactory the childFactory to set
     */
    public void setChildFactory(TableMemberChildFactory childFactory) {
        this.childFactory = childFactory;
    }
}
