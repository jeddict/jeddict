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

import static io.github.jeddict.jcode.JPAConstants.ORDER_COLUMN;
import static io.github.jeddict.jcode.JPAConstants.ORDER_COLUMN_FQN;
import io.github.jeddict.jpa.spec.OrderColumn;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import static java.util.Collections.singleton;

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
        return annotate(
                ORDER_COLUMN,
                attribute("name", name),
                attribute("insertable", insertable, val -> isGenerateDefaultValue() || val == false),
                attribute("nullable", nullable, val -> isGenerateDefaultValue() || val == false),
                attribute("updatable", updatable, val -> isGenerateDefaultValue() || val == false),
                attribute("columnDefinition", columnDefinition)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(ORDER_COLUMN_FQN);
    }

}
