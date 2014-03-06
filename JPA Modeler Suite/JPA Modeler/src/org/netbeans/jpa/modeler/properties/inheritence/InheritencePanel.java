package org.netbeans.jpa.modeler.properties.inheritence;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.spec.DiscriminatorType;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
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
public class InheritencePanel extends GenericEmbeddedEditor<InheritenceHandler> {

    private ModelerFile modelerFile;
    private EntityWidget entityWidget;
    private String type;
    private InheritenceHandler classSpec;

    @Override
    public void init() {
        initComponents();
        strategy_ComboBox.removeAllItems();
        Property[] strategyProperties = new Property[]{
            new Property(InheritanceType.SINGLE_TABLE, "Single table per class hierarchy (Default)"),
            new Property(InheritanceType.JOINED, "Joined Strategy"),
            new Property(InheritanceType.TABLE_PER_CLASS, "Table per concrete entity class")
        };
        strategy_ComboBox.setModel(new DefaultComboBoxModel(strategyProperties));
        column_type_ComboBox.removeAllItems();
        Property[] columnTypeProperties = new Property[]{
            new Property(DiscriminatorType.STRING, "String (Default)"),
            new Property(DiscriminatorType.CHAR, "Single character"),
            new Property(DiscriminatorType.INTEGER, "Integer")

        };
        column_type_ComboBox.setModel(new DefaultComboBoxModel(columnTypeProperties));

    }

    private void setStrategySelectedItem(InheritanceType type) {
        strategy_ComboBox.setSelectedItem(strategy_ComboBox.getItemAt(0));
        for (int i = 0; i < strategy_ComboBox.getItemCount(); i++) {
            Property property = (Property) strategy_ComboBox.getItemAt(i);
            if ((InheritanceType) property.getKey() == type) {
                strategy_ComboBox.setSelectedItem(property);
                break;
            }
        }
    }

    private void setColumnTypeSelectedItem(DiscriminatorType type) {
        column_type_ComboBox.setSelectedItem(column_type_ComboBox.getItemAt(0));
        for (int i = 0; i < column_type_ComboBox.getItemCount(); i++) {
            Property property = (Property) column_type_ComboBox.getItemAt(i);
            if ((DiscriminatorType) property.getKey() == type) {
                column_type_ComboBox.setSelectedItem(property);
                break;
            }
        }
    }

    @Override
    public InheritenceHandler getValue() {
        InheritanceType inheritanceType = (InheritanceType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();
        if (type.equals("ROOT") || type.equals("BRANCH")) {
            classSpec.getInheritance().setStrategy(inheritanceType);
            DiscriminatorType discriminatorType = (DiscriminatorType) ((Property) column_type_ComboBox.getSelectedItem()).getKey();
            classSpec.getDiscriminatorColumn().setDiscriminatorType(discriminatorType);
            classSpec.getDiscriminatorColumn().setName(column_name_TextField.getText());
            classSpec.getDiscriminatorColumn().setLength((Integer) column_length_Spinner.getValue());
            classSpec.getDiscriminatorColumn().setColumnDefinition(column_def_TextArea.getText());

        } else {
            classSpec.setInheritance(null);
            classSpec.setDiscriminatorColumn(null);
//            classSpec.getInheritance().setStrategy(null);
//            classSpec.getDiscriminatorColumn().setDiscriminatorType(null);
//            classSpec.getDiscriminatorColumn().setName(null);
//            classSpec.getDiscriminatorColumn().setLength(null);
//            classSpec.getDiscriminatorColumn().setColumnDefinition(null);

        }
        if (type.equals("LEAF") || type.equals("BRANCH")) {
            classSpec.setDiscriminatorValue(value_TextField.getText());
        } else {
            classSpec.setDiscriminatorValue(null);
        }

        return classSpec;
    }

    @Override
    public void setValue(InheritenceHandler classSpec) {
        this.classSpec = classSpec;

        GeneralizationFlowWidget outgoingGeneralizationFlowWidget = entityWidget.getOutgoingGeneralizationFlowWidget();
        List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = entityWidget.getIncomingGeneralizationFlowWidgets();

        if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
            outgoingGeneralizationFlowWidget = null;
        }

        if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "SINGLETON";
            setEnablePanel(column_LayeredPane, false);
            setEnablePanel(strategy_LayeredPane, false);
            setEnablePanel(value_LayeredPane, false);
        } else if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "LEAF";
            setEnablePanel(column_LayeredPane, false);
            setEnablePanel(strategy_LayeredPane, false);
            setEnablePanel(value_LayeredPane, true);
        } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "ROOT";
            setEnablePanel(column_LayeredPane, true);
            setEnablePanel(strategy_LayeredPane, true);
            setEnablePanel(value_LayeredPane, false);
        } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "BRANCH";
            setEnablePanel(column_LayeredPane, true);
            setEnablePanel(strategy_LayeredPane, true);
            setEnablePanel(value_LayeredPane, true);
        } else {
            type = null;
            setEnablePanel(column_LayeredPane, false);
            setEnablePanel(strategy_LayeredPane, false);
            setEnablePanel(value_LayeredPane, false);
        }

        value_TextField.setText(classSpec.getDiscriminatorValue());

        if (type != null && type.equals("LEAF")) {
            EntityWidget superEntityWidget = (EntityWidget) entityWidget.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
            InheritenceHandler superClassSpec = (InheritenceHandler) superEntityWidget.getBaseElementSpec();
            setStrategySelectedItem(superClassSpec.getInheritance().getStrategy());
            setColumnTypeSelectedItem(superClassSpec.getDiscriminatorColumn().getDiscriminatorType());
            column_name_TextField.setText(superClassSpec.getDiscriminatorColumn().getName());
            if (superClassSpec.getDiscriminatorColumn().getLength() != null) {
                column_length_Spinner.setValue(superClassSpec.getDiscriminatorColumn().getLength());
            } else {
                column_length_Spinner.setValue(30);
            }
            column_def_TextArea.setText(superClassSpec.getDiscriminatorColumn().getColumnDefinition());

        } else {
            setStrategySelectedItem(classSpec.getInheritance().getStrategy());
            setColumnTypeSelectedItem(classSpec.getDiscriminatorColumn().getDiscriminatorType());
            column_name_TextField.setText(classSpec.getDiscriminatorColumn().getName());
            if (classSpec.getDiscriminatorColumn().getLength() != null) {
                column_length_Spinner.setValue(classSpec.getDiscriminatorColumn().getLength());
            } else {
                column_length_Spinner.setValue(30);
            }
            column_def_TextArea.setText(classSpec.getDiscriminatorColumn().getColumnDefinition());

        }

    }

    /**
     * Creates new form Inheritence
     */
    public InheritencePanel(ModelerFile modelerFile, EntityWidget entityWidget) {
        this.modelerFile = modelerFile;
        this.entityWidget = entityWidget;
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
        value_LayeredPane = new javax.swing.JLayeredPane();
        value_Label = new javax.swing.JLabel();
        value_TextField = new javax.swing.JTextField();
        column_LayeredPane = new javax.swing.JLayeredPane();
        column_name_LayeredPane = new javax.swing.JLayeredPane();
        column_name_Label = new javax.swing.JLabel();
        column_name_TextField = new javax.swing.JTextField();
        column_type_LayeredPane = new javax.swing.JLayeredPane();
        column_type_Label = new javax.swing.JLabel();
        column_type_ComboBox = new javax.swing.JComboBox();
        column_length_LayeredPane = new javax.swing.JLayeredPane();
        column_length_Label = new javax.swing.JLabel();
        column_length_Spinner = new javax.swing.JSpinner();
        column_def_LayeredPane = new javax.swing.JLayeredPane();
        column_def_Label = new javax.swing.JLabel();
        column_def_ScrollPane = new javax.swing.JScrollPane();
        column_def_TextArea = new javax.swing.JTextArea();

        main_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Inheritance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        main_LayeredPane.setLayout(new java.awt.GridBagLayout());

        strategy_Label.setText("Strategy :");

        strategy_ComboBox.setToolTipText("");

        javax.swing.GroupLayout strategy_LayeredPaneLayout = new javax.swing.GroupLayout(strategy_LayeredPane);
        strategy_LayeredPane.setLayout(strategy_LayeredPaneLayout);
        strategy_LayeredPaneLayout.setHorizontalGroup(
            strategy_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strategy_LayeredPaneLayout.createSequentialGroup()
                .addComponent(strategy_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(strategy_ComboBox, 0, 412, Short.MAX_VALUE)
                .addContainerGap())
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
        gridBagConstraints.ipadx = 412;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 0, 0);
        main_LayeredPane.add(strategy_LayeredPane, gridBagConstraints);

        value_Label.setText("DiscriminatorValue :");

        javax.swing.GroupLayout value_LayeredPaneLayout = new javax.swing.GroupLayout(value_LayeredPane);
        value_LayeredPane.setLayout(value_LayeredPaneLayout);
        value_LayeredPaneLayout.setHorizontalGroup(
            value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(value_LayeredPaneLayout.createSequentialGroup()
                .addComponent(value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(value_TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );
        value_LayeredPaneLayout.setVerticalGroup(
            value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(value_LayeredPaneLayout.createSequentialGroup()
                .addGroup(value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(value_Label)
                    .addComponent(value_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );
        value_LayeredPane.setLayer(value_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        value_LayeredPane.setLayer(value_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 406;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        main_LayeredPane.add(value_LayeredPane, gridBagConstraints);

        column_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Discriminator Column", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N

        column_name_Label.setText("Name :");

        column_name_TextField.setText("DTYPE");
        column_name_TextField.setToolTipText("Default DTYPE");

        javax.swing.GroupLayout column_name_LayeredPaneLayout = new javax.swing.GroupLayout(column_name_LayeredPane);
        column_name_LayeredPane.setLayout(column_name_LayeredPaneLayout);
        column_name_LayeredPaneLayout.setHorizontalGroup(
            column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_name_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(column_name_TextField))
        );
        column_name_LayeredPaneLayout.setVerticalGroup(
            column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(column_name_Label)
                    .addComponent(column_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        column_name_LayeredPane.setLayer(column_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_name_LayeredPane.setLayer(column_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        column_type_Label.setText("Type :");

        javax.swing.GroupLayout column_type_LayeredPaneLayout = new javax.swing.GroupLayout(column_type_LayeredPane);
        column_type_LayeredPane.setLayout(column_type_LayeredPaneLayout);
        column_type_LayeredPaneLayout.setHorizontalGroup(
            column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_type_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_type_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(column_type_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        column_type_LayeredPaneLayout.setVerticalGroup(
            column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_type_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(column_type_Label)
                    .addComponent(column_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        column_type_LayeredPane.setLayer(column_type_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_type_LayeredPane.setLayer(column_type_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        column_length_Label.setText("Length :");

        column_length_Spinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(30), Integer.valueOf(0), null, Integer.valueOf(1)));
        column_length_Spinner.setToolTipText("Default 30");

        javax.swing.GroupLayout column_length_LayeredPaneLayout = new javax.swing.GroupLayout(column_length_LayeredPane);
        column_length_LayeredPane.setLayout(column_length_LayeredPaneLayout);
        column_length_LayeredPaneLayout.setHorizontalGroup(
            column_length_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_length_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_length_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(column_length_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        column_length_LayeredPaneLayout.setVerticalGroup(
            column_length_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_length_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_length_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(column_length_Label)
                    .addComponent(column_length_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        column_length_LayeredPane.setLayer(column_length_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_length_LayeredPane.setLayer(column_length_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        column_def_Label.setText("Column Definition :");

        column_def_TextArea.setColumns(20);
        column_def_TextArea.setRows(5);
        column_def_ScrollPane.setViewportView(column_def_TextArea);

        javax.swing.GroupLayout column_def_LayeredPaneLayout = new javax.swing.GroupLayout(column_def_LayeredPane);
        column_def_LayeredPane.setLayout(column_def_LayeredPaneLayout);
        column_def_LayeredPaneLayout.setHorizontalGroup(
            column_def_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_def_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_def_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(column_def_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
        );
        column_def_LayeredPaneLayout.setVerticalGroup(
            column_def_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_def_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_def_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(column_def_LayeredPaneLayout.createSequentialGroup()
                        .addComponent(column_def_Label)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(column_def_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                .addContainerGap())
        );
        column_def_LayeredPane.setLayer(column_def_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_def_LayeredPane.setLayer(column_def_ScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout column_LayeredPaneLayout = new javax.swing.GroupLayout(column_LayeredPane);
        column_LayeredPane.setLayout(column_LayeredPaneLayout);
        column_LayeredPaneLayout.setHorizontalGroup(
            column_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(column_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(column_name_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(column_type_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(column_length_LayeredPane)
                    .addComponent(column_def_LayeredPane))
                .addContainerGap())
        );
        column_LayeredPaneLayout.setVerticalGroup(
            column_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(column_name_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(column_type_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(column_length_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(column_def_LayeredPane)
                .addGap(16, 16, 16))
        );
        column_LayeredPane.setLayer(column_name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_type_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_length_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_def_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 307;
        gridBagConstraints.ipady = 99;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 24, 12);
        main_LayeredPane.add(column_LayeredPane, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(main_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 406, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane column_LayeredPane;
    private javax.swing.JLabel column_def_Label;
    private javax.swing.JLayeredPane column_def_LayeredPane;
    private javax.swing.JScrollPane column_def_ScrollPane;
    private javax.swing.JTextArea column_def_TextArea;
    private javax.swing.JLabel column_length_Label;
    private javax.swing.JLayeredPane column_length_LayeredPane;
    private javax.swing.JSpinner column_length_Spinner;
    private javax.swing.JLabel column_name_Label;
    private javax.swing.JLayeredPane column_name_LayeredPane;
    private javax.swing.JTextField column_name_TextField;
    private javax.swing.JComboBox column_type_ComboBox;
    private javax.swing.JLabel column_type_Label;
    private javax.swing.JLayeredPane column_type_LayeredPane;
    private javax.swing.JLayeredPane main_LayeredPane;
    private javax.swing.JComboBox strategy_ComboBox;
    private javax.swing.JLabel strategy_Label;
    private javax.swing.JLayeredPane strategy_LayeredPane;
    private javax.swing.JLabel value_Label;
    private javax.swing.JLayeredPane value_LayeredPane;
    private javax.swing.JTextField value_TextField;
    // End of variables declaration//GEN-END:variables

}
