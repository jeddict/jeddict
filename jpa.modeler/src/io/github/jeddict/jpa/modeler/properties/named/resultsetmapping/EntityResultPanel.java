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
package io.github.jeddict.jpa.modeler.properties.named.resultsetmapping;

import javax.swing.JOptionPane;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.EntityResult;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.EntityComponent;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.properties.spec.Entity;
import org.netbeans.modeler.properties.spec.RowValue;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityEditor;

public class EntityResultPanel extends EntityComponent<EntityResult> {

    private final ModelerFile modelerFile;
    private final EntityMappings entityMappings;
    private NAttributeEntity fieldResultEntity;
    private EntityResult entityResult;

    public EntityResultPanel(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
        this.entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
    }

    @Override
    public void postConstruct() {
        initComponents();
    }

    @Override
    public void init() {
        JPAModelerUtil.initEntityModel(entityClass_ComboBox, entityMappings);
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Entity Result");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[3]));
            entityResult = new EntityResult();
        }

        initColumnResultNAttributeEditor();
        fieldResultEntity = ResultMappingUtil.getFieldResult(entityResult.getFieldResult(), modelerFile);
        fieldResultEditor.setAttributeEntity(fieldResultEntity);

    }

    @Override
    public void updateEntity(Entity<EntityResult> entityValue) {
        this.setTitle("Update Entity Result");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            entityResult = (EntityResult) row[0];
            entityMappings.findEntity(entityResult.getEntityClass())
                    .ifPresent(e -> entityClass_ComboBox.setSelectedItem(new ComboBoxValue(e, e.getClazz())));
        }
        initColumnResultNAttributeEditor();
        fieldResultEntity = ResultMappingUtil.getFieldResult(entityResult.getFieldResult(), modelerFile);
        fieldResultEditor.setAttributeEntity(fieldResultEntity);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_LayeredPane = new javax.swing.JLayeredPane();
        entityClass_LayeredPane = new javax.swing.JLayeredPane();
        entityClass_Label = new javax.swing.JLabel();
        entityClass_ComboBox = new javax.swing.JComboBox();
        fieldResult_LayeredPane = new javax.swing.JLayeredPane();
        fieldResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        discriminatorColumn_LayeredPane = new javax.swing.JLayeredPane();
        discriminatorColumn_Label = new javax.swing.JLabel();
        discriminatorColumn_TextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(entityClass_Label, org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.entityClass_Label.text")); // NOI18N

        javax.swing.GroupLayout entityClass_LayeredPaneLayout = new javax.swing.GroupLayout(entityClass_LayeredPane);
        entityClass_LayeredPane.setLayout(entityClass_LayeredPaneLayout);
        entityClass_LayeredPaneLayout.setHorizontalGroup(
            entityClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entityClass_LayeredPaneLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(entityClass_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(entityClass_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );
        entityClass_LayeredPaneLayout.setVerticalGroup(
            entityClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entityClass_LayeredPaneLayout.createSequentialGroup()
                .addGroup(entityClass_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entityClass_Label)
                    .addComponent(entityClass_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        entityClass_LayeredPane.setLayer(entityClass_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        entityClass_LayeredPane.setLayer(entityClass_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        fieldResult_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.fieldResult_LayeredPane.border.title"))); // NOI18N
        fieldResult_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout fieldResult_LayeredPaneLayout = new javax.swing.GroupLayout(fieldResult_LayeredPane);
        fieldResult_LayeredPane.setLayout(fieldResult_LayeredPaneLayout);
        fieldResult_LayeredPaneLayout.setHorizontalGroup(
            fieldResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fieldResultEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        fieldResult_LayeredPaneLayout.setVerticalGroup(
            fieldResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fieldResultEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );
        fieldResult_LayeredPane.setLayer(fieldResultEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);
        save_Button.setBounds(0, 0, 70, 23);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(80, 0, 70, 23);

        org.openide.awt.Mnemonics.setLocalizedText(discriminatorColumn_Label, org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.discriminatorColumn_Label.text")); // NOI18N

        discriminatorColumn_TextField.setText(org.openide.util.NbBundle.getMessage(EntityResultPanel.class, "EntityResultPanel.discriminatorColumn_TextField.text")); // NOI18N

        javax.swing.GroupLayout discriminatorColumn_LayeredPaneLayout = new javax.swing.GroupLayout(discriminatorColumn_LayeredPane);
        discriminatorColumn_LayeredPane.setLayout(discriminatorColumn_LayeredPaneLayout);
        discriminatorColumn_LayeredPaneLayout.setHorizontalGroup(
            discriminatorColumn_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discriminatorColumn_LayeredPaneLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(discriminatorColumn_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discriminatorColumn_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        discriminatorColumn_LayeredPaneLayout.setVerticalGroup(
            discriminatorColumn_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discriminatorColumn_LayeredPaneLayout.createSequentialGroup()
                .addGroup(discriminatorColumn_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discriminatorColumn_Label)
                    .addComponent(discriminatorColumn_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        discriminatorColumn_LayeredPane.setLayer(discriminatorColumn_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        discriminatorColumn_LayeredPane.setLayer(discriminatorColumn_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_LayeredPaneLayout = new javax.swing.GroupLayout(root_LayeredPane);
        root_LayeredPane.setLayout(root_LayeredPaneLayout);
        root_LayeredPaneLayout.setHorizontalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(entityClass_LayeredPane)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(discriminatorColumn_LayeredPane)
            .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(fieldResult_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        root_LayeredPaneLayout.setVerticalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(entityClass_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discriminatorColumn_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 325, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_LayeredPaneLayout.createSequentialGroup()
                    .addContainerGap(91, Short.MAX_VALUE)
                    .addComponent(fieldResult_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(39, Short.MAX_VALUE)))
        );
        root_LayeredPane.setLayer(entityClass_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(fieldResult_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(discriminatorColumn_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

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
        io.github.jeddict.jpa.spec.Entity entitySpec = ((ComboBoxValue<io.github.jeddict.jpa.spec.Entity>) entityClass_ComboBox.getSelectedItem()).getValue();
        if (entitySpec == null) {
            JOptionPane.showMessageDialog(this, "Entity can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
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
                entityResult = (EntityResult) row[0];
            }
        }
        io.github.jeddict.jpa.spec.Entity entitySpec = ((ComboBoxValue<io.github.jeddict.jpa.spec.Entity>) entityClass_ComboBox.getSelectedItem()).getValue();
        if (entitySpec != null) {
            entityResult.setEntityClass(entitySpec.getClazz());
        }
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = entityResult;
            row[1] = entityResult.getEntityClass();
            row[2] = entityResult.getFieldResult().size();
        }

        fieldResultEntity.getTableDataListener().setData(fieldResultEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel discriminatorColumn_Label;
    private javax.swing.JLayeredPane discriminatorColumn_LayeredPane;
    private javax.swing.JTextField discriminatorColumn_TextField;
    private javax.swing.JComboBox entityClass_ComboBox;
    private javax.swing.JLabel entityClass_Label;
    private javax.swing.JLayeredPane entityClass_LayeredPane;
    private org.netbeans.modeler.properties.nentity.NEntityEditor fieldResultEditor;
    private javax.swing.JLayeredPane fieldResult_LayeredPane;
    private javax.swing.JLayeredPane root_LayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables

    private void initColumnResultNAttributeEditor() {
        fieldResultEditor = NEntityEditor.createInstance(fieldResult_LayeredPane, 534, 431);
    }

}
