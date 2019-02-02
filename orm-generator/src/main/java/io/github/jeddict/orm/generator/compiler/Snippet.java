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

import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public interface Snippet {

    public String getSnippet() throws InvalidDataException;

    public Collection<String> getImportSnippets() throws InvalidDataException;

    default String buildSnippets(String key, List<? extends Snippet> snippets) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (snippets != null && !snippets.isEmpty()) {
            builder.append(key).append(" =");
            if (snippets.size() > 1) {
                builder.append(OPEN_BRACES);
            }
            for (Snippet snippet : snippets) {
                builder.append(snippet.getSnippet()).append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);

            if (snippets.size() > 1) {
                builder.append(CLOSE_BRACES);
            }
            builder.append(COMMA);
        }
        return builder.toString();
    }

    default String buildSnippet(String key, Snippet snippet) throws InvalidDataException {
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
        if (isNotBlank(value)) {
            builder.append(key)
                    .append("=")
                    .append(QUOTE)
                    .append(value)
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.toString();
    }
    
    default String buildStringln(String key, String value) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (isNotBlank(value)) {
            builder.append(key)
                    .append("=")
                    .append(QUOTE)
                    .append(value)
                    .append(QUOTE)
                    .append(COMMA)
                    .append(NEW_LINE);
        }
        return builder.toString();
    }

    default String buildStrings(String key, List<String> values) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (!values.isEmpty()) {
            builder.append(key)
                    .append("=");
            if (values.size() > 1) {
                builder.append(OPEN_BRACES);
            }
            for (String value : values) {
                builder.append(QUOTE)
                        .append(value)
                        .append(QUOTE)
                        .append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            if (values.size() > 1) {
                builder.append(CLOSE_BRACES);
            }
            builder.append(COMMA);
        }
        return builder.toString();
    }

    default String buildExp(String key, String value) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (isNotBlank(value)) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }

    default String buildExp(String key, Boolean value) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }

}
