/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.SecondaryTable;

public class DBPrimaryKeyJoinColumn extends DBColumn<Id> implements DBForeignKey<PrimaryKeyJoinColumn> {

    private final PrimaryKeyJoinColumn joinColumn;
    private final List<PrimaryKeyJoinColumn> joinColumns;

    public DBPrimaryKeyJoinColumn(String name, Entity entity, Id attribute) {
        super(name, attribute);
        joinColumns = JoinColumnFinder.findPrimaryKeyJoinColumns(entity);
        joinColumn = JoinColumnFinder.findPrimaryKeyJoinColumn(name, joinColumns);
    }
    
    public DBPrimaryKeyJoinColumn(String name, SecondaryTable secondaryTable, Id attribute) {
        super(name, attribute);
        joinColumns = JoinColumnFinder.findPrimaryKeyJoinColumns(secondaryTable);
        joinColumn = JoinColumnFinder.findPrimaryKeyJoinColumn(name, joinColumns);
    }

    /**
     * @return the inverseJoinColumn
     */
    @Override
    public PrimaryKeyJoinColumn getJoinColumn() {
        return joinColumn;
    }

    @Override
    public List<PrimaryKeyJoinColumn> getJoinColumns() {
        return joinColumns;
    }
}
