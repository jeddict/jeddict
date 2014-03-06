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

import org.netbeans.jpa.modeler.source.generator.adaptor.orm.ORM2Java;
import org.netbeans.jpa.modeler.source.generator.adaptor.definition.InputDefinition;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Gaurav Gupta
 */
public class JPASourceCodeGenerator implements JavaSourceCodeGenerator {

//    private FileObject targetRepository;
//    private InputDefinition inputDefinition;
    @Override
    public void generate(ITaskSupervisor task, FileObject targetRepository, InputDefinition inputDefinition) {
//        this.targetRepository = targetRepository;
//        this.inputDefinition = inputDefinition;
        ORM2Java conv = new ORM2Java();
        conv.generateSource(task, (EntityMappings) inputDefinition.getModelerFile().getDefinitionElement(), FileUtil.toFile(targetRepository));

    }

}
