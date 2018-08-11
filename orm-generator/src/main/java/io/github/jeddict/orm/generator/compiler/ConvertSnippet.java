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

import static io.github.jeddict.jcode.JPAConstants.CONVERT;
import static io.github.jeddict.jcode.JPAConstants.CONVERT_FQN;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ConvertSnippet implements Snippet {

    private final ClassHelper converterClass;
    private final boolean disableConversion;
    private final String attributeName;

    public ConvertSnippet(Convert convert) {
        converterClass = StringUtils.isNotBlank(convert.getConverter()) ? new ClassHelper(convert.getConverter()) : null;
        disableConversion = convert.isDisableConversion();
        attributeName = convert.getAttributeName();
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(CONVERT)
                .append(OPEN_PARANTHESES);

        if (converterClass != null) {
            builder.append("converter=")
                    .append(converterClass.getClassNameWithClassSuffix())
                    .append(COMMA);
        }

        if (isGenerateDefaultValue() || disableConversion) {
            builder.append("disableConversion=")
                    .append(disableConversion)
                    .append(COMMA);
        }

        if (isNotBlank(attributeName)) {
            builder.append("attributeName=\"")
                    .append(attributeName)
                    .append(QUOTE)
                    .append(COMMA);
        }
        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(CONVERT_FQN);
        if (converterClass != null) {
            importSnippets.add(converterClass.getFQClassName());
        }
        return importSnippets;
    }
}
