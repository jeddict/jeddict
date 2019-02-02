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

import io.github.jeddict.orm.generator.compiler.AssociationOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertsSnippet;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.util.ImportSet;

public class ManagedClassDefSnippet extends ClassDefSnippet implements AttributeOverridesHandler, AssociationOverridesHandler {
    private static final String JPA_TEMPLATE_FILENAME = "beanclasstemplate.ftl";

    private ConvertsSnippet converts;
    private AssociationOverridesSnippet associationOverrides;
    private AttributeOverridesSnippet attributeOverrides;

    @Override
    protected String getTemplateName() {
        return JPA_TEMPLATE_FILENAME;
    }

    @Override
    public AssociationOverridesSnippet getAssociationOverrides() {
        return associationOverrides;
    }

    @Override
    public void setAssociationOverrides(AssociationOverridesSnippet associationOverrides) {
        this.associationOverrides = associationOverrides;
    }

    @Override
    public AttributeOverridesSnippet getAttributeOverrides() {
        return attributeOverrides;
    }

    @Override
    public void setAttributeOverrides(AttributeOverridesSnippet attributeOverrides) {
        this.attributeOverrides = attributeOverrides;
    }

    public ConvertsSnippet getConverts() {
        return converts;
    }

    public void setConverts(ConvertsSnippet converts) {
        this.converts = converts;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet imports = super.getImportSet();

        if (associationOverrides != null) {
            imports.addAll(associationOverrides.getImportSnippets());
        }

        if (attributeOverrides != null) {
            imports.addAll(attributeOverrides.getImportSnippets());
        }

        if (converts != null) {
            imports.addAll(converts.getImportSnippets());
        }

        return imports;
    }
}
