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

import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMNS;
import static io.github.jeddict.jcode.JPAConstants.JOIN_COLUMNS_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMNS;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_JOIN_COLUMNS_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JoinColumnsSnippet implements Snippet {

    private List<JoinColumnSnippet> joinColumns = Collections.<JoinColumnSnippet>emptyList();
    private ForeignKeySnippet foreignKey;
    private boolean mapKey;
    private final boolean repeatable;
    
    public JoinColumnsSnippet(boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public JoinColumnsSnippet(boolean repeatable, boolean mapKey) {
        this.repeatable = repeatable;
        this.mapKey = mapKey;
    }

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (joinColumns.size() == 1 && foreignKey==null) {
            return joinColumns.get(0).getSnippet();
        }
        
        boolean containerAnnotation = !this.repeatable || foreignKey != null; 
        
        if (containerAnnotation) {
            builder.append(AT);
            if (mapKey) {
                builder.append(MAP_KEY_JOIN_COLUMNS);
            } else {
                builder.append(JOIN_COLUMNS);
            }
            if (foreignKey != null) {
                builder.append(OPEN_PARANTHESES).append("value=").append(OPEN_BRACES);
            } else {
                builder.append(OPEN_PARANTHESES).append(OPEN_BRACES);
            }
        }

        for (JoinColumnSnippet joinColumn : joinColumns) {
            builder.append(joinColumn.getSnippet());
            if (containerAnnotation) {
                builder.append(COMMA);
            }
        }
        
        if (containerAnnotation) {
            builder.setLength(builder.length() - 1);
            builder.append(CLOSE_BRACES);
            builder.append(COMMA);
        }

        builder.append(attribute("foreignKey", foreignKey));

        if (containerAnnotation) {
            builder.setLength(builder.length() - 1);
            builder.append(CLOSE_PARANTHESES);
        }
         
         return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException { 
        Set<String> imports = new HashSet<>();
        boolean containerAnnotation = !this.repeatable || foreignKey != null; 
        if (joinColumns.size() == 1) {
            imports.addAll(joinColumns.get(0).getImportSnippets());
            if (containerAnnotation && foreignKey != null) {
                if (mapKey) {
                    imports.add(MAP_KEY_JOIN_COLUMNS_FQN);
                } else {
                    imports.add(JOIN_COLUMNS_FQN);
                }
            }
        } else {
            for (JoinColumnSnippet jc : joinColumns) {
                imports.addAll(jc.getImportSnippets());
            }
            if (containerAnnotation) {
                if (mapKey) {
                    imports.add(MAP_KEY_JOIN_COLUMNS_FQN);
                } else {
                    imports.add(JOIN_COLUMNS_FQN);
                }
            }
        }

        if (foreignKey != null) {
            imports.addAll(foreignKey.getImportSnippets());
        }

        return imports;
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
}
