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

import static io.github.jeddict.jcode.JPAConstants.COLUMN;
import static io.github.jeddict.jcode.JPAConstants.COLUMN_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_COLUMN_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.Collections;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ColumnDefSnippet implements Snippet {

    private boolean mapKey;
    private Boolean unique = false;
    private Boolean updatable = true;
    private Boolean insertable = true;
    private Boolean nullable = true;

    private int precision = 0;
    private int scale = 0;
    private int length = 255;

    private String columnDefinition = null;
    private String table = null;
    private String name = null;

    public ColumnDefSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public ColumnDefSnippet() {
    }

    public boolean isEmptyObject() {
        boolean empty = false;
        if (!isGenerateDefaultValue()) {
            if ((name == null || name.trim().isEmpty())
                    && (table == null || table.trim().isEmpty())
                    && (columnDefinition == null || columnDefinition.trim().isEmpty())
                    && unique == false && updatable == true && insertable == true && nullable == true
                    && length == 255 && scale == 0 && precision == 0) {
                empty = true;
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

        StringBuilder builder = new StringBuilder();

        if (name != null) {
            builder.append("name=\"")
                    .append(name)
                    .append("\",");
        }

        if (table != null) {
            builder.append("table=\"")
                    .append(table)
                    .append("\",");
        }

        if (isGenerateDefaultValue()) {
            if (unique == true) {
                builder.append("unique=true,");
            } else {
                builder.append("unique=false,");
            }
        } else if (unique == true) {
            builder.append("unique=true,");
        }

        if (isGenerateDefaultValue()) {
            if (insertable == true) {
                builder.append("insertable=true,");
            } else {
                builder.append("insertable=false,");
            }
        } else if (insertable == false) {
            builder.append("insertable=false,");
        }

        if (isGenerateDefaultValue()) {
            if (nullable == true) {
                builder.append("nullable=true,");
            } else {
                builder.append("nullable=false,");
            }
        } else if (nullable == false) {
            builder.append("nullable=false,");
        }

        if (isGenerateDefaultValue()) {
            if (updatable == true) {
                builder.append("updatable=true,");
            } else {
                builder.append("updatable=false,");
            }
        } else if (updatable == false) {
            builder.append("updatable=false,");
        }

        if (isGenerateDefaultValue() || length != 255) {
            builder.append("length=")
                    .append(length)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() || scale != 0) {
            builder.append("scale=")
                    .append(scale)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() || precision != 0) {
            builder.append("precision=")
                    .append(precision)
                    .append(COMMA);
        }

        if (columnDefinition != null) {
            builder.append("columnDefinition=\"")
                    .append(columnDefinition)
                    .append("\",");
        }

        return AT + (mapKey ? MAP_KEY_COLUMN : COLUMN)
                + OPEN_PARANTHESES
                + (builder.length() > 1 ? builder.substring(0, builder.length() - 1) : EMPTY)
                + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList((mapKey ? MAP_KEY_COLUMN_FQN : COLUMN_FQN));
    }
}
