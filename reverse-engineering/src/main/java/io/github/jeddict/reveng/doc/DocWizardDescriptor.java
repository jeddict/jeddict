/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.reveng.doc;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.reveng.BaseWizardDescriptor;
import static io.github.jeddict.reveng.doc.DocSetupPanelVisual.JAXB_SUPPORT;
import static io.github.jeddict.reveng.doc.DocSetupPanelVisual.JPA_SUPPORT;
import static io.github.jeddict.reveng.doc.DocSetupPanelVisual.JSONB_SUPPORT;
import static io.github.jeddict.reveng.doc.DocSetupPanelVisual.JSON_FILE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Objects.nonNull;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporter;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporterDelegate;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;

@TemplateRegistration(
        folder = "Persistence",
        position = 2,
        displayName = "#DocWizardDescriptor_displayName",
        iconBase = "io/github/jeddict/reveng/doc/resources/DOC_ICON.png",
        description = "resources/DOC_RE_DESC.html",
        category = "persistence")
public final class DocWizardDescriptor extends BaseWizardDescriptor {

    private WizardDescriptor wizard;
    private Project project;

    private FileObject packageFileObject;
    private String fileName, docFileLocation;

    private ProgressReporter reporter;
    private DocParser parser;
    private int progressIndex = 0;

    private final Consumer<String> reporterConsumer = progressMsg -> reporter.progress(progressMsg, progressIndex++);

    public DocWizardDescriptor() {
    }

    public DocWizardDescriptor(
            Project project,
            String docFileLocation,
            boolean jpaSupport,
            boolean jsonbSupport,
            boolean jaxbSupport) {
        this.project = project;
        this.docFileLocation = docFileLocation;
        if (docFileLocation.toLowerCase().endsWith("json")) {
            parser = new JsonParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("xml")) {
            parser = new XmlParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("yaml") || docFileLocation.toLowerCase().endsWith("yml")) {
            parser = new YmlParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("jpa")) {
            parser = new ModelerParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        }
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        index = 0;
        if (project == null) {
            project = Templates.getProject(wizard);
        }
        WizardDescriptor.Panel secondPanel = new DocSetupPanel(project, wizard);
        String names[];

        panels = new ArrayList<>();
        panels.add(secondPanel);
        names = new String[]{
            getMessage(DocWizardDescriptor.class, "LBL_Doc_Setup")
        };

        wizard.putProperty("NewFileWizard_Title", getMessage(DocWizardDescriptor.class, "NewFileWizard_Title"));
        org.netbeans.modeler.component.Wizards.mergeSteps(wizard, panels.toArray(new WizardDescriptor.Panel[0]), names);
    }

    @Override
    public Set<?> instantiate() throws IOException {
        if (project == null) {
            project = Templates.getProject(wizard);
        }
        packageFileObject = Templates.getTargetFolder(wizard);
        fileName = Templates.getTargetName(wizard);
        docFileLocation = (String) wizard.getProperty(JSON_FILE);
        boolean jpaSupport = (Boolean) wizard.getProperty(JPA_SUPPORT);
        boolean jsonbSupport = (Boolean) wizard.getProperty(JSONB_SUPPORT);
        boolean jaxbSupport = (Boolean) wizard.getProperty(JAXB_SUPPORT);
        if (docFileLocation.toLowerCase().endsWith("json")) {
            parser = new JsonParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("xml")) {
            parser = new XmlParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("yaml") || docFileLocation.toLowerCase().endsWith("yml")) {
            parser = new YmlParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        } else if (docFileLocation.toLowerCase().endsWith("jpa")) {
            parser = new ModelerParser(reporterConsumer, jpaSupport, jsonbSupport, jaxbSupport);
        }
        instantiateProcess(null, null);
        return Collections.singleton(DataFolder.findFolder(packageFileObject));
    }

    @Override
    public String name() {
        return NbBundle.getMessage(DocWizardDescriptor.class, "LBL_WizardTitle");
    }

    public void instantiateProcess(final EntityMappings entityMappings, final Runnable runnable) throws IOException {
        final String title = NbBundle.getMessage(DocWizardDescriptor.class, "TITLE_Progress_Class_Diagram"); //NOI18N
        final ProgressContributor progressContributor = AggregateProgressFactory.createProgressContributor(title);
        final AggregateProgressHandle handle = AggregateProgressFactory.createHandle(title, new ProgressContributor[]{progressContributor}, null, null);
        final ProgressPanel progressPanel = new ProgressPanel();
        final JComponent progressComponent = AggregateProgressFactory.createProgressComponent(handle);
        ProgressReporterDelegate reporter = new ProgressReporterDelegate(progressContributor, progressPanel);
        final Runnable r = () -> {
            try {
                handle.start();
                int progressStepCount = getProgressStepCount(10);
                progressContributor.start(progressStepCount);
                if (entityMappings != null) {
                    generate(reporter, entityMappings);
                } else {
                    EntityMappings entityMappingsResult = generate(reporter, entityMappings);
                    JPAModelerUtil.createNewModelerFile(entityMappingsResult, packageFileObject, fileName, true, true);
                }
                progressContributor.progress(progressStepCount);
            } catch (IOException ioe) {
                Logger.getLogger(DocWizardDescriptor.class.getName()).log(Level.INFO, null, ioe);
                NotifyDescriptor nd = new NotifyDescriptor.Message(ioe.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } catch (ProcessInterruptedException ce) {
                Logger.getLogger(DocWizardDescriptor.class.getName()).log(Level.INFO, null, ce);
            } finally {
                progressContributor.finish();
                SwingUtilities.invokeLater(progressPanel::close);
                JeddictLogger.createModelerFile("DOC-REV-ENG");
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
                    if (nonNull(runnable)) {
                        runnable.run();
                    }
                } else {
                    first = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        });
    }

    public EntityMappings generate(final ProgressReporter reporter, final EntityMappings entityMappings) throws FileNotFoundException, IOException, ProcessInterruptedException {
        this.reporter = reporter;
        File file = new File(docFileLocation);
        Reader reader = new FileReader(file);
        return parser.generateModel(entityMappings, reader);
    }

    public static int getProgressStepCount(int baseCount) {
        return baseCount + 2;
    }

}
