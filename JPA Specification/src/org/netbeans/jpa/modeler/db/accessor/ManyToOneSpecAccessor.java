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
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.netbeans.db.modeler.exception.DBValidationException;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class ManyToOneSpecAccessor extends ManyToOneAccessor {

    private final ManyToOne manyToOne;

    private ManyToOneSpecAccessor(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
    }

    public static ManyToOneSpecAccessor getInstance(ManyToOne manyToOne) {
        ManyToOneSpecAccessor accessor = new ManyToOneSpecAccessor(manyToOne);
        accessor.setName(manyToOne.getName());
        accessor.setTargetEntityName(manyToOne.getTargetEntity());
        if (manyToOne.isPrimaryKey()) { 
            IdClass idClass = manyToOne.getIdClass();
            if (idClass != null) {
                accessor.setId(Boolean.TRUE);
            } else {
                accessor.setMapsId(manyToOne.getName());
            }
        }
        if (!JoinTableValidator.isEmpty(manyToOne.getJoinTable())) {
            accessor.setJoinTable(manyToOne.getJoinTable().getAccessor());
        }
        JoinColumnValidator.filter(manyToOne.getJoinColumn());
        accessor.setJoinColumns(manyToOne.getJoinColumn().stream().map(JoinColumn::getAccessor).collect(toList()));
        return accessor;
    }

    @Override
    public void process() {
        try {
        super.process();
        getMapping().setProperty(Attribute.class, manyToOne);
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setAttribute(manyToOne);
        }
    }

}
