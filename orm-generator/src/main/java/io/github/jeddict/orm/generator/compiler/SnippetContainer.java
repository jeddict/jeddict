/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.TAB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SnippetContainer<T extends Snippet> implements Snippet {

    private List<T> snippets = Collections.<T>emptyList();
   
    private final boolean repeatable;

    public SnippetContainer(boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (snippets.isEmpty()) {
            throw new InvalidDataException(getContianerName() + " is empty");
        }

        if (snippets.size() == 1) {
            return snippets.get(0).getSnippet();
        }
        
        boolean containerAnnotation = !this.repeatable;
        
        StringBuilder stringBuilder = new StringBuilder();
        
        if(containerAnnotation){
            stringBuilder.append("@").append(getContianerName()).append("({");
            stringBuilder.append(NEW_LINE);
        }

        for (T snippet : snippets) {
            stringBuilder.append(snippet.getSnippet());
            if(containerAnnotation){stringBuilder.append(COMMA);}
        }
        
        if(containerAnnotation){
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append(NEW_LINE).append(TAB).append(CLOSE_BRACES).append(CLOSE_PARANTHESES);
        } 
        return stringBuilder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (snippets.isEmpty()) {
            return emptySet();
        }
        if (snippets.size() == 1) {
            return snippets.get(0).getImportSnippets();
        }
        Set<String> imports = new HashSet<>();

        boolean containerAnnotation = !this.repeatable;
        if(containerAnnotation){
            imports.add(getContianerFQN());
        }

        for (T snippet : snippets) {
            imports.addAll(snippet.getImportSnippets());
        }

        return imports;
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
    
    public boolean isEmpty() {
        return snippets.isEmpty();
    }

    public void set(List<T> snippets) {
        if (snippets != null) {
            this.snippets = snippets;
        }
    }

    public abstract String getContianerName();

    public abstract String getContianerFQN();
}
