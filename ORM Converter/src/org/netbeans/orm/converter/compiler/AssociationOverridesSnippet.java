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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AssociationOverridesSnippet implements Snippet {

    private List<AssociationOverrideSnippet> associationOverrides
            = Collections.EMPTY_LIST;

    public void addAssociationOverride(
            AssociationOverrideSnippet associationOverride) {

        if (associationOverrides.isEmpty()) {
            associationOverrides = new ArrayList<AssociationOverrideSnippet>();
        }

        associationOverrides.add(associationOverride);
    }

    public List<AssociationOverrideSnippet> getAssociationOverrides() {
        return associationOverrides;
    }

    public void setAssociationOverrides(
            List<AssociationOverrideSnippet> associationOverrides) {

        if (associationOverrides != null) {
            this.associationOverrides = associationOverrides;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (associationOverrides.isEmpty()) {
            throw new InvalidDataException("Missing AssociationOverrides");
        }

        if (associationOverrides.size() == 1) {
            return associationOverrides.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@AssociationOverrides({");

        for (AssociationOverrideSnippet associationOverride : associationOverrides) {
            builder.append(associationOverride.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (associationOverrides.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (associationOverrides.size() == 1) {
            return associationOverrides.get(0).getImportSnippets();
        }

        Collection<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.AssociationOverrides");
        importSnippets.addAll(associationOverrides.get(0).getImportSnippets());

        return importSnippets;
    }
}
