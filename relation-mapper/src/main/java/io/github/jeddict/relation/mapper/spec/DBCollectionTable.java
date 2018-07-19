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
import java.util.Set;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Index;
import io.github.jeddict.jpa.spec.UniqueConstraint;

public class DBCollectionTable extends DBTable {

    private final ElementCollection attribute;

    public DBCollectionTable(String name, Entity entity, ElementCollection attribute) {
        super(name, entity);
        this.attribute = attribute;
    }

    /**
     * @return the attribute
     */
    public ElementCollection getAttribute() {
        return attribute;
    }

    @Override
    public Set<UniqueConstraint> getUniqueConstraints() {
        return attribute.getCollectionTable().getUniqueConstraint();
    }

    @Override
    public List<Index> getIndexes() {
        return attribute.getCollectionTable().getIndex();
    }
}
