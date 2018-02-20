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
package io.github.jeddict.relation.mapper.spec;

import java.util.List;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.Attribute;

public abstract class DBEmbeddedAssociationColumn<E extends Attribute> extends DBEmbeddedColumn<E> implements DBForeignKey<JoinColumn> {

    protected AssociationOverride associationOverride;
    protected final boolean relationTableExist;

    protected JoinColumn joinColumn;
    protected List<JoinColumn> joinColumns;

    protected JoinColumn joinColumnOverride;
    protected List<JoinColumn> joinColumnsOverride;

    public DBEmbeddedAssociationColumn(String name, List<Embedded> embeddedList, E managedAttribute, boolean relationTableExist) {
        super(name, embeddedList, managedAttribute);
        this.relationTableExist = relationTableExist;

        associationOverride = embeddedList.get(0).findAssociationOverride(getKeyName());
        if (associationOverride == null) {
            associationOverride = new AssociationOverride();
            associationOverride.setName(getKeyName());
            embeddedList.get(0).addAssociationOverride(associationOverride);
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
