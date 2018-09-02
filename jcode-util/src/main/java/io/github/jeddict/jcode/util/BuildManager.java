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
package io.github.jeddict.jcode.util;

import java.io.Reader;
import java.util.function.BiFunction;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;

public abstract class BuildManager {

    public static BuildManager getInstance(Project project) {
        if (POMManager.isMavenProject(project)) {
            return new POMManager(project);
        } else {
            throw new IllegalStateException("Project type not supported");
        }
    }
    
    public static void reload(Project project) {
        if (POMManager.isMavenProject(project)) {
            POMManager.reload(project);
        } else {
            throw new IllegalStateException("Project type not supported");
        }
    }
        
    public abstract BuildManager copy(String... inputResources);
    
    public abstract BuildManager copy(Reader... inputResources);
    
    public abstract BuildManager commit();
    
    public abstract BuildManager reload();
    
    public abstract BuildManager setSourceVersion(final String version);

    public abstract BuildManager setExtensionOverrideFilter(final BiFunction<Xpp3Dom, POMExtensibilityElement, Boolean> extensionOverrideFilter);
    
    public abstract BuildManager addDefaultProperties(java.util.Properties prop);

    public abstract BuildManager addDefaultProperties(String profile, java.util.Properties prop);

    public abstract BuildManager addProperties(java.util.Properties prop);

    public abstract BuildManager addProperties(String profile, java.util.Properties prop);


}
