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

import static io.github.jeddict.jcode.JPAConstants.FIELD_RESULT;
import static io.github.jeddict.jcode.JPAConstants.FIELD_RESULT_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import static java.util.Collections.singleton;
import static org.apache.commons.lang.StringUtils.isBlank;

public class FieldResultSnippet implements Snippet {

    private String name = null;
    private String column = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name) || column == null) {
            throw new InvalidDataException("name or value cannot be null");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(FIELD_RESULT)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name))
                .append(buildString("column", column));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;

    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(FIELD_RESULT_FQN);
    }
}
