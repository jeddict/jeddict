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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.jpa.JPAConstants.SQL_RESULTSET_MAPPING;
import static io.github.jeddict.jcode.jpa.JPAConstants.SQL_RESULTSET_MAPPING_FQN;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

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

        if (name == null) {
            throw new InvalidDataException("name is null");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(SQL_RESULTSET_MAPPING).append("(");

        builder.append("name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (!entityResults.isEmpty()) {
            builder.append("entities={");

            for (EntityResultSnippet entityResult : entityResults) {
                builder.append(entityResult.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!constructorResults.isEmpty()) {
            builder.append("classes={");

            for (ConstructorResultSnippet constructorResult : constructorResults) {
                builder.append(constructorResult.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!columnResults.isEmpty()) {
            builder.append("columns={");

            for (ColumnResultSnippet columnResult : columnResults) {
                builder.append(columnResult.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (entityResults.isEmpty() && constructorResults.isEmpty() && columnResults.isEmpty()) {
            return Collections.singletonList(SQL_RESULTSET_MAPPING_FQN);
        }

        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(SQL_RESULTSET_MAPPING_FQN);
        if (entityResults != null) {
            for (EntityResultSnippet entityResult : entityResults) {
                importSnippets.addAll(entityResult.getImportSnippets());
            }
        }

        if (constructorResults != null) {
            for (ConstructorResultSnippet constructorResult : constructorResults) {
                importSnippets.addAll(constructorResult.getImportSnippets());
            }
        }

        if (columnResults != null) {
            for (ColumnResultSnippet columnResult : columnResults) {
                importSnippets.addAll(columnResult.getImportSnippets());
            }
        }

        return importSnippets;
    }
}
