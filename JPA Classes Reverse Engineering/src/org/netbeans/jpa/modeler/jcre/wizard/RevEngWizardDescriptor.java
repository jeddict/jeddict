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
package org.netbeans.jpa.modeler.jcre.wizard;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.jpa.modeler._import.javaclass.JCREProcessor;
import org.netbeans.jpa.modeler.source.JavaIdentifiers;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.getModelerFileVersion;
import org.netbeans.modeler.core.ModelerFile;
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
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = JCREProcessor.class)
@TemplateRegistration(folder = "Persistence", position = 2, displayName = "#RevEngWizardDescriptor_displayName", iconBase = "org/netbeans/jpa/modeler/jcre/wizard/resource/JPA_FILE_ICON.png", description = "resource/JPA_JCRE_DESC.html")
public final class RevEngWizardDescriptor implements WizardDescriptor.InstantiatingIterator<WizardDescriptor>, JCREProcessor {

    private int index;
    private PersistenceUnitWizardDescriptor puPanel;
    WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private Project project;

    @Override
    public Set<?> instantiate() throws IOException {
        final Set<String> entities = new HashSet<String>((List) wizard.getProperty(WizardProperties.ENTITY_CLASS));
        if (getProject() == null) {
            setProject(Templates.getProject(wizard));
        }
        final FileObject packageFileObject = Templates.getTargetFolder(wizard);
        final String fileName = Templates.getTargetName(wizard);
        boolean createPersistenceUnit = (Boolean) wizard.getProperty(org.netbeans.modules.j2ee.persistence.wizard.WizardProperties.CREATE_PERSISTENCE_UNIT);

        if (createPersistenceUnit) {
            PersistenceUnit punit = Util.buildPersistenceUnitUsingData(getProject(), puPanel.getPersistenceUnitName(), puPanel.getPersistenceConnection() != null ? puPanel.getPersistenceConnection().getName() : puPanel.getDatasource(), TableGeneration.NONE, puPanel.getSelectedProvider());
            ProviderUtil.setTableGeneration(punit, puPanel.getTableGeneration(), puPanel.getSelectedProvider());
            if (punit != null) {
                Util.addPersistenceUnitToProject(getProject(), punit);
            }
        }
        final String title = NbBundle.getMessage(RevEngWizardDescriptor.class, "TITLE_Progress_JPA_Model"); //NOI18N

        return instantiateJCREProcess(title, entities, packageFileObject, fileName, false, true);
    }

    private Set<?> instantiateJCREProcess(final String title, final Set<String> entities, final FileObject packageFileObject, final String fileName, boolean includeReference, boolean softWrite) throws IOException {
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        final ProgressPanel progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);
        final ProgressReporter reporter = new ProgressReporterDelegate(progressContributor, progressPanel);
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    handle.start();
                    int progressStepCount = getProgressStepCount(entities.size());
                    progressContributor.start(progressStepCount);
                    generateJPAModel(reporter, entities, getProject(), packageFileObject, fileName, includeReference, softWrite, true);
                    progressContributor.progress(progressStepCount);
                } catch (IOException ioe) {
                    Logger.getLogger(RevEngWizardDescriptor.class.getName()).log(Level.INFO, null, ioe);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(ioe.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                } finally {
                    progressContributor.finish();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressPanel.close();
                        }
                    });
                    handle.finish();
                }
            }
        };

        // Ugly hack ensuring the progress dialog opens after the wizard closes. Needed because:
        // 1) the wizard is not closed in the AWT event in which instantiate() is called.
        //    Instead it is closed in an event scheduled by SwingUtilities.invokeLater().
        // 2) when a modal dialog is created its owner is set to the foremost modal
        //    dialog already displayed (if any). Because of #1 the wizard will be
        //    closed when the progress dialog is already open, and since the wizard
        //    is the owner of the progress dialog, the progress dialog is closed too.
        // The order of the events in the event queue:
        // -  this event
        // -  the first invocation event of our runnable
        // -  the invocation event which closes the wizard
        // -  the second invocation event of our runnable
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

    public static EntityMappings generateJPAModel(ProgressReporter reporter, Set<String> entities, Project project, FileObject packageFileObject, String fileName) throws IOException {
        return generateJPAModel(reporter, entities, project, packageFileObject, fileName, false, true, true);
    }

    public static EntityMappings generateJPAModel(ProgressReporter reporter, Set<String> entities, Project project, FileObject packageFileObject, String fileName, boolean includeReference, boolean softWrite, boolean autoOpen) throws IOException {
        int progressIndex = 0;
        String progressMsg = NbBundle.getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Model_Pre"); //NOI18N;
        reporter.progress(progressMsg, progressIndex++);

        String version = getModelerFileVersion();

        EntityMappings entityMappingsSpec = EntityMappings.getNewInstance(version);
        entityMappingsSpec.setGenerated();

        if (!entities.isEmpty()) {
            String entity = entities.iterator().next();
            entityMappingsSpec.setPackage(JavaIdentifiers.getPackageName(entity));
        }

        for (String entityClass : entities) {
            progressMsg = NbBundle.getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Class_Parsing", entityClass + ".java");//NOI18N
            reporter.progress(progressMsg, progressIndex++);
            JPAModelGenerator.generateJPAModel(entityMappingsSpec, project, entityClass, packageFileObject);
        }

        if (includeReference) {
            List<ManagedClass> classes = new ArrayList<>(entityMappingsSpec.getEntity());
            // manageSiblingAttribute for MappedSuperClass and Embeddable is not required for (DBRE) DB REV ENG CASE
            classes.addAll(entityMappingsSpec.getMappedSuperclass());
            classes.addAll(entityMappingsSpec.getEmbeddable());

            for (ManagedClass managedClass : classes) {
                for (RelationAttribute attribute : new ArrayList<>(managedClass.getAttributes().getRelationAttributes())) {
                    String entityClass = StringUtils.isBlank(entityMappingsSpec.getPackage()) ? attribute.getTargetEntity() : entityMappingsSpec.getPackage() + '.' + attribute.getTargetEntity();
                    if (!entities.contains(entityClass)) {
                        progressMsg = NbBundle.getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Class_Parsing", entityClass + ".java");//NOI18N
                        reporter.progress(progressMsg, progressIndex++);
                        JPAModelGenerator.generateJPAModel(entityMappingsSpec, project, entityClass, packageFileObject);
                        entities.add(entityClass);
                    }
                }
            }
        }

        entityMappingsSpec.manageSiblingAttribute();
        entityMappingsSpec.repairDefinition(JPAModelerUtil.IO,true);
        entityMappingsSpec.manageJoinColumnRefName();

        JPAModelerUtil.createNewModelerFile(entityMappingsSpec, packageFileObject, fileName, softWrite, autoOpen);
        return entityMappingsSpec;
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        
        this.wizard = wizard;
        index = 0;
        // obtaining target folder
        if (getProject() == null) {
            setProject(Templates.getProject(wizard));
        }
        WizardDescriptor.Panel secondPanel = new ValidationPanel(getProject(), new PersistenceClientEntitySelection(NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"), null, wizard)); // NOI18N
        WizardDescriptor.Panel thirdPanel = new JPAModelSetupPanel(getProject(), wizard);
        String names[];
        boolean noPuNeeded = true;
        try {
            noPuNeeded = ProviderUtil.persistenceExists(getProject()) || !ProviderUtil.isValidServerInstanceOrNone(project);
        } catch (InvalidPersistenceXmlException ex) {
            Logger.getLogger(RevEngWizardDescriptor.class.getName()).log(Level.FINE, "Invalid persistence.xml: {0}", ex.getPath()); //NOI18N
        }
        if (!noPuNeeded) {
            puPanel = new PersistenceUnitWizardDescriptor(getProject());
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(secondPanel);
            panels.add(thirdPanel);
            panels.add(puPanel);
            names = new String[]{
                NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"),
                NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_JPA_Model"),
                NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_PersistenceUnitSetup")
            };
        } else {

            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(secondPanel);
            panels.add(thirdPanel);
            names = new String[]{
                NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_EntityClasses"),
                NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_JPA_Model")
            };
        }

        wizard.putProperty("NewFileWizard_Title", NbBundle.getMessage(RevEngWizardDescriptor.class, "NewFileWizard_Title_JPAModelFromEntities"));
        org.netbeans.modeler.component.Wizards.mergeSteps(wizard, panels.toArray(new WizardDescriptor.Panel[0]), names);
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return panels.get(index);
    }

    @Override
    public String name() {
        return NbBundle.getMessage(RevEngWizardDescriptor.class, "LBL_WizardTitle_FromEntity");
    }

    @Override
    public boolean hasNext() {
        return index < panels.size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void process(ModelerFile file) {
        try {

            this.setProject(file.getProject());
            EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
            FileObject packageFileObject = file.getFileObject().getParent();
            String fileName = file.getFileObject().getName();
            Set<String> entities = entityMappings.getEntity().stream()
                    .map(e -> StringUtils.isBlank(entityMappings.getPackage()) ? e.getClazz() : entityMappings.getPackage() + "." + e.getClazz()).collect(toSet());

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

    private static void notifyBackupCreation(ModelerFile file, FileObject backupFile) {
        if(backupFile==null){
            return;
        }
        NotificationDisplayer.getDefault().notify("Backup created",
                ImageUtilities.image2Icon(file.getIcon()),// ImageUtilities.loadImageIcon("org/netbeans/jpa/modeler/specification/model/file/JPA_FILE_ICON.png", false),
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

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

}
