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

import static io.github.jeddict.jcode.JPAConstants.PRIMARY_KEY_JOIN_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.PRIMARY_KEY_JOIN_COLUMN_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;

public class PrimaryKeyJoinColumnSnippet implements Snippet {

    private String columnDefinition = null;
    private String name = null;
    private String referencedColumnName = null;

    private ForeignKeySnippet foreignKey;
    
    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }
    
    /**
     * @return the foreignKey
     */
    public ForeignKeySnippet getForeignKey() {
        return foreignKey;
    }

    /**
     * @param foreignKey the foreignKey to set
     */
    public void setForeignKey(ForeignKeySnippet foreignKey) {
        this.foreignKey = foreignKey;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(PRIMARY_KEY_JOIN_COLUMN);

        if (isBlank(name)
                && isBlank(referencedColumnName)
                && isBlank(columnDefinition)) {
            return builder.toString();
        }

        builder.append(OPEN_PARANTHESES)
                .append(buildString("name", name))
                .append(buildString("referencedColumnName", referencedColumnName))
                .append(buildString("columnDefinition", columnDefinition))
                .append(buildSnippet("foreignKey", foreignKey));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(PRIMARY_KEY_JOIN_COLUMN_FQN);
        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }
        return imports;
    }
}
