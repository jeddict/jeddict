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
package io.github.jeddict.relation.mapper.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Index;
import io.github.jeddict.jpa.spec.UniqueConstraint;
import io.github.jeddict.jpa.spec.extend.FlowNode;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class DBTable extends FlowNode {

    private String name;
    private Entity entity;

    private final Map<String, DBColumn> columns = new LinkedHashMap<>();

    public DBTable(String name) {
        this(name, null);
    }

    public DBTable(String name, Entity entity) {
        this.name = name;
        this.entity = entity;
    }

    public void sortColumns() {
        //TODO
//        columns.values().forEach(column -> {
//            Attribute attribute = column.getAttribute();
//            JavaClass javaClass = attribute.getJavaClass();

//        });
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

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    public abstract Set<UniqueConstraint> getUniqueConstraints();
    
    public abstract List<Index> getIndexes();
}
