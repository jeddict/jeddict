/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SQLResultSetMappingSnippet implements Snippet {

    private String name = null;

    private List<EntityResultSnippet> entityResults = Collections.EMPTY_LIST;
    private List<ColumnResultSnippet> columnResults = Collections.EMPTY_LIST;

    public void addColumnResult(ColumnResultSnippet columnResult) {

        if (columnResults.isEmpty()) {
            columnResults = new ArrayList<ColumnResultSnippet>();
        }

        columnResults.add(columnResult);
    }

    public void addEntityResult(EntityResultSnippet entityResult) {

        if (entityResults.isEmpty()) {
            entityResults = new ArrayList<EntityResultSnippet>();
        }

        entityResults.add(entityResult);
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

    public List<ColumnResultSnippet> getColumnResults() {
        return columnResults;
    }

    public void setColumnResults(List<ColumnResultSnippet> columnResults) {
        if (columnResults != null) {
            this.columnResults = columnResults;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null) {
            throw new InvalidDataException("name is null");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@SqlResultSetMapping(");

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

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (entityResults.isEmpty() && columnResults.isEmpty()) {

            return Collections.singletonList(""
                    + "javax.persistence.SqlResultSetMapping");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.SqlResultSetMapping");

        if (entityResults != null) {
            for (EntityResultSnippet entityResult : entityResults) {
                importSnippets.addAll(entityResult.getImportSnippets());
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
