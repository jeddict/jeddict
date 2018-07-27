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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.JPAConstants.SECONDARY_TABLE;
import static io.github.jeddict.jcode.JPAConstants.SECONDARY_TABLE_FQN;
import io.github.jeddict.orm.generator.util.ImportSet;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class SecondaryTableSnippet implements Snippet {

    private String name = null;
    private String catalog = null;
    private String schema = null;
    private ForeignKeySnippet foreignKey;

    private List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = Collections.<PrimaryKeyJoinColumnSnippet>emptyList();
    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.<UniqueConstraintSnippet>emptyList();
    private List<IndexSnippet> indices = Collections.<IndexSnippet>emptyList();

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

    @Override
    public String getSnippet() throws InvalidDataException {

        if (name == null) {
            throw new InvalidDataException("Missing required field name");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(SECONDARY_TABLE).append("(name=\"");
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
        
                if (!indices.isEmpty()) {
            builder.append("indexes={");

            for (IndexSnippet snippet : indices) {
                builder.append(snippet.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }
        
        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (name == null) {
            return Collections.<String>emptyList();
        }

        ImportSet importSnippets = new ImportSet();

        importSnippets.add(SECONDARY_TABLE_FQN);

        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {
            Collection<String> columnsImportSnippets = primaryKeyJoinColumns.get(0).getImportSnippets();
            importSnippets.addAll(columnsImportSnippets);
        }

        if (uniqueConstraints != null && !uniqueConstraints.isEmpty()) {
            importSnippets.addAll(uniqueConstraints.get(0).getImportSnippets());
        }
        
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        if (!indices.isEmpty()) {
            importSnippets.addAll(indices.get(0).getImportSnippets());
        }
        return importSnippets;
    }

    /**
     * @return the foreignKey
     */
    public ForeignKeySnippet getForeignKey() {
        return foreignKey;
    }

    /**
     * @param foreignKey the foreignKey to set
     */
    public void setForeignKey(ForeignKeySnippet foreignKey) {
        this.foreignKey = foreignKey;
    }
    
        /**
     * @return the indices
     */
    public List<IndexSnippet> getIndices() {
        return indices;
    }

    /**
     * @param indices the indices to set
     */
    public void setIndices(List<IndexSnippet> indices) {
        this.indices = indices;
    }
}
