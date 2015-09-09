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
package org.netbeans.jpa.modeler.properties.named.resultsetmapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.netbeans.jpa.modeler.properties.named.query.QueryHintPanel;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityEditor;

public class ConstructorResultPanel extends EntityComponent<ConstructorResult> {
private final ModelerFile modelerFile;
private NAttributeEntity columnResultEntity;
private ConstructorResult constructorResult;

    public ConstructorResultPanel(ModelerFile modelerFile) {
        super("", true);
        this.modelerFile=modelerFile;
        initComponents();
    }

    @Override
    public void init() {
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Constructor Result");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[5]));
        }
        name_TextField.setText("");
        
        initColumnResultNAttributeEditor();
        columnResultEntity = getColumnResult();
        columnResultEditor.setAttributeEntity(columnResultEntity);
        
    }

    @Override
    public void updateEntity(Entity<ConstructorResult> entityValue) {
        this.setTitle("Update Constructor Result");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            constructorResult = (ConstructorResult) row[0];
            
            name_TextField.setText(constructorResult.getTargetClass());

        }
        initColumnResultNAttributeEditor();
        columnResultEntity = getColumnResult();
        columnResultEditor.setAttributeEntity(columnResultEntity);
        
    }

    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_LayeredPane = new javax.swing.JLayeredPane();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        columnResult_LayeredPane = new javax.swing.JLayeredPane();
        columnResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.name_Label.text_1")); // NOI18N

        name_TextField.setText(org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.name_TextField.text_1")); // NOI18N
        name_TextField.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.name_TextField.toolTipText_1")); // NOI18N
        name_TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                name_TextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout name_LayeredPaneLayout = new javax.swing.GroupLayout(name_LayeredPane);
        name_LayeredPane.setLayout(name_LayeredPaneLayout);
        name_LayeredPaneLayout.setHorizontalGroup(
            name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(name_LayeredPaneLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name_TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
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

        columnResult_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.columnResult_LayeredPane.border.title"))); // NOI18N
        columnResult_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout columnResult_LayeredPaneLayout = new javax.swing.GroupLayout(columnResult_LayeredPane);
        columnResult_LayeredPane.setLayout(columnResult_LayeredPaneLayout);
        columnResult_LayeredPaneLayout.setHorizontalGroup(
            columnResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(columnResultEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        columnResult_LayeredPaneLayout.setVerticalGroup(
            columnResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(columnResultEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );
        columnResult_LayeredPane.setLayer(columnResultEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);
        save_Button.setBounds(0, 0, 70, 23);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(80, 0, 70, 23);

        javax.swing.GroupLayout root_LayeredPaneLayout = new javax.swing.GroupLayout(root_LayeredPane);
        root_LayeredPane.setLayout(root_LayeredPaneLayout);
        root_LayeredPaneLayout.setHorizontalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name_LayeredPane)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(columnResult_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        root_LayeredPaneLayout.setVerticalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 372, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                    .addContainerGap(55, Short.MAX_VALUE)
                    .addComponent(columnResult_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(39, Short.MAX_VALUE)))
        );
        root_LayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(columnResult_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_LayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_LayeredPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
//        if (this.type_jComboBox.getSelectedItem().toString().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
//            JOptionPane.showMessageDialog(this, "Type can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
//            return false;
//        }//I18n
      
        return true;
    }

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        if (!validateField()) {
            return;
        }
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                constructorResult = new ConstructorResult();
            } else {
                constructorResult = (ConstructorResult) row[0];
            }
        }
        constructorResult.setTargetClass(name_TextField.getText());
        
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = constructorResult;
            row[1] = constructorResult.getTargetClass();
            row[2] = constructorResult.getColumn().size();
        }
        
        columnResultEntity.getTableDataListener().setData(columnResultEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void name_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_name_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_name_TextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private org.netbeans.modeler.properties.nentity.NEntityEditor columnResultEditor;
    private javax.swing.JLayeredPane columnResult_LayeredPane;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private javax.swing.JLayeredPane root_LayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables


   private NAttributeEntity getColumnResult() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("ColumnResult", "Column Result", "");
        attributeEntity.setCountDisplay(new String[]{"No Column Results", "One Column Result", " Column Results"});
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Class", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ColumnResultPanel());
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<Object[]>();
            int count;

            @Override
            public void initCount() {
                if (constructorResult != null && constructorResult.getColumn()!= null) {
                    count = constructorResult.getColumn().size();
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
                if (constructorResult != null && constructorResult.getColumn() != null) {
                    for (ColumnResult constructorResult : new CopyOnWriteArrayList<ColumnResult>(constructorResult.getColumn())) {
                        Object[] row = new Object[3];
                        row[0] = constructorResult;
                        row[1] = constructorResult.getName();
                        row[2] = constructorResult.getClazz();
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
                if (constructorResult != null && constructorResult.getColumn() != null) {
                    constructorResult.getColumn().clear();
                }
                for (Object[] row : data) {
                    ColumnResult column = (ColumnResult) row[0];
                    constructorResult.getColumn().add(column); 
                }
                initData();
            }
        });
        return attributeEntity;
    }

    
        private void initColumnResultNAttributeEditor() {
      columnResultEditor = NEntityEditor.createInstance(columnResult_LayeredPane,534,431);
   }
    
    


}
