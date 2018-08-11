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

import static io.github.jeddict.jcode.JPAConstants.TABLE_GENERATOR;
import static io.github.jeddict.jcode.JPAConstants.TABLE_GENERATOR_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TableGeneratorSnippet implements Snippet {

    private int allocationSize = 50;
    private int initialValue = 0;

    private String name;
    private String catalog = null;
    private String pkColumnName = null;
    private String pkColumnValue = null;
    private String schema = null;
    private String table = null;
    private String valueColumnName = null;

    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.<UniqueConstraintSnippet>emptyList();
    private List<IndexSnippet> indices = Collections.<IndexSnippet>emptyList();

    public int getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(int allocationSize) {
        this.allocationSize = allocationSize;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

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

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    public String getPkColumnValue() {
        return pkColumnValue;
    }

    public void setPkColumnValue(String pkColumnValue) {
        this.pkColumnValue = pkColumnValue;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<UniqueConstraintSnippet> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public void setUniqueConstraints(List<UniqueConstraintSnippet> uniqueConstraints) {
        if (uniqueConstraints != null) {
            this.uniqueConstraints = uniqueConstraints;
        }
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name)) {
            throw new InvalidDataException("Name is required");
        }

        StringBuilder builder = new StringBuilder(AT);

        builder.append(TABLE_GENERATOR)
                .append(OPEN_PARANTHESES);

        builder.append("name=")
                .append(QUOTE)
                .append(name)
                .append(QUOTE)
                .append(COMMA);

        if (isNotBlank(pkColumnValue)) {
            builder.append("pkColumnValue=")
                    .append(QUOTE)
                    .append(pkColumnValue)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(schema)) {
            builder.append("schema=")
                    .append(QUOTE)
                    .append(schema)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(table)) {
            builder.append("table=")
                    .append(QUOTE)
                    .append(table)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(valueColumnName)) {
            builder.append("valueColumnName=")
                    .append(QUOTE)
                    .append(valueColumnName)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(catalog)) {
            builder.append("catalog=")
                    .append(QUOTE)
                    .append(catalog)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(pkColumnName)) {
            builder.append("pkColumnName=")
                    .append(QUOTE)
                    .append(pkColumnName)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() || allocationSize != 50) {
            builder.append("allocationSize=")
                    .append(allocationSize)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() || initialValue != 0) {
            builder.append("initialValue=")
                    .append(initialValue)
                    .append(COMMA);
        }

        if (!uniqueConstraints.isEmpty()) {
            builder.append("uniqueConstraints={");

            for (UniqueConstraintSnippet uniqueConstraint : uniqueConstraints) {
                builder.append(uniqueConstraint.getSnippet())
                        .append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);

            builder.append(CLOSE_BRACES)
                    .append(COMMA);
        }

        if (!indices.isEmpty()) {
            builder.append("indexes=")
                    .append(OPEN_BRACES);

            for (IndexSnippet snippet : indices) {
                builder.append(snippet.getSnippet())
                        .append(COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(CLOSE_BRACES)
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;

    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (uniqueConstraints.isEmpty() && indices.isEmpty()) {
            return singleton(TABLE_GENERATOR_FQN);
        }

        Set<String> importSnippets = new HashSet<>();
        importSnippets.add(TABLE_GENERATOR_FQN);
        if (!uniqueConstraints.isEmpty()) {
            importSnippets.addAll(uniqueConstraints.get(0).getImportSnippets());
        }
        if (!indices.isEmpty()) {
            importSnippets.addAll(indices.get(0).getImportSnippets());
        }
        return importSnippets;
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
