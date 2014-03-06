package org.netbeans.jpa.modeler.properties.idgeneration;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.spec.GenerationType;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.SequenceGenerator;
import org.netbeans.jpa.modeler.spec.TableGenerator;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;
import org.netbeans.modeler.widget.properties.customattr.Property;

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
/**
 *
 * @author Gaurav Gupta
 */
public class IdGeneratorPanel extends GenericEmbeddedEditor<Id> {

    private ModelerFile modelerFile;
    private Id idAttributeSpec;

    @Override
    public void init() {
        initComponents();
        strategy_ComboBox.removeAllItems();
        Property[] strategyProperties = new Property[]{
            new Property(null, "None"),
            new Property(GenerationType.AUTO, "Auto"),
            new Property(GenerationType.IDENTITY, "Identity"),
            new Property(GenerationType.SEQUENCE, "Sequence"),
            new Property(GenerationType.TABLE, "Table")
        };
        strategy_ComboBox.setModel(new DefaultComboBoxModel(strategyProperties));

    }

    private void setStrategySelectedItem(GenerationType type) {
        strategy_ComboBox.setSelectedItem(strategy_ComboBox.getItemAt(0));
        for (int i = 0; i < strategy_ComboBox.getItemCount(); i++) {
            Property property = (Property) strategy_ComboBox.getItemAt(i);
            if ((GenerationType) property.getKey() == type) {
                strategy_ComboBox.setSelectedItem(property);
                break;
            }
        }
    }

    @Override
    public Id getValue() {
        GenerationType generationType = (GenerationType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();
        idAttributeSpec.getGeneratedValue().setStrategy(generationType);
        if (generationType == GenerationType.SEQUENCE) {
            SequenceGenerator sequenceGenerator = new SequenceGenerator();
            if (seqgen_name_TextField.getText() == null || seqgen_name_TextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Sequence Generator name can not be empty", "Name required", JOptionPane.INFORMATION_MESSAGE);
                throw new IllegalStateException();
            }
            sequenceGenerator.setName(seqgen_name_TextField.getText());
            sequenceGenerator.setSequenceName(seqgen_seqname_TextField.getText());
            if (seqgen_initialValue_Spinner.getValue() == null) {
                seqgen_initialValue_Spinner.setValue(1);
            }
            sequenceGenerator.setInitialValue((Integer) seqgen_initialValue_Spinner.getValue());
            if (seqgen_allocationSize_Spinner.getValue() == null) {
                seqgen_allocationSize_Spinner.setValue(50);
            }
            sequenceGenerator.setAllocationSize((Integer) seqgen_allocationSize_Spinner.getValue());
            sequenceGenerator.setSchema(seqgen_schema_TextField.getText());
            sequenceGenerator.setCatalog(seqgen_catalog_TextField.getText());
            idAttributeSpec.setSequenceGenerator(sequenceGenerator);
            idAttributeSpec.getGeneratedValue().setGenerator(sequenceGenerator.getName());
        } else if (generationType == GenerationType.TABLE) {
            TableGenerator tableGenerator = new TableGenerator();
            if (tabgen_name_TextField.getText() == null || tabgen_name_TextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Table Generator name can not be empty", "Name required", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            tableGenerator.setName(tabgen_name_TextField.getText());
            tableGenerator.setTable(tabgen_tableName_TextField.getText());
            tableGenerator.setCatalog(tabgen_catalog_TextField.getText());
            tableGenerator.setSchema(tabgen_schema_TextField.getText());
            tableGenerator.setPkColumnName(tabgen_pkcol_name_TextField.getText());
            tableGenerator.setPkColumnValue(tabgen_pkcol_value_TextField.getText());
            tableGenerator.setValueColumnName(tabgen_valuecol_name_TextField.getText());

            if (tabgen_valuecol_initialValue_Spinner.getValue() == null) {
                tabgen_valuecol_initialValue_Spinner.setValue(0);
            }
            tableGenerator.setInitialValue((Integer) tabgen_valuecol_initialValue_Spinner.getValue());
            if (tabgen_valuecol_allocationSize_Spinner.getValue() == null) {
                tabgen_valuecol_allocationSize_Spinner.setValue(50);
            }
            tableGenerator.setAllocationSize((Integer) tabgen_valuecol_allocationSize_Spinner.getValue());
            idAttributeSpec.setTableGenerator(tableGenerator);
            idAttributeSpec.getGeneratedValue().setGenerator(tableGenerator.getName());
        } else {
            idAttributeSpec.setSequenceGenerator(null);
            idAttributeSpec.setTableGenerator(null);
            idAttributeSpec.getGeneratedValue().setGenerator(null);
        }
        return idAttributeSpec;
    }

    @Override
    public void setValue(Id idAttributeSpec) {
        this.idAttributeSpec = idAttributeSpec;
        setStrategySelectedItem(idAttributeSpec.getGeneratedValue().getStrategy());
        manageFieldState();
    }

    private void manageFieldState() {
        GenerationType generationType = (GenerationType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();

        if (generationType == GenerationType.SEQUENCE && idAttributeSpec.getSequenceGenerator() != null) {
            SequenceGenerator sequenceGenerator = idAttributeSpec.getSequenceGenerator();
            seqgen_name_TextField.setText(sequenceGenerator.getName());
            seqgen_seqname_TextField.setText(sequenceGenerator.getSequenceName());
            if (sequenceGenerator.getInitialValue() == null) {
                sequenceGenerator.setInitialValue(1);
            }
            seqgen_initialValue_Spinner.setValue(sequenceGenerator.getInitialValue());
            if (sequenceGenerator.getAllocationSize() == null) {
                sequenceGenerator.setInitialValue(50);
            }
            seqgen_allocationSize_Spinner.setValue(sequenceGenerator.getAllocationSize());
            seqgen_schema_TextField.setText(sequenceGenerator.getSchema());
            seqgen_catalog_TextField.setText(sequenceGenerator.getCatalog());

        } else if (generationType == GenerationType.TABLE && idAttributeSpec.getTableGenerator() != null) {
            TableGenerator tableGenerator = idAttributeSpec.getTableGenerator();
            tabgen_name_TextField.setText(tableGenerator.getName());
            tabgen_tableName_TextField.setText(tableGenerator.getTable());
            tabgen_catalog_TextField.setText(tableGenerator.getCatalog());
            tabgen_schema_TextField.setText(tableGenerator.getSchema());
            tabgen_pkcol_name_TextField.setText(tableGenerator.getPkColumnName());
            tabgen_pkcol_value_TextField.setText(tableGenerator.getPkColumnValue());
            tabgen_valuecol_name_TextField.setText(tableGenerator.getValueColumnName());
            if (tableGenerator.getInitialValue() == null) {
                tableGenerator.setInitialValue(0);
            }
            tabgen_valuecol_initialValue_Spinner.setValue(tableGenerator.getInitialValue());
            if (tableGenerator.getAllocationSize() == null) {
                tableGenerator.setAllocationSize(50);
            }
            tabgen_valuecol_allocationSize_Spinner.setValue(tableGenerator.getAllocationSize());

        } else {
            seqgen_name_TextField.setText(null);
            seqgen_seqname_TextField.setText(null);
            seqgen_initialValue_Spinner.setValue(1);
            seqgen_allocationSize_Spinner.setValue(50);
            seqgen_schema_TextField.setText(null);
            seqgen_catalog_TextField.setText(null);
            tabgen_name_TextField.setText(null);
            tabgen_tableName_TextField.setText(null);
            tabgen_catalog_TextField.setText(null);
            tabgen_schema_TextField.setText(null);
            tabgen_pkcol_name_TextField.setText(null);
            tabgen_pkcol_value_TextField.setText(null);
            tabgen_valuecol_name_TextField.setText(null);
            tabgen_valuecol_initialValue_Spinner.setValue(0);
            tabgen_valuecol_allocationSize_Spinner.setValue(50);
        }
    }

    public IdGeneratorPanel(ModelerFile modelerFile) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        main_LayeredPane = new javax.swing.JLayeredPane();
        strategy_LayeredPane = new javax.swing.JLayeredPane();
        strategy_Label = new javax.swing.JLabel();
        strategy_ComboBox = new javax.swing.JComboBox();
        seqgen_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_name_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_name_Label = new javax.swing.JLabel();
        seqgen_name_TextField = new javax.swing.JTextField();
        seqgen_seqname_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_seqname_Label = new javax.swing.JLabel();
        seqgen_seqname_TextField = new javax.swing.JTextField();
        seqgen_initialValue_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_initialValue_Label = new javax.swing.JLabel();
        seqgen_initialValue_Spinner = new javax.swing.JSpinner();
        seqgen_allocationSize_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_allocationSize_Label = new javax.swing.JLabel();
        seqgen_allocationSize_Spinner = new javax.swing.JSpinner();
        seqgen_schema_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_schema_Label = new javax.swing.JLabel();
        seqgen_schema_TextField = new javax.swing.JTextField();
        seqgen_catalog_LayeredPane = new javax.swing.JLayeredPane();
        seqgen_catalog_Label = new javax.swing.JLabel();
        seqgen_catalog_TextField = new javax.swing.JTextField();
        tabgen_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_name_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_name_Label = new javax.swing.JLabel();
        tabgen_name_TextField = new javax.swing.JTextField();
        tabgen_tableName_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_tableName_Label = new javax.swing.JLabel();
        tabgen_tableName_TextField = new javax.swing.JTextField();
        tabgen_catalog_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_catalog_Label = new javax.swing.JLabel();
        tabgen_catalog_TextField = new javax.swing.JTextField();
        tabgen_schema_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_schema_Label = new javax.swing.JLabel();
        tabgen_schema_TextField = new javax.swing.JTextField();
        tabgen_pkcol_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_pkcol_name_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_pkcol_name_Label = new javax.swing.JLabel();
        tabgen_pkcol_name_TextField = new javax.swing.JTextField();
        tabgen_pkcol_value_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_pkcol_value_Label = new javax.swing.JLabel();
        tabgen_pkcol_value_TextField = new javax.swing.JTextField();
        tabgen_valuecol_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_valuecol_name_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_valuecol_name_Label = new javax.swing.JLabel();
        tabgen_valuecol_name_TextField = new javax.swing.JTextField();
        tabgen_valuecol_initialValue_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_valuecol_initialValue_Label = new javax.swing.JLabel();
        tabgen_valuecol_initialValue_Spinner = new javax.swing.JSpinner();
        tabgen_valuecol_allocationSize_LayeredPane = new javax.swing.JLayeredPane();
        tabgen_valuecol_allocationSize_Label = new javax.swing.JLabel();
        tabgen_valuecol_allocationSize_Spinner = new javax.swing.JSpinner();

        main_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        main_LayeredPane.setLayout(new java.awt.GridBagLayout());

        strategy_Label.setText("Strategy :");

        strategy_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strategy_ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout strategy_LayeredPaneLayout = new javax.swing.GroupLayout(strategy_LayeredPane);
        strategy_LayeredPane.setLayout(strategy_LayeredPaneLayout);
        strategy_LayeredPaneLayout.setHorizontalGroup(
            strategy_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strategy_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(strategy_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(strategy_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        strategy_LayeredPaneLayout.setVerticalGroup(
            strategy_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strategy_LayeredPaneLayout.createSequentialGroup()
                .addGroup(strategy_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(strategy_Label)
                    .addComponent(strategy_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        strategy_LayeredPane.setLayer(strategy_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        strategy_LayeredPane.setLayer(strategy_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 0, 0);
        main_LayeredPane.add(strategy_LayeredPane, gridBagConstraints);

        seqgen_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sequence Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        seqgen_LayeredPane.setAlignmentX(2.0F);
        seqgen_LayeredPane.setLayout(new java.awt.GridLayout(3, 2));

        seqgen_name_Label.setText("Name : *");

        javax.swing.GroupLayout seqgen_name_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_name_LayeredPane);
        seqgen_name_LayeredPane.setLayout(seqgen_name_LayeredPaneLayout);
        seqgen_name_LayeredPaneLayout.setHorizontalGroup(
            seqgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_name_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(seqgen_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seqgen_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        seqgen_name_LayeredPaneLayout.setVerticalGroup(
            seqgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_name_Label)
                    .addComponent(seqgen_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        seqgen_name_LayeredPane.setLayer(seqgen_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_name_LayeredPane.setLayer(seqgen_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_LayeredPane.add(seqgen_name_LayeredPane);

        seqgen_seqname_Label.setText("Sequence Name :");

        javax.swing.GroupLayout seqgen_seqname_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_seqname_LayeredPane);
        seqgen_seqname_LayeredPane.setLayout(seqgen_seqname_LayeredPaneLayout);
        seqgen_seqname_LayeredPaneLayout.setHorizontalGroup(
            seqgen_seqname_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_seqname_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(seqgen_seqname_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(seqgen_seqname_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        seqgen_seqname_LayeredPaneLayout.setVerticalGroup(
            seqgen_seqname_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_seqname_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_seqname_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_seqname_Label)
                    .addComponent(seqgen_seqname_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        seqgen_seqname_LayeredPane.setLayer(seqgen_seqname_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_seqname_LayeredPane.setLayer(seqgen_seqname_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_LayeredPane.add(seqgen_seqname_LayeredPane);

        seqgen_initialValue_Label.setText("Initial Value :");

        javax.swing.GroupLayout seqgen_initialValue_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_initialValue_LayeredPane);
        seqgen_initialValue_LayeredPane.setLayout(seqgen_initialValue_LayeredPaneLayout);
        seqgen_initialValue_LayeredPaneLayout.setHorizontalGroup(
            seqgen_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_initialValue_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(seqgen_initialValue_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seqgen_initialValue_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        seqgen_initialValue_LayeredPaneLayout.setVerticalGroup(
            seqgen_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_initialValue_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_initialValue_Label)
                    .addComponent(seqgen_initialValue_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        seqgen_initialValue_LayeredPane.setLayer(seqgen_initialValue_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_initialValue_LayeredPane.setLayer(seqgen_initialValue_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_LayeredPane.add(seqgen_initialValue_LayeredPane);

        seqgen_allocationSize_Label.setText("Allocation Size :");

        javax.swing.GroupLayout seqgen_allocationSize_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_allocationSize_LayeredPane);
        seqgen_allocationSize_LayeredPane.setLayout(seqgen_allocationSize_LayeredPaneLayout);
        seqgen_allocationSize_LayeredPaneLayout.setHorizontalGroup(
            seqgen_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_allocationSize_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(seqgen_allocationSize_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(seqgen_allocationSize_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        seqgen_allocationSize_LayeredPaneLayout.setVerticalGroup(
            seqgen_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_allocationSize_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_allocationSize_Label)
                    .addComponent(seqgen_allocationSize_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        seqgen_allocationSize_LayeredPane.setLayer(seqgen_allocationSize_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_allocationSize_LayeredPane.setLayer(seqgen_allocationSize_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_LayeredPane.add(seqgen_allocationSize_LayeredPane);

        seqgen_schema_Label.setText("Schema :");

        javax.swing.GroupLayout seqgen_schema_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_schema_LayeredPane);
        seqgen_schema_LayeredPane.setLayout(seqgen_schema_LayeredPaneLayout);
        seqgen_schema_LayeredPaneLayout.setHorizontalGroup(
            seqgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_schema_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(seqgen_schema_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seqgen_schema_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        seqgen_schema_LayeredPaneLayout.setVerticalGroup(
            seqgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_schema_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_schema_Label)
                    .addComponent(seqgen_schema_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        seqgen_schema_LayeredPane.setLayer(seqgen_schema_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_schema_LayeredPane.setLayer(seqgen_schema_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_schema_TextField.getAccessibleContext().setAccessibleName("");

        seqgen_LayeredPane.add(seqgen_schema_LayeredPane);

        seqgen_catalog_Label.setText("Catalog :");

        javax.swing.GroupLayout seqgen_catalog_LayeredPaneLayout = new javax.swing.GroupLayout(seqgen_catalog_LayeredPane);
        seqgen_catalog_LayeredPane.setLayout(seqgen_catalog_LayeredPaneLayout);
        seqgen_catalog_LayeredPaneLayout.setHorizontalGroup(
            seqgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_catalog_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(seqgen_catalog_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(seqgen_catalog_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        seqgen_catalog_LayeredPaneLayout.setVerticalGroup(
            seqgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seqgen_catalog_LayeredPaneLayout.createSequentialGroup()
                .addGroup(seqgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqgen_catalog_Label)
                    .addComponent(seqgen_catalog_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        seqgen_catalog_LayeredPane.setLayer(seqgen_catalog_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        seqgen_catalog_LayeredPane.setLayer(seqgen_catalog_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        seqgen_LayeredPane.add(seqgen_catalog_LayeredPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 26;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 16);
        main_LayeredPane.add(seqgen_LayeredPane, gridBagConstraints);

        tabgen_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Table Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        tabgen_LayeredPane.setLayout(new java.awt.GridLayout(2, 2));

        tabgen_name_Label.setText("Name : *");

        javax.swing.GroupLayout tabgen_name_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_name_LayeredPane);
        tabgen_name_LayeredPane.setLayout(tabgen_name_LayeredPaneLayout);
        tabgen_name_LayeredPaneLayout.setHorizontalGroup(
            tabgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_name_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(tabgen_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabgen_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        tabgen_name_LayeredPaneLayout.setVerticalGroup(
            tabgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_name_Label)
                    .addComponent(tabgen_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        tabgen_name_LayeredPane.setLayer(tabgen_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_name_LayeredPane.setLayer(tabgen_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_LayeredPane.add(tabgen_name_LayeredPane);

        tabgen_tableName_Label.setText("Table Name :");

        javax.swing.GroupLayout tabgen_tableName_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_tableName_LayeredPane);
        tabgen_tableName_LayeredPane.setLayout(tabgen_tableName_LayeredPaneLayout);
        tabgen_tableName_LayeredPaneLayout.setHorizontalGroup(
            tabgen_tableName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_tableName_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(tabgen_tableName_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabgen_tableName_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        tabgen_tableName_LayeredPaneLayout.setVerticalGroup(
            tabgen_tableName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_tableName_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_tableName_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_tableName_Label)
                    .addComponent(tabgen_tableName_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        tabgen_tableName_LayeredPane.setLayer(tabgen_tableName_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_tableName_LayeredPane.setLayer(tabgen_tableName_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_LayeredPane.add(tabgen_tableName_LayeredPane);

        tabgen_catalog_Label.setText("Catalog :");

        javax.swing.GroupLayout tabgen_catalog_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_catalog_LayeredPane);
        tabgen_catalog_LayeredPane.setLayout(tabgen_catalog_LayeredPaneLayout);
        tabgen_catalog_LayeredPaneLayout.setHorizontalGroup(
            tabgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_catalog_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(tabgen_catalog_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabgen_catalog_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        tabgen_catalog_LayeredPaneLayout.setVerticalGroup(
            tabgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_catalog_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_catalog_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_catalog_Label)
                    .addComponent(tabgen_catalog_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_catalog_LayeredPane.setLayer(tabgen_catalog_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_catalog_LayeredPane.setLayer(tabgen_catalog_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_LayeredPane.add(tabgen_catalog_LayeredPane);

        tabgen_schema_Label.setText("Schema :");

        javax.swing.GroupLayout tabgen_schema_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_schema_LayeredPane);
        tabgen_schema_LayeredPane.setLayout(tabgen_schema_LayeredPaneLayout);
        tabgen_schema_LayeredPaneLayout.setHorizontalGroup(
            tabgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_schema_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(tabgen_schema_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabgen_schema_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        tabgen_schema_LayeredPaneLayout.setVerticalGroup(
            tabgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_schema_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_schema_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_schema_Label)
                    .addComponent(tabgen_schema_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_schema_LayeredPane.setLayer(tabgen_schema_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_schema_LayeredPane.setLayer(tabgen_schema_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_LayeredPane.add(tabgen_schema_LayeredPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 26;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 16);
        main_LayeredPane.add(tabgen_LayeredPane, gridBagConstraints);

        tabgen_pkcol_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Primary Key Column", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        tabgen_pkcol_LayeredPane.setLayout(new java.awt.GridLayout(1, 0));

        tabgen_pkcol_name_Label.setText("Name :");

        javax.swing.GroupLayout tabgen_pkcol_name_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_pkcol_name_LayeredPane);
        tabgen_pkcol_name_LayeredPane.setLayout(tabgen_pkcol_name_LayeredPaneLayout);
        tabgen_pkcol_name_LayeredPaneLayout.setHorizontalGroup(
            tabgen_pkcol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_pkcol_name_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(tabgen_pkcol_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabgen_pkcol_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        tabgen_pkcol_name_LayeredPaneLayout.setVerticalGroup(
            tabgen_pkcol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_pkcol_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_pkcol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_pkcol_name_Label)
                    .addComponent(tabgen_pkcol_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_pkcol_name_LayeredPane.setLayer(tabgen_pkcol_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_pkcol_name_LayeredPane.setLayer(tabgen_pkcol_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_pkcol_LayeredPane.add(tabgen_pkcol_name_LayeredPane);

        tabgen_pkcol_value_Label.setText("Value :");

        javax.swing.GroupLayout tabgen_pkcol_value_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_pkcol_value_LayeredPane);
        tabgen_pkcol_value_LayeredPane.setLayout(tabgen_pkcol_value_LayeredPaneLayout);
        tabgen_pkcol_value_LayeredPaneLayout.setHorizontalGroup(
            tabgen_pkcol_value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_pkcol_value_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(tabgen_pkcol_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabgen_pkcol_value_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        tabgen_pkcol_value_LayeredPaneLayout.setVerticalGroup(
            tabgen_pkcol_value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_pkcol_value_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_pkcol_value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_pkcol_value_Label)
                    .addComponent(tabgen_pkcol_value_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_pkcol_value_LayeredPane.setLayer(tabgen_pkcol_value_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_pkcol_value_LayeredPane.setLayer(tabgen_pkcol_value_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_pkcol_LayeredPane.add(tabgen_pkcol_value_LayeredPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 26;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 16);
        main_LayeredPane.add(tabgen_pkcol_LayeredPane, gridBagConstraints);

        tabgen_valuecol_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Value Column", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        tabgen_valuecol_LayeredPane.setLayout(new java.awt.GridLayout(2, 2));

        tabgen_valuecol_name_Label.setText("Name :");

        javax.swing.GroupLayout tabgen_valuecol_name_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_valuecol_name_LayeredPane);
        tabgen_valuecol_name_LayeredPane.setLayout(tabgen_valuecol_name_LayeredPaneLayout);
        tabgen_valuecol_name_LayeredPaneLayout.setHorizontalGroup(
            tabgen_valuecol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_name_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(tabgen_valuecol_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabgen_valuecol_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        tabgen_valuecol_name_LayeredPaneLayout.setVerticalGroup(
            tabgen_valuecol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_valuecol_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_valuecol_name_Label)
                    .addComponent(tabgen_valuecol_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_valuecol_name_LayeredPane.setLayer(tabgen_valuecol_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_valuecol_name_LayeredPane.setLayer(tabgen_valuecol_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_valuecol_LayeredPane.add(tabgen_valuecol_name_LayeredPane);

        tabgen_valuecol_initialValue_Label.setText("Initial Value :");

        javax.swing.GroupLayout tabgen_valuecol_initialValue_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_valuecol_initialValue_LayeredPane);
        tabgen_valuecol_initialValue_LayeredPane.setLayout(tabgen_valuecol_initialValue_LayeredPaneLayout);
        tabgen_valuecol_initialValue_LayeredPaneLayout.setHorizontalGroup(
            tabgen_valuecol_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_initialValue_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(tabgen_valuecol_initialValue_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabgen_valuecol_initialValue_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        tabgen_valuecol_initialValue_LayeredPaneLayout.setVerticalGroup(
            tabgen_valuecol_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_initialValue_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_valuecol_initialValue_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_valuecol_initialValue_Label)
                    .addComponent(tabgen_valuecol_initialValue_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_valuecol_initialValue_LayeredPane.setLayer(tabgen_valuecol_initialValue_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_valuecol_initialValue_LayeredPane.setLayer(tabgen_valuecol_initialValue_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_valuecol_LayeredPane.add(tabgen_valuecol_initialValue_LayeredPane);

        tabgen_valuecol_allocationSize_Label.setText("Allocation Size :");

        javax.swing.GroupLayout tabgen_valuecol_allocationSize_LayeredPaneLayout = new javax.swing.GroupLayout(tabgen_valuecol_allocationSize_LayeredPane);
        tabgen_valuecol_allocationSize_LayeredPane.setLayout(tabgen_valuecol_allocationSize_LayeredPaneLayout);
        tabgen_valuecol_allocationSize_LayeredPaneLayout.setHorizontalGroup(
            tabgen_valuecol_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_allocationSize_LayeredPaneLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(tabgen_valuecol_allocationSize_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabgen_valuecol_allocationSize_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        tabgen_valuecol_allocationSize_LayeredPaneLayout.setVerticalGroup(
            tabgen_valuecol_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabgen_valuecol_allocationSize_LayeredPaneLayout.createSequentialGroup()
                .addGroup(tabgen_valuecol_allocationSize_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabgen_valuecol_allocationSize_Label)
                    .addComponent(tabgen_valuecol_allocationSize_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabgen_valuecol_allocationSize_LayeredPane.setLayer(tabgen_valuecol_allocationSize_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        tabgen_valuecol_allocationSize_LayeredPane.setLayer(tabgen_valuecol_allocationSize_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tabgen_valuecol_LayeredPane.add(tabgen_valuecol_allocationSize_LayeredPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 26;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 19, 16);
        main_LayeredPane.add(tabgen_valuecol_LayeredPane, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(main_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void strategy_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strategy_ComboBoxActionPerformed
        GenerationType generationType = (GenerationType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();
        if (generationType == GenerationType.SEQUENCE) {
            setEnablePanel(seqgen_LayeredPane, true);
            setEnablePanel(tabgen_LayeredPane, false);
            setEnablePanel(tabgen_pkcol_LayeredPane, false);
            setEnablePanel(tabgen_valuecol_LayeredPane, false);
//            setSize(getWidth(), 300);
        } else if (generationType == GenerationType.TABLE) {
            setEnablePanel(seqgen_LayeredPane, false);
            setEnablePanel(tabgen_LayeredPane, true);
            setEnablePanel(tabgen_pkcol_LayeredPane, true);
            setEnablePanel(tabgen_valuecol_LayeredPane, true);
//            setSize(getWidth(), 440);
        } else {
            setEnablePanel(seqgen_LayeredPane, false);
            setEnablePanel(tabgen_LayeredPane, false);
            setEnablePanel(tabgen_pkcol_LayeredPane, false);
            setEnablePanel(tabgen_valuecol_LayeredPane, false);
//            setSize(getWidth(), 200);
        }
        manageFieldState();

    }//GEN-LAST:event_strategy_ComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane main_LayeredPane;
    private javax.swing.JLayeredPane seqgen_LayeredPane;
    private javax.swing.JLabel seqgen_allocationSize_Label;
    private javax.swing.JLayeredPane seqgen_allocationSize_LayeredPane;
    private javax.swing.JSpinner seqgen_allocationSize_Spinner;
    private javax.swing.JLabel seqgen_catalog_Label;
    private javax.swing.JLayeredPane seqgen_catalog_LayeredPane;
    private javax.swing.JTextField seqgen_catalog_TextField;
    private javax.swing.JLabel seqgen_initialValue_Label;
    private javax.swing.JLayeredPane seqgen_initialValue_LayeredPane;
    private javax.swing.JSpinner seqgen_initialValue_Spinner;
    private javax.swing.JLabel seqgen_name_Label;
    private javax.swing.JLayeredPane seqgen_name_LayeredPane;
    private javax.swing.JTextField seqgen_name_TextField;
    private javax.swing.JLabel seqgen_schema_Label;
    private javax.swing.JLayeredPane seqgen_schema_LayeredPane;
    private javax.swing.JTextField seqgen_schema_TextField;
    private javax.swing.JLabel seqgen_seqname_Label;
    private javax.swing.JLayeredPane seqgen_seqname_LayeredPane;
    private javax.swing.JTextField seqgen_seqname_TextField;
    private javax.swing.JComboBox strategy_ComboBox;
    private javax.swing.JLabel strategy_Label;
    private javax.swing.JLayeredPane strategy_LayeredPane;
    private javax.swing.JLayeredPane tabgen_LayeredPane;
    private javax.swing.JLabel tabgen_catalog_Label;
    private javax.swing.JLayeredPane tabgen_catalog_LayeredPane;
    private javax.swing.JTextField tabgen_catalog_TextField;
    private javax.swing.JLabel tabgen_name_Label;
    private javax.swing.JLayeredPane tabgen_name_LayeredPane;
    private javax.swing.JTextField tabgen_name_TextField;
    private javax.swing.JLayeredPane tabgen_pkcol_LayeredPane;
    private javax.swing.JLabel tabgen_pkcol_name_Label;
    private javax.swing.JLayeredPane tabgen_pkcol_name_LayeredPane;
    private javax.swing.JTextField tabgen_pkcol_name_TextField;
    private javax.swing.JLabel tabgen_pkcol_value_Label;
    private javax.swing.JLayeredPane tabgen_pkcol_value_LayeredPane;
    private javax.swing.JTextField tabgen_pkcol_value_TextField;
    private javax.swing.JLabel tabgen_schema_Label;
    private javax.swing.JLayeredPane tabgen_schema_LayeredPane;
    private javax.swing.JTextField tabgen_schema_TextField;
    private javax.swing.JLabel tabgen_tableName_Label;
    private javax.swing.JLayeredPane tabgen_tableName_LayeredPane;
    private javax.swing.JTextField tabgen_tableName_TextField;
    private javax.swing.JLayeredPane tabgen_valuecol_LayeredPane;
    private javax.swing.JLabel tabgen_valuecol_allocationSize_Label;
    private javax.swing.JLayeredPane tabgen_valuecol_allocationSize_LayeredPane;
    private javax.swing.JSpinner tabgen_valuecol_allocationSize_Spinner;
    private javax.swing.JLabel tabgen_valuecol_initialValue_Label;
    private javax.swing.JLayeredPane tabgen_valuecol_initialValue_LayeredPane;
    private javax.swing.JSpinner tabgen_valuecol_initialValue_Spinner;
    private javax.swing.JLabel tabgen_valuecol_name_Label;
    private javax.swing.JLayeredPane tabgen_valuecol_name_LayeredPane;
    private javax.swing.JTextField tabgen_valuecol_name_TextField;
    // End of variables declaration//GEN-END:variables
}
