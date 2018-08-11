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

import static io.github.jeddict.jcode.JPAConstants.JOIN_TABLE;
import static io.github.jeddict.jcode.JPAConstants.JOIN_TABLE_FQN;
import io.github.jeddict.orm.generator.util.ImportSet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JoinTableSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<JoinColumnSnippet> inverseJoinColumns = Collections.<JoinColumnSnippet>emptyList();
    private List<JoinColumnSnippet> joinColumns = Collections.<JoinColumnSnippet>emptyList();
    
    private ForeignKeySnippet foreignKey;
    private ForeignKeySnippet inverseForeignKey;
    

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

    public List<UniqueConstraintSnippet> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public void setUniqueConstraints(List<UniqueConstraintSnippet> uniqueConstraints) {
        this.uniqueConstraints = uniqueConstraints;
    }

    public boolean isEmpty() {
        return (catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && inverseJoinColumns.isEmpty()
                && joinColumns.isEmpty()
                && uniqueConstraints.isEmpty() && indices.isEmpty();
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(AT).append(JOIN_TABLE).append(OPEN_PARANTHESES);

        builder.append(buildString("name", name));
        builder.append(buildString("schema", schema));
        builder.append(buildString("catalog", catalog));

        builder.append(buildAnnotations("uniqueConstraints", uniqueConstraints));
        builder.append(buildAnnotations("indexes", indices));
        builder.append(buildAnnotations("joinColumns", joinColumns));
        builder.append(buildAnnotations("inverseJoinColumns", inverseJoinColumns));

        builder.append(buildAnnotation("foreignKey", foreignKey));
        builder.append(buildAnnotation("inverseForeignKey", inverseForeignKey));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (isEmpty()) {
            return new ArrayList<>();
        }

        if (inverseJoinColumns.isEmpty() && joinColumns.isEmpty() && uniqueConstraints == null
                && foreignKey == null && inverseForeignKey == null ) {
            return Collections.singletonList(JOIN_TABLE_FQN);
        }

        ImportSet importSnippets = new ImportSet();
        
        importSnippets.add(JOIN_TABLE_FQN);

        if (!joinColumns.isEmpty()) {
            Collection<String> joinColumnSnippets  = joinColumns.get(0).getImportSnippets();
            importSnippets.addAll(joinColumnSnippets);
        }

         if (!uniqueConstraints.isEmpty()) {
            importSnippets.addAll(uniqueConstraints.get(0).getImportSnippets());
        }
         
         if (!indices.isEmpty()) {
            importSnippets.addAll(indices.get(0).getImportSnippets());
        }
        
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }
        
        if (inverseForeignKey != null) {
            importSnippets.addAll(inverseForeignKey.getImportSnippets());
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
     * @return the inverseForeignKey
     */
    public ForeignKeySnippet getInverseForeignKey() {
        return inverseForeignKey;
    }

    /**
     * @param inverseForeignKey the inverseForeignKey to set
     */
    public void setInverseForeignKey(ForeignKeySnippet inverseForeignKey) {
        this.inverseForeignKey = inverseForeignKey;
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
