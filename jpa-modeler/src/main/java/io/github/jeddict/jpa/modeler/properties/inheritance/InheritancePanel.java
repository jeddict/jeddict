package io.github.jeddict.jpa.modeler.properties.inheritance;

import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.InheritanceStateType;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.BRANCH;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.LEAF;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.ROOT;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.spec.DiscriminatorColumn;
import io.github.jeddict.jpa.spec.DiscriminatorType;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.InheritanceType;
import io.github.jeddict.jpa.spec.extend.InheritanceHandler;
import static java.lang.Boolean.TRUE;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.customattr.Property;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;

/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
public class InheritancePanel extends GenericEmbeddedEditor<InheritanceHandler> {

    private final ModelerFile modelerFile;
    private final EntityWidget entityWidget;
    private InheritanceStateType type;
    private InheritanceHandler inheritanceHandler;
    
    private static final Map<InheritanceType, String> ADVANTAGES = new HashMap<>();
    private static final Map<InheritanceType, String> DISADVANTAGES = new HashMap<>();
    
    static {
        ADVANTAGES.put(InheritanceType.SINGLE_TABLE, "Simplest to implement and performs better | No complex joins, unions, or subselects");
        ADVANTAGES.put(InheritanceType.JOINED, "Normalized | Define constraints on subclass properties");
        ADVANTAGES.put(InheritanceType.TABLE_PER_CLASS, "Define constraints on subclass properties | Easier to map a preexisting legacy schema");
        DISADVANTAGES.put(InheritanceType.SINGLE_TABLE, "Not normalized | All columns of subclass properties must be nullable");
        DISADVANTAGES.put(InheritanceType.JOINED, "Not as fast as the SINGLE_TABLE strategy");
        DISADVANTAGES.put(InheritanceType.TABLE_PER_CLASS, "Huge performance hit | UNION not supported by all relational databases | Poor support for polymorphic relationships");
        
    }
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

    private void setStrategySelectedItem(Inheritance inheritance) {
        strategy_ComboBox.setSelectedItem(strategy_ComboBox.getItemAt(0));
        for (int i = 0; i < strategy_ComboBox.getItemCount(); i++) {
            Property property = (Property) strategy_ComboBox.getItemAt(i);
            if (inheritance != null && (InheritanceType) property.getKey() == inheritance.getStrategy()) {
                strategy_ComboBox.setSelectedItem(property);
                break;
            }
        }
        manageStrategyTypeState();
    }

    private void setColumnTypeSelectedItem(DiscriminatorColumn col) {
        column_type_ComboBox.setSelectedItem(column_type_ComboBox.getItemAt(0));
        for (int i = 0; i < column_type_ComboBox.getItemCount(); i++) {
            Property property = (Property) column_type_ComboBox.getItemAt(i);
            if (col != null && (DiscriminatorType) property.getKey() == col.getDiscriminatorType()) {
                column_type_ComboBox.setSelectedItem(property);
                break;
            }
        }
    }

    @Override
    public InheritanceHandler getValue() {
        InheritanceType inheritanceType = (InheritanceType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();
        if (type == ROOT || type == BRANCH) {
            if (inheritanceHandler.getInheritance() == null) {
                inheritanceHandler.setInheritance(new Inheritance());
            }
            inheritanceHandler.getInheritance().setStrategy(inheritanceType);
            DiscriminatorType discriminatorType = (DiscriminatorType) ((Property) column_type_ComboBox.getSelectedItem()).getKey();

            if (column_name_TextField.getText().isEmpty() && column_length_Spinner.getValue().equals(30)
                    && column_def_TextArea.getText().isEmpty() && discriminatorType == DiscriminatorType.STRING) {
                inheritanceHandler.setDiscriminatorColumn(null);
            } else {
                inheritanceHandler.setDiscriminatorColumn(new DiscriminatorColumn());
                inheritanceHandler.getDiscriminatorColumn().setDiscriminatorType(discriminatorType);
                inheritanceHandler.getDiscriminatorColumn().setName(column_name_TextField.getText());
                inheritanceHandler.getDiscriminatorColumn().setLength((Integer) column_length_Spinner.getValue());
                inheritanceHandler.getDiscriminatorColumn().setColumnDefinition(column_def_TextArea.getText());

            }

        } else {
            inheritanceHandler.setInheritance(null);
            inheritanceHandler.setDiscriminatorColumn(null);
        }
        
        boolean isAbstract = TRUE.equals(entityWidget.getBaseElementSpec().getAbstract()); 
        if (!isAbstract && (type == LEAF || type == BRANCH || type == ROOT)) {
            inheritanceHandler.setDiscriminatorValue(value_TextField.getText());
        } else {
            inheritanceHandler.setDiscriminatorValue(null);
        }

        return inheritanceHandler;
    }

    @Override
    public void setValue(InheritanceHandler inheritanceHandler) {
        this.inheritanceHandler = inheritanceHandler;
        type = entityWidget.getInheritanceState();
        boolean isAbstract = TRUE.equals(entityWidget.getBaseElementSpec().getAbstract()); 
         switch (type) {
            case SINGLETON:
                setEnablePanel(strategy_LayeredPane, false);
                setEnablePanel(value_LayeredPane, false);
                break;
            case LEAF:
                setEnablePanel(strategy_LayeredPane, false);
                setEnablePanel(value_LayeredPane, !isAbstract);
                break;
            case ROOT:
            case BRANCH:
                setEnablePanel(strategy_LayeredPane, true);
                setEnablePanel(value_LayeredPane, !isAbstract);
                break;
        }

        value_TextField.setText(inheritanceHandler.getDiscriminatorValue());

        if (type != null && type == LEAF) {
            EntityWidget superEntityWidget = (EntityWidget) entityWidget.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
            InheritanceHandler superClassSpec = (InheritanceHandler) superEntityWidget.getBaseElementSpec();
            setUIValue(superClassSpec);
        } else {
            setUIValue(inheritanceHandler);
        }

    }

    private void setUIValue(InheritanceHandler classSpec) {
        setStrategySelectedItem(classSpec.getInheritance());
        setColumnTypeSelectedItem(classSpec.getDiscriminatorColumn());
        if (classSpec.getDiscriminatorColumn() != null) {
            column_name_TextField.setText(classSpec.getDiscriminatorColumn().getName());
            if (classSpec.getDiscriminatorColumn().getLength() != null) {
                column_length_Spinner.setValue(classSpec.getDiscriminatorColumn().getLength());
            } else {
                column_length_Spinner.setValue(30);
            }
            column_def_TextArea.setText(classSpec.getDiscriminatorColumn().getColumnDefinition());
        } else {
            column_name_TextField.setText("");
            column_length_Spinner.setValue(30);
            column_def_TextArea.setText("");
        }

    }

    /**
     * Creates new form Inheritance
     */
    public InheritancePanel(ModelerFile modelerFile, EntityWidget entityWidget) {
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
        advantagesPane = new javax.swing.JLayeredPane();
        advantagesTitle = new javax.swing.JLabel();
        advantagesText = new javax.swing.JLabel();
        disadvantagesPane = new javax.swing.JLayeredPane();
        disadvantagesTitle = new javax.swing.JLabel();
        disadvantagesText = new javax.swing.JLabel();

        main_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Inheritance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        main_LayeredPane.setPreferredSize(new java.awt.Dimension(540, 456));

        strategy_Label.setText("Strategy :");

        strategy_ComboBox.setToolTipText("");
        strategy_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strategy_ComboBoxActionPerformed(evt);
            }
        });

        strategy_LayeredPane.setLayer(strategy_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        strategy_LayeredPane.setLayer(strategy_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout strategy_LayeredPaneLayout = new javax.swing.GroupLayout(strategy_LayeredPane);
        strategy_LayeredPane.setLayout(strategy_LayeredPaneLayout);
        strategy_LayeredPaneLayout.setHorizontalGroup(
            strategy_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strategy_LayeredPaneLayout.createSequentialGroup()
                .addComponent(strategy_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(strategy_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        value_Label.setText("DiscriminatorValue :");

        value_LayeredPane.setLayer(value_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        value_LayeredPane.setLayer(value_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout value_LayeredPaneLayout = new javax.swing.GroupLayout(value_LayeredPane);
        value_LayeredPane.setLayout(value_LayeredPaneLayout);
        value_LayeredPaneLayout.setHorizontalGroup(
            value_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(value_LayeredPaneLayout.createSequentialGroup()
                .addComponent(value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(value_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        column_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Discriminator Column", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(51, 51, 51))); // NOI18N

        column_name_Label.setText("Name :");

        column_name_TextField.setText("DTYPE");
        column_name_TextField.setToolTipText("Default DTYPE");

        column_name_LayeredPane.setLayer(column_name_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_name_LayeredPane.setLayer(column_name_TextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout column_name_LayeredPaneLayout = new javax.swing.GroupLayout(column_name_LayeredPane);
        column_name_LayeredPane.setLayout(column_name_LayeredPaneLayout);
        column_name_LayeredPaneLayout.setHorizontalGroup(
            column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_name_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_name_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(column_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        column_name_LayeredPaneLayout.setVerticalGroup(
            column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_name_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_name_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(column_name_Label)
                    .addComponent(column_name_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        column_type_Label.setText("Type :");

        column_type_LayeredPane.setLayer(column_type_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_type_LayeredPane.setLayer(column_type_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout column_type_LayeredPaneLayout = new javax.swing.GroupLayout(column_type_LayeredPane);
        column_type_LayeredPane.setLayout(column_type_LayeredPaneLayout);
        column_type_LayeredPaneLayout.setHorizontalGroup(
            column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_type_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_type_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(column_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        column_type_LayeredPaneLayout.setVerticalGroup(
            column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_type_LayeredPaneLayout.createSequentialGroup()
                .addGroup(column_type_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(column_type_Label)
                    .addComponent(column_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        column_length_Label.setText("Length :");

        column_length_Spinner.setModel(new javax.swing.SpinnerNumberModel(30, 0, null, 1));
        column_length_Spinner.setToolTipText("Default 30");

        column_length_LayeredPane.setLayer(column_length_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_length_LayeredPane.setLayer(column_length_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout column_length_LayeredPaneLayout = new javax.swing.GroupLayout(column_length_LayeredPane);
        column_length_LayeredPane.setLayout(column_length_LayeredPaneLayout);
        column_length_LayeredPaneLayout.setHorizontalGroup(
            column_length_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_length_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_length_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        column_def_Label.setText("Column Definition :");

        column_def_TextArea.setColumns(20);
        column_def_TextArea.setRows(5);
        column_def_ScrollPane.setViewportView(column_def_TextArea);

        column_def_LayeredPane.setLayer(column_def_Label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_def_LayeredPane.setLayer(column_def_ScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout column_def_LayeredPaneLayout = new javax.swing.GroupLayout(column_def_LayeredPane);
        column_def_LayeredPane.setLayout(column_def_LayeredPaneLayout);
        column_def_LayeredPaneLayout.setHorizontalGroup(
            column_def_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(column_def_LayeredPaneLayout.createSequentialGroup()
                .addComponent(column_def_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(column_def_ScrollPane))
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

        column_LayeredPane.setLayer(column_name_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_type_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_length_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        column_LayeredPane.setLayer(column_def_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

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

        advantagesPane.setLayout(new java.awt.BorderLayout());

        advantagesTitle.setForeground(new java.awt.Color(51, 51, 51));
        advantagesTitle.setText("Advantages :");
        advantagesTitle.setPreferredSize(new java.awt.Dimension(90, 14));
        advantagesPane.add(advantagesTitle, java.awt.BorderLayout.WEST);

        advantagesText.setForeground(new java.awt.Color(0, 153, 0));
        advantagesPane.add(advantagesText, java.awt.BorderLayout.CENTER);

        disadvantagesPane.setLayout(new java.awt.BorderLayout());

        disadvantagesTitle.setForeground(new java.awt.Color(51, 51, 51));
        disadvantagesTitle.setText("Disadvantages :");
        disadvantagesTitle.setPreferredSize(new java.awt.Dimension(90, 14));
        disadvantagesPane.add(disadvantagesTitle, java.awt.BorderLayout.WEST);

        disadvantagesText.setForeground(new java.awt.Color(215, 80, 80));
        disadvantagesPane.add(disadvantagesText, java.awt.BorderLayout.CENTER);

        main_LayeredPane.setLayer(strategy_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        main_LayeredPane.setLayer(value_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        main_LayeredPane.setLayer(column_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        main_LayeredPane.setLayer(advantagesPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        main_LayeredPane.setLayer(disadvantagesPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout main_LayeredPaneLayout = new javax.swing.GroupLayout(main_LayeredPane);
        main_LayeredPane.setLayout(main_LayeredPaneLayout);
        main_LayeredPaneLayout.setHorizontalGroup(
            main_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_LayeredPaneLayout.createSequentialGroup()
                .addGroup(main_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_LayeredPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(strategy_LayeredPane))
                    .addGroup(main_LayeredPaneLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(main_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(value_LayeredPane, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(column_LayeredPane)))
                    .addGroup(main_LayeredPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(advantagesPane))
                    .addGroup(main_LayeredPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(disadvantagesPane)))
                .addContainerGap())
        );
        main_LayeredPaneLayout.setVerticalGroup(
            main_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_LayeredPaneLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(strategy_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(value_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(column_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(advantagesPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disadvantagesPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void strategy_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strategy_ComboBoxActionPerformed
        manageStrategyTypeState();
    }//GEN-LAST:event_strategy_ComboBoxActionPerformed

    private void manageStrategyTypeState(){
        InheritanceType inheritanceType = (InheritanceType) ((Property) strategy_ComboBox.getSelectedItem()).getKey();
        advantagesText.setText(ADVANTAGES.get(inheritanceType));
        disadvantagesText.setText(DISADVANTAGES.get(inheritanceType));
        setEnablePanel(column_LayeredPane,
                (inheritanceType == InheritanceType.JOINED || inheritanceType == InheritanceType.SINGLE_TABLE)
                && (type == BRANCH || type == ROOT));
        
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane advantagesPane;
    private javax.swing.JLabel advantagesText;
    private javax.swing.JLabel advantagesTitle;
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
    private javax.swing.JLayeredPane disadvantagesPane;
    private javax.swing.JLabel disadvantagesText;
    private javax.swing.JLabel disadvantagesTitle;
    private javax.swing.JLayeredPane main_LayeredPane;
    private javax.swing.JComboBox strategy_ComboBox;
    private javax.swing.JLabel strategy_Label;
    private javax.swing.JLayeredPane strategy_LayeredPane;
    private javax.swing.JLabel value_Label;
    private javax.swing.JLayeredPane value_LayeredPane;
    private javax.swing.JTextField value_TextField;
    // End of variables declaration//GEN-END:variables

}
