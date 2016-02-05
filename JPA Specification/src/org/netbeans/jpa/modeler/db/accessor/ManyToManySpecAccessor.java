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

import static java.util.stream.Collectors.toList;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class ManyToManySpecAccessor extends ManyToManyAccessor{
    
    private final ManyToMany manyToMany;

    private ManyToManySpecAccessor(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
    }
    public static ManyToManySpecAccessor getInstance(ManyToMany manyToMany){
        ManyToManySpecAccessor accessor = new ManyToManySpecAccessor(manyToMany);
        accessor.setName(manyToMany.getName());
        accessor.setTargetEntityName(manyToMany.getTargetEntity());
        accessor.setAttributeType(manyToMany.getCollectionType());
        accessor.setMappedBy(manyToMany.getMappedBy());
        if (!JoinTableValidator.isEmpty(manyToMany.getJoinTable())) {
            accessor.setJoinTable(manyToMany.getJoinTable().getAccessor());
        }
       return accessor;
    }

    @Override
    public void process(){
        super.process();
        getMapping().setProperty(Attribute.class, manyToMany);
    }
    
}
