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

import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public interface Snippet {

    public String getSnippet() throws InvalidDataException;

    public Collection<String> getImportSnippets() throws InvalidDataException;

    default String attributes(String key, List<? extends Snippet> snippets) throws InvalidDataException {
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

    default String attribute(String key, Snippet snippet) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        String snippetValue;
        if (snippet != null && (snippetValue = snippet.getSnippet()) != null) {
            builder.append(key).append(" =");
            builder.append(snippetValue);
            builder.append(COMMA);
        }
        return builder.toString();
    }

    default String attribute(String value) {
        StringBuilder builder = new StringBuilder();
        if (isNotBlank(value)) {
            builder.append(QUOTE)
                    .append(value)
                    .append(QUOTE);
        }
        return builder.toString();
    }

    default String attribute(String key, String value) {
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
    
        default String attribute(String key, String value, Predicate<String> condition) {
        StringBuilder builder = new StringBuilder();
        if (condition.test(value)) {
            builder.append(key)
                    .append("=")
                    .append(QUOTE)
                    .append(value)
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.toString();
    }

    default String attribute(String key, Integer value, Predicate<Integer> condition) {
        StringBuilder builder = new StringBuilder();
        if (condition.test(value)) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }
    
    default String attribute(String key, Boolean value, Predicate<Boolean> condition) {
        StringBuilder builder = new StringBuilder();
        if (condition.test(value)) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }
    
    default String attributeln(String key, String value) {
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

    default String attribute(String key, List<String> values) {
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

    default String attributeExp(String key, String value) {
        StringBuilder builder = new StringBuilder();
        if (isNotBlank(value)) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }
    
    default String attributeExp(String key, String value, Predicate<String> condition) {
        StringBuilder builder = new StringBuilder();
        if (condition.test(value)) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }

    default String attributeExp(String key, Boolean value) {
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(COMMA);
        }
        return builder.toString();
    }
    
    default String annotate(String annotation, StringBuilder content) {
        return AT + annotation + wrapParantheses(content.toString());
    }
        
    default String annotate(String annotation, String content) {
        return AT + annotation + wrapParantheses(content);
    }
    
    default String annotate(String annotation, String... content) {
        return AT + annotation + wrapParantheses(String.join(" ", content));
    }
    
    default String wrapParantheses(String value) {
        StringBuilder builder = new StringBuilder();
        if (isNotBlank(value)) {
            value = value.trim();
            builder.append(OPEN_PARANTHESES);
            builder.append(value);
            if (value.charAt(value.length() - 1) == ',') {
                builder.setLength(builder.length() - 1);
            }
            builder.append(CLOSE_PARANTHESES);
        }
        return builder.toString();
    }

}
