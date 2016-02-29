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

import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class DBParentAssociationInverseJoinColumn extends DBParentAssociationColumn<RelationAttribute> {

    public DBParentAssociationInverseJoinColumn(String name, Entity intrinsicClass, RelationAttribute managedAttribute, boolean relationTableExist) {
        super(name, intrinsicClass, managedAttribute,relationTableExist);
        joinColumnsOverride = JoinColumnFinder.findJoinColumns(associationOverride, managedAttribute, relationTableExist, true);
        joinColumnOverride = JoinColumnFinder.findJoinColumn(name, joinColumnsOverride);

        joinColumns = JoinColumnFinder.findJoinColumns(managedAttribute, relationTableExist, true);
        joinColumn = JoinColumnFinder.findJoinColumn(name, joinColumns);
    }

}
