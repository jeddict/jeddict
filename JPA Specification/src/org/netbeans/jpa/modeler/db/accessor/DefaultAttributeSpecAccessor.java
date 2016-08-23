/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.db.accessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

/**
 *
 * @author Gaurav Gupta
 */
public class DefaultAttributeSpecAccessor extends BasicAccessor {

    private DefaultAttribute attribute;
    private boolean inherit;

    private DefaultAttributeSpecAccessor(DefaultAttribute attribute) {
        this.attribute = attribute;
    }

    public static DefaultAttributeSpecAccessor getInstance(DefaultAttribute attribute, boolean inherit) {
        DefaultAttributeSpecAccessor accessor = new DefaultAttributeSpecAccessor(attribute);
        accessor.inherit = inherit;
        accessor.setName(attribute.getName());
        accessor.setAttributeType(attribute.getAttributeType());
        if (attribute.getColumn() != null) {
            accessor.setColumn(attribute.getColumn().getAccessor());
        }
        return accessor;
    }

    public void process() {
        super.process();
        getMapping().setProperty(Attribute.class, attribute);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
    }

}
