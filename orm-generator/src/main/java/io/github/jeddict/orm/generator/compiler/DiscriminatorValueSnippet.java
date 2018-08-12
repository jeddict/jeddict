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

import static io.github.jeddict.jcode.JPAConstants.DISCRIMINATOR_VALUE;
import static io.github.jeddict.jcode.JPAConstants.DISCRIMINATOR_VALUE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import static java.util.Collections.singleton;
import static org.apache.commons.lang.StringUtils.isBlank;

public class DiscriminatorValueSnippet implements Snippet {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDefault() {
        return isBlank(value);
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (value == null) {
            throw new InvalidDataException("DiscriminatorValue.value must be null");
        }
        return AT
                + DISCRIMINATOR_VALUE
                + OPEN_PARANTHESES
                + QUOTE
                + value
                + QUOTE
                + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(DISCRIMINATOR_VALUE_FQN);
    }
}
