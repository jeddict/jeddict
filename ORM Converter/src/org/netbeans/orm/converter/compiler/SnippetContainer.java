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
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_BRACES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.COMMA;
import static org.netbeans.orm.converter.util.ORMConverterUtil.NEW_LINE;
import static org.netbeans.orm.converter.util.ORMConverterUtil.NEW_TAB;
import static org.netbeans.orm.converter.util.ORMConverterUtil.TAB;

public abstract class SnippetContainer<T extends Snippet> implements Snippet {

    private List<T> snippets = Collections.EMPTY_LIST;

    @Override
    public String getSnippet() throws InvalidDataException {

        if (snippets.isEmpty()) {
            throw new InvalidDataException(getContianerName() + " is empty");
        }

        if (snippets.size() == 1) {
            return snippets.get(0).getSnippet();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@").append(getContianerName()).append("({");

        for (T snippet : snippets) {
            stringBuilder.append(NEW_LINE).append(NEW_TAB);
            stringBuilder.append(snippet.getSnippet());
            stringBuilder.append(COMMA);
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1)
                + NEW_LINE + TAB + CLOSE_BRACES + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (snippets.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (snippets.size() == 1) {
            return snippets.get(0).getImportSnippets();
        }
        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(getContianerFQN());

        for (T convertSnippet : snippets) {
            importSnippets.addAll(convertSnippet.getImportSnippets());
        }

        return importSnippets;
    }

    public void add(T snippet) {
        if (snippets.isEmpty()) {
            snippets = new ArrayList<>();
        }
        snippets.add(snippet);
    }

    public List<T> get() {
        return snippets;
    }

    public void set(List<T> snippets) {
        if (snippets != null) {
            this.snippets = snippets;
        }
    }

    public abstract String getContianerName();

    public abstract String getContianerFQN();
}
