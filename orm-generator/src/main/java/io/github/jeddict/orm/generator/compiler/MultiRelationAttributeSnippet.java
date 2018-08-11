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
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.getCommaSeparatedString;
import io.github.jeddict.settings.code.CodePanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    
    public abstract String getType();

    @Override
    public String getSnippet() throws InvalidDataException {

        if (mappedBy == null 
                && getFetchType() == null
                && getCascadeTypes().isEmpty() 
                && (mapKeySnippet==null || mapKeySnippet.isEmpty())) {
            return "@" + getType();
        }

        StringBuilder builder = new StringBuilder();
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet())
                    .append(ORMConverterUtil.NEW_LINE)
                    .append(ORMConverterUtil.TAB);
        }

        builder.append("@").append(getType()).append(OPEN_PARANTHESES);

        if (mappedBy != null) {
            builder.append("mappedBy = ")
                    .append(QUOTE)
                    .append(mappedBy)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (getFetchType() != null) {
            builder.append("fetch = ")
                    .append(getFetchType())
                    .append(COMMA);
        }

        if (CodePanel.isGenerateDefaultValue() && getTargetEntity() != null) {
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
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(PERSISTENCE_PACKAGE_PREFIX + getType());
        if (getFetchType() != null) {
            importSnippets.add(FETCH_TYPE_FQN);
        }
        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            importSnippets.add(CASCADE_TYPE_FQN);
        }
        if(mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            importSnippets.addAll(mapKeySnippet.getImportSnippets());
        }
        return importSnippets;
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

}
