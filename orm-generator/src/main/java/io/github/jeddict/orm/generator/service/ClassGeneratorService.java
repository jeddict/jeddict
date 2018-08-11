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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.FG_RED;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import io.github.jeddict.jpa.spec.DefaultClass;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.sync.JavaClassSyncHandler;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.compiler.def.ManagedClassDefSnippet;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import io.github.jeddict.orm.generator.spec.WritableSnippet;
import io.github.jeddict.orm.generator.util.ClassType;
import io.github.jeddict.orm.generator.util.ClassesRepository;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import static io.github.jeddict.settings.generate.GenerateSettings.isSyncExistingSourceCode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ModuleGenerator.class)
public class ClassGeneratorService implements ModuleGenerator {

    private EntityMappings entityMappings;//Required Generation based on inheritance means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private String packageName;
    private SourceGroup sourceGroup;
    private File destDir;
    private ITaskSupervisor task;
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings) {
        try {
            this.entityMappings = entityMappings;
            this.task = task;
            this.sourceGroup = sourceGroup;
            destDir = FileUtil.toFile(sourceGroup.getRootFolder());
            this.packageName = entityMappings.getPackage();

            generateMappedSuperClasses();
            generateEntityClasses();
            generateEmbededClasses();
            generateDefaultClasses();
            generateBeanClasses();
        } catch (InvalidDataException | IOException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
    }

    private void generateBeanClasses() throws InvalidDataException, IOException {
        List<BeanClass> parsedBeanClasses = entityMappings.getBeanClass()
                .stream()
                .filter(e -> e.getGenerateSourceCode())
                .collect(toList());
        if(!parsedBeanClasses.isEmpty()){
            task.log(Console.wrap("Generating Class : " , FG_DARK_RED, BOLD), true);
        }
        for (BeanClass parsedBeanClasse : parsedBeanClasses) {
            task.log(parsedBeanClasse.getClazz(), true);
            ClassDefSnippet classDef = new BeanClassGenerator(parsedBeanClasse, packageName).getClassDef();
            classDef.setJaxbSupport(entityMappings.getJaxbSupport());
            classesRepository.addWritableSnippet(ClassType.DEFAULT_CLASS, classDef);
            ORMConverterUtil.writeSnippet(classDef, destDir);
        }
    }
    
    private void generateDefaultClasses() throws InvalidDataException, IOException {
        List<DefaultClass> parsedDefaultClasses = entityMappings.getDefaultClass()
                .stream()
                .filter(e -> e.getGenerateSourceCode())
                .collect(toList());
        if(!parsedDefaultClasses.isEmpty()){
            task.log(Console.wrap("Generating IdClass/PrimaryKey Class : " , FG_DARK_RED, BOLD), true);
        }
        for (DefaultClass parsedDefaultClasse : parsedDefaultClasses) {
            task.log(parsedDefaultClasse.getClazz(), true);
            if (parsedDefaultClasse.isEmbeddable()) {
                generateEmbededIdClasses(parsedDefaultClasse);
            } else {
                generateIdClasses(parsedDefaultClasse);
            }
        }
    }
    
    private void generateEmbededClasses() throws InvalidDataException, IOException {
        List<Embeddable> parsedEmbeddables = entityMappings.getEmbeddable()
                .stream()
                .filter(e -> e.getGenerateSourceCode())
                .collect(toList());
        if(!parsedEmbeddables.isEmpty()){
            task.log(Console.wrap("Generating Embeddable Class : " , FG_DARK_RED, BOLD), true);
        }
        for (Embeddable parsedEmbeddable : parsedEmbeddables) {
            task.log(parsedEmbeddable.getClazz(), true);
            loadExistingSnippet(parsedEmbeddable);
            ManagedClassDefSnippet classDef = new EmbeddableGenerator(parsedEmbeddable, packageName).getClassDef();
            classDef.setJaxbSupport(entityMappings.getJaxbSupport());

            classesRepository.addWritableSnippet(ClassType.EMBEDED_CLASS, classDef);
            parsedEmbeddable.setFileObject(ORMConverterUtil.writeSnippet(classDef, destDir));
        }
    }

    private void generateEntityClasses() throws InvalidDataException, IOException {
        List<Entity> parsedEntities = entityMappings.getEntity()
                .stream()
                .filter(e -> e.getGenerateSourceCode())
                .collect(toList());
        if(!parsedEntities.isEmpty()){
            task.log(Console.wrap("Generating Entity Class : " , FG_DARK_RED, BOLD), true);
        }
        for (Entity parsedEntity : parsedEntities) {
            task.log(parsedEntity.getClazz(), true);
            loadExistingSnippet(parsedEntity);
            ManagedClassDefSnippet classDef = new EntityGenerator(parsedEntity, packageName).getClassDef();
            classDef.setJaxbSupport(entityMappings.getJaxbSupport());

            classesRepository.addWritableSnippet(ClassType.ENTITY_CLASS, classDef);
            parsedEntity.setFileObject(ORMConverterUtil.writeSnippet(classDef, destDir));
        }
    }

    private void generateMappedSuperClasses() throws InvalidDataException, IOException {
        List<MappedSuperclass> parsedMappedSuperclasses = entityMappings.getMappedSuperclass()
                .stream()
                .filter(e -> e.getGenerateSourceCode())
                .collect(toList());
        if(!parsedMappedSuperclasses.isEmpty()){
        task.log(Console.wrap("Generating MappedSuperclass Class : " , FG_DARK_RED, BOLD), true);
        }
        for (MappedSuperclass parsedMappedSuperclass : parsedMappedSuperclasses) {
            task.log(parsedMappedSuperclass.getClazz(), true);
            loadExistingSnippet(parsedMappedSuperclass);
            ManagedClassDefSnippet classDef = new MappedSuperClassGenerator(parsedMappedSuperclass, packageName).getClassDef();
            classDef.setJaxbSupport(entityMappings.getJaxbSupport());

            classesRepository.addWritableSnippet(ClassType.SUPER_CLASS, classDef);
            parsedMappedSuperclass.setFileObject(ORMConverterUtil.writeSnippet(classDef, destDir));
        }
    }

    private void generateEmbededIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        ClassDefSnippet classDef = new EmbeddableIdClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.EMBEDED_CLASS, classDef);
        ORMConverterUtil.writeSnippet(classDef, destDir);
    }

    private void generateIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        ClassDefSnippet classDef = new DefaultClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.DEFAULT_CLASS, classDef);
        ORMConverterUtil.writeSnippet(classDef, destDir);
    }

    private List<ClassDefSnippet> getPUXMLEntries() {
        List<WritableSnippet> entitySnippets = classesRepository.getWritableSnippets(ClassType.ENTITY_CLASS);
        List<ClassDefSnippet> classDefs = new ArrayList<>();
        for (WritableSnippet writableSnippet : entitySnippets) {
            classDefs.add((ClassDefSnippet) writableSnippet);
        }
        return classDefs;
    }

    static {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }

    private void loadExistingSnippet(JavaClass javaClass) {
        if (!isSyncExistingSourceCode()) {
            return;
        }
        String pathTemplate = javaClass.getRootPackage().replace(".", "/") + "/%s" + JAVA_EXT_SUFFIX;
        FileObject root = sourceGroup.getRootFolder();
        FileObject existingFile = null;
        if (javaClass.getPreviousClass() != null) {
            existingFile = root.getFileObject(String.format(pathTemplate, javaClass.getPreviousClass()));
        }
        if (existingFile == null) {
            existingFile = root.getFileObject(String.format(pathTemplate, javaClass.getClazz()));
        }
        if (existingFile != null) {
            try {
                CompilationUnit existingSource = JavaParser.parse(FileUtil.toFile(existingFile));
                JavaClassSyncHandler
                        .getInstance(javaClass)
                        .syncExistingSnippet(existingSource);
            } catch (FileNotFoundException ex) {
            } catch (ParseProblemException ex) {
                task.log(Console.wrap("Unable to sync with exising class : " + javaClass.getName(), FG_DARK_RED), true);
                task.log(Console.wrap("-----------------------------------", FG_DARK_RED), true);
                for (Problem prob : ex.getProblems()) {
                    task.log(Console.wrap("Problem : " + prob.getMessage(), FG_RED), true);
                }
                task.log(Console.wrap("-----------------------------------", FG_DARK_RED), true);
            }
        }
    }

}
