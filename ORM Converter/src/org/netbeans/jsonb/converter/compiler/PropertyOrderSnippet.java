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

import org.netbeans.orm.converter.compiler.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_PROPERTY_ORDER;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_PROPERTY_ORDER_FQN;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_BRACES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_BRACES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.QUOTE;

public class PropertyOrderSnippet implements Snippet {

    private final List<String> propertyOrder;

    public PropertyOrderSnippet(List<String> propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@")
                .append(JSONB_PROPERTY_ORDER)
                .append(OPEN_PARANTHESES)
                .append(OPEN_BRACES)
                .append(propertyOrder.stream().collect(joining("\", \"", QUOTE, QUOTE)))
                .append(CLOSE_BRACES)
                .append(CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(JSONB_PROPERTY_ORDER_FQN);
    }
}
