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
import org.netbeans.orm.converter.compiler.ClassDefSnippet;

/**
 *
 * The StaticMetamodel annotation specifies that the class is a metamodel class
 * that represents the entity, mapped superclass, or embeddable class designated
 * by the value element.
 */
public class JaxbPackageInfoClassDefSnippet extends ClassDefSnippet {

    private static final String JAXB_PACKAGE_INFO_TEMPLATE_FILENAME = "jaxb-package-info.vm";

    @Override
    protected String getTemplateName() {
        return JAXB_PACKAGE_INFO_TEMPLATE_FILENAME;
    }

    private String namespace;//ex : @XmlSchema(namespace = "http://www.example.org/customer", elementFormDefault = XmlNsForm.QUALIFIED)
 
    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}
