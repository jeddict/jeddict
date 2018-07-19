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

import static java.util.stream.Collectors.toList;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import io.github.jeddict.db.modeler.exception.DBValidationException;
import io.github.jeddict.jpa.spec.IdClass;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.ManyToOne;
import io.github.jeddict.jpa.spec.PrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.IAttributes;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class ManyToOneSpecAccessor extends ManyToOneAccessor {

    private final ManyToOne manyToOne;
    private boolean inherit;

    private ManyToOneSpecAccessor(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
    }

    public static ManyToOneSpecAccessor getInstance(ManyToOne manyToOne, boolean inherit) {
        ManyToOneSpecAccessor accessor = new ManyToOneSpecAccessor(manyToOne);
        accessor.inherit = inherit;
        accessor.setName(manyToOne.getName());
        accessor.setTargetEntityName(manyToOne.getTargetEntity());
        if (manyToOne.isPrimaryKey()) { 
            IdClass idClass = manyToOne.getIdClass();
            if (idClass != null) {
                accessor.setId(Boolean.TRUE);
            } else {
                IAttributes attributes = ((ManagedClass) manyToOne.getJavaClass()).getAttributes();
                if (attributes instanceof PrimaryKeyAttributes && !((PrimaryKeyAttributes) attributes).hasCompositePrimaryKey()) { //Ex 4.a Derived Identity
                    accessor.setId(Boolean.TRUE);
                } else {
                    accessor.setMapsId(manyToOne.getName());
                }
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
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(manyToOne.getJavaClass());
            exception.setAttribute(manyToOne);
            throw exception;
        }
    }

}
