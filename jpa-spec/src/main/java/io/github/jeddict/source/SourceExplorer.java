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
package io.github.jeddict.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
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

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }

    private CompilationUnit createCompilationUnit(String clazzFQN) throws FileNotFoundException {
        FileObject classFile = sourceRoot.getFileObject(clazzFQN.replace(".", "/") + JAVA_EXT_SUFFIX);
        return JavaParser.parse(FileUtil.toFile(classFile));
    }

    public ClassExplorer createClass(String clazzFQN) throws FileNotFoundException {
        CompilationUnit cu = createCompilationUnit(clazzFQN);
        ClassExplorer clazz = new ClassExplorer(this, cu);
        this.addClass(clazz);
        return clazz;
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


}
