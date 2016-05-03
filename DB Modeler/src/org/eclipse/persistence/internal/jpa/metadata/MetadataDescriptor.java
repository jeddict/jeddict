/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0M9 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     07/15/2008-1.0.1 Guy Pelletier 
 *       - 240679: MappedSuperclass Id not picked when on get() method accessor
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: add isMappedSuperclass() helper function in support
 *         of MappedSuperclass handling for the Metamodel API.
 *     08/11/2009-2.0 Michael O'Brien 
 *       - 284147: So we do not add a pseudo PK Field for MappedSuperclasses when
 *         1 or more PK fields already exist on the descriptor. 
 *         Add m_idAccessor map and hasIdAccessor() function.
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     01/05/2010-2.1 Guy Pelletier 
 *       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     03/08/2010-2.1 Michael O'Brien 
 *       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
 *                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
 *                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
 *                      temporary PK field used to process MappedSuperclasses for the Metamodel API
 *                      during MetadataProject.addMetamodelMappedSuperclass()
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     05/03/2009-1.2.1 Guy Pelletier 
 *       - 307547:  Exception in order by clause after migrating to eclipselink 1.2 release
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 *     07/16/2010-2.2 Guy Pelletier 
 *       - 260296: mixed access with no Transient annotation does not result in error
 *     08/04/2010-2.1.1 Guy Pelletier
 *       - 315782: JPA2 derived identity metadata processing validation doesn't account for autoboxing
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     10/27/2010-2.2 Guy Pelletier 
 *       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 251554: ExcludeDefaultMapping annotation needed
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 *     04/07/2012-2.5 Guy Pelletier    
 *       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer 
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     05/19/2014-2.6 Tomas Kraus
 *       - 437578: @Cacheable annotation value is now passed to CachePolicy
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.ExistenceType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.SingleTableMultitenantPolicy;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeMethodInfo;

import org.eclipse.persistence.internal.jpa.CMP3Policy;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ObjectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.mappings.DatabaseMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_PROPERTY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_FIELD;

/**
 * INTERNAL:
 * Common metatata descriptor for the annotation and xml processors. This class
 * is a wrap on an actual EclipseLink descriptor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataDescriptor {
    private boolean m_isCascadePersist;
    private boolean m_hasCache;
    private boolean m_hasCacheInterceptor;
    private boolean m_hasDefaultRedirectors;
    private boolean m_hasChangeTracking;
    private boolean m_hasCustomizer;
    private boolean m_hasReadOnly;
    private boolean m_hasCopyPolicy;
    private boolean m_hasPrimaryKey;
    private boolean m_hasSerializedObjectPolicy;
    
    // Default access methods are used for VIRTUAL mapping attributes when
    // the attributes do not specify their own access methods.
    private AccessMethodsMetadata m_defaultAccessMethods;
    
    /**
     * Entity @Cacheable annotation value (cacheable value of this descriptor).
     * This value contains Boolean value equal to annotation value or null when
     * no annotation was set for entity. Parent values are ignored, value refers
     * to current class only.
     */
    private Boolean m_cacheable;
    private Boolean m_usesCascadedOptimisticLocking;
    
    private ClassAccessor m_classAccessor;
    private DatabaseTable m_primaryTable;
    // The embedded id accessor for this descriptor if one exists.
    private EmbeddedIdAccessor m_embeddedIdAccessor;
    
    private List<String> m_idAttributeNames;
    private List<String> m_orderByAttributeNames;
    private List<String> m_idOrderByAttributeNames;
    private List<MetadataDescriptor> m_embeddableDescriptors;
    private List<TenantDiscriminatorColumnMetadata> m_defaultTenantDiscriminatorColumns;
    
    // Holds a list of derived id accessors.
    private List<ObjectAccessor> m_derivedIdAccessors;
    
    private Map<String, String> m_pkClassIDs;
    private Map<String, String> m_genericTypes;
    private Map<String, IdAccessor> m_idAccessors;
    // Holds all fields from BasicAccessors (Basic, Id, Version, Transformation)
    private Map<String, DatabaseField> m_fields;
    private Map<String, MappingAccessor> m_mappingAccessors;
    private Map<DatabaseField, MappingAccessor> m_primaryKeyAccessors;
    private Map<String, PropertyMetadata> m_properties;
    private Map<DatabaseField, DatabaseField> m_pkJoinColumnAssociations;
    private Map<String, AttributeOverrideMetadata> m_attributeOverrides;
    private Map<String, AssociationOverrideMetadata> m_associationOverrides;
    private Map<String, Map<String, MetadataAccessor>> m_biDirectionalManyToManyAccessors;
    private Map<String, List<ConvertMetadata>> m_converts;
    private Map<String, List<ConvertMetadata>> m_mapKeyConverts;
    
    private MetadataClass m_pkClass;
    private MetadataClass m_javaClass;
    // This is the root descriptor of the inheritance hierarchy. That is, for 
    // the entity that defines the inheritance strategy.
    private MetadataDescriptor m_inheritanceRootDescriptor;
    // This is our immediate parent's descriptor. Which may also be the root. 
    private MetadataDescriptor m_inheritanceParentDescriptor;
    // Used only for a mapped superclass descriptor. Allows us to look up
    // more specific types when a mapping may use a generic specification.
    // Note: a mapped superclass can not be discovered without the help of 
    // an entity accessor. Therefore, if this descriptor is to a mapped super
    // class, the child entity accessor will (and must) be set.
    private MetadataDescriptor m_metamodelMappedSuperclassChildDescriptor;
    private ClassDescriptor m_descriptor;
    
    // This is the default access type for the class accessor of this 
    // descriptor. The default access type is needed for those embeddables and 
    // mapped superclasses that are 'owned' or rely on this value for their own 
    // processing. It does not reflect an explicit access type.
    private String m_defaultAccess; 
    private String m_defaultSchema;
    private String m_defaultCatalog;
    private String m_existenceChecking;

    /**
     * INTERNAL: 
     */
    public MetadataDescriptor(MetadataClass javaClass) {
        m_defaultAccess = null;
        m_defaultSchema = null;
        m_defaultCatalog = null;
        
        m_inheritanceRootDescriptor = null;
        m_inheritanceParentDescriptor = null;
        
        m_hasCache = false;
        m_hasCacheInterceptor = false;
        m_hasDefaultRedirectors = false;
        m_hasChangeTracking = false;
        m_hasCustomizer = false;
        m_hasReadOnly = false;
        m_hasCopyPolicy = false;
        m_hasPrimaryKey = false;
        m_hasSerializedObjectPolicy = false;
        m_isCascadePersist = false;
        
        m_defaultAccessMethods = new AccessMethodsMetadata();
        
        m_idAttributeNames = new ArrayList<String>();
        m_orderByAttributeNames = new ArrayList<String>();
        m_idOrderByAttributeNames = new ArrayList<String>();
        m_embeddableDescriptors = new ArrayList<MetadataDescriptor>();
        m_derivedIdAccessors = new ArrayList<ObjectAccessor>();
        m_defaultTenantDiscriminatorColumns = new ArrayList<TenantDiscriminatorColumnMetadata>();
        
        m_pkClassIDs = new HashMap<String, String>();
        m_genericTypes = new HashMap<String, String>();
        m_mappingAccessors = new HashMap<String, MappingAccessor>();
        m_idAccessors = new HashMap<String, IdAccessor>();
        m_fields = new HashMap<String, DatabaseField>();
        m_primaryKeyAccessors = new HashMap<DatabaseField, MappingAccessor>();
        m_properties = new HashMap<String, PropertyMetadata>();
        m_pkJoinColumnAssociations = new HashMap<DatabaseField, DatabaseField>();
        m_attributeOverrides = new HashMap<String, AttributeOverrideMetadata>();
        m_associationOverrides = new HashMap<String, AssociationOverrideMetadata>();
        m_biDirectionalManyToManyAccessors = new HashMap<String, Map<String, MetadataAccessor>>();
        m_converts = new HashMap<String, List<ConvertMetadata>>();
        m_mapKeyConverts = new HashMap<String, List<ConvertMetadata>>();
        
        m_descriptor = new RelationalDescriptor();
        m_descriptor.setAlias("");
        
        // This is the default, set it in case no existence-checking is set.
        m_descriptor.getQueryManager().checkDatabaseForDoesExist();
        
        setJavaClass(javaClass);
    }
    
    /**
     * INTERNAL: 
     */
    public MetadataDescriptor(MetadataClass javaClass, ClassAccessor classAccessor) {
        this(javaClass);
        m_classAccessor = classAccessor;
    }
    
    /**
     * INTERNAL:
     */
     public void addAssociationOverride(AssociationOverrideMetadata associationOverride) {
        m_associationOverrides.put(associationOverride.getName(), associationOverride);   
     }
    
    /**
     * INTERNAL:
     */
    public void addAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        m_attributeOverrides.put(attributeOverride.getName(), attributeOverride);
    }
    
    /**
     * INTERNAL:
     * Add a convert to override a superclass class mapping.
     */
    public void addConvert(String attributeName, ConvertMetadata convert) {
        if (convert.isForMapKey()) {
            // This isForMapKey call will remove the key prefix when there is one.
            addMapKeyConvert(attributeName, convert);
        } else {
            if (! m_converts.containsKey(attributeName)) {
                m_converts.put(attributeName, new ArrayList<ConvertMetadata>());
            }
        
            m_converts.get(attributeName).add(convert);
        }
    }
    
    /** 
     * INTERNAL:
     */
    public void addDefaultEventListener(DescriptorEventListener listener) {
        m_descriptor.getEventManager().addDefaultEventListener(listener);
    }

    /**
     * INTERNAL:
     */
    public void addEmbeddableDescriptor(MetadataDescriptor embeddableDescriptor) {
        m_embeddableDescriptors.add(embeddableDescriptor);
    }

    /**
     * INTERNAL:
     */
    public void addEntityListenerEventListener(DescriptorEventListener listener) {
        m_descriptor.getEventManager().addEntityListenerEventListener(listener);
    }
    
    /**
     * INTERNAL:
     * Add a field from a basic mapping from this descriptor.
     */
    public void addField(DatabaseField field) {
        // If it should use an upper case for comparisons upper case the name 
        // before putting it in the map.  
        m_fields.put(field.getNameForComparisons(), field);
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldForInsert(DatabaseField field) {
        getReturningPolicy().addFieldForInsert(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldForInsertReturnOnly(DatabaseField field) {
        getReturningPolicy().addFieldForInsertReturnOnly(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldForUpdate(DatabaseField field) {
        getReturningPolicy().addFieldForUpdate(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addForeignKeyFieldForMultipleTable(DatabaseField fkField, DatabaseField pkField) {
        m_descriptor.addForeignKeyFieldForMultipleTable(fkField, pkField);
        m_pkJoinColumnAssociations.put(fkField, pkField);
    }
    
    /**
     * INTERNAL:
     * Add a generic type for this descriptor.
     */
    public void addGenericType(String genericName, String type) {
        m_genericTypes.put(genericName, type);
    }
    
    /**
     * INTERNAL:
     */
    public void addIdAttributeName(String idAttributeName) {
        m_idAttributeNames.add(idAttributeName);    
    }

    /**
     * INTERNAL:
     * Add a map key convert to override a superclass class mapping.
     */
    public void addMapKeyConvert(String attributeName, ConvertMetadata convert) {
        if (! m_mapKeyConverts.containsKey(attributeName)) {
            m_mapKeyConverts.put(attributeName, new ArrayList<ConvertMetadata>());
        }

        m_mapKeyConverts.get(attributeName).add(convert);
    }
    
    /**
     * INTERNAL:
     * If the accessor is an IdAccessor we store it in a separate map for use
     * during MappedSuperclass processing.
     */
    public void addMappingAccessor(MappingAccessor accessor) {
        // Don't bother adding a relationship accessor with a type of
        // ValueHolderInterface. This may be very legacy and no longer needed
        // but for the canonical model processing it's much cleaner if this
        // accessor does not show up in the mapping accessors list.
        if (accessor.isRelationship() && ((RelationshipAccessor) accessor).isValueHolderInterface()) {
            return;
        }
        
        // Log a warning message if we are overriding a mapping accessor.
        if (m_mappingAccessors.containsKey(accessor.getAttributeName())) {
            MappingAccessor existingAccessor = m_mappingAccessors.get(accessor.getAttributeName());
            String existingAccessType = existingAccessor.usesPropertyAccess() ? JPA_ACCESS_PROPERTY : JPA_ACCESS_FIELD;
            String accessType = accessor.usesPropertyAccess() ? JPA_ACCESS_PROPERTY : JPA_ACCESS_FIELD;
            getLogger().logWarningMessage(getLogger().INVERSE_ACCESS_TYPE_MAPPING_OVERRIDE, accessor.getJavaClass().getName(), existingAccessor.getAnnotatedElementName(), existingAccessType, accessor.getAnnotatedElementName(), accessType);
        }
        
        m_mappingAccessors.put(accessor.getAttributeName(), accessor);
        
        // Store IdAccessors in a separate map for use by hasIdAccessor()
        if (accessor.isId()) {
            m_idAccessors.put(accessor.getAttributeName(), (IdAccessor) accessor);
        }
        
        // Check if we already processed an EmbeddedId for this Entity or MappedSuperclass.
        if (accessor.isEmbeddedId() && hasEmbeddedId()) {
            throw ValidationException.multipleEmbeddedIdAnnotationsFound(getJavaClass(), accessor.getAttributeName(), this.getEmbeddedIdAttributeName());
        } 
        
        // 300051: store the single EmbeddedIdAccessor for use by hasEmbeddedId in MetadataProject.addMetamodelMappedSuperclass()
        if (accessor.isEmbeddedId()) {
            setEmbeddedIdAccessor((EmbeddedIdAccessor)accessor); 
        }
    }
    
    /**
     * INTERNAL:
     * We store these to validate the primary class when processing the entity 
     * class. Note: the pk id types are always stored as their boxed type (if 
     * applicable). Validation should therefore always be done against boxed 
     * types.
     * 
     * @see validateDerivedPKClassId
     * @see validatePKClassId
     */
    public void addPKClassId(String attributeName, String type) {
        m_pkClassIDs.put(attributeName, type);
    }
    
    /**
     * INTERNAL:
     * Add a property to the descriptor. Will check for an override/ignore case.
     */
    public void addProperty(PropertyMetadata property) {
        if (property.shouldOverride(m_properties.get(property.getName()))) {
            m_properties.put(property.getName(), property);
            m_descriptor.addUnconvertedProperty(property.getName(), property.getValue(), property.getJavaClassName(property.getValueType()));
        }
    }
    
    /**
     * INTERNAL:
     * Add a field representing the primary key or part of a composite primary 
     * key to the List of primary key fields on the relational descriptor 
     * associated with this metadata descriptor. Call this method if there
     * is no associated mapping accessor, e.g. a PrimaryKey annotation 
     * specification or a derived id mapping. Otherwise, regular JPA id mappings
     * should call addPrimaryKeyField(DatabaseField, MappingAccessor)
     */
    public void addPrimaryKeyField(DatabaseField field) {
        // Make sure the field has a table set.
        if (! field.hasTableName()) {
            field.setTable(getPrimaryTable());
        }
        
        // Add the field to the class descriptor.
        m_descriptor.addPrimaryKeyField(field);
        
        // Add the field to our internal field map.
        addField(field);
    }
    
    /**
     * INTERNAL:
     * Add a field representing the primary key or part of a composite primary 
     * key to the List of primary key fields on the relational descriptor 
     * associated with this metadata descriptor.
     */
    public void addPrimaryKeyField(DatabaseField field, MappingAccessor accessor) {
        addPrimaryKeyField(field);
        
        // Store the primary key field mappings keyed on their field name.
        m_primaryKeyAccessors.put(field, accessor);
    }
    
    /**
      * INTERNAL:
      * Store relationship accessors for later processing and quick look up.
      */
    public void addRelationshipAccessor(RelationshipAccessor accessor) {
        getProject().addRelationshipAccessor(accessor);
        
        // Store bidirectional ManyToMany relationships so that we may look at 
        // attribute names when defaulting join columns.
        if (accessor.isManyToMany()) {
            if (accessor.hasMappedBy()) {
                String referenceClassName = accessor.getReferenceClassName();
                
                // Initialize the map of bi-directional mappings for this class.
                if (! m_biDirectionalManyToManyAccessors.containsKey(referenceClassName)) {
                    m_biDirectionalManyToManyAccessors.put(referenceClassName, new HashMap<String, MetadataAccessor>());
                }
            
                m_biDirectionalManyToManyAccessors.get(referenceClassName).put(accessor.getMappedBy(), accessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addTable(DatabaseTable table) {
        m_descriptor.addTable(table);
    }
    
    /**
     * INTERNAL:
     * This method is called only for canonical model generation during the 
     * pre-processing stage. Canonical model generation needs to rebuild its 
     * accessor list from one compile round to another within Eclipse. This 
     * should not be called otherwise.
     * 
     * Anything that is set in the addAccessor(MappingAccessor) method should 
     * be cleared here.
     */
    public void clearMappingAccessors() {
        m_mappingAccessors.clear();
        m_embeddedIdAccessor = null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean excludeSuperclassListeners() {
        return m_descriptor.getEventManager().excludeSuperclassListeners();
    }
    
    /**
     * INTERNAL:
     */
    public String getAlias() {
        return m_descriptor.getAlias();
    }
    
    /**
     * INTERNAL:
     */
    public AssociationOverrideMetadata getAssociationOverrideFor(String attributeName) {
        return m_associationOverrides.get(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public Collection<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides.values();
    }
    
    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata getAttributeOverrideFor(String attributeName) {
        return m_attributeOverrides.get(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public Collection<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides.values();
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor getClassAccessor() {
        return m_classAccessor;
    }
    
    /**
     * INTERNAL:
     * Return the RelationalDescriptor instance associated with this MetadataDescriptor
     */
    public ClassDescriptor getClassDescriptor() {
        return m_descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public CMPPolicy getCMPPolicy() {
        return m_descriptor.getCMPPolicy();
    }
    
    /**
     * INTERNAL:
     */
    public List<ConvertMetadata> getConverts(String attributeName) {
        return m_converts.get(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public String getDefaultAccess() {
        return m_defaultAccess;
    }
    
    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata getDefaultAccessMethods() {
        return m_defaultAccessMethods;
    }
    
    /**
     * INTERNAL:
     */
    public String getDefaultCatalog() {
        return m_defaultCatalog;
    }
    
    /**
     * INTERNAL:
     */
    public String getDefaultSchema() {
        return m_defaultSchema;
    }
    
    /**
     * INTERNAL:
     */
    public List<TenantDiscriminatorColumnMetadata> getDefaultTenantDiscriminatorColumns() {
        return m_defaultTenantDiscriminatorColumns;
    }
    
    /**
     * INTERNAL:
     * The default table name is the descriptor alias, unless this descriptor 
     * metadata is an inheritance subclass with a SINGLE_TABLE strategy. Then 
     * it is the table name of the root descriptor metadata.
     */
    public String getDefaultTableName() {
        String defaultTableName =(getProject().useDelimitedIdentifier()) ? getAlias() : getAlias().toUpperCase();
        
        if (isInheritanceSubclass()) {    
            if (getInheritanceRootDescriptor().usesSingleTableInheritanceStrategy()) {
                defaultTableName = getInheritanceRootDescriptor().getPrimaryTableName();
            }
        }
        
        return defaultTableName;
    }
    
    /**
     * INTERNAL:
     */
    public List<ObjectAccessor> getDerivedIdAccessors(){
        return m_derivedIdAccessors;
    }
    
    /**
     * INTERNAL:
     * Return the embedded id accessor for this descriptor if one exists.
     */
    public EmbeddedIdAccessor getEmbeddedIdAccessor() {
        return m_embeddedIdAccessor;
    }
    
    /**
     * INTERNAL:
     */
    public String getEmbeddedIdAttributeName() {
        return m_embeddedIdAccessor.getAttributeName();
    }
    
    /**
     * INTERNAL:
     * This method assumes that by calling this method you are certain that
     * the related class accessor to this descriptor is an EntityAccessor.
     * You should not call this method otherwise, @see getClassAccessor()
     */
    public EntityAccessor getEntityAccessor() {
        return (EntityAccessor) m_classAccessor;
    }
    
    /**
     * INTERNAL:
     * Return the DatabaseField from the given field name from this descriptor.
     * It also checks primary key fields and parent descriptors. 
     */
    public DatabaseField getField(String fieldName) {
        if (! m_fields.containsKey(fieldName) && isInheritanceSubclass()) {
            return getInheritanceRootDescriptor().getField(fieldName);
        } else {
            return m_fields.get(fieldName);
        }
    }
    
    /**
     * INTERNAL:
     * Return the type from the generic name.
     */
    public String getGenericType(String genericName) {
       return m_genericTypes.get(genericName); 
    }
    
    /**
     * INTERNAL:
     */
    public Map getGenericTypes() {
        return m_genericTypes;
    }
    
    /**
     * INTERNAL:
     */
    public Map<String, IdAccessor> getIdAccessors() {
        return m_idAccessors;
    }
    
    /**
     * INTERNAL:
     * Return the primary key attribute name for this entity.
     */
    public String getIdAttributeName() {
        if (getIdAttributeNames().isEmpty()) {
            if (isInheritanceSubclass()) {
                return getInheritanceRootDescriptor().getIdAttributeName();
            } else {
                return "";
            }
        } else {
            return getIdAttributeNames().get(0);
        }
    }
    
    /**
     * INTERNAL:
     * Return the id attribute names declared on this descriptor metadata.
     */
    public List<String> getIdAttributeNames() {
        return m_idAttributeNames;
    }
    
    /**
     * INTERNAL:
     * Return the primary key attribute names for this entity. If there are no
     * id attribute names set then we are either:
     * 1) an inheritance subclass, get the id attribute names from the root
     *    of the inheritance structure.
     * 2) we have an embedded id. Get the id attribute names from the embedded
     *    descriptor metadata, which is equal the attribute names of all the
     *    direct to field mappings on that descriptor metadata. Currently does
     *    not traverse nested embeddables.
     */
    public List<String> getIdOrderByAttributeNames() {
        if (m_idOrderByAttributeNames.isEmpty()) {
            if (m_idAttributeNames.isEmpty()) {
                if (isInheritanceSubclass()) {  
                    // Get the id attribute names from our root parent.
                    m_idOrderByAttributeNames = getInheritanceRootDescriptor().getIdAttributeNames();
                } else {
                    // We must have a composite primary key as a result of an embedded id.
                    m_idOrderByAttributeNames = getMappingAccessor(getEmbeddedIdAttributeName()).getReferenceDescriptor().getOrderByAttributeNames();
                } 
            } else {
                m_idOrderByAttributeNames = m_idAttributeNames;
            }
        }
            
        return m_idOrderByAttributeNames;
    }
    
    
    /**
     * INTERNAL:
     * Assumes hasBidirectionalManyToManyAccessorFor has been called before
     * hand. 
     */
     public MetadataAccessor getBiDirectionalManyToManyAccessor(String className, String attributeName) {
        return m_biDirectionalManyToManyAccessors.get(className).get(attributeName);
    }
    
    /** 
     * INTERNAL:
     * Returns the immediate parent's descriptor in the inheritance hierarchy.
     */
    public MetadataDescriptor getInheritanceParentDescriptor() {
        return m_inheritanceParentDescriptor;
    }
    
    /** 
     * INTERNAL:
     * Returns the root descriptor of the inheritance hierarchy, that is, the 
     * one that defines the inheritance strategy.
     */
    public MetadataDescriptor getInheritanceRootDescriptor() {
        return m_inheritanceRootDescriptor;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getJavaClass() {
        return m_javaClass;
    }
    
    /**
     * INTERNAL:
     */
    public String getJavaClassName() {
        return m_descriptor.getJavaClassName();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return getProject().getLogger();
    }
    
    /**
     * INTERNAL:
     */
    public List<ConvertMetadata> getMapKeyConverts(String attributeName) {
        return m_mapKeyConverts.get(attributeName);
    }
    
    /**
     * INTERNAL:
     * This method will first check for an accessor with name equal to field or 
     * property name. If no accessor is found than it assumes the field or 
     * property name passed in may be a method name and converts it to its 
     * corresponding property name and looks for the accessor again. If still no 
     * accessor is found and this descriptor represents an inheritance subclass, 
     * then traverse up the chain to look for that accessor. Null is returned 
     * otherwise.
     */
    public MappingAccessor getMappingAccessor(String fieldOrPropertyName) {
        return getMappingAccessor(fieldOrPropertyName, true);
    }
    
    /**
     * INTERNAL:
     * This method will first check for an accessor with name equal to field or 
     * property name. If no accessor is found and the checkForMethodName flag is
     * set to true then we'll attempt to convert a potential method name to its 
     * corresponding property name and looks for the accessor again. If still no 
     * accessor is found and this descriptor represents an inheritance subclass, 
     * then traverse up the chain to look for that accessor. Null is returned 
     * otherwise.
     */
    protected MappingAccessor getMappingAccessor(String fieldOrPropertyName, boolean checkForMethodName) {
        MappingAccessor accessor = m_mappingAccessors.get(fieldOrPropertyName);
        
        if (accessor == null) {
            // Perhaps we have a method name. This is value add, and maybe we
            // really shouldn't do this but it covers the following case:
            // <order-by>age, getGender DESC</order-by>, where the user
            // specifies a method name.
            if (checkForMethodName) {
                accessor = m_mappingAccessors.get(Helper.getAttributeNameFromMethodName(fieldOrPropertyName));
            }
           
            // If still no accessor and we are an inheritance subclass, check 
            // our parent descriptor. Unless we are within a table per class 
            // strategy in which case, if the mapping doesn't exist within our
            // accessor list, we don't want to deal with it.
            if (accessor == null && isInheritanceSubclass() && ! usesTablePerClassInheritanceStrategy()) {
                accessor = getInheritanceParentDescriptor().getMappingAccessor(fieldOrPropertyName, checkForMethodName);
            }
        }
        
        if (accessor == null) {
            // Traverse any dot notation (nested embeddables) if specified.
            if (fieldOrPropertyName.contains(".")) {
                String attributeName = fieldOrPropertyName.substring(0, fieldOrPropertyName.indexOf("."));
                String subAttributeName = fieldOrPropertyName.substring(fieldOrPropertyName.indexOf(".") + 1);
            
                MappingAccessor embeddedAccessor = m_mappingAccessors.get(attributeName);
            
                if (embeddedAccessor != null) {
                    accessor = embeddedAccessor.getReferenceDescriptor().getMappingAccessor(subAttributeName, checkForMethodName);
                }
            }

            // If we are still null, maybe the user has not used a dot notation
            // that is, has not been fully specific. At this point this is value
            // add in that we will dig through the embeddable descriptors and 
            // look for and return the first mapping for the given attribute
            // name. This could be error prone, but looks like we have a number
            // of tests that don't use the dot notation.
            if (accessor == null) {
                // We didn't find an accessor on our descriptor (or a parent 
                // descriptor), check our aggregate descriptors now.
                for (MetadataDescriptor embeddableDescriptor : m_embeddableDescriptors) {
                    // If the attribute name employs the dot notation, rip off 
                    // the first  bit (up to the first dot and keep burying down 
                    // the embeddables)
                    String subAttributeName = new String(fieldOrPropertyName);
                    if (subAttributeName.contains(".")) {
                       subAttributeName = subAttributeName.substring(fieldOrPropertyName.indexOf(".") + 1);
                    }
            
                    accessor = embeddableDescriptor.getMappingAccessor(fieldOrPropertyName, checkForMethodName);
            
                    if (accessor != null) {
                        // Found one, stop looking ...
                        break;
                    }
                }
            }
        }
        
        return accessor;
    }
    
    /**
     * INTERNAL:
     * Return the collection of mapping accessors for this descriptor.
     */
    public Collection<MappingAccessor> getMappingAccessors() {
        return m_mappingAccessors.values();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseMapping getMappingForAttributeName(String attributeName) {
        return getMappingAccessor(attributeName).getMapping();
    }  
    
    /**
     * INTERNAL:
     */
    public List<DatabaseMapping> getMappings() {
        return m_descriptor.getMappings();
    }

    /**
     * INTERNAL:
     */
    public MetadataDescriptor getMetamodelMappedSuperclassChildDescriptor() {
        return m_metamodelMappedSuperclassChildDescriptor;
    }
    
    /**
     * INTERNAL:
     * This will return the attribute names for all the direct to field mappings 
     * on this descriptor metadata. This method will typically be called when an 
     * embedded or embedded id attribute has been specified as an order by 
     * field
     */
    public List<String> getOrderByAttributeNames() {
        if (m_orderByAttributeNames.isEmpty()) {
            for (DatabaseMapping mapping : getMappings()) {
                if (mapping.isDirectToFieldMapping()) {
                    m_orderByAttributeNames.add(mapping.getAttributeName());
                }
            }
        }
        
        return m_orderByAttributeNames;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getPKClass(){
        return m_pkClass;
    }
    
    /**
     * INTERNAL:
     */
    public Map<String, String> getPKClassIDs() {
        return m_pkClassIDs;
    }
    
    /**
     * INTERNAL:
     */
    public String getPKClassName() {
        String pkClassName = null;
        
        if (m_descriptor.hasCMPPolicy()) {
            pkClassName = ((CMP3Policy) m_descriptor.getCMPPolicy()).getPKClassName();    
        }
        
        return pkClassName;
    }
    
    /**
     * INTERNAL:
     * Return the primary key mapping for the given field. 
     */
    public MappingAccessor getPrimaryKeyAccessorForField(DatabaseField field) {
        return m_primaryKeyAccessors.get(field);
    }
    
    /**
     * INTERNAL:
     * Method to return the primary key field name this descriptor metadata. 
     * It assumes there is one.
     */
    public DatabaseField getPrimaryKeyField() {
        return getPrimaryKeyFields().iterator().next();
    }
    
    /**
     * INTERNAL:
     * Method to return the primary key field name for this descriptor metadata. 
     * It assumes there is one.
     */
    public String getPrimaryKeyFieldName() {
        return getPrimaryKeyField().getName();
    }
    
    /**
     * INTERNAL
     * Return the primary key fields for this descriptor metadata. If this is
     * an inheritance subclass and it has no primary key fields, then grab the 
     * primary key fields from the root. In a table per class inheritance 
     * strategy, the primary key fields (and all mappings for that matter) are
     * inherited from the parent meaning there is no need to check the parent
     * in this case, it should have primary key fields and checking the parent
     * could lead to processing errors.
     */
    public List<DatabaseField> getPrimaryKeyFields() {
        List<DatabaseField> primaryKeyFields = m_descriptor.getPrimaryKeyFields();
        
        if (primaryKeyFields.isEmpty() && isInheritanceSubclass() && ! usesTablePerClassInheritanceStrategy()) {
            primaryKeyFields = getInheritanceRootDescriptor().getPrimaryKeyFields();
        }
        
        return primaryKeyFields;
    }

    /**
     * INTERNAL:
     * Recursively check the potential chaining of the primary key fields from 
     * a inheritance subclass, all the way to the root of the inheritance 
     * hierarchy.
     */
    public DatabaseField getPrimaryKeyJoinColumnAssociation(DatabaseField foreignKey) {
        DatabaseField primaryKey = m_pkJoinColumnAssociations.get(foreignKey);

        if ( primaryKey == null || primaryKey.getName() == null || ! isInheritanceSubclass()) {
            return foreignKey;
        } else {
            return getInheritanceParentDescriptor().getPrimaryKeyJoinColumnAssociation(primaryKey);
        } 
    }
    
    /**
     * INTERNAL:
     * Returns the first primary key join column association if there is one.
     * Otherwise, the primary key field given is returned.
     */
    public DatabaseField getPrimaryKeyJoinColumnAssociationField(DatabaseField primaryKeyField) {
        if (! m_pkJoinColumnAssociations.isEmpty()) {
            return m_pkJoinColumnAssociations.keySet().iterator().next();
        }
        
        return primaryKeyField;
    }
    
    /**
     * INTERNAL:
     * Assumes there is one primary key field set. This method should be called 
     * when qualifying any primary key field (from a join column) for this 
     * descriptor. This method was created because in an inheritance hierarchy 
     * with a joined strategy we can't use getPrimaryTableName() since it would
     * return the wrong table name. From the spec, the primary key must be 
     * defined on the entity that is the root of the entity hierarchy or on a 
     * mapped superclass of the entity hierarchy. The primary key must be 
     * defined exactly once in an entity hierarchy.
     */
    public DatabaseTable getPrimaryKeyTable() {
        return getPrimaryKeyField().getTable();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseTable getPrimaryTable() {
        if (m_primaryTable == null && isInheritanceSubclass()) {
            return getInheritanceRootDescriptor().getPrimaryTable();
        } else {
            if (m_descriptor.isAggregateDescriptor()) {
                // Aggregate descriptors don't have tables, just return a 
                // a default empty table.
                return new DatabaseTable();
            }
            
            return m_primaryTable;
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getPrimaryTableName() {
        return getPrimaryTable().getName();
    }

    /**
     * INTERNAL:
     */
    public MetadataProject getProject() {
        return getClassAccessor().getProject();
    }
    
    /**
     * INTERNAL:
     */
    protected ReturningPolicy getReturningPolicy() {
        if (! m_descriptor.hasReturningPolicy()) {
            m_descriptor.setReturningPolicy(new ReturningPolicy());
        }
        
        return m_descriptor.getReturningPolicy();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getSequenceNumberField() {
        return m_descriptor.getSequenceNumberField();
    }
    
    /**
     * INTERNAL:
     * Assumes a call to hasSingleTableMultitenant has been made before hand.
     */
    public Map<String, List<DatabaseField>> getSingleTableMultitenantFields() {
        return ((SingleTableMultitenantPolicy) m_descriptor.getMultitenantPolicy()).getTenantDiscriminatorFieldsKeyedOnContext();
    }
    
    /**
     * INTERNAL:
     * Returns true is an additional criteria has been set on this descriptor's
     * query manager.
     */
    public boolean hasAdditionalCriteria() {
        return m_descriptor.getQueryManager().hasAdditionalCriteria();
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAssociationOverrideFor(String attributeName) {
        return m_associationOverrides.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAttributeOverrideFor(String attributeName) {
        return m_attributeOverrides.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasCompositePrimaryKey() {
        return getPrimaryKeyFields().size() > 1 || getPKClass() != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEmbeddedId() {
        return m_embeddedIdAccessor != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasExistenceChecking() {
        return m_existenceChecking != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasBiDirectionalManyToManyAccessorFor(String className, String attributeName) {
        if (m_biDirectionalManyToManyAccessors.containsKey(className)) {
            return m_biDirectionalManyToManyAccessors.get(className).containsKey(attributeName);
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Indicates that a Cache annotation or cache element has already been 
     * processed for this descriptor.
     */
    public boolean hasCache() {
        return m_hasCache;
    }
    
    /**
     * INTERNAL:
     * Indicates that a Cacheable annotation or cache element has already been 
     * processed for this descriptor.
     */
    public boolean hasCacheable() {
        return m_cacheable != null;
    }
    
    /**
     * INTERNAL:
     * Indicates that a CacheInterceptor annotation or cacheInterceptor element has already been 
     * processed for this descriptor.
     */
    public boolean hasCacheInterceptor() {
        return m_hasCacheInterceptor;
    }
    
    /**
     * INTERNAL:
     * Indicates that a DefaultRedirectors annotation or default-redirectors element has already been 
     * processed for this descriptor.
     */
    public boolean hasDefaultRedirectors() {
        return m_hasDefaultRedirectors;
    }
    
    /**
     * INTERNAL:
     * Indicates that a Change tracking annotation or change tracking element 
     * has already been processed for this descriptor.
     */
    public boolean hasChangeTracking() {
        return m_hasChangeTracking;
    }
    
    /**
     * INTERNAL:
     * Indicates that a copy Policy annotation or copy policy element 
     * has already been processed for this descriptor.
     */
    public boolean hasCopyPolicy() {
        return m_hasCopyPolicy;
    }

    /**
     * INTERNAL:
     * Return true if there is convert metadata for the given attribute name.
     */
    public boolean hasConverts(String attributeName) { 
        return m_converts.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     * Indicates that a customizer annotation or customizer element has already 
     * been processed for this descriptor.
     */
    public boolean hasCustomizer() {
        return m_hasCustomizer;
    }
    
    /**
     * INTERNAL:
     * Return whether there is an IdAccessor on this descriptor.
     */
    public boolean hasIdAccessor() {
        return ! m_idAccessors.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Return true if there is map key convert metadata for the given attribute 
     * name.
     */
    public boolean hasMapKeyConverts(String attributeName) { 
        return m_mapKeyConverts.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     * Returns true if we already have (processed) an accessor for the given
     * attribute name.
     */
    public boolean hasMappingAccessor(String attributeName) {
        return getMappingAccessor(attributeName, false) != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappingForAttributeName(String attributeName) {
        return m_descriptor.getMappingForAttributeName(attributeName) != null;
    }
    
    /**
     * INTERNAL:
     * Indicates if multitenant metadata has been processed for this descriptor.
     */
    public boolean hasMultitenant() { 
        return m_descriptor.hasMultitenantPolicy();
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasPKClass() {
        return m_pkClass != null;
    }
    
    /**
     * INTERNAL:
     * Indicates that a PrimaryKey annotation or primary-key element has been 
     * processed for this descriptor.
     */
    public boolean hasPrimaryKey() {
        return m_hasPrimaryKey;
    }
    
    /**
     * INTERNAL:
     * Return true is the descriptor has primary key fields set.
     */
    public boolean hasPrimaryKeyFields() {
        return m_descriptor.getPrimaryKeyFields().size() > 0;
    }
    
    /**
     * INTERNAL:
     * Indicates that a read only annotation or read only element has already 
     * been processed for this descriptor.
     */
    public boolean hasReadOnly() {
        return m_hasReadOnly;
    }
    
    /**
     * INTERNAL:
     * Indicates if single table multitenant metadata has been processed for 
     * this descriptor.
     */
    public boolean hasSingleTableMultitenant() { 
        return hasMultitenant() && m_descriptor.getMultitenantPolicy().isSingleTableMultitenantPolicy();
    }
    
    /**
     * INTERNAL:
     * Indicates that a SerializedObject annotation or serialized-object element has been 
     * processed for this descriptor.
     */
    public boolean m_hasSerializedObjectPolicy() {
        return m_hasSerializedObjectPolicy;
    }

    /**
     * INTERNAL:
     * Indicates that an explicit cacheable value of true has been set for 
     * this descriptor.
     */
    public boolean isCacheableTrue() {
        if (m_cacheable != null) {
            return m_cacheable.booleanValue();
        } else if (isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().isCacheableTrue();
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Indicates that an explicit cacheable value of false has been set for 
     * this descriptor.
     */
    public boolean isCacheableFalse() {
        if (m_cacheable != null) {
            return ! m_cacheable.booleanValue();
        } else if (isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().isCacheableFalse();
        }

        return false;
    }
    
    /**
     * INTERNAL:
     * Indicates that cascade-persist should be applied to all relationship 
     * mappings for this entity.
     */
    public boolean isCascadePersist() {
        return m_isCascadePersist;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isEmbeddable() {
        return m_descriptor.isAggregateDescriptor();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isEmbeddableCollection() {
        return m_descriptor.isAggregateCollectionDescriptor();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isInheritanceSubclass() {
        return m_inheritanceParentDescriptor != null;
    }
    
    /**
     * INTERNAL:
     * Return whether the ClassAccessor on this MetadataDescriptor is a MappedSuperclassAccessor.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */
    public boolean isMappedSuperclass() {
        return getClassAccessor().isMappedSuperclass();
    }
    
    /**
     * INTERNAL:
     */
    public boolean pkClassWasNotValidated() {
        return ! m_pkClassIDs.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Process this descriptors mapping accessors. Some accessors will not be 
     * processed right away, instead stored on the project for processing in a 
     * later  stage. This method can not and must not be called beyond 
     * MetadataProject stage 2 processing.
     */
    public void processMappingAccessors() {
        for (MappingAccessor accessor : m_mappingAccessors.values()) {
            if (! accessor.isProcessed()) {
                // If we a mapped key map accessor with an embeddable as the 
                // key, process that embeddable accessor now.
                if (accessor.isMappedKeyMapAccessor()) {
                    MappedKeyMapAccessor mapAccessor = (MappedKeyMapAccessor) accessor;
                    EmbeddableAccessor mapKeyEmbeddableAccessor = getProject().getEmbeddableAccessor(mapAccessor.getMapKeyClass());
                    
                    if (mapKeyEmbeddableAccessor != null && ! mapKeyEmbeddableAccessor.isProcessed()) {
                        mapKeyEmbeddableAccessor.process();
                    }
                }
                
                // We need to defer the processing of some mappings to stage
                // 3 processing. Accessors are added to different lists since
                // the order or processing of those accessors is important.
                // See MetadataProject.processStage2() for more details.
                // Care must be taken in the order of checking here.
                if (accessor.isDirectEmbeddableCollection() || accessor.isEmbedded()) {
                    EmbeddableAccessor embeddableAccessor = getProject().getEmbeddableAccessor(accessor.getReferenceClass());
                    
                    // If there is no embeddable accessor at this point,
                    // something is wrong, throw an exception. Note a direct
                    // embeddable collection can't hit here since we don't build 
                    // a direct embeddable collection if the reference class is 
                    // not an Embeddable.
                    if (embeddableAccessor == null) {
                        throw ValidationException.invalidEmbeddedAttribute(getJavaClass(), accessor.getAttributeName(), accessor.getReferenceClass());
                    } else {
                        // Process the embeddable class now (if it's not already processed)
                        if (! embeddableAccessor.isProcessed()) {
                            embeddableAccessor.process();
                        }
                    
                        // Store this descriptor metadata. It may be needed again 
                        // later on to look up a mappedBy attribute etc.
                        addEmbeddableDescriptor(embeddableAccessor.getDescriptor());
                    
                        // Since association overrides are not allowed on 
                        // embeddedid's we can and must process it right now,
                        // instead of deferring it till after the relationship
                        // accessors have processed.
                        if (accessor.isEmbeddedId() || accessor.isDerivedIdClass()) {
                            accessor.process();
                        } else {
                            // Otherwise defer it because of association overrides.
                            // We can't process this mapping till all the
                            // relationship mappings have been processed.
                            getProject().addEmbeddableMappingAccessor(accessor);
                        }
                    }
                } else if (accessor.isDirectCollection()) {
                    getProject().addDirectCollectionAccessor(accessor);
                } else if (accessor.isRelationship()) {
                    if (accessor.derivesId()) {
                        m_derivedIdAccessors.add((ObjectAccessor) accessor);
                        getProject().addAccessorWithDerivedId(m_classAccessor);
                    } else {
                        addRelationshipAccessor((RelationshipAccessor) accessor);
                    }
                } else {
                    accessor.process();
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Remove the following field from the primary key field lists. Presumably,
     * it is not a primary key field or is being replaced with another. See
     * EmbeddedAccessor processAttributeOverride method.
     */
    public void removePrimaryKeyField(DatabaseField field) {
        // Remove the field from the class descriptor list.
        m_descriptor.getPrimaryKeyFields().remove(field);
        
        // Remove the primary key field and accessor.
        m_primaryKeyAccessors.remove(field);
    }
    
    /**
     * INTERNAL:
     * Record whether this descriptor uses property access. This information is used to
     * modify the behavior of some of our weaving features
     */
    public void setAccessTypeOnClassDescriptor(String accessType){
        if (accessType.equals(JPA_ACCESS_PROPERTY)) {
            m_descriptor.usePropertyAccessForWeaving();
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setAlias(String alias) {
        m_descriptor.setAlias(alias);
    }
    
    /**
     * INTERNAL:
     * Get entity @Cacheable annotation value.
     * @return Entity @Cacheable annotation value. This value refers to current
     *         class only and does not contain inherited value from parent
     *         classes.
     */
    public Boolean getCacheable() {
        return m_cacheable;
    }

    /**
     * INTERNAL:
     * Set entity @Cacheable annotation value.
     * @param cacheable Entity @Cacheable annotation value. This value refers
     *        to current class only and does not contain inherited value from
     *        parent classes.
     */
    public void setCacheable(Boolean cacheable) {
        m_cacheable = cacheable;
    }
    
    /**
     * INTERNAL:
     * Pass entity @Cacheable annotation value to cache configuration object
     * in class descriptor.
     */
    public void setCacheableInDescriptor() {
        m_descriptor.setCacheable(m_cacheable);
    }

    /**
     * INTERNAL:
     */
    public void setClassAccessor(ClassAccessor accessor) {
        m_classAccessor = accessor;
    }
    
    /**
     * INTERNAL:
     */
    public void setDefaultAccess(String defaultAccess) {
        m_defaultAccess = defaultAccess;
    }
    
    /**
     * INTERNAL:
     * Default access methods can come from the following locations 
     * (in XML only) :
     * - persistence-unit-defaults
     * - entity-mappings
     * - entity
     * - embeddable
     * 
     * Be default, the default access methods are set to use "get" and "set"
     * unless they are overridden by discovering access methods specified at
     * one of the locations above. 
     */
    public void setDefaultAccessMethods(AccessMethodsMetadata accessMethods) {
        m_defaultAccessMethods = accessMethods;
        getClassDescriptor().getVirtualAttributeMethods().add(new VirtualAttributeMethodInfo(accessMethods.getGetMethodName(),accessMethods.getSetMethodName()));
    }
    
    /**
     * INTERNAL:
     */
    public void setDefaultCatalog(String defaultCatalog) {
        m_defaultCatalog = defaultCatalog;
    }
    
    /**
     * INTERNAL:
     */
    public void setDefaultSchema(String defaultSchema) {
        m_defaultSchema = defaultSchema;
    }
    
    /**
     * INTERNAL:
     */
    public void setDefaultTenantDiscriminatorColumns(List<TenantDiscriminatorColumnMetadata> defaultTenantDiscriminatorColumns) {
        m_defaultTenantDiscriminatorColumns = defaultTenantDiscriminatorColumns;
    }
    
    /**
     * INTERNAL:
     * Set the RelationalDescriptor instance associated with this MetadataDescriptor
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        m_descriptor = descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public void setEmbeddedIdAccessor(EmbeddedIdAccessor embeddedIdAccessor) {
        m_embeddedIdAccessor = embeddedIdAccessor;
    }
    
    /** 
     * INTERNAL:
     */
    public void setEntityEventListener(EntityListener listener) {
        m_descriptor.getEventManager().setEntityEventListener(listener);
    }
    
    /**
     * INTERNAL:
     */
    public void setExcludeDefaultListeners(boolean excludeDefaultListeners) {
        m_descriptor.getEventManager().setExcludeDefaultListeners(excludeDefaultListeners);
    }
    
    /**
     * INTERNAL:
     */
    public void setExcludeSuperclassListeners(boolean excludeSuperclassListeners) {
        m_descriptor.getEventManager().setExcludeSuperclassListeners(excludeSuperclassListeners);
    }
    
    /**
     * INTERNAL:
     */
    public void setExistenceChecking(String existenceChecking) {
        m_existenceChecking = existenceChecking;
        
        if (existenceChecking.equals(ExistenceType.CHECK_CACHE.name())) {
            m_descriptor.getQueryManager().checkCacheForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.CHECK_DATABASE.name())) {
            m_descriptor.getQueryManager().checkDatabaseForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.ASSUME_EXISTENCE.name())) {
            m_descriptor.getQueryManager().assumeExistenceForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.ASSUME_NON_EXISTENCE.name())) {
            m_descriptor.getQueryManager().assumeNonExistenceForDoesExist();
        }
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a cache annotation or cache xml element.
     */
    public void setHasCache() {
        m_hasCache = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a cache annotation or cache xml element.
     */
    public void setHasCacheInterceptor() {
        m_hasCacheInterceptor = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a customizer annotation or customizer
     * xml element.
     */
    public void setHasCustomizer() {
        m_hasCustomizer = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a cache annotation or cache xml element.
     */
    public void setHasDefaultRedirectors() {
        m_hasDefaultRedirectors = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a PrimaryKey annotation or primary-key
     * xml element.
     */
    public void setHasPrimaryKey() {
       m_hasPrimaryKey = true; 
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a change tracking annotation or change
     * tracking xml element.
     */
    public void setHasChangeTracking() {
        m_hasChangeTracking = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a copy policy annotation or copy policy xml element.
     */
    public void setHasCopyPolicy() {
        m_hasCopyPolicy = true;
    }

    /**
     * INTERNAL:
     * Indicates that we have processed a serialized object annotation or serialized object xml element.
     */
    public void setHasSerializedObjectPolicy() {
        m_hasSerializedObjectPolicy = true;
    }

    /**
     * INTERNAL:
     * Set the immediate parent's descriptor of the inheritance hierarchy.
     */
    public void setInheritanceParentDescriptor(MetadataDescriptor inheritanceParentDescriptor) {
        m_inheritanceParentDescriptor = inheritanceParentDescriptor;
    }

    /**
     * INTERNAL:
     * Set the root descriptor of the inheritance hierarchy, that is, the one 
     * that defines the inheritance strategy.
     */
    public void setInheritanceRootDescriptor(MetadataDescriptor inheritanceRootDescriptor) {
        m_inheritanceRootDescriptor = inheritanceRootDescriptor;
    }

    /**
     * INTERNAL:
     * Indicates that cascade-persist should be added to the set of cascade 
     * values for all relationship mappings.
     */
    public void setIsCascadePersist(boolean isCascadePersist) {
        m_isCascadePersist = isCascadePersist;
    }

    /**
     * INTERNAL:
     */
    public void setIsEmbeddable() {
        m_descriptor.descriptorIsAggregate();
    }

    /**
     * INTERNAL:
     * Used to set this descriptors java class. 
     */
    public void setJavaClass(MetadataClass javaClass) {
        m_javaClass = javaClass;
        m_descriptor.setJavaClassName(javaClass.getName());

        // If the javaClass is an interface, add it to the java interface name
        // on the relational descriptor.
        if (javaClass.isInterface()) {
            m_descriptor.setJavaInterfaceName(javaClass.getName());
        }
    }

    /**
     * INTERNAL:
     */
    public void setMetamodelMappedSuperclassChildDescriptor(MetadataDescriptor childDescriptor) {
        m_metamodelMappedSuperclassChildDescriptor = childDescriptor;
    }

    /**
     * INTERNAL:
     */
    public void setOptimisticLockingPolicy(OptimisticLockingPolicy policy) {
        m_descriptor.setOptimisticLockingPolicy(policy);
    }

    /**
     * INTERNAL:
     */
    public void setPKClass(MetadataClass pkClass) {
        m_pkClass = pkClass;
        CMP3Policy policy = new CMP3Policy();
        policy.setPrimaryKeyClassName(pkClass.getName());
        m_descriptor.setCMPPolicy(policy);
    }

    /**
     * INTERNAL:
     */
    public void setPrimaryTable(DatabaseTable primaryTable) {
        addTable(primaryTable);
        m_primaryTable = primaryTable;
    }

    /**
     * INTERNAL:
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            m_descriptor.setReadOnly();
        }
        
        m_hasReadOnly = true;
    }

    /**
     * INTERNAL:
     */
    public void setSequenceNumberField(DatabaseField field) {
        m_descriptor.setSequenceNumberField(field);
    }

    /**
     * INTERNAL:
     */
    public void setSequenceNumberName(String name) {
        m_descriptor.setSequenceNumberName(name);
    }

    /**
     * INTERNAL:
     */
    public void setUsesCascadedOptimisticLocking(Boolean usesCascadedOptimisticLocking) {
        m_usesCascadedOptimisticLocking = usesCascadedOptimisticLocking;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getJavaClassName();
    }

    /**
     * INTERNAL:
     */
    public void useNoCache() {
        m_descriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
    }

    /**
     * INTERNAL:
     */
    public boolean usesCascadedOptimisticLocking() {
        return m_usesCascadedOptimisticLocking != null && m_usesCascadedOptimisticLocking.booleanValue();
    }

    /**
     * INTERNAL:
     * Returns true if this class uses default property access. All access 
     * discovery and processing should have been performed before calling 
     * this method and a default access type should have been set. 
     */
    public boolean usesDefaultPropertyAccess() {
        return m_defaultAccess.equals(JPA_ACCESS_PROPERTY);
    }

    /**
     * INTERNAL:
     */
    public boolean usesOptimisticLocking() {
        return m_descriptor.usesOptimisticLocking();
    }

    /**
     * INTERNAL:
     * Indicates if the strategy on the descriptor's inheritance policy is 
     * SINGLE_TABLE. This method must only be called on those descriptors 
     * holding an EntityAccessor. NOTE: Inheritance is currently not supported 
     * on embeddables.
     */
    public boolean usesSingleTableInheritanceStrategy() {
        return ((EntityAccessor) m_classAccessor).getInheritance().usesSingleTableStrategy();
    }

    /**
     * INTERNAL:
     * Return true if this descriptor uses a table per class inheritance policy.
     */
    public boolean usesTablePerClassInheritanceStrategy() {
        return m_descriptor.hasTablePerClassPolicy();
    }

    /**
     * INTERNAL:
     * Return true if this descriptors class processed OptimisticLocking 
     * meta data of type VERSION_COLUMN.
     */
    public boolean usesVersionColumnOptimisticLocking() {
        // If an optimistic locking metadata of type VERSION_COLUMN was not 
        // specified, then m_usesCascadedOptimisticLocking will be null, that 
        // is, we won't have processed the cascade value.
        return m_usesCascadedOptimisticLocking != null;
    }

    /**
     * INTERNAL:
     * This method is used to validate derived id fields only. Re-using the
     * invalid composite pk attribute validation exception would yield an
     * interesting error message. Therefore, this method should be used
     * to validate derived id members. When validating derived ids things are
     * slightly reversed in terms on context. The expectedType passed in should
     * be the boxed type (were applicable).
     */
    public void validateDerivedPKClassId(String attributeName, String expectedType, String referenceClassName) {
        if (m_pkClassIDs.containsKey(attributeName))  {
            String actualType = m_pkClassIDs.get(attributeName);
            
            if (actualType.equals(expectedType)) {
                m_pkClassIDs.remove(attributeName);
            } else {
                throw ValidationException.invalidDerivedCompositePKAttribute(referenceClassName, getPKClassName(), attributeName, expectedType, actualType);
            }
        }
    }

    /**
     * INTERNAL:
     * This method is used only to validate id fields that were found on a
     * pk class were also found on the entity. The actualType passed in should
     * be the boxed type (were applicable).
     */
    public void validatePKClassId(String attributeName, String actualType) {
        if (m_pkClassIDs.containsKey(attributeName))  {
            String expectedType =  m_pkClassIDs.get(attributeName);
            
            if (expectedType.equals(actualType)) {
                m_pkClassIDs.remove(attributeName);
            } else {
                throw ValidationException.invalidCompositePKAttribute(getJavaClassName(), getPKClassName(), attributeName, expectedType, actualType);
            }
        }
    }
}
