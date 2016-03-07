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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityEditor;

public class ConstructorResultPanel extends EntityComponent<ConstructorResult> {

    private final ModelerFile modelerFile;
    private NAttributeEntity columnResultEntity;
    private ConstructorResult constructorResult;

    public ConstructorResultPanel(ModelerFile modelerFile) {

        this.modelerFile = modelerFile;
    }

    @Override
    public void postConstruct() {
        initComponents();
    }

    @Override
    public void init() {
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Constructor Result");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[3]));
            constructorResult = new ConstructorResult();
        }
        targetClass_ComboBox.setSelectedItem("");

        initColumnResultNAttributeEditor();
        columnResultEntity = ResultMappingUtil.getColumnResult(constructorResult.getColumn(), modelerFile);
        columnResultEditor.setAttributeEntity(columnResultEntity);

    }

    @Override
    public void updateEntity(Entity<ConstructorResult> entityValue) {
        this.setTitle("Update Constructor Result");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            constructorResult = (ConstructorResult) row[0];

            if (((DefaultComboBoxModel) targetClass_ComboBox.getModel()).getIndexOf(constructorResult.getTargetClass()) == -1) {
                ((DefaultComboBoxModel) targetClass_ComboBox.getModel()).addElement(constructorResult.getTargetClass());
            }
            targetClass_ComboBox.setSelectedItem(constructorResult.getTargetClass());
        }
        initColumnResultNAttributeEditor();
        columnResultEntity = ResultMappingUtil.getColumnResult(constructorResult.getColumn(), modelerFile);
        columnResultEditor.setAttributeEntity(columnResultEntity);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_LayeredPane = new javax.swing.JLayeredPane();
        targetClass_LayeredPane = new javax.swing.JLayeredPane();
        targetClass_Label = new javax.swing.JLabel();
        targetClass_ComboBox = new javax.swing.JComboBox();
        targetClass_SearchAction = new javax.swing.JButton();
        columnResult_LayeredPane = new javax.swing.JLayeredPane();
        columnResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(targetClass_Label, org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.targetClass_Label.text_1")); // NOI18N

        targetClass_ComboBox.setEditable(true);

        targetClass_SearchAction.setBackground(new java.awt.Color(255, 255, 255));
        targetClass_SearchAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        targetClass_SearchAction.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorResultPanel.class, "ConstructorResultPanel.targetClass_SearchAction.toolTipText")); // NOI18N
        targetClass_SearchAction.setAlignmentY(0.0F);
        targetClass_SearchAction.setBorderPainted(false);
        targetClass_SearchAction.setMargin(null);
        targetClass_SearchAction.setPreferredSize(new java.awt.Dimension(55, 22));
        targetClass_SearchAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetClass_SearchActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout targetClass_LayeredPaneLayout = new javax.swing.GroupLayout(targetClass_LayeredPane);
        targetClass_LayeredPane.setLayout(targetClass_LayeredPaneLayout);
        targetClass_LayeredPaneLayout.setHorizontalGroup(
            targetClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(targetClass_LayeredPaneLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(targetClass_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetClass_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetClass_SearchAction, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        targetClass_LayeredPaneLayout.setVerticalGroup(
            targetClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(targetClass_LayeredPaneLayout.createSequentialGroup()
                .addGroup(targetClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(targetClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(targetClass_Label)
                        .addComponent(targetClass_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(targetClass_SearchAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        targetClass_LayeredPane.setLayer(targetClass_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        targetClass_LayeredPane.setLayer(targetClass_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        targetClass_LayeredPane.setLayer(targetClass_SearchAction, javax.swing.JLayeredPane.DEFAULT_LAYER);

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
            .addComponent(targetClass_LayeredPane)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                .addGap(0, 382, Short.MAX_VALUE)
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
                .addComponent(targetClass_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                    .addContainerGap(55, Short.MAX_VALUE)
                    .addComponent(columnResult_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(39, Short.MAX_VALUE)))
        );
        root_LayeredPane.setLayer(targetClass_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
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
        if (targetClass_ComboBox.getSelectedItem().toString().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "TargetClass can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        if (columnResultEditor.getSavedModel().size() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Columns can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
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
            if (row[0] != null) { // for update
                constructorResult = (ConstructorResult) row[0];
            }
        }
        constructorResult.setTargetClass(targetClass_ComboBox.getSelectedItem().toString());

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

    private void targetClass_SearchActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetClass_SearchActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile);
        if (((DefaultComboBoxModel) targetClass_ComboBox.getModel()).getIndexOf(dataType) == -1) {
            ((DefaultComboBoxModel) targetClass_ComboBox.getModel()).addElement(dataType);
        }
        targetClass_ComboBox.setSelectedItem(dataType);
    }//GEN-LAST:event_targetClass_SearchActionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private org.netbeans.modeler.properties.nentity.NEntityEditor columnResultEditor;
    private javax.swing.JLayeredPane columnResult_LayeredPane;
    private javax.swing.JLayeredPane root_LayeredPane;
    private javax.swing.JButton save_Button;
    private javax.swing.JComboBox targetClass_ComboBox;
    private javax.swing.JLabel targetClass_Label;
    private javax.swing.JLayeredPane targetClass_LayeredPane;
    private javax.swing.JButton targetClass_SearchAction;
    // End of variables declaration//GEN-END:variables

    private void initColumnResultNAttributeEditor() {
        columnResultEditor = NEntityEditor.createInstance(columnResult_LayeredPane, 534, 431);
    }

}
