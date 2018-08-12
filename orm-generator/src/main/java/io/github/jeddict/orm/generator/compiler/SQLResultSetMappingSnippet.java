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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.SQL_RESULTSET_MAPPING;
import static io.github.jeddict.jcode.JPAConstants.SQL_RESULTSET_MAPPING_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;

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

        StringBuilder builder = new StringBuilder(AT);
        builder.append(SQL_RESULTSET_MAPPING)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name))
                .append(buildSnippets("entities", entityResults))
                .append(buildSnippets("classes", constructorResults))
                .append(buildSnippets("columns", columnResults));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
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
