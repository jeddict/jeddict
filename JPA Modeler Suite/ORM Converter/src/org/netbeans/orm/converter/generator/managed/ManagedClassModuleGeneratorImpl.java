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
package org.netbeans.orm.converter.generator.managed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.compiler.LifecycleListenerSnippet;
import org.netbeans.orm.converter.compiler.WritableSnippet;
import org.netbeans.orm.converter.generator.DefaultClassGenerator;
import org.netbeans.orm.converter.generator.EmbeddableGenerator;
import org.netbeans.orm.converter.generator.EmbeddableIdClassGenerator;
import org.netbeans.orm.converter.generator.EntityGenerator;
import org.netbeans.orm.converter.generator.LifecycleCallbackGenerator;
import org.netbeans.orm.converter.generator.SuperClassGenerator;
import org.netbeans.orm.converter.generator.identifiable.IdentifiableClassDefSnippet;
import org.netbeans.orm.converter.spec.ModuleGenerator;
import org.netbeans.orm.converter.util.ClassType;
import org.netbeans.orm.converter.util.ClassesRepository;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

@org.openide.util.lookup.ServiceProvider(service = ModuleGenerator.class)
public class ManagedClassModuleGeneratorImpl implements ModuleGenerator {

    private EntityMappings parsedEntityMappings;//Required Generation based on inheritence means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private String packageName;
    private File destDir;
    private ITaskSupervisor task;
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings parsedEntityMappings) {
        try {
            this.parsedEntityMappings = parsedEntityMappings;
            this.task = task;
            destDir = FileUtil.toFile(sourceGroup.getRootFolder());
            this.packageName = parsedEntityMappings.getPackage();

            generateMappedSuperClasses();
            generateEntityClasses();
            generateEmbededClasses();
            for (DefaultClass defaultClass : parsedEntityMappings.getDefaultClass()) {
                if (defaultClass.isEmbeddable()) {
                    generateEmbededIdClasses(defaultClass);
                } else {
                    generateIdClasses(defaultClass);
                }
            }
            generateLifeCycleClasses();
        } catch (InvalidDataException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void generateEmbededClasses() throws InvalidDataException, IOException {
        List<Embeddable> parsedEmbeddables = parsedEntityMappings.getEmbeddable();
        for (Embeddable parsedEmbeddable : parsedEmbeddables) {
            task.log("Generating Embeddable Class : " + parsedEmbeddable.getClazz(), true);
            ManagedClassDefSnippet classDef = new EmbeddableGenerator(parsedEmbeddable, packageName).getClassDef();
            classDef.setJaxbSupport(parsedEntityMappings.getJaxbSupport()); 
            
            classesRepository.addWritableSnippet(ClassType.EMBEDED_CLASS, classDef);
            ORMConverterUtil.writeSnippet(classDef, destDir);
        }
    }

    private void generateEntityClasses() throws InvalidDataException, IOException {
        List<Entity> parsedEntities = parsedEntityMappings.getEntity();
        for (Entity parsedEntity : parsedEntities) {
            task.log("Generating Entity Class : " + parsedEntity.getClazz(), true);
            ManagedClassDefSnippet classDef = new EntityGenerator(parsedEntity, packageName).getClassDef();
            classDef.setJaxbSupport(parsedEntityMappings.getJaxbSupport()); 
            
            classesRepository.addWritableSnippet(ClassType.ENTITY_CLASS, classDef);
            ORMConverterUtil.writeSnippet(classDef, destDir);
        }
    }

    private void generateMappedSuperClasses() throws InvalidDataException, IOException {
        List<MappedSuperclass> parsedMappedSuperclasses = parsedEntityMappings.getMappedSuperclass();
        for (MappedSuperclass parsedMappedSuperclass : parsedMappedSuperclasses) {
            task.log("Generating MappedSuperclass Class : " + parsedMappedSuperclass.getClazz(), true);
            ManagedClassDefSnippet classDef = new SuperClassGenerator(parsedMappedSuperclass, packageName).getClassDef();
            classDef.setJaxbSupport(parsedEntityMappings.getJaxbSupport()); 
            
            classesRepository.addWritableSnippet(ClassType.SUPER_CLASS, classDef);
            ORMConverterUtil.writeSnippet(classDef, destDir);
        }
    }

    private void generateLifeCycleClasses() throws InvalidDataException, IOException {
        List<ClassDefSnippet> classDefs = getPUXMLEntries();
        //Generate lifecycle events processors
        LifecycleCallbackGenerator callbackGenerator = new LifecycleCallbackGenerator(parsedEntityMappings, classDefs, packageName);
        Collection<LifecycleListenerSnippet> lifecycleListeners = callbackGenerator.getLifecycleListeners();
        for (LifecycleListenerSnippet lifecycleListener : lifecycleListeners) {
            classesRepository.addWritableSnippet(ClassType.LISTENER_CLASS, lifecycleListener);
            ORMConverterUtil.writeSnippet(lifecycleListener, destDir);
        }
    }

    private void generateEmbededIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        task.log("Generating EmbeddedId Class : " + defaultClass.getClazz(), true);
        ClassDefSnippet classDef = new EmbeddableIdClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.EMBEDED_CLASS, classDef);
        ORMConverterUtil.writeSnippet(classDef, destDir);
    }

    private void generateIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        task.log("Generating IdClass Class : " + defaultClass.getClazz(), true);
        ClassDefSnippet classDef = new DefaultClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.SERIALIZER_CLASS, classDef);
        ORMConverterUtil.writeSnippet(classDef, destDir);
    }

    private List<ClassDefSnippet> getPUXMLEntries() {
        List<WritableSnippet> entitySnippets = classesRepository.getWritableSnippets(ClassType.ENTITY_CLASS);
        List<ClassDefSnippet> classDefs = new ArrayList<ClassDefSnippet>();
        for (WritableSnippet writableSnippet : entitySnippets) {
            classDefs.add((ClassDefSnippet) writableSnippet);
        }
        return classDefs;
    }

}
