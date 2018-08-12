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

import static io.github.jeddict.jcode.JSONBConstants.JSONB_PROPERTY_ORDER;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_PROPERTY_ORDER_FQN;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import static java.util.Collections.singleton;
import java.util.List;
import static java.util.stream.Collectors.joining;

public class PropertyOrderSnippet implements Snippet {

    private final List<String> propertyOrder;

    public PropertyOrderSnippet(List<String> propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(JSONB_PROPERTY_ORDER)
                .append(OPEN_PARANTHESES)
                .append(OPEN_BRACES)
                .append(propertyOrder.stream().collect(joining("\", \"", QUOTE, QUOTE)))
                .append(CLOSE_BRACES)
                .append(CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(JSONB_PROPERTY_ORDER_FQN);
    }
}
