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

import org.netbeans.jpa.modeler.spec.bean.AssociationAttribute;
import org.netbeans.jpa.modeler.spec.bean.BeanClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.orm.converter.compiler.def.VariableDefSnippet;
import org.netbeans.orm.converter.compiler.def.BeanClassDefSnippet;

public class BeanClassGenerator extends ClassGenerator<BeanClassDefSnippet> {

    private final BeanClass beanClass;

    public BeanClassGenerator(BeanClass beanClass, String packageName) {
        super(new BeanClassDefSnippet(), beanClass.getRootElement().getJavaEEVersion());
        this.beanClass = beanClass;
        this.rootPackageName = packageName;
        this.packageName = beanClass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public BeanClassDefSnippet getClassDef() {
        for (Attribute attribute : beanClass.getAttributes().getAllAttribute()) {
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
        }
                   
        classDef = initClassDef(packageName, beanClass);
        classDef.setBeanClass(true);
        return classDef;
    }
}
