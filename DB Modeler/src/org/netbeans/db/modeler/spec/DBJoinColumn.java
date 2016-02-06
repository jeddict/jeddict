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
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class DBJoinColumn extends DBColumn {

    private JoinColumn joinColumn;

    public DBJoinColumn(String name, Attribute attribute) {
        super(name, attribute);
        List<JoinColumn> joinColumns;
        if (attribute instanceof RelationAttribute) {
            joinColumns = ((RelationAttribute) attribute).getJoinTable().getJoinColumn();
        } else if (attribute instanceof ElementCollection) {
            joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }

        boolean created = false;
            for (JoinColumn column : joinColumns) {
                if (name.equals(column.getName())) {
                    this.joinColumn = column;
                    created = true;
                    break;
                }
            }

        if (!created) {
            joinColumn = new JoinColumn();
            joinColumns.add(joinColumn);
        }
    }

    /**
     * @return the joinColumn
     */
    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

}