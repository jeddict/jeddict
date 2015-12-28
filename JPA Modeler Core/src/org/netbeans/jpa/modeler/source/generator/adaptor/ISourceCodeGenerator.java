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
package org.netbeans.jpa.modeler.source.generator.adaptor;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jpa.modeler.source.generator.adaptor.definition.InputDefinition;
import org.netbeans.modeler.task.ITaskSupervisor;

/**
 *
 * @author Gaurav Gupta
 */
public interface ISourceCodeGenerator {

    void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, InputDefinition inputDefinition);

}
