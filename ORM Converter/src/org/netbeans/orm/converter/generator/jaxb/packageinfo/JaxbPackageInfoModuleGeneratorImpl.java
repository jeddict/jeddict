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
package org.netbeans.orm.converter.generator.jaxb.packageinfo;

import org.netbeans.orm.converter.generator.staticmetamodel.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.spec.ModuleGenerator;
import org.netbeans.orm.converter.util.ClassType;
import org.netbeans.orm.converter.util.ClassesRepository;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

@org.openide.util.lookup.ServiceProvider(service = ModuleGenerator.class)
public class JaxbPackageInfoModuleGeneratorImpl implements ModuleGenerator {

    private Set<StaticMetamodelGenerator> staticMetamodelClass;//Required Generation based on inheritence means if any entity metamodel is generated then its super class metamodel must be generated either user want or not .
    private ITaskSupervisor task;
    private final ClassesRepository classesRepository = ClassesRepository.getInstance();
    private String packageName;
    private File destDir;

    @Override
    public void generate(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings parsedEntityMappings) {
        this.staticMetamodelClass = new HashSet<StaticMetamodelGenerator>();
        this.task = task;
        destDir = FileUtil.toFile(sourceGroup.getRootFolder());
        this.packageName = parsedEntityMappings.getPackage();
        try {
            if(parsedEntityMappings.getJaxbNameSpace()==null || parsedEntityMappings.getJaxbNameSpace().trim().isEmpty()){
                return;
            }
            JaxbPackageInfoGenerator packageInfoGenerator = new JaxbPackageInfoGenerator(parsedEntityMappings, packageName);
            ClassDefSnippet packageInfoDef = packageInfoGenerator.getClassDef();
            classesRepository.addWritableSnippet(ClassType.JAXB_PACKAGE_INFO, packageInfoDef);
            task.log("Generating JAXB package-info.java", true);
            ORMConverterUtil.writeSnippet(packageInfoDef, destDir);
        } catch (InvalidDataException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
