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
import static io.github.jeddict.jcode.JPAConstants.CONVERT_NOSQL_FQN;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import io.github.jeddict.util.StringUtils;

public class ConvertSnippet extends ORMSnippet {

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
        StringBuilder builder = new StringBuilder();
        
        if (isNoSQL()) {
            builder.append(converterClass.getClassNameWithClassSuffix());
        } else {
            if (converterClass != null) {
                builder.append(attributeExp("converter", converterClass.getClassNameWithClassSuffix()));
            }
            if (isGenerateDefaultValue() || disableConversion) {
                builder.append(attributeExp("disableConversion", disableConversion));
            }
            builder.append(attribute("attributeName", attributeName));
        }

        return annotate(CONVERT, builder.toString());
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
          imports.add(isNoSQL() ? CONVERT_NOSQL_FQN : CONVERT_FQN);
        if (converterClass != null) {
            imports.add(converterClass.getFQClassName());
        }
        return imports;
    }
}
