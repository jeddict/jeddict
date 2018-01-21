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
package org.netbeans.orm.converter.compiler.def;

import org.netbeans.orm.converter.compiler.AssociationOverridesHandler;
import org.netbeans.orm.converter.compiler.AssociationOverridesSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverridesHandler;
import org.netbeans.orm.converter.compiler.AttributeOverridesSnippet;
import org.netbeans.orm.converter.compiler.ConvertsSnippet;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.util.ImportSet;

public class ManagedClassDefSnippet extends ClassDefSnippet implements AttributeOverridesHandler, AssociationOverridesHandler {
    private static final String JPA_TEMPLATE_FILENAME = "jpatemplate.vm";

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
        ImportSet importSnippets = super.getImportSet();

        if (associationOverrides != null) {
            importSnippets.addAll(associationOverrides.getImportSnippets());
        }

        if (attributeOverrides != null) {
            importSnippets.addAll(attributeOverrides.getImportSnippets());
        }

        if (converts != null) {
            importSnippets.addAll(converts.getImportSnippets());
        }

        return importSnippets;
    }
}
