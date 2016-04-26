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
package org.netbeans.jpa.modeler.source.generator.ui;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.lang.model.SourceVersion;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.jcode.core.util.ProjectHelper;
import static org.netbeans.jcode.core.util.ProjectHelper.getJavaProjects;
import static org.netbeans.jcode.core.util.SourceGroups.getPackageForFolder;
import org.netbeans.jcode.layer.DefaultBusinessLayer;
import org.netbeans.jcode.layer.DefaultControllerLayer;
import org.netbeans.jcode.layer.DefaultViewerLayer;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.TechContext;
import static org.netbeans.jcode.layer.Technology.NONE_LABEL;
import static org.netbeans.jcode.layer.Technology.Type.BUSINESS;
import static org.netbeans.jcode.layer.Technology.Type.CONTROLLER;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.stack.config.panel.DefaultConfigPanel;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.netbeans.jcode.ui.source.ProjectCellRenderer;
import org.netbeans.jcode.ui.source.SourceRootCellRenderer;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.window.GenericDialog;
import org.netbeans.orm.converter.generator.GeneratorUtil;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Gaurav_Gupta
 */
public class GenerateCodeDialog extends GenericDialog
        implements PropertyChangeListener {

    private static final Preferences technologyLayerPref = NbPreferences.forModule(Generator.class);//ProjectUtils.getPreferences(prj, ProjectUtils.class, true);
    private FileObject modelerFileObject;
    private final String modelerFilePackage;
    private final EntityMappings entityMappings;
    private final ApplicationConfigData configData;

    /**
     * Creates new form GenerateCodeDialog
     */
    public GenerateCodeDialog(ModelerFile modelerFile) {
        this.configData = new ApplicationConfigData();
        this.modelerFileObject = modelerFile.getFileObject();
        this.entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
        initComponents();
        propertyChangeSupport = new PropertyChangeSupport(this);
        populateExistingProjectElementGroup();
        setPackage(entityMappings.getPackage());
        generateDefaultValue.setSelected(GeneratorUtil.isGenerateDefaultValue());
        propertyChangeSupport.addPropertyChangeListener(this);
        this.setTitle(NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.title"));
        getRootPane().setDefaultButton(generateSourceCode);

        modelerFilePackage = getPackageForFolder(sourceGroup, modelerFileObject.getParent());
        initLayer();
    }

    void initLayer() {
        configPane.removeAll();
        configPane.setVisible(false);
        if (!ProjectHelper.isJavaEE6AndHigher(getTargetPoject())) {
            businessLayerCombo.setEnabled(false);
            controllerLayerCombo.setEnabled(false);
            viewerLayerCombo.setEnabled(false);
            this.pack();
            return;
        } else {
            businessLayerCombo.setEnabled(true);
        }
        businessLayerCombo.setModel(new DefaultComboBoxModel(Generator.getBusinessService().toArray()));
        controllerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(new DefaultControllerLayer())}));
        viewerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(new DefaultViewerLayer())}));
        controllerLayerCombo.setEnabled(false);
        viewerLayerCombo.setEnabled(false);
     

        TechContext businessContext = Generator.get(technologyLayerPref.get(BUSINESS.name(), DefaultBusinessLayer.class.getSimpleName()));
        TechContext controllerContext = Generator.get(technologyLayerPref.get(CONTROLLER.name(), DefaultControllerLayer.class.getSimpleName()));
        TechContext viewerContext = Generator.get(technologyLayerPref.get(VIEWER.name(), DefaultViewerLayer.class.getSimpleName()));
        if (businessContext != null) {
            businessLayerCombo.setSelectedItem(businessContext);
            if (businessContext.isValid() && controllerContext != null) {
                controllerLayerCombo.setSelectedItem(controllerContext);
                if (controllerContext.isValid() && viewerContext != null) {
                    viewerLayerCombo.setSelectedItem(viewerContext);
                }
            }
        }

        this.pack();
    }

    private final static int business_PANEL_INDEX = 0, CONTROLLER_PANEL_INDEX = 1, VIEWER_PANEL_INDEX = 2;
    private LayerConfigPanel[] layerConfigPanels = new LayerConfigPanel[3];

    private void setTechPanel(int index, JPanel techLayerPanel, TechContext technologyLayer) {
        try {
            LayerConfigPanel techPanel;
            if (technologyLayer.isValid()) {
                techPanel = technologyLayer.getTechnology().panel().newInstance();
            } else {
                techPanel = new DefaultConfigPanel();
            }

            techPanel.init(modelerFilePackage, targetPoject, sourceGroup);
            techPanel.read();
            techLayerPanel.removeAll();
            techLayerPanel.add(techPanel);

            configPane.removeAll();
            configPane.setVisible(false);
            layerConfigPanels[index]= techPanel;
            if (index == business_PANEL_INDEX) {
                getConfigData().setBussinesLayerConfig(techPanel.getConfigData());
                getConfigData().setBussinesLayerGenerator(technologyLayer.getGenerator());
                addLayerTab(getBusinessLayer().toString(), businessPanel);
            } else if (index == CONTROLLER_PANEL_INDEX) {
                getConfigData().setControllerLayerConfig(techPanel.getConfigData());
                getConfigData().setControllerLayerGenerator(technologyLayer.getGenerator());
                addLayerTab(getBusinessLayer().toString(), businessPanel);
                addLayerTab(getControllerLayer().toString(), controllerPanel);
            } else if (index == VIEWER_PANEL_INDEX) {
                getConfigData().setViewerLayerConfig(techPanel.getConfigData());
                getConfigData().setViewerLayerGenerator(technologyLayer.getGenerator());
                addLayerTab(getBusinessLayer().toString(), businessPanel);
                addLayerTab(getControllerLayer().toString(), controllerPanel);
                addLayerTab(getViewerLayer().toString(), viewerPanel);
            }

            if (configPane.getComponentCount() > index) {
                configPane.setSelectedIndex(index);
            }

            if (configPane.getComponentCount() >= 1) {
                configPane.setVisible(true);
            }
            this.pack();

        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addLayerTab(String title, JPanel panel) {
        if (StringUtils.isBlank(title) || title.equalsIgnoreCase(NONE_LABEL)) {
            return;
        }
        configPane.addTab(title, panel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionPane = new javax.swing.JLayeredPane();
        packageLabel = new javax.swing.JLabel();
        resourcePackageCombo = new javax.swing.JComboBox();
        businessLayerCombo = new javax.swing.JComboBox();
        businessLayerLabel = new javax.swing.JLabel();
        targetProjectCombo = new javax.swing.JComboBox();
        targetProjectLabel = new javax.swing.JLabel();
        sourceFolderCombo = new javax.swing.JComboBox();
        sourceFolderLabel = new javax.swing.JLabel();
        viewerLayerLabel = new javax.swing.JLabel();
        viewerLayerCombo = new javax.swing.JComboBox();
        controllerLayerCombo = new javax.swing.JComboBox();
        controllerLayerLabel = new javax.swing.JLabel();
        configPane = new javax.swing.JTabbedPane();
        businessPanel = new javax.swing.JPanel();
        controllerPanel = new javax.swing.JPanel();
        viewerPanel = new javax.swing.JPanel();
        actionPane = new javax.swing.JLayeredPane();
        generateDefaultValue = new javax.swing.JCheckBox();
        actionLayeredPane = new javax.swing.JLayeredPane();
        generateSourceCode = new javax.swing.JButton();
        cencelGenerateCode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(150, 17));

        resourcePackageCombo.setEditable(true);
        resourcePackageCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                resourcePackageComboItemStateChanged(evt);
            }
        });
        resourcePackageCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourcePackageComboActionPerformed(evt);
            }
        });

        resourcePackageCombo.setEditable(true);
        businessLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                businessLayerComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(businessLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.businessLayerLabel.text")); // NOI18N

        targetProjectCombo.setMinimumSize(new java.awt.Dimension(50, 20));
        targetProjectCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                targetProjectComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(targetProjectLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.targetProjectLabel.text")); // NOI18N
        targetProjectLabel.setPreferredSize(new java.awt.Dimension(150, 17));

        sourceFolderCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sourceFolderComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(sourceFolderLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.sourceFolderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(viewerLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.viewerLayerLabel.text")); // NOI18N

        resourcePackageCombo.setEditable(true);
        viewerLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewerLayerComboItemStateChanged(evt);
            }
        });

        resourcePackageCombo.setEditable(true);
        controllerLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                controllerLayerComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(controllerLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.controllerLayerLabel.text")); // NOI18N

        businessPanel.setLayout(new javax.swing.BoxLayout(businessPanel, javax.swing.BoxLayout.LINE_AXIS));
        configPane.addTab(org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.businessPanel.TabConstraints.tabTitle"), businessPanel); // NOI18N

        controllerPanel.setLayout(new javax.swing.BoxLayout(controllerPanel, javax.swing.BoxLayout.LINE_AXIS));
        configPane.addTab(org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.controllerPanel.TabConstraints.tabTitle"), controllerPanel); // NOI18N

        viewerPanel.setLayout(new javax.swing.BoxLayout(viewerPanel, javax.swing.BoxLayout.LINE_AXIS));
        configPane.addTab(org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.viewerPanel.TabConstraints.tabTitle"), viewerPanel); // NOI18N

        javax.swing.GroupLayout optionPaneLayout = new javax.swing.GroupLayout(optionPane);
        optionPane.setLayout(optionPaneLayout);
        optionPaneLayout.setHorizontalGroup(
            optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPaneLayout.createSequentialGroup()
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPaneLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionPaneLayout.createSequentialGroup()
                                .addComponent(targetProjectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(targetProjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(sourceFolderLabel)
                                .addGap(18, 18, 18)
                                .addComponent(sourceFolderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(optionPaneLayout.createSequentialGroup()
                                .addComponent(viewerLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(viewerLayerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(optionPaneLayout.createSequentialGroup()
                                .addComponent(controllerLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(controllerLayerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(optionPaneLayout.createSequentialGroup()
                                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(businessLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(packageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resourcePackageCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(businessLayerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(optionPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(configPane)))
                .addContainerGap())
        );
        optionPaneLayout.setVerticalGroup(
            optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPaneLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(targetProjectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sourceFolderLabel)
                        .addComponent(targetProjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sourceFolderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resourcePackageCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPaneLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(businessLayerLabel))
                    .addComponent(businessLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPaneLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(controllerLayerLabel))
                    .addComponent(controllerLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPaneLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(viewerLayerLabel))
                    .addComponent(viewerLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(configPane, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );
        optionPane.setLayer(packageLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(resourcePackageCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(businessLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(businessLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(targetProjectCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(targetProjectLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(sourceFolderCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(sourceFolderLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(viewerLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(viewerLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(controllerLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(controllerLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(configPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/jpa/modeler/source/generator/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(generateDefaultValue, bundle.getString("GenerateCodeDialog.generateDefaultValue.text")); // NOI18N
        generateDefaultValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateDefaultValueActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(generateSourceCode, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.text")); // NOI18N
        generateSourceCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateSourceCodeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cencelGenerateCode, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.cencelGenerateCode.text")); // NOI18N
        cencelGenerateCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cencelGenerateCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actionLayeredPaneLayout = new javax.swing.GroupLayout(actionLayeredPane);
        actionLayeredPane.setLayout(actionLayeredPaneLayout);
        actionLayeredPaneLayout.setHorizontalGroup(
            actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(generateSourceCode, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cencelGenerateCode, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        actionLayeredPaneLayout.setVerticalGroup(
            actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionLayeredPaneLayout.createSequentialGroup()
                .addGroup(actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateSourceCode)
                    .addComponent(cencelGenerateCode))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        actionLayeredPane.setLayer(generateSourceCode, javax.swing.JLayeredPane.DEFAULT_LAYER);
        actionLayeredPane.setLayer(cencelGenerateCode, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout actionPaneLayout = new javax.swing.GroupLayout(actionPane);
        actionPane.setLayout(actionPaneLayout);
        actionPaneLayout.setHorizontalGroup(
            actionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateDefaultValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(actionLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        actionPaneLayout.setVerticalGroup(
            actionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPaneLayout.createSequentialGroup()
                .addGroup(actionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generateDefaultValue)
                    .addComponent(actionLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        actionPane.setLayer(generateDefaultValue, javax.swing.JLayeredPane.DEFAULT_LAYER);
        actionPane.setLayer(actionLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(actionPane))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(optionPane)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(optionPane)
                .addGap(5, 5, 5)
                .addComponent(actionPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public final static String PROP_TARGET_PROJECT = "TARGET_PROJECT"; // NOI18N
    public final static String PROP_NO_TARGET_PROJECT = "NO_TARGET_PROJECT"; // NOI18N
    public final static String PROP_SOURCE_FOLDER = "SOURCE_FOLDER"; // NOI18N
    public final static String PROP_NO_SOURCE_FOLDER = "NO_SOURCE_FOLDER"; // NOI18N

    private Project targetPoject = null;
    private final Project orignalProject = null;
    private SourceGroup sourceGroup = null;
    private boolean noTargetProject = false;
    private boolean noOpenTargets = false;

    private void targetProjectComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_targetProjectComboItemStateChanged
        // TODO add your handling code here:
        setTargetPoject((Project) targetProjectCombo.getSelectedItem());
        populateSourceFolderCombo();

        String prop = getTargetPoject() == null ? PROP_NO_TARGET_PROJECT : PROP_TARGET_PROJECT;

        getPropertyChangeSupport().firePropertyChange(prop, null, evt);
        
        initLayer();
    }//GEN-LAST:event_targetProjectComboItemStateChanged

    private void sourceFolderComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sourceFolderComboItemStateChanged
        // TODO add your handling code here:
        setSourceGroup((SourceGroup) sourceFolderCombo.getSelectedItem());

        populatePackageCombo();
    }//GEN-LAST:event_sourceFolderComboItemStateChanged

    private void generateSourceCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSourceCodeActionPerformed
        if(!hasError()){
            setVisible(false);
            this.setDialogResult(javax.swing.JOptionPane.OK_OPTION);
            dispose();
            store();
        }
    }//GEN-LAST:event_generateSourceCodeActionPerformed

    private boolean hasError(){
        if (sourceGroup == null) {
            NotifyDescriptor d = new NotifyDescriptor.Message("Please select the Source Folder .", NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Source Folder");
            DialogDisplayer.getDefault().notify(d);
            return true;
        }
        if(!SourceVersion.isName(getPackage())){
            NotifyDescriptor d = new NotifyDescriptor.Message("Please select the Entity Package .", NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Entity Package");
            DialogDisplayer.getDefault().notify(d);
            return true;
        }
        
        for (int i = 0; i < configPane.getComponentCount(); i++) {
            if (layerConfigPanels[i].hasError()) {
                configPane.setSelectedIndex(i);
                return true;
            } else {
                layerConfigPanels[i].store();
            }
        }
        return false;
    }
    
    private void store(){
        entityMappings.setPackage(getPackage());
        if (getBusinessLayer() != null) {
            technologyLayerPref.put(BUSINESS.name(), getBusinessLayer().getGenerator().getClass().getSimpleName());
            if (getControllerLayer() != null) {
                technologyLayerPref.put(CONTROLLER.name(), getControllerLayer().getGenerator().getClass().getSimpleName());
                if (getViewerLayer() != null) {
                    technologyLayerPref.put(VIEWER.name(), getViewerLayer().getGenerator().getClass().getSimpleName());
                }
            }
        }
    }
    
    
    private void cencelGenerateCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cencelGenerateCodeActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cencelGenerateCodeActionPerformed

    private void generateDefaultValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateDefaultValueActionPerformed
        GeneratorUtil.setGenerateDefaultValue(generateDefaultValue.isSelected());
    }//GEN-LAST:event_generateDefaultValueActionPerformed

    private void resourcePackageComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_resourcePackageComboItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_resourcePackageComboItemStateChanged

    private void businessLayerComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_businessLayerComboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            changebusinessLayer(getBusinessLayer());
        }
    }//GEN-LAST:event_businessLayerComboItemStateChanged

    private void viewerLayerComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_viewerLayerComboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            changeViewerLayer(getViewerLayer());
        }
    }//GEN-LAST:event_viewerLayerComboItemStateChanged

    private void controllerLayerComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_controllerLayerComboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            changeControllerLayer(getControllerLayer());
        }
    }//GEN-LAST:event_controllerLayerComboItemStateChanged

    private void resourcePackageComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourcePackageComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resourcePackageComboActionPerformed

    private void changebusinessLayer(TechContext businessLayer) {
        System.out.println("Generator.getController(businessLayer) " + Generator.getController(businessLayer));
        controllerLayerCombo.setModel(new DefaultComboBoxModel(Generator.getController(businessLayer).toArray()));
        controllerLayerCombo.setEnabled(businessLayer.isValid());
        viewerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(new DefaultViewerLayer())}));
        viewerLayerCombo.setEnabled(false);
        setTechPanel(business_PANEL_INDEX, businessPanel, businessLayer);
        if (!businessLayer.isValid()) {
            viewerLayerCombo.setEnabled(false);
        }
    }

    private void changeControllerLayer(TechContext controllerLayer) {
        viewerLayerCombo.setModel(new DefaultComboBoxModel(Generator.getViewer(controllerLayer).toArray()));
        viewerLayerCombo.setEnabled(controllerLayer.isValid());
        setTechPanel(CONTROLLER_PANEL_INDEX, controllerPanel, controllerLayer);
    }

    private void changeViewerLayer(TechContext viewerLayer) {
        setTechPanel(VIEWER_PANEL_INDEX, viewerPanel, viewerLayer);
    }

    public TechContext getBusinessLayer() {
        return (TechContext) businessLayerCombo.getModel().getSelectedItem();
    }

    public TechContext getViewerLayer() {
        return (TechContext) viewerLayerCombo.getModel().getSelectedItem();
    }

    public TechContext getControllerLayer() {
        return (TechContext) controllerLayerCombo.getModel().getSelectedItem();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String propName = "";

        if (event != null) {
            propName = event.getPropertyName();
        }

        if (propName.equals(PROP_TARGET_PROJECT)) {
            noTargetProject = false;
        } else if (propName.equals(PROP_NO_TARGET_PROJECT)) {
            noTargetProject = true;
        }

        String msg;

        if (noOpenTargets) {
            msg = NbBundle.getMessage(
                    GenerateCodeDialog.class, "MSG_NoOpenTargets"); // NIO18N
        } else if (noTargetProject) {
            msg = NbBundle.getMessage(
                    GenerateCodeDialog.class, "MSG_NoTargetJavaProject"); // NIO18N
        }

    }

    //
    // target project added elements processing
    //
    private void enableExistingProjectElementGroup(boolean enable) {
        targetProjectCombo.setEnabled(enable);
        sourceFolderCombo.setEnabled(enable);
    }

    private void populateExistingProjectElementGroup() {
        ProjectCellRenderer projectCellRenderer = new ProjectCellRenderer(targetProjectCombo.getRenderer());
        targetProjectCombo.setRenderer(projectCellRenderer);
        List<Project> list = getJavaProjects();

        if (orignalProject != null && !list.contains(orignalProject)) {
            list.add(orignalProject);
        }

        if (list == null || list.isEmpty()) {
            noOpenTargets = true;
            enableExistingProjectElementGroup(false);
        } else {
            //list.add(0, null);

            DefaultComboBoxModel projectsModel = new DefaultComboBoxModel(list.toArray());
            targetProjectCombo.setModel(projectsModel);

            // Issue Fix #5850 Start
            Project project = FileOwnerQuery.getOwner(modelerFileObject);
            if (project != null) {
                targetProjectCombo.setSelectedItem(project);
            } else {
                targetProjectCombo.setSelectedIndex(-1);
            }
            // Issue Fix #5850 End

            // When the selected index was set to -1 it reset the targetPrj
            // value.  Since the targetPrj was simply initialized with the
            // origPrj value, just set it again.
            setTargetPoject(orignalProject);
            selectTargetProject();
            noOpenTargets = false;
            // enableExistingProjectElementGroup(true);
        }

        propertyChange(null);
    }

    private void selectTargetProject() {
        if (getTargetPoject() == null) {
            if (targetProjectCombo.getSelectedItem() != null) {
                setTargetPoject((Project) targetProjectCombo.getSelectedItem());
                sourceFolderCombo.setEnabled(true);
            } else {
                sourceFolderCombo.setEnabled(false);
            }
        } else {
            targetProjectCombo.setSelectedItem(getTargetPoject());
            sourceFolderCombo.setEnabled(true);
        }

        if (targetProjectCombo.getSelectedItem() != null) {
            populateSourceFolderCombo();
            sourceFolderCombo.setEnabled(true);
        }
    }

    private void populateSourceFolderCombo() {
        SourceRootCellRenderer srcCellRenderer
                = new SourceRootCellRenderer(sourceFolderCombo.getRenderer());
        sourceFolderCombo.setRenderer(srcCellRenderer);
        ArrayList<SourceGroup> srcRoots = new ArrayList<>();
        int index = 0;
        FileObject sfo = null;

        if (getSourceGroup() != null) {
            sfo = getSourceGroup().getRootFolder();
        }
        if (targetPoject != null) {
            Sources sources = ProjectUtils.getSources(targetPoject);
            if (sources != null) {
                SourceGroup[] srcGrps = sources.getSourceGroups(
                        JavaProjectConstants.SOURCES_TYPE_JAVA);

                if (srcGrps != null) {
                    for (SourceGroup g : srcGrps) {
                        if (g != null) {
                            srcRoots.add(g);

                            if (g.getRootFolder() != null
                                    && g.getRootFolder().equals(sfo)) {
                                index = srcRoots.size() - 1;
                            }
                        }
                    }
                }
            }
        }

        DefaultComboBoxModel rootsModel
                = new DefaultComboBoxModel(srcRoots.toArray());

        sourceFolderCombo.setModel(rootsModel);

        if (srcRoots.size() > 0) {
            sourceFolderCombo.setSelectedIndex(index);
            setSourceGroup(srcRoots.get(index));
            sourceFolderCombo.setEnabled(true);
        } else {
            sourceFolderCombo.setEnabled(false);
        }
        populatePackageCombo();
    }

    private void populatePackageCombo() {
        if (sourceGroup != null) {
            resourcePackageCombo.setRenderer(PackageView.listRenderer());
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSize() > 0) {
                model.setSelectedItem(model.getElementAt(0));
            }
            resourcePackageCombo.setModel(model);
        }
    }

    /**
     * @return the sourceGroup
     */
    public SourceGroup getSourceGroup() {
        return sourceGroup;
    }

    /**
     * @param sourceGroup the sourceGroup to set
     */
    public void setSourceGroup(SourceGroup sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    /**
     * @return the modelerFileObject
     */
    public FileObject getModelerFileObject() {
        return modelerFileObject;
    }

    /**
     * @param modelerFileObject the modelerFileObject to set
     */
    public void setModelerFileObject(FileObject modelerFileObject) {
        this.modelerFileObject = modelerFileObject;
    }

    /**
     * @return the targetPoject
     */
    public Project getTargetPoject() {
        return targetPoject;
    }

    /**
     * @param targetPoject the targetPoject to set
     */
    public void setTargetPoject(Project targetPoject) {
        this.targetPoject = targetPoject;
    }

    public String getPackage() {
        return ((JTextComponent) resourcePackageCombo.getEditor().getEditorComponent()).getText();
    }

    private void setPackage(String _package) {
        ComboBoxModel model = resourcePackageCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(_package)) {
                model.setSelectedItem(model.getElementAt(i));
                return;
            }
        }
        ((JTextComponent) resourcePackageCombo.getEditor().getEditorComponent()).setText(_package);
    }

    private PropertyChangeSupport propertyChangeSupport = null;

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane actionLayeredPane;
    private javax.swing.JLayeredPane actionPane;
    private javax.swing.JComboBox businessLayerCombo;
    private javax.swing.JLabel businessLayerLabel;
    private javax.swing.JPanel businessPanel;
    private javax.swing.JButton cencelGenerateCode;
    private javax.swing.JTabbedPane configPane;
    private javax.swing.JComboBox controllerLayerCombo;
    private javax.swing.JLabel controllerLayerLabel;
    private javax.swing.JPanel controllerPanel;
    private javax.swing.JCheckBox generateDefaultValue;
    private javax.swing.JButton generateSourceCode;
    private javax.swing.JLayeredPane optionPane;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JComboBox resourcePackageCombo;
    private javax.swing.JComboBox sourceFolderCombo;
    private javax.swing.JLabel sourceFolderLabel;
    private javax.swing.JComboBox targetProjectCombo;
    private javax.swing.JLabel targetProjectLabel;
    private javax.swing.JComboBox viewerLayerCombo;
    private javax.swing.JLabel viewerLayerLabel;
    private javax.swing.JPanel viewerPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the configData
     */
    public ApplicationConfigData getConfigData() {
        configData.setProject(getTargetPoject());
        configData.setSourceGroup(getSourceGroup());
        return configData;
    }

}

class JavaProjectConstants {

    private JavaProjectConstants() {
    }

    /**
     * Java package root sources type.
     *
     * @see org.netbeans.api.project.Sources
     */
    public static final String SOURCES_TYPE_JAVA = "java"; // NOI18N

    /**
     * Package root sources type for resources, if these are not put together
     * with Java sources.
     *
     * @see org.netbeans.api.project.Sources
     * @since org.netbeans.modules.java.project/1 1.11
     */
    public static final String SOURCES_TYPE_RESOURCES = "resources"; // NOI18N

    /**
     * Hint for <code>SourceGroupModifier</code> to create a
     * <code>SourceGroup</code> for main project codebase.
     *
     * @see org.netbeans.api.project.SourceGroupModifier
     * @since org.netbeans.modules.java.project/1 1.24
     */
    public static final String SOURCES_HINT_MAIN = "main"; //NOI18N

    /**
     * Hint for <code>SourceGroupModifier</code> to create a
     * <code>SourceGroup</code> for project's tests.
     *
     * @see org.netbeans.api.project.SourceGroupModifier
     * @since org.netbeans.modules.java.project/1 1.24
     */
    public static final String SOURCES_HINT_TEST = "test"; //NOI18N

    /**
     * Standard artifact type representing a JAR file, presumably used as a Java
     * library of some kind.
     *
     * @see org.netbeans.api.project.ant.AntArtifact
     */
    public static final String ARTIFACT_TYPE_JAR = "jar"; // NOI18N

    /**
     * Standard artifact type representing a folder containing classes,
     * presumably used as a Java library of some kind.
     *
     * @see org.netbeans.api.project.ant.AntArtifact
     * @since org.netbeans.modules.java.project/1 1.4
     */
    public static final String ARTIFACT_TYPE_FOLDER = "folder"; //NOI18N

    /**
     * Standard command for running Javadoc on a project.
     *
     * @see org.netbeans.spi.project.ActionProvider
     */
    public static final String COMMAND_JAVADOC = "javadoc"; // NOI18N

    /**
     * Standard command for reloading a class in a foreign VM and continuing
     * debugging.
     *
     * @see org.netbeans.spi.project.ActionProvider
     */
    public static final String COMMAND_DEBUG_FIX = "debug.fix"; // NOI18N

}
