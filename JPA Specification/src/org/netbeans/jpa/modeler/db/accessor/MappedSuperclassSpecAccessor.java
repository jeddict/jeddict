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
package org.netbeans.jpa.modeler.db.accessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;

/**
 *
 * @author Shiwani Gupta
 */
public class MappedSuperclassSpecAccessor extends EntityAccessor {

    private MappedSuperclass mappedSuperclass;

    private MappedSuperclassSpecAccessor(MappedSuperclass mappedSuperclass) {
        this.mappedSuperclass = mappedSuperclass;
    }

    public static MappedSuperclassSpecAccessor getInstance(MappedSuperclass mappedSuperclass) {
        MappedSuperclassSpecAccessor accessor = new MappedSuperclassSpecAccessor(mappedSuperclass);

        accessor.setClassName(mappedSuperclass.getClazz());
        if (mappedSuperclass.getSuperclass() != null) {
            accessor.setParentClassName(mappedSuperclass.getSuperclass().getClazz());
        }
//        accessor.setAccess("VIRTUAL");
        accessor.setAttributes(mappedSuperclass.getAttributes().getAccessor(true));
//        accessor.setName(mappedSuperclass.getName());

//        if (mappedSuperclass.getTable() != null) {
//            accessor.setTable(mappedSuperclass.getTable().getAccessor());
//        }
//        processSuperClass(mappedSuperclass, accessor);
//        if (mappedSuperclass.getInheritance() != null) {
//            accessor.setInheritance(mappedSuperclass.getInheritance().getAccessor());
//        }
//        if (mappedSuperclass.getDiscriminatorColumn() != null) {
//            accessor.setDiscriminatorColumn(mappedSuperclass.getDiscriminatorColumn().getAccessor());
//        }
//        accessor.setDiscriminatorValue(mappedSuperclass.getDiscriminatorValue());
        return accessor;

    }

    /**
     * @return the entity
     */
    public MappedSuperclass getMappedSuperclass() {
        return mappedSuperclass;
    }

}
