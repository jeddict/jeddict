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
package io.github.jeddict.db.metadata;

import org.eclipse.persistence.internal.jpa.metadata.DBMetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.inheritance.InheritanceMetadata;

/**
 *
 * @author Shiwani Gupta
 */
public class InheritanceSpecMetadata extends InheritanceMetadata {

    public InheritanceSpecMetadata() {
    }

    /**
     * INTERNAL: Recursive method to traverse table per class inheritance
     * hierarchy and grab all the 'inherited' accessors for subclasses of the
     * hierarchy.
     *
     * What we know: - All parent classes will already have been processed.
     * Inheritance hierarchies are processed top->down. - Always go through the
     * given descriptors pointer to its class accessor, as we can not rely on
     * the reloaded accessors for inheritance checks, mapped superclasses etc.
     * Use the descriptors provided. - When adding accessors from superclasses
     * to an inheritance subclasses descriptor, they must be reloaded/cloned and
     * cannot be shared. Otherwise the processing of those accessor will only be
     * performed once by their 'real' owning entity accessor.
     */
    @Override
    public void addTablePerClassParentMappings(MetadataDescriptor startingDescriptor, MetadataDescriptor realDescriptor) {
        EntityAccessor reloadedParentEntity = null;
        MetadataDescriptor realParentDescriptor = null;

        // If we are an inheritance subclass, recursively call up to the root
        // entity so that we can grab a copy of all our inherited mapping
        // accessors. Copies of our parent accessors are done by reloading the
        // parent entities through OX (if they were originally loaded from XML).
        // This is our way of cloning. The reloaded accessors are rebuilt using
        // the startingDescriptor context, that is where we want to add the
        // accessors.
        if (realDescriptor.isInheritanceSubclass() && realDescriptor.getInheritanceRootDescriptor().usesTablePerClassInheritanceStrategy()) {
            realParentDescriptor = realDescriptor.getInheritanceParentDescriptor();
            reloadedParentEntity = reloadEntity((EntityAccessor) realParentDescriptor.getClassAccessor(), startingDescriptor);
            addTablePerClassParentMappings(startingDescriptor, realParentDescriptor);
        }

        // If we are the starting entity, the processing of our mapped
        // superclass and our accessors will be done when we process our
        // immediate accessors. Also, our immediate mapped superclasses will
        // have other metadata for us to process (and not just the addition of
        // accessors). See EntityAccesor process() and processClassMetadata().
        if (reloadedParentEntity != null) {
            // Be sure to reload the mapped superclass from the 'real' entity
            // accessor which has already discovered the list.
            EntityAccessor realParentEntityAccessor = (EntityAccessor) realParentDescriptor.getClassAccessor();

            for (MappedSuperclassAccessor mappedSuperclass : realParentEntityAccessor.getMappedSuperclasses()) {
                // Reload the mapped superclass and add its accessors.
                reloadMappedSuperclass(mappedSuperclass, startingDescriptor).addAccessors();
            }

            // Add the mapping accessors from the reloaded entity.
            reloadedParentEntity.addAccessors();
            /**
             * fix for SUPERCLASS_ATTR_CLONE
             */
            ((DBMetadataDescriptor) reloadedParentEntity.getDescriptor()).setParentClassMapping(
                    realParentEntityAccessor.getDescriptor().getMappings());

        }
    }

}
