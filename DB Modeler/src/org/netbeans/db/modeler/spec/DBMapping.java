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
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.specification.model.document.IDefinitionElement;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;

/**
 *
 * @author Gaurav Gupta
 */
public class DBMapping implements IDefinitionElement, IRootElement {

    private String id;
    private String name;
    private final Map<String, DBTable> tables = new HashMap<>();
    private final Map<String, String> creationQueries = new HashMap<>();
    private final Map<String, List<String>> alterationQueries = new HashMap<>();

    /**
     * @return the tables
     */
    public Collection<DBTable> getTables() {
        return tables.values();
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(List<DBTable> tables) {
        tables.stream().forEach(t -> addTable(t));
    }

    public DBTable getTable(String name) {
        return this.tables.get(name);
    }

    public void addTable(DBTable table) {
        this.tables.put(table.getName(), table);
    }

    public void removeTable(DBTable table) {
        this.tables.remove(table.getName());
    }

    public List<DBTable> findAllTable(String tableName) {
        List<DBTable> tablesResult = new ArrayList<>();
        for (DBTable table : tables.values()) {
            if (tableName.equals(table.getName())) {
                tablesResult.add(table);
            }
        }
        return tablesResult;
    }

    @Override
    public void removeBaseElement(IBaseElement baseElement_In) {
        if (baseElement_In instanceof DBTable) {
            removeTable((DBTable) baseElement_In);
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }
    }

    @Override
    public void addBaseElement(IBaseElement baseElement_In) {
        if (baseElement_In instanceof DBTable) {
            addTable((DBTable) baseElement_In);
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    @Override
    public Map<String, String> getCustomAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCustomAttributes(Map<String, String> customAttributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getRootElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRootElement(Object rootElement) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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

    public String getCreateQuery(String table) {
        return creationQueries.get(table);
    }

    public String putCreateQuery(String table, String query) {
        return creationQueries.put(table, query);
    }

    public List<String> getAlterQuery(String table) {
        return alterationQueries.get(table);
    }

    public void putAlterQuery(String table, String query) {
        if(getAlterQuery(table)==null){
            alterationQueries.put(table, new ArrayList<>());
        }
        getAlterQuery(table).add(query);
    }

    public String getSQL() {
        StringBuilder queryList = new StringBuilder();
        creationQueries.values().stream().forEach((query) -> {
            queryList.append(query).append(";\n");
        });

        alterationQueries.values().stream().flatMap(queries -> queries.stream()).forEach((query) -> {
            queryList.append(query).append(";\n");
        });

        return queryList.toString();
    }
}
