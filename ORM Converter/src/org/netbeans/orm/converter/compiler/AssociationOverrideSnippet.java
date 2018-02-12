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
import static org.netbeans.jcode.jpa.JPAConstants.ASSOCIATION_OVERRIDE;
import static org.netbeans.jcode.jpa.JPAConstants.ASSOCIATION_OVERRIDE_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

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

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(ASSOCIATION_OVERRIDE).append("(");

        builder.append("name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);

        if (!joinColumns.isEmpty()) {
            builder.append(ORMConverterUtil.COMMA);
            builder.append("joinColumns={");
            for (JoinColumnSnippet joinColumn : joinColumns) {
                builder.append(joinColumn.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
        }

        if (joinTable != null && joinTable.getSnippet() != null) {
            builder.append(ORMConverterUtil.COMMA);
            builder.append("joinTable=");
            builder.append(joinTable.getSnippet());
        }
        
        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);

        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(ASSOCIATION_OVERRIDE_FQN);
        if (joinColumns != null && !joinColumns.isEmpty()) {
            importSnippets.addAll(joinColumns.get(0).getImportSnippets());
        }
        if (joinTable != null) {
            importSnippets.addAll(joinTable.getImportSnippets());
        }
        
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        return importSnippets;
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
