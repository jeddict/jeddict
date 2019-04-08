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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jpa.spec.bean.AssociationAttribute;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import io.github.jeddict.orm.generator.compiler.def.BeanClassDefSnippet;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;

public class BeanClassGenerator extends ClassGenerator<BeanClassDefSnippet> {

    private final BeanClass beanClass;

    public BeanClassGenerator(BeanClass beanClass, String packageName) {
        super(new BeanClassDefSnippet());
        this.beanClass = beanClass;
        this.rootPackageName = packageName;
        this.packageName = beanClass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public BeanClassDefSnippet getClassDef() {
        beanClass.getAttributes().getAllAttribute().forEach(this::processVariable);
                   
        classDef = initClassDef(packageName, beanClass);
        classDef.setBeanClass(true);
        return classDef;
    }

    @Override
    protected VariableDefSnippet processVariable(Attribute attribute) {
        VariableDefSnippet variableDef = getVariableDef(attribute);
        if (attribute instanceof BaseAttribute) {
            variableDef.setType(((BaseAttribute) attribute).getAttributeType());
        } else if (attribute instanceof AssociationAttribute) {
            variableDef.setType(rootPackageName, ((AssociationAttribute) attribute).getConnectedClass());
        }
        if (attribute instanceof CollectionTypeHandler) {
            variableDef.setCollectionType(((CollectionTypeHandler) attribute).getCollectionType());
            variableDef.setCollectionImplType(((CollectionTypeHandler) attribute).getCollectionImplType());
        }
        return variableDef;
    }

}
