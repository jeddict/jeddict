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
package io.github.jeddict.jpa.modeler.source.generator.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;
import javax.lang.model.SourceVersion;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import io.github.jeddict.jcode.util.POMManager;
import io.github.jeddict.jcode.util.ProjectHelper;
import static io.github.jeddict.jcode.util.ProjectHelper.getJavaProjects;
import io.github.jeddict.jcode.util.ProjectType;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.TechContext;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.LayerConfigPanel;
import static io.github.jeddict.jcode.RegistryType.CONSUL;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.BUSINESS;
import static io.github.jeddict.jcode.annotation.Technology.Type.CONTROLLER;
import static io.github.jeddict.jcode.annotation.Technology.Type.VIEWER;
import io.github.jeddict.jcode.impl.DefaultBusinessLayer;
import io.github.jeddict.jcode.impl.DefaultControllerLayer;
import io.github.jeddict.jcode.impl.DefaultViewerLayer;
import io.github.jeddict.jcode.ui.ProjectCellRenderer;
import io.github.jeddict.jcode.util.PreferenceUtils;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import static io.github.jeddict.jpa.spec.extend.ProjectType.GATEWAY;
import static io.github.jeddict.jpa.spec.extend.ProjectType.MONOLITH;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ERROR_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.SUCCESS_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.WARNING_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.WORKSPACE_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.window.GenericDialog;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import static org.openide.awt.Mnemonics.setLocalizedText;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.NbPreferences;
import static io.github.jeddict.jpa.spec.extend.ProjectType.MICROSERVICE;

/**
 *
 * @author Gaurav_Gupta
 */
public class GenerateCodeDialog extends GenericDialog {

    private final Preferences technologyPref;
    private final FileObject modelerFileObject;
    private final EntityMappings entityMappings;
    private final ApplicationConfigData configData;
    private final JPAModelerScene scene;
    private final ModelerFile modelerFile;
    private EntityGenerationSettingDialog entityGenerationSettingDialog;
    private final ProjectInfo targetProjectInfo = new ProjectInfo();
    private final ProjectInfo gatewayProjectInfo = new ProjectInfo();

    private final static String SOURCES_TYPE_JAVA = "java";
    private final static String COMPLETE_APPLICATION = "COMPLETE_APPLICATION";

    /**
     * Creates new form GenerateCodeDialog
     *
     * @param modelerFile
     */
    public GenerateCodeDialog(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
        this.scene = (JPAModelerScene) modelerFile.getModelerScene();
        this.configData = new ApplicationConfigData();
        this.modelerFileObject = modelerFile.getFileObject();
        this.entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
        this.technologyPref = NbPreferences.forModule(Generator.class);
        initUIComponents();
    }

    private void manageEntityGenerationSetting() {
        Optional<WorkSpace> optionalWorkSpace = entityMappings.findGeneratedWorkSpace();
        WorkSpace selectedWorkSpace = null;
        if (optionalWorkSpace.isPresent()) {
            selectedWorkSpace = optionalWorkSpace.get();
            if (optionalWorkSpace.get() == entityMappings.getRootWorkSpace()
                    || optionalWorkSpace.get() == entityMappings.getCurrentWorkSpace()) {
                //skip
            } else if (optionalWorkSpace.get() == entityMappings.getPreviousWorkSpace()) {
                selectedWorkSpace = entityMappings.getCurrentWorkSpace();
                entityMappings.setGenerateWorkSpaceClass(selectedWorkSpace);
            }
        }
        this.entityGenerationSettingDialog = new EntityGenerationSettingDialog(scene, selectedWorkSpace);
    }

    private boolean isSupportedProject(ProjectInfo projectInfo) {
        ProjectType projectType = projectInfo.getProject() != null ? ProjectHelper.getProjectType(projectInfo.getProject()) : null;
        return projectType != null && projectType == ProjectType.WEB
                && projectInfo.getProject().getClass().getName().contains("Maven");
    }

    private void initUIComponents() {
        manageEntityGenerationSetting();
        initComponents();
        this.setTitle(NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.title"));
        setCaptureWindowSize(false);
        setDefaultButton(generateSourceCode);
        setMicroservice(entityMappings.getProjectType() == MICROSERVICE);
        setArchState();

        populateProjectCombo(targetProjectCombo, targetProjectInfo);
        populatePackageCombo(targetProjectPackageCombo, targetProjectInfo);
        populateProjectCombo(gatewayProjectCombo, gatewayProjectInfo);
        populatePackageCombo(gatewayProjectPackageCombo, gatewayProjectInfo);
        setEntityPackage(StringUtils.isNotBlank(entityMappings.getEntityPackage()) ? entityMappings.getEntityPackage() : "domain");
        setTargetPackage(StringUtils.isNotBlank(entityMappings.getProjectPackage()) ? entityMappings.getProjectPackage() : "");
//      setGatewayPackage(StringUtils.isNotBlank(entityMappings.getGatewayPackage()) ? entityMappings.getGatewayPackage() : "");
        this.pack();
    }

    private void initLayer() {
        setCompleteApplicationCompVisibility(false);

        configPane.removeAll();
        configPane.setVisible(false);

        infoLabel.setText("");
        businessLayerCombo.setModel(new DefaultComboBoxModel(Generator.getBusinessService(isMicroservice() || isGateway()).toArray()));
        controllerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(DefaultControllerLayer.class)}));
        viewerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(DefaultViewerLayer.class)}));
        businessLayerCombo.setEnabled(true);
        controllerLayerCombo.setEnabled(false);
        viewerLayerCombo.setEnabled(false);

        TechContext businessContext = Generator.get(
                technologyPref.get(BUSINESS.name(),
                        DefaultBusinessLayer.class.getSimpleName())
        );
        TechContext controllerContext = Generator.get(
                technologyPref.get(CONTROLLER.name(),
                        DefaultControllerLayer.class.getSimpleName())
        );
        TechContext viewerContext = Generator.get(
                technologyPref.get(VIEWER.name(),
                        DefaultViewerLayer.class.getSimpleName())
        );

        SwingUtilities.invokeLater(() -> {

            if (businessContext != null) {
                businessLayerCombo.getModel().setSelectedItem(businessContext);
                if (businessContext.isValid() && controllerContext != null) {
                    controllerLayerCombo.getModel().setSelectedItem(controllerContext);
                    if (controllerContext.isValid() && viewerContext != null) {
                        viewerLayerCombo.getModel().setSelectedItem(viewerContext);
                    }
                }
            }
        });
    }

    private void setTechPanel(TechContext techContext) {
        if ((isMicroservice() || isGateway())
                && techContext.getTechnology().type() == VIEWER
                && techContext.getTechnology().microservice()) {
            techContext.createPanel(gatewayProjectInfo.getProject(), gatewayProjectInfo.getSourceGroup(), getGatewayPackage());
        } else {
            techContext.createPanel(targetProjectInfo.getProject(), targetProjectInfo.getSourceGroup(), getTargetPackage());
        }
        configPane.removeAll();
        configPane.setVisible(false);
        boolean nonePanel = techContext.getTechnology().panel() == LayerConfigPanel.class;
        switch (techContext.getTechnology().type()) {
            case BUSINESS:
                getConfigData().setBussinesTechContext(techContext);
                addLayerTab(getConfigData().getBussinesTechContext());
                getConfigData().setControllerTechContext(null);
                getConfigData().setViewerTechContext(null);
                if (nonePanel) {
                    getConfigData().setBussinesTechContext(null);
                }
                break;
            case CONTROLLER:
                getConfigData().setControllerTechContext(techContext);
                addLayerTab(getConfigData().getBussinesTechContext());
                addLayerTab(getConfigData().getControllerTechContext());
                getConfigData().setViewerTechContext(null);
                if (nonePanel) {
                    getConfigData().setControllerTechContext(null);
                }
                break;
            case VIEWER:
                getConfigData().setViewerTechContext(techContext);
                addLayerTab(getConfigData().getBussinesTechContext());
                addLayerTab(getConfigData().getControllerTechContext());
                addLayerTab(getConfigData().getViewerTechContext());
                if (nonePanel) {
                    getConfigData().setViewerTechContext(null);
                }
                break;
            default:
                break;
        }

        if (configPane.getComponentCount() >= 1) {
            if (!nonePanel) {
                configPane.setSelectedComponent(techContext.getPanel());
            }
            configPane.setVisible(true);
        }
        this.pack();
        setVisible(true);
    }

    private void refreshLayer() {
        getBusinessLayer().resetPanel();
        getControllerLayer().resetPanel();
        getViewerLayer().resetPanel();
        if (getViewerLayer().isValid()) {
            setTechPanel(getViewerLayer());
        } else if (getControllerLayer().isValid()) {
            setTechPanel(getControllerLayer());
        } else if (getBusinessLayer().isValid()) {
            setTechPanel(getBusinessLayer());
        }
    }

    private void addLayerTab(TechContext techContext) {
        Technology tech = techContext.getTechnology();
        if (tech.panel() != LayerConfigPanel.class) {
            String title = tech.label();
            int index = tech.tabIndex() - 1;
            if (techContext.getPanel() == null) {
                techContext.createPanel(targetProjectInfo.getProject(), targetProjectInfo.getSourceGroup(), getTargetPackage());
            }
            if (index < 0) {
                configPane.addTab(title, null, techContext.getPanel(), tech.description());
            } else {
                configPane.insertTab(title, null, techContext.getPanel(), tech.description(), index);
            }
            techContext.getSiblingTechContext()
                    .stream()
                    .filter(tc -> isCompleteApplication() ? true : tc.getTechnology().entityGenerator())
                    .forEach(context -> this.addLayerTab(context));
        }
    }

    private void changeBusinessLayer(TechContext businessLayer) {
        controllerLayerCombo.setModel(new DefaultComboBoxModel(Generator.getController(businessLayer, isMicroservice() || isGateway()).toArray()));
        controllerLayerCombo.setEnabled(businessLayer.isValid());
        viewerLayerCombo.setModel(new DefaultComboBoxModel(new Object[]{new TechContext(DefaultViewerLayer.class)}));
        viewerLayerCombo.setEnabled(false);
        setTechPanel(businessLayer);
    }

    private void changeControllerLayer(TechContext controllerLayer) {
        viewerLayerCombo.setModel(new DefaultComboBoxModel(Generator.getViewer(controllerLayer, isMicroservice() || isGateway()).toArray()));
        viewerLayerCombo.setEnabled(controllerLayer.isValid());
        setTechPanel(controllerLayer);
    }

    private void changeViewerLayer(TechContext viewerLayer) {
        setTechPanel(viewerLayer);
    }

    private TechContext getBusinessLayer() {
        return (TechContext) businessLayerCombo.getModel().getSelectedItem();
    }

    private TechContext getViewerLayer() {
        return (TechContext) viewerLayerCombo.getModel().getSelectedItem();
    }

    private TechContext getControllerLayer() {
        return (TechContext) controllerLayerCombo.getModel().getSelectedItem();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        archbuttonGroup = new javax.swing.ButtonGroup();
        optionPane = new javax.swing.JLayeredPane();
        wrapperLayeredPane = new javax.swing.JLayeredPane();
        archLayeredPane = new javax.swing.JLayeredPane();
        monolithRadioButton = new javax.swing.JRadioButton();
        microservicesRadioButton = new javax.swing.JRadioButton();
        gatewayRadioButton = new javax.swing.JRadioButton();
        targetProjectLayeredPane = new javax.swing.JLayeredPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        targetProjectLabel = new javax.swing.JLabel();
        targetProjectCombo = new javax.swing.JComboBox();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        targetProjectPackageLabel = new javax.swing.JLabel();
        targetProjectPackageCombo = new javax.swing.JComboBox();
        gatewayProjectLayeredPane = new javax.swing.JLayeredPane();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        gatewayProjectLabel = new javax.swing.JLabel();
        gatewayProjectCombo = new javax.swing.JComboBox();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        gatewayProjectPackageLabel = new javax.swing.JLabel();
        gatewayProjectPackageCombo = new javax.swing.JComboBox();
        entityLayeredPane = new javax.swing.JLayeredPane();
        packageLabel = new javax.swing.JLabel();
        packageWrapper = new javax.swing.JLayeredPane();
        packagePrefixLabel = new javax.swing.JLabel();
        entitySetting = new javax.swing.JButton();
        entityPackageTextField = new javax.swing.JTextField();
        businessLayeredPane = new javax.swing.JLayeredPane();
        businessLayerCombo = new javax.swing.JComboBox();
        businessLayerLabel = new javax.swing.JLabel();
        controllerLayeredPane = new javax.swing.JLayeredPane();
        controllerLayerCombo = new javax.swing.JComboBox();
        controllerLayerLabel = new javax.swing.JLabel();
        viewerLayeredPane = new javax.swing.JLayeredPane();
        viewerLayerCombo = new javax.swing.JComboBox();
        viewerLayerLabel = new javax.swing.JLabel();
        configPane = new javax.swing.JTabbedPane();
        actionPane = new javax.swing.JLayeredPane();
        actionLayeredPane = new javax.swing.JLayeredPane();
        generateSourceCode = new javax.swing.JButton();
        cencelGenerateCode = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        targetCompleteAppCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        wrapperLayeredPane.setLayout(new java.awt.GridLayout(7, 0, 0, 4));

        archbuttonGroup.add(monolithRadioButton);
        monolithRadioButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(monolithRadioButton, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.monolithRadioButton.text")); // NOI18N
        monolithRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monolithRadioButtonActionPerformed(evt);
            }
        });

        archbuttonGroup.add(microservicesRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(microservicesRadioButton, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.microservicesRadioButton.text")); // NOI18N
        microservicesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                microservicesRadioButtonActionPerformed(evt);
            }
        });

        archbuttonGroup.add(gatewayRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(gatewayRadioButton, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.gatewayRadioButton.text")); // NOI18N
        gatewayRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gatewayRadioButtonActionPerformed(evt);
            }
        });

        archLayeredPane.setLayer(monolithRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        archLayeredPane.setLayer(microservicesRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        archLayeredPane.setLayer(gatewayRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout archLayeredPaneLayout = new javax.swing.GroupLayout(archLayeredPane);
        archLayeredPane.setLayout(archLayeredPaneLayout);
        archLayeredPaneLayout.setHorizontalGroup(
            archLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(archLayeredPaneLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(monolithRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(microservicesRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(gatewayRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        archLayeredPaneLayout.setVerticalGroup(
            archLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(monolithRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(microservicesRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(gatewayRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        wrapperLayeredPane.add(archLayeredPane);

        targetProjectLayeredPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 3, 1));
        targetProjectLayeredPane.setLayout(new java.awt.BorderLayout(10, 0));

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(280, 42));
        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(targetProjectLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.targetProjectLabel.text")); // NOI18N
        targetProjectLabel.setPreferredSize(new java.awt.Dimension(95, 17));
        jLayeredPane1.add(targetProjectLabel, java.awt.BorderLayout.WEST);

        targetProjectCombo.setMinimumSize(new java.awt.Dimension(50, 20));
        targetProjectCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                targetProjectComboItemStateChanged(evt);
            }
        });
        jLayeredPane1.add(targetProjectCombo, java.awt.BorderLayout.CENTER);

        targetProjectLayeredPane.add(jLayeredPane1, java.awt.BorderLayout.WEST);

        jLayeredPane2.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(targetProjectPackageLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.targetProjectPackageLabel.text")); // NOI18N
        targetProjectPackageLabel.setPreferredSize(new java.awt.Dimension(50, 17));
        jLayeredPane2.add(targetProjectPackageLabel, java.awt.BorderLayout.WEST);

        targetProjectPackageCombo.setEditable(true);
        targetProjectPackageCombo.setMinimumSize(new java.awt.Dimension(50, 20));
        jLayeredPane2.add(targetProjectPackageCombo, java.awt.BorderLayout.CENTER);

        targetProjectLayeredPane.add(jLayeredPane2, java.awt.BorderLayout.CENTER);

        wrapperLayeredPane.add(targetProjectLayeredPane);

        gatewayProjectLayeredPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 3, 1));
        gatewayProjectLayeredPane.setLayout(new java.awt.BorderLayout(10, 0));

        jLayeredPane3.setPreferredSize(new java.awt.Dimension(280, 42));
        jLayeredPane3.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(gatewayProjectLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.gatewayProjectLabel.text")); // NOI18N
        gatewayProjectLabel.setPreferredSize(new java.awt.Dimension(95, 17));
        jLayeredPane3.add(gatewayProjectLabel, java.awt.BorderLayout.WEST);

        gatewayProjectCombo.setMinimumSize(new java.awt.Dimension(50, 20));
        gatewayProjectCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gatewayProjectComboItemStateChanged(evt);
            }
        });
        jLayeredPane3.add(gatewayProjectCombo, java.awt.BorderLayout.CENTER);

        gatewayProjectLayeredPane.add(jLayeredPane3, java.awt.BorderLayout.WEST);

        jLayeredPane4.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(gatewayProjectPackageLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.gatewayProjectPackageLabel.text")); // NOI18N
        gatewayProjectPackageLabel.setPreferredSize(new java.awt.Dimension(50, 17));
        jLayeredPane4.add(gatewayProjectPackageLabel, java.awt.BorderLayout.WEST);

        gatewayProjectPackageCombo.setEditable(true);
        gatewayProjectPackageCombo.setMinimumSize(new java.awt.Dimension(50, 20));
        jLayeredPane4.add(gatewayProjectPackageCombo, java.awt.BorderLayout.CENTER);

        gatewayProjectLayeredPane.add(jLayeredPane4, java.awt.BorderLayout.CENTER);

        wrapperLayeredPane.add(gatewayProjectLayeredPane);

        entityLayeredPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 3, 1));
        entityLayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(95, 17));
        entityLayeredPane.add(packageLabel, java.awt.BorderLayout.WEST);

        packageWrapper.setLayout(new java.awt.BorderLayout());

        packagePrefixLabel.setForeground(new java.awt.Color(153, 153, 153));
        org.openide.awt.Mnemonics.setLocalizedText(packagePrefixLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.packagePrefixLabel.text")); // NOI18N
        packagePrefixLabel.setPreferredSize(new java.awt.Dimension(130, 14));
        packageWrapper.add(packagePrefixLabel, java.awt.BorderLayout.WEST);

        entitySetting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/resource/image/java/JAVA_CLASS.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entitySetting, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.entitySetting.text")); // NOI18N
        entitySetting.setBorder(null);
        entitySetting.setBorderPainted(false);
        entitySetting.setPreferredSize(new java.awt.Dimension(20, 17));
        entitySetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entitySettingActionPerformed(evt);
            }
        });
        packageWrapper.add(entitySetting, java.awt.BorderLayout.EAST);

        entityPackageTextField.setText(org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.entityPackageTextField.text")); // NOI18N
        packageWrapper.add(entityPackageTextField, java.awt.BorderLayout.CENTER);

        entityLayeredPane.add(packageWrapper, java.awt.BorderLayout.CENTER);

        wrapperLayeredPane.add(entityLayeredPane);

        businessLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                businessLayerComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(businessLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.businessLayerLabel.text")); // NOI18N

        businessLayeredPane.setLayer(businessLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        businessLayeredPane.setLayer(businessLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout businessLayeredPaneLayout = new javax.swing.GroupLayout(businessLayeredPane);
        businessLayeredPane.setLayout(businessLayeredPaneLayout);
        businessLayeredPaneLayout.setHorizontalGroup(
            businessLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessLayeredPaneLayout.createSequentialGroup()
                .addComponent(businessLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(businessLayerCombo, 0, 519, Short.MAX_VALUE))
        );
        businessLayeredPaneLayout.setVerticalGroup(
            businessLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessLayeredPaneLayout.createSequentialGroup()
                .addGroup(businessLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(businessLayerLabel)
                    .addComponent(businessLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        wrapperLayeredPane.add(businessLayeredPane);

        controllerLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                controllerLayerComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(controllerLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.controllerLayerLabel.text")); // NOI18N

        controllerLayeredPane.setLayer(controllerLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        controllerLayeredPane.setLayer(controllerLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout controllerLayeredPaneLayout = new javax.swing.GroupLayout(controllerLayeredPane);
        controllerLayeredPane.setLayout(controllerLayeredPaneLayout);
        controllerLayeredPaneLayout.setHorizontalGroup(
            controllerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controllerLayeredPaneLayout.createSequentialGroup()
                .addComponent(controllerLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controllerLayerCombo, 0, 519, Short.MAX_VALUE))
        );
        controllerLayeredPaneLayout.setVerticalGroup(
            controllerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controllerLayeredPaneLayout.createSequentialGroup()
                .addGroup(controllerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(controllerLayerLabel)
                    .addComponent(controllerLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wrapperLayeredPane.add(controllerLayeredPane);

        viewerLayerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewerLayerComboItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(viewerLayerLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.viewerLayerLabel.text")); // NOI18N

        viewerLayeredPane.setLayer(viewerLayerCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        viewerLayeredPane.setLayer(viewerLayerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout viewerLayeredPaneLayout = new javax.swing.GroupLayout(viewerLayeredPane);
        viewerLayeredPane.setLayout(viewerLayeredPaneLayout);
        viewerLayeredPaneLayout.setHorizontalGroup(
            viewerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewerLayeredPaneLayout.createSequentialGroup()
                .addComponent(viewerLayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewerLayerCombo, 0, 519, Short.MAX_VALUE))
        );
        viewerLayeredPaneLayout.setVerticalGroup(
            viewerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewerLayeredPaneLayout.createSequentialGroup()
                .addGroup(viewerLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewerLayerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewerLayerLabel))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        wrapperLayeredPane.add(viewerLayeredPane);

        optionPane.setLayer(wrapperLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        optionPane.setLayer(configPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout optionPaneLayout = new javax.swing.GroupLayout(optionPane);
        optionPane.setLayout(optionPaneLayout);
        optionPaneLayout.setHorizontalGroup(
            optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configPane)
                    .addComponent(wrapperLayeredPane))
                .addContainerGap())
        );
        optionPaneLayout.setVerticalGroup(
            optionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPaneLayout.createSequentialGroup()
                .addComponent(wrapperLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(configPane, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

        actionLayeredPane.setLayer(generateSourceCode, javax.swing.JLayeredPane.DEFAULT_LAYER);
        actionLayeredPane.setLayer(cencelGenerateCode, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout actionLayeredPaneLayout = new javax.swing.GroupLayout(actionLayeredPane);
        actionLayeredPane.setLayout(actionLayeredPaneLayout);
        actionLayeredPaneLayout.setHorizontalGroup(
            actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionLayeredPaneLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(generateSourceCode, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cencelGenerateCode)
                .addGap(14, 14, 14))
        );
        actionLayeredPaneLayout.setVerticalGroup(
            actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionLayeredPaneLayout.createSequentialGroup()
                .addGroup(actionLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateSourceCode)
                    .addComponent(cencelGenerateCode))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.infoLabel.text")); // NOI18N

        setCompleteApplication(technologyPref.getBoolean(COMPLETE_APPLICATION, true));
        targetCompleteAppCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(targetCompleteAppCheckBox, org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.targetCompleteAppCheckBox.text")); // NOI18N
        targetCompleteAppCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.targetCompleteAppCheckBox.toolTipText")); // NOI18N
        targetCompleteAppCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetCompleteAppCheckBoxActionPerformed(evt);
            }
        });

        actionPane.setLayer(actionLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        actionPane.setLayer(infoLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        actionPane.setLayer(targetCompleteAppCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout actionPaneLayout = new javax.swing.GroupLayout(actionPane);
        actionPane.setLayout(actionPaneLayout);
        actionPaneLayout.setHorizontalGroup(
            actionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(targetCompleteAppCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actionLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        actionPaneLayout.setVerticalGroup(
            actionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPaneLayout.createSequentialGroup()
                .addComponent(actionLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(actionPaneLayout.createSequentialGroup()
                .addComponent(targetCompleteAppCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actionPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(optionPane)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(actionPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void targetProjectComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_targetProjectComboItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            Project project = (Project)targetProjectCombo.getSelectedItem();
            targetProjectInfo.setProject(project);
            populateSourceFolderCombo(targetProjectInfo);
            populatePackageCombo(targetProjectPackageCombo, targetProjectInfo);
            
//            Class businessLayerConfigClass = getBusinessLayer().getPanel().getConfigDataClass();
//            Preferences businessLayerPref = ProjectUtils.getPreferences(project, businessLayerConfigClass,true);
//            if (businessLayerPref.getByteArray(businessLayerConfigClass.getName(), null) != null) {
                refreshLayer();
//            }
        }
    }//GEN-LAST:event_targetProjectComboItemStateChanged

    private void generateSourceCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSourceCodeActionPerformed
        if (!hasError()) {
            setVisible(false);
            this.setDialogResult(javax.swing.JOptionPane.OK_OPTION);
            dispose();
            store();
        }
    }//GEN-LAST:event_generateSourceCodeActionPerformed

    private boolean hasError() {
        if (!Technology.NONE_LABEL.equals(getBusinessLayer().getTechnology().label())) {
            if ((isMonolith() && !isSupportedProject(targetProjectInfo))
                    || (isMicroservice() && (!isSupportedProject(gatewayProjectInfo) || !isSupportedProject(targetProjectInfo)))
                    || (isGateway() && !isSupportedProject(gatewayProjectInfo))) {
                NotifyDescriptor d = new NotifyDescriptor.Message(
                        "Please select the [Maven > Web Application] project for full-stack app",
                        NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle("Invalid project type");
                DialogDisplayer.getDefault().notify(d);
                return true;
            }
        }
        if (isMicroservice() && targetProjectInfo.getProject() == gatewayProjectInfo.getProject()) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                    "Target and Gateway project can't be same",
                    NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Same destination");
            DialogDisplayer.getDefault().notify(d);
            return true;
        } 
        
        if (isGateway() && !SourceVersion.isName(getGatewayPackage())) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                    "Please select the Gateway Project Package.",
                    NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Gateway Project Package");
            DialogDisplayer.getDefault().notify(d);
            gatewayProjectPackageCombo.requestFocus();
            return true;
        } 
        
        if ((isMonolith() || isMicroservice()) && !SourceVersion.isName(getTargetPackage())) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                    "Please select the Project Package.",
                    NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Project Package");
            DialogDisplayer.getDefault().notify(d);
            targetProjectPackageCombo.requestFocus();
            return true;
        }
        
        if (!SourceVersion.isName(getEntityPackage())) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                    "Please select the Entity Package.",
                    NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle("Entity Package");
            DialogDisplayer.getDefault().notify(d);
            return true;
        }
        for (Component component : configPane.getComponents()) {
            if (component instanceof LayerConfigPanel) {
                LayerConfigPanel panel = (LayerConfigPanel) component;
                if (panel.hasError()) {
                    configPane.setSelectedComponent(component);
                    return true;
                } else {
                    panel.store();
                    PreferenceUtils.set(targetProjectInfo.getProject(), panel.getConfigData());
                }
            }
        }
        return false;
    }

    private void store() {
        modelerFile.getModelerPanelTopComponent().changePersistenceState(false);
        entityMappings.setProjectType(isMonolith() ? MONOLITH : MICROSERVICE);
        entityMappings.setProjectPackage(getTargetPackage());
        entityMappings.setEntityPackage(getEntityPackage());
        if ((isMonolith() && isSupportedProject(targetProjectInfo))
                || (isMicroservice() && isSupportedProject(gatewayProjectInfo) && isSupportedProject(targetProjectInfo))
                || (isGateway() && isSupportedProject(gatewayProjectInfo))) {
            technologyPref.put(BUSINESS.name(), getBusinessLayer().getGeneratorClass().getSimpleName());
            technologyPref.put(CONTROLLER.name(), getControllerLayer().getGeneratorClass().getSimpleName());
            technologyPref.put(VIEWER.name(), getViewerLayer().getGeneratorClass().getSimpleName());
        }
    }

    private boolean isMonolith() {
        return monolithRadioButton.isSelected();
    }

    private void setMicroservice(boolean enabled) {
        microservicesRadioButton.setSelected(enabled);
    }

    private boolean isMicroservice() {
        return microservicesRadioButton.isSelected();
    }

    private void setGateway(boolean enabled) {
        gatewayRadioButton.setSelected(enabled);
    }

    private boolean isGateway() {
        return gatewayRadioButton.isSelected();
    }

    private void cencelGenerateCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cencelGenerateCodeActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cencelGenerateCodeActionPerformed

    private void businessLayerComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_businessLayerComboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            setCompleteApplicationCompVisibility(getBusinessLayer().getTechnology().panel() != LayerConfigPanel.class);
            changeBusinessLayer(getBusinessLayer());
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

    private void entitySettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entitySettingActionPerformed
        entityGenerationSettingDialog.setVisible(true);
        manageGenerateButtonStatus();
    }//GEN-LAST:event_entitySettingActionPerformed

    private void targetCompleteAppCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetCompleteAppCheckBoxActionPerformed
        technologyPref.putBoolean(COMPLETE_APPLICATION, isCompleteApplication());
        manageGenerateButtonStatus();
        refreshLayer();
    }//GEN-LAST:event_targetCompleteAppCheckBoxActionPerformed

    private void microservicesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_microservicesRadioButtonActionPerformed
        setArchState();
    }//GEN-LAST:event_microservicesRadioButtonActionPerformed

    private void monolithRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monolithRadioButtonActionPerformed
        setArchState();
    }//GEN-LAST:event_monolithRadioButtonActionPerformed

    private void gatewayProjectComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gatewayProjectComboItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            gatewayProjectInfo.setProject((Project) gatewayProjectCombo.getSelectedItem());
            populateSourceFolderCombo(gatewayProjectInfo);
            populatePackageCombo(gatewayProjectPackageCombo, gatewayProjectInfo);
        }
    }//GEN-LAST:event_gatewayProjectComboItemStateChanged

    private void gatewayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gatewayRadioButtonActionPerformed
        setArchState();
    }//GEN-LAST:event_gatewayRadioButtonActionPerformed

    private void setArchState() {
        wrapperLayeredPane.removeAll();
        wrapperLayeredPane.revalidate();
        wrapperLayeredPane.repaint();
        entitySetting.setVisible(true);
        targetCompleteAppCheckBox.setEnabled(true);

        wrapperLayeredPane.add(archLayeredPane);
        if (isMicroservice()) {
            ((GridLayout) wrapperLayeredPane.getLayout()).setRows(7);
            ((GridLayout) wrapperLayeredPane.getLayout()).setVgap(4);
            wrapperLayeredPane.add(targetProjectLayeredPane);
            wrapperLayeredPane.add(gatewayProjectLayeredPane);
        } else if (isGateway()) {
            entitySetting.setVisible(false);
            targetCompleteAppCheckBox.setEnabled(false);
            ((GridLayout) wrapperLayeredPane.getLayout()).setRows(6);
            ((GridLayout) wrapperLayeredPane.getLayout()).setVgap(10);
            wrapperLayeredPane.add(gatewayProjectLayeredPane);
        } else {
            ((GridLayout) wrapperLayeredPane.getLayout()).setRows(6);
            ((GridLayout) wrapperLayeredPane.getLayout()).setVgap(10);
            wrapperLayeredPane.add(targetProjectLayeredPane);
        }
        wrapperLayeredPane.add(entityLayeredPane);
        wrapperLayeredPane.add(businessLayeredPane);
        wrapperLayeredPane.add(controllerLayeredPane);
        wrapperLayeredPane.add(viewerLayeredPane);
        initLayer();
    }

    private void manageGenerateButtonStatus() {
        List<JavaClass> javaClassList = entityMappings.findGeneratedClass();
        Optional<WorkSpace> optionalWorkSpace = entityMappings.findGeneratedWorkSpace();
        generateSourceCode.setEnabled(true);
        if (javaClassList.isEmpty()) {
            if (isCompleteApplication()) {
                generateSourceCode.setIcon(SUCCESS_ICON);
                setLocalizedText(generateSourceCode, getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.baseApp.text")); // NOI18N
            } else {
                generateSourceCode.setIcon(ERROR_ICON);
                setLocalizedText(generateSourceCode, getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.text")); // NOI18N
                generateSourceCode.setEnabled(false);
            }
        } else if (optionalWorkSpace.isPresent() && optionalWorkSpace.get() != entityMappings.getRootWorkSpace()) {
            generateSourceCode.setIcon(WORKSPACE_ICON);
            setLocalizedText(generateSourceCode, getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.workspace.text", optionalWorkSpace.get().getName())); // NOI18N
        } else if (javaClassList.size() < entityMappings.getAllJavaClass().size()) {
            generateSourceCode.setIcon(WARNING_ICON);
            setLocalizedText(generateSourceCode, getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.warning.text")); // NOI18N
        } else {
            generateSourceCode.setIcon(SUCCESS_ICON);
            setLocalizedText(generateSourceCode, getMessage(GenerateCodeDialog.class, "GenerateCodeDialog.generateSourceCode.text")); // NOI18N
        }
    }

    private void enableExistingProjectElementGroup(boolean enable) {
        targetProjectCombo.setEnabled(enable);
    }

    private void populateProjectCombo(JComboBox projectCombo, ProjectInfo projectInfo) {
        ProjectCellRenderer projectCellRenderer = new ProjectCellRenderer(projectCombo.getRenderer());
        projectCombo.setRenderer(projectCellRenderer);
        List<Project> list = getJavaProjects();

        if (list == null || list.isEmpty()) {
            enableExistingProjectElementGroup(false);
        } else {
            DefaultComboBoxModel projectsModel = new DefaultComboBoxModel(list.toArray());
            projectCombo.setModel(projectsModel);

            // Issue Fix #5850 
            Project modelerProject = FileOwnerQuery.getOwner(modelerFileObject);
            if (modelerProject != null) {
                projectCombo.setSelectedItem(modelerProject);
            } else {
                projectCombo.setSelectedIndex(-1);
            }
            selectProject(projectCombo, projectInfo);
        }
    }

    private void selectProject(JComboBox projectCombo, ProjectInfo projectInfo) {
        if (projectInfo.getProject() == null) {
            if (projectCombo.getSelectedItem() != null) {
                projectInfo.setProject((Project) projectCombo.getSelectedItem());
            }
        } else {
            projectCombo.setSelectedItem(projectInfo.getProject());
        }
        if (projectCombo.getSelectedItem() != null) {
            populateSourceFolderCombo(projectInfo);
        }
    }

    private void populateSourceFolderCombo(ProjectInfo projectInfo) {
        ArrayList<SourceGroup> srcRoots = new ArrayList<>();
        int index = 0;
        FileObject sfo = projectInfo.getSourceGroup() != null ? projectInfo.getSourceGroup().getRootFolder() : null;
        if (projectInfo.getProject() != null) {
            Sources sources = ProjectUtils.getSources(projectInfo.getProject());
            if (sources != null) {
                SourceGroup[] srcGrps = sources.getSourceGroups(SOURCES_TYPE_JAVA);
                if (srcGrps != null) {
                    for (SourceGroup srcGrp : srcGrps) {
                        if (srcGrp != null) {
                            srcRoots.add(srcGrp);
                            if (srcGrp.getRootFolder() != null && srcGrp.getRootFolder().equals(sfo)) {
                                index = srcRoots.size() - 1;
                            }
                        }
                    }
                }
            }
        }

        if (srcRoots.size() > 0) {
            projectInfo.setSourceGroup(srcRoots.get(index));
        }
    }

    private void populatePackageCombo(JComboBox packageCombo, ProjectInfo projectInfo) {
        if (projectInfo.getSourceGroup() != null) {
            packageCombo.setRenderer(PackageView.listRenderer());
            ComboBoxModel model = PackageView.createListView(projectInfo.getSourceGroup());
            if (model.getSize() > 0) {
                model.setSelectedItem(model.getElementAt(0));
            }
            packageCombo.setModel(model);
        }
    }

    /**
     * @return the modelerFileObject
     */
    public FileObject getModelerFileObject() {
        return modelerFileObject;
    }

    public String getEntityPackage() {
        return entityPackageTextField.getText();
    }

    private void setEntityPackage(String _package) {
        entityPackageTextField.setText(_package);
    }

    public String getTargetPackage() {
        return getPackage(targetProjectPackageCombo);
    }

    private void setTargetPackage(String _package) {
        setPackage(targetProjectPackageCombo, _package);
    }

    public String getGatewayPackage() {
        return getPackage(gatewayProjectPackageCombo);
    }

    private void setGatewayPackage(String _package) {
        setPackage(gatewayProjectPackageCombo, _package);
    }

    public String getPackage(JComboBox packageCombo) {
        return ((JTextComponent) packageCombo.getEditor().getEditorComponent()).getText();
    }

    private void setPackage(JComboBox packageCombo, String _package) {
        ComboBoxModel model = packageCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(_package)) {
                model.setSelectedItem(model.getElementAt(i));
                return;
            }
        }
        ((JTextComponent) packageCombo.getEditor().getEditorComponent()).setText(_package);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane actionLayeredPane;
    private javax.swing.JLayeredPane actionPane;
    private javax.swing.JLayeredPane archLayeredPane;
    private javax.swing.ButtonGroup archbuttonGroup;
    private javax.swing.JComboBox businessLayerCombo;
    private javax.swing.JLabel businessLayerLabel;
    private javax.swing.JLayeredPane businessLayeredPane;
    private javax.swing.JButton cencelGenerateCode;
    private javax.swing.JTabbedPane configPane;
    private javax.swing.JComboBox controllerLayerCombo;
    private javax.swing.JLabel controllerLayerLabel;
    private javax.swing.JLayeredPane controllerLayeredPane;
    private javax.swing.JLayeredPane entityLayeredPane;
    private javax.swing.JTextField entityPackageTextField;
    private javax.swing.JButton entitySetting;
    private javax.swing.JComboBox gatewayProjectCombo;
    private javax.swing.JLabel gatewayProjectLabel;
    private javax.swing.JLayeredPane gatewayProjectLayeredPane;
    private javax.swing.JComboBox gatewayProjectPackageCombo;
    private javax.swing.JLabel gatewayProjectPackageLabel;
    private javax.swing.JRadioButton gatewayRadioButton;
    private javax.swing.JButton generateSourceCode;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JRadioButton microservicesRadioButton;
    private javax.swing.JRadioButton monolithRadioButton;
    private javax.swing.JLayeredPane optionPane;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel packagePrefixLabel;
    private javax.swing.JLayeredPane packageWrapper;
    private javax.swing.JCheckBox targetCompleteAppCheckBox;
    private javax.swing.JComboBox targetProjectCombo;
    private javax.swing.JLabel targetProjectLabel;
    private javax.swing.JLayeredPane targetProjectLayeredPane;
    private javax.swing.JComboBox targetProjectPackageCombo;
    private javax.swing.JLabel targetProjectPackageLabel;
    private javax.swing.JComboBox viewerLayerCombo;
    private javax.swing.JLabel viewerLayerLabel;
    private javax.swing.JLayeredPane viewerLayeredPane;
    private javax.swing.JLayeredPane wrapperLayeredPane;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the configData
     */
    public ApplicationConfigData getConfigData() {
        configData.setCompleteApplication(isCompleteApplication());
        configData.setProjectType(isMonolith() ? MONOLITH : (isMicroservice() ? MICROSERVICE : GATEWAY));
        configData.setRegistryType(CONSUL);
        
        if (isMonolith()) {
            configData.setTargetProject(targetProjectInfo.getProject());
            configData.setTargetSourceGroup(targetProjectInfo.getSourceGroup());
            configData.setTargetPackage(getTargetPackage());
            configData.setGatewayProject(targetProjectInfo.getProject());
            configData.setGatewaySourceGroup(targetProjectInfo.getSourceGroup());
            configData.setGatewayPackage(getTargetPackage());
        } else if (isMicroservice()) {
            configData.setTargetProject(targetProjectInfo.getProject());
            configData.setTargetSourceGroup(targetProjectInfo.getSourceGroup());
            configData.setTargetPackage(getTargetPackage());
            configData.setGatewayProject(gatewayProjectInfo.getProject());
            configData.setGatewaySourceGroup(gatewayProjectInfo.getSourceGroup());
            configData.setGatewayPackage(getGatewayPackage());
        } else if (isGateway()) {
            configData.setTargetProject(gatewayProjectInfo.getProject());
            configData.setTargetSourceGroup(gatewayProjectInfo.getSourceGroup());
            configData.setTargetPackage(getGatewayPackage());
            configData.setGatewayProject(gatewayProjectInfo.getProject());
            configData.setGatewaySourceGroup(gatewayProjectInfo.getSourceGroup());
            configData.setGatewayPackage(getGatewayPackage());
        }

        if (POMManager.isMavenProject(configData.getTargetProject())) {
            configData.setTargetArtifactId(new POMManager(configData.getTargetProject(), true).getArtifactId());
        }
        if (POMManager.isMavenProject(configData.getGatewayProject())) {
            configData.setGatewayArtifactId(new POMManager(configData.getGatewayProject(), true).getArtifactId());
        }

        return configData;
    }

    private boolean isCompleteApplication() {
        return targetCompleteAppCheckBox.isSelected() && targetCompleteAppCheckBox.isVisible();
    }

    private void setCompleteApplication(boolean select) {
        targetCompleteAppCheckBox.setSelected(select);
    }

    private void setCompleteApplicationCompVisibility(boolean visible) {
        targetCompleteAppCheckBox.setVisible(visible);
        manageGenerateButtonStatus();
    }
}

class ProjectInfo {

    private Project project;
    private SourceGroup sourceGroup;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public SourceGroup getSourceGroup() {
        return sourceGroup;
    }

    public void setSourceGroup(SourceGroup sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

}
