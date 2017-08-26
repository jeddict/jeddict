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
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.EntityResult;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.EntityComponent;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.INEntityDataListener;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityEditor;

public class ResultSetMappingsPanel extends EntityComponent<SqlResultSetMapping> {

    private final ModelerFile modelerFile;
    private final EntityMappings entityMappings;
    private final org.netbeans.jpa.modeler.spec.Entity entity; // entity null if used from other setting such as stored procedure or named queries
    private NAttributeEntity entityResultEntity;
    private NAttributeEntity constructorResultEntity;
    private NAttributeEntity columnResultEntity;
    private SqlResultSetMapping resultSetMapping;

    public ResultSetMappingsPanel(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
        this.entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
        entity = null;
    }

    public ResultSetMappingsPanel(ModelerFile modelerFile, org.netbeans.jpa.modeler.spec.Entity entity) {
        this.modelerFile = modelerFile;
        this.entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
        this.entity = entity;
    }

    @Override
    public void postConstruct() {
        initComponents();
    }

    @Override
    public void init() {
        jTabbedPane.setSelectedIndex(0);
        if (entity == null) {
            entity_LayeredPane.setVisible(true);
            JPAModelerUtil.initEntityModel(entity_ComboBox, entityMappings);
        } else {
            entity_LayeredPane.setVisible(false);
        }

    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new ResultSet Mapping");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[8]));
            resultSetMapping = new SqlResultSetMapping();
        }
        name_TextField.setText("");

        initEntityResultNAttributeEditor();
        entityResultEntity = getEntityResult();
        entityResultEditor.setAttributeEntity(entityResultEntity);

        initConstructorResultNAttributeEditor();
        constructorResultEntity = getConstructorResult();
        constructorResultEditor.setAttributeEntity(constructorResultEntity);

        initColumnResultNAttributeEditor();
        columnResultEntity = ResultMappingUtil.getColumnResult(resultSetMapping.getColumnResult(), modelerFile);
        columnResultEditor.setAttributeEntity(columnResultEntity);

    }

    @Override
    public void updateEntity(Entity<SqlResultSetMapping> entityValue) {
        this.setTitle("Update ResultSet Mapping");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            resultSetMapping = (SqlResultSetMapping) row[0];
            name_TextField.setText(resultSetMapping.getName());
            if (entity == null) {
                org.netbeans.jpa.modeler.spec.Entity entity = (org.netbeans.jpa.modeler.spec.Entity) row[1];
                entity_ComboBox.setSelectedItem(new ComboBoxValue(entity, entity.getClazz()));
            }
        }

        initEntityResultNAttributeEditor();
        entityResultEntity = getEntityResult();
        entityResultEditor.setAttributeEntity(entityResultEntity);
        initConstructorResultNAttributeEditor();
        constructorResultEntity = getConstructorResult();
        constructorResultEditor.setAttributeEntity(constructorResultEntity);
        initColumnResultNAttributeEditor();
        columnResultEntity = ResultMappingUtil.getColumnResult(resultSetMapping.getColumnResult(), modelerFile);
        columnResultEditor.setAttributeEntity(columnResultEntity);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_jLayeredPane = new javax.swing.JLayeredPane();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        entity_LayeredPane = new javax.swing.JLayeredPane();
        entity_Label = new javax.swing.JLabel();
        entity_ComboBox = new javax.swing.JComboBox();
        jTabbedPane = new javax.swing.JTabbedPane();
        entityResult_LayeredPane = new javax.swing.JLayeredPane();
        entityResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        constructorResult_LayeredPane = new javax.swing.JLayeredPane();
        constructorResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        columnResult_LayeredPane = new javax.swing.JLayeredPane();
        columnResultEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.name_Label.text_1")); // NOI18N

        name_TextField.setText(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.name_TextField.text_1")); // NOI18N
        name_TextField.setToolTipText(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.name_TextField.toolTipText_1")); // NOI18N
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
                .addComponent(name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name_TextField)
                .addContainerGap())
        );
        name_LayeredPaneLayout.setVerticalGroup(
            name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(name_Label)
                .addComponent(name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        name_LayeredPane.setLayer(name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        name_LayeredPane.setLayer(name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(entity_Label, org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.entity_Label.text")); // NOI18N

        javax.swing.GroupLayout entity_LayeredPaneLayout = new javax.swing.GroupLayout(entity_LayeredPane);
        entity_LayeredPane.setLayout(entity_LayeredPaneLayout);
        entity_LayeredPaneLayout.setHorizontalGroup(
            entity_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entity_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(entity_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(entity_ComboBox, 0, 424, Short.MAX_VALUE)
                .addContainerGap())
        );
        entity_LayeredPaneLayout.setVerticalGroup(
            entity_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entity_LayeredPaneLayout.createSequentialGroup()
                .addGroup(entity_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entity_Label)
                    .addComponent(entity_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        entity_LayeredPane.setLayer(entity_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        entity_LayeredPane.setLayer(entity_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        entityResult_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout entityResult_LayeredPaneLayout = new javax.swing.GroupLayout(entityResult_LayeredPane);
        entityResult_LayeredPane.setLayout(entityResult_LayeredPaneLayout);
        entityResult_LayeredPaneLayout.setHorizontalGroup(
            entityResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(entityResultEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        entityResult_LayeredPaneLayout.setVerticalGroup(
            entityResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(entityResultEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );
        entityResult_LayeredPane.setLayer(entityResultEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.entityResult_LayeredPane.TabConstraints.tabTitle"), entityResult_LayeredPane); // NOI18N

        constructorResult_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout constructorResult_LayeredPaneLayout = new javax.swing.GroupLayout(constructorResult_LayeredPane);
        constructorResult_LayeredPane.setLayout(constructorResult_LayeredPaneLayout);
        constructorResult_LayeredPaneLayout.setHorizontalGroup(
            constructorResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(constructorResultEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        constructorResult_LayeredPaneLayout.setVerticalGroup(
            constructorResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(constructorResultEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );
        constructorResult_LayeredPane.setLayer(constructorResultEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.constructorResult_LayeredPane.TabConstraints.tabTitle"), constructorResult_LayeredPane); // NOI18N

        columnResult_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        javax.swing.GroupLayout columnResult_LayeredPaneLayout = new javax.swing.GroupLayout(columnResult_LayeredPane);
        columnResult_LayeredPane.setLayout(columnResult_LayeredPaneLayout);
        columnResult_LayeredPaneLayout.setHorizontalGroup(
            columnResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(columnResultEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        columnResult_LayeredPaneLayout.setVerticalGroup(
            columnResult_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(columnResultEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );
        columnResult_LayeredPane.setLayer(columnResultEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.columnResult_LayeredPane.TabConstraints.tabTitle"), columnResult_LayeredPane); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.save_Button.toolTipText")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);
        save_Button.setBounds(0, 0, 70, 29);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(ResultSetMappingsPanel.class, "ResultSetMappingsPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);
        cancel_Button.setBounds(80, 0, 70, 30);

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addGap(0, 388, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(name_LayeredPane)
            .addComponent(entity_LayeredPane)
            .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(entity_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 329, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                    .addGap(104, 104, 104)
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(64, Short.MAX_VALUE)))
        );
        root_jLayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(entity_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(jTabbedPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
        if (name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Name can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        if (entity == null) {
            org.netbeans.jpa.modeler.spec.Entity entitySpec = ((ComboBoxValue<org.netbeans.jpa.modeler.spec.Entity>) entity_ComboBox.getSelectedItem()).getValue();
            if (entitySpec == null) {
                JOptionPane.showMessageDialog(this, "Entity can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }//I18n
        }
        return true;
    }

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        if (!validateField()) {
            return;
        }
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] != null) { //for update
                resultSetMapping = (SqlResultSetMapping) row[0];
            }
        }
        resultSetMapping.setName(name_TextField.getText());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = resultSetMapping;

            if (entity == null) {
                org.netbeans.jpa.modeler.spec.Entity entitySpec = ((ComboBoxValue<org.netbeans.jpa.modeler.spec.Entity>) entity_ComboBox.getSelectedItem()).getValue();
                row[1] = entitySpec;
                row[2] = true;
                row[3] = resultSetMapping.getName();
                row[4] = entitySpec == null ? "" : entitySpec.getClazz();
            } else {
                row[1] = resultSetMapping.getName();
            }
        }

        entityResultEntity.getTableDataListener().setData(entityResultEditor.getSavedModel());
        constructorResultEntity.getTableDataListener().setData(constructorResultEditor.getSavedModel());
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
    private org.netbeans.modeler.properties.nentity.NEntityEditor constructorResultEditor;
    private javax.swing.JLayeredPane constructorResult_LayeredPane;
    private org.netbeans.modeler.properties.nentity.NEntityEditor entityResultEditor;
    private javax.swing.JLayeredPane entityResult_LayeredPane;
    private javax.swing.JComboBox entity_ComboBox;
    private javax.swing.JLabel entity_Label;
    private javax.swing.JLayeredPane entity_LayeredPane;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private javax.swing.JLayeredPane root_jLayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables

    private NAttributeEntity getEntityResult() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("EntityResult", "Entity Result", "");
        attributeEntity.setCountDisplay(new String[]{"No Entity Results", "One Entity Result", " Entity Results"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Entity Class", false, String.class));
        columns.add(new Column("Field", false, Integer.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new EntityResultPanel(modelerFile));
        attributeEntity.setTableDataListener(new INEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (resultSetMapping != null && resultSetMapping.getEntityResult() != null) {
                    count = resultSetMapping.getEntityResult().size();
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
                List<Object[]> data_local = new LinkedList<>();
                if (resultSetMapping != null && resultSetMapping.getEntityResult() != null) {
                    for (EntityResult entityResult : new CopyOnWriteArrayList<>(resultSetMapping.getEntityResult())) {
                        Object[] row = new Object[3];
                        row[0] = entityResult;
                        row[1] = entityResult.getEntityClass();
                        row[2] = entityResult.getFieldResult().size();
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
                if (resultSetMapping != null && resultSetMapping.getEntityResult() != null) {
                    resultSetMapping.getEntityResult().clear();
                }
                for (Object[] row : data) {
                    EntityResult entityResult = (EntityResult) row[0];
                    resultSetMapping.getEntityResult().add(entityResult);
                }
                initData();
            }
        });
        return attributeEntity;
    }

    private NAttributeEntity getConstructorResult() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("ConstructorResult", "Constructor Result", "");
        attributeEntity.setCountDisplay(new String[]{"No Constructor Results", "One Constructor Result", " Constructor Results"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Target Class", false, String.class));
        columns.add(new Column("Column", false, Integer.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ConstructorResultPanel(modelerFile));
        attributeEntity.setTableDataListener(new INEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (resultSetMapping != null && resultSetMapping.getConstructorResult() != null) {
                    count = resultSetMapping.getConstructorResult().size();
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
                List<Object[]> data_local = new LinkedList<>();
                if (resultSetMapping != null && resultSetMapping.getConstructorResult() != null) {
                    for (ConstructorResult constructorResult : new CopyOnWriteArrayList<>(resultSetMapping.getConstructorResult())) {
                        Object[] row = new Object[3];
                        row[0] = constructorResult;
                        row[1] = constructorResult.getTargetClass();
                        row[2] = constructorResult.getColumn().size();
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
                if (resultSetMapping != null && resultSetMapping.getConstructorResult() != null) {
                    resultSetMapping.getConstructorResult().clear();
                }
                for (Object[] row : data) {
                    ConstructorResult constructorResult = (ConstructorResult) row[0];
                    resultSetMapping.getConstructorResult().add(constructorResult);
                }
                initData();
            }
        });
        return attributeEntity;
    }

    private void initEntityResultNAttributeEditor() {
        entityResultEditor = NEntityEditor.createInstance(entityResult_LayeredPane, 534, 431);
    }

    private void initConstructorResultNAttributeEditor() {
        constructorResultEditor = NEntityEditor.createInstance(constructorResult_LayeredPane, 534, 431);
    }

    private void initColumnResultNAttributeEditor() {
        columnResultEditor = NEntityEditor.createInstance(columnResult_LayeredPane, 534, 431);
    }

}
