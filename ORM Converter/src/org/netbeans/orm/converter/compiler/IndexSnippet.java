/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static org.netbeans.jcode.jpa.JPAConstants.INDEX;
import static org.netbeans.jcode.jpa.JPAConstants.INDEX_FQN;
import org.netbeans.jpa.modeler.spec.Index;
import org.netbeans.jpa.modeler.spec.OrderType;
import org.netbeans.jpa.modeler.spec.extend.OrderbyItem;
import org.netbeans.orm.converter.util.ORMConverterUtil;

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
    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(INDEX_FQN);

    }
}
