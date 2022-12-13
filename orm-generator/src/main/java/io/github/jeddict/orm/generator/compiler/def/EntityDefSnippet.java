/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler.def;

import static io.github.jeddict.jcode.JPAConstants.ENTITY;
import static io.github.jeddict.jcode.JPAConstants.ENTITY_FQN;
import static io.github.jeddict.jcode.JPAConstants.ENTITY_NOSQL_FQN;
import io.github.jeddict.orm.generator.compiler.DiscriminatorColumnSnippet;
import io.github.jeddict.orm.generator.compiler.DiscriminatorValueSnippet;
import io.github.jeddict.orm.generator.compiler.InheritanceSnippet;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.util.ImportSet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.util.StringUtils.isEmpty;

public class EntityDefSnippet extends IdentifiableClassDefSnippet {

    private String entityName;
    private DiscriminatorColumnSnippet discriminatorColumn;
    private DiscriminatorValueSnippet discriminatorValue;
    private InheritanceSnippet inheritance;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public DiscriminatorColumnSnippet getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    public void setDiscriminatorColumn(DiscriminatorColumnSnippet discriminatorColumn) {
        this.discriminatorColumn = discriminatorColumn;
    }

    public DiscriminatorValueSnippet getDiscriminatorValue() {
        return discriminatorValue;
    }

    public void setDiscriminatorValue(DiscriminatorValueSnippet discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public InheritanceSnippet getInheritance() {
        return inheritance;
    }

    public void setInheritance(InheritanceSnippet inheritance) {
        this.inheritance = inheritance;
    }

    @Override
    public String getManagedType() {
        if (isEmpty(entityName)) {
            return AT + ENTITY;
        } else {
            if (isNoSQL()) {
                return AT + ENTITY + wrapParantheses(attribute(entityName));
            } else {
                return AT + ENTITY + wrapParantheses(attribute("name", entityName));
            }
        }
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet imports = super.getImportSet();
        
        if (isNoSQL()) {
            imports.add(ENTITY_NOSQL_FQN);
        } else {
            imports.add(ENTITY_FQN);

            if (discriminatorColumn != null) {
                imports.addAll(discriminatorColumn.getImportSnippets());
            }

            if (discriminatorValue != null) {
                imports.addAll(discriminatorValue.getImportSnippets());
            }

            if (inheritance != null) {
                imports.addAll(inheritance.getImportSnippets());
            }
        }
        return imports;
    }
}
