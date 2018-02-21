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
package io.github.jeddict.orm.generator;

import io.github.jeddict.jcode.stack.config.data.ApplicationConfigData;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.compiler.CompilerConfig;
import io.github.jeddict.orm.generator.compiler.CompilerConfigManager;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import io.github.jeddict.orm.generator.util.ClassesRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public class JPASourceCodeGenerator implements ISourceCodeGenerator {

    @Override
    public void generate(ITaskSupervisor task, ApplicationConfigData applicationConfig) {
        EntityMappings entityMappings = applicationConfig.getEntityMappings();
        CompilerConfig compilerConfig = new CompilerConfig(entityMappings.getPackage());
        CompilerConfigManager.getInstance().initialize(compilerConfig);
        ClassesRepository.getInstance().clear();
        Lookup.getDefault()
                .lookupAll(ModuleGenerator.class)
                .forEach((moduleGenerator) -> {
                    moduleGenerator.generate(task, 
                            applicationConfig.getTargetProject(), 
                            applicationConfig.getTargetSourceGroup(), 
                            entityMappings);
                });
        
    }

}
