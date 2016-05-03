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
package org.netbeans.jpa.modeler.source.generator.task;

import org.apache.commons.lang.StringUtils;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.jcode.generator.JEEApplicationGenerator;
import org.netbeans.jpa.modeler.source.generator.adaptor.ISourceCodeGenerator;
import org.netbeans.jpa.modeler.source.generator.adaptor.ISourceCodeGeneratorFactory;
import org.netbeans.jpa.modeler.source.generator.adaptor.SourceCodeGeneratorType;
import org.netbeans.jpa.modeler.source.generator.adaptor.definition.InputDefinition;
import org.netbeans.jpa.modeler.source.generator.adaptor.definition.orm.ORMInputDefiniton;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.jcode.task.AbstractNBTask;
import org.openide.util.Lookup;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jcode.task.progress.ProgressConsoleHandler;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.openide.util.NbBundle;

public class SourceCodeGeneratorTask extends AbstractNBTask {

    private final ModelerFile modelerFile;
    private final ApplicationConfigData appicationConfigData;

    private final static int SUBTASK_TOT = 1;

    public SourceCodeGeneratorTask(ModelerFile modelerFile, ApplicationConfigData appicationConfigData) {
        this.modelerFile = modelerFile;
        this.appicationConfigData = appicationConfigData;
    }

    @Override
    protected void initTask() {
        setLogLevel(TERSE);
        setTaskName(getBundleMessage("LBL_GenerateCodeDialogTitle")); // NOI18N

        setDisplayOutput(true);

        progressContribs = new ProgressContributor[SUBTASK_TOT];
        int i = 0;

        progressContribs[i] = AggregateProgressFactory
                .createProgressContributor(getBundleMessage("MSG_Processing")); // NOI18N
    }

    @Override
    protected void begin() {
        // Issue Fix #5847 Start
        if (!modelerFile.getModelerPanelTopComponent().isPersistenceState()) {
            this.log("Saving " + modelerFile.getName() + " File..\n");
//            modelerFile.save();//asynchronous : causes to generate code before saving
            modelerFile.getModelerUtil().saveModelerFile(modelerFile);//synchronous
            modelerFile.getModelerScene().getModelerPanelTopComponent().changePersistenceState(true);//remove * from header
        } else {
            JPAModelerUtil.preExecution(modelerFile);
        }
        // Issue Fix #5847 End
        try {
            exportCode();
        } catch (Throwable t) {
            modelerFile.handleException(t);
        }
    }

    @Override
    protected void finish() {
    }

    /**
     * @param elements The collection of elements to generate for
     *
     */
    private void exportCode() {
        ISourceCodeGeneratorFactory sourceGeneratorFactory = Lookup.getDefault().lookup(ISourceCodeGeneratorFactory.class);
        ISourceCodeGenerator sourceGenerator = sourceGeneratorFactory.getSourceGenerator(SourceCodeGeneratorType.JPA);
        InputDefinition definiton = new ORMInputDefiniton();
        definiton.setModelerFile(modelerFile);
        sourceGenerator.generate(this, appicationConfigData.getProject(), appicationConfigData.getSourceGroup(), definiton);

        if (appicationConfigData.getBussinesLayerConfig() != null) {
            EntityMappings entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
            for (Entity entity : entityMappings.getEntity()) {
                String entiyFQN = StringUtils.isNotBlank(entityMappings.getPackage()) ? entityMappings.getPackage() + '.' + entity.getClazz() : entity.getClazz();
                appicationConfigData.putEntity(entiyFQN, entity.getFileObject());
            }
            ProgressHandler handler = new ProgressConsoleHandler(this);
            JEEApplicationGenerator.generate(handler, appicationConfigData);
        }
    }

    private static String getBundleMessage(String key) {
        return NbBundle.getMessage(SourceCodeGeneratorTask.class, key);
    }

}
