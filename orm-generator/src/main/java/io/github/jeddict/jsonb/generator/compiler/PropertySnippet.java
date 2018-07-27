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
package io.github.jeddict.jsonb.generator.compiler;

import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_PROPERTY;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_PROPERTY_FQN;
import static io.github.jeddict.settings.code.CodePanel.isGenerateDefaultValue;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;

public class PropertySnippet implements Snippet {

    private final String property;
    private final Boolean nillable;

    public PropertySnippet(String property, Boolean nillable) {
        this.property = property;
        this.nillable = nillable;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(JSONB_PROPERTY).append("(");
        if (!StringUtils.isBlank(property)) {
            builder.append((isGenerateDefaultValue() || nillable)?"value=\"":"\"");
            builder.append(property);
            builder.append(QUOTE);
            builder.append(COMMA);
        }
        if (isGenerateDefaultValue() || nillable) {
            builder.append("nillable=");
            builder.append(nillable);
            builder.append(COMMA);
        }
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(JSONB_PROPERTY_FQN);
    }
}
