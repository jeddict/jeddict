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
package org.netbeans.orm.converter.generator;

import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;

public class DefaultClassGenerator extends ClassGenerator<ClassDefSnippet> {

    private DefaultClass defaultClass;

    public DefaultClassGenerator(DefaultClass parsedDefaultClass, String packageName) {
        super(new ClassDefSnippet());
        this.defaultClass = parsedDefaultClass;
        this.rootPackageName = packageName;
        this.packageName = defaultClass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public ClassDefSnippet getClassDef() {
        defaultClass.getAttributes().getDefaultAttributes()
                .stream()
                .forEach((defaultAttribute) -> {
                    VariableDefSnippet variableDef = getVariableDef(defaultAttribute);
                    variableDef.setType(defaultAttribute.getAttributeType());
                });
        classDef = initClassDef(packageName, defaultClass);
        classDef.setDefaultClass(true);

        return classDef;
    }
}
