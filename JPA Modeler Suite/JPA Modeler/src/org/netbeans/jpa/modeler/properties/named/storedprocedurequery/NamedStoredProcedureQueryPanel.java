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
package org.netbeans.jpa.modeler.properties.named.storedprocedurequery;

import org.netbeans.jpa.modeler.properties.named.query.QueryHintPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.ParameterMode;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.jpa.modeler.spec.StoredProcedureParameter;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modules.db.metadata.model.api.Action;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.MetadataModels;
import org.netbeans.modules.db.metadata.model.api.Parameter;
import org.netbeans.modules.db.metadata.model.api.Procedure;
import org.netbeans.modules.db.metadata.model.api.SQLType;

public class NamedStoredProcedureQueryPanel extends EntityComponent<NamedStoredProcedureQuery> {

    private NamedStoredProcedureQuery namedStoredProcedureQuery;

    private NAttributeEntity queryHintEntity;
    private NAttributeEntity parametersEntity;
    private final ModelerFile modelerFile;

    public NamedStoredProcedureQueryPanel(ModelerFile modelerFile) {
        super("", true);
        this.modelerFile = modelerFile;
        initComponents();
        DatabaseExplorerUIs.connect(dbCon_jComboBox, ConnectionManager.getDefault());

    }

    @Override
    public void init() {
    }

    private DatabaseConnection getConnection() {
        Object item = dbCon_jComboBox.getSelectedItem();
        if (item == null) {
            return null;
        } else {
            return (DatabaseConnection) item;
        }
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Named StoredProcedure Query");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[4]));
        }
        namedStoredProcedureQuery = null;
        name_TextField.setText("");
        dbCon_jComboBox.setSelectedIndex(0);

        initCustomNAttributeEditor();
        queryHintEntity = getQueryHint();
        queryHintEditor.setAttributeEntity(queryHintEntity);
        parametersEntity = getStoredProcedureParameter();
        parametersEditor.setAttributeEntity(parametersEntity);
    }

    @Override
    public void updateEntity(Entity<NamedStoredProcedureQuery> entityValue) {
        this.setTitle("Update Named StoredProcedure Query");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            namedStoredProcedureQuery = (NamedStoredProcedureQuery) row[0];
            name_TextField.setText(namedStoredProcedureQuery.getName());
        }

        initCustomNAttributeEditor();
        queryHintEntity = getQueryHint();
        queryHintEditor.setAttributeEntity(queryHintEntity);
        parametersEntity = getStoredProcedureParameter();
        parametersEditor.setAttributeEntity(parametersEntity);
    }

    void initCustomNAttributeEditor() {
        parameters_LayeredPane.removeAll();
        parametersEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        javax.swing.GroupLayout parameters_LayeredPaneLayout = new javax.swing.GroupLayout(parameters_LayeredPane);
        parameters_LayeredPane.setLayout(parameters_LayeredPaneLayout);
        parameters_LayeredPaneLayout.setHorizontalGroup(
                parameters_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(parametersEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        parameters_LayeredPaneLayout.setVerticalGroup(
                parameters_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(parameters_LayeredPaneLayout.createSequentialGroup()
                        .addComponent(parametersEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
        );
        parameters_LayeredPane.setLayer(parametersEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        queryHint_LayeredPane.removeAll();
        queryHintEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
                queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(queryHintEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
                queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(queryHint_LayeredPaneLayout.createSequentialGroup()
                        .addComponent(queryHintEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
        );
        queryHint_LayeredPane.setLayer(queryHintEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);
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
        procedureName_LayeredPane = new javax.swing.JLayeredPane();
        procedureName_Label = new javax.swing.JLabel();
        procedureName_jComboBox = new javax.swing.JComboBox();
        dbCon_LayeredPane = new javax.swing.JLayeredPane();
        dbCon_Label = new javax.swing.JLabel();
        dbCon_jComboBox = new javax.swing.JComboBox();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        Save = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        queryHint_LayeredPane = new javax.swing.JLayeredPane();
        queryHintEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        parameters_LayeredPane = new javax.swing.JLayeredPane();
        parametersEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.name_Label.text")); // NOI18N

        name_TextField.setText(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.name_TextField.text")); // NOI18N
        name_TextField.setToolTipText(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.name_TextField.toolTipText")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(procedureName_Label, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.procedureName_Label.text")); // NOI18N

        procedureName_jComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.procedureName_jComboBox.toolTipText")); // NOI18N
        procedureName_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procedureName_jComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout procedureName_LayeredPaneLayout = new javax.swing.GroupLayout(procedureName_LayeredPane);
        procedureName_LayeredPane.setLayout(procedureName_LayeredPaneLayout);
        procedureName_LayeredPaneLayout.setHorizontalGroup(
            procedureName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(procedureName_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(procedureName_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(procedureName_jComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        procedureName_LayeredPaneLayout.setVerticalGroup(
            procedureName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(procedureName_LayeredPaneLayout.createSequentialGroup()
                .addGroup(procedureName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(procedureName_Label)
                    .addComponent(procedureName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        procedureName_LayeredPane.setLayer(procedureName_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        procedureName_LayeredPane.setLayer(procedureName_jComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(dbCon_Label, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.dbCon_Label.text")); // NOI18N

        dbCon_jComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dbCon_jComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout dbCon_LayeredPaneLayout = new javax.swing.GroupLayout(dbCon_LayeredPane);
        dbCon_LayeredPane.setLayout(dbCon_LayeredPaneLayout);
        dbCon_LayeredPaneLayout.setHorizontalGroup(
            dbCon_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbCon_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dbCon_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dbCon_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dbCon_LayeredPaneLayout.setVerticalGroup(
            dbCon_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbCon_LayeredPaneLayout.createSequentialGroup()
                .addGroup(dbCon_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbCon_Label)
                    .addComponent(dbCon_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dbCon_LayeredPane.setLayer(dbCon_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dbCon_LayeredPane.setLayer(dbCon_jComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(Save, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.Save.text")); // NOI18N
        Save.setToolTipText(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.Save.toolTipText")); // NOI18N
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(Save);
        Save.setBounds(0, 0, 70, 23);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(80, 0, 70, 23);

        queryHint_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.queryHint_LayeredPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        queryHint_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(queryHintEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryHint_LayeredPaneLayout.createSequentialGroup()
                .addComponent(queryHintEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addContainerGap())
        );
        queryHint_LayeredPane.setLayer(queryHintEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        parameters_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.parameters_LayeredPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        parameters_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout parameters_LayeredPaneLayout = new javax.swing.GroupLayout(parameters_LayeredPane);
        parameters_LayeredPane.setLayout(parameters_LayeredPaneLayout);
        parameters_LayeredPaneLayout.setHorizontalGroup(
            parameters_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parametersEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        parameters_LayeredPaneLayout.setVerticalGroup(
            parameters_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameters_LayeredPaneLayout.createSequentialGroup()
                .addComponent(parametersEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );
        parameters_LayeredPane.setLayer(parametersEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(name_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(procedureName_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(parameters_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                                .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(procedureName_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameters_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
        );
        root_jLayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(procedureName_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(dbCon_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(queryHint_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        queryHint_LayeredPane.getAccessibleContext().setAccessibleName("");
        queryHint_LayeredPane.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.queryHint_LayeredPane.AccessibleContext.accessibleDescription")); // NOI18N
        root_jLayeredPane.setLayer(parameters_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
        if (this.name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Name field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
//        if (this.procedureName_jComboBox().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
//            JOptionPane.showMessageDialog(this, "Query field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
//            return false;
//        }//I18n
        return true;
    }

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
//        if (!validateField()) {
//            return;
//        }
//        
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                namedStoredProcedureQuery = new NamedStoredProcedureQuery();
            } else {
                namedStoredProcedureQuery = (NamedStoredProcedureQuery) row[0];
            }
        }

        namedStoredProcedureQuery.setName(name_TextField.getText());
        namedStoredProcedureQuery.setProcedureName(procedureName_jComboBox.getSelectedItem().toString());


        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = namedStoredProcedureQuery;
            row[1] = namedStoredProcedureQuery.getName();
            row[2] = namedStoredProcedureQuery.getProcedureName();
//            row[3] = namedStoredProcedureQuery.getLockMode();
        }
        
        parametersEntity.getTableDataListener().setData(parametersEditor.getSavedModel());
        queryHintEntity.getTableDataListener().setData(queryHintEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_SaveActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void name_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_name_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_name_TextFieldActionPerformed

    private void dbCon_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dbCon_jComboBoxItemStateChanged
        // TODO add your handling code here:

//        getConnection().
        final MetadataModel metaDataModel = MetadataModels.createModel(getConnection().getJDBCConnection(), getConnection().getSchema());
//                metaDataModel.

//          connection.setMetadataModel(model);
//               MetadataElementHandle metadataElementHandle = new MetadataElementHandle();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (metaDataModel != null) {
                    try {
                        metaDataModel.runReadAction(
                                new Action<Metadata>() {
                                    @Override
                                    public void run(Metadata metaData) {
                                        final Collection<Procedure> procedures = metaData.getDefaultSchema().getProcedures();
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                procedureName_jComboBox.removeAllItems();
                                                for (Procedure procedure : procedures) {
//                                                    procedureName_jComboBox.addItem(procedure.getName());

                                                    procedureName_jComboBox.addItem(new ComboBoxValue(procedure, procedure.getName()));
                                                    System.out.println("procedure : " + procedure.getName());
                                                }
                                            }
//                            Procedure proc = procedureHandle.resolve(metaData);
//                            name = proc.getName();
//                            type = proc.getReturnValue() == null ? ProcedureNode.Type.Procedure : ProcedureNode.Type.Function;
//
//                            updateProperties(proc);
//                            schemaName = proc.getParent().getName();
//                            catalogName = proc.getParent().getParent().getName();
                                        });
                                    }
                                }
                        );

                    } catch (MetadataModelException e) {
                        e.printStackTrace();
                        System.out.println("");
//                NodeRegistry.handleMetadataModelException(this.getClass(), connection, e, true);
                    }
                }
            }
        });

        t.start();


    }//GEN-LAST:event_dbCon_jComboBoxItemStateChanged

    private void procedureName_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procedureName_jComboBoxActionPerformed
       
        
        ComboBoxValue<Procedure> comboBoxValue = (ComboBoxValue<Procedure>) procedureName_jComboBox.getSelectedItem();
        Procedure procedure = comboBoxValue.getValue();
        if(name_TextField.getText().trim().isEmpty()){
            name_TextField.setText(procedure.getName());
        }
        
        System.out.println("procedure : " + procedure.getName());
        
        namedStoredProcedureQuery.getParameter().clear();
        
        for (Parameter parameter : procedure.getParameters()) {
            System.out.println("parameter : " + parameter.getName() + " - " + parameter.getTypeName() + " - " + parameter.getDirection() + " - " + parameter.getType());
            StoredProcedureParameter storedProcedureParameter = new StoredProcedureParameter();
            storedProcedureParameter.setName(parameter.getName());
            storedProcedureParameter.setClazz(SQLTypeMap.toClass(SQLType.getJavaSQLType(parameter.getType())).getName());
            storedProcedureParameter.setMode(ParameterMode.valueOf(parameter.getDirection().toString()));
            namedStoredProcedureQuery.getParameter().add(storedProcedureParameter);
        }
//parainitData()
//        parametersEntity.getTableDataListener().getData()
        parametersEditor.setAttributeEntity(parametersEntity);
        for (org.netbeans.modules.db.metadata.model.api.Column column : procedure.getColumns()) {
            System.out.println("Column : " + column.getName() + " - " + column.getTypeName() + " - " + column.getType());

        }

        System.out.println("ReturnValue : " + procedure.getReturnValue());

    }//GEN-LAST:event_procedureName_jComboBoxActionPerformed

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
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getHint() != null) {
                    count = namedStoredProcedureQuery.getHint().size();
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
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getHint() != null) {
                    for (QueryHint queryHint : new CopyOnWriteArrayList<QueryHint>(namedStoredProcedureQuery.getHint())) {
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
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getHint() != null) {
                    namedStoredProcedureQuery.getHint().clear();
                }
                for (Object[] row : data) {
                    QueryHint queryHint = (QueryHint) row[0];
                    namedStoredProcedureQuery.getHint().add(queryHint);
                }
                initData();
            }
        });
        return attributeEntity;
    }

    private NAttributeEntity getStoredProcedureParameter() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("StoredProcedureParameter", "StoredProcedure Parameter", "");
        attributeEntity.setCountDisplay(new String[]{"No StoredProcedure Parameters", "One StoredProcedure Parameter", " StoredProcedure Parameters"});
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Type", false, String.class));
        columns.add(new Column("ParameterMode", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new StoredProcedureParameterPanel(modelerFile));
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<Object[]>();
            int count;

            @Override
            public void initCount() {
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getParameter() != null) {
                    count = namedStoredProcedureQuery.getParameter().size();
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
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getParameter() != null) {
                    for (StoredProcedureParameter storedProcedureParameter : new CopyOnWriteArrayList<StoredProcedureParameter>(namedStoredProcedureQuery.getParameter())) {
                        Object[] row = new Object[5];
                        row[0] = storedProcedureParameter;
                        row[1] = storedProcedureParameter.getName();
                        row[2] = storedProcedureParameter.getClazz();
                        row[3] = storedProcedureParameter.getMode();
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
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getParameter() != null) {
                    namedStoredProcedureQuery.getParameter().clear();
                }
                for (Object[] row : data) {
                    StoredProcedureParameter storedProcedureParameter = (StoredProcedureParameter) row[0];
                    namedStoredProcedureQuery.getParameter().add(storedProcedureParameter);
                }
                initData();
            }
        });
        return attributeEntity;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Save;
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel dbCon_Label;
    private javax.swing.JLayeredPane dbCon_LayeredPane;
    private javax.swing.JComboBox dbCon_jComboBox;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private org.netbeans.modeler.properties.nentity.NEntityEditor parametersEditor;
    private javax.swing.JLayeredPane parameters_LayeredPane;
    private javax.swing.JLabel procedureName_Label;
    private javax.swing.JLayeredPane procedureName_LayeredPane;
    private javax.swing.JComboBox procedureName_jComboBox;
    private org.netbeans.modeler.properties.nentity.NEntityEditor queryHintEditor;
    private javax.swing.JLayeredPane queryHint_LayeredPane;
    private javax.swing.JLayeredPane root_jLayeredPane;
    // End of variables declaration//GEN-END:variables
}
