/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jpa.spec.DefaultAttribute;
import io.github.jeddict.jpa.spec.DefaultClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.orm.generator.compiler.def.EmbeddableIdDefSnippet;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;

public class EmbeddableIdClassGenerator extends ClassGenerator<EmbeddableIdDefSnippet> {

    private final DefaultClass defaultClass;

    public EmbeddableIdClassGenerator(DefaultClass parsedDefaultClass, String packageName) {
        super(new EmbeddableIdDefSnippet());
        this.defaultClass = parsedDefaultClass;
        this.rootPackageName = packageName;
        this.packageName = defaultClass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public EmbeddableIdDefSnippet getClassDef() {
        defaultClass.getAttributes().getDefaultAttributes()
                .forEach(this::processDefaultAttribute);
        classDef = initClassDef(packageName, defaultClass);
        classDef.setDefaultClass(true);

        return classDef;
    }

    @Override
    protected VariableDefSnippet processVariable(Attribute attr) {
        if (attr instanceof DefaultAttribute) {
            return processDefaultAttribute((DefaultAttribute) attr);
        } else {
            throw new IllegalStateException("Invalid Attribute Type");
        }
    }

    protected VariableDefSnippet processDefaultAttribute(DefaultAttribute attr) {
        VariableDefSnippet variableDef = getVariableDef(attr);
        variableDef.setType(attr.getAttributeType());
        return variableDef;
    }
}
