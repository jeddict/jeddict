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
package org.netbeans.orm.converter.generator.persistencexml;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.WritableSnippet;
import org.netbeans.orm.converter.generator.PersistenceXMLGenerator;
import org.netbeans.orm.converter.spec.ModuleGenerator;
import org.netbeans.orm.converter.util.ClassType;
import org.netbeans.orm.converter.util.ClassesRepository;

@org.openide.util.lookup.ServiceProvider(service = ModuleGenerator.class)
public class PersistenceXMLGeneratorImpl implements ModuleGenerator {

    private EntityMappings parsedEntityMappings;//Required Generation based on inheritence means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings parsedEntityMappings) {
        this.parsedEntityMappings = parsedEntityMappings;
        generatePersistenceXML(project, sourceGroup);
    }

    private void generatePersistenceXML(Project project, SourceGroup sourceGroup) {
        List<ClassDefSnippet> classDefs = getPUXMLEntries();
        //Generate persistence.xml
        PersistenceXMLGenerator persistenceXMLGenerator = new PersistenceXMLGenerator(classDefs);
        persistenceXMLGenerator.setPUName(parsedEntityMappings.getPersistenceUnitName());
        persistenceXMLGenerator.generatePersistenceXML(project, sourceGroup);
    }

    private List<ClassDefSnippet> getPUXMLEntries() {
//        List<WritableSnippet> results = new ArrayList<WritableSnippet>();
        List<WritableSnippet> entitySnippets = classesRepository.getWritableSnippets(ClassType.ENTITY_CLASS);

//        List<WritableSnippet> superClassSnippets
//                = classesRepository.getWritableSnippets(ClassType.SUPER_CLASS);
//
//        List<WritableSnippet> embededSnippets
//                = classesRepository.getWritableSnippets(ClassType.EMBEDED_CLASS);
//        results.addAll(entitySnippets);
//        results.addAll(superClassSnippets);
//        results.addAll(embededSnippets);
        List<ClassDefSnippet> classDefs = new ArrayList<ClassDefSnippet>();
        for (WritableSnippet writableSnippet : entitySnippets) {
            classDefs.add((ClassDefSnippet) writableSnippet);
        }
        return classDefs;
    }


}
