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
package org.netbeans.jpa.modeler.reveng.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modeler.component.Wizards;
import org.netbeans.jpa.modeler.source.SourceGroups;
import org.netbeans.jpa.modeler.reveng.database.generator.IPersistenceGeneratorProvider;
import org.netbeans.jpa.modeler.reveng.database.generator.IPersistenceModelGenerator;
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
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

@TemplateRegistration(folder = "Persistence", position = 2, displayName = "#DBImportWizardDescriptor_displayName", iconBase = "org/netbeans/jpa/modeler/reveng/database/resource/JPA_FILE_ICON.png", description = "resource/JPA_DB_IMPORT_DESC.html")
public final class DBImportWizardDescriptor implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private static final String PROP_HELPER = "wizard-helper"; //NOI18N
    private int currentPanel = 0;
    private WizardDescriptor wizardDescriptor;
    private IPersistenceModelGenerator generator;
    private ImportHelper helper;
    private ProgressPanel progressPanel;
    private Project project;
    private final RequestProcessor RP = new RequestProcessor(DBImportWizardDescriptor.class.getSimpleName(), 5);

    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    private static IPersistenceModelGenerator createPersistenceGenerator() {
        IPersistenceGeneratorProvider persistenceGeneratorProvider = (IPersistenceGeneratorProvider) Lookup.getDefault().lookup(IPersistenceGeneratorProvider.class);
        return persistenceGeneratorProvider.createGenerator();
    }

    static ImportHelper getHelper(WizardDescriptor wizardDescriptor) {
        return (ImportHelper) wizardDescriptor.getProperty(PROP_HELPER);
    }

    @Override
    public Set<?> instantiate() throws IOException {
        // TODO return set of FileObject (or DataObject) you have created

        final String title = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_EntityClassesGeneration");
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle
                = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    handle.start();
                    createModel(wizardDescriptor, progressContributor);
                } catch (Exception ioe) {
                    Logger.getLogger("global").log(Level.INFO, null, ioe);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(ioe.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                } finally {
                    generator.uninit();
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
                    RP.post(r);
                    progressPanel.open(progressComponent, title);
                } else {
                    first = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        });

        // The commented code below is the ideal state, but since there is not way to request
        // TemplateWizard.Iterator.instantiate() be called asynchronously it
        // would cause the wizard to stay visible until the bean generation process
        // finishes. So for now just returning the package -- not a problem,
        // JavaPersistenceGenerator.createdObjects() returns an empty set anyway.
        // remember to wait for createBeans() to actually return!
        // Set created = generator.createdObjects();
        // if (created.size() == 0) {
        //     created = Collections.singleton(SourceGroupSupport.getFolderForPackage(helper.getLocation(), helper.getPackageName()));
        // }
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
                SourceGroups.getFolderForPackage(helper.getLocation(), helper.getPackageName())));
//        return Collections.emptySet();
    }

    private void createModel(WizardDescriptor wiz, ProgressContributor handle) throws IOException {
        try {
            handle.start(1); //TODO: need the correct number of work units here
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

//                String projectName = ProjectUtils.getInformation(project).getDisplayName();
            }

            String extracting = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_ExtractingEntityClassesAndRelationships");

            handle.progress(extracting);
            progressPanel.setText(extracting);

            helper.buildBeans();
            generator.generateModel(progressPanel, helper, dbschemaFile, handle);

        } finally {
            handle.finish();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressPanel.close();
                }
            });
        }
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        wizardDescriptor = wizard;

        project = Templates.getProject(wizard);

        panels = createPanels();
        Wizards.mergeSteps(wizardDescriptor, panels.toArray(new WizardDescriptor.Panel[0]), createSteps());

        generator = createPersistenceGenerator();

        FileObject configFilesFolder = PersistenceLocation.getLocation(project);

        helper = new ImportHelper(project, configFilesFolder, generator);

        wizard.putProperty(PROP_HELPER, helper);

        generator.init(wizard);

    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
        generator.uninit();
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return panels.get(currentPanel);
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return currentPanel > 0;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        currentPanel--;
    }

    @Override
    public boolean hasNext() {
        return currentPanel < panels.size() - 1;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        currentPanel++;
    }

//    @Override
//    public WizardDescriptor.Panel<WizardDescriptor> current() {
//        return panels.get(currentPanel);
//    }
    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
//    private String[] createSteps() {
//        String[] beforeSteps = (String[]) wizard.getProperty("WizardPanel_contentData");
//        assert beforeSteps != null : "This wizard may only be used embedded in the template wizard";
//        String[] res = new String[(beforeSteps.length - 1) + panels.size()];
//        for (int i = 0; i < res.length; i++) {
//            if (i < (beforeSteps.length - 1)) {
//                res[i] = beforeSteps[i];
//            } else {
//                res[i] = panels.get(i - beforeSteps.length + 1).getComponent().getName();
//            }
//        }
//        return res;
//    }
    private String[] createSteps() {

        return new String[]{
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_DatabaseTables"),
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_EntityClasses"),
            NbBundle.getMessage(DBImportWizardDescriptor.class, "LBL_MappingOptions"),};

    }

    private List<WizardDescriptor.Panel<WizardDescriptor>> createPanels() {

        String wizardTitle = NbBundle.getMessage(DBImportWizardDescriptor.class, "TXT_TITLE");

        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new DatabaseTablesSelectorPanel.WizardPanel(wizardTitle));
        panels.add(new EntityClassesConfigurationPanel.WizardPanel());
        return panels;

    }

}
