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

import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMN_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMN_FQN;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        return annotate(
                mapKey ? MAP_KEY_JOIN_COLUMN : JOIN_COLUMN,
                attribute("name", name),
                attribute("referencedColumnName", referencedColumnName),
                attribute("table", table),
                attribute("unique", unique, val -> isGenerateDefaultValue() || val == true),
                attribute("insertable", insertable, val -> isGenerateDefaultValue() || val == false),
                attribute("nullable", nullable, val -> isGenerateDefaultValue() || val == false),
                attribute("updatable", updatable, val -> isGenerateDefaultValue() || val == false),
                attribute("foreignKey", foreignKey),
                attribute("columnDefinition", columnDefinition)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (mapKey) {
            imports.add(MAP_KEY_JOIN_COLUMN_FQN);
        } else {
            imports.add(JOIN_COLUMN_FQN);
        }
        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }
        return imports;
    }

}
