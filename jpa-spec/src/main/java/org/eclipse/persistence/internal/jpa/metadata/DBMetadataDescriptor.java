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
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.List;
import org.eclipse.persistence.descriptors.DBRelationalDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Common metatata descriptor for the annotation and xml processors.
 *
 * @author Gaurav Gupta
 * @since Jeddict 1.3
 */
public class DBMetadataDescriptor extends MetadataDescriptor {

    /**
     * @param parentClassMapping the parentClassMapping to set
     */
    public void setParentClassMapping(List<DatabaseMapping> parentClassMapping) {
        ((DBRelationalDescriptor) getClassDescriptor()).setParentClassMapping(parentClassMapping);
    }

    public DBMetadataDescriptor(MetadataClass javaClass, ClassAccessor classAccessor) {
        super(javaClass);
        RelationalDescriptor des = new DBRelationalDescriptor(classAccessor);
        setDescriptor(des);
        des.setAlias("");
        des.getQueryManager().checkDatabaseForDoesExist();
        setJavaClass(javaClass);
        setClassAccessor(classAccessor);
    }
}
