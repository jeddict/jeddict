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
package io.github.jeddict.reveng.klass;

import io.github.jeddict.analytics.JeddictLogger;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getModelerFileVersion;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.reveng.BaseWizardDescriptor;
import io.github.jeddict.reveng.JCREProcessor;
import io.github.jeddict.source.JavaSourceParserUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.wizard.EntityClosure;
import org.netbeans.modules.j2ee.persistence.wizard.PersistenceClientEntitySelection;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporter;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporterDelegate;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.WizardProperties;
import org.netbeans.modules.j2ee.persistence.wizard.unit.PersistenceUnitWizardDescriptor;
import org.netbeans.modules.j2ee.persistence.wizard.unit.PersistenceUnitWizardPanel.TableGeneration;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.ImageUtilities;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

@ServiceProvider(service = JCREProcessor.class)
@TemplateRegistration(
        folder = "Persistence",
        position = 2,
        displayName = "#RevEngWizardDescriptor_displayName",
        iconBase = "io/github/jeddict/reveng/klass/resources/JPA_FILE_ICON.png",
        description = "resources/JPA_JCRE_DESC.html",
        category = "persistence"
)
public final class RevEngWizardDescriptor extends BaseWizardDescriptor implements JCREProcessor {

    private PersistenceUnitWizardDescriptor puPanel;
    private WizardDescriptor wizard;
    private Project project;

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        index = 0;
        // obtaining target folder
        if (project == null) {
            project = Templates.getProject(wizard);
        }
        WizardDescriptor.Panel secondPanel = new ValidationPanel(project, new PersistenceClientEntitySelection(getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"), null, wizard)); // NOI18N
        WizardDescriptor.Panel thirdPanel = new JPAModelSetupPanel(project, wizard);
        String names[];
        boolean noPuNeeded = true;
        try {
            noPuNeeded = ProviderUtil.persistenceExists(project) || !ProviderUtil.isValidServerInstanceOrNone(project);
        } catch (InvalidPersistenceXmlException ex) {
            Logger.getLogger(RevEngWizardDescriptor.class.getName()).log(Level.FINE, "Invalid persistence.xml: {0}", ex.getPath()); //NOI18N
        }
        if (!noPuNeeded) {
            puPanel = new PersistenceUnitWizardDescriptor(project);
            panels = new ArrayList<>();
            panels.add(secondPanel);
            panels.add(thirdPanel);
            panels.add(puPanel);
            names = new String[]{
                getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"),
                getMessage(RevEngWizardDescriptor.class, "LBL_JPA_Model"),
                getMessage(RevEngWizardDescriptor.class, "LBL_PersistenceUnitSetup")
            };
        } else {

            panels = new ArrayList<>();
            panels.add(secondPanel);
            panels.add(thirdPanel);
            names = new String[]{
                getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"),
                getMessage(RevEngWizardDescriptor.class, "LBL_JPA_Model")
            };
        }

        wizard.putProperty("NewFileWizard_Title", getMessage(RevEngWizardDescriptor.class, "NewFileWizard_Title"));
        org.netbeans.modeler.component.Wizards.mergeSteps(wizard, panels.toArray(new WizardDescriptor.Panel[0]), names);
    }

    @Override
    public Set<?> instantiate() throws IOException {
        final Set<String> entities = new HashSet<>((List) wizard.getProperty(WizardProperties.ENTITY_CLASS));
        if (project == null) {
            project = Templates.getProject(wizard);
        }
        final FileObject packageFileObject = Templates.getTargetFolder(wizard);
        final String fileName = Templates.getTargetName(wizard);
        boolean createPersistenceUnit = (Boolean) wizard.getProperty(org.netbeans.modules.j2ee.persistence.wizard.WizardProperties.CREATE_PERSISTENCE_UNIT);

        if (createPersistenceUnit) {
            PersistenceUnit punit = Util.buildPersistenceUnitUsingData(project, puPanel.getPersistenceUnitName(), puPanel.getPersistenceConnection() != null ? puPanel.getPersistenceConnection().getName() : puPanel.getDatasource(), TableGeneration.NONE, puPanel.getSelectedProvider());
            ProviderUtil.setTableGeneration(punit, puPanel.getTableGeneration(), puPanel.getSelectedProvider());
            if (punit != null) {
                Util.addPersistenceUnitToProject(project, punit);
            }
        }
        final String title = getMessage(RevEngWizardDescriptor.class, "TITLE_Progress_JPA_Model"); //NOI18N

        return instantiateJCREProcess(title, entities, packageFileObject, fileName, false, true);
    }

    @Override
    public String name() {
        return getMessage(RevEngWizardDescriptor.class, "LBL_WizardTitle");
    }

    @Override
    public void process(ModelerFile file) {
        try {
            this.project = file.getProject();
            EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
            FileObject packageFileObject = file.getFileObject().getParent();
            String fileName = file.getFileObject().getName();
            Set<String> entities = entityMappings.getEntity()
                    .stream()
                    .map(e -> StringUtils.isBlank(entityMappings.getPackage())
                    ? e.getClazz() : entityMappings.getPackage() + "." + e.getClazz())
                    .collect(toSet());

            EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(project.getProjectDirectory());
            EntityClosure entityClosure = EntityClosure.create(entityClassScope, project);
            entityClosure.setClosureEnabled(true);
            entityClosure.addEntities(entities);
            if (entityClosure.getSelectedEntities().size() > entities.size()) {
                javax.swing.JOptionPane.showInputDialog("Entity closure have more entity");
            }

            String backupFileName = fileName + "_backup";
            FileObject backupFile = null;
            try {
                backupFile = org.openide.filesystems.FileUtil.copyFile(file.getFileObject(), packageFileObject, backupFileName);
            } catch (Exception ex) {

            }
            instantiateJCREProcess("Updating Design", entities, packageFileObject, fileName, true, false);

            notifyBackupCreation(file, backupFile);
        } catch (IOException ex) {
            file.handleException(ex);
        }
    }

    private Set<?> instantiateJCREProcess(final String title, final Set<String> entities, final FileObject packageFileObject, final String fileName, boolean includeReference, boolean softWrite) throws IOException {
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        final ProgressPanel progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);
        final ProgressReporter reporter = new ProgressReporterDelegate(progressContributor, progressPanel);
        final Runnable r = () -> {
            try {
                handle.start();
                int progressStepCount = getProgressStepCount(entities.size());
                progressContributor.start(progressStepCount);
                generateJPAModel(reporter, entities, project, packageFileObject, fileName, includeReference, softWrite, true);
                progressContributor.progress(progressStepCount);
            } catch (IOException ioe) {
                Logger.getLogger(RevEngWizardDescriptor.class.getName()).log(Level.INFO, null, ioe);
                NotifyDescriptor nd = new NotifyDescriptor.Message(ioe.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } catch (ProcessInterruptedException ce) {
                Logger.getLogger(RevEngWizardDescriptor.class.getName()).log(Level.INFO, null, ce);
            } finally {
                progressContributor.finish();
                SwingUtilities.invokeLater(progressPanel::close);
                JeddictLogger.createModelerFile("JPA-REV-ENG");
                handle.finish();
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            private boolean first = true;

            @Override
            public void run() {
                if (!first) {
                    RequestProcessor.getDefault().post(r);
                    progressPanel.open(progressComponent, title);
                } else {
                    first = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        });
        return Collections.singleton(DataFolder.findFolder(packageFileObject));
    }

    public static int getProgressStepCount(int entityCount) {
        return entityCount + 2;
    }

    public static EntityMappings generateJPAModel(ProgressReporter reporter, Set<String> entities, Project project, FileObject packageFileObject, String fileName) throws IOException, ProcessInterruptedException {
        return generateJPAModel(reporter, entities, project, packageFileObject, fileName, false, true, true);
    }

    public static EntityMappings generateJPAModel(ProgressReporter reporter, Set<String> entities, Project project, FileObject packageFileObject, String fileName, boolean includeReference, boolean softWrite, boolean autoOpen) throws IOException, ProcessInterruptedException {
        int progressIndex = 0;
        String progressMsg = getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Model_Pre"); //NOI18N;
        reporter.progress(progressMsg, progressIndex++);

        String version = getModelerFileVersion();

        final EntityMappings entityMappingsSpec = EntityMappings.getNewInstance(version);
        entityMappingsSpec.setGenerated();

        if (!entities.isEmpty()) {
            String entity = entities.iterator().next();
            String _package = JavaIdentifiers.getPackageName(entity);
            entityMappingsSpec.setProjectPackage(_package.substring(0, _package.lastIndexOf('.')));
            entityMappingsSpec.setEntityPackage(_package.substring(_package.lastIndexOf('.')));
        }

        List<String> missingEntities = new ArrayList<>();
        for (String entityClass : entities) {
            progressMsg = getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Class_Parsing", entityClass + JAVA_EXT_SUFFIX);//NOI18N
            reporter.progress(progressMsg, progressIndex++);
            JPAModelGenerator.generateJPAModel(entityMappingsSpec, project, entityClass, packageFileObject, missingEntities);
        }

        if (includeReference) {
            List<ManagedClass> classes = new ArrayList<>(entityMappingsSpec.getEntity());
            // manageSiblingAttribute for MappedSuperClass and Embeddable is not required for (DBRE) DB REV ENG CASE
            classes.addAll(entityMappingsSpec.getMappedSuperclass());
            classes.addAll(entityMappingsSpec.getEmbeddable());

            for (ManagedClass<IPersistenceAttributes> managedClass : classes) {
                for (RelationAttribute attribute : new ArrayList<>(managedClass.getAttributes().getRelationAttributes())) {
                    String entityClass = StringUtils.isBlank(entityMappingsSpec.getPackage()) ? attribute.getTargetEntity() : entityMappingsSpec.getPackage() + '.' + attribute.getTargetEntity();
                    if (!entities.contains(entityClass)) {
                        progressMsg = getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Class_Parsing", entityClass + JAVA_EXT_SUFFIX);//NOI18N
                        reporter.progress(progressMsg, progressIndex++);
                        JPAModelGenerator.generateJPAModel(entityMappingsSpec, project, entityClass, packageFileObject, missingEntities);
                        entities.add(entityClass);
                    }
                }
            }
        }

        if (!missingEntities.isEmpty()) {
            final String title, _package;
            StringBuilder message = new StringBuilder();
            if (missingEntities.size() == 1) {
                title = "Conflict detected - Entity not found";
                message.append(JavaSourceParserUtil.simpleClassName(missingEntities.get(0))).append(" Entity is ");
            } else {
                title = "Conflict detected - Entities not found";
                message.append("Entities ").append(
                        missingEntities.stream().map(e -> JavaSourceParserUtil.simpleClassName(e)).collect(toList()))
                        .append(" are ");
            }
            if (StringUtils.isEmpty(entityMappingsSpec.getPackage())) {
                _package = "<default_root_package>";
            } else {
                _package = entityMappingsSpec.getPackage();
            }
            message.append("missing in Project classpath[").append(_package).append("]. \n Would like to cancel the process ?");
            SwingUtilities.invokeLater(() -> {
                JButton cancel = new JButton("Cancel import process (Recommended)");
                JButton procced = new JButton("Procced");
                cancel.addActionListener((ActionEvent e) -> {
                    Window w = SwingUtilities.getWindowAncestor(cancel);
                    if (w != null) {
                        w.setVisible(false);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append('\n').append("You have following option to resolve conflict :").append('\n').append('\n');
                    sb.append("1- New File > Persistence > JPA Diagram from Reverse Engineering (Manually select entities)").append('\n');
                    sb.append("2- Recover missing entities manually > Reopen diagram file >  Import entities again");
                    NotifyDescriptor nd = new NotifyDescriptor.Message(sb.toString(), NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                });
                procced.addActionListener((ActionEvent e) -> {
                    Window w = SwingUtilities.getWindowAncestor(cancel);
                    if (w != null) {
                        w.setVisible(false);
                    }
                    manageEntityMappingspec(entityMappingsSpec);
                    JPAModelerUtil.createNewModelerFile(entityMappingsSpec, packageFileObject, fileName, softWrite, autoOpen);
                });

                JOptionPane.showOptionDialog(WindowManager.getDefault().getMainWindow(), message.toString(), title, OK_CANCEL_OPTION,
                        ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), new Object[]{cancel, procced}, cancel);
            });

        } else {
            manageEntityMappingspec(entityMappingsSpec);
            JPAModelerUtil.createNewModelerFile(entityMappingsSpec, packageFileObject, fileName, softWrite, autoOpen);
            return entityMappingsSpec;
        }

        throw new ProcessInterruptedException();
    }

    private static void manageEntityMappingspec(EntityMappings entityMappingsSpec) {
        entityMappingsSpec.manageRefId();
        entityMappingsSpec.repairDefinition(JPAModelerUtil.IO, true);
//        entityMappingsSpec.manageJoinColumnRefName();
    }

    private static void notifyBackupCreation(ModelerFile file, FileObject backupFile) {
        if (backupFile == null) {
            return;
        }
        NotificationDisplayer.getDefault().notify("Backup created",
                ImageUtilities.image2Icon(file.getIcon()),
                "Previous state of file has been saved to " + backupFile.getName() + ". Click here to delete it", (ActionEvent e) -> {
            try {
                if (backupFile.isValid()) {
                    backupFile.delete();
                }
            } catch (IOException ex) {
                file.handleException(ex);
            }
        }, NotificationDisplayer.Priority.NORMAL, NotificationDisplayer.Category.INFO);
    }

    /**
     * A panel which checks that the target project has a valid server set
     * otherwise it delegates to the real panel.
     */
    private static class ValidationPanel extends DelegatingWizardDescriptorPanel {

        private ValidationPanel(Project project, WizardDescriptor.Panel delegate) {
            super(project, delegate);
        }
    }

}
