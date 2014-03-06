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

public class JoinColumnSnippet implements Snippet {

    private boolean insertable = true;
    private boolean nullable = true;
    private boolean unique = false;
    private boolean updatable = true;

    private String columnDefinition = null;
    private String name = null;
    private String referencedColumnName = null;
    private String table = null;

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

    public String getSnippet() throws InvalidDataException {

        if (insertable == true
                && nullable == true
                && unique == false
                && updatable == true
                && columnDefinition == null
                && name == null
                && referencedColumnName == null
                && table == null) {

            return "@JoinColumn";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@JoinColumn(");

        if (columnDefinition != null && !columnDefinition.trim().isEmpty()) {
            builder.append("columnDefinition=\"");
            builder.append(columnDefinition);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (referencedColumnName != null) {
            builder.append("referencedColumnName=\"");
            builder.append(referencedColumnName);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (table != null && !table.trim().isEmpty()) {
            builder.append("table=\"");
            builder.append(table);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

//        if (insertable == true) {
        builder.append("insertable=");
        builder.append(insertable);
        builder.append(ORMConverterUtil.COMMA);
//        }

//        if (nullable == true) {
        builder.append("nullable=");
        builder.append(nullable);
        builder.append(ORMConverterUtil.COMMA);
//        }

//        if (unique == false) {
        builder.append("unique=");
        builder.append(unique);
        builder.append(ORMConverterUtil.COMMA);
//        }

//        if (updatable == true) {
        builder.append("updatable=");
        builder.append(updatable);
        builder.append(ORMConverterUtil.COMMA);
//        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList("javax.persistence.JoinColumn");
    }
}
