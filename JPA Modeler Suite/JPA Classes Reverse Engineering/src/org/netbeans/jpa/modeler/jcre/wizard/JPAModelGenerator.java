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
package org.netbeans.jpa.modeler.jcre.wizard;

import org.netbeans.jpa.source.JavaSourceParserUtil;
import java.io.IOException;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.filesystems.FileObject;

public class JPAModelGenerator {

    public static void generateJPAModel(final EntityMappings entityMappings, Project project, final String entityClass, FileObject pkg) throws IOException {
//        final boolean isInjection = Util.isContainerManaged(project);
//        final String simpleEntityName = JavaSourceParserUtil.simpleClassName(entityClass);

        final boolean[] fieldAccess = new boolean[]{false};
        //detect access type
        final ClasspathInfo classpathInfo = ClasspathInfo.create(pkg);
        JavaSource javaSource = JavaSource.create(classpathInfo);
        javaSource.runUserActionTask(new Task<CompilationController>() {
            @Override
            public void run(CompilationController controller) throws IOException {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                TypeElement jc = controller.getElements().getTypeElement(entityClass);
//                ElementHandle<TypeElement> elementHandle = ElementHandle.create(jc);
//                arrEntityClassFO[0] = org.netbeans.api.java.source.SourceUtils.getFile(elementHandle, controller.getClasspathInfo());
                fieldAccess[0] = JavaSourceParserUtil.isFieldAccess(jc);
                org.netbeans.jpa.modeler.spec.Entity entitySpec = new org.netbeans.jpa.modeler.spec.Entity();
                entitySpec.load(entityMappings, jc, fieldAccess[0]);
                entityMappings.addEntity(entitySpec);

            }
        }, true);
 }

}
