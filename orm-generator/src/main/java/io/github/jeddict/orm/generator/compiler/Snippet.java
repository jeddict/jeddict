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

import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import java.util.List;

public interface Snippet {

    public String getSnippet() throws InvalidDataException;

    public Collection<String> getImportSnippets() throws InvalidDataException;

    default String buildAnnotations(String key, List<? extends Snippet> snippets) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (!snippets.isEmpty()) {
            builder.append(key).append(" =");
            if (snippets.size() > 1) {
                builder.append(OPEN_BRACES);
            }

            for (Snippet joinColumn : snippets) {
                builder.append(joinColumn.getSnippet()).append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);

            if (snippets.size() > 1) {
                builder.append(CLOSE_BRACES);
            }
            builder.append(COMMA);
        }
        return builder.toString();
    }

    default String buildAnnotation(String key, Snippet snippet) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        String snippetValue;
        if (snippet != null && (snippetValue = snippet.getSnippet()) != null) {
            builder.append(key).append(" =");
            builder.append(snippetValue);
            builder.append(COMMA);
        }
        return builder.toString();
    }

    default String buildString(String key, String value) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (value != null && !value.trim().isEmpty()) {
            builder.append(key)
                    .append("=")
                    .append(QUOTE)
                    .append(value)
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.toString();
    }
}
