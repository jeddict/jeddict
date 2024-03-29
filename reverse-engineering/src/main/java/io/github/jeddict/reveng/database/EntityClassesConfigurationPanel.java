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
package io.github.jeddict.reveng.database;

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderSourceGroup;
import static io.github.jeddict.jcode.util.ProjectHelper.getJavaSourceGroups;
import static io.github.jeddict.jcode.util.ProjectHelper.getPackageForFolder;
import static io.github.jeddict.jcode.util.ProjectHelper.isFolderWritable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.persistence.provider.Provider;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.SourceGroupUISupport;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.TableClosure;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.TableSource;
import org.netbeans.modules.j2ee.persistence.wizard.library.PersistenceLibrarySupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class EntityClassesConfigurationPanel extends javax.swing.JPanel {

    private final static Logger LOGGER = Logger.getLogger(EntityClassesConfigurationPanel.class.getName());

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private JTextComponent packageComboBoxEditor;

    private Project project;

    private SelectedTables selectedTables;

    private EntityClassesConfigurationPanel() {
        initComponents();

        classNamesTable.getParent().setBackground(classNamesTable.getBackground());
        classNamesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // NOI18N

        packageComboBoxEditor = ((JTextComponent) packageComboBox.getEditor().getEditorComponent());
        Document packageComboBoxDocument = packageComboBoxEditor.getDocument();
        packageComboBoxDocument.addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                packageChanged();
            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    boolean isBeanValidationSupported() {
        if (project == null) {
            return false;
        }

        final String notNullAnnotation = "jakarta.validation.constraints.NotNull";    //NOI18N
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup groups[] = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (groups == null || groups.length < 1) {
            return false;
        }
        SourceGroup firstGroup = groups[0];
        FileObject fo = firstGroup.getRootFolder();
        ClassPath compile = ClassPath.getClassPath(fo, ClassPath.COMPILE);
        if (compile == null) {
            return false;
        }
        return compile.findResource(notNullAnnotation.replace('.', '/') + ".class") != null;//NOI18N
    }

    public void initialize(Project project, FileObject targetFolder) {
        this.project = project;

        projectTextField.setText(ProjectUtils.getInformation(project).getDisplayName());

        SourceGroup[] sourceGroups = getJavaSourceGroups(project);
        SourceGroupUISupport.connect(locationComboBox, sourceGroups);

        packageComboBox.setRenderer(PackageView.listRenderer());

        updatePackageComboBox();

        if (targetFolder != null) {
            // set default source group and package cf. targetFolder
            SourceGroup targetSourceGroup = getFolderSourceGroup(sourceGroups, targetFolder);
            if (targetSourceGroup != null) {
                locationComboBox.setSelectedItem(targetSourceGroup);
                String targetPackage = getPackageForFolder(targetSourceGroup, targetFolder);
                if (targetPackage != null) {
                    packageComboBoxEditor.setText(targetPackage);
                }
            }
        }
    }

    public void update(TableClosure tableClosure, String tableSourceName) {
        try {
            if (selectedTables == null) {
                selectedTables = new SelectedTables(tableClosure, getLocationValue(), getPackageName());
                selectedTables.addChangeListener(e -> changeSupport.fireChange());
            } else {
                selectedTables.setTableClosureAndTargetFolder(tableClosure, getLocationValue(), getPackageName());
            }
        } catch (IOException e) {
            ExceptionUtils.printStackTrace(e);
        }

        TableUISupport.connectClassNames(classNamesTable, selectedTables);
    }

    public SelectedTables getSelectedTables() {
        return selectedTables;
    }

    public SourceGroup getLocationValue() {
        return (SourceGroup) locationComboBox.getSelectedItem();
    }

    public String getPackageName() {
        return packageComboBoxEditor.getText();
    }

    public String getFileName() {
        return fileNameTextField.getText();
    }

    public boolean getGenerateValidationConstraints() {
        return isBeanValidationSupported();
    }

    private void locationChanged() {
        updatePackageComboBox();
        updateSelectedTables();
        changeSupport.fireChange();
    }

    private void packageChanged() {
        updateSelectedTables();
        changeSupport.fireChange();
    }

    private void fileNameChanged() {
        changeSupport.fireChange();
    }

    private void updatePackageComboBox() {
        SourceGroup sourceGroup = (SourceGroup) locationComboBox.getSelectedItem();
        if (sourceGroup != null) {
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSelectedItem() != null && model.getSelectedItem().toString().startsWith("META-INF")
                    && model.getSize() > 1) { // NOI18N
                model.setSelectedItem(model.getElementAt(1));
            }
            packageComboBox.setModel(model);
        }
    }

    private void updateSelectedTables() {
        if (selectedTables != null) {
            try {
                selectedTables.setTargetFolder(getLocationValue(), getPackageName());
            } catch (IOException e) {
                ExceptionUtils.printStackTrace(e);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableActionsPopup = new javax.swing.JPopupMenu();
        specifyNamesLabel = new javax.swing.JLabel();
        classNamesLabel = new javax.swing.JLabel();
        classNamesScrollPane = new javax.swing.JScrollPane();
        classNamesTable = new TableUISupport.ClassNamesTable();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        spacerPanel = new javax.swing.JPanel();
        tableActionsButton = new javax.swing.JButton();
        warningLabel = new ShyLabel();
        fileNameTextField = new javax.swing.JTextField();
        fileNameLabel = new javax.swing.JLabel();

        tableActionsPopup.setInvoker(tableActionsButton);

        setName(org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_EntityClasses")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(specifyNamesLabel, org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_SpecifyEntityClassNames")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(classNamesLabel, org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_ClassNames")); // NOI18N

        classNamesScrollPane.setMinimumSize(new java.awt.Dimension(23, 80));
        classNamesScrollPane.setViewportView(classNamesTable);

        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_Project")); // NOI18N

        projectTextField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_SrcLocation")); // NOI18N

        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(EntityClassesConfigurationPanel.class, "LBL_Package")); // NOI18N

        packageComboBox.setEditable(true);

        spacerPanel.setPreferredSize(new java.awt.Dimension(377, 24));

        org.openide.awt.Mnemonics.setLocalizedText(tableActionsButton, "...");
        tableActionsButton.setMaximumSize(new java.awt.Dimension(24, 24));
        tableActionsButton.setMinimumSize(new java.awt.Dimension(24, 24));
        tableActionsButton.setPreferredSize(new java.awt.Dimension(24, 24));
        tableActionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableActionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout spacerPanelLayout = new javax.swing.GroupLayout(spacerPanel);
        spacerPanel.setLayout(spacerPanelLayout);
        spacerPanelLayout.setHorizontalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spacerPanelLayout.createSequentialGroup()
                .addContainerGap(404, Short.MAX_VALUE)
                .addComponent(tableActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        spacerPanelLayout.setVerticalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spacerPanelLayout.createSequentialGroup()
                .addComponent(tableActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, "  ");
        warningLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        warningLabel.setMaximumSize(new java.awt.Dimension(1000, 29));

        fileNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fileNameTextFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(fileNameLabel, "File Name :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(warningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(specifyNamesLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(classNamesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectLabel)
                    .addComponent(locationLabel)
                    .addComponent(packageLabel)
                    .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spacerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addComponent(packageComboBox, 0, 428, Short.MAX_VALUE)
                    .addComponent(locationComboBox, 0, 428, Short.MAX_VALUE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(classNamesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(fileNameTextField)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(specifyNamesLabel)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(classNamesLabel)
                    .addComponent(classNamesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spacerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameLabel))
                .addGap(83, 83, 83)
                .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        locationChanged();
    }//GEN-LAST:event_locationComboBoxActionPerformed

    private void tableActionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableActionsButtonActionPerformed
        Component component = ((Component) evt.getSource());
        Point loc = component.getLocationOnScreen();
        loc.y += component.getHeight() / 2;
        loc.x += component.getWidth() / 2;
        tableActionsPopup.setLocation(loc);
        tableActionsPopup.setVisible(true);
    }//GEN-LAST:event_tableActionsButtonActionPerformed

    private void fileNameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fileNameTextFieldKeyPressed
        fileNameChanged();
    }//GEN-LAST:event_fileNameTextFieldKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classNamesLabel;
    private javax.swing.JScrollPane classNamesScrollPane;
    private javax.swing.JTable classNamesTable;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JPanel spacerPanel;
    private javax.swing.JLabel specifyNamesLabel;
    private javax.swing.JButton tableActionsButton;
    private javax.swing.JPopupMenu tableActionsPopup;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables

    public static final class WizardPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel, ChangeListener {

        private final ChangeSupport changeSupport = new ChangeSupport(this);

        private EntityClassesConfigurationPanel component;
        private boolean componentInitialized;

        private WizardDescriptor wizardDescriptor;
        private Project project;
        private boolean isFinishable;

        private List<Provider> providers;

        public WizardPanel() {
            this(false);
        }

        public WizardPanel(boolean isFinishable) {
            this.isFinishable = isFinishable;
        }

        @Override
        public EntityClassesConfigurationPanel getComponent() {
            if (component == null) {
                component = new EntityClassesConfigurationPanel();
                component.addChangeListener(this);
            }
            return component;
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public HelpCtx getHelp() {
            return new HelpCtx(EntityClassesConfigurationPanel.class);
        }

        @Override
        public void readSettings(Object settings) {
            wizardDescriptor = (WizardDescriptor) settings;

            ImportHelper helper = DBImportWizardDescriptor.getHelper(wizardDescriptor);

            if (!componentInitialized) {
                componentInitialized = true;

                project = Templates.getProject(wizardDescriptor);
                FileObject targetFolder = Templates.getTargetFolder(wizardDescriptor);

                getComponent().initialize(project, targetFolder);
            }

            TableSource tableSource = helper.getTableSource();
            String tableSourceName = null;
            if (tableSource != null) {
                // the name of the table source is only relevant if the source
                // was a data source of connection, since it will be sent to the
                // persistence unit panel, which only deals with data sources
                // or connections
                TableSource.Type tableSourceType = tableSource.getType();
                if (tableSourceType == TableSource.Type.DATA_SOURCE || tableSourceType == TableSource.Type.CONNECTION) {
                    tableSourceName = tableSource.getName();
                }
            }

            getComponent().update(helper.getTableClosure(), tableSourceName);
        }

        @Override
        public boolean isValid() {
            SourceGroup sourceGroup = getComponent().getLocationValue();
            if (sourceGroup == null) {
                setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_JavaTargetChooser_SelectSourceGroup"));
                return false;
            }

            String packageName = getComponent().getPackageName();
            if (packageName.trim().equals("")) { // NOI18N
                setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_JavaTargetChooser_CantUseDefaultPackage"));
                return false;
            }

            if (!JavaIdentifiers.isValidPackageName(packageName)) {
                setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_JavaTargetChooser_InvalidPackage")); //NOI18N
                return false;
            }

            if (!isFolderWritable(sourceGroup, packageName)) {
                setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_JavaTargetChooser_UnwritablePackage")); //NOI18N
                return false;
            }

            if (getComponent().getFileName().trim().equals("")) { // NOI18N
                setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_JavaTargetChooser_CantUseDefaultFileName"));
                return false;
            }

            // issue 92192: need to check that we will have a persistence provider
            // available to add to the classpath while generating entity classes (unless
            // the classpath already contains one)
            FileObject packageFO = getFolderForPackage(sourceGroup, packageName, false);
            if (packageFO == null) {
                packageFO = sourceGroup.getRootFolder();
            }
            ClassPath classPath = ClassPath.getClassPath(packageFO, ClassPath.COMPILE);

            if (classPath != null) {
                if (classPath.findResource("jakarta/persistence/EntityManager.class") == null) { // NOI18N
                    // initialize the provider list lazily
                    if (providers == null) {
                        providers = PersistenceLibrarySupport.getProvidersFromLibraries();
                    }
                    if (providers.size() == 0) {
                        setErrorMessage(NbBundle.getMessage(EntityClassesConfigurationPanel.class, "ERR_NoJavaPersistenceAPI")); // NOI18N
                        return false;
                    }
                }
            } else {
                LOGGER.warning("Cannot get a classpath for package " + packageName + " in " + sourceGroup); // NOI18N
            }

            SelectedTables selectedTables = getComponent().getSelectedTables();
            // check for null needed since isValid() can be called when
            // EntityClassesPanel.update() has not been called yet, e.g. from within
            // EntityClassesPanel.initialize()
            if (selectedTables != null) {
                String problem = selectedTables.getFirstProblemDisplayName();
                if (problem != null) {
                    setErrorMessage(problem);
                    return false;
                }
            }

            setErrorMessage(" "); // NOI18N
            return true;
        }

        @Override
        public void storeSettings(Object settings) {
            ImportHelper helper = DBImportWizardDescriptor.getHelper(wizardDescriptor);

            helper.setSelectedTables(getComponent().getSelectedTables());
            helper.setLocation(getComponent().getLocationValue());
            helper.setPackageName(getComponent().getPackageName());
            helper.setFileName(getComponent().getFileName());
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            changeSupport.fireChange();
        }

        private void setErrorMessage(String errorMessage) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, errorMessage); // NOI18N
        }

        @Override
        public boolean isFinishPanel() {
            return isFinishable;
        }
    }

    /**
     * A crude attempt at a label which doesn't expand its parent.
     */
    private static final class ShyLabel extends JLabel {

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.width = 0;
            return size;
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension size = super.getMinimumSize();
            size.width = 0;
            return size;
        }
    }

}
