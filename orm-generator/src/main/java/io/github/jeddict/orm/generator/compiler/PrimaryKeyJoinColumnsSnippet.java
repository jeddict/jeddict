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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS;
import static io.github.jeddict.jcode.jpa.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS_FQN;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class PrimaryKeyJoinColumnsSnippet implements Snippet {

    private List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = Collections.<PrimaryKeyJoinColumnSnippet>emptyList();
    private ForeignKeySnippet foreignKey;
    private boolean repeatable;

    public PrimaryKeyJoinColumnsSnippet(boolean repeatable) {
        this.repeatable = repeatable;
    }
    
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
        StringBuilder builder = new StringBuilder();
        if (primaryKeyJoinColumns.size() == 1 && foreignKey == null) {
            return primaryKeyJoinColumns.get(0).getSnippet();
        }
                
        boolean isRepeatable = this.repeatable || foreignKey != null; 
        
        if (isRepeatable) {
            builder.append("@").append(PRIMARY_KEY_JOIN_COLUMNS);
            if (foreignKey != null) {
                builder.append("( value={");
            } else {
                builder.append("({");
            }
        }
        
        for (PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn : primaryKeyJoinColumns) {
            builder.append(primaryKeyJoinColumn.getSnippet());
            if(isRepeatable){builder.append(ORMConverterUtil.COMMA);}
        }
        if (isRepeatable) {
            builder.setLength(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }
        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }
        
        if (isRepeatable) {
            builder.setLength(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        }
         return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        boolean isRepeatable = this.repeatable || foreignKey != null; 
        if (primaryKeyJoinColumns.size() == 1) {
            importSnippets.addAll(primaryKeyJoinColumns.get(0).getImportSnippets());
            if(isRepeatable && foreignKey != null){
                importSnippets.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            }
        } else {
            for (PrimaryKeyJoinColumnSnippet jc : primaryKeyJoinColumns) {
                importSnippets.addAll(jc.getImportSnippets());
            }
            if (isRepeatable) {
                importSnippets.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            }
        }

        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        return importSnippets;

    }
}
