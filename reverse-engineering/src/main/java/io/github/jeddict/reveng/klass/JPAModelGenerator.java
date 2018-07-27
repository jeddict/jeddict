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
package io.github.jeddict.reveng.klass;

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.source.JavaSourceParserUtil;
import java.io.IOException;
import java.util.List;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

public class JPAModelGenerator {

    public static void generateJPAModel(final EntityMappings entityMappings, Project project, final String entityClass, FileObject pkg, final List<String> missingEntities) throws IOException {
        final boolean[] fieldAccess = new boolean[]{false};
        //detect access type
        final ClasspathInfo classpathInfo = ClasspathInfo.create(pkg);
        JavaSource javaSource = JavaSource.create(classpathInfo);
        javaSource.runUserActionTask((CompilationController controller) -> {
            try {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                TypeElement jc = controller.getElements().getTypeElement(entityClass);
                if (jc != null) {
                    fieldAccess[0] = JavaSourceParserUtil.isFieldAccess(jc);
                    if (!entityMappings.findEntity(jc.getSimpleName().toString()).isPresent()) {
                        io.github.jeddict.jpa.spec.Entity entitySpec = new io.github.jeddict.jpa.spec.Entity();
                        entitySpec.load(entityMappings, jc, fieldAccess[0]);
                        entityMappings.addEntity(entitySpec);
                    }
                } else {
                    missingEntities.add(entityClass);
                }
            } catch(Throwable t){
                ExceptionUtils.printStackTrace(t);
            }
        }, true);
    }

}
