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
package io.github.jeddict.test;

import io.github.jeddict.test.mock.MockActiveDocumentProvider;
import io.github.jeddict.test.mock.MockEnvironmentFactory;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.TechContext;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.FG_DARK_BLUE;
import io.github.jeddict.jcode.util.POMManager;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderSourceGroup;
import io.github.jeddict.jpa.modeler.source.generator.task.SourceCodeGeneratorTask;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.ProjectType;
import static io.github.jeddict.jpa.spec.extend.ProjectType.GATEWAY;
import static io.github.jeddict.jpa.spec.extend.ProjectType.MICROSERVICE;
import static io.github.jeddict.jpa.spec.extend.ProjectType.MONOLITH;
import io.github.jeddict.test.mock.MockTaskSupervisor;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.netbeans.api.project.Project;
import org.netbeans.junit.MockServices;
import org.openide.filesystems.FileObject;

/**
 *
 * @author jGauravGupta
 */
public abstract class FullStackApplicationTest extends BaseModelTest {

    protected Project generateMonolith(String applicationName, String modelerFile) {
        return runGenerator(applicationName, modelerFile, MONOLITH, null);
    }

    protected Project generateMicroService(
            String applicationName,
            String modelerFile,
            Project gatewayProject) {
        return runGenerator(applicationName, modelerFile, MICROSERVICE, gatewayProject);
    }

    protected Project generateGateway(String applicationName, String modelerFile) {
        return runGenerator(applicationName, modelerFile, GATEWAY, null);
    }

    private Project runGenerator(
            String applicationName,
            String modelerFile,
            ProjectType projectType,
            Project gatewayProject) {

        Project project = null;
        try {
//            MockServices.setServices(MockEnvironmentFactory.class, MockActiveDocumentProvider.class);

            project = createProject(applicationName);
            EntityMappings entityMappings = loadEntityMappings(modelerFile);
            assertNotNull(entityMappings);

            FileObject source = getJavaSourceGroup(project);
            assertNotNull(source);
            printProjectPath(project);

            assertNotNull(entityMappings.getProjectPackage());
            assertNotNull(entityMappings.getEntityPackage());

            if (gatewayProject == null) {
                gatewayProject = project;
            }
            FileObject gatewaySource = getJavaSourceGroup(gatewayProject);
            assertNotNull(gatewaySource);

            ApplicationConfigData configData = new ApplicationConfigData();
            configData.setEntityMappings(entityMappings);
            configData.setProjectType(projectType);
            configData.setCompleteApplication(true);

            configData.setTargetProject(project);
            configData.setTargetSourceGroup(getFolderSourceGroup(source));
            configData.setTargetPackage(entityMappings.getProjectPackage());
            configData.setTargetArtifactId(new POMManager(project, true).getArtifactId());

            configData.setGatewayProject(gatewayProject);
            configData.setGatewaySourceGroup(getFolderSourceGroup(gatewaySource));
            configData.setGatewayPackage(entityMappings.getProjectPackage());
            configData.setTargetArtifactId(new POMManager(gatewayProject, true).getArtifactId());

            configData.setRepositoryTechContext(getRepositoryContext());
            configData.setControllerTechContext(getControllerContext());
            configData.setViewerTechContext(getViewerContext());

            SourceCodeGeneratorTask.exportCode(configData, entityMappings, new MockTaskSupervisor());

            printProjectPath(project);
        } catch (Throwable ex) {
            ex.printStackTrace();
            fail(ex);
        }
        return project;
    }

    private void printProjectPath(Project project) {
        System.out.println(Console.wrap("Project Path :" + project.getProjectDirectory().getPath(), FG_DARK_BLUE));
    }

    protected abstract TechContext getRepositoryContext();

    protected abstract TechContext getControllerContext();

    protected abstract TechContext getViewerContext();


}
