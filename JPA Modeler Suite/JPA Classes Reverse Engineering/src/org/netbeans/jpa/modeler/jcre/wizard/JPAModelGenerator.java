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
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import org.openide.filesystems.FileObject;

public class JPAModelGenerator {

    public static void generateJPAModel(final EntityMappings entityMappings, Project project, final String entityClass, FileObject pkg) throws IOException {
        final boolean isInjection = Util.isContainerManaged(project);
        final String simpleEntityName = JavaSourceParserUtil.simpleClassName(entityClass);
        String persistenceUnit = Util.getPersistenceUnitAsString(project, entityClass);
        final String fieldName = JavaSourceParserUtil.fieldFromClassName(simpleEntityName);

        final List<ElementHandle<ExecutableElement>> idGetter = new ArrayList<ElementHandle<ExecutableElement>>();
        final FileObject[] arrEntityClassFO = new FileObject[1];
        final List<ElementHandle<ExecutableElement>> toOneRelMethods = new ArrayList<ElementHandle<ExecutableElement>>();
        final List<ElementHandle<ExecutableElement>> toManyRelMethods = new ArrayList<ElementHandle<ExecutableElement>>();
        final boolean[] fieldAccess = new boolean[]{false};
        //detect access type
        final ClasspathInfo classpathInfo = ClasspathInfo.create(pkg);
        JavaSource javaSource = JavaSource.create(classpathInfo);
        javaSource.runUserActionTask(new Task<CompilationController>() {
            @Override
            public void run(CompilationController controller) throws IOException {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                TypeElement jc = controller.getElements().getTypeElement(entityClass);
                ElementHandle<TypeElement> elementHandle = ElementHandle.create(jc);
                arrEntityClassFO[0] = org.netbeans.api.java.source.SourceUtils.getFile(elementHandle, controller.getClasspathInfo());
                fieldAccess[0] = JavaSourceParserUtil.isFieldAccess(jc);

                org.netbeans.jpa.modeler.spec.Entity entitySpec = new org.netbeans.jpa.modeler.spec.Entity();
                entitySpec.load(entityMappings, jc, fieldAccess[0]);
                entityMappings.addEntity(entitySpec);
                System.out.println("Entity : " + entitySpec);

            }
        }, true);

//        if (idGetter.size() < 1) {
//            String msg = entityClass + ": " + NbBundle.getMessage(JpaControllerGenerator.class, "ERR_GenJsfPages_CouldNotFindIdProperty"); //NOI18N
//            if (fieldAccess[0]) {
//                msg += " " + NbBundle.getMessage(JpaControllerGenerator.class, "ERR_GenJsfPages_EnsureSimpleIdNaming"); //NOI18N
//            }
//            throw new IOException(msg);
//        }
//
//        if (arrEntityClassFO[0] != null) {
//            addImplementsClause(arrEntityClassFO[0], entityClass, "java.io.Serializable"); //NOI18N
//        }
//
//        controllerFileObject = addImplementsClause(controllerFileObject, controllerClass, "java.io.Serializable"); //NOI18N
//        generateJpaController(fieldName, pkg, idGetter.get(0), persistenceUnit, controllerClass, exceptionPackage,
//                entityClass, simpleEntityName, toOneRelMethods, toManyRelMethods, isInjection, fieldAccess[0], controllerFileObject, embeddedPkSupport, getPersistenceVersion(project));
    }

}
