/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.classmember;

import java.awt.event.ItemEvent;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.AccessModifierType;
import org.netbeans.jpa.modeler.spec.extend.Constructor;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import static org.openide.util.NbBundle.getMessage;

public class ConstructorPanel extends EntityComponent<Constructor> {

    private Constructor constructor;
    private final Set<Constructor> constructors;
    private final PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget;

    public ConstructorPanel(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        this.persistenceClassWidget = persistenceClassWidget;
        constructors = persistenceClassWidget.getBaseElementSpec().getConstructors();
    }

    @Override
    public void postConstruct() {
        initComponents();
    }

    @Override
    public void init() {
        ((ClassMemberPanel) classMemberPanel).init();
        preScrollPane.setVisible(false);
        postScrollPane.setVisible(false);
        customCodeButton.setSelected(false);
        accessModifierComboInit();
        pack();
    }
    
    private void accessModifierComboInit(){
        accessModifierComboBox.removeAllItems();
        accessModifierComboBox.addItem(new ComboBoxValue(AccessModifierType.PUBLIC, AccessModifierType.PUBLIC.getValue()));
        accessModifierComboBox.addItem(new ComboBoxValue(AccessModifierType.PROTECTED, AccessModifierType.PROTECTED.getValue()));
        accessModifierComboBox.addItem(new ComboBoxValue(AccessModifierType.PRIVATE, AccessModifierType.PRIVATE.getValue()));
        accessModifierComboBox.addItem(new ComboBoxValue(AccessModifierType.DEFAULT, AccessModifierType.DEFAULT.getValue()));
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create Constructor");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[2]));
        }
        constructor = new Constructor();
        setAccessModifierType(AccessModifierType.PUBLIC);
        ((ClassMemberPanel)classMemberPanel).setPersistenceClassWidget(persistenceClassWidget);
        ((ClassMemberPanel) classMemberPanel).setValue(constructor);
        preEditorPane.setText(null);
        postEditorPane.setText(null);
        
    }

    @Override
    public void updateEntity(Entity<Constructor> entityValue) {
        this.setTitle("Update Constructor");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            constructor = (Constructor) row[0];
            ((ClassMemberPanel)classMemberPanel).setPersistenceClassWidget(persistenceClassWidget);
            ((ClassMemberPanel) classMemberPanel).setValue(constructor);
            preEditorPane.setText(constructor.getPreCode());
            postEditorPane.setText(constructor.getPostCode());
            
            if(StringUtils.isNotBlank(constructor.getPreCode()) ||
               StringUtils.isNotBlank(constructor.getPostCode())){
                displayEditorPane(ItemEvent.SELECTED);
            }
            
        }
        
    }
    
    private void setAccessModifierType(AccessModifierType accessModifier) {
        if (accessModifier == null) {
            accessModifierComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < accessModifierComboBox.getItemCount(); i++) {
                if (((ComboBoxValue<AccessModifierType>) accessModifierComboBox.getItemAt(i)).getValue() == accessModifier) {
                    accessModifierComboBox.setSelectedIndex(i);
                }
            }
        }
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
        jLayeredPane1 = new javax.swing.JLayeredPane();
        preScrollPane = new javax.swing.JScrollPane();
        preEditorPane = new javax.swing.JEditorPane();
        classMemberPanel = new ClassMemberPanel(org.openide.util.NbBundle.getMessage(ClassMemberPanel.class, "LBL_constructor_select"));
        postScrollPane = new javax.swing.JScrollPane();
        postEditorPane = new javax.swing.JEditorPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        accessModifierLayeredPane = new javax.swing.JLayeredPane();
        accessModifierComboBox = new javax.swing.JComboBox();
        customCodeButton = new javax.swing.JToggleButton();

        rootLayeredPane.setLayout(new java.awt.BorderLayout());

        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        preScrollPane.setMaximumSize(new java.awt.Dimension(32767, 102));

        preEditorPane.setContentType("text/x-java"); // NOI18N
        preEditorPane.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        preEditorPane.setMinimumSize(new java.awt.Dimension(106, 60));
        preEditorPane.setPreferredSize(new java.awt.Dimension(106, 60));
        preScrollPane.setViewportView(preEditorPane);

        jLayeredPane1.add(preScrollPane, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout classMemberPanelLayout = new javax.swing.GroupLayout(classMemberPanel);
        classMemberPanel.setLayout(classMemberPanelLayout);
        classMemberPanelLayout.setHorizontalGroup(
            classMemberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        classMemberPanelLayout.setVerticalGroup(
            classMemberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );

        jLayeredPane1.add(classMemberPanel, java.awt.BorderLayout.CENTER);

        postScrollPane.setMaximumSize(new java.awt.Dimension(32767, 102));

        postEditorPane.setContentType("text/x-java"); // NOI18N
        postEditorPane.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        postEditorPane.setMinimumSize(new java.awt.Dimension(106, 60));
        postEditorPane.setPreferredSize(new java.awt.Dimension(106, 60));
        postScrollPane.setViewportView(postEditorPane);

        jLayeredPane1.add(postScrollPane, java.awt.BorderLayout.SOUTH);

        rootLayeredPane.add(jLayeredPane1, java.awt.BorderLayout.CENTER);

        jLayeredPane2.setPreferredSize(new java.awt.Dimension(472, 80));

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);
        save_Button.setBounds(10, 0, 80, 30);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(100, 0, 70, 30);

        jLayeredPane2.add(action_jLayeredPane);
        action_jLayeredPane.setBounds(280, 40, 170, 40);

        accessModifierComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.accessModifierComboBox.toolTipText")); // NOI18N
        accessModifierComboBox.setPreferredSize(new java.awt.Dimension(28, 23));
        accessModifierLayeredPane.add(accessModifierComboBox);
        accessModifierComboBox.setBounds(100, 30, 150, 27);

        customCodeButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        customCodeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/expand.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(customCodeButton, org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.customCodeButton.text")); // NOI18N
        customCodeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ConstructorPanel.class, "ConstructorPanel.customCodeButton.toolTipText")); // NOI18N
        customCodeButton.setAlignmentY(0.0F);
        customCodeButton.setIconTextGap(1);
        customCodeButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        customCodeButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                customCodeButtonItemStateChanged(evt);
            }
        });
        accessModifierLayeredPane.add(customCodeButton);
        customCodeButton.setBounds(0, 30, 90, 27);

        jLayeredPane2.add(accessModifierLayeredPane);
        accessModifierLayeredPane.setBounds(0, 10, 270, 70);

        rootLayeredPane.add(jLayeredPane2, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

      private boolean validateField() {
//        if (constructors.contains(constructor)){// bug : will only work for existing entry
//            JOptionPane.showMessageDialog(this, "Constructor with same signature already exist : " + constructor.getSignature(), "Duplicate Constructor", javax.swing.JOptionPane.WARNING_MESSAGE);
//            return false;
//        }
          if(constructor.getAttributes().isEmpty() && (constructor.getAccessModifier()==AccessModifierType.DEFAULT || constructor.getAccessModifier()==AccessModifierType.PRIVATE)){
              JOptionPane.showMessageDialog(this, getMessage(ConstructorPanel.class, "NO_ARG_ACCESS_MODIFIER.text"),
                      getMessage(ConstructorPanel.class, "NO_ARG_ACCESS_MODIFIER.title"), javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
          }
          
        return true;
    }
      
    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        constructor = (Constructor) ((ClassMemberPanel) classMemberPanel).getValue();
        constructor.setAccessModifier(((ComboBoxValue<AccessModifierType>) accessModifierComboBox.getSelectedItem()).getValue());
        if (!validateField()) {
            return;
        }
        constructor.setPreCode(preEditorPane.getText());
        constructor.setPostCode(postEditorPane.getText());
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = constructor;
            row[1] = constructor.toString();
        }
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void customCodeButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_customCodeButtonItemStateChanged
        displayEditorPane(evt.getStateChange());
        pack();
    }//GEN-LAST:event_customCodeButtonItemStateChanged

    private void displayEditorPane(int display){
        if(display == ItemEvent.SELECTED){
            preScrollPane.setVisible(true);
            postScrollPane.setVisible(true);
        } else if(display == ItemEvent.DESELECTED){
            preScrollPane.setVisible(false);
            postScrollPane.setVisible(false);
        }
        pack();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox accessModifierComboBox;
    private javax.swing.JLayeredPane accessModifierLayeredPane;
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JPanel classMemberPanel;
    private javax.swing.JToggleButton customCodeButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JEditorPane postEditorPane;
    private javax.swing.JScrollPane postScrollPane;
    private javax.swing.JEditorPane preEditorPane;
    private javax.swing.JScrollPane preScrollPane;
    private javax.swing.JLayeredPane rootLayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables

}
