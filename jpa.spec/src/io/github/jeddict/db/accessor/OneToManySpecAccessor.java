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
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import io.github.jeddict.db.modeler.exception.DBValidationException;
import io.github.jeddict.db.accessor.spec.MapKeyAccessor;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class OneToManySpecAccessor extends OneToManyAccessor implements MapKeyAccessor {

    private final OneToMany oneToMany;
    private boolean inherit;

    private OneToManySpecAccessor(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
    }

    public static OneToManySpecAccessor getInstance(OneToMany oneToMany, boolean inherit) {
        OneToManySpecAccessor accessor = new OneToManySpecAccessor(oneToMany);
        accessor.inherit = inherit;
        accessor.setName(oneToMany.getName());
        accessor.setTargetEntityName(oneToMany.getTargetEntity());
        accessor.setAttributeType(oneToMany.getCollectionType());
        accessor.setMappedBy(oneToMany.getMappedBy());
        if (!JoinTableValidator.isEmpty(oneToMany.getJoinTable())) {
            accessor.setJoinTable(oneToMany.getJoinTable().getAccessor());
        }
        JoinColumnValidator.filter(oneToMany.getJoinColumn());
        accessor.setJoinColumns(oneToMany.getJoinColumn().stream().map(JoinColumn::getAccessor).collect(toList()));
        if (oneToMany.getOrderColumn() != null) {
            accessor.setOrderColumn(oneToMany.getOrderColumn().getAccessor());
        }
        accessor.setMapKeyConverts(oneToMany.getMapKeyConverts().stream().map(Convert::getAccessor).collect(toList()));
        MapKeyUtil.load(accessor, oneToMany); 
        return accessor;

    }

    @Override
    public void process() {
        try{
        super.process();
        getMapping().setProperty(Attribute.class, oneToMany);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(oneToMany.getJavaClass());
            exception.setAttribute(oneToMany);
            throw exception;
        }
    }

}
