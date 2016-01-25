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

import org.netbeans.jpa.modeler.spec.extend.FlowPin;

/**
 *
 * @author Gaurav Gupta
 */
public class Column extends FlowPin {

    private String name;
    private String dataType;
    private int size;
    private int subSize;
    private boolean primaryKey;

    private boolean foreignKey;
    private Column referenceColumn;
    private Table referenceTable;

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
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the foreignKey
     */
    public boolean isForeignKey() {
        return foreignKey;
    }

    /**
     * @param foreignKey the foreignKey to set
     */
    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    /**
     * @return the referenceColumn
     */
    public Column getReferenceColumn() {
        return referenceColumn;
    }

    /**
     * @param referenceColumn the referenceColumn to set
     */
    public void setReferenceColumn(Column referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    /**
     * @return the referenceTable
     */
    public Table getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable the referenceTable to set
     */
    public void setReferenceTable(Table referenceTable) {
        this.referenceTable = referenceTable;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the subSize
     */
    public int getSubSize() {
        return subSize;
    }

    /**
     * @param subSize the subSize to set
     */
    public void setSubSize(int subSize) {
        this.subSize = subSize;
    }
}
