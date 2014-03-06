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
import java.util.Collections;
import java.util.List;

public class EnumeratedSnippet implements Snippet {

    public static final String TYPE_ORDINAL = "EnumType.ORDINAL";
    public static final String TYPE_STRING = "EnumType.STRING";

    private String value = null;

    private static final List<String> ENUM_TYPES = getEnumTypes();

    public EnumeratedSnippet() {
    }

    public EnumeratedSnippet(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {

        if (!ENUM_TYPES.contains(value)) {
            throw new IllegalArgumentException(
                    "Invalid Enumerated Type, Supported types"
                    + ENUM_TYPES);
        }

        this.value = value;
    }

    public String getSnippet() throws InvalidDataException {

        if (value == null || value.equals(TYPE_ORDINAL)) {
            return "@Enumerated";
        }

        return "@Enumerated(EnumType.STRING)";
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (value == null || value.equals(TYPE_ORDINAL)) {
            return Collections.singleton("javax.persistence.Enumerated");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.Enumerated");
        importSnippets.add("javax.persistence.EnumType");

        return importSnippets;
    }

    private static List<String> getEnumTypes() {
        List<String> enumTypes = new ArrayList<String>();

        enumTypes.add(TYPE_STRING);
        enumTypes.add(TYPE_ORDINAL);

        return enumTypes;
    }
}
