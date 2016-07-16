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
import static org.netbeans.jcode.jpa.JPAConstants.JOIN_COLUMNS;
import static org.netbeans.jcode.jpa.JPAConstants.JOIN_COLUMNS_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_JOIN_COLUMNS;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_JOIN_COLUMNS_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class JoinColumnsSnippet implements Snippet {

    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;
    private ForeignKeySnippet foreignKey;
    private boolean mapKey;
    
    public JoinColumnsSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public JoinColumnsSnippet() {
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
        StringBuilder builder = new StringBuilder("@");
        if (mapKey) {
            builder.append(MAP_KEY_JOIN_COLUMNS);
        } else {
            builder.append(JOIN_COLUMNS);
        }
        
        if (joinColumns.isEmpty()) {
            return builder.toString();
        }

        if (joinColumns.size() == 1) {
            return joinColumns.get(0).getSnippet();
        }

        builder.append("({");

        for (JoinColumnSnippet joinColumn : joinColumns) {
            builder.append(joinColumn.getSnippet());
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
        if (joinColumns.isEmpty()) {
            if (mapKey) {
                importSnippets.add(MAP_KEY_JOIN_COLUMNS_FQN);
            } else {
                importSnippets.add(JOIN_COLUMNS_FQN);
            }
        } else if (joinColumns.size() == 1) {
            importSnippets.addAll(joinColumns.get(0).getImportSnippets());
        } else {
            for (JoinColumnSnippet jc : joinColumns) {
                importSnippets.addAll(jc.getImportSnippets());
            }
            if (mapKey) {
                importSnippets.add(MAP_KEY_JOIN_COLUMNS_FQN);
            } else {
                importSnippets.add(JOIN_COLUMNS_FQN);
            }
        }

        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
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
}
