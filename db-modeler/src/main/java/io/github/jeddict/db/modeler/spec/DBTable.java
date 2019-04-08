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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import org.netbeans.modules.db.metadata.model.api.Table;

/**
 *
 * @author Gaurav Gupta
 */
public class DBTable extends FlowNode {

    private String name;
    private boolean primary;
    private final Table table;

    private final Map<String, DBColumn> columns = new LinkedHashMap<>();

    public DBTable(String name, Table table) {
        this.name = name;
        this.table = table;
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

    /**
     * @return the primary
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * @param primary the primary to set
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    
    public String getSchemaName() {
        return table.getParent().getName();
    }
    
    public String getCatalogName() {
        return table.getParent().getParent().getName();
    }
    
    /**
     * @return the columns
     */
    public Collection<DBColumn> getColumns() {
        return columns.values();
    }

    public Collection<DBColumn> findColumns(String columnName) {
        List<DBColumn> columnResult = new ArrayList<>();
        columns.values().stream().filter(c -> columnName.equals(c.getName())).forEach(c -> columnResult.add(c));
        return columnResult;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(List<DBColumn> columns) {
        columns.forEach(c -> addColumn(c));
    }

    public DBColumn getColumn(String name) {
        return this.columns.get(name.toUpperCase());
    }

    public void addColumn(DBColumn column) {
        this.columns.put(column.getName().toUpperCase(), column);
    }

    public void removeColumn(DBColumn column) {
        this.columns.remove(column.getName().toUpperCase());
    }

}
