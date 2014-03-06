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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.Collections;

import java.util.List;

public class OrderBySnippet implements Snippet {

    private List<String> orderList = Collections.EMPTY_LIST;

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        if (orderList != null) {
            this.orderList = orderList;
        }
    }

    public String getSnippet() throws InvalidDataException {

        if (orderList.isEmpty()) {
            return "@OrderBy";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@OrderBy(");

        for (String order : orderList) {
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(order);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList("javax.persistence.OrderBy");
    }
}
