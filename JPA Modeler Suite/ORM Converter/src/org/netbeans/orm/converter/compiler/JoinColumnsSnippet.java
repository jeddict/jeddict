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
import java.util.Collections;
import java.util.List;

public class JoinColumnsSnippet implements Snippet {

    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (joinColumns.isEmpty()) {
            return "@JoinColumns";
        }

        if (joinColumns.size() == 1) {
            return joinColumns.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@JoinColumns({");

        for (JoinColumnSnippet joinColumn : joinColumns) {
            builder.append(joinColumn.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }
        builder.deleteCharAt(builder.length() - 1);

        builder.append(ORMConverterUtil.CLOSE_BRACES);
        builder.append(ORMConverterUtil.COMMA);

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {

        if (joinColumns.isEmpty()) {
            return Collections.singletonList("javax.persistence.JoinColumns");
        }

        if (joinColumns.size() == 1) {
            return Collections.singletonList("javax.persistence.JoinColumn");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.JoinColumn");
        importSnippets.add("javax.persistence.JoinColumns");

        return importSnippets;
    }
}
