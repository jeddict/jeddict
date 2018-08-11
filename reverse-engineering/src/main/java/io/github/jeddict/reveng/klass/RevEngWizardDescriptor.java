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

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import io.github.jeddict.analytics.JeddictLogger;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jcode.util.ProjectHelper.findSourceGroupForFile;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderSourceGroup;
import io.github.jeddict.jpa.modeler.initializer.JPAFileActionListener;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.reveng.BaseWizardDescriptor;
import io.github.jeddict.reveng.JCREProcessor;
import static io.github.jeddict.reveng.settings.RevengPanel.isIncludeReferencedClasses;
import io.github.jeddict.source.ClassExplorer;
import io.github.jeddict.source.JavaSourceParserUtil;
import io.github.jeddict.source.SourceExplorer;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Collections.singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showInputDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static org.apache.commons.lang.StringUtils.isEmpty;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.wizard.EntityClosure;
import org.netbeans.modules.j2ee.persistence.wizard.PersistenceClientEntitySelection;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import static org.netbeans.modules.j2ee.persistence.wizard.WizardProperties.CREATE_PERSISTENCE_UNIT;
import static org.netbeans.modules.j2ee.persistence.wizard.WizardProperties.ENTITY_CLASS;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporter;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporterDelegate;
import org.netbeans.modules.j2ee.persistence.wizard.unit.PersistenceUnitWizardDescriptor;
import org.netbeans.modules.j2ee.persistence.wizard.unit.PersistenceUnitWizardPanel.TableGeneration;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import static org.openide.NotifyDescriptor.ERROR_MESSAGE;
import static org.openide.NotifyDescriptor.OK_CANCEL_OPTION;
import org.openide.WizardDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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

    private static final Logger LOG = Logger.getLogger(RevEngWizardDescriptor.class.getName());

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
            LOG.log(FINE, "Invalid persistence.xml: {0}", ex.getPath()); //NOI18N
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

    /**
     * Create new diagram via class reverse engineering
     *
     * @return
     * @throws IOException
     */
    @Override
    public Set<?> instantiate() throws IOException {
        final Set<String> entities = new HashSet<>((List) wizard.getProperty(ENTITY_CLASS));
        if (project == null) {
            project = Templates.getProject(wizard);
        }
        final FileObject targetFilePath = Templates.getTargetFolder(wizard);
        FileObject sourcePackage = getFolderSourceGroup(targetFilePath).getRootFolder();
        final String targetFileName = Templates.getTargetName(wizard);
        boolean createPersistenceUnit = (Boolean) wizard.getProperty(CREATE_PERSISTENCE_UNIT);

        if (createPersistenceUnit) {
            PersistenceUnit persistenceUnit = Util.buildPersistenceUnitUsingData(
                    project,
                    puPanel.getPersistenceUnitName(),
                    nonNull(puPanel.getPersistenceConnection()) ? puPanel.getPersistenceConnection().getName() : puPanel.getDatasource(),
                    TableGeneration.NONE,
                    puPanel.getSelectedProvider()
            );

            if (nonNull(persistenceUnit)) {
                ProviderUtil.setTableGeneration(
                        persistenceUnit,
                        puPanel.getTableGeneration(),
                        puPanel.getSelectedProvider()
                );
                Util.addPersistenceUnitToProject(project, persistenceUnit);
            }
        }
        final String title = getMessage(RevEngWizardDescriptor.class, "TITLE_Progress_JPA_Model");
        EntityMappings entityMappings = EntityMappings.getNewInstance(JPAModelerUtil.getModelerFileVersion());
        entityMappings.setGenerated();
        instantiateJCREProcess(
                title, entityMappings,
                sourcePackage, entities,
                targetFilePath, targetFileName,
                isIncludeReferencedClasses(), true,
                null
        );
        return singleton(DataFolder.findFolder(targetFilePath));
    }

    /**
     * Update the complete existing diagram with existing class
     *
     * @param modelerFile
     */
    @Override
    public void syncExistingDiagram(ModelerFile modelerFile) {
        try {
            this.project = modelerFile.getProject();
            EntityMappings entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
            FileObject targetFilePath = modelerFile.getFileObject().getParent();
            FileObject sourcePackage = getFolderSourceGroup(targetFilePath).getRootFolder();
            String targetFileName = modelerFile.getFileObject().getName();
            Set<String> entities = entityMappings.getEntity()
                    .stream()
                    .map(JavaClass::getFQN)
                    .collect(toSet());

            EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(project.getProjectDirectory());
            EntityClosure entityClosure = EntityClosure.create(entityClassScope, project);
            entityClosure.setClosureEnabled(true);
            entityClosure.addEntities(entities);
            if (entityClosure.getSelectedEntities().size() > entities.size()) {
                showInputDialog("Entity closure have more entity");
            }

            FileObject backupFile = null;
            try {
                String backupFileName = targetFileName + "_backup";
                backupFile = FileUtil.copyFile(modelerFile.getFileObject(), targetFilePath, backupFileName);
            } catch (IOException ex) {
            }
            EntityMappings newEntityMappings = EntityMappings.getNewInstance(JPAModelerUtil.getModelerFileVersion());
            newEntityMappings.setGenerated();
            instantiateJCREProcess(
                    "Updating Design", newEntityMappings,
                    sourcePackage, entities,
                    targetFilePath, targetFileName,
                    isIncludeReferencedClasses(), false,
                    null
            );
            notifyBackupCreation(modelerFile, backupFile);
        } catch (IOException ex) {
            modelerFile.handleException(ex);
        }
    }

    /**
     * Drop classes in existing diagram
     *
     * @param modelerFile
     * @param entityFiles
     */
    @Override
    public void processDropedClasses(ModelerFile modelerFile, List<File> entityFiles) {
        List<FileObject> fileObjects
                = entityFiles
                        .stream()
                        .map(FileUtil::toFileObject)
                        .collect(toList());

        Map<SourceGroup, Set<String>> sourceGroups = new HashMap<>();
        for (FileObject fileObject : fileObjects) {
            SourceGroup sourceGroup = findSourceGroupForFile(fileObject);
            if (!sourceGroups.containsKey(sourceGroup)) {
                sourceGroups.put(sourceGroup, new HashSet<>());
            }
            String entity = fileObject.getPath()
                    .substring(sourceGroup.getRootFolder().getPath().length() + 1, fileObject.getPath().lastIndexOf(JAVA_EXT_SUFFIX))
                    .replace('/', '.');
            sourceGroups.get(sourceGroup).add(entity);
        }

        try {
            EntityMappings entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
            int totalSource = sourceGroups.keySet().size();
            int sourceIndex = 0;
            for (SourceGroup sourceGroup : sourceGroups.keySet()) {
                int currentSource = ++sourceIndex;
                Set<String> entities = sourceGroups.get(sourceGroup);
                instantiateJCREProcess(
                        "Importing classes",
                        entityMappings,
                        sourceGroup.getRootFolder(), entities,
                        null, null,
                        isIncludeReferencedClasses(), false,
                        () -> {
                            if (totalSource == currentSource) {
                                modelerFile.save(true);
                                modelerFile.close();
                                JPAFileActionListener.open(modelerFile);
                            }
                        }
                );
            }
        } catch (IOException ex) {
            modelerFile.handleException(ex);
        }
    }

    private void instantiateJCREProcess(
            final String title,
            final EntityMappings entityMappings,
            final FileObject sourcePackage,
            final Set<String> entities,
            final FileObject targetFilePath,
            final String targetFileName,
            final boolean includeReference,
            final boolean softWrite,
            final Runnable callback) throws IOException {
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        final ProgressPanel progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);
        final ProgressReporter reporter = new ProgressReporterDelegate(progressContributor, progressPanel);
        final Runnable action = () -> {
            try {
                handle.start();
                int progressStepCount = getProgressStepCount(entities.size());
                progressContributor.start(progressStepCount);
                generateJPAModel(
                        reporter, entityMappings,
                        sourcePackage, entities,
                        targetFilePath, targetFileName,
                        includeReference, softWrite, true
                );
                progressContributor.progress(progressStepCount);
            } catch (IOException | UnsolvedSymbolException ex) {
                LOG.log(INFO, null, ex);
                NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getLocalizedMessage(), ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } catch (ParseProblemException ex) {
                LOG.log(INFO, null, ex);
                String message = ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf("Problem stacktrace"));
                NotifyDescriptor nd = new NotifyDescriptor.Message(message, ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } catch (UnsupportedOperationException ex) {
                LOG.log(INFO, null, ex);
                NotifyDescriptor nd = new NotifyDescriptor.Message("uncompilable source code detected", ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } catch (ProcessInterruptedException ex) {
                LOG.log(INFO, null, ex);
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
                    RequestProcessor.getDefault().post(action);
                    progressPanel.open(progressComponent, title);
                    if (nonNull(callback)) {
                        callback.run();
                    }
                } else {
                    first = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        });
    }

    private EntityMappings generateJPAModel(
            final ProgressReporter reporter,
            EntityMappings entityMappings,
            final FileObject sourcePackage,
            final Set<String> entities,
            final FileObject targetFilePath,
            final String targetFileName,
            final boolean includeReference,
            final boolean softWrite,
            final boolean autoOpen) throws IOException, ProcessInterruptedException {

        int progressIndex = 0;
        String progressMsg = getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Model_Pre"); //NOI18N;
        reporter.progress(progressMsg, progressIndex++);

        List<String> missingEntities = new ArrayList<>();
        SourceExplorer source = new SourceExplorer(sourcePackage, entityMappings, entities, includeReference);
        for (String entityClassFQN : entities) {
            try {
                source.createClass(entityClassFQN);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                missingEntities.add(entityClassFQN);
            }
        }

        progressIndex = loadJavaClasses(reporter, progressIndex, source.getClasses(), entityMappings);
        List<ClassExplorer> classes = checkReferencedClasses(source, missingEntities, includeReference);
        while (!classes.isEmpty()) {
            progressIndex = loadJavaClasses(reporter, progressIndex, classes, entityMappings);
            classes = checkReferencedClasses(source, missingEntities, includeReference);
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
            if (isEmpty(entityMappings.getPackage())) {
                _package = "<default_root_package>";
            } else {
                _package = entityMappings.getPackage();
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
                procced.addActionListener(e -> {
                    Window window = SwingUtilities.getWindowAncestor(cancel);
                    if (nonNull(window)) {
                        window.setVisible(false);
                    }
                    manageEntityMapping(entityMappings);
                    if (nonNull(targetFilePath) && nonNull(targetFileName)) {
                        JPAModelerUtil.createNewModelerFile(entityMappings, targetFilePath, targetFileName, softWrite, autoOpen);
                    }
                });

                JOptionPane.showOptionDialog(WindowManager.getDefault().getMainWindow(), message.toString(), title, OK_CANCEL_OPTION,
                        ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), new Object[]{cancel, procced}, cancel);
            });

        } else {
            manageEntityMapping(entityMappings);
            if (nonNull(targetFilePath) && nonNull(targetFileName)) {
                JPAModelerUtil.createNewModelerFile(entityMappings, targetFilePath, targetFileName, softWrite, autoOpen);
            }
            return entityMappings;
        }

        throw new ProcessInterruptedException();
    }

    private int loadJavaClasses(
            final ProgressReporter reporter,
            int progressIndex,
            final List<ClassExplorer> selectedClasses,
            final EntityMappings entityMappings) {

        List<ClassExplorer> classes = new CopyOnWriteArrayList<>(selectedClasses);
        classes.sort((c1, c2) -> c1.isEntity() || c1.isMappedSuperclass() ? -1 : 1);

        for (ClassExplorer clazz : classes) {
            String progressMsg = getMessage(RevEngWizardDescriptor.class, "MSG_Progress_JPA_Class_Parsing", clazz.getName() + JAVA_EXT_SUFFIX);//NOI18N
            reporter.progress(progressMsg, progressIndex++);
            parseJavaClass(entityMappings, clazz);
        }
        return progressIndex;
    }

    private List<ClassExplorer> checkReferencedClasses(SourceExplorer source, List<String> missingEntities, boolean includeReference) {
        List<ClassExplorer> newReferencedClass = new ArrayList<>();
        EntityMappings entityMappings = source.getEntityMappings();
        if (includeReference) {
            // manageSiblingAttribute for MappedSuperClass and Embeddable is not required for (DBRE) DB REV ENG CASE
            for (ManagedClass<IPersistenceAttributes> managedClass : entityMappings.getAllManagedClass()) {
                for (RelationAttribute attribute : new ArrayList<>(managedClass.getAttributes().getRelationAttributes())) {
                    String entityClass = attribute.getTargetEntity();
                    String entityClassFQN = attribute.getTargetEntityFQN();
                    if (entityMappings.findAllJavaClass(entityClass).isEmpty()) {
                        ClassExplorer clazz;
                        try {
                            clazz = source.createClass(entityClassFQN);
                            newReferencedClass.add(clazz);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                            missingEntities.add(entityClassFQN);
                        }
                    }
                }
            }

        }
        return newReferencedClass;
    }

    private Optional<JavaClass> parseJavaClass(
            final EntityMappings entityMappings,
            final ClassExplorer clazz) {

        JavaClass javaClass = null;
        String className = clazz.getName();

        if (clazz.isEntity()) {
            if (!entityMappings.findEntity(className).isPresent()) {
                Entity entity;
                javaClass = entity = new Entity();
                entity.load(clazz);
                entityMappings.addEntity(entity);
            }
        } else if (clazz.isMappedSuperclass()) {
            if (!entityMappings.findMappedSuperclass(className).isPresent()) {
                MappedSuperclass mappedSuperclass;
                javaClass = mappedSuperclass = new MappedSuperclass();
                mappedSuperclass.load(clazz);
                entityMappings.addMappedSuperclass(mappedSuperclass);
            }
        } else if (clazz.isEmbeddable()) {

            if (!entityMappings.isCompositePrimaryKeyClass(clazz.getName())
                    && !entityMappings.findEmbeddable(className).isPresent()) {
                Embeddable embeddable;
                javaClass = embeddable = new Embeddable();
                embeddable.load(clazz);
                entityMappings.addEmbeddable(embeddable);
            }
        } else if (!clazz.isEnum() && !clazz.isInterface()) {

            if (!entityMappings.isCompositePrimaryKeyClass(clazz.getName())
                    && !entityMappings.findBeanClass(className).isPresent()) {
                BeanClass beanClass;
                javaClass = beanClass = new BeanClass();
                beanClass.load(clazz);
                entityMappings.addBeanClass(beanClass);
            }
        }

        return Optional.ofNullable(javaClass);
    }

    @Override
    public String name() {
        return getMessage(RevEngWizardDescriptor.class, "LBL_WizardTitle");
    }

    public static int getProgressStepCount(int entityCount) {
        return entityCount + 2;
    }

    private static void manageEntityMapping(EntityMappings entityMappings) {
        entityMappings.manageRefId();
        entityMappings.repairDefinition(JPAModelerUtil.IO, true);
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
