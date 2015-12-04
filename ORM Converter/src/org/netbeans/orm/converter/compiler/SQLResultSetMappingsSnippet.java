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
import java.util.TreeSet;

public class SQLResultSetMappingsSnippet implements Snippet {

    private List<SQLResultSetMappingSnippet> sqlResultSetMappings
            = Collections.EMPTY_LIST;

    public void addSQLResultSetMapping(
            SQLResultSetMappingSnippet sqlResultSetMapping) {

        if (sqlResultSetMappings.isEmpty()) {
            sqlResultSetMappings = new ArrayList<SQLResultSetMappingSnippet>();
        }

        sqlResultSetMappings.add(sqlResultSetMapping);
    }

    public List<SQLResultSetMappingSnippet> getSqlResultSetMappings() {
        return sqlResultSetMappings;
    }

    public void setSqlResultSetMappings(
            List<SQLResultSetMappingSnippet> sqlResultSetMappings) {
        if (sqlResultSetMappings != null) {
            this.sqlResultSetMappings = sqlResultSetMappings;
        }
    }

    public String getSnippet() throws InvalidDataException {
        if (sqlResultSetMappings.isEmpty()) {
            throw new InvalidDataException("Missing SQLResultSetMappings");
        }

        if (sqlResultSetMappings.size() == 1) {
            return sqlResultSetMappings.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@SqlResultSetMappings({");

        for (SQLResultSetMappingSnippet sqlResultSetMapping : sqlResultSetMappings) {
            builder.append(sqlResultSetMapping.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (sqlResultSetMappings.size() == 1) {
            return sqlResultSetMappings.get(0).getImportSnippets();
        }

        //Sort and eliminate duplicates
        Collection<String> importSnippets = new TreeSet<String>();

        importSnippets.add("javax.persistence.SqlResultSetMappings");

        for (SQLResultSetMappingSnippet sqlResultSetMapping : sqlResultSetMappings) {
            importSnippets.addAll(sqlResultSetMapping.getImportSnippets());
        }

        return importSnippets;
    }
}
