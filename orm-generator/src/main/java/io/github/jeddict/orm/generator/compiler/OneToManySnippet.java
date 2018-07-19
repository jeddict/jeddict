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

import io.github.jeddict.settings.code.CodePanel;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class OneToManySnippet extends MultiRelationAttributeSnippet {

    private boolean orphanRemoval = false;
    
    @Override
    public String getSnippet() throws InvalidDataException {

        if (!CodePanel.isGenerateDefaultValue()) {
            if (mappedBy == null
                    && orphanRemoval == false
                    && getFetchType() == null
                    && getCascadeTypes().isEmpty() 
                    && (mapKeySnippet==null || mapKeySnippet.isEmpty())) {
                return "@" + getType();
            }
        }
               
        StringBuilder builder = new StringBuilder();
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet())
                    .append(ORMConverterUtil.NEW_LINE)
                    .append(ORMConverterUtil.TAB);
        }

        builder.append("@").append(getType()).append("(");

        if (!getCascadeTypes().isEmpty()) {
            builder.append("cascade={");
            String encodedString = ORMConverterUtil.getCommaSeparatedString(getCascadeTypes());
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

        if (CodePanel.isGenerateDefaultValue() || orphanRemoval == true) {
            builder.append("orphanRemoval=").append(orphanRemoval).append(",");
        }
        
        if (mappedBy != null) {
            builder.append("mappedBy = ");
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(mappedBy);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

       @Override
    public String getType() {
        return "OneToMany";
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
