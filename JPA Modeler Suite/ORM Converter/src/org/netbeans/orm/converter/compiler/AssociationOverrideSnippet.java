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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AssociationOverrideSnippet implements Snippet {

    private String name = null;
    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addJoinColumn(JoinColumnSnippet joinColumn) {

        if (joinColumns.isEmpty()) {
            joinColumns = new ArrayList<JoinColumnSnippet>();
        }

        joinColumns.add(joinColumn);
    }

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null || joinColumns == null) {
            throw new InvalidDataException("Name and JoinColumns required");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@AssociationOverride(");

        builder.append("name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        builder.append("joinColumns={");

        for (JoinColumnSnippet joinColumn : joinColumns) {
            builder.append(joinColumn.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        builder.deleteCharAt(builder.length() - 1);

        builder.append(ORMConverterUtil.CLOSE_BRACES);
        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);

        return builder.toString();
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.AssociationOverride");
        importSnippets.addAll(joinColumns.get(0).getImportSnippets());

        return importSnippets;
    }
}
