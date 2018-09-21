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
package io.github.jeddict.reveng.database;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.collaborate.issues.ExceptionUtils;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.reveng.BaseWizardDescriptor;
import io.github.jeddict.reveng.database.generator.DBModelGenerator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.Optional;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modeler.component.Wizards;
import org.netbeans.modules.j2ee.persistence.api.PersistenceLocation;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

@TemplateRegistration(
        folder = "Persistence",
        position = 2,
        displayName = "#DBImportWizardDescriptor_displayName",
        iconBase = "io/github/jeddict/reveng/database/resources/JPA_FILE_ICON.png",
        description = "resources/JPA_DB_IMPORT_DESC.html",
        category = "persistence"
)
public final class DBImportWizardDescriptor extends BaseWizardDescriptor {

    private static final String PROP_HELPER = "wizard-helper"; //NOI18N
    private WizardDescriptor wizard;
    private ImportHelper helper;
    private ProgressPanel progressPanel;
    private Project project;
    private DBModelGenerator generator;
    private final RequestProcessor RP = new RequestProcessor(DBImportWizardDescriptor.class.getSimpleName(), 5);

    static ImportHelper getHelper(WizardDescriptor wizardDescriptor) {
        return (ImportHelper) wizardDescriptor.getProperty(PROP_HELPER);
    }

    @Override
    public Set<?> instantiate() throws IOException {
        return instantiateProcess(null);
    }

    public Set<?> instantiateProcess(final Runnable runnable) throws IOException {
        final String title = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_EntityClassesGeneration");
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle
                = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);

        final Runnable r = () -> {
            try {
                handle.start();
                createModel(progressContributor);
            } catch (IOException t) {
                ExceptionUtils.printStackTrace(t);
            } finally {
                JeddictLogger.createModelerFile("DB-REV-ENG");
                handle.finish();
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
                    RP.post(r);
                    progressPanel.open(progressComponent, title);
                    if (nonNull(runnable)) {
                        runnable.run();
                    }
                } else {
                    first = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        });

        if (helper.getDBSchemaFile() != null) {//for now open persistence.xml in case of schema was used, as it's 99% will require persistence.xml update
            DataObject dObj = null;
            try {
                dObj = ProviderUtil.getPUDataObject(project);
            } catch (InvalidPersistenceXmlException ex) {
            }
            if (dObj != null) {
                return Collections.<DataObject>singleton(dObj);
            }
        }

        return Collections.<DataObject>singleton(DataFolder.findFolder(
                getFolderForPackage(helper.getLocation(), helper.getPackageName(), true)));
    }

    public void createModel(ProgressContributor handle) throws IOException {
        try {
            handle.progress(NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_SavingSchema"));
            progressPanel.setText(NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_SavingSchema"));

            FileObject dbschemaFile = helper.getDBSchemaFile();
            if (dbschemaFile == null) {
                File f = new File(project.getProjectDirectory().getPath() + File.separator + "src");
                FileObject configFilesFolder = FileUtil.toFileObject(f);

                if (configFilesFolder == null) {
                    String message = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_NoConfigFiles");
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE));
                    return;
                }
            }

            String extracting = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_ExtractingEntityClassesAndRelationships");
            handle.progress(extracting);
            progressPanel.setText(extracting);

            helper.buildBeans();
            generator.generateModel(progressPanel, helper, dbschemaFile, handle);
        } finally {
            handle.finish();
            SwingUtilities.invokeLater(progressPanel::close);
        }
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.project = Templates.getProject(wizard);
        generator = new DBModelGenerator(project);
        FileObject configFilesFolder = PersistenceLocation.getLocation(project);
        this.helper = new ImportHelper(project, configFilesFolder);

        this.wizard = wizard;
        panels = createPanels();
        Wizards.mergeSteps(wizard, panels.toArray(new WizardDescriptor.Panel[0]), createSteps());
        this.wizard.putProperty(PROP_HELPER, helper);
    }

    public void initialize(Project project, EntityMappings entityMappings, Optional<JavaClass> javaClass) {
        this.project = project;
        generator = new DBModelGenerator(project, entityMappings, javaClass);
        FileObject configFilesFolder = PersistenceLocation.getLocation(project);
        this.helper = new ImportHelper(project, configFilesFolder);
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public ImportHelper getHelper() {
        return helper;
    }

    private String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_DatabaseTables"),
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_EntityClasses"),
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_MappingOptions"),};
    }

    private List<WizardDescriptor.Panel<WizardDescriptor>> createPanels() {
        String wizardTitle = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_TITLE");
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new DatabaseTablesSelectorPanel.WizardPanel(wizardTitle));
        panels.add(new EntityClassesConfigurationPanel.WizardPanel());
        return panels;
    }

}
