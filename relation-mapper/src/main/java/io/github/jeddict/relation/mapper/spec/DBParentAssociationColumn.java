/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.spec;

import java.util.List;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.Attribute;

public abstract class DBParentAssociationColumn<E extends Attribute> extends DBParentColumn<E> implements DBForeignKey<JoinColumn> {

    protected AssociationOverride associationOverride;
    protected final boolean relationTableExist;

    protected JoinColumn joinColumn;
    protected List<JoinColumn> joinColumns;

    protected JoinColumn joinColumnOverride;
    protected List<JoinColumn> joinColumnsOverride;

    public DBParentAssociationColumn(String name, Entity intrinsicClass, E managedAttribute, boolean relationTableExist) {
        super(name, intrinsicClass, managedAttribute);
        this.relationTableExist = relationTableExist;

        associationOverride = intrinsicClass.findAssociationOverride(getKeyName());
        if (associationOverride == null) {
            associationOverride = new AssociationOverride();
            associationOverride.setName(getKeyName());
            intrinsicClass.addAssociationOverride(associationOverride);
        }
    }

    /**
     * @return the associationOverride
     */
    public AssociationOverride getAssociationOverride() {
        return associationOverride;
    }

    /**
     * @return the joinColumn
     */
    @Override
    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    @Override
    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public JoinColumn getJoinColumnOverride() {
        return joinColumnOverride;
    }

    public List<JoinColumn> getJoinColumnsOverride() {
        return joinColumnsOverride;
    }

    /**
     * Get the value of relationTableExist
     *
     * @return the value of relationTableExist
     */
    public boolean isRelationTableExist() {
        return relationTableExist;
    }
}
