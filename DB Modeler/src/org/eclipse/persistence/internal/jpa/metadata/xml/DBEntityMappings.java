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
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.util.ArrayList;
import java.util.HashMap;
import static java.util.stream.Collectors.toList;
import org.eclipse.persistence.internal.jpa.metadata.DBMetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.converters.MixedConverterMetadata;
import org.netbeans.jpa.modeler.db.accessor.EmbeddableSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;

/**
 * Object to hold onto the entity mappings metadata.
 *
 * @author Gaurav Gupta
 * @since JPA Modeler 1.3
 */
public class DBEntityMappings extends XMLEntityMappings {

    public DBEntityMappings(EntityMappings mappings) {

        setPackage(mappings.getPackage());
        setEntities(mappings.getEntity().stream().map(EntitySpecAccessor::getInstance).collect(toList()));
        setMappedSuperclasses(mappings.getMappedSuperclass().stream().map(MappedSuperclass::getAccessor).collect(toList()));
        setEmbeddables(mappings.getEmbeddable().stream().map(EmbeddableSpecAccessor::getInstance).collect(toList()));

        setMixedConverters(new ArrayList<>());

        setConverters(new ArrayList<>());
        setTypeConverters(new ArrayList<>());
        setObjectTypeConverters(new ArrayList<>());
        setSerializedConverters(new ArrayList<>());
        setStructConverters(new ArrayList<>());
        setTableGenerators(new ArrayList<>());
        setUuidGenerators(new ArrayList<>());
        setSequenceGenerators(new ArrayList<>());
        setPartitioning(new ArrayList<>());
        setReplicationPartitioning(new ArrayList<>());
        setRoundRobinPartitioning(new ArrayList<>());
        setPinnedPartitioning(new ArrayList<>());
        setRangePartitioning(new ArrayList<>());
        setValuePartitioning(new ArrayList<>());
        setHashPartitioning(new ArrayList<>());
        setNamedQueries(new ArrayList<>());
        setNamedNativeQueries(new ArrayList<>());
        setNamedStoredProcedureQueries(new ArrayList<>());
        setNamedStoredFunctionQueries(new ArrayList<>());
        setNamedPLSQLStoredFunctionQueries(new ArrayList<>());
        setNamedPLSQLStoredProcedureQueries(new ArrayList<>());
        setSqlResultSetMappings(new ArrayList<>());
        setOracleArrayTypes(new ArrayList<>());
        setOracleObjectTypes(new ArrayList<>());
        setPLSQLTables(new ArrayList<>());
        setPLSQLRecords(new ArrayList<>());
        setTenantDiscriminatorColumns(new ArrayList<>());
    }

    /**
     * INTERNAL: Assumes the correct class loader has been set before calling
     * this method.
     */
    public void initPersistenceUnitClasses(HashMap<String, EntityAccessor> allEntities, HashMap<String, EmbeddableAccessor> allEmbeddables) {
        // Build our ConverterAccessor and ConverterMetadata lists from
        // the mixed converter metadata list.
        for (MixedConverterMetadata mixedConverter : getMixedConverters()) {
            if (mixedConverter.isConverterMetadata()) {
                getConverters().add(mixedConverter.buildConverterMetadata());
            } else {
                getConverterAccessors().add(mixedConverter.buildConverterAccessor());
            }
        }

        // Process the entities
        for (EntityAccessor entity : getEntities()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass entityClass = getMetadataClass(getPackageQualifiedClassName(entity.getClassName()), false);

            // Initialize the entity with its metadata descriptor and project.
            // This initialization must be done before a potential merge below.
            entity.initXMLClassAccessor(entityClass, new DBMetadataDescriptor(entityClass, entity), getProject(), this);

            if (allEntities.containsKey(entityClass.getName())) {
                // Merge this entity with the existing one.
                allEntities.get(entityClass.getName()).merge(entity);
            } else {
                // Add this entity to the map.
                allEntities.put(entityClass.getName(), entity);
            }
        }

        // Process the embeddables.
        for (EmbeddableAccessor embeddable : getEmbeddables()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass embeddableClass = getMetadataClass(getPackageQualifiedClassName(embeddable.getClassName()), false);

            // Initialize the embeddable with its metadata descriptor and project.
            // This initialization must be done before a potential merge below.
            embeddable.initXMLClassAccessor(embeddableClass, new DBMetadataDescriptor(embeddableClass, embeddable), getProject(), this);

            if (allEmbeddables.containsKey(embeddableClass.getName())) {
                // Merge this embeddable with the existing one.
                allEmbeddables.get(embeddableClass.getName()).merge(embeddable);
            } else {
                // Add this embeddable to the map.
                allEmbeddables.put(embeddableClass.getName(), embeddable);
            }
        }

        // Process the mapped superclasses
        for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass mappedSuperclassClass = getMetadataClass(getPackageQualifiedClassName(mappedSuperclass.getClassName()), false);

            // Initialize the mapped superclass with a metadata descriptor and project.
            // This initialization must be done before a potential merge below.
            mappedSuperclass.initXMLClassAccessor(mappedSuperclassClass, new DBMetadataDescriptor(mappedSuperclassClass, mappedSuperclass), getProject(), this);

            if (getProject().hasMappedSuperclass(mappedSuperclassClass)) {
                // Merge this mapped superclass with the existing one.
                getProject().getMappedSuperclassAccessor(mappedSuperclassClass).merge(mappedSuperclass);
            } else {
                // Add this mapped superclass to the project.
                getProject().addMappedSuperclass(mappedSuperclass);
            }
        }

        // Process the JPA converter classes.
        for (ConverterAccessor converterAccessor : getConverterAccessors()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass converterClass = getMetadataClass(getPackageQualifiedClassName(converterAccessor.getClassName()), false);

            // Initialize the converter class.
            // This initialization must be done before a potential merge below.
            converterAccessor.initXMLObject(converterClass, this);

            if (getProject().hasConverterAccessor(converterClass)) {
                // Merge this converter with the existing one (will check for discrepancies between them)
                getProject().getConverterAccessor(converterClass).merge(converterAccessor);
            } else {
                // Add this converter to the project.
                getProject().addConverterAccessor(converterAccessor);
            }
        }
    }

}
