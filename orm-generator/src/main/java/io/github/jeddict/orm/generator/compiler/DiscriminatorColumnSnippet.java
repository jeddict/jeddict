/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

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
        return annotate(
                DISCRIMINATOR_COLUMN,
                attribute("length", length, val -> isGenerateDefaultValue() || val != 30),
                attribute("name", name),
                attribute("discriminatorType", "DiscriminatorType." + discriminatorType, val -> discriminatorType != null),
                attribute("columnDefinition", columnDefinition)
        );
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
        Set<String> imports = new HashSet<>();
        imports.add(DISCRIMINATOR_COLUMN_FQN);
        if (discriminatorType != null) {
            imports.add(DISCRIMINATOR_TYPE_FQN);
        }
        return imports;
    }
}
