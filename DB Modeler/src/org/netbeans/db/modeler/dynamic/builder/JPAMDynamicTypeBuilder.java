package org.netbeans.db.modeler.dynamic.builder;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DBRelationalDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.jpa.dynamic.DynamicIdentityPolicy;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;

public class JPAMDynamicTypeBuilder extends DynamicTypeBuilder {

    public JPAMDynamicTypeBuilder(Class<?> dynamicClass, DynamicType parentType, String... tableNames) {
        super(dynamicClass, parentType, tableNames);
    }
    
    public JPAMDynamicTypeBuilder(DynamicClassLoader dcl, ClassDescriptor descriptor, DynamicType parentType) {
        super(dcl, descriptor, parentType);
    }

    @Override
    protected void configure(ClassDescriptor descriptor, String... tableNames) {
        DBRelationalDescriptor relationDescriptor = (DBRelationalDescriptor) descriptor;
        // Configure Table names if provided
        if (tableNames != null) {
            if (tableNames.length == 0) {
                //Fix for : https://github.com/jeddict/jeddict/issues/1
                // If ClassDescriptor is entity then don't make it Aggregate
                if (descriptor.getTables().size() == 0 && !(relationDescriptor.getAccessor() instanceof EntitySpecAccessor)) {
                    descriptor.descriptorIsAggregate();
                }
            } else {
                for (int index = 0; index < tableNames.length; index++) {
                    descriptor.addTableName(tableNames[index]);
                }
            }

        }

        for (int index = 0; index < descriptor.getMappings().size(); index++) {
            addMapping(descriptor.getMappings().get(index));
        }

        descriptor.setProperty(DynamicType.DESCRIPTOR_PROPERTY, entityType);
    

        if (descriptor.getCMPPolicy() == null) {
            descriptor.setCMPPolicy(new DynamicIdentityPolicy());
        }
    }
}
