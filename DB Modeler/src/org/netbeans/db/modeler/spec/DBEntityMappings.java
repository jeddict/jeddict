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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.persistence.AttributeConverter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.eclipse.persistence.internal.jpa.metadata.DBMetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.converters.MixedConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.netbeans.jpa.modeler.db.accessor.DefaultClassSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EmbeddableSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.Converter;
import org.netbeans.jpa.modeler.spec.EntityMappings;

/**
 * Object to hold onto the entity mappings metadata.
 *
 * @author Gaurav Gupta
 * @since JPA Modeler 1.3
 */
public class DBEntityMappings extends XMLEntityMappings {

    private final EntityMappings mappings;
    private final ClassLoader classLoader;

    public DBEntityMappings(EntityMappings mappings, ClassLoader classLoader) {
        this.mappings = mappings;
        this.classLoader = classLoader;

//        setPackage(mappings.getPackage());//conflict with converter virtual class add the package prefix
        setEntities(mappings.getEntity().stream().map(EntitySpecAccessor::getInstance).collect(toList()));
//      setMappedSuperclasses(mappings.getMappedSuperclass().stream().map(MappedSuperclassSpecAccessor::getInstance).collect(toList()));
        setMappedSuperclasses(new ArrayList<>());

        List<EmbeddableAccessor> embeddableAccessors = new ArrayList<>();
        embeddableAccessors.addAll(mappings.getEmbeddable().stream().map(EmbeddableSpecAccessor::getInstance).collect(toList()));
        embeddableAccessors.addAll(mappings.getDefaultClass().stream().map(DefaultClassSpecAccessor::getInstance).collect(toList()));
        setEmbeddables(embeddableAccessors);
        setMixedConverters(mappings.getConverter().stream().map(Converter::getAccessor).collect(toList()));
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

    private void createConverterClass(Converter convert, ClassLoader classLoader) {
        //create Java Class
        Class<?> attributeConverter = new ByteBuddy()
//                .subclass(TypeDescription.Generic.Builder.parameterizedType(AttributeConverter.class, String.class, Integer.class).build())
                .subclass(AttributeConverter.class)
                .name(convert.getClazz())
                .annotateType(AnnotationDescription.Builder.ofType(javax.persistence.Converter.class).build())
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        //create MetadataClass
        MetadataClass metadataClass = new MetadataClass(getMetadataFactory(), convert.getClazz());
        metadataClass.addInterface(AttributeConverter.class.getName());
        metadataClass.addGenericType("");
        metadataClass.addGenericType("");
        metadataClass.addGenericType(convert.getAttributeType());
        metadataClass.addGenericType("");
        metadataClass.addGenericType(convert.getFieldType());
        getMetadataFactory().addMetadataClass(metadataClass);

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

        mappings.getConverter().stream().forEach(convert -> createConverterClass(convert, classLoader));

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

    protected XMLEntityMappings reloadXMLEntityMappingsObject(XMLEntityMappings xmlEntityMappings) {
        XMLEntityMappings newXMLEntityMappings = super.reloadXMLEntityMappingsObject(xmlEntityMappings);
        if (xmlEntityMappings.getEntities() != null) {
            for (int i = 0; i < xmlEntityMappings.getEntities().size(); i++) {
                copyAttributesProperty(xmlEntityMappings.getEntities().get(i).getAttributes(), newXMLEntityMappings.getEntities().get(i).getAttributes());
            }
        }
        if (xmlEntityMappings.getEmbeddables() != null) {
            for (int i = 0; i < xmlEntityMappings.getEmbeddables().size(); i++) {
                copyAttributesProperty(xmlEntityMappings.getEmbeddables().get(i).getAttributes(), newXMLEntityMappings.getEmbeddables().get(i).getAttributes());
            }
        }
        if (xmlEntityMappings.getMappedSuperclasses() != null) {
            for (int i = 0; i < xmlEntityMappings.getMappedSuperclasses().size(); i++) {
                copyAttributesProperty(xmlEntityMappings.getMappedSuperclasses().get(i).getAttributes(), newXMLEntityMappings.getMappedSuperclasses().get(i).getAttributes());
            }
        }
        return newXMLEntityMappings;
    }

    private void copyAttributesProperty(XMLAttributes preAttributes, XMLAttributes newAttributes) {
        for (int i = 0; i < preAttributes.getIds().size(); i++) {
            copyAttributesProperty(preAttributes.getIds().get(i), newAttributes.getIds().get(i));
        }

        for (int i = 0; i < preAttributes.getBasics().size(); i++) {
            copyAttributesProperty(preAttributes.getBasics().get(i), newAttributes.getBasics().get(i));
        }

        for (int i = 0; i < preAttributes.getEmbeddeds().size(); i++) {
            copyAttributesProperty(preAttributes.getEmbeddeds().get(i), newAttributes.getEmbeddeds().get(i));
        }

        for (int i = 0; i < preAttributes.getElementCollections().size(); i++) {
            copyAttributesProperty(preAttributes.getElementCollections().get(i), newAttributes.getElementCollections().get(i));
        }

        for (int i = 0; i < preAttributes.getBasicCollections().size(); i++) {
            copyAttributesProperty(preAttributes.getBasicCollections().get(i), newAttributes.getBasicCollections().get(i));
        }

        for (int i = 0; i < preAttributes.getBasicMaps().size(); i++) {
            copyAttributesProperty(preAttributes.getBasicMaps().get(i), newAttributes.getBasicMaps().get(i));
        }

        copyAttributesProperty(preAttributes.getEmbeddedId(), newAttributes.getEmbeddedId());

        for (int i = 0; i < preAttributes.getVersions().size(); i++) {
            copyAttributesProperty(preAttributes.getVersions().get(i), newAttributes.getVersions().get(i));
        }

        for (int i = 0; i < preAttributes.getTransients().size(); i++) {
            copyAttributesProperty(preAttributes.getTransients().get(i), newAttributes.getTransients().get(i));
        }
        for (int i = 0; i < preAttributes.getAccessors().size(); i++) {
            copyAttributesProperty(preAttributes.getAccessors().get(i), newAttributes.getAccessors().get(i));
        }

        for (int i = 0; i < preAttributes.getArrays().size(); i++) {
            copyAttributesProperty(preAttributes.getArrays().get(i), newAttributes.getArrays().get(i));
        }
        for (int i = 0; i < preAttributes.getVariableOneToOnes().size(); i++) {
            copyAttributesProperty(preAttributes.getVariableOneToOnes().get(i), newAttributes.getVariableOneToOnes().get(i));
        }
        for (int i = 0; i < preAttributes.getStructures().size(); i++) {
            copyAttributesProperty(preAttributes.getStructures().get(i), newAttributes.getStructures().get(i));
        }

        for (int i = 0; i < preAttributes.getTransformations().size(); i++) {
            copyAttributesProperty(preAttributes.getTransformations().get(i), newAttributes.getTransformations().get(i));
        }

        for (int i = 0; i < preAttributes.getManyToManys().size(); i++) {
            copyAttributesProperty(preAttributes.getManyToManys().get(i), newAttributes.getManyToManys().get(i));
        }
        for (int i = 0; i < preAttributes.getManyToOnes().size(); i++) {
            copyAttributesProperty(preAttributes.getManyToOnes().get(i), newAttributes.getManyToOnes().get(i));
        }
        for (int i = 0; i < preAttributes.getOneToManys().size(); i++) {
            copyAttributesProperty(preAttributes.getOneToManys().get(i), newAttributes.getOneToManys().get(i));
        }
        for (int i = 0; i < preAttributes.getOneToOnes().size(); i++) {
            copyAttributesProperty(preAttributes.getOneToOnes().get(i), newAttributes.getOneToOnes().get(i));
        }

    }

    private void copyAttributesProperty(MappingAccessor preAttributes, MappingAccessor newAttributes) {
//        newAttributes.getMapping().setProperties(preAttributes.getMapping().getProperties());
    }

}
