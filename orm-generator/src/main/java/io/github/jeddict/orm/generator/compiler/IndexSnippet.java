/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.jcode.JPAConstants.INDEX;
import static io.github.jeddict.jcode.JPAConstants.INDEX_FQN;
import io.github.jeddict.jpa.spec.Index;
import io.github.jeddict.jpa.spec.OrderType;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.SPACE;
import java.util.Collection;
import static java.util.Collections.singleton;

public class IndexSnippet implements Snippet {

    private final Index index;

    public IndexSnippet(Index index) {
        this.index = index;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (index.getColumnList().isEmpty()) {
            throw new InvalidDataException("Missing Index columnList");
        }

        return annotate(
                INDEX,
                attribute("name", index.getName()),
                getColumnList(),
                attribute("unique", true, val -> index.isUnique() != null && index.isUnique())
        );
    }
    
    private String getColumnList() {
        StringBuilder builder = new StringBuilder();
        builder.append("columnList=").append(QUOTE);
        for (OrderbyItem orderbyItem : index.getColumnList()) {
            String property = orderbyItem.getProperty();
            OrderType orderType = orderbyItem.getOrderType();
            builder.append(property);
            if (orderType != null) {
                builder.append(SPACE).append(orderType.name());
            }
            builder.append(COMMA);
        }
        builder.setLength(builder.length() - 1);
        builder.append(QUOTE).append(COMMA);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(INDEX_FQN);

    }
}
