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
package io.github.jeddict.jpa.modeler.properties.order.column;

import javax.swing.JOptionPane;
import io.github.jeddict.jpa.spec.OrderColumn;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;

public class OrderColumnPanel extends GenericEmbeddedEditor<OrderColumn> {
    
    private OrderColumn orderColumn;

    public OrderColumnPanel() {
    }

    @Override
    public void init() {
        initComponents();
        setLoaded();
    }
    
    @Override
    public void setValue(OrderColumn orderColumn) {
        this.orderColumn = orderColumn;
        name_TextField.setText(orderColumn.getName());
        columnDefinition_TextArea.setText(orderColumn.getColumnDefinition());
        nullable_CheckBox.setSelected(orderColumn.isNullable());
        insertable_CheckBox.setSelected(orderColumn.isInsertable());
        updatable_CheckBox.setSelected(orderColumn.isUpdatable());
    }
    
    @Override
    public OrderColumn getValue() {
        if (!validateField()) {
            throw new IllegalStateException();
        }
        orderColumn.setName(name_TextField.getText());
        orderColumn.setColumnDefinition(columnDefinition_TextArea.getText());
        orderColumn.setNullable(nullable_CheckBox.isSelected());
        orderColumn.setInsertable(insertable_CheckBox.isSelected());
        orderColumn.setUpdatable(updatable_CheckBox.isSelected());
        return orderColumn;
    }
    
      private boolean validateField() {
        if (this.name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Parameter column name can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orderColumnPanel = new javax.swing.JLayeredPane();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        columnDefinition_LayeredPane = new javax.swing.JLayeredPane();
        columnDefinition_Label = new javax.swing.JLabel();
        columnDefinition_ScrollPane = new javax.swing.JScrollPane();
        columnDefinition_TextArea = new javax.swing.JTextArea();
        layeredPane = new javax.swing.JLayeredPane();
        padding = new javax.swing.JLabel();
        nullable_CheckBox = new javax.swing.JCheckBox();
        updatable_CheckBox = new javax.swing.JCheckBox();
        insertable_CheckBox = new javax.swing.JCheckBox();

        orderColumnPanel.setPreferredSize(new java.awt.Dimension(570, 137));

        name_LayeredPane.setToolTipText(org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.name_LayeredPane.toolTipText")); // NOI18N
        name_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        name_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.name_Label.text")); // NOI18N
        name_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        name_LayeredPane.add(name_Label, java.awt.BorderLayout.WEST);

        name_TextField.setText(org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.name_TextField.text")); // NOI18N
        name_TextField.setToolTipText(org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.name_TextField.toolTipText")); // NOI18N
        name_LayeredPane.add(name_TextField, java.awt.BorderLayout.CENTER);

        columnDefinition_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(columnDefinition_Label, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.columnDefinition_Label.text")); // NOI18N
        columnDefinition_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        columnDefinition_LayeredPane.add(columnDefinition_Label, java.awt.BorderLayout.WEST);

        columnDefinition_TextArea.setColumns(20);
        columnDefinition_TextArea.setRows(5);
        columnDefinition_ScrollPane.setViewportView(columnDefinition_TextArea);

        columnDefinition_LayeredPane.add(columnDefinition_ScrollPane, java.awt.BorderLayout.CENTER);

        layeredPane.setLayout(new java.awt.GridLayout(1, 4));

        org.openide.awt.Mnemonics.setLocalizedText(padding, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.padding.text")); // NOI18N
        layeredPane.add(padding);

        nullable_CheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(nullable_CheckBox, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.nullable_CheckBox.text")); // NOI18N
        layeredPane.add(nullable_CheckBox);

        updatable_CheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(updatable_CheckBox, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.updatable_CheckBox.text")); // NOI18N
        layeredPane.add(updatable_CheckBox);

        insertable_CheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(insertable_CheckBox, org.openide.util.NbBundle.getMessage(OrderColumnPanel.class, "OrderColumnPanel.insertable_CheckBox.text")); // NOI18N
        layeredPane.add(insertable_CheckBox);

        orderColumnPanel.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        orderColumnPanel.setLayer(columnDefinition_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        orderColumnPanel.setLayer(layeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout orderColumnPanelLayout = new javax.swing.GroupLayout(orderColumnPanel);
        orderColumnPanel.setLayout(orderColumnPanelLayout);
        orderColumnPanelLayout.setHorizontalGroup(
            orderColumnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderColumnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(orderColumnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(name_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(columnDefinition_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addComponent(layeredPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        orderColumnPanelLayout.setVerticalGroup(
            orderColumnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderColumnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(columnDefinition_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(orderColumnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(orderColumnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel columnDefinition_Label;
    private javax.swing.JLayeredPane columnDefinition_LayeredPane;
    private javax.swing.JScrollPane columnDefinition_ScrollPane;
    private javax.swing.JTextArea columnDefinition_TextArea;
    private javax.swing.JCheckBox insertable_CheckBox;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private javax.swing.JCheckBox nullable_CheckBox;
    private javax.swing.JLayeredPane orderColumnPanel;
    private javax.swing.JLabel padding;
    private javax.swing.JCheckBox updatable_CheckBox;
    // End of variables declaration//GEN-END:variables

}
