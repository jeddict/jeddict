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
import static io.github.jeddict.jcode.JPAConstants.PERSISTENCE_PACKAGE_PREFIX;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.TAB;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.getCommaSeparatedString;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public abstract class MultiRelationAttributeSnippet extends AbstractRelationDefSnippet /*implements CollectionTypeHandler*/ {

    protected String collectionType;
    protected String collectionImplType;
    protected String mappedBy = null;
    protected MapKeySnippet mapKeySnippet;

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    /**
     * @return the mapKeySnippet
     */
    public MapKeySnippet getMapKeySnippet() {
        return mapKeySnippet;
    }

    /**
     * @param mapKeySnippet the mapKeySnippet to set
     */
    public void setMapKeySnippet(MapKeySnippet mapKeySnippet) {
        this.mapKeySnippet = mapKeySnippet;
    }

    public abstract String getType();

    @Override
    public String getSnippet() throws InvalidDataException {

        StringBuilder builder = new StringBuilder();
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet())
                    .append(NEW_LINE)
                    .append(TAB);
        }
        builder.append(AT).append(getType());

        if (mappedBy == null
                && getFetchType() == null
                && getCascadeTypes().isEmpty()) {
            return builder.toString();
        }

        builder.append(OPEN_PARANTHESES)
                .append(buildString("mappedBy", mappedBy))
                .append(buildExp("fetch", getFetchType()));

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
        imports.add(PERSISTENCE_PACKAGE_PREFIX + getType());
        if (getFetchType() != null) {
            imports.add(FETCH_TYPE_FQN);
        }
        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            imports.add(CASCADE_TYPE_FQN);
        }
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            imports.addAll(mapKeySnippet.getImportSnippets());
        }
        return imports;
    }

}
