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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.SQL_RESULTSET_MAPPING;
import static io.github.jeddict.jcode.JPAConstants.SQL_RESULTSET_MAPPING_FQN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

public class SQLResultSetMappingSnippet implements Snippet {

    private String name = null;

    private List<EntityResultSnippet> entityResults = Collections.<EntityResultSnippet>emptyList();
    private List<ConstructorResultSnippet> constructorResults = Collections.<ConstructorResultSnippet>emptyList();
    private List<ColumnResultSnippet> columnResults = Collections.<ColumnResultSnippet>emptyList();

    public void addColumnResult(ColumnResultSnippet columnResult) {

        if (columnResults.isEmpty()) {
            columnResults = new ArrayList<>();
        }

        columnResults.add(columnResult);
    }

    public void addEntityResult(EntityResultSnippet entityResult) {

        if (entityResults.isEmpty()) {
            entityResults = new ArrayList<>();
        }

        entityResults.add(entityResult);
    }

    public void addConstructorResult(ConstructorResultSnippet constructorResult) {

        if (constructorResults.isEmpty()) {
            constructorResults = new ArrayList<>();
        }

        constructorResults.add(constructorResult);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EntityResultSnippet> getEntityResults() {
        return entityResults;
    }

    public void setEntityResults(List<EntityResultSnippet> entityResults) {
        if (entityResults != null) {
            this.entityResults = entityResults;
        }
    }

    public List<ConstructorResultSnippet> getConstructorResults() {
        return constructorResults;
    }

    public void setConstructorResults(List<ConstructorResultSnippet> constructorResults) {
        if (constructorResults != null) {
            this.constructorResults = constructorResults;
        }
    }

    public List<ColumnResultSnippet> getColumnResults() {
        return columnResults;
    }

    public void setColumnResults(List<ColumnResultSnippet> columnResults) {
        if (columnResults != null) {
            this.columnResults = columnResults;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name)) {
            throw new InvalidDataException("name is null");
        }
        return annotate(
                SQL_RESULTSET_MAPPING,
                attribute("name", name),
                attributes("entities", entityResults),
                attributes("classes", constructorResults),
                attributes("columns", columnResults)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (entityResults.isEmpty()
                && constructorResults.isEmpty()
                && columnResults.isEmpty()) {
            return singleton(SQL_RESULTSET_MAPPING_FQN);
        }

        Set<String> imports = new HashSet<>();
        imports.add(SQL_RESULTSET_MAPPING_FQN);
        if (entityResults != null) {
            for (EntityResultSnippet entityResult : entityResults) {
                imports.addAll(entityResult.getImportSnippets());
            }
        }

        if (constructorResults != null) {
            for (ConstructorResultSnippet constructorResult : constructorResults) {
                imports.addAll(constructorResult.getImportSnippets());
            }
        }

        if (columnResults != null) {
            for (ColumnResultSnippet columnResult : columnResults) {
                imports.addAll(columnResult.getImportSnippets());
            }
        }

        return imports;
    }
}
