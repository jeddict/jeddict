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
package io.github.jeddict.fullstack;

import io.github.jeddict.generator.repository.SampleRepositoryConfigData;
import io.github.jeddict.generator.repository.SampleRepositoryGenerator;
import io.github.jeddict.jcode.TechContext;
import io.github.jeddict.test.FullStackApplicationTest;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import org.junit.jupiter.api.Test;
import org.netbeans.api.project.Project;

/**
 *
 * @author jGauravGupta
 */
public class RepositoryApplicationTest extends FullStackApplicationTest {

    @Test
    void test() throws Exception {
        Project project = generateMonolith("repository-sample-app", "default-monolith.jpa");
        fireMavenBuild(project, singletonList("install"), emptyList(), null);
    }

    @Override
    protected TechContext getRepositoryContext() {
        SampleRepositoryConfigData repositoryData = new SampleRepositoryConfigData();
        repositoryData.setPrefixName("");
        repositoryData.setPackage("repository");
        TechContext techContext = new TechContext(SampleRepositoryGenerator.class);
        techContext.setConfigData(repositoryData);
        return techContext;
    }

    @Override
    protected TechContext getControllerContext() {
        return null;
    }

    @Override
    protected TechContext getViewerContext() {
        return null;
    }
}
