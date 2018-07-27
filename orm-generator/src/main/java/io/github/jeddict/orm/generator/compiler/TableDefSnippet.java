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
import static io.github.jeddict.jcode.JPAConstants.TABLE;
import static io.github.jeddict.jcode.JPAConstants.TABLE_FQN;
import io.github.jeddict.orm.generator.util.ImportSet;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class TableDefSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.<UniqueConstraintSnippet>emptyList();
    private List<IndexSnippet> indices = Collections.<IndexSnippet>emptyList();

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

    public List<UniqueConstraintSnippet> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public void setUniqueConstraints(List<UniqueConstraintSnippet> uniqueConstraints) {
        this.uniqueConstraints = uniqueConstraints;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (name == null
                && catalog == null
                && schema == null
                && uniqueConstraints.isEmpty() && indices.isEmpty()) {
            return "@" + TABLE;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(TABLE).append("(");

        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

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

        if (!uniqueConstraints.isEmpty()) {
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

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;

    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (uniqueConstraints == null) {
            return Collections.singletonList(TABLE_FQN);
        }

        ImportSet importSnippets = new ImportSet();

        importSnippets.add(TABLE_FQN);
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
