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
import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.generator.GeneratorUtil;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class OneToOneSnippet extends AbstractRelationDefSnippet
        implements RelationDefSnippet {

    private boolean optional = false;
    private String mappedBy = null;
    private String mapsId;
    private boolean primaryKey;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (isPrimaryKey()) {
            if (mapsId == null) {
                builder.append("@Id");
            } else if (mapsId.trim().isEmpty()) {
                builder.append("@MapsId");
            } else {
                builder.append("@MapsId(\"").append(mapsId).append("\")");
            }
        }
        builder.append("@OneToOne");
        if (!GeneratorUtil.isGenerateDefaultValue()) {
        if (mappedBy == null
                && optional == true
                && getTargetEntity() == null
                && getFetchType() == null
                && getCascadeTypes().isEmpty()) {
            return builder.toString();
        }
        }

        builder.append("(");

        if (GeneratorUtil.isGenerateDefaultValue() || optional == false) {
                builder.append("optional=").append(optional).append(",");
        }

        if (!getCascadeTypes().isEmpty()) {
            builder.append("cascade={");

            String encodedString = ORMConverterUtil.getCommaSeparatedString(
                    getCascadeTypes());

            builder.append(encodedString);
            builder.append("},");
        }

        if (getFetchType() != null) {
            builder.append("fetch=");
            builder.append(getFetchType());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (getTargetEntity() != null) {
            builder.append("targetEntity=");
            builder.append(getTargetEntity());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (getMappedBy() != null) {   //Added By Gaurav Gupta
            builder.append("mappedBy=");
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(getMappedBy());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {

        if (getFetchType() == null
                && getCascadeTypes().isEmpty() && !isPrimaryKey()) {

            return Collections.singletonList("javax.persistence.OneToOne");
        }

        List<String> importSnippets = new ArrayList<String>();

        if (isPrimaryKey()) {
            if (mapsId == null) {
                importSnippets.add("javax.persistence.Id");
            } else {
                importSnippets.add("javax.persistence.MapsId");
            }
        }

        importSnippets.add("javax.persistence.OneToOne");

        if (getFetchType() != null) {
            importSnippets.add("javax.persistence.FetchType");
        }

        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            importSnippets.add("javax.persistence.CascadeType");
        }

        return importSnippets;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the mapsId
     */
    public String getMapsId() {
        return mapsId;
    }

    /**
     * @param mapsId the mapsId to set
     */
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }
}
