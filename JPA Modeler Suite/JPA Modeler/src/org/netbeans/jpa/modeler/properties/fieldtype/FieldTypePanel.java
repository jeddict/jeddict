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
package org.netbeans.jpa.modeler.properties.fieldtype;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.TitledBorder;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.Lob;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;

/**
 *
 * @author Gaurav_Gupta
 */
public class FieldTypePanel extends GenericEmbeddedEditor<BaseAttribute> {

    private ModelerFile modelerFile;
    private BaseAttribute baseAttribute;

    @Override
    public void init() {
        initComponents();
//        language_ComboBox.setEditable(true);
    }

    @Override
    public BaseAttribute getValue() {
        if (baseAttribute instanceof Basic) {
            Basic basic = (Basic) baseAttribute;
            basic.setLob(null);
            basic.setEnumerated(null);
            basic.setTemporal(null);
            if (type_ComboBox.getSelectedItem().equals("Enumerated")) {
                if (String_RadioButton.isSelected()) {
                    basic.setEnumerated(EnumType.STRING);
                } else {
                    basic.setEnumerated(EnumType.ORDINAL);
                }
            } else if (type_ComboBox.getSelectedItem().equals("Lob")) {
                basic.setLob(new Lob());
            } else if (type_ComboBox.getSelectedItem().equals("Temporal")) {
                if (Date_RadioButton.isSelected()) {
                    basic.setTemporal(TemporalType.DATE);
                } else if (Time_RadioButton.isSelected()) {
                    basic.setTemporal(TemporalType.TIME);
                } else if (TimeStamp_RadioButton.isSelected()) {
                    basic.setTemporal(TemporalType.TIMESTAMP);
                }
            }

        }
        if (baseAttribute instanceof ElementCollection) {
            ElementCollection elementCollection = (ElementCollection) baseAttribute;
            elementCollection.setLob(null);
            elementCollection.setEnumerated(null);
            elementCollection.setTemporal(null);
            if (type_ComboBox.getSelectedItem().equals("Enumerated")) {
                if (String_RadioButton.isSelected()) {
                    elementCollection.setEnumerated(EnumType.STRING);
                } else {
                    elementCollection.setEnumerated(EnumType.ORDINAL);
                }
            } else if (type_ComboBox.getSelectedItem().equals("Lob")) {
                elementCollection.setLob(new Lob());
            } else if (type_ComboBox.getSelectedItem().equals("Temporal")) {
                if (Date_RadioButton.isSelected()) {
                    elementCollection.setTemporal(TemporalType.DATE);
                } else if (Time_RadioButton.isSelected()) {
                    elementCollection.setTemporal(TemporalType.TIME);
                } else if (TimeStamp_RadioButton.isSelected()) {
                    elementCollection.setTemporal(TemporalType.TIMESTAMP);
                }
            }

        } else if (baseAttribute instanceof Id) {
            Id id = (Id) baseAttribute;
            id.setTemporal(null);
            if (type_ComboBox.getSelectedItem().equals("Temporal")) {
                if (Date_RadioButton.isSelected()) {
                    id.setTemporal(TemporalType.DATE);
                } else if (Time_RadioButton.isSelected()) {
                    id.setTemporal(TemporalType.TIME);
                } else if (TimeStamp_RadioButton.isSelected()) {
                    id.setTemporal(TemporalType.TIMESTAMP);
                }
            }

        } else if (baseAttribute instanceof Transient) {
//            Transient _transient = (Transient) baseAttribute;

        } else if (baseAttribute instanceof Version) {
            Version version = (Version) baseAttribute;
            version.setTemporal(null);
            if (type_ComboBox.getSelectedItem().equals("Temporal")) {
                if (Date_RadioButton.isSelected()) {
                    version.setTemporal(TemporalType.DATE);
                } else if (Time_RadioButton.isSelected()) {
                    version.setTemporal(TemporalType.TIME);
                } else if (TimeStamp_RadioButton.isSelected()) {
                    version.setTemporal(TemporalType.TIMESTAMP);
                }
            }
        }
        baseAttribute.setAttributeType(dataType_ComboBox.getSelectedItem().toString());
        return baseAttribute;
    }

    private void initTypeComboBox() {
        TitledBorder titledBorder = (TitledBorder) jLayeredPane1.getBorder();
        List<String> type = new ArrayList<String>();
        type.add("Default");
        if (baseAttribute instanceof Basic) {
            type.add("Enumerated");
            type.add("Lob");
            type.add("Temporal");
            titledBorder.setTitle("Basic Attribute");
        } else if (baseAttribute instanceof ElementCollection) {
            type.add("Enumerated");
            type.add("Lob");
            type.add("Temporal");
            titledBorder.setTitle("ElementCollection<Basic> Attribute");
        } else if (baseAttribute instanceof Id) {
            type.add("Temporal");
            titledBorder.setTitle("Id Attribute");
        } else if (baseAttribute instanceof Version) {
//            type.add("Temporal");
            titledBorder.setTitle("Version Attribute");
        } else if (baseAttribute instanceof Transient) {
            titledBorder.setTitle("Transient Attribute");
        }

        type_ComboBox.removeAllItems();
        type_ComboBox.setModel(new DefaultComboBoxModel(type.toArray(new String[0])));

        //ElementCollection[Basic Type Value] => Lob,Enumerated,Temporal
        //Id => Temporal
    }

    private void setDataTypeEditable() {
        dataType_ComboBox.setEditable(true);
        dataType_Action.setVisible(true);

    }

    private void setDataTypeNonEditable() {
        dataType_ComboBox.setEditable(false);
        dataType_Action.setVisible(false);

    }

    private void initDataTypeComboBox() {
        String[] dataType = null;
        setDataTypeNonEditable();
        if (baseAttribute instanceof Basic) {
            if ("Enumerated".equals(type_ComboBox.getSelectedItem())) {
                setDataTypeEditable();
            } else if ("Temporal".equals(type_ComboBox.getSelectedItem())) {
                dataType = new String[]{"java.util.Date", "java.util.Calendar"};
            } else {
                dataType = new String[]{"String", "char", "boolean", "byte", "short", "int", "long", "float", "double", "Character", "Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double", "java.math.BigInteger", "java.math.BigDecimal", "java.util.Date", "java.util.Calendar",
                    "java.sql.Date", "java.sql.Time", "java.sql.Timestamp", "byte[]", "Byte[]", "char[]", "Character[]"};
            }
        } else if (baseAttribute instanceof ElementCollection) {
            if ("Enumerated".equals(type_ComboBox.getSelectedItem())) {
                setDataTypeEditable();
            } else if ("Temporal".equals(type_ComboBox.getSelectedItem())) {
                dataType = new String[]{"java.util.Date", "java.util.Calendar"};
            } else {
                dataType = new String[]{"String", "Character", "Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double",
                    "java.math.BigInteger", "java.math.BigDecimal", "java.util.Date", "java.util.Calendar",
                    "java.sql.Date", "java.sql.Time", "java.sql.Timestamp", "byte[]", "Byte[]", "char[]", "Character[]"};
            }
        } else if (baseAttribute instanceof Id) {
            if ("Temporal".equals(type_ComboBox.getSelectedItem())) {
                dataType = new String[]{"java.util.Date"};
            } else {
                dataType = new String[]{"String", "char", "boolean", "byte", "short", "int", "long", "float", "double", "Character", "Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double",
                    "java.math.BigInteger", "java.math.BigDecimal", "java.util.Date", "java.sql.Date"};
            }
        } else if (baseAttribute instanceof Version) {
            dataType = new String[]{"int", "Integer", "short", "Short", "long", "Long", "java.sql.Timestamp"};
        } else if (baseAttribute instanceof Transient) {
            setDataTypeEditable();
        }
        if (dataType == null) {
            dataType = new String[]{""};
        }

        dataType_ComboBox.removeAllItems();
        dataType_ComboBox.setModel(new DefaultComboBoxModel(dataType));
        dataType_ComboBox.setSelectedItem(dataType[0]);

    }

    @Override
    public void setValue(BaseAttribute baseAttribute) {
        this.baseAttribute = baseAttribute;
        initTypeComboBox();
        if (baseAttribute instanceof Basic) {
            Basic basic = (Basic) baseAttribute;
            if (basic.getLob() != null) {
                type_ComboBox.setSelectedItem("Lob");
            } else if (basic.getEnumerated() != null) {
                type_ComboBox.setSelectedItem("Enumerated");
                if (basic.getEnumerated() == EnumType.STRING) {
                    String_RadioButton.setSelected(true);
                } else {
                    Ordinal_RadioButton.setSelected(true);
                }
            } else if (basic.getTemporal() != null) {
                type_ComboBox.setSelectedItem("Temporal");
                if (basic.getTemporal() == TemporalType.DATE) {
                    Date_RadioButton.setSelected(true);
                } else if (basic.getTemporal() == TemporalType.TIME) {
                    Time_RadioButton.setSelected(true);
                } else if (basic.getTemporal() == TemporalType.TIMESTAMP) {
                    TimeStamp_RadioButton.setSelected(true);
                }
            } else {
                type_ComboBox.setSelectedItem("Default");
            }

        } else if (baseAttribute instanceof ElementCollection) {
            ElementCollection elementCollection = (ElementCollection) baseAttribute;
            if (elementCollection.getLob() != null) {
                type_ComboBox.setSelectedItem("Lob");
            } else if (elementCollection.getEnumerated() != null) {
                type_ComboBox.setSelectedItem("Enumerated");
                if (elementCollection.getEnumerated() == EnumType.STRING) {
                    String_RadioButton.setSelected(true);
                } else {
                    Ordinal_RadioButton.setSelected(true);
                }
            } else if (elementCollection.getTemporal() != null) {
                type_ComboBox.setSelectedItem("Temporal");
                if (elementCollection.getTemporal() == TemporalType.DATE) {
                    Date_RadioButton.setSelected(true);
                } else if (elementCollection.getTemporal() == TemporalType.TIME) {
                    Time_RadioButton.setSelected(true);
                } else if (elementCollection.getTemporal() == TemporalType.TIMESTAMP) {
                    TimeStamp_RadioButton.setSelected(true);
                }
            } else {
                type_ComboBox.setSelectedItem("Default");
            }

        } else if (baseAttribute instanceof Id) {
            Id id = (Id) baseAttribute;
            if (id.getTemporal() != null) {
                type_ComboBox.setSelectedItem("Temporal");
                if (id.getTemporal() == TemporalType.DATE) {
                    Date_RadioButton.setSelected(true);
                } else if (id.getTemporal() == TemporalType.TIME) {
                    Time_RadioButton.setSelected(true);
                } else if (id.getTemporal() == TemporalType.TIMESTAMP) {
                    TimeStamp_RadioButton.setSelected(true);
                }
            } else {
                type_ComboBox.setSelectedItem("Default");
            }

        } else if (baseAttribute instanceof Version) {
            Version version = (Version) baseAttribute;
            if (version.getTemporal() != null) {
                type_ComboBox.setSelectedItem("Temporal");
                if (version.getTemporal() == TemporalType.DATE) {
                    Date_RadioButton.setSelected(true);
                } else if (version.getTemporal() == TemporalType.TIME) {
                    Time_RadioButton.setSelected(true);
                } else if (version.getTemporal() == TemporalType.TIMESTAMP) {
                    TimeStamp_RadioButton.setSelected(true);
                }
            } else {
                type_ComboBox.setSelectedItem("Default");
            }

        } else if (baseAttribute instanceof Transient) {
//            Transient _transient = (Transient) baseAttribute;

        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        type_ComboBoxActionPerformed(null);
        initDataTypeComboBox();
        dataType_ComboBox.setSelectedItem(baseAttribute.getAttributeType());

    }

    public FieldTypePanel(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Temporal_buttonGroup = new javax.swing.ButtonGroup();
        Enumerated_buttonGroup = new javax.swing.ButtonGroup();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        type_LayeredPane = new javax.swing.JLayeredPane();
        type_Label = new javax.swing.JLabel();
        type_ComboBox = new javax.swing.JComboBox();
        extendType_LayeredPane = new javax.swing.JLayeredPane();
        Enumerated_LayeredPane1 = new javax.swing.JLayeredPane();
        Ordinal_RadioButton = new javax.swing.JRadioButton();
        String_RadioButton = new javax.swing.JRadioButton();
        Temporal_LayeredPane = new javax.swing.JLayeredPane();
        Date_RadioButton = new javax.swing.JRadioButton();
        Time_RadioButton = new javax.swing.JRadioButton();
        TimeStamp_RadioButton = new javax.swing.JRadioButton();
        dataType_LayeredPane = new javax.swing.JLayeredPane();
        dataType_Label = new javax.swing.JLabel();
        dataType_ComboBox = new javax.swing.JComboBox();
        dataType_Action = new javax.swing.JButton();

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.jLayeredPane1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(type_Label, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.type_Label.text")); // NOI18N

        type_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout type_LayeredPaneLayout = new javax.swing.GroupLayout(type_LayeredPane);
        type_LayeredPane.setLayout(type_LayeredPaneLayout);
        type_LayeredPaneLayout.setHorizontalGroup(
            type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(type_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        type_LayeredPaneLayout.setVerticalGroup(
            type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_LayeredPaneLayout.createSequentialGroup()
                .addGroup(type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_Label))
                .addGap(0, 3, Short.MAX_VALUE))
        );
        type_LayeredPane.setLayer(type_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        type_LayeredPane.setLayer(type_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane1.add(type_LayeredPane);
        type_LayeredPane.setBounds(10, 30, 424, 23);

        extendType_LayeredPane.setLayout(new java.awt.FlowLayout());

        Enumerated_buttonGroup.add(Ordinal_RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(Ordinal_RadioButton, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.Ordinal_RadioButton.text")); // NOI18N

        Enumerated_buttonGroup.add(String_RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(String_RadioButton, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.String_RadioButton.text")); // NOI18N

        javax.swing.GroupLayout Enumerated_LayeredPane1Layout = new javax.swing.GroupLayout(Enumerated_LayeredPane1);
        Enumerated_LayeredPane1.setLayout(Enumerated_LayeredPane1Layout);
        Enumerated_LayeredPane1Layout.setHorizontalGroup(
            Enumerated_LayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Enumerated_LayeredPane1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(Ordinal_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(String_RadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Enumerated_LayeredPane1Layout.setVerticalGroup(
            Enumerated_LayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Enumerated_LayeredPane1Layout.createSequentialGroup()
                .addGroup(Enumerated_LayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Ordinal_RadioButton)
                    .addComponent(String_RadioButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Enumerated_LayeredPane1.setLayer(Ordinal_RadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        Enumerated_LayeredPane1.setLayer(String_RadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        extendType_LayeredPane.add(Enumerated_LayeredPane1);

        Temporal_buttonGroup.add(Date_RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(Date_RadioButton, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.Date_RadioButton.text")); // NOI18N

        Temporal_buttonGroup.add(Time_RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(Time_RadioButton, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.Time_RadioButton.text")); // NOI18N

        Temporal_buttonGroup.add(TimeStamp_RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(TimeStamp_RadioButton, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.TimeStamp_RadioButton.text")); // NOI18N

        javax.swing.GroupLayout Temporal_LayeredPaneLayout = new javax.swing.GroupLayout(Temporal_LayeredPane);
        Temporal_LayeredPane.setLayout(Temporal_LayeredPaneLayout);
        Temporal_LayeredPaneLayout.setHorizontalGroup(
            Temporal_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Temporal_LayeredPaneLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(Date_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Time_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TimeStamp_RadioButton)
                .addGap(1, 1, 1))
        );
        Temporal_LayeredPaneLayout.setVerticalGroup(
            Temporal_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Temporal_LayeredPaneLayout.createSequentialGroup()
                .addGroup(Temporal_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Date_RadioButton)
                    .addComponent(Time_RadioButton)
                    .addComponent(TimeStamp_RadioButton))
                .addContainerGap())
        );
        Temporal_LayeredPane.setLayer(Date_RadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        Temporal_LayeredPane.setLayer(Time_RadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        Temporal_LayeredPane.setLayer(TimeStamp_RadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        extendType_LayeredPane.add(Temporal_LayeredPane);

        jLayeredPane1.add(extendType_LayeredPane);
        extendType_LayeredPane.setBounds(120, 60, 320, 30);

        org.openide.awt.Mnemonics.setLocalizedText(dataType_Label, org.openide.util.NbBundle.getMessage(FieldTypePanel.class, "FieldTypePanel.dataType_Label.text")); // NOI18N

        dataType_ComboBox.setEditable(true);

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dataType_LayeredPaneLayout = new javax.swing.GroupLayout(dataType_LayeredPane);
        dataType_LayeredPane.setLayout(dataType_LayeredPaneLayout);
        dataType_LayeredPaneLayout.setHorizontalGroup(
            dataType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataType_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataType_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dataType_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dataType_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dataType_LayeredPaneLayout.setVerticalGroup(
            dataType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataType_LayeredPaneLayout.createSequentialGroup()
                .addGroup(dataType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dataType_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dataType_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dataType_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dataType_Label)))
                .addGap(0, 11, Short.MAX_VALUE))
        );
        dataType_LayeredPane.setLayer(dataType_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dataType_LayeredPane.setLayer(dataType_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dataType_LayeredPane.setLayer(dataType_Action, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane1.add(dataType_LayeredPane);
        dataType_LayeredPane.setBounds(10, 100, 453, 31);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private String previousType = null;
    private void type_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_ComboBoxActionPerformed
        if ("Enumerated".equals(type_ComboBox.getSelectedItem())) {
            Temporal_LayeredPane.setVisible(false);
            Enumerated_LayeredPane1.setVisible(true);
            if (evt != null) {
                Ordinal_RadioButton.setSelected(true);
            }
            initDataTypeComboBox();
        } else if ("Temporal".equals(type_ComboBox.getSelectedItem())) {
            Temporal_LayeredPane.setVisible(true);
            Enumerated_LayeredPane1.setVisible(false);
            if (evt != null) {
                Date_RadioButton.setSelected(true);
            }
            initDataTypeComboBox();
        } else {
            if ("Enumerated".equals(previousType) || "Temporal".equals(previousType)) {
                initDataTypeComboBox();
            }
            Temporal_LayeredPane.setVisible(false);
            Enumerated_LayeredPane1.setVisible(false);
        }
        previousType = (String) type_ComboBox.getSelectedItem();

    }//GEN-LAST:event_type_ComboBoxActionPerformed

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile);
        if (((DefaultComboBoxModel) dataType_ComboBox.getModel()).getIndexOf(dataType) == -1) {
            ((DefaultComboBoxModel) dataType_ComboBox.getModel()).addElement(dataType);
        }
        dataType_ComboBox.setSelectedItem(dataType);
    }//GEN-LAST:event_dataType_ActionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton Date_RadioButton;
    private javax.swing.JLayeredPane Enumerated_LayeredPane1;
    private javax.swing.ButtonGroup Enumerated_buttonGroup;
    private javax.swing.JRadioButton Ordinal_RadioButton;
    private javax.swing.JRadioButton String_RadioButton;
    private javax.swing.JLayeredPane Temporal_LayeredPane;
    private javax.swing.ButtonGroup Temporal_buttonGroup;
    private javax.swing.JRadioButton TimeStamp_RadioButton;
    private javax.swing.JRadioButton Time_RadioButton;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JComboBox dataType_ComboBox;
    private javax.swing.JLabel dataType_Label;
    private javax.swing.JLayeredPane dataType_LayeredPane;
    private javax.swing.JLayeredPane extendType_LayeredPane;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JComboBox type_ComboBox;
    private javax.swing.JLabel type_Label;
    private javax.swing.JLayeredPane type_LayeredPane;
    // End of variables declaration//GEN-END:variables

}
