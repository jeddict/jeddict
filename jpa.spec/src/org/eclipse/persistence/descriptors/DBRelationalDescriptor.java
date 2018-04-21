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
package org.eclipse.persistence.descriptors;

import java.util.List;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

public class DBRelationalDescriptor extends RelationalDescriptor {

    private MetadataAccessor accessor;

    public DBRelationalDescriptor(MetadataAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    /**
     * @return the accessor
     */
    public MetadataAccessor getAccessor() {
        return accessor;
    }

    /**
     * @return the parentClassMapping
     */
    public List<DatabaseMapping> getParentClassMapping() {
        return parentClassMapping;
    }

    /**
     * @param parentClassMapping the parentClassMapping to set
     */
    public void setParentClassMapping(List<DatabaseMapping> parentClassMapping) {
        this.parentClassMapping = parentClassMapping;
    }

    /**
     * Id : SUPERCLASS_ATTR_CLONE. Description : Fix for If class have super
     * class then super class mapping is cloned and copied to subclass, to share
     * the attributes but in the cloning process, Attribute Spec property is
     * missed.
     */
    private List<DatabaseMapping> parentClassMapping;
}
