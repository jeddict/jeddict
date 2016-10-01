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
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_STORED_PROCEDURE_QUERIES;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_STORED_PROCEDURE_QUERIES_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedStoredProcedureQueriesSnippet implements Snippet {

    private List<NamedStoredProcedureQuerySnippet> namedStoredProcedureQueries = Collections.EMPTY_LIST;

    public void addNamedStoredProcedureQuery(NamedStoredProcedureQuerySnippet namedQueryDef) {

        if (namedStoredProcedureQueries.isEmpty()) {
            namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQuerySnippet>();
        }

        namedStoredProcedureQueries.add(namedQueryDef);
    }

    public List<NamedStoredProcedureQuerySnippet> getNamedStoredProcedureQueries() {
        return namedStoredProcedureQueries;
    }

    public void setNamedStoredProcedureQueries(List<NamedStoredProcedureQuerySnippet> namedStoredProcedureQueries) {
        if (namedStoredProcedureQueries != null) {
            this.namedStoredProcedureQueries = namedStoredProcedureQueries;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (namedStoredProcedureQueries.isEmpty()) {
            throw new InvalidDataException("Missing " + NAMED_STORED_PROCEDURE_QUERIES);
        }

        if (namedStoredProcedureQueries.size() == 1) {
            return namedStoredProcedureQueries.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(NAMED_STORED_PROCEDURE_QUERIES).append("({");

        for (NamedStoredProcedureQuerySnippet namedStoredProcedureQuery : namedStoredProcedureQueries) {
            builder.append(namedStoredProcedureQuery.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (namedStoredProcedureQueries.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (namedStoredProcedureQueries.size() == 1) {
            return namedStoredProcedureQueries.get(0).getImportSnippets();
        }

        ArrayList<String> importSnippets = new ArrayList<>();

        importSnippets.add(NAMED_STORED_PROCEDURE_QUERIES_FQN);
        for (NamedStoredProcedureQuerySnippet namedStoredProcedureQuery : namedStoredProcedureQueries) {
            importSnippets.addAll(namedStoredProcedureQuery.getImportSnippets());
        }

        return importSnippets;
    }
}
