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

import static io.github.jeddict.jcode.JPAConstants.ORDER_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.ORDER_COLUMN_FQN;
import io.github.jeddict.jpa.spec.OrderColumn;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import static java.util.Collections.singleton;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class OrderColumnSnippet implements Snippet {

    private boolean insertable = true;
    private boolean nullable = true;
    private boolean updatable = true;
    private String columnDefinition = null;
    private String name = null;

    public OrderColumnSnippet(OrderColumn orderColumn) {
        this.name = orderColumn.getName();
        this.columnDefinition = orderColumn.getColumnDefinition();
        this.insertable = orderColumn.isInsertable();
        this.nullable = orderColumn.isNullable();
        this.updatable = orderColumn.isUpdatable();
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(ORDER_COLUMN);

        if (!isGenerateDefaultValue()) {
            if (insertable == true
                    && nullable == true
                    && updatable == true
                    && isBlank(columnDefinition)
                    && isBlank(name)) {
                return builder.toString();
            }
        }

        builder.append(OPEN_PARANTHESES);

        if (isNotBlank(name)) {
            builder.append("name=\"")
                    .append(name)
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append(" insertable=")
                    .append(insertable)
                    .append(COMMA);
        } else if (insertable == false) {
            builder.append(" insertable=")
                    .append(insertable)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append(" nullable=")
                    .append(nullable)
                    .append(COMMA);
        } else if (nullable == false) {
            builder.append(" nullable=")
                    .append(nullable)
                    .append(COMMA);
        }

        if (isGenerateDefaultValue()) {
            builder.append(" updatable=")
                    .append(updatable)
                    .append(COMMA);
        } else if (updatable == false) {
            builder.append(" updatable=")
                    .append(updatable)
                    .append(COMMA);
        }

        if (isNotBlank(columnDefinition)) {
            builder.append(" columnDefinition=\"")
                    .append(columnDefinition)
                    .append(QUOTE)
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(ORDER_COLUMN_FQN);
    }

}
