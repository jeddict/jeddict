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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.jpa.JPAConstants.ORDER_BY;
import static io.github.jeddict.jcode.jpa.JPAConstants.ORDER_BY_FQN;
import io.github.jeddict.settings.code.CodePanel;
import io.github.jeddict.jpa.spec.OrderBy;
import io.github.jeddict.jpa.spec.OrderType;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.SPACE;

public class OrderBySnippet implements Snippet {

    private List<OrderbyItem> orderList = Collections.<OrderbyItem>emptyList();

    public OrderBySnippet(OrderBy orderBy) {
        orderList = orderBy.getAttributes();
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (orderList.isEmpty()) {
            return "@" + ORDER_BY;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(ORDER_BY).append("(");
        builder.append(QUOTE);

        for (OrderbyItem order : orderList) {
            boolean propertyExist = !StringUtils.isBlank(order.getProperty());
            if (propertyExist) {
                builder.append(order.getProperty());
            }
            if (order.getOrderType() != null) {
                if (propertyExist) { //ElementCollection Basic type then property not exist
                    builder.append(SPACE);
                }
                builder.append(order.getOrderType().name());
            } else if (CodePanel.isGenerateDefaultValue()) {
                if (propertyExist) {
                    builder.append(SPACE);
                }
                builder.append(OrderType.ASC.name());
            }
            builder.append(COMMA).append(SPACE);
        }
        builder.setLength(builder.length() - 2);
        builder.append(QUOTE).append(CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(ORDER_BY_FQN);
    }
}
