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

public class PrimaryKeyJoinColumnsSnippet implements Snippet {

    private List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns
            = Collections.EMPTY_LIST;

    public void addPrimaryKeyJoinColumn(
            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn) {

        if (primaryKeyJoinColumns.isEmpty()) {
            primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnSnippet>();
        }

        primaryKeyJoinColumns.add(primaryKeyJoinColumn);
    }

    public List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns() {
        return primaryKeyJoinColumns;
    }

    public void setPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns) {
        if (primaryKeyJoinColumns != null) {
            this.primaryKeyJoinColumns = primaryKeyJoinColumns;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (primaryKeyJoinColumns.isEmpty()) {
            return "@PrimaryKeyJoinColumns";
        }

        if (primaryKeyJoinColumns.size() == 1) {
            return primaryKeyJoinColumns.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@PrimaryKeyJoinColumns({");

        for (PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn : primaryKeyJoinColumns) {
            builder.append(primaryKeyJoinColumn.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {

        if (primaryKeyJoinColumns.isEmpty()) {
            return Collections.singletonList(
                    "javax.persistence.PrimaryKeyJoinColumns");
        }

        if (primaryKeyJoinColumns.size() == 1) {
            return Collections.singletonList(
                    "javax.persistence.PrimaryKeyJoinColumn");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.PrimaryKeyJoinColumn");
        importSnippets.add("javax.persistence.PrimaryKeyJoinColumns");

        return importSnippets;

    }
}
