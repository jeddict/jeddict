/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class BasicSnippet extends ORMSnippet {

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
        return annotate(
                BASIC,
                attribute("optional", optional, val -> isGenerateDefaultValue() || val == false),
                attributeExp("fetch", getFetchType())
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(BASIC_FQN);
        if (isNotBlank(getFetchType())) {
            imports.add(FETCH_TYPE_FQN);
        }
        return imports;
    }

}
