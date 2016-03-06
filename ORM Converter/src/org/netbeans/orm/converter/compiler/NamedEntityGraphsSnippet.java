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
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedEntityGraphsSnippet implements Snippet {

    private List<NamedEntityGraphSnippet> namedEntityGraphs = Collections.EMPTY_LIST;

    public void addNamedEntityGraph(NamedEntityGraphSnippet namedQueryDef) {

        if (namedEntityGraphs.isEmpty()) {
            namedEntityGraphs = new ArrayList<>();
        }

        namedEntityGraphs.add(namedQueryDef);
    }

    public List<NamedEntityGraphSnippet> getNamedEntityGraphs() {
        return namedEntityGraphs;
    }

    public void setNamedEntityGraphs(List<NamedEntityGraphSnippet> namedEntityGraphs) {
        if (namedEntityGraphs != null) {
            this.namedEntityGraphs = namedEntityGraphs;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (namedEntityGraphs.isEmpty()) {
            throw new InvalidDataException("Missing NamedEntityGraphs");
        }

        if (namedEntityGraphs.size() == 1) {
            return namedEntityGraphs.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@NamedEntityGraphs({");

        for (NamedEntityGraphSnippet namedEntityGraph : namedEntityGraphs) {
            builder.append(namedEntityGraph.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (namedEntityGraphs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (namedEntityGraphs.size() == 1) {
            return namedEntityGraphs.get(0).getImportSnippets();
        }

        ArrayList<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.NamedEntityGraphs");
        namedEntityGraphs.stream().forEach(e
                -> {
            try {
                importSnippets.addAll(e.getImportSnippets());
            } catch (InvalidDataException ex) {
                ExceptionUtils.printStackTrace(ex);
            }
        }
        );

        return importSnippets;
    }
}
