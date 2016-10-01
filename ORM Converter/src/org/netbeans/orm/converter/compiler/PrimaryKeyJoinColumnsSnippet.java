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
import java.util.Collections;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS;
import static org.netbeans.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class PrimaryKeyJoinColumnsSnippet implements Snippet {

    private List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = Collections.EMPTY_LIST;
    private ForeignKeySnippet foreignKey;

    public void addPrimaryKeyJoinColumn(PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn) {
        if (primaryKeyJoinColumns.isEmpty()) {
            primaryKeyJoinColumns = new ArrayList<>();
        }
        primaryKeyJoinColumns.add(primaryKeyJoinColumn);
    }
    
    public List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns() {
        return primaryKeyJoinColumns;
    }

    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns) {
        if (primaryKeyJoinColumns != null) {
            this.primaryKeyJoinColumns = primaryKeyJoinColumns;
        }
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

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder("@");
        builder.append(PRIMARY_KEY_JOIN_COLUMNS);

        if (primaryKeyJoinColumns.isEmpty()) {
            return builder.toString();
        }

        if (primaryKeyJoinColumns.size() == 1 && foreignKey == null) {
            return primaryKeyJoinColumns.get(0).getSnippet();
        }

        builder.append("( value={");

        for (PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn : primaryKeyJoinColumns) {
            builder.append(primaryKeyJoinColumn.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        builder.deleteCharAt(builder.length() - 1);

        builder.append(ORMConverterUtil.CLOSE_BRACES);
        builder.append(ORMConverterUtil.COMMA);

        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (primaryKeyJoinColumns.isEmpty()) {
            importSnippets.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
        } else if (primaryKeyJoinColumns.size() == 1) {
            importSnippets.addAll(primaryKeyJoinColumns.get(0).getImportSnippets());
            if(foreignKey != null){
                importSnippets.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            }
        } else {
            importSnippets.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            for (PrimaryKeyJoinColumnSnippet jc : primaryKeyJoinColumns) {
                importSnippets.addAll(jc.getImportSnippets());
            }
        }

        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        return importSnippets;

    }
}
