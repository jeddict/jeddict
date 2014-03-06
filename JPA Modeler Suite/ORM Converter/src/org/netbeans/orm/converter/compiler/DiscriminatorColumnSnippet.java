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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.jpa.modeler.spec.DiscriminatorType;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class DiscriminatorColumnSnippet implements Snippet {

    private String name = null;
    private DiscriminatorType discriminatorType = null;
    private String columnDefinition = null;
    private int length = 30;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiscriminatorType getDiscriminatorType() {
        return discriminatorType;
    }

    public void setDiscriminatorType(DiscriminatorType discriminatorType) {
        this.discriminatorType = discriminatorType;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSnippet() throws InvalidDataException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("@DiscriminatorColumn(");

        stringBuilder.append("length=");
        stringBuilder.append(length);
        stringBuilder.append(ORMConverterUtil.COMMA);

        if (name != null && !name.isEmpty()) {
            stringBuilder.append("name=\"");
            stringBuilder.append(name);
            stringBuilder.append(ORMConverterUtil.QUOTE);
            stringBuilder.append(ORMConverterUtil.COMMA);
        }

        if (discriminatorType != null) {
            stringBuilder.append("discriminatorType=DiscriminatorType.");
            stringBuilder.append(discriminatorType);
            stringBuilder.append(ORMConverterUtil.COMMA);
        }

        if (columnDefinition != null && !columnDefinition.isEmpty()) {
            stringBuilder.append("columnDefinition=\"");
            stringBuilder.append(columnDefinition);
            stringBuilder.append(ORMConverterUtil.QUOTE);
            stringBuilder.append(ORMConverterUtil.COMMA);
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;

    }

    public boolean isDefault() {
        if ((name == null || name.isEmpty()) && (columnDefinition == null || columnDefinition.isEmpty()) && (discriminatorType == null || discriminatorType == DiscriminatorType.STRING) && length == 30) {
            return true;
        }
        return false;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (discriminatorType == null) {
            return Collections.singletonList(
                    "javax.persistence.DiscriminatorColumn");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.DiscriminatorColumn");
        importSnippets.add("javax.persistence.DiscriminatorType");

        return importSnippets;
    }
}
