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
package io.github.jeddict.jpa.modeler.properties.order.by;

import static java.util.Collections.singletonList;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.BasicCollectionAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MultiRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.TreeNode;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderAction;
import io.github.jeddict.jpa.modeler.properties.order.type.OrderTypeColumn;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.OrderBy;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node;

public class OrderByPanel extends GenericEmbeddedEditor<OrderBy> implements ExplorerManager.Provider {
    
    private ExplorerManager manager;
    private OrderBy orderBy;
    private PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget;
    private AttributeWidget rootAttributeWidget;
    private TreeNode<OrderBy> node;

    public OrderByPanel(AttributeWidget rootAttributeWidget) {
        this.rootAttributeWidget = rootAttributeWidget;
        if (rootAttributeWidget instanceof MultiRelationAttributeWidget) {
            persistenceClassWidget = ((MultiRelationAttributeWidget) rootAttributeWidget).getRelationFlowWidget().getTargetEntityWidget();
        } else if (rootAttributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
            persistenceClassWidget = ((MultiValueEmbeddedAttributeWidget) rootAttributeWidget).getEmbeddableFlowWidget().getTargetEmbeddableWidget();
        } else if (rootAttributeWidget instanceof BasicCollectionAttributeWidget) {
            persistenceClassWidget = null;
        }
    }
    
    

    @Override
    public void init() {
        manager = new ExplorerManager();
        initComponents();
        setLoaded();
    }
    
    @Override
    public void setValue(OrderBy orderBy) {
        this.orderBy = orderBy;
        invokeLater(() -> {
            if (persistenceClassWidget != null) {
                node = new OBRootNode(persistenceClassWidget, orderBy, new OrderByChildFactory(), new CheckableAttributeNode());
            } else { //ElementCollection Basic type
                node = new OBLeafNode(rootAttributeWidget, orderBy, null, singletonList(OrderAction.class));
            }
            manager.setRootContext((Node)node);
            node.init();
        });

    }
    
    @Override
    public OrderBy getValue() {
        orderBy.getAttributes().clear();
        loadClassMember(orderBy);
        return orderBy;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootLayeredPane = new javax.swing.JLayeredPane();
        outlineView = new OutlineView("@OrderBy");

        outlineView.setToolTipText(org.openide.util.NbBundle.getMessage(OrderByPanel.class, "OrderByPanel.outlineView.toolTipText")); // NOI18N

        rootLayeredPane.setLayer(outlineView, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout rootLayeredPaneLayout = new javax.swing.GroupLayout(rootLayeredPane);
        rootLayeredPane.setLayout(rootLayeredPaneLayout);
        rootLayeredPaneLayout.setHorizontalGroup(
            rootLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(outlineView, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        rootLayeredPaneLayout.setVerticalGroup(
            rootLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(outlineView, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane)
        );
    }// </editor-fold>//GEN-END:initComponents
        
    private void loadClassMember(OrderBy orderBy) {
        if (node instanceof OBRootNode) {
            for (TreeNode childNode : ((OBRootNode)node).getChildList()) {
                loadAttributeNode(orderBy, null, childNode);
            }
        } else if (node instanceof OBLeafNode) {
                orderBy.addAttribute(new OrderbyItem(null, ((OBLeafNode)node).getOrder()));
        }
        
    }
    
    private void loadAttributeNode(OrderBy orderBy, String propertyPath, TreeNode childNode) {
        if (childNode.getCheckableNode() == null || !childNode.getCheckableNode().isSelected() || !childNode.getCheckableNode().isCheckEnabled()) {
            return;
        }
        if (childNode instanceof OBInternalNode) {
            String nextPropertyPath = ((OBInternalNode) childNode).getPropertyPath();
            for (TreeNode subChildNode : ((OBInternalNode) childNode).getChildList()) {
                loadAttributeNode(orderBy, nextPropertyPath, subChildNode);
            }
        } else if (childNode instanceof OBLeafNode) {
            Attribute attribute = ((Attribute) (((OBLeafNode) childNode).getLeafAttributeWidget().getBaseElementSpec()));
            String finalPropertyPath = (propertyPath!=null?propertyPath+'.':EMPTY) + attribute.getName();
            if (childNode instanceof OrderTypeColumn) {
                orderBy.addAttribute(new OrderbyItem(finalPropertyPath, ((OrderTypeColumn) childNode).getOrder()));
            } else {
                orderBy.addAttribute(new OrderbyItem(finalPropertyPath, null));
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

}
