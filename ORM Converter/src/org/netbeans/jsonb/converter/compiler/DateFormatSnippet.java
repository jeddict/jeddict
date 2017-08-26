/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.converter.compiler;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_DATE_FORMAT;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_DATE_FORMAT_FQN;
import static org.netbeans.jpa.modeler.settings.code.CodePanel.isGenerateDefaultValue;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbFormat;
import org.netbeans.orm.converter.compiler.*;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.COMMA;
import static org.netbeans.orm.converter.util.ORMConverterUtil.QUOTE;

public class DateFormatSnippet implements Snippet {

    private final JsonbFormat format;

    public DateFormatSnippet(JsonbFormat format) {
        this.format = format;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(JSONB_DATE_FORMAT).append("(");
        if (!StringUtils.isBlank(format.getValue())) {
            builder.append(!StringUtils.isBlank(format.getLocale())?"value=\"":"\"");
            builder.append(format.getValue());
            builder.append(QUOTE);
            builder.append(COMMA);
        }
        if (isGenerateDefaultValue() || !StringUtils.isBlank(format.getLocale())) {
            builder.append("locale=\"");
            builder.append(format.getLocale());
            builder.append(QUOTE);
            builder.append(COMMA);
        }
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(JSONB_DATE_FORMAT_FQN);
    }
}
