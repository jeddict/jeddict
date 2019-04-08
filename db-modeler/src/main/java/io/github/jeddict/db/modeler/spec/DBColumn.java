/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.db.modeler.spec;

import io.github.jeddict.jpa.spec.extend.FlowPin;
import org.netbeans.modules.db.metadata.model.api.Column;
import org.netbeans.modules.db.metadata.model.api.Nullable;

/**
 *
 * @author Gaurav Gupta
 */
public class DBColumn extends FlowPin {

    private final Column column;
    private String name;

    public DBColumn(String name, Column column) {
        this.column = column;
        this.name = name;
    }

    public String getTableName() {
        return column.getParent().getName();
    }

    public String getTypeName() {
        return column.getTypeName();
    }

    public Boolean getNullable() {
        return column.getNullable() == Nullable.NULLABLE;
    }

    public Integer getLength() {
        return column.getLength();
    }

    public Short getScale() {
        return column.getScale();
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Column getColumn() {
        return column;
    }
    
}
