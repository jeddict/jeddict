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
package io.github.jeddict.relation.mapper.persistence.internal.jpa.metadata;

import java.util.Map;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import io.github.jeddict.relation.mapper.dynamic.builder.JPAMDynamicTypeBuilder;

/**
 *
 * @author Gaurav Gupta
 */
public class JPAMMetadataProject extends MetadataProject {

    public JPAMMetadataProject(AbstractSession session, boolean weaveLazy, boolean weaveEager, boolean weaveFetchGroups, boolean multitenantSharedEmf, boolean multitenantSharedCache) {
        super(null, session, weaveLazy, weaveEager, weaveFetchGroups, multitenantSharedEmf, multitenantSharedCache);
    }

    @Override
    public boolean isSharedCacheModeAll() {
        return false;
    }
    
        /**
     * INTERNAL:
     * Create the dynamic types using JPA metadata processed descriptors. Called 
     * at deploy time after all metadata processing has completed.
     */
    @Override
    protected void createDynamicType(MetadataDescriptor descriptor, Map<String, DynamicType> dynamicTypes, DynamicClassLoader dcl) {
        // Build the dynamic class only if we have not already done so.
        if (! dynamicTypes.containsKey(descriptor.getJavaClassName())) {
            JPAMDynamicTypeBuilder typeBuilder = null;
            
            if (descriptor.isInheritanceSubclass()) {
                // Get the parent descriptor
                MetadataDescriptor parentDescriptor = descriptor.getInheritanceParentDescriptor();
                
                // Recursively call up the parents.
                createDynamicType(parentDescriptor, dynamicTypes, dcl);
                
                // Create the dynamic type using the parent type.
                typeBuilder = new JPAMDynamicTypeBuilder(dcl, descriptor.getClassDescriptor(), dynamicTypes.get(parentDescriptor.getJavaClassName()));
            } else {
                // Create the dynamic type
                typeBuilder = new JPAMDynamicTypeBuilder(dcl, descriptor.getClassDescriptor(), null);
            }
            
            // Store the type builder by java class name.
            dynamicTypes.put(descriptor.getJavaClassName(), typeBuilder.getType());
        }
    }

}
