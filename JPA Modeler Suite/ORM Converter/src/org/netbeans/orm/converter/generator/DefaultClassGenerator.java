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

import java.util.ArrayList;
import java.util.logging.Logger;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;

public class DefaultClassGenerator extends ClassGenerator {

    private static Logger logger = ORMConvLogger.getLogger(DefaultClassGenerator.class);

    private DefaultClass defaultClass = null;

    public DefaultClassGenerator(DefaultClass parsedDefaultClass, String packageName) {
        this.defaultClass = parsedDefaultClass;
        this.packageName = packageName;
    }

    public ClassDefSnippet getClassDef() {
        for (DefaultAttribute defaultAttribute : defaultClass.getAttributes()) {
            VariableDefSnippet variableDef = getVariableDef(defaultAttribute.getName());
            variableDef.setType(defaultAttribute.getAttributeType());
        }
        //Class decorations
        ClassHelper classHelper = new ClassHelper(defaultClass.getClazz());
        classHelper.setPackageName(packageName);

        classDef.setVariableDefs(new ArrayList<VariableDefSnippet>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());
        classDef.setPackageName(classHelper.getPackageName());
        classDef.setDefaultClass(true);

        return classDef;
    }
}
