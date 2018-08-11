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

import static io.github.jeddict.jcode.JPAConstants.CONSTRAINT_MODE;
import static io.github.jeddict.jcode.JPAConstants.CONSTRAINT_MODE_FQN;
import static io.github.jeddict.jcode.JPAConstants.FOREIGN_KEY;
import static io.github.jeddict.jcode.JPAConstants.FOREIGN_KEY_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ForeignKeySnippet implements Snippet {

    private String description;
    private String name;
    private String constraintMode;
    private String foreignKeyDefinition;

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);

        builder.append(FOREIGN_KEY)
                .append(OPEN_PARANTHESES);

        if (isNotBlank(name)) {
            builder.append("name=\"")
                    .append(name)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isNotBlank(constraintMode)) {
            builder.append("value=")
                    .append(CONSTRAINT_MODE)
                    .append(".")
                    .append(constraintMode)
                    .append(COMMA);
        } else if (isGenerateDefaultValue()) {
            builder.append("value=")
                    .append(CONSTRAINT_MODE)
                    .append(".")
                    .append("PROVIDER_DEFAULT")
                    .append(COMMA);
        }

        if (isNotBlank(foreignKeyDefinition)) {
            builder.append("foreignKeyDefinition=\"")
                    .append(foreignKeyDefinition)
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (isNotBlank(constraintMode) || isGenerateDefaultValue()) {
            importSnippets.add(CONSTRAINT_MODE_FQN);
        }
        importSnippets.add(FOREIGN_KEY_FQN);
        return importSnippets;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the constraintMode
     */
    public String getConstraintMode() {
        return constraintMode;
    }

    /**
     * @param constraintMode the constraintMode to set
     */
    public void setConstraintMode(String constraintMode) {
        this.constraintMode = constraintMode;
    }

    /**
     * @return the foreignKeyDefinition
     */
    public String getForeignKeyDefinition() {
        return foreignKeyDefinition;
    }

    /**
     * @param foreignKeyDefinition the foreignKeyDefinition to set
     */
    public void setForeignKeyDefinition(String foreignKeyDefinition) {
        this.foreignKeyDefinition = foreignKeyDefinition;
    }
}
