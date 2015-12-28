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

import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.orm.converter.generator.ClassGenerator;
import org.netbeans.orm.converter.util.ClassHelper;

public class JaxbPackageInfoGenerator extends ClassGenerator<JaxbPackageInfoClassDefSnippet> {

    private EntityMappings parsedEntityMappings = null;

    public JaxbPackageInfoGenerator(EntityMappings parsedEntityMappings, String packageName) {
        super(new JaxbPackageInfoClassDefSnippet());
        this.parsedEntityMappings = parsedEntityMappings;
        this.packageName = packageName;
    }

    @Override
    public JaxbPackageInfoClassDefSnippet getClassDef() {
        ClassHelper classHelper = new ClassHelper("package-info");
        classHelper.setPackageName(packageName);
        classDef.setClassName(classHelper.getFQClassName());
        classDef.setPackageName(classHelper.getPackageName());
        classDef.setNamespace(parsedEntityMappings.getJaxbNameSpace());
        return classDef;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.parsedEntityMappings.getJaxbNameSpace() != null ? this.parsedEntityMappings.getJaxbNameSpace().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JaxbPackageInfoGenerator other = (JaxbPackageInfoGenerator) obj;
        return !(this.parsedEntityMappings.getJaxbNameSpace() != other.parsedEntityMappings.getJaxbNameSpace() && (this.parsedEntityMappings.getJaxbNameSpace() == null || !this.parsedEntityMappings.getJaxbNameSpace().equals(other.parsedEntityMappings.getJaxbNameSpace())));
    }

}
