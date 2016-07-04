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

import java.util.List;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.JoinColumn;

public class DBPrimaryKeyJoinColumn extends DBColumn<Id> implements DBForeignKey {

    private final JoinColumn joinColumn;
    private final List<JoinColumn> joinColumns;

    public DBPrimaryKeyJoinColumn(String name, Id attribute) {
        super(name, attribute);
        joinColumns = null;//JoinColumnFinder.findJoinColumns(attribute, relationTableExist, true);
        joinColumn = null;//JoinColumnFinder.findJoinColumn(name, joinColumns);
    }

    /**
     * @return the inverseJoinColumn
     */
    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }
}
