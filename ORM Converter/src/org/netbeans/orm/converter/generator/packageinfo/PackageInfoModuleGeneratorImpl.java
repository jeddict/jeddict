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
package org.netbeans.orm.converter.generator.packageinfo;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.task.ITaskSupervisor;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.spec.ModuleGenerator;
import org.netbeans.orm.converter.util.ClassType;
import org.netbeans.orm.converter.util.ClassesRepository;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import org.openide.filesystems.FileUtil;

@org.openide.util.lookup.ServiceProvider(service = ModuleGenerator.class)
public class PackageInfoModuleGeneratorImpl implements ModuleGenerator {

    private final ClassesRepository classesRepository = ClassesRepository.getInstance();
    private String packageName;
    private File destDir;

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings parsedEntityMappings) {
        destDir = FileUtil.toFile(sourceGroup.getRootFolder());
        this.packageName = parsedEntityMappings.getPackage();
        try {
            if (StringUtils.isBlank(parsedEntityMappings.getJaxbNameSpace())
                    && !parsedEntityMappings.isJsonbPackageInfoExist()) {
                return;
            }
            PackageInfoGenerator packageInfoGenerator = new PackageInfoGenerator(parsedEntityMappings, packageName);
            ClassDefSnippet packageInfoDef = packageInfoGenerator.getClassDef();
            classesRepository.addWritableSnippet(ClassType.JAXB_PACKAGE_INFO, packageInfoDef);
            task.log("Generating package-info.java", true);
            ORMConverterUtil.writeSnippet(packageInfoDef, destDir);
        } catch (InvalidDataException | IOException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
    }

}
