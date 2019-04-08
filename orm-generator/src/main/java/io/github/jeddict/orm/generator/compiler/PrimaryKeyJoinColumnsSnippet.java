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

import static io.github.jeddict.jcode.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS;
import static io.github.jeddict.jcode.JPAConstants.PRIMARY_KEY_JOIN_COLUMNS_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            builder.append(AT)
                    .append(PRIMARY_KEY_JOIN_COLUMNS)
                    .append(OPEN_PARANTHESES);
            if (foreignKey != null) {
                builder.append("value={");
            } else {
                builder.append("{");
            }
        }
        
        for (PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn : primaryKeyJoinColumns) {
            builder.append(primaryKeyJoinColumn.getSnippet());
            if (isRepeatable) {
                builder.append(COMMA);
            }
        }
        if (isRepeatable) {
            builder.setLength(builder.length() - 1);
            builder.append(CLOSE_BRACES);
            builder.append(COMMA);
        }

        builder.append(attribute("foreignKey", foreignKey));

        if (isRepeatable) {
            builder.setLength(builder.length() - 1);
            builder.append(CLOSE_PARANTHESES);
        }
         return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        boolean isRepeatable = this.repeatable || foreignKey != null; 
        if (primaryKeyJoinColumns.size() == 1) {
            imports.addAll(primaryKeyJoinColumns.get(0).getImportSnippets());
            if(isRepeatable && foreignKey != null){
                imports.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            }
        } else {
            for (PrimaryKeyJoinColumnSnippet jc : primaryKeyJoinColumns) {
                imports.addAll(jc.getImportSnippets());
            }
            if (isRepeatable) {
                imports.add(PRIMARY_KEY_JOIN_COLUMNS_FQN);
            }
        }

        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }

        return imports;
    }
}
