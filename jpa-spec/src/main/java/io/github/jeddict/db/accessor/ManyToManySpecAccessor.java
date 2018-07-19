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
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import io.github.jeddict.db.modeler.exception.DBValidationException;
import io.github.jeddict.db.accessor.spec.MapKeyAccessor;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class ManyToManySpecAccessor extends ManyToManyAccessor implements MapKeyAccessor  {

    private final ManyToMany manyToMany;
    private boolean inherit;

    private ManyToManySpecAccessor(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
    }

    public static ManyToManySpecAccessor getInstance(ManyToMany manyToMany, boolean inherit) {
        ManyToManySpecAccessor accessor = new ManyToManySpecAccessor(manyToMany);
        accessor.inherit = inherit;
        accessor.setName(manyToMany.getName());
        accessor.setTargetEntityName(manyToMany.getTargetEntity());
        accessor.setAttributeType(manyToMany.getCollectionType());
        accessor.setMappedBy(manyToMany.getMappedBy());
        if (!JoinTableValidator.isEmpty(manyToMany.getJoinTable())) {
            accessor.setJoinTable(manyToMany.getJoinTable().getAccessor());
        }
        if (manyToMany.getOrderColumn() != null) {
            accessor.setOrderColumn(manyToMany.getOrderColumn().getAccessor());
        }
        accessor.setMapKeyConverts(manyToMany.getMapKeyConverts().stream().map(Convert::getAccessor).collect(toList()));
        MapKeyUtil.load(accessor, manyToMany);
        return accessor;
    }

    @Override
    public void process() {
        try{
        super.process();
        getMapping().setProperty(Attribute.class, manyToMany);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(manyToMany.getJavaClass());
            exception.setAttribute(manyToMany);
            throw exception;
        }
    }

}
