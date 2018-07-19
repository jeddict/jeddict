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
import static io.github.jeddict.jcode.jpa.JPAConstants.BASIC;
import static io.github.jeddict.jcode.jpa.JPAConstants.BASIC_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.FETCH_TYPE_FQN;
import io.github.jeddict.settings.code.CodePanel;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;

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
        
        StringBuilder builder = new StringBuilder();
       builder.append("@").append(BASIC);
        if (!CodePanel.isGenerateDefaultValue()) {
            if (optional == true && getFetchType() == null){
                return builder.toString();
            }
        }
        
        builder.append("(");

        if (CodePanel.isGenerateDefaultValue() || optional == false) {
            builder.append("optional=").append(optional).append(COMMA);
        }
        
        if (getFetchType() != null) {
            builder.append("fetch = ");
            builder.append(getFetchType());
            builder.append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (fetchType == null) {
            return Collections.singletonList(BASIC_FQN);
        }

        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(BASIC_FQN);
        importSnippets.add(FETCH_TYPE_FQN);

        return importSnippets;
    }
}
