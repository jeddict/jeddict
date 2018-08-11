/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.spec;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.SecondaryTable;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JoinColumnHandler;
import io.github.jeddict.jpa.spec.extend.JoinTableHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public class JoinColumnFinder {

    public static List<JoinColumn> findJoinColumns(Attribute attribute, boolean relationTableExist, boolean inverse) {
        return findJoinColumns(attribute, relationTableExist, inverse, false);
    }

    public static List<JoinColumn> findMapKeyJoinColumns(Attribute attribute) {
        return findJoinColumns(attribute, false, false, true);
    }

    private static List<JoinColumn> findJoinColumns(Attribute attribute, boolean relationTableExist, boolean inverse, boolean mapKey) {
        List<JoinColumn> joinColumns;
        if (mapKey) {
            if (attribute instanceof MapKeyHandler) {
                joinColumns = ((MapKeyHandler) attribute).getMapKeyJoinColumn();
            } else {
                throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
            }
        } else {
            if (attribute instanceof RelationAttribute) {
                joinColumns = getRelationAttributeJoinColumn(attribute, (JoinTableHandler) attribute, relationTableExist, inverse);
            } else if (attribute instanceof ElementCollection) { // not applicable for inverse-join-column
                joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();
            } else {
                throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
            }
        }
        return joinColumns;
    }

    public static List<JoinColumn> findJoinColumns(AssociationOverride associationOverride, Attribute attribute, boolean relationTableExist, boolean inverse) {
        List<JoinColumn> joinColumns;
        if (attribute instanceof RelationAttribute) {
            joinColumns = getRelationAttributeJoinColumn(attribute, associationOverride, relationTableExist, inverse);
        } else if (attribute instanceof ElementCollection) { 
            joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();//https://github.com/jeddict/jeddict/issues/148 AssociactionOverride is not applicable for element collection
        } else {            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
        return joinColumns;
    }

    public static JoinColumn findJoinColumn(String name, List<JoinColumn> joinColumns) {
        return findJoinColumn(name, joinColumns, false);
    }

    public static JoinColumn findMapKeyJoinColumn(String name, List<JoinColumn> joinColumns) {
        return findJoinColumn(name, joinColumns, true);
    }

    private static JoinColumn findJoinColumn(String name, List<JoinColumn> joinColumns, boolean mapKey) {
        JoinColumn joinColumn = null;
        boolean created = false;
//        List<JoinColumn> joinColumnList = joinColumns;
        if (joinColumns != null) {
            for (Iterator<? extends JoinColumn> it = joinColumns.iterator(); it.hasNext();) {
                JoinColumn column = it.next();
                if (name.equals(column.getName())) {
                    joinColumn = column;
                    created = true;
                    break;
                } else if (StringUtils.isBlank(column.getName())) {
//                it.remove();
                }
            }

            if (!created) {
                joinColumn = new JoinColumn();
                joinColumn.setImplicitName(name);
                joinColumns.add(joinColumn);
            }
        }
        return joinColumn;
    }

    public static List<PrimaryKeyJoinColumn> findPrimaryKeyJoinColumns(Entity entity) {
        return entity.getPrimaryKeyJoinColumn();
    }
    
    public static List<PrimaryKeyJoinColumn> findPrimaryKeyJoinColumns(SecondaryTable secondaryTable) {
        return secondaryTable.getPrimaryKeyJoinColumn();
    }

    public static PrimaryKeyJoinColumn findPrimaryKeyJoinColumn(String name, List<PrimaryKeyJoinColumn> joinColumns) {
        PrimaryKeyJoinColumn joinColumn = null;
        boolean created = false;
        for (Iterator<PrimaryKeyJoinColumn> it = joinColumns.iterator(); it.hasNext();) {
            PrimaryKeyJoinColumn column = it.next();
            if (name.equals(column.getName())) {
                joinColumn = column;
                created = true;
                break;
            }
        }

        if (!created) {
            joinColumn = new PrimaryKeyJoinColumn();
            joinColumn.setImplicitName(name);
            joinColumns.add(joinColumn);
        }
        return joinColumn;
    }

    private static List<JoinColumn> getRelationAttributeJoinColumn(Attribute attribute, JoinTableHandler tableHandler, boolean relationTableExist, boolean inverse) {
        List<JoinColumn> joinColumns;
        if (!relationTableExist) {
            if (attribute instanceof JoinColumnHandler) {//SingleRelationAttribute OneToMany
                joinColumns = ((JoinColumnHandler)tableHandler).getJoinColumn();
            } else {
                throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
            }
        } else if (inverse) {
            joinColumns = tableHandler.getJoinTable().getInverseJoinColumn();
        } else {
            joinColumns = tableHandler.getJoinTable().getJoinColumn();
        }
        return joinColumns;
    }

}
