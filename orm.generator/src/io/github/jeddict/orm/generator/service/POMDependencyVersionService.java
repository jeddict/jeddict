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
package io.github.jeddict.orm.generator.service;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jcode.util.POMManager;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ModuleGenerator.class)
public class POMDependencyVersionService implements ModuleGenerator {

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings) {
        if (POMManager.isMavenProject(project)) {
            POMManager manager = new POMManager(project);
            updateJavaEEDependency(manager);
            updatePlugin(manager);
            manager.commit();
            manager.reload();
        }
    }

    private void updatePlugin(POMManager manager) {
        String groupId = "org.apache.maven.plugins";
        String artifactId = "maven-compiler-plugin";
        Map<String, String> configs = new HashMap<>();
        configs.put("source", "1.8");
        configs.put("target", "1.8");
        manager.setPluginConfiguration(groupId, artifactId, configs);
    }

    private void updateJavaEEDependency(POMManager manager) {
        String groupId = "javax";
        String artifactId = "javaee-web-api";
        String finalVersion = "8.0";
        String version = manager.getDependencyVersion(groupId, artifactId);
        if (version == null) {
            artifactId = "javaee-api";
            version = manager.getDependencyVersion(groupId, artifactId);
        }
        if (version != null && !finalVersion.equals(version)) {
            manager.setDependencyVersion(groupId, artifactId, finalVersion);
        }

    }

}
