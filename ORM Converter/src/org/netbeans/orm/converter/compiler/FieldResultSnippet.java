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

import java.util.Collection;
import java.util.Collections;

public class FieldResultSnippet implements Snippet {

    private String name = null;
    private String column = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null || column == null) {
            throw new InvalidDataException("name or value cannot be null");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@FieldResult(");

        builder.append("name=\"");
        builder.append(getName());
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        builder.append("column=\"");
        builder.append(column);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);

        return builder.toString();

    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList("javax.persistence.FieldResult");
    }
}
