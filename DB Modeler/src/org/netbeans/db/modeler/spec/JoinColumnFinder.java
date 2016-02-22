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
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public class JoinColumnFinder {

    public static List<JoinColumn> findJoinColumns(Attribute attribute, boolean relationTableExist, boolean inverse) {
        List<JoinColumn> joinColumns;
        if (attribute instanceof RelationAttribute) {
            if (!relationTableExist) {
                if (attribute instanceof SingleRelationAttribute) {
                    joinColumns = ((SingleRelationAttribute) attribute).getJoinColumn();
                } else if (attribute instanceof OneToMany) {
                    joinColumns = ((OneToMany) attribute).getJoinColumn();
                } else {
                    throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
                }
            } else if (inverse) {
                joinColumns = ((RelationAttribute) attribute).getJoinTable().getInverseJoinColumn();
            } else {
                joinColumns = ((RelationAttribute) attribute).getJoinTable().getJoinColumn();
            }
        } else if (attribute instanceof ElementCollection) { // not applicable for inverse-join-column
            joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
        return joinColumns;
    }

    public static List<JoinColumn> findJoinColumns(AssociationOverride associationOverride, Attribute attribute, boolean relationTableExist, boolean inverse) {
        List<JoinColumn> joinColumns;
        if (attribute instanceof RelationAttribute) {
            if (!relationTableExist) {
                if (attribute instanceof SingleRelationAttribute | attribute instanceof OneToMany) {
                    joinColumns = associationOverride.getJoinColumn();
                } else {
                    throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
                }
            } else if (inverse) {
                joinColumns = associationOverride.getJoinTable().getInverseJoinColumn();
            } else {
                joinColumns = associationOverride.getJoinTable().getJoinColumn();
            }
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
        return joinColumns;
    }

   public static JoinColumn findJoinColumn(String name, List<JoinColumn> joinColumns) {
        JoinColumn joinColumn = null;
        boolean created = false;
        for (Iterator<JoinColumn> it = joinColumns.iterator(); it.hasNext();) {
            JoinColumn column = it.next();
            if (name.equals(column.getName())) {
                joinColumn = column;
                created = true;
                break;
            } else if (StringUtils.isBlank(column.getName())) {
                it.remove();
            }
        }

        if (!created) {
            joinColumn = new JoinColumn();
            joinColumns.add(joinColumn);
        }
        return joinColumn;
    }
}
