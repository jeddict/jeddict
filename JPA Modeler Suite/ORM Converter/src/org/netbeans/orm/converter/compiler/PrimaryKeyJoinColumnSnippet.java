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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.Collections;
import java.util.List;

public class PrimaryKeyJoinColumnSnippet implements Snippet {

    private String columnDefinition = null;
    private String name = null;
    private String referencedColumnName = null;

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

    public String getSnippet() throws InvalidDataException {

        if (name == null
                && referencedColumnName == null
                && columnDefinition == null) {
            return "@PrimaryKeyJoinColumn";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@PrimaryKeyJoinColumn(");

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

        if (columnDefinition != null) {
            builder.append("columnDefinition=\"");
            builder.append(columnDefinition);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList("javax.persistence.PrimaryKeyJoinColumn");
    }
}
