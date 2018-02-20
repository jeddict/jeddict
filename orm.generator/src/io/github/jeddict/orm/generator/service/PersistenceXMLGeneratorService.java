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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.spec.WritableSnippet;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import io.github.jeddict.orm.generator.util.ClassType;
import io.github.jeddict.orm.generator.util.ClassesRepository;

@org.openide.util.lookup.ServiceProvider(service = ModuleGenerator.class)
public class PersistenceXMLGeneratorService implements ModuleGenerator {

    private EntityMappings entityMappings;//Required Generation based on inheritance means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings) {
        this.entityMappings = entityMappings;
        generatePersistenceXML(project, sourceGroup);
    }

    private void generatePersistenceXML(Project project, SourceGroup sourceGroup) {
        List<ClassDefSnippet> classDefs = getPUXMLEntries();
        //Generate persistence.xml
        PersistenceXMLGenerator persistenceXMLGenerator = new PersistenceXMLGenerator(entityMappings, classDefs);
        persistenceXMLGenerator.generatePersistenceXML(project, sourceGroup);
    }

    private List<ClassDefSnippet> getPUXMLEntries() {
        List<WritableSnippet> entitySnippets = classesRepository.getWritableSnippets(ClassType.ENTITY_CLASS);
        List<ClassDefSnippet> classDefs = new ArrayList<>();
        entitySnippets.forEach(writableSnippet -> classDefs.add((ClassDefSnippet) writableSnippet));
        return classDefs;
    }

}
