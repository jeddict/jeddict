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
package io.github.jeddict.jpa.modeler.source.generator.task;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.*;
import io.github.jeddict.jcode.generator.ApplicationGenerator;
import io.github.jeddict.jcode.task.AbstractNBTask;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jcode.task.progress.ProgressConsoleHandler;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.FileUtil.readString;
import io.github.jeddict.jpa.modeler.initializer.PreExecutionUtil;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.IPersistenceXMLGenerator;
import io.github.jeddict.orm.generator.ISourceCodeGenerator;
import io.github.jeddict.orm.generator.ISourceCodeGeneratorFactory;
import io.github.jeddict.orm.generator.SourceCodeGeneratorType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.modeler.core.ModelerFile;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;

public class SourceCodeGeneratorTask extends AbstractNBTask {

    private final ModelerFile modelerFile;
    private final ApplicationConfigData appConfigData;
    private final Runnable afterExecution;

    private final static int SUBTASK_TOT = 1;
    private static final String BANNER_TXT;

    static {
        String text = "Jeddict <https://jeddict.github.io>";
        try (InputStream stream = SourceCodeGeneratorTask.class.getResourceAsStream("banner")) {
            text = Console.wrap(readString(stream), FG_MAGENTA);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            BANNER_TXT = text;
        }
    }

    public SourceCodeGeneratorTask(ModelerFile modelerFile, ApplicationConfigData appConfigData, Runnable afterExecution) {
        this.modelerFile = modelerFile;
        this.appConfigData = appConfigData;
        this.afterExecution=afterExecution;
        initialize();
    }

    @Override
    protected void initTask() {
        setLogLevel(TERSE);
        setTaskName(getMessage(SourceCodeGeneratorTask.class, "LBL_GenerateCodeDialogTitle", appConfigData.getTargetArtifactId()));
        setDisplayOutput(true);
        progressContribs = new ProgressContributor[SUBTASK_TOT];
        progressContribs[0] = AggregateProgressFactory
                .createProgressContributor(getMessage(SourceCodeGeneratorTask.class, "MSG_Processing")); // NOI18N
    }

    @Override
    protected void begin() {
        PreExecutionUtil.preExecution(modelerFile);
        try {
            EntityMappings entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
            exportCode(appConfigData, entityMappings, this);
        } catch (Throwable t) {
            modelerFile.handleException(t);
        } finally {
            JeddictLogger.logGenerateEvent(appConfigData);
        }
    }

    @Override
    protected void finish() {
        modelerFile.save(true);
        if (afterExecution != null) {
            RequestProcessor.getDefault().post(afterExecution);
        }
    }

    /**
     * @param elements The collection of elements to generate for
     *
     */
    public static void exportCode(
            ApplicationConfigData appConfigData,
            EntityMappings entityMappings,
            ITaskSupervisor task) {

        ProgressHandler handler = new ProgressConsoleHandler(task);
        handler.append(BANNER_TXT);

        ISourceCodeGeneratorFactory sourceGeneratorFactory = Lookup.getDefault().lookup(ISourceCodeGeneratorFactory.class);
        ISourceCodeGenerator domainGenerator = sourceGeneratorFactory.getSourceGenerator(SourceCodeGeneratorType.JPA);
        ApplicationGenerator applicationGenerator = null;
        
        appConfigData.setEntityMappings(entityMappings);
        if (appConfigData.getRepositoryTechContext()!= null) {
            applicationGenerator = new ApplicationGenerator();
            applicationGenerator.initialize(appConfigData, handler);
            applicationGenerator.preGeneration();
        }
        
        if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
            domainGenerator.generate(task, appConfigData);
        }
        if (appConfigData.isGateway()) {
            Lookup.getDefault()
                    .lookup(IPersistenceXMLGenerator.class)
                    .generatePersistenceXML(
                            task,
                            appConfigData.getGatewayProject(), 
                            appConfigData.getGatewaySourceGroup(),
                            entityMappings,
                            Collections.emptyList());
        }
        
        if (appConfigData.getRepositoryTechContext()!= null) {
            applicationGenerator.generate();
            applicationGenerator.postGeneration();
        }
        entityMappings.cleanRuntimeArtifact();

    }

}
