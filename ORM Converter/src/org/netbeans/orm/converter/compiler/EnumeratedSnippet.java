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
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.ENUMERATED;
import static org.netbeans.jcode.jpa.JPAConstants.ENUMERATED_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ENUM_TYPE_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ENUM_TYPE_ORDINAL;
import static org.netbeans.jcode.jpa.JPAConstants.ENUM_TYPE_STRING;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_ENUMERATED;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_ENUMERATED_FQN;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.orm.converter.generator.GeneratorUtil;
import org.netbeans.orm.converter.util.ORMConverterUtil;

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
        StringBuilder builder = new StringBuilder();
        builder.append('@');
         if (mapKey) {
            builder.append(MAP_KEY_ENUMERATED);
        } else {
            builder.append(ENUMERATED);
        }
        if (ENUM_TYPE_STRING.equals(value)) {
            builder.append(ORMConverterUtil.OPEN_PARANTHESES).append(ENUM_TYPE_STRING).append(ORMConverterUtil.CLOSE_PARANTHESES);
        } else if (ENUM_TYPE_ORDINAL.equals(value)) {
            builder.append(ORMConverterUtil.OPEN_PARANTHESES).append(ENUM_TYPE_ORDINAL).append(ORMConverterUtil.CLOSE_PARANTHESES);
        } else if (GeneratorUtil.isGenerateDefaultValue()){
            builder.append(ORMConverterUtil.OPEN_PARANTHESES).append(ENUM_TYPE_ORDINAL).append(ORMConverterUtil.CLOSE_PARANTHESES);  
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        List<String> importSnippets = new ArrayList<>();
        if (mapKey) {
            importSnippets.add(MAP_KEY_ENUMERATED_FQN);
        } else {
            importSnippets.add(ENUMERATED_FQN);
        }
        
        if(ENUM_TYPE_STRING.equals(value) || ENUM_TYPE_ORDINAL.equals(value) || GeneratorUtil.isGenerateDefaultValue()){
            importSnippets.add(ENUM_TYPE_FQN);
        }
        return importSnippets;
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
