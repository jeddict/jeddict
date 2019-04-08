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

import static io.github.jeddict.jcode.JPAConstants.ASSOCIATION_OVERRIDE;
import static io.github.jeddict.jcode.JPAConstants.ASSOCIATION_OVERRIDE_FQN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssociationOverrideSnippet implements Snippet {

    private String name = null;
    private List<JoinColumnSnippet> joinColumns = Collections.<JoinColumnSnippet>emptyList();
    private JoinTableSnippet joinTable;
    private ForeignKeySnippet foreignKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addJoinColumn(JoinColumnSnippet joinColumn) {

        if (joinColumns.isEmpty()) {
            joinColumns = new ArrayList<>();
        }

        joinColumns.add(joinColumn);
    }

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (name == null || joinColumns == null) {
            throw new InvalidDataException("Name and JoinColumns required");
        }

        return annotate(
                ASSOCIATION_OVERRIDE,
                attribute("name", name),
                attributes("joinColumns", joinColumns),
                attribute("joinTable", joinTable),
                attribute("foreignKey", foreignKey)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();

        imports.add(ASSOCIATION_OVERRIDE_FQN);
        if (joinColumns != null && !joinColumns.isEmpty()) {
            imports.addAll(joinColumns.get(0).getImportSnippets());
        }
        if (joinTable != null) {
            imports.addAll(joinTable.getImportSnippets());
        }
        
        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }

        return imports;
    }

    /**
     * @return the joinTable
     */
    public JoinTableSnippet getJoinTable() {
        return joinTable;
    }

    /**
     * @param joinTable the joinTable to set
     */
    public void setJoinTable(JoinTableSnippet joinTable) {
        this.joinTable = joinTable;
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
}
