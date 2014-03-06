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

public class NamedQueryDefSnippet implements Snippet {

    protected String attributeType = null;
    protected String query = null;
    protected String name = null;

    protected List<QueryHintSnippet> queryHints = Collections.EMPTY_LIST;

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String queryName) {
        this.name = queryName;
    }

    public void addQueryHint(QueryHintSnippet queryHint) {

        if (queryHints.isEmpty()) {
            queryHints = new ArrayList<QueryHintSnippet>();
        }

        queryHints.add(queryHint);
    }

    public List<QueryHintSnippet> getQueryHints() {
        return queryHints;
    }

    public void setQueryHints(List<QueryHintSnippet> queryHints) {
        if (queryHints != null) {
            this.queryHints = queryHints;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null || query == null) {
            throw new InvalidDataException(
                    "Query data missing, Name:" + name + " Query: " + query);
        }

        //remove new lines from query
        query = query.replaceAll("\\n", " ");
        query = query.replaceAll("\\t", " ");

        StringBuilder builder = new StringBuilder();

        builder.append("@NamedQuery(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        builder.append("query=\"");
        builder.append(query);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (!queryHints.isEmpty()) {
            builder.append("hints={");

            for (QueryHintSnippet queryHint : queryHints) {
                builder.append(queryHint.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {

        if (queryHints.isEmpty()) {
            return Collections.singletonList("javax.persistence.NamedQuery");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.NamedQuery");
        importSnippets.addAll(queryHints.get(0).getImportSnippets());

        return importSnippets;
    }
}
