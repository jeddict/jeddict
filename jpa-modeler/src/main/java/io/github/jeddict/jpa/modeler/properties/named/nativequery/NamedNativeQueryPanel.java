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
package io.github.jeddict.jpa.modeler.properties.named.nativequery;

import static io.github.jeddict.jcode.util.ProjectHelper.getClassName;
import static io.github.jeddict.jpa.modeler.properties.named.query.NamedQueryPanel.showDescription;
import io.github.jeddict.jpa.modeler.properties.named.query.QueryHintPanel;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.NamedNativeQuery;
import io.github.jeddict.jpa.spec.QueryHint;
import io.github.jeddict.jpa.spec.extend.cache.DBConnectionUtil;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.EntityComponent;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.INEntityDataListener;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityEditor;
import org.netbeans.modeler.properties.spec.Entity;
import org.netbeans.modeler.properties.spec.RowValue;
import org.netbeans.modules.db.api.sql.execute.SQLExecution;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

public class NamedNativeQueryPanel extends EntityComponent<NamedNativeQuery> implements Lookup.Provider {

    private NamedNativeQuery namedNativeQuery;
    private NAttributeEntity attributeEntity;
    private final ModelerFile modelerFile;
    private final EntityMappings entityMappings;
    private final io.github.jeddict.jpa.spec.Entity entity;
    private String description;

    public NamedNativeQueryPanel(ModelerFile modelerFile, io.github.jeddict.jpa.spec.Entity entity) {
        this.modelerFile = modelerFile;
        this.entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
        this.entity=entity;
    }

    @Override
    public void postConstruct() {
        initComponents();
        DBConnectionUtil.loadConnection(modelerFile, dbCon_jComboBox);
    }

    @Override
    public void init() {
        initResultClassesModel();
        initResultSetMappingModel();
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle("Create new Named Native Query");
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[4]));
        }
        namedNativeQuery = null;
        description = null;
        name_TextField.setText(StringUtils.EMPTY);
        query_EditorPane.setText(StringUtils.EMPTY);

        initCustomNAttributeEditor();
        attributeEntity = getQueryHint();
        customNAttributeClientEditor.setAttributeEntity(attributeEntity);
    }

    @Override
    public void updateEntity(Entity<NamedNativeQuery> entityValue) {
        this.setTitle("Update Named Query");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            namedNativeQuery = (NamedNativeQuery) row[0];
            name_TextField.setText(namedNativeQuery.getName());
            query_EditorPane.setText(namedNativeQuery.getQuery());
            description = namedNativeQuery.getDescription();
            
            if(namedNativeQuery.getResultClass()!=null){
            addResultClass(namedNativeQuery.getResultClass());
            }
            resultSetMapping_jComboBox.setSelectedItem(namedNativeQuery.getResultSetMapping());
        }

        initCustomNAttributeEditor();
        attributeEntity = getQueryHint();
        customNAttributeClientEditor.setAttributeEntity(attributeEntity);
    }

    void initCustomNAttributeEditor() {
        customNAttributeClientEditor = NEntityEditor.createInstance(queryHint_LayeredPane, 602, 249);
    }
    

    private void initResultClassesModel() {
        resultClass_jComboBox.removeAllItems();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(null);
        entityMappings.getEntity().forEach(entity -> model.addElement(entity.getClazz()));
        resultClass_jComboBox.setModel(model);
    }
    
    private void initResultSetMappingModel() {
        resultSetMapping_jComboBox.removeAllItems();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(null);
        entity.getSqlResultSetMapping().forEach(mapping -> model.addElement(mapping.getName()));
        resultSetMapping_jComboBox.setModel(model);
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
        dbCon_LayeredPane = new javax.swing.JLayeredPane();
        dbCon_Label = new javax.swing.JLabel();
        dbCon_jComboBox = new javax.swing.JComboBox();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        query_LayeredPane = new javax.swing.JLayeredPane();
        query_Label = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        query_ScrollPane = new javax.swing.JScrollPane();
        query_EditorPane = new javax.swing.JEditorPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        descriptionButton = new javax.swing.JButton();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        queryHint_LayeredPane = new javax.swing.JLayeredPane();
        customNAttributeClientEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        resultClass_LayeredPane = new javax.swing.JLayeredPane();
        resultClass_Label = new javax.swing.JLabel();
        resultClass_jComboBox = new javax.swing.JComboBox();
        dataType_Action = new javax.swing.JButton();
        resultSetMapping_LayeredPane = new javax.swing.JLayeredPane();
        resultSetMapping_Label = new javax.swing.JLabel();
        resultSetMapping_jComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        dbCon_LayeredPane.setEnabled(false);
        dbCon_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        dbCon_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dbCon_Label, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.dbCon_Label.text")); // NOI18N
        dbCon_LayeredPane.add(dbCon_Label, java.awt.BorderLayout.WEST);

        dbCon_jComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dbCon_jComboBoxItemStateChanged(evt);
            }
        });
        dbCon_LayeredPane.add(dbCon_jComboBox, java.awt.BorderLayout.CENTER);

        name_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        name_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(name_Label, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.name_Label.text")); // NOI18N
        name_LayeredPane.add(name_Label, java.awt.BorderLayout.WEST);

        name_TextField.setToolTipText(org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.name_TextField.toolTipText")); // NOI18N
        name_TextField.setPreferredSize(new java.awt.Dimension(40, 27));
        name_LayeredPane.add(name_TextField, java.awt.BorderLayout.CENTER);

        query_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(query_Label, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.query_Label.text")); // NOI18N
        query_LayeredPane.add(query_Label, java.awt.BorderLayout.WEST);

        jLayeredPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        query_ScrollPane.setPreferredSize(new java.awt.Dimension(400, 100));

        query_EditorPane.setPreferredSize(new java.awt.Dimension(206, 23));
        query_ScrollPane.setViewportView(query_EditorPane);
        //jEditorPane1.getDocument().removeDocumentListener(NamedStoredProcedureQueryPanel.this);
        query_EditorPane.setEditorKit(CloneableEditorSupport.getEditorKit("text/x-sql"));
        //jEditorPane1.getDocument().addDocumentListener(NamedStoredProcedureQueryPanel.this);

        jLayeredPane1.add(query_ScrollPane, java.awt.BorderLayout.CENTER);

        jLayeredPane2.setMinimumSize(new java.awt.Dimension(15, 100));
        jLayeredPane2.setPreferredSize(new java.awt.Dimension(30, 100));

        descriptionButton.setBackground(new java.awt.Color(204, 204, 204));
        descriptionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/resource/image/misc/note.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(descriptionButton, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.descriptionButton.text")); // NOI18N
        descriptionButton.setToolTipText(org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.descriptionButton.toolTipText")); // NOI18N
        descriptionButton.setBorder(null);
        descriptionButton.setBorderPainted(false);
        descriptionButton.setMinimumSize(new java.awt.Dimension(10, 25));
        descriptionButton.setPreferredSize(new java.awt.Dimension(10, 15));
        descriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionButtonActionPerformed(evt);
            }
        });
        jLayeredPane2.add(descriptionButton);
        descriptionButton.setBounds(10, 0, 20, 20);

        jLayeredPane1.add(jLayeredPane2, java.awt.BorderLayout.EAST);

        query_LayeredPane.add(jLayeredPane1, java.awt.BorderLayout.CENTER);

        action_jLayeredPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.save_Button.text")); // NOI18N
        save_Button.setPreferredSize(new java.awt.Dimension(60, 26));
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(save_Button);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(70, 26));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });
        action_jLayeredPane.add(cancel_Button);

        queryHint_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.queryHint_LayeredPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        queryHint_LayeredPane.setPreferredSize(new java.awt.Dimension(460, 30));

        queryHint_LayeredPane.setLayer(customNAttributeClientEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customNAttributeClientEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
        );

        resultClass_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(resultClass_Label, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.resultClass_Label.text")); // NOI18N
        resultClass_LayeredPane.add(resultClass_Label, java.awt.BorderLayout.WEST);

        resultClass_LayeredPane.add(resultClass_jComboBox, java.awt.BorderLayout.CENTER);

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.setPreferredSize(new java.awt.Dimension(27, 25));
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });
        resultClass_LayeredPane.add(dataType_Action, java.awt.BorderLayout.EAST);

        resultSetMapping_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        resultSetMapping_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(resultSetMapping_Label, org.openide.util.NbBundle.getMessage(NamedNativeQueryPanel.class, "NamedNativeQueryPanel.resultSetMapping_Label.text")); // NOI18N
        resultSetMapping_LayeredPane.add(resultSetMapping_Label, java.awt.BorderLayout.WEST);

        resultSetMapping_LayeredPane.add(resultSetMapping_jComboBox, java.awt.BorderLayout.CENTER);

        root_jLayeredPane.setLayer(dbCon_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(query_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(queryHint_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(resultClass_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(resultSetMapping_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(action_jLayeredPane))
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(query_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                            .addComponent(name_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                            .addComponent(resultSetMapping_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resultClass_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(query_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(resultClass_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultSetMapping_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(queryHint_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(root_jLayeredPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
        if (this.name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Name field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        if (this.query_EditorPane.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
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
                namedNativeQuery = new NamedNativeQuery();
            } else {
                namedNativeQuery = (NamedNativeQuery) row[0];
            }
        }

        namedNativeQuery.setName(name_TextField.getText());
        namedNativeQuery.setQuery(query_EditorPane.getText());
        namedNativeQuery.setDescription(description);
        namedNativeQuery.setResultClass((String)resultClass_jComboBox.getSelectedItem());
        namedNativeQuery.setResultSetMapping((String)resultSetMapping_jComboBox.getSelectedItem());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = namedNativeQuery;
            row[1] = namedNativeQuery.isEnable();
            row[2] = namedNativeQuery.getName();
            row[3] = namedNativeQuery.getQuery();
        }
        attributeEntity.getTableDataListener().setData(customNAttributeClientEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void dbCon_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dbCon_jComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            DBConnectionUtil.saveConnection(modelerFile, dbCon_jComboBox);
        }
    }//GEN-LAST:event_dbCon_jComboBoxItemStateChanged

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        String resultClass = NBModelerUtil.browseClass(modelerFile);
        addResultClass(resultClass);
    }//GEN-LAST:event_dataType_ActionActionPerformed

    private void descriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionButtonActionPerformed
        this.description = showDescription(this, "Description", description);
    }//GEN-LAST:event_descriptionButtonActionPerformed
    
    private void addResultClass(String resultClass){
        DefaultComboBoxModel model = (DefaultComboBoxModel) resultClass_jComboBox.getModel();
        String unqualifiedClassName = getClassName(resultClass);
        if (model.getIndexOf(unqualifiedClassName) != -1) { // check if it is Entity then select
            resultClass_jComboBox.setSelectedItem(unqualifiedClassName);
        } else { //if other class
            if (model.getIndexOf(resultClass) == -1) {
                model.addElement(resultClass);
            }
            resultClass_jComboBox.setSelectedItem(resultClass);
        }
    }
    
    private Lookup lookup;

    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            InstanceContent lookupContent = new InstanceContent();
            lookupContent.add(new SQLExecutionImpl(modelerFile));
            Lookup[] content = {new AbstractLookup(lookupContent)};
            lookup = new ProxyLookup(content);
        }
        return lookup;
    }

    
    private static final class SQLExecutionImpl implements SQLExecution {

        private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
        private boolean executing = false;
        private final ModelerFile file;

        public SQLExecutionImpl(ModelerFile file) {
            this.file = file;
        }
        
        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.removePropertyChangeListener(listener);
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public boolean isExecuting() {
            return executing;
        }
        
        public void setExecuting(boolean executing) {
            this.executing = executing;
            propChangeSupport.firePropertyChange(SQLExecution.PROP_EXECUTING, null, null);
        }
        
        @Override
        public boolean isSelection() {
            return false;
        }

        @Override
        public void execute() {
        }

        @Override
        public void executeSelection() {
        }

        @Override
        public void setDatabaseConnection(DatabaseConnection dbconn) {
        }

        @Override
        public DatabaseConnection getDatabaseConnection() {
           DatabaseConnection connection = DBConnectionUtil.getConnection(file);
            return connection;
        }

        @Override
        public void showHistory() {
            // not tested
        }
    }

    private NAttributeEntity getQueryHint() {
        final NAttributeEntity attributeEntityObj = new NAttributeEntity("QueryHint", "Query Hint", "");
        attributeEntityObj.setCountDisplay(new String[]{"No QueryHints", "One QueryHint", " QueryHints"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Value", false, String.class));
        attributeEntityObj.setColumns(columns);
        attributeEntityObj.setCustomDialog(new QueryHintPanel());
        attributeEntityObj.setTableDataListener(new INEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (namedNativeQuery != null && namedNativeQuery.getHint() != null) {
                    count = namedNativeQuery.getHint().size();
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
                if (namedNativeQuery != null && namedNativeQuery.getHint() != null) {
                    for (QueryHint queryHint : new CopyOnWriteArrayList<>(namedNativeQuery.getHint())) {
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
                if (namedNativeQuery != null && namedNativeQuery.getHint() != null) {
                    namedNativeQuery.getHint().clear();
                }
                for (Object[] row : data) {
                    QueryHint queryHint = (QueryHint) row[0];
                    namedNativeQuery.getHint().add(queryHint);
                }
                initData();
            }
        });
        return attributeEntityObj;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private org.netbeans.modeler.properties.nentity.NEntityEditor customNAttributeClientEditor;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JLabel dbCon_Label;
    private javax.swing.JLayeredPane dbCon_LayeredPane;
    private javax.swing.JComboBox dbCon_jComboBox;
    private javax.swing.JButton descriptionButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private javax.swing.JLayeredPane queryHint_LayeredPane;
    private javax.swing.JEditorPane query_EditorPane;
    private javax.swing.JLabel query_Label;
    private javax.swing.JLayeredPane query_LayeredPane;
    private javax.swing.JScrollPane query_ScrollPane;
    private javax.swing.JLabel resultClass_Label;
    private javax.swing.JLayeredPane resultClass_LayeredPane;
    private javax.swing.JComboBox resultClass_jComboBox;
    private javax.swing.JLabel resultSetMapping_Label;
    private javax.swing.JLayeredPane resultSetMapping_LayeredPane;
    private javax.swing.JComboBox resultSetMapping_jComboBox;
    private javax.swing.JLayeredPane root_jLayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables
}
