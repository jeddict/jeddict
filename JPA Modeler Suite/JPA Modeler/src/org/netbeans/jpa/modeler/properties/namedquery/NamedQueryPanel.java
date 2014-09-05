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
package org.netbeans.jpa.modeler.properties.namedquery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.spec.LockModeType;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;

public class NamedQueryPanel extends EntityComponent<NamedQuery> {

    private NamedQuery namedQuery;
    private NAttributeEntity attributeEntity;

    public NamedQueryPanel() {
        super("", true);
        initComponents();

    }

    @Override
    public void init() {
        lockModeType_jComboBox.removeAllItems();
        lockModeType_jComboBox.addItem(new ComboBoxValue(null, ""));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.NONE, "None"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.OPTIMISTIC, "Optimistic"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.OPTIMISTIC_FORCE_INCREMENT, "Optimistic Force Increment"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.PESSIMISTIC_FORCE_INCREMENT, "Pessimistic Force Increment"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.PESSIMISTIC_READ, "Pessimistic Read"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.PESSIMISTIC_WRITE, "Pessimistic Write"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.READ, "Read"));
        lockModeType_jComboBox.addItem(new ComboBoxValue(LockModeType.WRITE, "Write"));
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Named Query");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[4]));
        }
        namedQuery = null;
        name_TextField.setText("");
        query_TextArea.setText("");
        lockModeType_jComboBox.setSelectedIndex(0);

        initCustomNAttributeEditor();
        attributeEntity = getQueryHint();
        customNAttributeClientEditor.setAttributeEntity(attributeEntity);
    }

    @Override
    public void updateEntity(Entity<NamedQuery> entityValue) {
        this.setTitle("Update Named Query");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            namedQuery = (NamedQuery) row[0];
            name_TextField.setText(namedQuery.getName());
            query_TextArea.setText(namedQuery.getQuery());
            setLockModeType(namedQuery.getLockMode());
        }

        initCustomNAttributeEditor();
        attributeEntity = getQueryHint();
        customNAttributeClientEditor.setAttributeEntity(attributeEntity);
    }

    private void setLockModeType(LockModeType lockMode) {
        if (lockMode == null) {
            lockModeType_jComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < lockModeType_jComboBox.getItemCount(); i++) {
                if (((ComboBoxValue<LockModeType>) lockModeType_jComboBox.getItemAt(i)).getValue() == lockMode) {
                    lockModeType_jComboBox.setSelectedIndex(i);
                }
            }
        }
    }

    void initCustomNAttributeEditor() {
        queryHint_LayeredPane.removeAll();
        customNAttributeClientEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
                queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
                queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(queryHint_LayeredPaneLayout.createSequentialGroup()
                        .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
        );
        queryHint_LayeredPane.setLayer(customNAttributeClientEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_jLayeredPane = new javax.swing.JLayeredPane();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        query_LayeredPane = new javax.swing.JLayeredPane();
        query_Label = new javax.swing.JLabel();
        query_ScrollPane = new javax.swing.JScrollPane();
        query_TextArea = new javax.swing.JTextArea();
        lockModeType_LayeredPane = new javax.swing.JLayeredPane();
        lockModeType_Label = new javax.swing.JLabel();
        lockModeType_jComboBox = new javax.swing.JComboBox();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        queryHint_LayeredPane = new javax.swing.JLayeredPane();
        customNAttributeClientEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.name_Label.text")); // NOI18N

        name_TextField.setText(org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.name_TextField.text")); // NOI18N

        javax.swing.GroupLayout name_LayeredPaneLayout = new javax.swing.GroupLayout(name_LayeredPane);
        name_LayeredPane.setLayout(name_LayeredPaneLayout);
        name_LayeredPaneLayout.setHorizontalGroup(
            name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(name_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        name_LayeredPaneLayout.setVerticalGroup(
            name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name_Label)
                    .addComponent(name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        name_LayeredPane.setLayer(name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        name_LayeredPane.setLayer(name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(query_Label, org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.query_Label.text")); // NOI18N

        query_TextArea.setColumns(20);
        query_TextArea.setRows(5);
        query_ScrollPane.setViewportView(query_TextArea);

        javax.swing.GroupLayout query_LayeredPaneLayout = new javax.swing.GroupLayout(query_LayeredPane);
        query_LayeredPane.setLayout(query_LayeredPaneLayout);
        query_LayeredPaneLayout.setHorizontalGroup(
            query_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(query_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(query_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(query_ScrollPane)
                .addContainerGap())
        );
        query_LayeredPaneLayout.setVerticalGroup(
            query_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(query_LayeredPaneLayout.createSequentialGroup()
                .addGroup(query_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(query_Label)
                    .addComponent(query_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        query_LayeredPane.setLayer(query_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        query_LayeredPane.setLayer(query_ScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(lockModeType_Label, org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.lockModeType_Label.text")); // NOI18N

        javax.swing.GroupLayout lockModeType_LayeredPaneLayout = new javax.swing.GroupLayout(lockModeType_LayeredPane);
        lockModeType_LayeredPane.setLayout(lockModeType_LayeredPaneLayout);
        lockModeType_LayeredPaneLayout.setHorizontalGroup(
            lockModeType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockModeType_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lockModeType_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lockModeType_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        lockModeType_LayeredPaneLayout.setVerticalGroup(
            lockModeType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockModeType_LayeredPaneLayout.createSequentialGroup()
                .addGroup(lockModeType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lockModeType_Label)
                    .addComponent(lockModeType_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        lockModeType_LayeredPane.setLayer(lockModeType_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lockModeType_LayeredPane.setLayer(lockModeType_jComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);
        save_Button.setBounds(0, 0, 70, 23);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(80, 0, 70, 23);

        queryHint_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), org.openide.util.NbBundle.getMessage(NamedQueryPanel.class, "NamedQueryPanel.queryHint_LayeredPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        queryHint_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryHint_LayeredPaneLayout.createSequentialGroup()
                .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        queryHint_LayeredPane.setLayer(customNAttributeClientEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lockModeType_LayeredPane)
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(name_LayeredPane)
                            .addComponent(query_LayeredPane))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addContainerGap())
            .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(18, Short.MAX_VALUE)))
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(query_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lockModeType_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(231, 231, 231)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
            .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                    .addGap(205, 205, 205)
                    .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(121, Short.MAX_VALUE)))
        );
        root_jLayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(query_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(lockModeType_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(queryHint_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
        if (this.name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Name field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        if (this.query_TextArea.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Query field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        return true;
    }

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        if (!validateField()) {
            return;
        }
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                namedQuery = new NamedQuery();
            } else {
                namedQuery = (NamedQuery) row[0];
            }
        }

        namedQuery.setName(name_TextField.getText());
        namedQuery.setQuery(query_TextArea.getText());
        namedQuery.setLockMode(((ComboBoxValue<LockModeType>) lockModeType_jComboBox.getSelectedItem()).getValue());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = namedQuery;
            row[1] = namedQuery.getName();
            row[2] = namedQuery.getQuery();
            row[3] = namedQuery.getLockMode();
        }
        attributeEntity.getTableDataListener().setData(customNAttributeClientEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private NAttributeEntity getQueryHint() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("QueryHint", "Query Hint", "");
        attributeEntity.setCountDisplay(new String[]{"No QueryHints", "One QueryHint", " QueryHints"});
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Value", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new QueryHintPanel());
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<Object[]>();
            int count;

            @Override
            public void initCount() {
                if (namedQuery != null && namedQuery.getHint() != null) {
                    count = namedQuery.getHint().size();
                } else {
                    count = 0;
                }
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<Object[]>();
                if (namedQuery != null && namedQuery.getHint() != null) {
                    for (QueryHint queryHint : new CopyOnWriteArrayList<QueryHint>(namedQuery.getHint())) {
                        Object[] row = new Object[5];
                        row[0] = queryHint;
                        row[1] = queryHint.getName();
                        row[2] = queryHint.getValue();
                        data_local.add(row);
                    }
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                if (namedQuery != null && namedQuery.getHint() != null) {
                    namedQuery.getHint().clear();
                }
                for (Object[] row : data) {
                    QueryHint queryHint = (QueryHint) row[0];
                    namedQuery.getHint().add(queryHint);
                }
                initData();
            }
        });
        return attributeEntity;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private org.netbeans.modeler.properties.nentity.NEntityEditor customNAttributeClientEditor;
    private javax.swing.JLabel lockModeType_Label;
    private javax.swing.JLayeredPane lockModeType_LayeredPane;
    private javax.swing.JComboBox lockModeType_jComboBox;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private javax.swing.JLayeredPane queryHint_LayeredPane;
    private javax.swing.JLabel query_Label;
    private javax.swing.JLayeredPane query_LayeredPane;
    private javax.swing.JScrollPane query_ScrollPane;
    private javax.swing.JTextArea query_TextArea;
    private javax.swing.JLayeredPane root_jLayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables
}
