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
package io.github.jeddict.orm.generator.service.packageinfo;

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import java.io.File;
import java.io.IOException;
import io.github.jeddict.util.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.spec.ModuleGenerator;
import io.github.jeddict.orm.generator.util.ClassType;
import io.github.jeddict.orm.generator.util.ClassesRepository;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ModuleGenerator.class)
public class PackageInfoModuleGeneratorImpl implements ModuleGenerator {

    private final ClassesRepository classesRepository = ClassesRepository.getInstance();
    private String packageName;
    private File destDir;

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings) {
        destDir = FileUtil.toFile(sourceGroup.getRootFolder());
        this.packageName = entityMappings.getPackage();
        try {
            if (StringUtils.isBlank(entityMappings.getJaxbNameSpace())
                    && !entityMappings.isJsonbPackageInfoExist()) {
                return;
            }
            PackageInfoGenerator packageInfoGenerator = new PackageInfoGenerator(entityMappings, packageName);
            ClassDefSnippet packageInfoDef = packageInfoGenerator.getClassDef();
            classesRepository.addWritableSnippet(ClassType.JAXB_PACKAGE_INFO, packageInfoDef);
            task.log("Generating package-info.java", true);
            ORMConverterUtil.writeSnippet(packageInfoDef, destDir);
        } catch (InvalidDataException | IOException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
    }

}
