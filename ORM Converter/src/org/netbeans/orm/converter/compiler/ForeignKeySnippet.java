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
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.jpa.JPAConstants.CONSTRAINT_MODE;
import static org.netbeans.jcode.jpa.JPAConstants.CONSTRAINT_MODE_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.FOREIGN_KEY;
import static org.netbeans.jcode.jpa.JPAConstants.FOREIGN_KEY_FQN;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class ForeignKeySnippet implements Snippet {

    private String description;
    private String name;
    private String constraintMode;
    private String foreignKeyDefinition;

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();

        builder.append("@").append(FOREIGN_KEY).append("(");
        if (StringUtils.isNotBlank(name)) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (StringUtils.isNotBlank(constraintMode)) {
            builder.append("value=").append(CONSTRAINT_MODE).append(".");
            builder.append(constraintMode);
            builder.append(ORMConverterUtil.COMMA);
        } else if (CodePanel.isGenerateDefaultValue()) {
            builder.append("value=").append(CONSTRAINT_MODE).append(".");
            builder.append("PROVIDER_DEFAULT");
            builder.append(ORMConverterUtil.COMMA);
        }
        if (StringUtils.isNotBlank(foreignKeyDefinition)) {
            builder.append("foreignKeyDefinition=\"");
            builder.append(foreignKeyDefinition);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }
        return builder.substring(0, builder.length() - 1) + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (StringUtils.isNotBlank(constraintMode) || CodePanel.isGenerateDefaultValue()) {
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
