/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.JPAConstants.LOCK_MODE_TYPE;
import static io.github.jeddict.jcode.JPAConstants.LOCK_MODE_TYPE_FQN;
import static io.github.jeddict.jcode.JPAConstants.NAMED_QUERY;
import static io.github.jeddict.jcode.JPAConstants.NAMED_QUERY_FQN;
import io.github.jeddict.jpa.spec.LockModeType;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class NamedQuerySnippet implements Snippet {

    protected String query = null;
    protected String name = null;
    private LockModeType lockMode;

    protected List<QueryHintSnippet> queryHints = Collections.<QueryHintSnippet>emptyList();

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
            queryHints = new ArrayList<>();
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

    @Override
    public String getSnippet() throws InvalidDataException {

        if (name == null || query == null) {
            throw new InvalidDataException(
                    "Query data missing, Name:" + name + " Query: " + query);
        }

        //remove new lines from query
        query = query.replaceAll("\\n", " ");
        query = query.replaceAll("\\t", " ");

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(NAMED_QUERY).append("(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        builder.append("query=\"");
        builder.append(query);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (lockMode != null) {
            builder.append("lockMode=").append(LOCK_MODE_TYPE).append(".");
            builder.append(lockMode);
            builder.append(ORMConverterUtil.COMMA);
        }

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

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(NAMED_QUERY_FQN);
        if (lockMode != null) {
            importSnippets.add(LOCK_MODE_TYPE_FQN);
        }
        if (!queryHints.isEmpty()) {
            importSnippets.addAll(queryHints.get(0).getImportSnippets());
        }
        return importSnippets;
    }

    /**
     * @return the lockMode
     */
    public LockModeType getLockMode() {
        return lockMode;
    }

    /**
     * @param lockMode the lockMode to set
     */
    public void setLockMode(LockModeType lockMode) {
        this.lockMode = lockMode;
    }
}
