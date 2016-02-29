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
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class DBInverseJoinColumn extends DBColumn<RelationAttribute> implements DBForeignKey {

    private final boolean relationTableExist;

    private final JoinColumn joinColumn;
    private final List<JoinColumn> joinColumns;

    public DBInverseJoinColumn(String name, RelationAttribute attribute, boolean relationTableExist) {
        super(name, attribute);
        this.relationTableExist = relationTableExist;
        joinColumns = JoinColumnFinder.findJoinColumns(attribute, relationTableExist, true);
        joinColumn = JoinColumnFinder.findJoinColumn(name, joinColumns);
        System.out.println("asssswee");
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

    public boolean isRelationTableExist() {
        return relationTableExist;
    }
}
