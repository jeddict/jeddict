/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        return annotate(
                JOIN_TABLE,
                attribute("name", name),
                attribute("schema", schema),
                attribute("catalog", catalog),
                attributes("uniqueConstraints", uniqueConstraints),
                attributes("indexes", indices),
                attributes("joinColumns", joinColumns),
                attributes("inverseJoinColumns", inverseJoinColumns),
                attribute("foreignKey", foreignKey),
                attribute("inverseForeignKey", inverseForeignKey)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (isEmpty()) {
            return emptySet();
        }

        if (inverseJoinColumns.isEmpty() && joinColumns.isEmpty() && uniqueConstraints == null
                && foreignKey == null && inverseForeignKey == null ) {
            return singleton(JOIN_TABLE_FQN);
        }

        Set<String> imports = new HashSet<>();
        
        imports.add(JOIN_TABLE_FQN);

        if (!joinColumns.isEmpty()) {
            Collection<String> joinColumnSnippets  = joinColumns.get(0).getImportSnippets();
            imports.addAll(joinColumnSnippets);
        }

         if (!uniqueConstraints.isEmpty()) {
             imports.addAll(uniqueConstraints.get(0).getImportSnippets());
        }
         
         if (!indices.isEmpty()) {
             imports.addAll(indices.get(0).getImportSnippets());
        }
        
        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }
        
        if (inverseForeignKey != null) {
            imports.addAll(inverseForeignKey.getImportSnippets());
        }

        return imports;
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
