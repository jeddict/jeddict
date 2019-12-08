/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.jcode.JPAConstants.ENUMERATED;
import static io.github.jeddict.jcode.JPAConstants.ENUMERATED_FQN;
import static io.github.jeddict.jcode.JPAConstants.ENUM_TYPE_FQN;
import static io.github.jeddict.jcode.JPAConstants.ENUM_TYPE_ORDINAL;
import static io.github.jeddict.jcode.JPAConstants.ENUM_TYPE_STRING;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_ENUMERATED;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_ENUMERATED_FQN;
import io.github.jeddict.jpa.spec.EnumType;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumeratedSnippet implements Snippet {

    private String value = null;
    private static final List<String> ENUM_TYPES = getEnumTypes();
    private boolean mapKey;

    public EnumeratedSnippet() {
    }

    public EnumeratedSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public EnumeratedSnippet(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (!ENUM_TYPES.contains(value)) {
            throw new IllegalArgumentException("Invalid Enumerated Type, Supported types" + ENUM_TYPES);
        }
        this.value = value;
    }

    public void setValue(EnumType parsedEnumType) {
        if (parsedEnumType.equals(EnumType.ORDINAL)) {
            this.setValue(ENUM_TYPE_ORDINAL);
        } else if (parsedEnumType.equals(EnumType.STRING)) {
            this.setValue(ENUM_TYPE_STRING);
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        String enumerated = mapKey ? MAP_KEY_ENUMERATED : ENUMERATED;
        if (ENUM_TYPE_STRING.equals(value)) {
            return annotate(
                    enumerated,
                    ENUM_TYPE_STRING
            );
        } else if (isGenerateDefaultValue()
                || ENUM_TYPE_ORDINAL.equals(value)) {
            return annotate(
                    enumerated,
                    ENUM_TYPE_ORDINAL
            );
        } else {
            return annotate(
                    enumerated
            );
        }
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (mapKey) {
            imports.add(MAP_KEY_ENUMERATED_FQN);
        } else {
            imports.add(ENUMERATED_FQN);
        }
        
        if (isGenerateDefaultValue()
                || ENUM_TYPE_STRING.equals(value)
                || ENUM_TYPE_ORDINAL.equals(value)) {
            imports.add(ENUM_TYPE_FQN);
        }
        return imports;
    }

    private static List<String> getEnumTypes() {
        List<String> enumTypes = new ArrayList<>();
        enumTypes.add(ENUM_TYPE_STRING);
        enumTypes.add(ENUM_TYPE_ORDINAL);
        return enumTypes;
    }

    /**
     * @return the mapKey
     */
    public boolean isMapKey() {
        return mapKey;
    }

    /**
     * @param mapKey the mapKey to set
     */
    public void setMapKey(boolean mapKey) {
        this.mapKey = mapKey;
    }
}
