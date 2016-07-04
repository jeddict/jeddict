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
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.netbeans.db.modeler.exception.DBValidationException;
import org.netbeans.jpa.modeler.db.accessor.spec.MapKeyAccessor;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class OneToManySpecAccessor extends OneToManyAccessor implements MapKeyAccessor {

    private final OneToMany oneToMany;

    private OneToManySpecAccessor(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
    }

    public static OneToManySpecAccessor getInstance(OneToMany oneToMany) {
        OneToManySpecAccessor accessor = new OneToManySpecAccessor(oneToMany);
        accessor.setName(oneToMany.getName());
        accessor.setTargetEntityName(oneToMany.getTargetEntity());
        accessor.setAttributeType(oneToMany.getCollectionType());
        accessor.setMappedBy(oneToMany.getMappedBy());
        if (!JoinTableValidator.isEmpty(oneToMany.getJoinTable())) {
            accessor.setJoinTable(oneToMany.getJoinTable().getAccessor());
        }
        JoinColumnValidator.filter(oneToMany.getJoinColumn());
        accessor.setJoinColumns(oneToMany.getJoinColumn().stream().map(JoinColumn::getAccessor).collect(toList()));

        MapKeyUtil.load(accessor, oneToMany); 
        return accessor;

    }

    @Override
    public void process() {
        try{
        super.process();
        getMapping().setProperty(Attribute.class, oneToMany);
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setAttribute(oneToMany);
        }
    }

}
