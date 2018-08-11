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

import static io.github.jeddict.jcode.JPAConstants.BASIC;
import static io.github.jeddict.jcode.JPAConstants.BASIC_FQN;
import static io.github.jeddict.jcode.JPAConstants.FETCH_TYPE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class BasicSnippet implements Snippet {

    private String fetchType = null;
    private boolean optional = true;

    public String getFetchType() {
        if (fetchType != null) {
            return "FetchType." + fetchType;
        }
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(BASIC);

        if (!isGenerateDefaultValue()) {
            if (optional == true && isBlank(getFetchType())) {
                return builder.toString();
            }
        }
        
        builder.append(OPEN_PARANTHESES);

        if (isGenerateDefaultValue() || optional == false) {
            builder.append("optional=")
                    .append(optional)
                    .append(COMMA);
        }
        
        if (isNotBlank(getFetchType())) {
            builder.append("fetch = ")
                    .append(getFetchType())
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(BASIC_FQN);
        if (isNotBlank(getFetchType())) {
            importSnippets.add(FETCH_TYPE_FQN);
        }
        return importSnippets;
    }

}
