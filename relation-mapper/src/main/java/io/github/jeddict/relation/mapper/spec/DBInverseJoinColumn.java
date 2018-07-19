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
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;

public class DBInverseJoinColumn extends DBColumn<RelationAttribute> implements DBForeignKey<JoinColumn> {

    private final boolean relationTableExist;

    private final JoinColumn joinColumn;
    private final List<JoinColumn> joinColumns;

    public DBInverseJoinColumn(String name, RelationAttribute attribute, boolean relationTableExist) {
        super(name, attribute);
        this.relationTableExist = relationTableExist;
        joinColumns = JoinColumnFinder.findJoinColumns(attribute, relationTableExist, true);
        joinColumn = JoinColumnFinder.findJoinColumn(name, joinColumns);
    }

    /**
     * @return the inverseJoinColumn
     */
    @Override
    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    @Override
    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public boolean isRelationTableExist() {
        return relationTableExist;
    }
}
