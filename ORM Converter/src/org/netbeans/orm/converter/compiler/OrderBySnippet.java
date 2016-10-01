/**
 * Copyright [2014] Gaurav Gupta
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.jpa.JPAConstants.ORDER_BY;
import static org.netbeans.jcode.jpa.JPAConstants.ORDER_BY_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class OrderBySnippet implements Snippet {

    private List<String> orderList = Collections.EMPTY_LIST;

    public List<String> getOrderList() {
        return orderList;
    }

    public OrderBySnippet() {
    }

    public OrderBySnippet(String orderBy) {
        if (!StringUtils.EMPTY.equals(orderBy)) {
            orderList = new ArrayList<String>();
            orderList.add(orderBy);
        }
    }

    public void setOrderList(List<String> orderList) {
        if (orderList != null) {
            this.orderList = orderList;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (orderList.isEmpty()) {
            return "@" + ORDER_BY;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(ORDER_BY).append("(");

        for (String order : orderList) {
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(order);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(ORDER_BY_FQN);
    }
}
