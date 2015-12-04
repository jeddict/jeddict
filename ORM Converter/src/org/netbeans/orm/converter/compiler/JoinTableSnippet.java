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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class JoinTableSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<JoinColumnSnippet> inverseJoinColumns = Collections.EMPTY_LIST;
    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;

    private UniqueConstraintSnippet uniqueConstraint = null;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<JoinColumnSnippet> getInverseJoinColumns() {
        return inverseJoinColumns;
    }

    public void setInverseJoinColumns(List<JoinColumnSnippet> inverseJoinColumns) {
        if (inverseJoinColumns != null) {
            this.inverseJoinColumns = inverseJoinColumns;
        }
    }

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    public UniqueConstraintSnippet getUniqueConstraint() {
        return uniqueConstraint;
    }

    public void setUniqueConstraints(UniqueConstraintSnippet uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    public boolean isEmpty() {
        return (catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && joinColumns.isEmpty()
                && inverseJoinColumns.isEmpty()
                && uniqueConstraint == null;
    }

    public String getSnippet() throws InvalidDataException {

        if ((catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && inverseJoinColumns.isEmpty()
                && joinColumns.isEmpty()
                && uniqueConstraint == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@JoinTable(");

        if (name != null && !name.trim().isEmpty()) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (schema != null && !schema.trim().isEmpty()) {
            builder.append("schema=\"");
            builder.append(schema);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (catalog != null && !catalog.trim().isEmpty()) {
            builder.append("catalog=\"");
            builder.append(catalog);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (uniqueConstraint != null) {
            builder.append("uniqueConstraints=");

            builder.append(uniqueConstraint.getSnippet());

            builder.append(ORMConverterUtil.COMMA);
        }

        if (!joinColumns.isEmpty()) {
            builder.append("joinColumns={");

            for (JoinColumnSnippet joinColumn : joinColumns) {
                builder.append(joinColumn.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!inverseJoinColumns.isEmpty()) {
            builder.append("inverseJoinColumns={");

            for (JoinColumnSnippet joinColumn : inverseJoinColumns) {
                builder.append(joinColumn.getSnippet());
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

        if (isEmpty()) {
            return new ArrayList<String>();
        }

        if (inverseJoinColumns.isEmpty()
                && joinColumns.isEmpty()
                && uniqueConstraint == null) {

            return Collections.singletonList("javax.persistence.JoinTable");
        }

        Collection<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.JoinTable");

        if (!joinColumns.isEmpty()) {
            Collection<String> joinColumnSnippets
                    = joinColumns.get(0).getImportSnippets();

            importSnippets.addAll(joinColumnSnippets);
        }

        if (uniqueConstraint != null) {
            Collection<String> uniqueConstraintSnippets
                    = uniqueConstraint.getImportSnippets();

            importSnippets.addAll(uniqueConstraintSnippets);
        }

        return importSnippets;
    }
}
