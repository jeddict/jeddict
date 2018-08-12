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

import static io.github.jeddict.jcode.JPAConstants.SECONDARY_TABLE;
import static io.github.jeddict.jcode.JPAConstants.SECONDARY_TABLE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;

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

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name)) {
            throw new InvalidDataException("Missing required field name");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(SECONDARY_TABLE)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name))
                .append(buildString("schema", schema))
                .append(buildString("catalog", catalog))
                .append(buildSnippets("pkJoinColumns", primaryKeyJoinColumns))
                .append(buildSnippets("uniqueConstraints", uniqueConstraints))
                .append(buildSnippets("indexes", indices))
                .append(buildSnippet("foreignKey", foreignKey));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (isBlank(name)) {
            return emptySet();
        }

        Set<String> imports = new HashSet<>();
        imports.add(SECONDARY_TABLE_FQN);

        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {
            Collection<String> columnsImportSnippets = primaryKeyJoinColumns.get(0).getImportSnippets();
            imports.addAll(columnsImportSnippets);
        }

        if (uniqueConstraints != null && !uniqueConstraints.isEmpty()) {
            imports.addAll(uniqueConstraints.get(0).getImportSnippets());
        }

        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }

        if (!indices.isEmpty()) {
            imports.addAll(indices.get(0).getImportSnippets());
        }
        return imports;
    }
}
