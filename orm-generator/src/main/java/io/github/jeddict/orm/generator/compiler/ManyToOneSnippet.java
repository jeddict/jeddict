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

import static io.github.jeddict.jcode.JPAConstants.CASCADE_TYPE_FQN;
import static io.github.jeddict.jcode.JPAConstants.FETCH_TYPE_FQN;
import static io.github.jeddict.jcode.JPAConstants.ID;
import static io.github.jeddict.jcode.JPAConstants.ID_FQN;
import static io.github.jeddict.jcode.JPAConstants.MANY_TO_ONE;
import static io.github.jeddict.jcode.JPAConstants.MANY_TO_ONE_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAPS_ID;
import static io.github.jeddict.jcode.JPAConstants.MAPS_ID_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.getCommaSeparatedString;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class ManyToOneSnippet extends SingleRelationAttributeSnippet {

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (isPrimaryKey()) {
            builder.append(AT);
            if (mapsId == null) {
                builder.append(ID);
            } else if (mapsId.trim().isEmpty()) {
                builder.append(MAPS_ID);
            } else {
                builder.append(MAPS_ID).append("(\"").append(mapsId).append("\")");
            }
        }
        builder.append(AT).append(MANY_TO_ONE);

        if (!isGenerateDefaultValue()
                && optional == true
                && isBlank(getFetchType())
                && getCascadeTypes().isEmpty()) {
                return builder.toString();
        }

        builder.append(OPEN_PARANTHESES)
                .append(attributeExp("fetch", getFetchType()));

        if (isGenerateDefaultValue() || optional == false) {
            builder.append("optional = ")
                    .append(optional)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() && isNotBlank(getTargetEntity())) {
            builder.append("targetEntity = ")
                    .append(getTargetEntity())
                    .append(COMMA);
        }

        if (!getCascadeTypes().isEmpty()) {
            builder.append("cascade = ");
            if (getCascadeTypes().size() > 1) {
                builder.append(OPEN_BRACES)
                        .append(getCommaSeparatedString(getCascadeTypes()))
                        .append(CLOSE_BRACES);
            } else {
                builder.append(getCascadeTypes().get(0));
            }
            builder.append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (isPrimaryKey()) {
            if (mapsId == null) {
                imports.add(ID_FQN);
            } else {
                imports.add(MAPS_ID_FQN);
            }
        }
        imports.add(MANY_TO_ONE_FQN);

        if (getFetchType() != null) {
            imports.add(FETCH_TYPE_FQN);
        }

        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            imports.add(CASCADE_TYPE_FQN);
        }

        return imports;
    }

}
