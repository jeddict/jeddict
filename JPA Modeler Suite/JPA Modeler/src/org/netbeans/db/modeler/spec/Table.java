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
package org.netbeans.db.modeler.spec;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;

/**
 *
 * @author Gaurav Gupta
 */
public class Table extends FlowNode {

    private String name;

    private List<PrimaryKey> primaryKeys;
    private List<ForiegnKey> foriegnKeys;
    private List<Column> columns;

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

    /**
     * @return the primaryKeys
     */
    public List<PrimaryKey> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * @param primaryKeys the primaryKeys to set
     */
    public void setPrimaryKeys(List<PrimaryKey> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    /**
     * @return the foriegnKeys
     */
    public List<ForiegnKey> getForiegnKeys() {
        return foriegnKeys;
    }

    /**
     * @param foriegnKeys the foriegnKeys to set
     */
    public void setForiegnKeys(List<ForiegnKey> foriegnKeys) {
        this.foriegnKeys = foriegnKeys;
    }

    /**
     * @return the columns
     */
    public List<Column> getColumns() {
        if(columns==null){
            columns = new ArrayList<>();
        }
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    } 
    
    public void addColumn(Column column) {
        this.columns.add(column);
    }
    
        public void removeColumn(Column column) {
        this.columns.remove(column);
    }

}
