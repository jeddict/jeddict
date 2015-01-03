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
package org.netbeans.orm.converter.generator.staticmetamodel;

import org.netbeans.orm.converter.compiler.ClassDefSnippet;

/**
 *
 * The StaticMetamodel annotation specifies that the class is a metamodel class
 * that represents the entity, mapped superclass, or embeddable class designated
 * by the value element.
 */
public class StaticMetamodelClassDefSnippet extends ClassDefSnippet {

    private static final String STATIC_METAMODEL_TEMPLATE_FILENAME = "staticmetamodel.vm";

    protected String getTemplateName() {
        return STATIC_METAMODEL_TEMPLATE_FILENAME;
    }

    private String value;//i.e: @StaticMetamodel( Person.class )   Class being modelled by the annotated class. //entity, mapped superclass, or embeddable class

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
