/**
 * Copyright [2018] Gaurav Gupta
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
package io.github.jeddict.db.viewer.spec;

import org.netbeans.modules.db.metadata.model.api.ForeignKeyColumn;

/**
 *
 * @author Gaurav Gupta
 */
public class DBForeignKey extends DBColumn {

    private DBColumn referenceColumn;

    private DBTable referenceTable;

    private final ForeignKeyColumn foreignKeyColumn;

    public DBForeignKey(String name, ForeignKeyColumn foreignKeyColumn) {
        super(name, foreignKeyColumn.getReferringColumn());
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public ForeignKeyColumn getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    /**
     * @return the referenceColumn
     */
    public DBColumn getReferenceColumn() {
        return referenceColumn;
    }

    /**
     * @param referenceColumn the referenceColumn to set
     */
    public void setReferenceColumn(DBColumn referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    /**
     * @return the referenceTable
     */
    public DBTable getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable the referenceTable to set
     */
    public void setReferenceTable(DBTable referenceTable) {
        this.referenceTable = referenceTable;
    }

}
