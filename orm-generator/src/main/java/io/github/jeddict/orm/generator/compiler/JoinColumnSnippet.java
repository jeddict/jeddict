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

import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMN_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMN_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class JoinColumnSnippet implements Snippet {

    private boolean mapKey;
    
    private boolean insertable = true;
    private boolean nullable = true;
    private boolean unique = false;
    private boolean updatable = true;

    private String columnDefinition = null;
    private String name = null;
    private String referencedColumnName = null;
    private String table = null;
    
    private ForeignKeySnippet foreignKey;
    
    
    public JoinColumnSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public JoinColumnSnippet() {
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        if (mapKey) {
            builder.append(MAP_KEY_JOIN_COLUMN);
        } else {
            builder.append(JOIN_COLUMN);
        }
        
        if (!isGenerateDefaultValue()) {
            if (insertable == true
                    && nullable == true
                    && unique == false
                    && updatable == true
                    && columnDefinition == null
                    && name == null
                    && referencedColumnName == null
                    && table == null 
                    && foreignKey==null) {
                return builder.toString();
            }
        }

        builder.append(OPEN_PARANTHESES);

        if (isNotBlank(name)) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(QUOTE);
            builder.append(COMMA);
        }

        if (isNotBlank(referencedColumnName)) {
            builder.append("referencedColumnName=\"");
            builder.append(referencedColumnName);
            builder.append(QUOTE);
            builder.append(COMMA);
        }

        if (isNotBlank(table)) {
            builder.append("table=\"");
            builder.append(table);
            builder.append(QUOTE);
            builder.append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append("unique=");
            builder.append(unique);
            builder.append(COMMA);
        } else if (unique == true) {
            builder.append("unique=");
            builder.append(unique);
            builder.append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append("insertable=");
            builder.append(insertable);
            builder.append(COMMA);
        } else if (insertable == false) {
            builder.append("insertable=");
            builder.append(insertable);
            builder.append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append("nullable=");
            builder.append(nullable);
            builder.append(COMMA);
        } else if (nullable == false) {
            builder.append("nullable=");
            builder.append(nullable);
            builder.append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append("updatable=");
            builder.append(updatable);
            builder.append(COMMA);
        } else if (updatable == false) {
            builder.append("updatable=");
            builder.append(updatable);
            builder.append(COMMA);
        }
        
        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(COMMA);
        }

        if (isNotBlank(columnDefinition)) {
            builder.append("columnDefinition=\"");
            builder.append(columnDefinition);
            builder.append(QUOTE);
            builder.append(COMMA);
        }
        
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (mapKey) {
            importSnippets.add(MAP_KEY_JOIN_COLUMN_FQN);
        } else {
            importSnippets.add(JOIN_COLUMN_FQN);
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
