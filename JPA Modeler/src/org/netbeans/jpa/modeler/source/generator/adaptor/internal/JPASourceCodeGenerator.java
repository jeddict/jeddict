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
package org.netbeans.jpa.modeler.source.generator.adaptor.internal;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jpa.modeler.source.generator.adaptor.definition.InputDefinition;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.netbeans.orm.converter.compiler.CompilerConfig;
import org.netbeans.orm.converter.compiler.CompilerConfigManager;
import org.netbeans.orm.converter.spec.ModuleGenerator;
import org.netbeans.orm.converter.util.ClassesRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public class JPASourceCodeGenerator implements JavaSourceCodeGenerator {

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, InputDefinition inputDefinition) {
        EntityMappings parsedEntityMappings = (EntityMappings) inputDefinition.getModelerFile().getDefinitionElement();
        CompilerConfig compilerConfig = new CompilerConfig(parsedEntityMappings.getPackage());
        CompilerConfigManager.getInstance().initialize(compilerConfig);
        ClassesRepository.getInstance().clear();
        for (ModuleGenerator moduleGenerator : Lookup.getDefault().lookupAll(ModuleGenerator.class)) {
            moduleGenerator.generate(task, project, sourceGroup, parsedEntityMappings);
        }
    }

}
