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

import static io.github.jeddict.jcode.JSONBConstants.JSONB_DATE_FORMAT;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_DATE_FORMAT_FQN;
import io.github.jeddict.jsonb.spec.JsonbFormat;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.Collections;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DateFormatSnippet implements Snippet {

    private final JsonbFormat format;

    public DateFormatSnippet(JsonbFormat format) {
        this.format = format;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(JSONB_DATE_FORMAT).append(OPEN_PARANTHESES);

        if (isNotBlank(format.getValue())) {
            builder.append(isNotBlank(format.getLocale()) ? "value=" : EMPTY)
                    .append(QUOTE)
                    .append(format.getValue())
                    .append(QUOTE)
                    .append(COMMA);
        }
        if (isGenerateDefaultValue() || isNotBlank(format.getLocale())) {
            builder.append("locale=\"")
                    .append(format.getLocale())
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(JSONB_DATE_FORMAT_FQN);
    }
}
