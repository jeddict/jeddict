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

import static io.github.jeddict.test.BaseModelTest.writeFile;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author jGauravGupta
 */
public class ProjectBuilder extends NbTestCase {

    public static final String SRC = "src/main/java";

    public static final String RESOURCES = "src/main/resources";

    public static final String TEST_SRC = "src/test/java";

    public static final String TEST_RESOURCES = "src/test/resources";

    public static final String WEBAPP = "src/main/webapp";

    private final String name;

    private final Project project;

    private final FileObject projectFileObject;

    private final FileObject src;

    private final FileObject resources;

    private final FileObject webapp;

    private final FileObject testSrc;

    private final FileObject testResources;

    public ProjectBuilder(String name) throws IOException {
        super(name);
        this.name = name;
        clearWorkDir();
        projectFileObject = FileUtil.toFileObject(getWorkDir());
        writeFile(projectFileObject, "pom.xml", getPom(name));
        project = ProjectManager.getDefault().findProject(projectFileObject);
        src = FileUtil.createFolder(projectFileObject, SRC);
        resources = FileUtil.createFolder(projectFileObject, RESOURCES);
        webapp = FileUtil.createFolder(projectFileObject, WEBAPP);
        testSrc = FileUtil.createFolder(projectFileObject, TEST_SRC);
        testResources = FileUtil.createFolder(projectFileObject, TEST_RESOURCES);
    }

    public FileObject getProjectRoot() throws IOException {
        return projectFileObject;
    }

    public Project get() {
        return project;
    }

    @Override
    public String getName() {
        return name;
    }

    public FileObject getSrc() {
        return src;
    }

    public FileObject getResources() {
        return resources;
    }

    public FileObject getWebapp() {
        return webapp;
    }

    public FileObject getTestSrc() {
        return testSrc;
    }

    public FileObject getTestResources() {
        return testResources;
    }


    private String getPom(String appName) {
        return POM.replace("${applicationName}", appName);
    }

    private static final String POM = "<project xmlns='http://maven.apache.org/POM/4.0.0'>\n"
            + "  <modelVersion>4.0.0</modelVersion>\n"
            + "  <groupId>io.github.jeddict</groupId>\n"
            + "  <artifactId>${applicationName}</artifactId>\n"
            + "  <packaging>war</packaging>\n"
            + "  <version>1.0-SNAPSHOT</version>\n"
            + "  <name>${applicationName}</name>\n"
            + "  <properties>\n"
            + "      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n"
            + "      <maven.compiler.source>1.8</maven.compiler.source>\n"
            + "      <maven.compiler.target>1.8</maven.compiler.target>\n"
            + "  </properties>\n"
            + "  <dependencies>\n"
            + "      <dependency>\n"
            + "          <groupId>javax</groupId>\n"
            + "          <artifactId>javaee-web-api</artifactId>\n"
            + "          <version>8.0</version>\n"
            + "          <scope>provided</scope>\n"
            + "      </dependency>\n"
            + "  </dependencies>\n"
            + "  <build>\n"
            + "      <plugins>\n"
            + "          <plugin>\n"
            + "              <groupId>org.apache.maven.plugins</groupId>\n"
            + "              <artifactId>maven-compiler-plugin</artifactId>\n"
            + "              <version>3.8.0</version>\n"
            + "              <configuration>\n"
            + "                  <source>${maven.compiler.source}</source>\n"
            + "                  <target>${maven.compiler.target}</target>\n"
            + "              </configuration>\n"
            + "          </plugin>\n"
            + "          <plugin>\n"
            + "              <groupId>org.apache.maven.plugins</groupId>\n"
            + "              <artifactId>maven-war-plugin</artifactId>\n"
            + "              <version>3.2.2</version>\n"
            + "              <configuration>\n"
            + "                  <failOnMissingWebXml>false</failOnMissingWebXml>\n"
            + "              </configuration>\n"
            + "          </plugin>\n"
            + "      </plugins>\n"
            + "  </build>\n"
            + "</project>";
}
