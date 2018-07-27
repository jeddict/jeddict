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
package io.github.jeddict.orm.generator.compiler.def;

import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.JPAConstants.ENTITY;
import static io.github.jeddict.jcode.JPAConstants.ENTITY_FQN;
import io.github.jeddict.orm.generator.compiler.DiscriminatorColumnSnippet;
import io.github.jeddict.orm.generator.compiler.DiscriminatorValueSnippet;
import io.github.jeddict.orm.generator.compiler.InheritanceSnippet;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.util.ImportSet;

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
        if (StringUtils.isEmpty(entityName)) {
            return "@" + ENTITY;
        } else {
            return "@" + ENTITY + "(name=\"" + entityName + "\")";
        }
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet importSnippets = super.getImportSet();

        importSnippets.add(ENTITY_FQN);

        if (discriminatorColumn != null) {
            importSnippets.addAll(discriminatorColumn.getImportSnippets());
        }

        if (discriminatorValue != null) {
            importSnippets.addAll(discriminatorValue.getImportSnippets());
        }

        if (inheritance != null) {
            importSnippets.addAll(inheritance.getImportSnippets());
        }
        return importSnippets;
    }
}
