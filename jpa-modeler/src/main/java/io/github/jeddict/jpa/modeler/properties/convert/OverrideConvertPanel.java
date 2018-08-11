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
package io.github.jeddict.jpa.modeler.properties.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.toList;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static io.github.jeddict.jpa.modeler.properties.convert.ConvertPanel.importAttributeConverter;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embeddable;
import static io.github.jeddict.jpa.spec.EmbeddableAttributes.getPaths;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.EntityComponent;
import org.netbeans.modeler.properties.spec.Entity;
import org.netbeans.modeler.properties.spec.RowValue;
import static org.openide.util.NbBundle.getMessage;

public class OverrideConvertPanel extends EntityComponent<Convert> {

    private final ModelerFile modelerFile;
    private final Object convertContainer;
    private boolean mapKey;

    public OverrideConvertPanel(ModelerFile modelerFile, Object convertContainer, boolean mapKey) {
        this.modelerFile = modelerFile;
        this.convertContainer = convertContainer;
        this.mapKey = mapKey;
    }

    @Override
    public void postConstruct() {
        initComponents();
        converter_EditorPane = NBModelerUtil.getJavaSingleLineEditor(converter_LayeredPane, null, getMessage(OverrideConvertPanel.class, "INFO_ATTRIBUTE_CONVERTER")).second();
    }

    @Override
    public void init() {
        loadAttributes();
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Add new Convert");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[4]));
        }
        converter_EditorPane.setText("");
        attribute_ComboBox.setSelectedItem("");
        disableConversion_CheckBox.setSelected(false);
    }

    @Override
    public void updateEntity(Entity<Convert> entityValue) {
        this.setTitle("Update Convert");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            Convert convert = (Convert) row[0];
            converter_EditorPane.setText(convert.getConverter());
            attribute_ComboBox.setSelectedItem(convert.getAttributeName());
            disableConversion_CheckBox.setSelected(convert.isDisableConversion());
        }
    }

    private void loadAttributes() {
        ManagedClass attributeClass = null;
        String prefix;
        if(mapKey){
            prefix = "key";
            if (convertContainer instanceof MapKeyHandler) {//ElementCollection,MultiRelationAttribute
                MapKeyHandler elementCollection = (MapKeyHandler) convertContainer;
//                if (elementCollection.getMapKeyAttribute() != null) {//MapKeyType.EXT
//                    if (elementCollection.getMapKeyAttribute() instanceof Embedded) {
//                        attributeClass = ((Embedded) elementCollection.getMapKeyAttribute()).getConnectedClass();
//                    }
//                } else 
                if (elementCollection.getMapKeyEmbeddable() != null) {//MapKeyType.NEW
                    attributeClass = elementCollection.getMapKeyEmbeddable();
                } else if (elementCollection.getMapKeyEntity() != null) {//MapKeyType.NEW
                    attributeClass = elementCollection.getMapKeyEntity();
                }
            } 
        } else {
            prefix = EMPTY;
            if (convertContainer instanceof io.github.jeddict.jpa.spec.Entity) {
                attributeClass = (ManagedClass) ((io.github.jeddict.jpa.spec.Entity) convertContainer).getSuperclass();
            } else if (convertContainer instanceof Embedded) {
                attributeClass = ((Embedded) convertContainer).getConnectedClass();
            } else if (convertContainer instanceof ElementCollection) {
                attributeClass = ((ElementCollection) convertContainer).getConnectedClass();
            }
        }
        
        attribute_ComboBox.removeAllItems();
        attribute_Label.setEnabled(attributeClass != null);
        attribute_ComboBox.setEnabled(attributeClass != null);
        
        if (attributeClass != null) {
            List<String> items = new ArrayList<>();
            List<Attribute> attributes = attributeClass.getAttributes().getAllAttribute(true);
            items.addAll(attributes
                        .stream()
                        .filter(attr -> attr instanceof Basic)
                        .map(attr -> (Basic)attr)
                        .filter(basic -> basic.getTemporal() == null)
                        .filter(basic -> basic.getEnumerated() == null)
                        .map(Basic::getName)
                        .map(attrName -> prefix.isEmpty() ? attrName : (prefix + '.' + attrName))
                        .collect(toList()));
                items.addAll(attributes
                        .stream()
                        .filter(attr -> attr instanceof ElementCollection)
                        .map(attr -> (ElementCollection)attr)
                        .filter(ec -> ec.getTemporal() == null)
                        .filter(ec -> ec.getEnumerated() == null)
                        .map(ElementCollection::getName)
                        .map(attrName -> prefix.isEmpty() ? attrName : (prefix + '.' + attrName))
                        .collect(toList()));
                
//            if ((attributeClass instanceof Entity) || (attributeClass instanceof MappedSuperclass)) {
              if (attributeClass instanceof Embeddable) {
                items.addAll(attributes
                        .stream()
                        .filter(attr -> attr instanceof Embedded)
                        .map(attr -> (Embedded)attr)
                        .map(emb -> getPaths(prefix, emb, attr -> ((attr instanceof Basic) || (attr instanceof ElementCollection))))
                        .collect(ArrayList<String>::new, ArrayList::addAll, ArrayList::addAll));
            }
            items.forEach(attribute_ComboBox::addItem);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        converter_WrapperPane = new javax.swing.JLayeredPane();
        converter_Label = new javax.swing.JLabel();
        converter_LayeredPane = new javax.swing.JLayeredPane();
        dataType_Action = new javax.swing.JButton();
        attribute_LayeredPane = new javax.swing.JLayeredPane();
        attribute_Label = new javax.swing.JLabel();
        attribute_ComboBox = new javax.swing.JComboBox<>();
        action_LayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        disableConversion_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        converter_WrapperPane.setToolTipText(org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.converter_WrapperPane.toolTipText")); // NOI18N
        converter_WrapperPane.setPreferredSize(new java.awt.Dimension(170, 27));
        converter_WrapperPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(converter_Label, org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.converter_Label.text")); // NOI18N
        converter_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        converter_WrapperPane.add(converter_Label, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout converter_LayeredPaneLayout = new javax.swing.GroupLayout(converter_LayeredPane);
        converter_LayeredPane.setLayout(converter_LayeredPaneLayout);
        converter_LayeredPaneLayout.setHorizontalGroup(
            converter_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 407, Short.MAX_VALUE)
        );
        converter_LayeredPaneLayout.setVerticalGroup(
            converter_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        converter_WrapperPane.add(converter_LayeredPane, java.awt.BorderLayout.CENTER);

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });
        converter_WrapperPane.add(dataType_Action, java.awt.BorderLayout.EAST);

        attribute_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        attribute_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(attribute_Label, org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.attribute_Label.text")); // NOI18N
        attribute_Label.setPreferredSize(new java.awt.Dimension(90, 14));
        attribute_LayeredPane.add(attribute_Label, java.awt.BorderLayout.WEST);

        attribute_ComboBox.setEditable(true);
        attribute_ComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.attribute_ComboBox.toolTipText")); // NOI18N
        attribute_LayeredPane.add(attribute_ComboBox, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_LayeredPane.add(save_Button);
        save_Button.setBounds(20, 0, 70, 30);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_LayeredPane.add(cancel_Button);
        cancel_Button.setBounds(100, 0, 70, 30);

        org.openide.awt.Mnemonics.setLocalizedText(disableConversion_CheckBox, org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "OverrideConvertPanel.disableConversion_CheckBox.text")); // NOI18N
        disableConversion_CheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(OverrideConvertPanel.class, "INFO_DISABLE_CONVERSION")); // NOI18N

        jLayeredPane1.setLayer(converter_WrapperPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(attribute_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(action_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(disableConversion_CheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(converter_WrapperPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(attribute_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(disableConversion_CheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
                        .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(converter_WrapperPane, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attribute_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(action_LayeredPane)
                    .addComponent(disableConversion_CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, Short.MAX_VALUE)
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
        Convert convert = null;

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                convert = new Convert();
            } else {
                convert = (Convert) row[0];
            }
        }

        convert.setConverter(converter_EditorPane.getText());
        convert.setAttributeName((String) attribute_ComboBox.getSelectedItem());
        convert.setDisableConversion(disableConversion_CheckBox.isSelected());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = convert;
            row[1] = convert.getConverter();
            row[2] = convert.getAttributeName();
            row[3] = convert.isDisableConversion();
        }
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile, converter_EditorPane.getText());
        if (StringUtils.isNotEmpty(dataType)) {
            converter_EditorPane.setText(dataType);
        }
    }//GEN-LAST:event_dataType_ActionActionPerformed
    private boolean validateField() {
//        if (this.converter_EditorPane.getText().trim().length() <= 0 && !disableConversion_CheckBox.isSelected()) {
//            JOptionPane.showMessageDialog(this, getMessage(OverrideConvertPanel.class, "MSG_Validation"), "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
//            return false;
//        }

        if (attribute_ComboBox.isEnabled() && this.attribute_ComboBox.getSelectedItem().toString().trim().length() <= 0 && !disableConversion_CheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "Attribute name can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        AtomicBoolean validated = new AtomicBoolean(false);
        importAttributeConverter(converter_EditorPane.getText(), validated, modelerFile);
        
        return validated.get();
    }

    private JEditorPane converter_EditorPane;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_LayeredPane;
    private javax.swing.JComboBox<String> attribute_ComboBox;
    private javax.swing.JLabel attribute_Label;
    private javax.swing.JLayeredPane attribute_LayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel converter_Label;
    private javax.swing.JLayeredPane converter_LayeredPane;
    private javax.swing.JLayeredPane converter_WrapperPane;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JCheckBox disableConversion_CheckBox;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables
}
