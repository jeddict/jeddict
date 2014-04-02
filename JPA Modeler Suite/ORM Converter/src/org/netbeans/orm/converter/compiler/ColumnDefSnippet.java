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

import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class ColumnDefSnippet implements Snippet {

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

    public boolean isEmptyObject() {
        boolean empty = false;
        if ((name == null || name.trim().isEmpty())
                && (table == null || table.trim().isEmpty())
                && (columnDefinition == null || columnDefinition.trim().isEmpty())
                && unique == false && updatable == true && insertable == true && nullable == true
                && length == 255 && scale == 0 && precision == 0) {
            empty = true;
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

    public String getSnippet() throws InvalidDataException {

        StringBuilder builder = new StringBuilder();

        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append("\",");
        }

        if (columnDefinition != null) {
            builder.append("columnDefinition=\"");
            builder.append(columnDefinition);
            builder.append("\",");
        }

        if (table != null) {
            builder.append("table=\"");
            builder.append(table);
            builder.append("\",");
        }

        if (unique == true) {
            builder.append("unique=true,");
        }

        if (updatable == false) {
            builder.append("updatable=false,");
        }

        if (insertable == false) {
            builder.append("insertable=false,");
        }

        if (nullable == false) {
            builder.append("nullable=false,");
        }

        if (length != 255) {
            builder.append("length=");
            builder.append(length);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (scale != 0) {
            builder.append("scale=");
            builder.append(scale);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (precision != 0) {
            builder.append("precision=");
            builder.append(precision);
            builder.append(ORMConverterUtil.COMMA);
        }
//BUG Resolved : output @Column) if no attribute exist
        return "@Column" + ORMConverterUtil.OPEN_PARANTHESES + (builder.length() > 1 ? builder.substring(0, builder.length() - 1) : "")
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList("javax.persistence.Column");
    }
}
