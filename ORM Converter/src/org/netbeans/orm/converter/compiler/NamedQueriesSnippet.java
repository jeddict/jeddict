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

public class NamedQueriesSnippet implements Snippet {

    private List<NamedQueryDefSnippet> namedQueries = Collections.EMPTY_LIST;

    public void addNamedQuery(NamedQueryDefSnippet namedQueryDef) {

        if (namedQueries.isEmpty()) {
            namedQueries = new ArrayList<NamedQueryDefSnippet>();
        }

        namedQueries.add(namedQueryDef);
    }

    public List<NamedQueryDefSnippet> getNamedQueries() {
        return namedQueries;
    }

    public void setNamedQueries(List<NamedQueryDefSnippet> namedQueries) {
        if (namedQueries != null) {
            this.namedQueries = namedQueries;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (namedQueries.isEmpty()) {
            throw new InvalidDataException("Missing NamedQueries");
        }

        if (namedQueries.size() == 1) {
            return namedQueries.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@NamedQueries({");

        for (NamedQueryDefSnippet namedQuery : namedQueries) {
            builder.append(namedQuery.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (namedQueries.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (namedQueries.size() == 1) {
            return namedQueries.get(0).getImportSnippets();
        }

        ArrayList<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.NamedQueries");
        importSnippets.addAll(namedQueries.get(0).getImportSnippets());

        return importSnippets;
    }
}
