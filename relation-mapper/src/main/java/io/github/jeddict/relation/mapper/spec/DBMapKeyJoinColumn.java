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
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.Attribute;

/**
 *
 * @author Gaurav Gupta
 */
public class DBMapKeyJoinColumn extends DBColumn<Attribute> implements DBForeignKey<JoinColumn> {

    private final JoinColumn joinColumn;
    private final List<JoinColumn> joinColumns;

    public DBMapKeyJoinColumn(String name, Attribute attribute) {
        super(name, attribute);
        joinColumns = JoinColumnFinder.findMapKeyJoinColumns(attribute);
        joinColumn = JoinColumnFinder.findMapKeyJoinColumn(name, joinColumns);
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


}
