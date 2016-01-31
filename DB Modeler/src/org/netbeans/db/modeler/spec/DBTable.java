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
package org.netbeans.db.modeler.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;

/**
 *
 * @author Gaurav Gupta
 */
public class DBTable extends FlowNode {

    private String name;
    private Entity entity;

    private final Map<String, DBColumn> columns = new HashMap<>();

    public DBTable(String name) {
        this(name, null);
    }

    public DBTable(String name, Entity entity) {
        this.name = name;
        this.entity = entity;
        if (this.getClass() == DBTable.class) {
            entity.getTable().setGeneratedName(name);
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

//    /**
//     * @return the primaryKeys
//     */
//    public List<PrimaryKey> getPrimaryKeys() {
//        return primaryKeys;
//    }
//
//    /**
//     * @param primaryKeys the primaryKeys to set
//     */
//    public void setPrimaryKeys(List<PrimaryKey> primaryKeys) {
//        this.primaryKeys = primaryKeys;
//    }
//
//    /**
//     * @return the foreignKeys
//     */
//    public List<ForeignKey> getForeignKeys() {
//        return foreignKeys;
//    }
//
//    /**
//     * @param foreignKeys the foreignKeys to set
//     */
//    public void setForeignKeys(List<ForeignKey> foreignKeys) {
//        this.foreignKeys = foreignKeys;
//    }
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
        columns.stream().forEach(c -> addColumn(c));
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

}
