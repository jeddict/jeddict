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

import static io.github.jeddict.jcode.JPAConstants.COLUMN;
import static io.github.jeddict.jcode.JPAConstants.COLUMN_DEFAULT_LENGTH;
import static io.github.jeddict.jcode.JPAConstants.COLUMN_FQN;
import static io.github.jeddict.jcode.JPAConstants.COLUMN_NOSQL_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_COLUMN_FQN;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import static java.util.Collections.singleton;
import static io.github.jeddict.util.StringUtils.isBlank;

public class ColumnSnippet extends ORMSnippet {

    private boolean mapKey;
    private Boolean unique = false;
    private Boolean updatable = true;
    private Boolean insertable = true;
    private Boolean nullable = true;

    private int precision = 0;
    private int scale = 0;
    private int length = COLUMN_DEFAULT_LENGTH;

    private String columnDefinition = null;
    private String table = null;
    private String name = null;

    public ColumnSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public ColumnSnippet() {
    }

    public boolean isEmptyObject() {
        boolean empty = false;
        if (!isGenerateDefaultValue()) {
            if (noSQL) {
                if (isBlank(name)) {
                    empty = true;
                }
            } else {
                if (isBlank(name) && isBlank(table) && isBlank(columnDefinition)
                        && unique == false && updatable == true 
                        && insertable == true && nullable == true
                        && length == COLUMN_DEFAULT_LENGTH 
                        && scale == 0 && precision == 0) {
                    empty = true;
                }
            }
        }
        return empty;
    }

    public Boolean isUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }

    public Boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(Boolean insertable) {
        this.insertable = insertable;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (!noSQL) {
            return annotate(
                    mapKey ? MAP_KEY_COLUMN : COLUMN,
                    attribute("name", name),
                    attribute("table", table),
                    attribute("unique", unique, val -> isGenerateDefaultValue() || val == true),
                    attribute("insertable", insertable, val -> isGenerateDefaultValue() || val == false),
                    attribute("nullable", nullable, val -> isGenerateDefaultValue() || val == false),
                    attribute("updatable", updatable, val -> isGenerateDefaultValue() || val == false),
                    attribute("length", length, val -> isGenerateDefaultValue() || val != COLUMN_DEFAULT_LENGTH),
                    attribute("scale", scale, val -> isGenerateDefaultValue() || val != 0),
                    attribute("precision", precision, val -> isGenerateDefaultValue() || val != 0),
                    attribute("columnDefinition", columnDefinition)
            );
        } else {
            return annotate(
                    COLUMN,
                    attribute(name)
            );
        }
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (noSQL) {
            return singleton(COLUMN_NOSQL_FQN);
        } else {
            return singleton((mapKey ? MAP_KEY_COLUMN_FQN : COLUMN_FQN));
        }
    }
}
