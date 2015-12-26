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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.jpa.modeler.properties.named.query.QueryHintPanel;
import org.netbeans.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.jpa.modeler.spec.ParameterMode;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.jpa.modeler.spec.StoredProcedureParameter;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.Entity;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.RowValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.internal.EntityComponent;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityEditor;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Parameter;
import org.netbeans.modules.db.metadata.model.api.Procedure;
import org.netbeans.modules.db.metadata.model.api.SQLType;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.RequestProcessor;

public class NamedStoredProcedureQueryPanel extends EntityComponent<NamedStoredProcedureQuery> implements DocumentListener{
private static final RequestProcessor RP = new RequestProcessor(NamedStoredProcedureQueryPanel.class);
    private NamedStoredProcedureQuery namedStoredProcedureQuery;

    private NAttributeEntity queryHintEntity;
    private NAttributeEntity parametersEntity;
    private NAttributeEntity resultSetMappingsEntity;
    private final ModelerFile modelerFile;
    private final EntityMappings entityMappings;
    private org.netbeans.jpa.modeler.spec.Entity entity;

    public NamedStoredProcedureQueryPanel(ModelerFile modelerFile, org.netbeans.jpa.modeler.spec.Entity entity) {

        this.modelerFile = modelerFile;
        this.entity = entity;
        this.entityMappings = (EntityMappings) modelerFile.getRootElement();
        initComponents();
        DatabaseExplorerUIs.connect(dbCon_jComboBox, ConnectionManager.getDefault());

    }

    @Override
    public void init() {
        jTabbedPane.setSelectedIndex(0);
        initResultClassesModel();
        resultSetMappingTypeActionPerformed(null);
    }

    private DatabaseConnection getConnection() {
        Object item = dbCon_jComboBox.getSelectedItem();
        if (item instanceof DatabaseConnection) {
            return (DatabaseConnection) item;
        } else {
            return null;
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

        initQueryHintCustomNAttributeEditor();
        queryHintEntity = getQueryHint();
        queryHintEditor.setAttributeEntity(queryHintEntity);
        initParametersCustomNAttributeEditor();
        parametersEntity = getStoredProcedureParameter();
        parametersEditor.setAttributeEntity(parametersEntity);
        initResultSetMappingsNAttributeEditor();
        resultSetMappingsEntity = getResultSetMappings();
        resultSetMappingsEditor.setAttributeEntity(resultSetMappingsEntity);
    }

    @Override
    public void updateEntity(Entity<NamedStoredProcedureQuery> entityValue) {
        this.setTitle("Update Named StoredProcedure Query");
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            namedStoredProcedureQuery = (NamedStoredProcedureQuery) row[0];
            name_TextField.setText(namedStoredProcedureQuery.getName());

            if (((DefaultComboBoxModel) procedureName_jComboBox.getModel()).getIndexOf(namedStoredProcedureQuery.getProcedureName()) == -1) {
                ((DefaultComboBoxModel) procedureName_jComboBox.getModel()).addElement(namedStoredProcedureQuery.getProcedureName());
            }
            procedureName_jComboBox.setSelectedItem(namedStoredProcedureQuery.getProcedureName());
        }

        if (!namedStoredProcedureQuery.getResultSetMapping().isEmpty()) {
            resultSetMappingType_jComboBox.setSelectedIndex(1);//select second item "Resultset mapping"
        }

        initQueryHintCustomNAttributeEditor();
        queryHintEntity = getQueryHint();
        queryHintEditor.setAttributeEntity(queryHintEntity);
        initParametersCustomNAttributeEditor();
        parametersEntity = getStoredProcedureParameter();
        parametersEditor.setAttributeEntity(parametersEntity);
        initResultSetMappingsNAttributeEditor();
        resultSetMappingsEntity = getResultSetMappings();
        resultSetMappingsEditor.setAttributeEntity(resultSetMappingsEntity);

        loadResultClassesList();

    }

    private void loadResultClassesList() {
        List<Object> resultClasses_jListElement = new ArrayList<>();
        for (String resultClass : namedStoredProcedureQuery.getResultClass()) {
            if (resultClass.charAt(0) == '{' && resultClass.charAt(resultClass.length() - 1) == '}') {
                String id = resultClass.substring(1, resultClass.length() - 1);
                org.netbeans.jpa.modeler.spec.Entity entity = entityMappings.getEntity(id);
                if (entity != null) {
                    resultClasses_jListElement.add(entity);
                }
            } else {
                resultClasses_jListElement.add(resultClass);
            }
        }
        setResultClassSelectedValues(resultClasses_jList, resultClasses_jListElement.toArray());
    }

    private void initResultClassesModel() {
        DefaultListModel model = new DefaultListModel();
        for (org.netbeans.jpa.modeler.spec.Entity entity : entityMappings.getEntity()) {
            model.addElement(entity);
        }
        resultClasses_jList.setSelectionModel(getMultiSelectionModel());
        resultClasses_jList.setModel(model);
        resultClasses_jList.setCellRenderer(new ResultClassesRenderer());
    }

    private void setResultClassSelectedValues(JList list, Object... values) {
        list.clearSelection();
        for (Object value : values) {
            int index = getElementIndexInList(list.getModel(), value);
            if (index >= 0) {
                list.addSelectionInterval(index, index);
            } else if (value instanceof String) {  //if external lib class not exists then add
                addAndSelectItemInList(list, value);
            }
        }
        list.ensureIndexIsVisible(list.getSelectedIndex());
    }

    private void addAndSelectItemInList(JList list, Object value) {
        ((DefaultListModel) list.getModel()).addElement(value);
        int index = getElementIndexInList(list.getModel(), value);
        list.addSelectionInterval(index, index);
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
        jTabbedPane = new javax.swing.JTabbedPane();
        base_jLayeredPane = new javax.swing.JLayeredPane();
        name_LayeredPane = new javax.swing.JLayeredPane();
        name_Label = new javax.swing.JLabel();
        name_TextField = new javax.swing.JTextField();
        procedureName_LayeredPane = new javax.swing.JLayeredPane();
        procedureName_Label = new javax.swing.JLabel();
        procedureName_jComboBox = new javax.swing.JComboBox();
        dbCon_LayeredPane = new javax.swing.JLayeredPane();
        dbCon_Label = new javax.swing.JLabel();
        dbCon_jComboBox = new javax.swing.JComboBox();
        parameters_LayeredPane = new javax.swing.JLayeredPane();
        parametersEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        result_jLayeredPane = new javax.swing.JLayeredPane();
        resultSetMappingType_jLayeredPane = new javax.swing.JLayeredPane();
        resultSetMappingType_jLabel = new javax.swing.JLabel();
        resultSetMappingType_jComboBox = new javax.swing.JComboBox();
        resultSet_jLayeredPane = new javax.swing.JLayeredPane();
        resultClasses_jLayeredPane = new javax.swing.JLayeredPane();
        resultClasses_jScrollPane = new javax.swing.JScrollPane();
        resultClasses_jList = new javax.swing.JList();
        resultClasses_Action = new javax.swing.JButton();
        resultSetMappings_jLayeredPane = new javax.swing.JLayeredPane();
        resultSetMappingsEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        queryHint_LayeredPane = new javax.swing.JLayeredPane();
        queryHintEditor = new org.netbeans.modeler.properties.nentity.NEntityEditor();
        previewCode_LayeredPane = new javax.swing.JLayeredPane();
        annotation_LayeredPane = new javax.swing.JLayeredPane();
        annotation_ScrollPane1 = new javax.swing.JScrollPane();
        annotation_EditorPane = new javax.swing.JEditorPane();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        Save = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

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
                .addComponent(name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        procedureName_jComboBox.setEditable(true);
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
                .addComponent(procedureName_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(procedureName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(dbCon_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dbCon_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(parametersEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
        );
        parameters_LayeredPane.setLayer(parametersEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout base_jLayeredPaneLayout = new javax.swing.GroupLayout(base_jLayeredPane);
        base_jLayeredPane.setLayout(base_jLayeredPaneLayout);
        base_jLayeredPaneLayout.setHorizontalGroup(
            base_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(base_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(base_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(base_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(name_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(procedureName_LayeredPane, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(parameters_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        base_jLayeredPaneLayout.setVerticalGroup(
            base_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(base_jLayeredPaneLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(dbCon_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(procedureName_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameters_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );
        base_jLayeredPane.setLayer(name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        base_jLayeredPane.setLayer(procedureName_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        base_jLayeredPane.setLayer(dbCon_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        base_jLayeredPane.setLayer(parameters_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.base_jLayeredPane.TabConstraints.tabTitle"), base_jLayeredPane); // NOI18N

        result_jLayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(resultSetMappingType_jLabel, org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.resultSetMappingType_jLabel.text")); // NOI18N

        resultSetMappingType_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Result Classes", "ResultSet Mappings" }));
        resultSetMappingType_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultSetMappingTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultSetMappingType_jLayeredPaneLayout = new javax.swing.GroupLayout(resultSetMappingType_jLayeredPane);
        resultSetMappingType_jLayeredPane.setLayout(resultSetMappingType_jLayeredPaneLayout);
        resultSetMappingType_jLayeredPaneLayout.setHorizontalGroup(
            resultSetMappingType_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultSetMappingType_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultSetMappingType_jLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultSetMappingType_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resultSetMappingType_jLayeredPaneLayout.setVerticalGroup(
            resultSetMappingType_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultSetMappingType_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(resultSetMappingType_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultSetMappingType_jLabel)
                    .addComponent(resultSetMappingType_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        resultSetMappingType_jLayeredPane.setLayer(resultSetMappingType_jLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        resultSetMappingType_jLayeredPane.setLayer(resultSetMappingType_jComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        result_jLayeredPane.add(resultSetMappingType_jLayeredPane, java.awt.BorderLayout.NORTH);

        resultSet_jLayeredPane.setPreferredSize(new java.awt.Dimension(1055, 400));
        resultSet_jLayeredPane.setLayout(new java.awt.FlowLayout());

        resultClasses_jLayeredPane.setFocusable(false);
        resultClasses_jLayeredPane.setPreferredSize(new java.awt.Dimension(510, 400));

        resultClasses_jScrollPane.setViewportView(resultClasses_jList);

        resultClasses_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        resultClasses_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultClasses_ActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultClasses_jLayeredPaneLayout = new javax.swing.GroupLayout(resultClasses_jLayeredPane);
        resultClasses_jLayeredPane.setLayout(resultClasses_jLayeredPaneLayout);
        resultClasses_jLayeredPaneLayout.setHorizontalGroup(
            resultClasses_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultClasses_jLayeredPaneLayout.createSequentialGroup()
                .addComponent(resultClasses_jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultClasses_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        resultClasses_jLayeredPaneLayout.setVerticalGroup(
            resultClasses_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultClasses_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultClasses_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(resultClasses_jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        resultClasses_jLayeredPane.setLayer(resultClasses_jScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        resultClasses_jLayeredPane.setLayer(resultClasses_Action, javax.swing.JLayeredPane.DEFAULT_LAYER);

        resultSet_jLayeredPane.add(resultClasses_jLayeredPane);

        resultSetMappings_jLayeredPane.setPreferredSize(new java.awt.Dimension(530, 400));

        javax.swing.GroupLayout resultSetMappings_jLayeredPaneLayout = new javax.swing.GroupLayout(resultSetMappings_jLayeredPane);
        resultSetMappings_jLayeredPane.setLayout(resultSetMappings_jLayeredPaneLayout);
        resultSetMappings_jLayeredPaneLayout.setHorizontalGroup(
            resultSetMappings_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
            .addGroup(resultSetMappings_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultSetMappings_jLayeredPaneLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultSetMappingsEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        resultSetMappings_jLayeredPaneLayout.setVerticalGroup(
            resultSetMappings_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(resultSetMappings_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultSetMappings_jLayeredPaneLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultSetMappingsEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        resultSetMappings_jLayeredPane.setLayer(resultSetMappingsEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        resultSet_jLayeredPane.add(resultSetMappings_jLayeredPane);

        result_jLayeredPane.add(resultSet_jLayeredPane, java.awt.BorderLayout.CENTER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.result_jLayeredPane.TabConstraints.tabTitle"), result_jLayeredPane); // NOI18N

        javax.swing.GroupLayout queryHint_LayeredPaneLayout = new javax.swing.GroupLayout(queryHint_LayeredPane);
        queryHint_LayeredPane.setLayout(queryHint_LayeredPaneLayout);
        queryHint_LayeredPaneLayout.setHorizontalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(queryHintEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
        );
        queryHint_LayeredPaneLayout.setVerticalGroup(
            queryHint_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryHint_LayeredPaneLayout.createSequentialGroup()
                .addComponent(queryHintEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addContainerGap())
        );
        queryHint_LayeredPane.setLayer(queryHintEditor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.queryHint_LayeredPane.TabConstraints.tabTitle"), queryHint_LayeredPane); // NOI18N

        annotation_ScrollPane1.setViewportView(annotation_EditorPane);
        annotation_EditorPane.getDocument().removeDocumentListener(NamedStoredProcedureQueryPanel.this);
        annotation_EditorPane.setEditorKit(CloneableEditorSupport.getEditorKit("text/x-java"));
        // Need to re-add the document listeners since pane.setEditorKit() changes the document
        annotation_EditorPane.getDocument().addDocumentListener(NamedStoredProcedureQueryPanel.this);

        javax.swing.GroupLayout annotation_LayeredPaneLayout = new javax.swing.GroupLayout(annotation_LayeredPane);
        annotation_LayeredPane.setLayout(annotation_LayeredPaneLayout);
        annotation_LayeredPaneLayout.setHorizontalGroup(
            annotation_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(annotation_ScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
        );
        annotation_LayeredPaneLayout.setVerticalGroup(
            annotation_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(annotation_ScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );
        annotation_LayeredPane.setLayer(annotation_ScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout previewCode_LayeredPaneLayout = new javax.swing.GroupLayout(previewCode_LayeredPane);
        previewCode_LayeredPane.setLayout(previewCode_LayeredPaneLayout);
        previewCode_LayeredPaneLayout.setHorizontalGroup(
            previewCode_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(previewCode_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(annotation_LayeredPane)
                .addContainerGap())
        );
        previewCode_LayeredPaneLayout.setVerticalGroup(
            previewCode_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(previewCode_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(annotation_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(226, Short.MAX_VALUE))
        );
        previewCode_LayeredPane.setLayer(annotation_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(NamedStoredProcedureQueryPanel.class, "NamedStoredProcedureQueryPanel.previewCode_LayeredPane.TabConstraints.tabTitle"), previewCode_LayeredPane); // NOI18N

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

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, root_jLayeredPaneLayout.createSequentialGroup()
                .addComponent(jTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
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
        if (this.name_TextField.getText().trim().length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, "Name field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        if(procedureName_jComboBox.getSelectedItem()==null){
             JOptionPane.showMessageDialog(this, "Procedure can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (procedureName_jComboBox.getSelectedItem().toString().length() <= 0) {
            JOptionPane.showMessageDialog(this, "Procedure field can't be empty", "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }//I18n
        return true;
    }

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        if (!validateField()) {
            return;
        }
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

        parametersEntity.getTableDataListener().setData(parametersEditor.getSavedModel());

        namedStoredProcedureQuery.getResultClass().clear();
        namedStoredProcedureQuery.getResultSetMapping().clear();
        if ("Result Classes".equals(resultSetMappingType_jComboBox.getSelectedItem())) {
            //ResultClass
            for (Object obj : resultClasses_jList.getSelectedValuesList()) {
                if (obj instanceof org.netbeans.jpa.modeler.spec.Entity) {
                    org.netbeans.jpa.modeler.spec.Entity entityObj = (org.netbeans.jpa.modeler.spec.Entity) obj;
                    namedStoredProcedureQuery.getResultClass().add("{" + entityObj.getId() + "}");
                } else {
                    namedStoredProcedureQuery.getResultClass().add((String) obj);
                }
            }
        } else {
            resultSetMappingsEntity.getTableDataListener().setData(resultSetMappingsEditor.getSavedModel());
        }

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = namedStoredProcedureQuery;
            row[1] = namedStoredProcedureQuery.getName();
            row[2] = namedStoredProcedureQuery.getProcedureName();
            row[3] = namedStoredProcedureQuery.getParameter().size();
        }

        queryHintEntity.getTableDataListener().setData(queryHintEditor.getSavedModel());
        saveActionPerformed(evt);
    }//GEN-LAST:event_SaveActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void dbCon_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dbCon_jComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
//            Thread dbTask = new Thread(new StoredProcedureExecutor());
//            dbTask.start();
            RP.post(new StoredProcedureExecutor());
        }
    }//GEN-LAST:event_dbCon_jComboBoxItemStateChanged

    class StoredProcedureExecutor implements Runnable {

        @Override
        public void run() {
            DatabaseConnection dbCon = getConnection();
            if (dbCon != null) {
             MetadataModel metaDataModel = MetadataModelManager.get(dbCon);
            if (metaDataModel != null) {
                try {
                    metaDataModel.runReadAction((Metadata metaData) -> {
                        final Collection<Procedure> procedures = metaData.getDefaultSchema().getProcedures();
                        SwingUtilities.invokeLater(() -> {
                            if(!procedures.isEmpty()){
                                procedureName_jComboBox.removeAllItems();
                                for (Procedure procedure : procedures) {
                                    procedureName_jComboBox.addItem(new ComboBoxValue(procedure, procedure.getName()));
                                }
                            }
                        });
                    });

                } catch (MetadataModelException e) {
                    e.printStackTrace();
                }
            }
        }
        }
    }

    private String previousProcedureName;
    private void procedureName_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procedureName_jComboBoxActionPerformed
        if (procedureName_jComboBox.getSelectedItem() instanceof ComboBoxValue) {
            ComboBoxValue comboBoxValue = (ComboBoxValue) procedureName_jComboBox.getSelectedItem();
            Procedure procedure = (Procedure) comboBoxValue.getValue();

            if (name_TextField.getText().trim().isEmpty()
                    || previousProcedureName == null || previousProcedureName.equalsIgnoreCase(name_TextField.getText().trim())) {
                name_TextField.setText(procedure.getName());
            }
            previousProcedureName = procedure.getName();

            if (namedStoredProcedureQuery == null) {
                namedStoredProcedureQuery = new NamedStoredProcedureQuery();
            }
            namedStoredProcedureQuery.getParameter().clear();

            for (Parameter parameter : procedure.getParameters()) {
                StoredProcedureParameter storedProcedureParameter = new StoredProcedureParameter();
                storedProcedureParameter.setName(parameter.getName());
                storedProcedureParameter.setClazz(SQLTypeMap.toClass(SQLType.getJavaSQLType(parameter.getType())).getName());
                storedProcedureParameter.setMode(ParameterMode.valueOf(parameter.getDirection().toString()));
                namedStoredProcedureQuery.getParameter().add(storedProcedureParameter);
            }
            initParametersCustomNAttributeEditor();
            parametersEntity = getStoredProcedureParameter();
            parametersEditor.setAttributeEntity(parametersEntity);
        } else {
            String procedureName = (String) procedureName_jComboBox.getSelectedItem();
            if (name_TextField.getText().trim().isEmpty()) {
                name_TextField.setText(procedureName);
            }
        }
    }//GEN-LAST:event_procedureName_jComboBoxActionPerformed

    private void name_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_name_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_name_TextFieldActionPerformed

    private void resultClasses_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultClasses_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile);
        if (getElementIndexInList(resultClasses_jList.getModel(), dataType) == -1) {
            addAndSelectItemInList(resultClasses_jList, dataType);
        }
    }//GEN-LAST:event_resultClasses_ActionActionPerformed

    private void resultSetMappingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultSetMappingTypeActionPerformed
        if ("Result Classes".equals(resultSetMappingType_jComboBox.getSelectedItem())) {
            resultClasses_jLayeredPane.setVisible(true);
            resultSetMappings_jLayeredPane.setVisible(false);
        } else {
            resultClasses_jLayeredPane.setVisible(false);
            resultSetMappings_jLayeredPane.setVisible(true);
        }
    }//GEN-LAST:event_resultSetMappingTypeActionPerformed

    
    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
       if(jTabbedPane.getSelectedIndex() == 3){
          System.out.println("ddddddd");
//        EditorSettings.getDefault().getAllMimeTypes();
        
        setDocumentText(annotation_EditorPane.getDocument(), "private static void setDocumentText(Document doc, String text) {\n" +
"        try {\n" +
"            doc.remove(0, doc.getLength());\n" +
"            doc.insertString(0, text, null);\n" +
"        } catch (BadLocationException ble) {\n" +
"            LOG.log(Level.WARNING, null, ble);\n" +
"        }\n" +
"    }");
    }
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private static void setDocumentText(Document doc, String text) {
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, text, null);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
//            LOG.log(Level.WARNING, null, ble);
        }
    }
    
    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
@Override
    public void insertUpdate(javax.swing.event.DocumentEvent e){}

    /**
     * Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
@Override
    public void removeUpdate(javax.swing.event.DocumentEvent e){}

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
@Override
    public void changedUpdate(javax.swing.event.DocumentEvent e){}
    
    
    private NAttributeEntity getResultSetMappings() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("ResultSetMappings", "ResultSet Mappings", "");
        attributeEntity.setCountDisplay(new String[]{"No ResultSet Mappings", "One ResultSet Mapping", " ResultSet Mappings"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("ENTITY_OBJECT", false, true, org.netbeans.jpa.modeler.spec.Entity.class));
        columns.add(new Column("Add ResultSet Mapping", true, Boolean.class));
        columns.add(new Column("ResultSet Name", true, String.class));
        columns.add(new Column("Entity", false, String.class));
//        columns.add(new Column("Entity Result", false, Integer.class));
//        columns.add(new Column("Constructor Result", false, Integer.class));
//        columns.add(new Column("Column Result", false, Integer.class));

        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ResultSetMappingsPanel(modelerFile));
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getHint() != null) {
                    count = namedStoredProcedureQuery.getResultSetMapping().size();
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
//                if (entityMappings.getSqlResultSetMapping() != null) {
                for (org.netbeans.jpa.modeler.spec.Entity entity : entityMappings.getEntity()) {
                    for (SqlResultSetMapping resultSetMapping : new CopyOnWriteArrayList<>(entity.getSqlResultSetMapping())) {
                        Object[] row = new Object[5];
                        row[0] = resultSetMapping;
                        row[1] = entity;
                        row[2] = namedStoredProcedureQuery == null ? false : namedStoredProcedureQuery.getResultSetMapping().contains(resultSetMapping.getName());//NamedStoredProcedureQueryPanel.this.entity == entity;
                        row[3] = resultSetMapping.getName();
                        row[4] = entity.getClazz();

//                            row[5] = resultSetMapping.getEntityResult().size();
//                            row[6] = resultSetMapping.getConstructorResult().size();
//                            row[7] = resultSetMapping.getColumnResult().size();
                        data_local.add(row);
                    }
                }
//                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedStoredProcedureQuery.getResultSetMapping().clear();
                if (entityMappings.getSqlResultSetMapping() != null) {
                    entityMappings.getSqlResultSetMapping().clear();
                }
                for (org.netbeans.jpa.modeler.spec.Entity entity : entityMappings.getEntity()) {
                    entity.getSqlResultSetMapping().clear();
                }

                for (Object[] row : data) {
                    SqlResultSetMapping resultSetMapping = (SqlResultSetMapping) row[0];
                    org.netbeans.jpa.modeler.spec.Entity entity = (org.netbeans.jpa.modeler.spec.Entity) row[1];
                    Boolean selected = (Boolean) row[2];
                    if (selected) {
                        namedStoredProcedureQuery.getResultSetMapping().add(resultSetMapping.getName());
                    }
                    resultSetMapping.setEntity(entity);
                    if (entity == null) {
                        entityMappings.getSqlResultSetMapping().add(resultSetMapping);
                    } else {
                        entity.getSqlResultSetMapping().add(resultSetMapping);
                    }
                }
                initData();
            }
        });
        return attributeEntity;
    }

    private NAttributeEntity getQueryHint() {
        final NAttributeEntity attributeEntity = new NAttributeEntity("QueryHint", "Query Hint", "");
        attributeEntity.setCountDisplay(new String[]{"No QueryHints", "One QueryHint", " QueryHints"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Value", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new QueryHintPanel());
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
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
                List<Object[]> data_local = new LinkedList<>();
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getHint() != null) {
                    for (QueryHint queryHint : new CopyOnWriteArrayList<>(namedStoredProcedureQuery.getHint())) {
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
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Type", false, String.class));
        columns.add(new Column("ParameterMode", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new StoredProcedureParameterPanel(modelerFile));
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
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
                List<Object[]> data_local = new LinkedList<>();
                if (namedStoredProcedureQuery != null && namedStoredProcedureQuery.getParameter() != null) {
                    for (StoredProcedureParameter storedProcedureParameter : new CopyOnWriteArrayList<>(namedStoredProcedureQuery.getParameter())) {
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
    private javax.swing.JEditorPane annotation_EditorPane;
    private javax.swing.JLayeredPane annotation_LayeredPane;
    private javax.swing.JScrollPane annotation_ScrollPane1;
    private javax.swing.JLayeredPane base_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel dbCon_Label;
    private javax.swing.JLayeredPane dbCon_LayeredPane;
    private javax.swing.JComboBox dbCon_jComboBox;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel name_Label;
    private javax.swing.JLayeredPane name_LayeredPane;
    private javax.swing.JTextField name_TextField;
    private org.netbeans.modeler.properties.nentity.NEntityEditor parametersEditor;
    private javax.swing.JLayeredPane parameters_LayeredPane;
    private javax.swing.JLayeredPane previewCode_LayeredPane;
    private javax.swing.JLabel procedureName_Label;
    private javax.swing.JLayeredPane procedureName_LayeredPane;
    private javax.swing.JComboBox procedureName_jComboBox;
    private org.netbeans.modeler.properties.nentity.NEntityEditor queryHintEditor;
    private javax.swing.JLayeredPane queryHint_LayeredPane;
    private javax.swing.JButton resultClasses_Action;
    private javax.swing.JLayeredPane resultClasses_jLayeredPane;
    private javax.swing.JList resultClasses_jList;
    private javax.swing.JScrollPane resultClasses_jScrollPane;
    private javax.swing.JComboBox resultSetMappingType_jComboBox;
    private javax.swing.JLabel resultSetMappingType_jLabel;
    private javax.swing.JLayeredPane resultSetMappingType_jLayeredPane;
    private org.netbeans.modeler.properties.nentity.NEntityEditor resultSetMappingsEditor;
    private javax.swing.JLayeredPane resultSetMappings_jLayeredPane;
    private javax.swing.JLayeredPane resultSet_jLayeredPane;
    private javax.swing.JLayeredPane result_jLayeredPane;
    private javax.swing.JLayeredPane root_jLayeredPane;
    // End of variables declaration//GEN-END:variables

    private void initQueryHintCustomNAttributeEditor() {
        queryHintEditor = NEntityEditor.createInstance(queryHint_LayeredPane, 534, 431);
    }

    private void initParametersCustomNAttributeEditor() {
        parametersEditor = NEntityEditor.createInstance(parameters_LayeredPane, 500, 276);
    }

    private void initResultSetMappingsNAttributeEditor() {
        resultSetMappingsEditor = NEntityEditor.createInstance(resultSetMappings_jLayeredPane, 500, 276);
    }

    private ListSelectionModel getMultiSelectionModel() {
        return new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        };
    }

    private class ResultClassesRenderer extends JLabel implements ListCellRenderer {

        public ResultClassesRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object object, int index,
                boolean isSelected, boolean cellHasFocus) {
            if (object instanceof org.netbeans.jpa.modeler.spec.Entity) {
                org.netbeans.jpa.modeler.spec.Entity entity = (org.netbeans.jpa.modeler.spec.Entity) object;
                String _class = entity.getClazz();
//        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/" + _class + ".png"));
//        setIcon(imageIcon);
                setText(_class);
            } else {
                setText(object.toString());
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    private int getElementIndexInList(ListModel model, Object value) {
        if (value == null) {
            return -1;
        }
        if (model instanceof DefaultListModel) {
            return ((DefaultListModel) model).indexOf(value);
        }
        for (int i = 0; i < model.getSize(); i++) {
            if (value.equals(model.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
