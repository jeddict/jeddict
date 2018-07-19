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
package io.github.jeddict.orm.generator.service.staticmetamodel;

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import io.github.jeddict.jcode.util.JavaSourceHelper;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import io.github.jeddict.orm.generator.util.ClassType;
import io.github.jeddict.orm.generator.util.ClassesRepository;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ModuleGenerator.class)
public class StaticModelModuleGeneratorImpl implements ModuleGenerator {

    private Set<StaticMetamodelGenerator> staticMetamodelClass;//Required Generation based on inheritance means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private io.github.jeddict.jcode.task.ITaskSupervisor task;
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();
    private String packageName;
    private String entityPackageName;
    private File destDir;

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings) {
        if (!entityMappings.getGenerateStaticMetamodel()) {
            return;
        }
        this.staticMetamodelClass = new HashSet<>();
        this.task = task;
        destDir = FileUtil.toFile(sourceGroup.getRootFolder());
        this.entityPackageName = entityMappings.getPackage();
        this.packageName = entityMappings.getProjectPackage() + '.' + entityMappings.getStaticMetamodelPackage();
        if(!JavaSourceHelper.isValidPackageName(packageName)){
            this.packageName = entityPackageName;
        }
        task.log(Console.wrap("Generating StaticModel Class : " , FG_DARK_RED, BOLD), true);
        try {
            for (JavaClass javaClass : entityMappings.getJavaClass()) {
                    generateStaticMetamodel((ManagedClass) javaClass);
            }
            flushStaticMetamodel();
        } catch (InvalidDataException | IOException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
    }

    private void generateStaticMetamodel(ManagedClass managedClass) throws InvalidDataException, IOException {
            StaticMetamodelGenerator staticMetamodel = new StaticMetamodelGenerator(managedClass, entityPackageName, packageName);
            ClassDefSnippet staticMetamodelClassDef = staticMetamodel.getClassDef();
            classesRepository.addWritableSnippet(ClassType.STATIC_METAMODEL_CLASS, staticMetamodelClassDef);
            task.log(managedClass.getClazz() + "_", true);
            ORMConverterUtil.writeSnippet(staticMetamodelClassDef, destDir);

            if (staticMetamodelClass.contains(staticMetamodel)) {
                staticMetamodelClass.remove(staticMetamodel);
            }
            if (managedClass.getSuperclass() != null) {
                StaticMetamodelGenerator staticMetamodelSuperClass = new StaticMetamodelGenerator(managedClass, entityPackageName, packageName);
                staticMetamodelClass.add(staticMetamodelSuperClass);
            }
    }

    private void flushStaticMetamodel() throws InvalidDataException, IOException {
        for (StaticMetamodelGenerator staticMetamodel : staticMetamodelClass) {
            flushStaticMetamodel(staticMetamodel);
        }
        staticMetamodelClass = null;
    }

    private void flushStaticMetamodel(StaticMetamodelGenerator staticMetamodel) throws InvalidDataException, IOException {
        ClassDefSnippet staticMetamodelClassDef = staticMetamodel.getClassDef();
        classesRepository.addWritableSnippet(ClassType.STATIC_METAMODEL_CLASS, staticMetamodelClassDef);
        task.log(staticMetamodel.getManagedClass().getClazz(), true);
        ORMConverterUtil.writeSnippet(staticMetamodelClassDef, destDir);

        if (staticMetamodel.getManagedClass().getSuperclass() != null) {
            StaticMetamodelGenerator staticMetamodelSuperClass = new StaticMetamodelGenerator((ManagedClass) staticMetamodel.getManagedClass().getSuperclass(), entityPackageName, packageName);
            flushStaticMetamodel(staticMetamodelSuperClass);
        }
    }

}
