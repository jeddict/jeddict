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

public class AttributeOverridesSnippet implements Snippet {

    private List<AttributeOverrideSnippet> attributeOverrides = Collections.EMPTY_LIST;

    public void addAttributeOverrides(AttributeOverrideSnippet attributeOverride) {

        if (attributeOverrides.isEmpty()) {
            attributeOverrides = new ArrayList<AttributeOverrideSnippet>();
        }

        attributeOverrides.add(attributeOverride);
    }

    public List<AttributeOverrideSnippet> getAttributeOverrides() {
        return attributeOverrides;
    }

    public void setAttributeOverrides(
            List<AttributeOverrideSnippet> attributeOverrides) {

        if (attributeOverrides != null) {
            this.attributeOverrides = attributeOverrides;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (attributeOverrides.isEmpty()) {
            throw new InvalidDataException("Missing AttributeOverrides");
        }

        if (attributeOverrides.size() == 1) {
            return attributeOverrides.get(0).getSnippet();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@AttributeOverrides({");

        for (AttributeOverrideSnippet attributeOverride : attributeOverrides) {
            builder.append(attributeOverride.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (attributeOverrides.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (attributeOverrides.size() == 1) {
            return attributeOverrides.get(0).getImportSnippets();
        }

        Collection<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.AttributeOverrides");

        for (AttributeOverrideSnippet attributeOverride : attributeOverrides) {
            importSnippets.addAll(attributeOverride.getImportSnippets());
        }

        return importSnippets;
    }
}
