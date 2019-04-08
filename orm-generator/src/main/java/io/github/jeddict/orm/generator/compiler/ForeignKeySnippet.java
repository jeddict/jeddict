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
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class ForeignKeySnippet implements Snippet {

    private String description;
    private String name;
    private String constraintMode;
    private String foreignKeyDefinition;

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

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append(attribute("name", name));

        if (isNotBlank(constraintMode)) {
            builder.append(attributeExp("value", CONSTRAINT_MODE + "." + constraintMode));
        } else if (isGenerateDefaultValue()) {
            builder.append(attributeExp("value", CONSTRAINT_MODE + "." + "PROVIDER_DEFAULT"));
        }

        builder.append(attribute("foreignKeyDefinition", foreignKeyDefinition));

        return annotate(FOREIGN_KEY, builder);
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (isNotBlank(constraintMode) || isGenerateDefaultValue()) {
            imports.add(CONSTRAINT_MODE_FQN);
        }
        imports.add(FOREIGN_KEY_FQN);
        return imports;
    }

}
