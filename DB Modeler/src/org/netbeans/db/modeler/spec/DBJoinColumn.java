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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;

public class DBJoinColumn extends DBColumn implements DBForeignKey{

    private final boolean relationTableExist;
    private JoinColumn joinColumn;
    private List<JoinColumn> joinColumns;

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public DBJoinColumn(String name, Attribute attribute, boolean relationTableExist) {
        super(name, attribute);
        this.relationTableExist = relationTableExist;
        if (attribute instanceof RelationAttribute) {
            if (!relationTableExist) {
                if (attribute instanceof SingleRelationAttribute) {
                    joinColumns = ((SingleRelationAttribute) attribute).getJoinColumn();
                } else if (attribute instanceof OneToMany) {
                    joinColumns = ((OneToMany) attribute).getJoinColumn();
                } else {
                    throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
                }
            } else {
                joinColumns = ((RelationAttribute) attribute).getJoinTable().getJoinColumn();
            }
        } else if (attribute instanceof ElementCollection) {
            joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }

        boolean created = false;
        for (Iterator<JoinColumn> it = joinColumns.iterator(); it.hasNext();) {
            JoinColumn column = it.next();
            if (name.equals(column.getName())) {
                this.joinColumn = column;
                created = true;
                break;
            } else if(StringUtils.isBlank(column.getName())) {
                it.remove();
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

    /**
     * Get the value of relationTableExist
     *
     * @return the value of relationTableExist
     */
    public boolean isRelationTableExist() {
        return relationTableExist;
    }

}
