/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.convert;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.Converter;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import static org.openide.util.NbBundle.getMessage;

public class ConverterPanel extends EntityComponent<Converter> {

    private final ModelerFile modelerFile;

    public ConverterPanel(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
    }

    @Override
    public void postConstruct() {
        initComponents();
        converter_EditorPane = NBModelerUtil.getJavaSingleLineEditor(converter_LayeredPane, null, getMessage(ConverterPanel.class, "INFO_ATTRIBUTE_CONVERTER")).second();
        attribute_EditorPane = NBModelerUtil.getJavaSingleLineEditor(attribute_LayeredPane, null, null).second();
        field_EditorPane = NBModelerUtil.getJavaSingleLineEditor(field_LayeredPane, null, null).second();
    }

    @Override
    public void init() {
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Add new Converter");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[5]));
        }
        converter_EditorPane.setText("");
        attribute_EditorPane.setText("");
        field_EditorPane.setText("");
        autoApply_CheckBox.setSelected(false);
    }

    @Override
    public void updateEntity(Entity<Converter> entityValue) {
        this.setTitle("Update Converter");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            Converter converter = (Converter) row[0];
            converter_EditorPane.setText(converter.getClazz());
            attribute_EditorPane.setText(converter.getAttributeType());
            field_EditorPane.setText(converter.getFieldType());
            autoApply_CheckBox.setSelected(converter.isAutoApply());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        action_LayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        autoApply_CheckBox = new javax.swing.JCheckBox();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        converter_WrapperPane = new javax.swing.JLayeredPane();
        converter_Label = new javax.swing.JLabel();
        converter_LayeredPane = new javax.swing.JLayeredPane();
        dataType_Action = new javax.swing.JButton();
        attribute_WrapperPane = new javax.swing.JLayeredPane();
        attribute_Label = new javax.swing.JLabel();
        attribute_LayeredPane = new javax.swing.JLayeredPane();
        attribute_Action = new javax.swing.JButton();
        field_WrapperPane = new javax.swing.JLayeredPane();
        field_Label = new javax.swing.JLabel();
        field_LayeredPane = new javax.swing.JLayeredPane();
        field_Action = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_LayeredPane.add(save_Button);
        save_Button.setBounds(20, 0, 70, 30);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_LayeredPane.add(cancel_Button);
        cancel_Button.setBounds(100, 0, 70, 30);

        org.openide.awt.Mnemonics.setLocalizedText(autoApply_CheckBox, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.autoApply_CheckBox.text")); // NOI18N
        autoApply_CheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "INFO_AUTO_APPLY")); // NOI18N

        jLayeredPane2.setLayout(new java.awt.GridLayout(3, 1, 0, 10));

        converter_WrapperPane.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.converter_WrapperPane.toolTipText")); // NOI18N
        converter_WrapperPane.setPreferredSize(new java.awt.Dimension(170, 27));
        converter_WrapperPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(converter_Label, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.converter_Label.text")); // NOI18N
        converter_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        converter_WrapperPane.add(converter_Label, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout converter_LayeredPaneLayout = new javax.swing.GroupLayout(converter_LayeredPane);
        converter_LayeredPane.setLayout(converter_LayeredPaneLayout);
        converter_LayeredPaneLayout.setHorizontalGroup(
            converter_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );
        converter_LayeredPaneLayout.setVerticalGroup(
            converter_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        converter_WrapperPane.add(converter_LayeredPane, java.awt.BorderLayout.CENTER);

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });
        converter_WrapperPane.add(dataType_Action, java.awt.BorderLayout.EAST);

        jLayeredPane2.add(converter_WrapperPane);

        attribute_WrapperPane.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.attribute_WrapperPane.toolTipText")); // NOI18N
        attribute_WrapperPane.setPreferredSize(new java.awt.Dimension(170, 27));
        attribute_WrapperPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(attribute_Label, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.attribute_Label.text")); // NOI18N
        attribute_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        attribute_WrapperPane.add(attribute_Label, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout attribute_LayeredPaneLayout = new javax.swing.GroupLayout(attribute_LayeredPane);
        attribute_LayeredPane.setLayout(attribute_LayeredPaneLayout);
        attribute_LayeredPaneLayout.setHorizontalGroup(
            attribute_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );
        attribute_LayeredPaneLayout.setVerticalGroup(
            attribute_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        attribute_WrapperPane.add(attribute_LayeredPane, java.awt.BorderLayout.CENTER);

        attribute_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        attribute_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        attribute_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attribute_ActionActionPerformed(evt);
            }
        });
        attribute_WrapperPane.add(attribute_Action, java.awt.BorderLayout.EAST);

        jLayeredPane2.add(attribute_WrapperPane);

        field_WrapperPane.setToolTipText(org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.field_WrapperPane.toolTipText")); // NOI18N
        field_WrapperPane.setPreferredSize(new java.awt.Dimension(170, 27));
        field_WrapperPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(field_Label, org.openide.util.NbBundle.getMessage(ConverterPanel.class, "ConverterPanel.field_Label.text")); // NOI18N
        field_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        field_WrapperPane.add(field_Label, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout field_LayeredPaneLayout = new javax.swing.GroupLayout(field_LayeredPane);
        field_LayeredPane.setLayout(field_LayeredPaneLayout);
        field_LayeredPaneLayout.setHorizontalGroup(
            field_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );
        field_LayeredPaneLayout.setVerticalGroup(
            field_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        field_WrapperPane.add(field_LayeredPane, java.awt.BorderLayout.CENTER);

        field_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        field_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        field_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                field_ActionActionPerformed(evt);
            }
        });
        field_WrapperPane.add(field_Action, java.awt.BorderLayout.EAST);

        jLayeredPane2.add(field_WrapperPane);

        jLayeredPane1.setLayer(action_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(autoApply_CheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(autoApply_CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(128, Short.MAX_VALUE)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(action_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(autoApply_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(88, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        if (!validateField()) {
            return;
        }
        Converter converter = null;

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                converter = new Converter();
            } else {
                converter = (Converter) row[0];
            }
        }

        converter.setClazz(converter_EditorPane.getText());
        converter.setAttributeType(attribute_EditorPane.getText());
        converter.setFieldType(field_EditorPane.getText());
        converter.setAutoApply(autoApply_CheckBox.isSelected());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = converter;
            row[1] = converter.getClazz();
            row[2] = converter.getAttributeType();
            row[3] = converter.getFieldType();
            row[4] = converter.isAutoApply();
        }
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile, converter_EditorPane.getText());
        if (StringUtils.isNotEmpty(dataType)) {
            converter_EditorPane.setText(dataType);
        }
    }//GEN-LAST:event_dataType_ActionActionPerformed

    private void field_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile, field_EditorPane.getText());
        if (StringUtils.isNotEmpty(dataType)) {
            field_EditorPane.setText(dataType);
        }
    }//GEN-LAST:event_field_ActionActionPerformed

    private void attribute_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attribute_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile, attribute_EditorPane.getText());
        if (StringUtils.isNotEmpty(dataType)) {
            attribute_EditorPane.setText(dataType);
        }
    }//GEN-LAST:event_attribute_ActionActionPerformed
    private boolean validateField() {
        if (converter_EditorPane.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this, "Converter can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (attribute_EditorPane.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this, "Attribute type can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (field_EditorPane.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this, "Field type can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private JEditorPane converter_EditorPane;
    private JEditorPane attribute_EditorPane;
    private JEditorPane field_EditorPane;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_LayeredPane;
    private javax.swing.JButton attribute_Action;
    private javax.swing.JLabel attribute_Label;
    private javax.swing.JLayeredPane attribute_LayeredPane;
    private javax.swing.JLayeredPane attribute_WrapperPane;
    private javax.swing.JCheckBox autoApply_CheckBox;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel converter_Label;
    private javax.swing.JLayeredPane converter_LayeredPane;
    private javax.swing.JLayeredPane converter_WrapperPane;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JButton field_Action;
    private javax.swing.JLabel field_Label;
    private javax.swing.JLayeredPane field_LayeredPane;
    private javax.swing.JLayeredPane field_WrapperPane;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables
}
