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

public class SecondaryTableSnippet implements Snippet {

    private String name = null;
    private String catalog = null;
    private String schema = null;

    private List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns
            = Collections.EMPTY_LIST;

    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.EMPTY_LIST;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns() {
        return primaryKeyJoinColumns;
    }

    public void setPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns) {
        if (primaryKeyJoinColumns != null) {
            this.primaryKeyJoinColumns = primaryKeyJoinColumns;
        }
    }

    public List<UniqueConstraintSnippet> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public void setUniqueConstraints(List<UniqueConstraintSnippet> uniqueConstraints) {
        this.uniqueConstraints = uniqueConstraints;
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null) {
            throw new InvalidDataException("Missing required feild name");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@SecondaryTable(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (schema != null) {
            builder.append("schema=\"");
            builder.append(schema);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (catalog != null) {
            builder.append("catalog=\"");
            builder.append(catalog);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {
            builder.append("pkJoinColumns={");

            for (PrimaryKeyJoinColumnSnippet pkJoinColumn : primaryKeyJoinColumns) {
                builder.append(pkJoinColumn.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (uniqueConstraints != null && !uniqueConstraints.isEmpty()) {
            builder.append("uniqueConstraints={");

            for (UniqueConstraintSnippet uniqueConstraint : uniqueConstraints) {
                builder.append(uniqueConstraint.getSnippet());
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

        if (name == null) {
            return Collections.EMPTY_LIST;
        }

        Collection<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.SecondaryTable");

        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {

            Collection<String> columnsImportSnippets
                    = primaryKeyJoinColumns.get(0).getImportSnippets();

            importSnippets.addAll(columnsImportSnippets);
        }

        if (uniqueConstraints != null && !uniqueConstraints.isEmpty()) {

            Collection<String> ucImportSnippets
                    = uniqueConstraints.get(0).getImportSnippets();

            importSnippets.addAll(ucImportSnippets);
        }

        return importSnippets;
    }
}
