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
package io.github.jeddict.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_BLUE;
import static io.github.jeddict.jcode.console.Console.FG_DARK_CYAN;
import static io.github.jeddict.jcode.console.Console.FG_DARK_GREEN;
import static io.github.jeddict.jcode.console.Console.FG_DARK_MAGENTA;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.FG_DARK_YELLOW;
import static io.github.jeddict.jcode.console.Console.FG_RED;
import static io.github.jeddict.jcode.console.Console.wrap;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jcode.util.FileUtil.readString;
import static io.github.jeddict.jcode.util.ProjectHelper.getFileObject;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getEntityMapping;
import io.github.jeddict.jpa.spec.DefaultClass;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.sync.JavaClassSyncHandler;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.service.BeanClassGenerator;
import io.github.jeddict.orm.generator.service.ClassGenerator;
import io.github.jeddict.orm.generator.service.DefaultClassGenerator;
import io.github.jeddict.orm.generator.service.EmbeddableGenerator;
import io.github.jeddict.orm.generator.service.EmbeddableIdClassGenerator;
import io.github.jeddict.orm.generator.service.EntityGenerator;
import io.github.jeddict.orm.generator.service.MappedSuperClassGenerator;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import io.github.jeddict.reveng.klass.ClassWizardDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXBException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.ProgressReporter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;


/**
 * Test flow :
 * <br>
 * - Parse the model
 * <br>
 * - Generates the source code
 * <br>
 * - Validate using java parser for compilation issue
 * <br>
 * - Compare with the existing source
 *
 * @author jGauravGupta
 */
public class BaseModelTest {

    protected void testModelerFile(String fileName) throws Exception {
        EntityMappings entityMappings = loadEntityMappings(fileName);
        assertNotNull(entityMappings);

        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        generateClasses(entityMappings, fileName);
    }

    protected void generateClasses(EntityMappings entityMappings, FileObject sourceGroup) throws Exception {
        File sourceRoot = FileUtil.toFile(sourceGroup);
        String packageName = entityMappings.getPackage();
        for (Entity clazz : entityMappings.getEntity()) {
            generateClass(new EntityGenerator(clazz, packageName), entityMappings, sourceRoot);
        }
        for (MappedSuperclass clazz : entityMappings.getMappedSuperclass()) {
            generateClass(new MappedSuperClassGenerator(clazz, packageName), entityMappings, sourceRoot);
        }
        for (Embeddable clazz : entityMappings.getEmbeddable()) {
            generateClass(new EmbeddableGenerator(clazz, packageName), entityMappings, sourceRoot);
        }
        for (DefaultClass clazz : entityMappings.getDefaultClass()) {
            if (clazz.isEmbeddable()) {
                generateClass(new EmbeddableIdClassGenerator(clazz, packageName), entityMappings, sourceRoot);
            } else {
                generateClass(new DefaultClassGenerator(clazz, packageName), entityMappings, sourceRoot);
            }
        }
        for (BeanClass clazz : entityMappings.getBeanClass()) {
            generateClass(new BeanClassGenerator(clazz, packageName), entityMappings, sourceRoot);
        }
    }

    private void generateClass(ClassGenerator generator, EntityMappings entityMappings, File sourceRoot) throws Exception {
        ClassDefSnippet classDef = generator.getClassDef();
        classDef.setJaxbSupport(entityMappings.getJaxbSupport());
        classDef.getSnippet();
        ORMConverterUtil.writeSnippet(classDef, sourceRoot);
    }


    protected void generateClasses(EntityMappings entityMappings, String fileName) throws Exception {
        String packageName = this.getClass().getPackage().getName();
        for (Entity clazz : entityMappings.getEntity()) {
            testClass(clazz, new EntityGenerator(clazz, packageName), entityMappings, fileName);
        }
        for (MappedSuperclass clazz : entityMappings.getMappedSuperclass()) {
            testClass(clazz, new MappedSuperClassGenerator(clazz, packageName), entityMappings, fileName);
        }
        for (Embeddable clazz : entityMappings.getEmbeddable()) {
            testClass(clazz, new EmbeddableGenerator(clazz, packageName), entityMappings, fileName);
        }
        for (DefaultClass clazz : entityMappings.getDefaultClass()) {
            if (clazz.isEmbeddable()) {
                testClass(clazz, new EmbeddableIdClassGenerator(clazz, packageName), entityMappings, fileName);
            } else {
                testClass(clazz, new DefaultClassGenerator(clazz, packageName), entityMappings, fileName);
            }
        }
        for (BeanClass clazz : entityMappings.getBeanClass()) {
            testClass(clazz, new BeanClassGenerator(clazz, packageName), entityMappings, fileName);
        }
    }

    private void testClass(JavaClass javaClass, ClassGenerator generator, EntityMappings entityMappings, String fileName) throws Exception {
        PrettyPrinter prettyPrinter = new PrettyPrinter();

        String existingSource;
        CompilationUnit existingUnit;

        String newSource = null;
        CompilationUnit newUnit;

        try (InputStream existingSourceStream
                = this.getClass().getResourceAsStream(javaClass.getClazz() + JAVA_EXT_SUFFIX)) {
            existingSource = readString(existingSourceStream);
            existingUnit = JavaParser.parse(existingSource);
            assertNotNull(existingUnit);
            existingSource = prettyPrinter.print(existingUnit);
        }

        if (fileName == null) {
            JavaClassSyncHandler
                    .getInstance(javaClass)
                    .syncExistingSnippet(existingUnit);
            fileName = wrap("Reverse Engineering", FG_DARK_MAGENTA);
        } else {
            fileName = wrap(fileName, FG_DARK_BLUE);
        }

        try {
            ClassDefSnippet classDef = generator.getClassDef();
            classDef.setJaxbSupport(entityMappings.getJaxbSupport());
            newSource = classDef.getSnippet();
            assertNotNull(newSource);
            newUnit = JavaParser.parse(newSource);
            assertNotNull(newUnit);
            newSource = prettyPrinter.print(newUnit);

//            System.out.println("newSource " + newSource);
            try (BufferedReader existingSourceReader = new BufferedReader(new StringReader(existingSource));
                    BufferedReader newSourceReader = new BufferedReader(new StringReader(newSource));) {

                String existingSourceLine;
                String newSourceLine;
                int lineNumber = 0;
                while ((existingSourceLine = existingSourceReader.readLine()) != null && (newSourceLine = newSourceReader.readLine()) != null) {
//                    System.out.println("new " + newSourceLine);
//                    System.out.println("ext " + existingSourceLine);
                    ++lineNumber;
                    assertEquals(existingSourceLine, newSourceLine,
                            '\n'
                            + wrap("Failed : " + javaClass.getFQN() + " [" + fileName + "]", FG_DARK_RED, BOLD)
                            + '\n'
                            + wrap("Line number : " + lineNumber, FG_RED, BOLD)
                            + '\n'
                            + wrap("existingSourceLine : " + existingSourceLine, FG_DARK_RED)
                            + '\n'
                            + wrap("newSourceLine : " + newSourceLine, FG_DARK_RED)
                            + '\n'
                    );
                }
            }

            System.out.println(wrap("Passed : ", FG_DARK_GREEN, BOLD)
                    + wrap(javaClass.getFQN(), FG_DARK_CYAN)
                    + " [" + fileName + "]"
            );
        } catch (ParseProblemException ex) {
            fail(wrap(
                    "Compilation Failed : "
                    + javaClass.getFQN() + " [" + fileName + "]"
                    + '\n'
                    + "---------------------"
                    + '\n'
                    + newSource
                    + '\n'
                    + "---------------------"
                    + ex.getMessage()
                    + "---------------------",
                    FG_RED
            ));
        }
    }

    protected void reverseEngineeringTest(String... classes) {
        try {
            ProjectBuilder projectBuilder = new ProjectBuilder("reverse-engineering-test");
            FileObject src = projectBuilder.getSrc();
            EntityMappings entityMappings = createEntityMappings();

            String packageName = this.getClass().getPackage().getName();
            Set<String> classFqns = new HashSet<>();
            for (String clazz : classes) {
                FileObject classPackage = getFileObject(src, packageName, "\\.");

                classFqns.add(packageName + '.' + clazz);
                File classFile = Utilities.toFile(this.getClass().getResource(clazz + JAVA_EXT_SUFFIX).toURI());
                FileObject classFileObject = FileUtil.toFileObject(classFile);
                classFileObject.copy(classPackage, clazz, JAVA_EXT);
            }

            ClassWizardDescriptor descriptor = new ClassWizardDescriptor();
            descriptor.loadSource(getProgressReporter(), entityMappings, src, classFqns, false);

            generateClasses(entityMappings, (String) null);
        } catch (Exception ex) {
            fail(Arrays.toString(classes) + " reverse engineering failed", ex);
        }
    }

    protected EntityMappings createEntityMappings() throws IOException {
        String packageName = this.getClass().getPackage().getName();
        EntityMappings entityMappings = EntityMappings.getNewInstance(JPAModelerUtil.getModelerFileVersion());
        entityMappings.setEntityPackage(packageName);
        return entityMappings;
    }

    protected EntityMappings loadEntityMappings(String fileName) {
        try {
            URL resource = this.getClass().getResource(fileName);
            assertNotNull(resource, fileName + " not found");
            File file = Utilities.toFile(resource.toURI());
            return getEntityMapping(file);
        } catch (JAXBException ex) {
            fail(fileName + " file jaxb parsing error", ex);
        } catch (URISyntaxException ex) {
            fail(fileName + " file uri syntax error", ex);
        }
        throw new IllegalStateException("Unable to load file : " + fileName);
    }

    protected ProgressReporter getProgressReporter() {
        return (message, index) -> System.out.println(wrap(message, FG_DARK_YELLOW));
    }
    /**
     * Create a new data file with specified initial contents.No file events
     * should be fired until the resulting file is complete (see
     * {@link FileObject#createAndOpen}).
     *
     * @param root a root folder which should already exist
     * @param path a /-separated path to the new file within that root
     * @param body the complete contents of the new file (in UTF-8 encoding)
     * @return
     * @throws java.io.IOException
     */
    public static FileObject writeFile(FileObject root, String path, String body) throws IOException {
        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            root = FileUtil.createFolder(root, path.substring(0, slash));
            path = path.substring(slash + 1);
        }
        FileObject existing = root.getFileObject(path);
        OutputStream os = existing != null ? existing.getOutputStream() : root.createAndOpen(path);
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, UTF_8));
            pw.print(body);
            pw.flush();
        } finally {
            os.close();
        }
        return root.getFileObject(path);
    }

//    protected InvocationResult fireMavenBuild(Project project, List<String> goals, List<String> profiles, Properties properties) {
//        InvocationResult result = null;
//        try {
//            String mavenHome = System.getenv("M2_HOME");
//            if (mavenHome == null) {
//                mavenHome = System.getenv("MAVEN_HOME");
//            }
//            InvocationRequest request = new DefaultInvocationRequest();
//            request.setPomFile(new File(project.getProjectDirectory().getPath() + "/pom.xml"));
//            System.out.println("Project : " + request.getPomFile().getAbsolutePath());
//            request.setGoals(goals);
//            request.setProfiles(profiles);
//            request.setProperties(properties);
//
//            Invoker invoker = new DefaultInvoker();
//            invoker.setMavenHome(new File(mavenHome));
//            System.out.println("Maven Home : " + mavenHome);
//            result = invoker.execute(request);
//            assertEquals(0, result.getExitCode(), "Maven build failed : " + result.getExecutionException().getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            fail("Maven build failed", ex);
//        }
//        return result;
//    }

    protected int fireMavenBuild(Project project, List<String> goals, List<String> profiles, Properties properties) {
        int code = 0;
        String mavenHome = System.getenv("M2_HOME");
        if (mavenHome == null) {
            mavenHome = System.getenv("MAVEN_HOME");
        }
        if (mavenHome == null) {
            throw new IllegalStateException("MAVEN_HOME or M2_HOME path not defined");
        }

        try {
            String cmd = (System.getProperty("os.name").startsWith("Windows")) ? "mvn.cmd" : "mvn";
            cmd = mavenHome + File.separator + "bin" + File.separator + cmd;
            List<String> args = new ArrayList<>();
            args.add(cmd);
            args.add("-B");

            if (goals != null && !goals.isEmpty()) {
                args.addAll(goals);
            }
            if (profiles != null && !profiles.isEmpty()) {
                args.add("-P");
                args.addAll(profiles);
            }
            if (properties != null) {
                properties.entrySet().forEach(e -> args.add("-D" + e.getKey() + "=" + e.getValue()));
            }

            ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[]{}));
            pb.directory(FileUtil.toFile(project.getProjectDirectory()));
            Process process = pb.start();

            redirectOutput(process.getInputStream(), System.out);
            redirectOutput(process.getErrorStream(), System.err);
            code = process.waitFor();
            if (code != 0) {
                assertEquals(0, code, "Maven Process build failed : " + code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Maven Process build failed", ex);
        }
        return code;
    }

    private void redirectOutput(final InputStream input, final PrintStream output) {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    output.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}
