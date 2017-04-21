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
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMN;
import static org.netbeans.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMN_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class PrimaryKeyJoinColumnSnippet implements Snippet {

    private String columnDefinition = null;
    private String name = null;
    private String referencedColumnName = null;

    private ForeignKeySnippet foreignKey;
    
    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
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

        if (name == null
                && referencedColumnName == null
                && columnDefinition == null) {
            return "@" + PRIMARY_KEY_JOIN_COLUMN;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(PRIMARY_KEY_JOIN_COLUMN).append("(");

        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (referencedColumnName != null && !referencedColumnName.trim().isEmpty()) {
            builder.append("referencedColumnName=\"");
            builder.append(referencedColumnName);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (columnDefinition != null) {
            builder.append("columnDefinition=\"");
            builder.append(columnDefinition);
            builder.append(ORMConverterUtil.QUOTE);
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
    public List<String> getImportSnippets() throws InvalidDataException {
       List<String> importSnippets = new ArrayList<>();
       importSnippets.add(PRIMARY_KEY_JOIN_COLUMN_FQN);
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        return importSnippets;
    }
}
