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
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.jpa.JPAConstants.INDEX;
import static io.github.jeddict.jcode.jpa.JPAConstants.INDEX_FQN;
import io.github.jeddict.jpa.spec.Index;
import io.github.jeddict.jpa.spec.OrderType;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

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
        

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(INDEX).append("(");

        if (StringUtils.isNotBlank(index.getName())) {
            builder.append("name=\"");
            builder.append(index.getName());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }
                
        
        builder.append("columnList=\"");
        for (OrderbyItem orderbyItem : index.getColumnList()) {
            String property = orderbyItem.getProperty();
            OrderType orderType = orderbyItem.getOrderType();
            builder.append(property);
            if(orderType!=null){
                builder.append(" ").append(orderType.name());
            }
            builder.append(ORMConverterUtil.COMMA);
        }
        builder.setLength(builder.length() - 1);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);



        if (index.isUnique()!=null && index.isUnique()) {
            builder.append("unique=true");
            builder.append(ORMConverterUtil.COMMA);
        }
        
        builder.setLength(builder.length() - 1);

        
        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(INDEX_FQN);

    }
}
