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

import static io.github.jeddict.jcode.JPAConstants.DISCRIMINATOR_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.DISCRIMINATOR_COLUMN_FQN;
import static io.github.jeddict.jcode.JPAConstants.DISCRIMINATOR_TYPE_FQN;
import io.github.jeddict.jpa.spec.DiscriminatorType;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DiscriminatorColumnSnippet implements Snippet {

    private String name = null;
    private DiscriminatorType discriminatorType = null;
    private String columnDefinition = null;
    private int length = 31;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiscriminatorType getDiscriminatorType() {
        return discriminatorType;
    }

    public void setDiscriminatorType(DiscriminatorType discriminatorType) {
        this.discriminatorType = discriminatorType;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);

        builder.append(DISCRIMINATOR_COLUMN)
                .append(OPEN_PARANTHESES);

        if (isGenerateDefaultValue() || length != 30) {
            builder.append("length=")
                    .append(length)
                    .append(COMMA);
        }

        if (isNotBlank(name)) {
            builder.append("name=\"")
                    .append(name)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (discriminatorType != null) {
            builder.append("discriminatorType=DiscriminatorType.")
                    .append(discriminatorType)
                    .append(COMMA);
        }

        if (isNotBlank(columnDefinition)) {
            builder.append("columnDefinition=\"")
                    .append(columnDefinition)
                    .append(QUOTE)
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    public boolean isDefault() {
        if (isBlank(name)
                && isBlank(columnDefinition)
                && (discriminatorType == null || discriminatorType == DiscriminatorType.STRING)
                && length == 30) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(DISCRIMINATOR_COLUMN_FQN);
        if (discriminatorType != null) {
            importSnippets.add(DISCRIMINATOR_TYPE_FQN);
        }
        return importSnippets;
    }
}
