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
import java.util.Collections;
import java.util.List;

public class ManyToOneSnippet extends AbstractRelationDefSnippet
        implements RelationDefSnippet {

    private boolean optional = false;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getSnippet() throws InvalidDataException {

        if (optional == true
                && getTargetEntity() == null
                && getFetchType() == null
                && getCascadeTypes().isEmpty()) {

            return "@ManyToOne";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("@ManyToOne(");

        if (optional == false) {
            builder.append("optional=false,");
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

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {

        if (getFetchType() == null
                && getCascadeTypes().isEmpty()) {

            return Collections.singletonList("javax.persistence.ManyToOne");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.ManyToOne");

        if (getFetchType() != null) {
            importSnippets.add("javax.persistence.FetchType");
        }

        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            importSnippets.add("javax.persistence.CascadeType");
        }

        return importSnippets;
    }
}
