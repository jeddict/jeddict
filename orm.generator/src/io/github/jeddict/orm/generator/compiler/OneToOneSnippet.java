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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.jpa.JPAConstants.CASCADE_TYPE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.FETCH_TYPE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ID;
import static io.github.jeddict.jcode.jpa.JPAConstants.ID_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.MAPS_ID;
import static io.github.jeddict.jcode.jpa.JPAConstants.MAPS_ID_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ONE_TO_ONE;
import static io.github.jeddict.jcode.jpa.JPAConstants.ONE_TO_ONE_FQN;
import io.github.jeddict.settings.code.CodePanel;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class OneToOneSnippet extends SingleRelationAttributeSnippet {

    private boolean orphanRemoval = false;
    private String mappedBy = null;

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (isPrimaryKey()) {
            builder.append("@");
            if (mapsId == null) {
                builder.append(ID);
            } else if (mapsId.trim().isEmpty()) {
                builder.append(MAPS_ID);
            } else {
                builder.append(MAPS_ID).append("(\"").append(mapsId).append("\")");
            }
        }
        builder.append("@").append(ONE_TO_ONE);
        if (!CodePanel.isGenerateDefaultValue()) {
            if (mappedBy == null
                    && optional == true
                    && getFetchType() == null
                    && getCascadeTypes().isEmpty()) {
                return builder.toString();
            }
        }

        builder.append("(");

        if (CodePanel.isGenerateDefaultValue() || optional == false) {
            builder.append("optional=").append(optional).append(",");
        }
        
        if (CodePanel.isGenerateDefaultValue() || orphanRemoval == true) {
            builder.append("orphanRemoval=").append(orphanRemoval).append(",");
        }

        if (!getCascadeTypes().isEmpty()) {
            builder.append("cascade={");

            String encodedString = ORMConverterUtil.getCommaSeparatedString(
                    getCascadeTypes());

            builder.append(encodedString);
            builder.append("},");
        }

        if (getFetchType() != null) {
            builder.append("fetch = ");
            builder.append(getFetchType());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (CodePanel.isGenerateDefaultValue() && getTargetEntity() != null) {
            builder.append("targetEntity = ");
            builder.append(getTargetEntity());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (getMappedBy() != null) {
            builder.append("mappedBy = ");
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(getMappedBy());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (getFetchType() == null
                && getCascadeTypes().isEmpty() && !isPrimaryKey()) {

            return Collections.singletonList(ONE_TO_ONE_FQN);
        }

        List<String> importSnippets = new ArrayList<>();

        if (isPrimaryKey()) {
            if (mapsId == null) {
                importSnippets.add(ID_FQN);
            } else {
                importSnippets.add(MAPS_ID_FQN);
            }
        }

        importSnippets.add(ONE_TO_ONE_FQN);

        if (getFetchType() != null) {
            importSnippets.add(FETCH_TYPE_FQN);
        }

        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            importSnippets.add(CASCADE_TYPE_FQN);
        }
        return importSnippets;
    }

    /**
     * @return the orphanRemoval
     */
    public boolean isOrphanRemoval() {
        return orphanRemoval;
    }

    /**
     * @param orphanRemoval the orphanRemoval to set
     */
    public void setOrphanRemoval(boolean orphanRemoval) {
        this.orphanRemoval = orphanRemoval;
    }
}
