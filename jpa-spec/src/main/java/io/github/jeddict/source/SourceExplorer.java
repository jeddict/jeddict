/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import static io.github.jeddict.jcode.util.ProjectHelper.getClassLoaders;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderSourceGroup;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author jGauravGupta
 */
public class SourceExplorer {

    private final EntityMappings entityMappings;

    boolean includeReference;

    private final FileObject sourceRoot;

    private final Set<String> selectedClasses;

    private final List<ClassExplorer> classes = new ArrayList<>();

    private final List<String> missingClasses = new ArrayList<>();

    public SourceExplorer(
            FileObject sourceRoot,
            EntityMappings entityMappings,
            Set<String> selectedClasses,
            boolean includeReference) {
        this.sourceRoot = sourceRoot;
        this.entityMappings = entityMappings;
        this.includeReference = includeReference;
        this.selectedClasses = selectedClasses.stream().map(JavaIdentifiers::unqualify).collect(toSet());
        configureJavaParser(sourceRoot);
    }

    private void configureJavaParser(FileObject sourceRoot) {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new JavaParserTypeSolver(FileUtil.toFile(sourceRoot)));
        combinedTypeSolver.add(new ReflectionTypeSolver());

        SourceGroup sourceGroup = getFolderSourceGroup(sourceRoot);
        Project project = FileOwnerQuery.getOwner(sourceRoot);
        List<ClassLoader> classLoaders = getClassLoaders(project, sourceGroup);
        for (ClassLoader classLoader : classLoaders) {
            combinedTypeSolver.add(new ClassloaderTypeSolver(classLoader));
        }

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }

    public Optional<CompilationUnit> createCompilationUnit(String clazzFQN) throws FileNotFoundException {
        FileObject classFile = sourceRoot.getFileObject(clazzFQN.replace(".", "/") + JAVA_EXT_SUFFIX);
        return createCompilationUnit(classFile);
    }

    public Optional<CompilationUnit> createCompilationUnit(FileObject classFile) throws FileNotFoundException {
        if (classFile != null) {
            return Optional.of(JavaParser.parse(FileUtil.toFile(classFile)));
        }
        return Optional.empty();
    }

    public Optional<ClassExplorer> createClass(String clazzFQN) throws FileNotFoundException {
        Optional<CompilationUnit> cuOpt = createCompilationUnit(clazzFQN);
        if (cuOpt.isPresent()) {
            ClassExplorer clazz = new ClassExplorer(this, cuOpt.get());
            this.addClass(clazz);
            return Optional.of(clazz);
        }
        return Optional.empty();
    }

    public EntityMappings getEntityMappings() {
        return entityMappings;
    }

    public boolean isIncludeReference() {
        return includeReference;
    }

    public boolean isSelectedClass(String simpleClassName) {
        return selectedClasses.contains(simpleClassName);
    }

    public List<ClassExplorer> getClasses() {
        return unmodifiableList(classes);
    }

    public boolean addClass(ClassExplorer clazz) {
        return classes.add(clazz);
    }

    public boolean removeClass(ClassExplorer clazz) {
        return classes.remove(clazz);
    }

    public List<String> getMissingClasses() {
        return missingClasses;
    }

    public boolean addMissingClass(String clazz) {
        return missingClasses.add(clazz);
    }

    public boolean removeMissingClass(String clazz) {
        return missingClasses.remove(clazz);
    }

    public Optional<Embeddable> findEmbeddable(ResolvedReferenceTypeDeclaration type) {
        Optional<Embeddable> embeddableOpt = entityMappings.findEmbeddable(type.getClassName());
        if (!embeddableOpt.isPresent()
                && (isIncludeReference() || isSelectedClass(type.getClassName()))) {
            try {
                embeddableOpt = createClass(type.getQualifiedName()).map(clazz -> {
                    Embeddable embeddable = new Embeddable();
                    embeddable.setClazz(clazz.getName());
                    entityMappings.addEmbeddable(embeddable);
                    embeddable.load(clazz);
                    return embeddable;
                });
            } catch (FileNotFoundException ex) {
                addMissingClass(type.getQualifiedName());
            }
        }
        return embeddableOpt;
    }

    public Optional<Entity> findEntity(ResolvedReferenceTypeDeclaration type) {
        Optional<Entity> entityOpt = entityMappings.findEntity(type.getClassName());
        if (!entityOpt.isPresent()
                && (isIncludeReference() || isSelectedClass(type.getClassName()))) {
            try {
                entityOpt = createClass(type.getQualifiedName()).map(clazz -> {
                    Entity entity = new Entity();
                    entity.setClazz(clazz.getName());
                    entityMappings.addEntity(entity);
                    entity.load(clazz);
                    return entity;
                });
            } catch (FileNotFoundException ex) {
                addMissingClass(type.getQualifiedName());
            }
        }
        return entityOpt;
    }

    public Optional<MappedSuperclass> findMappedSuperclass(ResolvedReferenceTypeDeclaration type) {
        Optional<MappedSuperclass> mappedSuperclassOpt = entityMappings.findMappedSuperclass(type.getClassName());
        if (!mappedSuperclassOpt.isPresent()
                && (isIncludeReference() || isSelectedClass(type.getClassName()))) {
            try {
                mappedSuperclassOpt = createClass(type.getQualifiedName()).map(clazz -> {
                    MappedSuperclass mappedSuperclass = new MappedSuperclass();
                    mappedSuperclass.setClazz(clazz.getName());
                    entityMappings.addMappedSuperclass(mappedSuperclass);
                    mappedSuperclass.load(clazz);
                    return mappedSuperclass;
                });
            } catch (FileNotFoundException ex) {
                addMissingClass(type.getQualifiedName());
            }
        }
        return mappedSuperclassOpt;
    }

    public Optional<BeanClass> findBeanClass(ResolvedReferenceTypeDeclaration type) {
        Optional<BeanClass> beanClassOpt = entityMappings.findBeanClass(type.getClassName());
        if (!beanClassOpt.isPresent()
                && (isIncludeReference() || isSelectedClass(type.getClassName()))) {
            try {
                beanClassOpt = createClass(type.getQualifiedName()).map(clazz -> {
                    BeanClass beanClass = new BeanClass();
                    beanClass.setClazz(clazz.getName());
                    entityMappings.addBeanClass(beanClass);
                    beanClass.load(clazz);
                    return beanClass;
                });
            } catch (FileNotFoundException ex) {
                addMissingClass(type.getQualifiedName());
            }
        }
        return beanClassOpt;
    }

}
