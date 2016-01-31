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

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

/**
 *
 * @author Gaurav Gupta
 */
public class IdSpecAccessor extends IdAccessor{
    
    private Id id;

    private IdSpecAccessor(Id id) {
        this.id = id;
    }
    public static IdSpecAccessor getInstance(Id id){
        IdSpecAccessor accessor = new IdSpecAccessor(id);
        accessor.setName(id.getName());
        accessor.setAttributeType(id.getAttributeType());
        if (id.getColumn() != null) {
            accessor.setColumn(id.getColumn().getAccessor());
        }
        return accessor;
    }

    public void process(){
        super.process();
        getMapping().setProperty(Attribute.class, id);
    }
    
}
