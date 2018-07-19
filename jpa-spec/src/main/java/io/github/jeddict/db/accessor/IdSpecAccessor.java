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
package io.github.jeddict.db.accessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.extend.Attribute;

/**
 *
 * @author Gaurav Gupta
 */
public class IdSpecAccessor extends IdAccessor {

    private Id id;
    private boolean inherit;

    private IdSpecAccessor(Id id) {
        this.id = id;
    }

    public static IdSpecAccessor getInstance(Id id, boolean inherit) {
        IdSpecAccessor accessor = new IdSpecAccessor(id);
        accessor.inherit = inherit;
        accessor.setName(id.getName());
        accessor.setAttributeType(id.getAttributeType());
        if (id.getColumn() != null) {
            accessor.setColumn(id.getColumn().getAccessor());
        }
        if (id.getGeneratedValue() != null && id.getGeneratedValue().getStrategy()!=null) {
            accessor.setGeneratedValue(id.getGeneratedValue().getAccessor());
        }  
        if (id.getSequenceGenerator()!= null) {
            accessor.setSequenceGenerator(id.getSequenceGenerator().getAccessor());
        }  
        if (id.getTableGenerator() != null) {
            accessor.setTableGenerator(id.getTableGenerator().getAccessor());
        }  
        
        return accessor;
    }

    @Override
    public void process() {
        super.process(); 
        getMapping().setProperty(Attribute.class, id);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
    }

}
