/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     stardif - updates for Cascaded locking and inheritance
 *     02/20/2009-1.1 Guy Pelletier 
 *       - 259829: TABLE_PER_CLASS with abstract classes does not work
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     04/05/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 3)
 *     04/21/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 5)
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 *     06/25/2014-2.5.2 Rick Curtis 
 *       - 438177: Support M2M map with jointable
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import org.eclipse.persistence.descriptors.MultitenantPolicy;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.AbstractColumnMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.descriptors.copying.*;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedChangeTracking;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * <p><b>Purpose</b>:
 * Abstract descriptor class for defining persistence information on a class.
 * This class provides the data independent behavior and is subclassed,
 * for relational, object-relational, EIS, XML, etc.
 *
 * @see RelationalDescriptor
 * @see org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor
 * @see org.eclipse.persistence.eis.EISDescriptor
 * @see org.eclipse.persistence.oxm.XMLDescriptor
 */
public class ClassDescriptor extends CoreDescriptor<AttributeGroup, DescriptorEventManager, DatabaseField, InheritancePolicy, InstantiationPolicy, Vector, ObjectBuilder> implements Cloneable, Serializable {
    protected Class javaClass;
    protected String javaClassName;
    protected Vector<DatabaseTable> tables;
    protected transient DatabaseTable defaultTable;
    protected List<DatabaseField> primaryKeyFields;
    protected Map<DatabaseTable, Map<DatabaseField, DatabaseField>> additionalTablePrimaryKeyFields;
    protected transient List<DatabaseTable> multipleTableInsertOrder;
    protected Map<DatabaseTable, Set<DatabaseTable>> multipleTableForeignKeys;
    /** Support delete cascading on the database for multiple and inheritance tables. */
    protected boolean isCascadeOnDeleteSetOnDatabaseOnSecondaryTables;

    protected transient Vector<DatabaseField> fields;
    protected transient Vector<DatabaseField> allFields;
    protected transient List<DatabaseField> selectionFields;
    protected transient List<DatabaseField> allSelectionFields;
    protected Vector<DatabaseMapping> mappings;
    
    //Used to track which other classes reference this class in cases where
    // the referencing classes need to be notified of something.
    protected Set<ClassDescriptor> referencingClasses;

    //used by the lock on clone process.  This will contain the foreign reference
    //mapping without indirection
    protected List<DatabaseMapping> lockableMappings;
    protected Map<String, QueryKey> queryKeys;

    // Additional properties.
    protected String sequenceNumberName;
    protected DatabaseField sequenceNumberField;
    protected transient String sessionName;
    protected transient Vector constraintDependencies;
    protected transient String amendmentMethodName;
    protected transient Class amendmentClass;
    protected String amendmentClassName;
    protected String alias;
    protected boolean shouldBeReadOnly;
    protected boolean shouldAlwaysConformResultsInUnitOfWork;

    // for bug 2612601 allow ability not to register results in UOW.
    protected boolean shouldRegisterResultsInUnitOfWork = true;

    // Delegation objects, these perform most of the behavior.
    protected DescriptorQueryManager queryManager;
    protected CopyPolicy copyPolicy;
    protected String copyPolicyClassName;
    protected InterfacePolicy interfacePolicy;
    protected OptimisticLockingPolicy optimisticLockingPolicy;
    protected List<CascadeLockingPolicy> cascadeLockingPolicies;
    protected WrapperPolicy wrapperPolicy;
    protected ObjectChangePolicy changePolicy;
    protected ReturningPolicy returningPolicy;
    protected HistoryPolicy historyPolicy;
    protected String partitioningPolicyName;
    protected PartitioningPolicy partitioningPolicy;
    protected CMPPolicy cmpPolicy;
    protected CachePolicy cachePolicy;
    protected MultitenantPolicy multitenantPolicy;
    protected SerializedObjectPolicy serializedObjectPolicy;

    //manage fetch group behaviors and operations
    protected FetchGroupManager fetchGroupManager;

    /** Additional properties may be added. */
    protected Map properties;
    /** Allow the user to defined un-converted properties which will be initialized at runtime. */
    protected Map<String, List<String>> unconvertedProperties;
    
    protected transient int initializationStage;
    protected transient int interfaceInitializationStage;
    /** The following are the [initializationStage] states the descriptor passes through during the initialization. */
    protected static final int UNINITIALIZED = 0;
    protected static final int PREINITIALIZED = 1;
    protected static final int INITIALIZED = 2; // this state represents a fully initialized descriptor
    protected static final int POST_INITIALIZED = 3; // however this value is used by the public function isFullyInitialized()
    protected static final int ERROR = -1;
    
    protected int descriptorType;
    /** Define valid descriptor types. */
    protected static final int NORMAL = 0;
    protected static final int INTERFACE = 1;
    protected static final int AGGREGATE = 2;
    protected static final int AGGREGATE_COLLECTION = 3;
    
    protected boolean shouldOrderMappings;
    protected CacheInvalidationPolicy cacheInvalidationPolicy = null;

    /** PERF: Used to optimize cache locking to only acquire deferred locks when required (no-indirection). */
    protected boolean shouldAcquireCascadedLocks = false;
    /** INTERNAL: flag to indicate the initialization state of cascade locking for this descriptor */
    protected boolean cascadedLockingInitialized = false;

    /** PERF: Compute and store if the primary key is simple (direct-mapped) to allow fast extraction. */
    protected boolean hasSimplePrimaryKey = false;
    
    /**
     * Defines if any mapping reference a field in a secondary table.
     * This is used to disable deferring multiple table writes.
     */
    protected boolean hasMultipleTableConstraintDependecy = false;

    public static final int UNDEFINED_OBJECT_CHANGE_BEHAVIOR = CachePolicy.UNDEFINED_OBJECT_CHANGE_BEHAVIOR;
    public static final int SEND_OBJECT_CHANGES = CachePolicy.SEND_OBJECT_CHANGES;
    public static final int INVALIDATE_CHANGED_OBJECTS = CachePolicy.INVALIDATE_CHANGED_OBJECTS;
    public static final int SEND_NEW_OBJECTS_WITH_CHANGES = CachePolicy.SEND_NEW_OBJECTS_WITH_CHANGES;
    public static final int DO_NOT_SEND_CHANGES = CachePolicy.DO_NOT_SEND_CHANGES;

    public static final int UNDEFINED_ISOLATATION = CachePolicy.UNDEFINED_ISOLATATION;
    public static final int USE_SESSION_CACHE_AFTER_TRANSACTION = CachePolicy.USE_SESSION_CACHE_AFTER_TRANSACTION;
    public static final int ISOLATE_NEW_DATA_AFTER_TRANSACTION = CachePolicy.ISOLATE_NEW_DATA_AFTER_TRANSACTION; // this is the default behaviour even when undefined.
    public static final int ISOLATE_CACHE_AFTER_TRANSACTION = CachePolicy.ISOLATE_CACHE_AFTER_TRANSACTION;
    public static final int ISOLATE_FROM_CLIENT_SESSION = CachePolicy.ISOLATE_FROM_CLIENT_SESSION;  // Entity Instances only exist in UOW and shared cache.
    public static final int ISOLATE_CACHE_ALWAYS = CachePolicy.ISOLATE_CACHE_ALWAYS;

    /** INTERNAL: Backdoor for using changes sets for new objects. */
    public static boolean shouldUseFullChangeSetsForNewObjects = false;
    
    /** Allow connection unwrapping to be configured. */
    protected boolean isNativeConnectionRequired;

    /** Allow zero primary key validation to be configured. */
    protected IdValidation idValidation;
    
    /** Allow zero primary key validation to be configured per field. */
    protected List<IdValidation> primaryKeyIdValidations;
    
    // JPA 2.0 Derived identities - map of mappings that act as derived ids
    protected Map<String, DatabaseMapping> derivesIdMappings;
        
    //Added for default Redirectors
    protected QueryRedirector defaultQueryRedirector;
    protected QueryRedirector defaultReadAllQueryRedirector;
    protected QueryRedirector defaultReadObjectQueryRedirector;
    protected QueryRedirector defaultReportQueryRedirector;
    protected QueryRedirector defaultUpdateObjectQueryRedirector;
    protected QueryRedirector defaultInsertObjectQueryRedirector;
    protected QueryRedirector defaultDeleteObjectQueryRedirector;
    
    //Added for default Redirectors
    protected String defaultQueryRedirectorClassName;
    protected String defaultReadAllQueryRedirectorClassName;
    protected String defaultReadObjectQueryRedirectorClassName;
    protected String defaultReportQueryRedirectorClassName;
    protected String defaultUpdateObjectQueryRedirectorClassName;
    protected String defaultInsertObjectQueryRedirectorClassName;
    protected String defaultDeleteObjectQueryRedirectorClassName;
    
    /** Store the Sequence used for the descriptor. */
    protected Sequence sequence; 
    
    /** Mappings that require postCalculateChanges method to be called */
    protected List<DatabaseMapping> mappingsPostCalculateChanges;
    /** Mappings that require postCalculateChangesOnDeleted method to be called */
    protected List<DatabaseMapping> mappingsPostCalculateChangesOnDeleted;
    
    /** used by aggregate descriptors to hold additional fields needed when they are stored in an AggregatateCollection
     *  These fields are generally foreign key fields that are required in addition to the fields in the descriptor's 
     *  mappings to uniquely identify the Aggregate*/
    protected transient List<DatabaseField> additionalAggregateCollectionKeyFields;
    
    /** stores a list of mappings that require preDelete as a group prior to the delete individually */
    protected List<DatabaseMapping> preDeleteMappings;
    
    /** stores fields that are written by Map key mappings so they can be checked for multiple writable mappings */
    protected transient List<DatabaseField> additionalWritableMapKeyFields;
    
    /** whether this descriptor has any relationships through its mappings, through inheritance, or through aggregates */
    protected boolean hasRelationships = false;
    
    /** Stores a set of FK fields that will be cached to later retrieve noncacheable mappings */
    protected Set<DatabaseField> foreignKeyValuesForCaching;
    
    /** caches if this descriptor has any non cacheable mappings */
    protected boolean hasNoncacheableMappings = false;
    
    /** This flag stores whether this descriptor is using Property access based on JPA semantics.  It is used to modify
     * the behavior of our weaving functionality as it pertains to adding annotations to fields
     */
    protected boolean weavingUsesPropertyAccess = false;
    
    /** A list of methods that are used by virtual mappings.  This list is used to control weaving of methods
     * used for virtual access*/
    protected List<VirtualAttributeMethodInfo> virtualAttributeMethods = null;
    
    /**
     * A list of AttributeAccessors in order of access from root to leaf to arrive at current AggregateDescriptor.
     * Only application for Aggregate Descriptors.
     */
    protected List<AttributeAccessor> accessorTree;

    /**
     * JPA DescriptorCustomizer list stored here to preserve it when caching the project
     */
    protected String descriptorCustomizerClassName;

    /**
     * This flag controls if a UOW should acquire locks for clone or simple clone the instance passed to registerExistingObject.  If the IdentityMap type does not
     * have concurrent access this can save a return to the identity map for cloning.
     */
    protected boolean shouldLockForClone = true;

    /**
     * PUBLIC:
     * Return a new descriptor.
     */
    public ClassDescriptor() {
        // Properties
        this.tables = NonSynchronizedVector.newInstance(3);
        this.mappings = NonSynchronizedVector.newInstance();
        this.primaryKeyFields = new ArrayList(2);
        this.fields = NonSynchronizedVector.newInstance();
        this.allFields = NonSynchronizedVector.newInstance();
        this.constraintDependencies = NonSynchronizedVector.newInstance(2);
        this.multipleTableForeignKeys = new HashMap(5);
        this.queryKeys = new HashMap(5);
        this.initializationStage = UNINITIALIZED;
        this.interfaceInitializationStage = UNINITIALIZED;
        this.descriptorType = NORMAL;
        this.shouldOrderMappings = true;
        this.shouldBeReadOnly = false;
        this.shouldAlwaysConformResultsInUnitOfWork = false;
        this.shouldAcquireCascadedLocks = false;
        this.hasSimplePrimaryKey = false;
        this.derivesIdMappings = new HashMap(5);
        
        this.referencingClasses = new HashSet<ClassDescriptor>();

        // Policies
        this.objectBuilder = new ObjectBuilder(this);
        this.cachePolicy = new CachePolicy();
        
        this.additionalWritableMapKeyFields = new ArrayList(2);
        this.foreignKeyValuesForCaching = new HashSet<DatabaseField>();
    }

    /**
     * PUBLIC:
     * This method should only be used for interface descriptors.  It
     * adds an abstract query key to the interface descriptor.  Any
     * implementors of that interface must define the query key
     * defined by this abstract query key.
     */
    public void addAbstractQueryKey(String queryKeyName) {
        QueryKey queryKey = new QueryKey();
        queryKey.setName(queryKeyName);
        addQueryKey(queryKey);
    }
    
    /**
     * INTERNAL:
     * Add the cascade locking policy to all children that have a relationship to this descriptor
     * either by inheritance or by encapsulation/aggregation.
     * @param policy - the CascadeLockingPolicy
     */
    public void addCascadeLockingPolicy(CascadeLockingPolicy policy) {
        getCascadeLockingPolicies().add(policy);
        // 232608: propagate later version changes up to the locking policy on a parent branch by setting the policy on all children here            
        if (hasInheritance()) {
            // InOrder traverse the entire [deep] tree, not just the next level
            for (ClassDescriptor parent : getInheritancePolicy().getAllChildDescriptors()) {
                // Set the same cascade locking policy on all descriptors that inherit from this descriptor.
                parent.addCascadeLockingPolicy(policy);
            }
        }

        // do not propagate an extra locking policy to other mappings, if this descriptor already
        // has a cascaded optimistic locking policy that will be cascaded
        if (!this.cascadedLockingInitialized) {
            // never cascade locking until descriptor is initialized 
            if (isInitialized(INITIALIZED)) {
                // Set cascade locking policies on privately owned children mappings.
                for (DatabaseMapping mapping : getMappings()) {
                    prepareCascadeLockingPolicy(mapping);
                }
                this.cascadedLockingInitialized = true;
            }
        }
    }
    
    /**
     * ADVANCED:
     * EclipseLink automatically orders database access through the foreign key information provided in 1:1 and 1:m mappings.
     * In some case when 1:1 are not defined it may be required to tell the descriptor about a constraint,
     * this defines that this descriptor has a foreign key constraint to another class and must be inserted after
     * instances of the other class.
     */
    public void addConstraintDependencies(Class dependencies) {
        addConstraintDependency(dependencies);
    }
    
    /**
     * ADVANCED:
     * EclipseLink automatically orders database access through the foreign key information provided in 1:1 and 1:m mappings.
     * In some case when 1:1 are not defined it may be required to tell the descriptor about a constraint,
     * this defines that this descriptor has a foreign key constraint to another class and must be inserted after
     * instances of the other class.
     */
    public void addConstraintDependency(Class dependencies) {
        getConstraintDependencies().add(dependencies);
    }
    
    /**
     * Return a new direct/basic mapping for this type of descriptor.
     */
    public AbstractDirectMapping newDirectMapping() {
        return new DirectToFieldMapping();
    }
    
    /**
     * Return a new aggregate/embedded mapping for this type of descriptor.
     */
    public AggregateMapping newAggregateMapping() {
        return new AggregateObjectMapping();
    }
    
    /**
     * Return a new aggregate collection/element collection mapping for this type of descriptor.
     */
    public DatabaseMapping newAggregateCollectionMapping() {
        return new AggregateCollectionMapping();
    }
    
    /**
     * Return a new direct collection/element collection mapping for this type of descriptor.
     */
    public DatabaseMapping newDirectCollectionMapping() {
        return new DirectCollectionMapping();
    }
    
    /**
     * Return a new one to one mapping for this type of descriptor.
     */
    public ObjectReferenceMapping newOneToOneMapping() {
        OneToOneMapping mapping = new OneToOneMapping();
        mapping.setIsOneToOneRelationship(true);
        return mapping;
    }
    
    /**
     * Return a new many to one mapping for this type of descriptor.
     */
    public ObjectReferenceMapping newManyToOneMapping() {
        return new ManyToOneMapping();
    }
    
    /**
     * Return a new one to many mapping for this type of descriptor.
     */
    public CollectionMapping newOneToManyMapping() {
        return new OneToManyMapping();
    }
    
    /**
     * Return a new one to many mapping for this type of descriptor.
     */
    public CollectionMapping newUnidirectionalOneToManyMapping() {
        return new UnidirectionalOneToManyMapping();
    }
    
    /**
     * Return a new one to many mapping for this type of descriptor.
     */
    public CollectionMapping newManyToManyMapping() {
        return new ManyToManyMapping();
    }
    
    /**
     * PUBLIC:
     * Add a direct to field mapping to the receiver. The new mapping specifies that
     * an instance variable of the class of objects which the receiver describes maps in
     * the default manner for its type to the indicated database field.
     *
     * @param attributeName is the name of an instance variable of the
     * class which the receiver describes.
     * @param fieldName is the name of the database column which corresponds
     * with the designated instance variable.
     * @return The newly created DatabaseMapping is returned.
     */
    public DatabaseMapping addDirectMapping(String attributeName, String fieldName) {
        AbstractDirectMapping mapping = newDirectMapping();

        mapping.setAttributeName(attributeName);
        mapping.setField(new DatabaseField(fieldName));

        return addMapping(mapping);
    }

    /**
     * PUBLIC:
     * Add a direct to field mapping to the receiver. The new mapping specifies that
     * a variable accessed by the get and set methods of the class of objects which
     * the receiver describes maps in  the default manner for its type to the indicated
     * database field.
     */
    public DatabaseMapping addDirectMapping(String attributeName, String getMethodName, String setMethodName, String fieldName) {
        AbstractDirectMapping mapping = (AbstractDirectMapping)addDirectMapping(attributeName, fieldName);

        mapping.setSetMethodName(setMethodName);
        mapping.setGetMethodName(getMethodName);

        return mapping;
    }

    /**
     * PUBLIC:
     * Add a query key to the descriptor. Query keys define Java aliases to database fields.
     */
    public void addDirectQueryKey(String queryKeyName, String fieldName) {
        DirectQueryKey queryKey = new DirectQueryKey();
        DatabaseField field = new DatabaseField(fieldName);

        queryKey.setName(queryKeyName);
        queryKey.setField(field);
        getQueryKeys().put(queryKeyName, queryKey);
    }
    
    /**
     * PUBLIC:
     * This protocol can be used to associate multiple tables with foreign key 
     * information. Use this method to associate secondary tables to a 
     * primary table. Specify the source foreign key field to the target
     * primary key field. The join criteria will be generated based on the 
     * fields provided. Unless the customary insert order is specified by the user
     * (using setMultipleTableInsertOrder method)
     * the (automatically generated) table insert order will ensure that 
     * insert into target table happens before insert into the source table
     * (there may be a foreign key constraint in the database that requires
     * target table to be inserted before the source table).
     */
    public void addForeignKeyFieldNameForMultipleTable(String sourceForeignKeyFieldName, String targetPrimaryKeyFieldName) throws DescriptorException {  
        addForeignKeyFieldForMultipleTable(new DatabaseField(sourceForeignKeyFieldName), new DatabaseField(targetPrimaryKeyFieldName));
    }
    
    /**
     * PUBLIC:
     * This protocol can be used to associate multiple tables with foreign key 
     * information. Use this method to associate secondary tables to a 
     * primary table. Specify the source foreign key field to the target
     * primary key field. The join criteria will be generated based on the 
     * fields provided.
     */
    public void addForeignKeyFieldForMultipleTable(DatabaseField sourceForeignKeyField, DatabaseField targetPrimaryKeyField) throws DescriptorException {
        // Make sure that the table is fully qualified.
        if ((!sourceForeignKeyField.hasTableName()) || (!targetPrimaryKeyField.hasTableName())) {
            throw DescriptorException.multipleTablePrimaryKeyMustBeFullyQualified(this);
        }

        setAdditionalTablePrimaryKeyFields(sourceForeignKeyField.getTable(), targetPrimaryKeyField, sourceForeignKeyField);
        Set<DatabaseTable> sourceTables = getMultipleTableForeignKeys().get(targetPrimaryKeyField.getTable());
        if(sourceTables == null) {
            sourceTables = new HashSet<DatabaseTable>(3);
            getMultipleTableForeignKeys().put(targetPrimaryKeyField.getTable(), sourceTables);
        }
        sourceTables.add(sourceForeignKeyField.getTable());
    }

    /**
     * PUBLIC:
     * Add a database mapping to the receiver. Perform any required
     * initialization of both the mapping and the receiving descriptor
     * as a result of adding the new mapping.
     */
    public DatabaseMapping addMapping(DatabaseMapping mapping) {
        // For CR#2646, if the mapping already points to the parent descriptor then leave it.
        if (mapping.getDescriptor() == null) {
            mapping.setDescriptor(this);
        }
        getMappings().add(mapping);
        return mapping;
    }

    protected void validateMappingType(DatabaseMapping mapping) {
        if (!(mapping.isRelationalMapping())) {
            throw DescriptorException.invalidMappingType(mapping);
        }
    }
    
    /**
     * PUBLIC:
     * Specify the primary key field of the descriptors table.
     * This should be called for each field that makes up the primary key of the table.
     * If the descriptor has many tables, this must be the primary key in the first table,
     * if the other tables have the same primary key nothing else is required, otherwise
     * a primary key/foreign key field mapping must be provided for each of the other tables.
     * @see #addForeignKeyFieldNameForMultipleTable(String, String)
     */
    public void addPrimaryKeyFieldName(String fieldName) {
        addPrimaryKeyField(new DatabaseField(fieldName));
    }

    /**
     * ADVANCED:
     * Specify the primary key field of the descriptors table.
     * This should be called for each field that makes up the primary key of the table.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public void addPrimaryKeyField(DatabaseField field) {
        getPrimaryKeyFields().add(field);
    }

    /**
     * PUBLIC:
     * Add a query key to the descriptor. Query keys define Java aliases to database fields.
     */
    public void addQueryKey(QueryKey queryKey) {
        getQueryKeys().put(queryKey.getName(), queryKey);
    }

    /**
     * PUBLIC:
     * Specify the table for the class of objects the receiver describes.
     * This method is used if there is more than one table.
     */
    public void addTable(DatabaseTable table) {
        getTables().add(table);
    }
    
    /**
     * PUBLIC:
     * Specify the table name for the class of objects the receiver describes.
     * If the table has a qualifier it should be specified using the dot notation,
     * (i.e. "userid.employee"). This method is used if there is more than one table.
     */
    public void addTableName(String tableName) {
        addTable(new DatabaseTable(tableName));
    }
    
    /**
     * PUBLIC:
     * Add an unconverted property (to be initialiazed at runtime)
     */
    public void addUnconvertedProperty(String propertyName, String propertyValue, String propertyType) {
        List<String> valuePair = new ArrayList<String>(2);
        valuePair.add(propertyValue);
        valuePair.add(propertyType);
        getUnconvertedProperties().put(propertyName, valuePair);
    }

    /**
     * INTERNAL:
     * Adjust the order of the tables in the multipleTableInsertOrder Vector according to the FK
     * relationship if one (or more) were previously specified. I.e. target of FK relationship should be inserted
     * before source.
     * If the multipleTableInsertOrder has been specified (presumably by the user) then do not change it.
     */
    public void adjustMultipleTableInsertOrder() {
        // Check if a user defined insert order was given.
        if ((getMultipleTableInsertOrder() == null) || getMultipleTableInsertOrder().isEmpty()) {
            createMultipleTableInsertOrder();
        } else {
            verifyMultipleTableInsertOrder();
        }
        toggleAdditionalTablePrimaryKeyFields();
    }

    /**
     * PUBLIC:
     * Used to set the descriptor to always conform in any unit of work query.
     *
     */
    public void alwaysConformResultsInUnitOfWork() {
        setShouldAlwaysConformResultsInUnitOfWork(true);
    }

    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldAlwaysRefreshCache} with an argument of <CODE>true</CODE>:
     * it configures a <CODE>ClassDescriptor</CODE> to always refresh the cache if data is received from the database by any query.<P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For example, by
     * default, when a query for a single object based on its primary key is executed, OracleAS TopLink will first look in the
     * cache for the object. If the object is in the cache, the cached object is returned and data is not refreshed. To avoid
     * cache hits, use the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is not desired. Normally,
     * if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.
     *
     * @see #dontAlwaysRefreshCache
     */
    public void alwaysRefreshCache() {
        setShouldAlwaysRefreshCache(true);
    }

    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldAlwaysRefreshCacheOnRemote} with an argument of <CODE>true</CODE>:
     * it configures a <CODE>ClassDescriptor</CODE> to always remotely refresh the cache if data is received from the database by any
     * query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.<P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For example, by
     * default, when a query for a single object based on its primary key is executed, OracleAS TopLink will first look in the
     * cache for the object. If the object is in the cache, the cached object is returned and data is not refreshed. To avoid
     * cache hits, use the {@link #disableCacheHitsOnRemote} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is not desired.
     * Normally, if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.
     *
     * @see #dontAlwaysRefreshCacheOnRemote
     */
    public void alwaysRefreshCacheOnRemote() {
        setShouldAlwaysRefreshCacheOnRemote(true);
    }

    /**
     * ADVANCED:
     * Call the descriptor amendment method.
     * This is called while loading or creating a descriptor that has an amendment method defined.
     */
    public void applyAmendmentMethod() {
        applyAmendmentMethod(null);
    }

    /**
     * INTERNAL:
     * Call the descriptor amendment method.
     * This is called while loading or creating a descriptor that has an amendment method defined.
     */
    public void applyAmendmentMethod(DescriptorEvent event) {
        if ((getAmendmentClass() == null) || (getAmendmentMethodName() == null)) {
            return;
        }

        Method method = null;
        Class[] argTypes = new Class[1];

        // BUG#2669585
        // Class argument type must be consistent, descriptor, i.e. instance may be a subclass.
        argTypes[0] = ClassDescriptor.class;
        try {
            method = Helper.getDeclaredMethod(getAmendmentClass(), getAmendmentMethodName(), argTypes);
        } catch (Exception ignore) {
            // Return type should now be ClassDescriptor.
            argTypes[0] = ClassDescriptor.class;
            try {
                method = Helper.getDeclaredMethod(getAmendmentClass(), getAmendmentMethodName(), argTypes);
            } catch (Exception exception) {
                throw DescriptorException.invalidAmendmentMethod(getAmendmentClass(), getAmendmentMethodName(), exception, this);
            }
        }

        Object[] args = new Object[1];
        args[0] = this;

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                AccessController.doPrivileged(new PrivilegedMethodInvoker(method, null, args));
            } else {
                PrivilegedAccessHelper.invokeMethod(method, null, args);
            }
        } catch (Exception exception) {
            throw DescriptorException.errorOccuredInAmendmentMethod(getAmendmentClass(), getAmendmentMethodName(), exception, this);
        }
    }

    /**
     * INTERNAL:
     * Used to determine if a foreign key references the primary key.
     */
    public boolean arePrimaryKeyFields(Vector fields) {
        if (!(fields.size() == (getPrimaryKeyFields().size()))) {
            return false;
        }

        for (Enumeration enumFields = fields.elements(); enumFields.hasMoreElements();) {
            DatabaseField field = (DatabaseField)enumFields.nextElement();

            if (!getPrimaryKeyFields().contains(field)) {
                return false;
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * Some attributes have default values defined in Project.
     * If such the value for the attribute hasn't been set then the default value is assigned.
     */
    protected void assignDefaultValues(AbstractSession session) {
        if (this.idValidation == null) {
            this.idValidation = session.getProject().getDefaultIdValidation();
        }
        getCachePolicy().assignDefaultValues(session);
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    public Expression buildBatchCriteriaByPK(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        int size = getPrimaryKeyFields().size();
        if (size > 1) {
            // Support composite keys using nested IN.
            List<Expression> fields = new ArrayList<Expression>(size);
            for (DatabaseField targetForeignKeyField : primaryKeyFields) {
                fields.add(builder.getField(targetForeignKeyField));
            }
            return query.getSession().getPlatform().buildBatchCriteriaForComplexId(builder, fields);
        } else {
            return query.getSession().getPlatform().buildBatchCriteria(builder, builder.getField(primaryKeyFields.get(0)));
        }

    }
    /**
     * INTERNAL:
     * Return a call built from a statement. Subclasses may throw an exception
     * if the statement is not appropriate.
     */
    public DatasourceCall buildCallFromStatement(SQLStatement statement, DatabaseQuery query, AbstractSession session) {
        DatabaseCall call = statement.buildCall(session);
        if (isNativeConnectionRequired()) {
            call.setIsNativeConnectionRequired(true);
        }
        return call;
    }

    /**
     * INTERNAL:
     * Extract the direct values from the specified field value.
     * Return them in a vector.
     */
    public Vector buildDirectValuesFromFieldValue(Object fieldValue) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * A DatabaseField is built from the given field name.
     */

    // * added 9/7/00 by Les Davis
    // * bug fix for null pointer in initialization of mappings in remote session
    public DatabaseField buildField(String fieldName) {
        DatabaseField field = new DatabaseField(fieldName);
        DatabaseTable table;

        if (field.hasTableName()) {
            table = getTable(field.getTableName());
        } else if (getDefaultTable() != null) {
            table = getDefaultTable();
        } else {
            table = getTable(getTableName());
        }

        field.setTable(table);
        return field;
    }

    /**
     * INTERNAL:
     * The table of the field is ensured to be unique from the descriptor's tables.
     * If the field has no table the default table is assigned.
     * This is used only in initialization.
     * Fields are ensured to be unique so if the field has already been built it is returned.
     */
    public DatabaseField buildField(DatabaseField field) {
        return buildField(field, null);
    }
    
    public DatabaseField buildField(DatabaseField field, DatabaseTable relationTable) {
        DatabaseField builtField = getObjectBuilder().getFieldsMap().get(field);
        if (builtField == null) {
            builtField = field;
            DatabaseTable table;
            if (relationTable != null && field.hasTableName() && field.getTableName().equals(relationTable.getName())){
                table = relationTable;
            } else if (relationTable != null && !field.hasTableName()) {
                table = relationTable;
            } else if (field.hasTableName()) {
                table = getTable(field.getTableName());
            } else {
                table = getDefaultTable();
            }
            field.setTable(table);
            getObjectBuilder().getFieldsMap().put(builtField, builtField);
        }
        return builtField;
    }

    /**
     * INTERNAL:
     * Build the appropriate field value for the specified
     * set of direct values.
     */
    public Object buildFieldValueFromDirectValues(Vector directValues, String elementDataTypeName, AbstractSession session) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * Build and return the appropriate field value for the specified
     * set of foreign keys (i.e. each row has the fields that
     * make up a foreign key).
     */
    public Object buildFieldValueFromForeignKeys(Vector foreignKeys, String referenceDataTypeName, AbstractSession session) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * Build and return the field value from the specified nested database row.
     */
    public Object buildFieldValueFromNestedRow(AbstractRecord nestedRow, AbstractSession session) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * Build and return the appropriate field value for the specified
     * set of nested rows.
     */
    public Object buildFieldValueFromNestedRows(Vector nestedRows, String structureName, AbstractSession session) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * Build and return the nested database row from the specified field value.
     */
    public AbstractRecord buildNestedRowFromFieldValue(Object fieldValue) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     * INTERNAL:
     * Build and return the nested rows from the specified field value.
     */
    public Vector buildNestedRowsFromFieldValue(Object fieldValue, AbstractSession session) throws DatabaseException {
        throw DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(this);
    }

    /**
     *  To check that tables and fields are present in database
     */
    protected void checkDatabase(AbstractSession session) {
        if (session.getIntegrityChecker().shouldCheckDatabase()) {
            for (Iterator iterator = getTables().iterator(); iterator.hasNext();) {
                DatabaseTable table = (DatabaseTable)iterator.next();
                if (session.getIntegrityChecker().checkTable(table, session)) {
                    // To load the fields of database into a vector
                    List databaseFields = new ArrayList();
                    List result = session.getAccessor().getColumnInfo(null, null, table.getName(), null, session);
                    // Table name may need to be lowercase.
                    if (result.isEmpty() && session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                        result = session.getAccessor().getColumnInfo(null, null, table.getName().toLowerCase(), null, session);
                    }
                    for (Iterator resultIterator = result.iterator(); resultIterator.hasNext();) {
                        AbstractRecord row = (AbstractRecord)resultIterator.next();
                        if (session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                            databaseFields.add(((String)row.get("COLUMN_NAME")).toUpperCase());
                        } else {
                            databaseFields.add(row.get("COLUMN_NAME"));
                        }
                    }

                    // To check that the fields of descriptor are present in the database.
                    for (DatabaseField field : getFields()) {
                        if (field.getTable().equals(table) && (!databaseFields.contains(field.getName()))) {
                            session.getIntegrityChecker().handleError(DescriptorException.fieldIsNotPresentInDatabase(this, table.getName(), field.getName()));
                        }
                    }
                } else {
                    session.getIntegrityChecker().handleError(DescriptorException.tableIsNotPresentInDatabase(this));
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Verify that an aggregate descriptor's inheritance tree
     * is full of aggregate descriptors.
     */
    public void checkInheritanceTreeAggregateSettings(AbstractSession session, AggregateMapping mapping) throws DescriptorException {
        if (!this.hasInheritance()) {
            return;
        }

        if (this.isChildDescriptor()) {
            Class parentClass = this.getInheritancePolicy().getParentClass();
            if (parentClass == this.getJavaClass()) {
                throw DescriptorException.parentClassIsSelf(this);
            }

            // recurse up the inheritance tree to the root descriptor
            session.getDescriptor(parentClass).checkInheritanceTreeAggregateSettings(session, mapping);
        } else {
            // we have a root descriptor, now verify it and all its children, grandchildren, etc.
            this.checkInheritanceTreeAggregateSettingsForChildren(session, mapping);
        }
    }

    /**
     * Verify that an aggregate descriptor's inheritance tree
     * is full of aggregate descriptors, cont.
     */
    private void checkInheritanceTreeAggregateSettingsForChildren(AbstractSession session, AggregateMapping mapping) throws DescriptorException {
        if (!this.isAggregateDescriptor()) {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregate(this.getJavaClass().getName(), mapping));
        }
        for (ClassDescriptor childDescriptor : this.getInheritancePolicy().getChildDescriptors()) {
            // recurse down the inheritance tree to its leaves
            childDescriptor.checkInheritanceTreeAggregateSettingsForChildren(session, mapping);
        }
    }

    /**
     * INTERNAL:
     * Create multiple table insert order.
     * If its a child descriptor then insert order starts
     * with the same insert order as in the parent.
     * Non-inherited tables ordered to adhere to 
     * multipleTableForeignKeys:
     * the target table (the key in multipleTableForeignKeys map)
     * should stand in insert order before any of the source tables
     * (members of the corresponding value in multipleTableForeignKeys).
     */
    protected void createMultipleTableInsertOrder() {
        int nParentTables = 0;
        if (isChildDescriptor()) {
            nParentTables = getInheritancePolicy().getParentDescriptor().getTables().size();
            setMultipleTableInsertOrder(new ArrayList(getInheritancePolicy().getParentDescriptor().getMultipleTableInsertOrder()));
            
            if(nParentTables == getTables().size()) {
                // all the tables mapped by the parent - nothing to do.
                return;
            }
        }

        if(getMultipleTableForeignKeys().isEmpty()) {
            if(nParentTables == 0) {
                // no multipleTableForeignKeys specified - keep getTables() order.
                setMultipleTableInsertOrder((Vector)getTables().clone());
            } else {
                // insert order for parent-defined tables has been already copied from parent descriptor,
                // add the remaining tables keeping the same order as in getTables()
                for(int k = nParentTables; k < getTables().size(); k++) {
                    getMultipleTableInsertOrder().add(getTables().get(k));
                }
            }
            return;
        }
        
        verifyMultipleTablesForeignKeysTables();
        
        // tableComparison[i][j] indicates the order between i and j tables:
        // -1 i table before j table;
        // +1 i table after j table;
        //  0 - not defined (could be either before or after)
        int[][] tableComparison = createTableComparison(getTables(), nParentTables);

        // Now create insert order of the tables:
        // getTables.get(i) table should be 
        //  before getTable.get(j) in insert order if tableComparison[i][j]==-1;
        //  after getTable.get(j) in insert order if tableComparison[i][j]== 1;
        //  doesn't matter if tableComparison[i][j]== 0.
        createMultipleTableInsertOrderFromComparison(tableComparison, nParentTables);
    }

    /**
     * INTERNAL:
     * Verify multiple table insert order provided by the user.
     * If its a child descriptor then insert order starts
     * with the same insert order as in the parent.
     * Non-inherited tables ordered to adhere to 
     * multipleTableForeignKeys:
     * the target table (the key in multipleTableForeignKeys map)
     * should stand in insert order before any of the source tables
     * (members of the corresponding value in multipleTableForeignKeys).
     */
    protected void verifyMultipleTableInsertOrder() {
        int nParentTables = 0;
        if (isChildDescriptor()) {
            nParentTables = getInheritancePolicy().getParentDescriptor().getTables().size();

            if(nParentTables + getMultipleTableInsertOrder().size() == getTables().size()) {
                // the user specified insert order only for the tables directly mapped by the descriptor,
                // the inherited tables order must be the same as in parent descriptor
                List<DatabaseTable> childMultipleTableInsertOrder = getMultipleTableInsertOrder(); 
                setMultipleTableInsertOrder(new ArrayList(getInheritancePolicy().getParentDescriptor().getMultipleTableInsertOrder()));
                getMultipleTableInsertOrder().addAll(childMultipleTableInsertOrder);
            }
            
        }
        
        if (getMultipleTableInsertOrder().size() != getTables().size()) {
            throw DescriptorException.multipleTableInsertOrderMismatch(this);
        }

        if(nParentTables == getTables().size()) {
            // all the tables mapped by the parent - nothing to do.
            return;
        }

        if(getMultipleTableForeignKeys().isEmpty()) {
            // nothing to do
            return;
        }
        
        verifyMultipleTablesForeignKeysTables();
        
        // tableComparison[i][j] indicates the order between i and j tables:
        // -1 i table before j table;
        // +1 i table after j table;
        //  0 - not defined (could be either before or after)
        int[][] tableComparison = createTableComparison(getMultipleTableInsertOrder(), nParentTables);

        for(int i = nParentTables; i < getMultipleTableInsertOrder().size(); i++) {
            for(int j = i + 1; j < getTables().size(); j++) {
                if(tableComparison[i - nParentTables][j - nParentTables] > 0) {
                    throw DescriptorException.insertOrderConflictsWithMultipleTableForeignKeys(this, getMultipleTableInsertOrder().get(i), getMultipleTableInsertOrder().get(j));
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Verify that the tables specified in multipleTablesForeignKeysTables are valid.
     */
    protected void verifyMultipleTablesForeignKeysTables() {
        Iterator<Map.Entry<DatabaseTable, Set<DatabaseTable>>> itTargetTables = getMultipleTableForeignKeys().entrySet().iterator();
        while(itTargetTables.hasNext()) {
            Map.Entry<DatabaseTable, Set<DatabaseTable>> entry = itTargetTables.next();
            DatabaseTable targetTable = entry.getKey();
            if (getTables().indexOf(targetTable) == -1) {
                throw DescriptorException.illegalTableNameInMultipleTableForeignKeyField(this, targetTable);
            }
            Iterator<DatabaseTable> itSourceTables = entry.getValue().iterator();
            while(itSourceTables.hasNext()) {
                DatabaseTable sourceTable = itSourceTables.next();
                if (getTables().indexOf(sourceTable) == -1) {
                    throw DescriptorException.illegalTableNameInMultipleTableForeignKeyField(this, targetTable);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * This helper method creates a matrix that contains insertion order comparison for the tables.
     * Comparison is done for indexes from nStart to tables.size()-1.
     */
    protected int[][] createTableComparison(List<DatabaseTable> tables, int nStart) {
        int nTables = tables.size();
        // tableComparison[i][j] indicates the order between i and j tables:
        // -1 i table before j table;
        // +1 i table after j table;
        //  0 - not defined (could be either before or after)
        int[][] tableComparison = new int[nTables - nStart][nTables - nStart];

        Iterator<Map.Entry<DatabaseTable, Set<DatabaseTable>>> itTargetTables = getMultipleTableForeignKeys().entrySet().iterator();
        // loop through all pairs of target and source tables and insert either +1 or -1 into tableComparison for each pair.  
        while(itTargetTables.hasNext()) {
            Map.Entry<DatabaseTable, Set<DatabaseTable>> entry = itTargetTables.next();
            DatabaseTable targetTable = entry.getKey();
            int targetIndex = tables.indexOf(targetTable) - nStart;
            if(targetIndex >= 0) {
                Set<DatabaseTable> sourceTables = entry.getValue(); 
                Iterator<DatabaseTable> itSourceTables = sourceTables.iterator();
                while(itSourceTables.hasNext()) {
                    DatabaseTable sourceTable = itSourceTables.next();
                    int sourceIndex = tables.indexOf(sourceTable) - nStart;
                    if(sourceIndex >= 0) {
                        // targetTable should be before sourceTable: tableComparison[sourceIndex, targetIndex] = 1; tableComparison[targetIndex, sourceIndex] =-1.
                        if(tableComparison[targetIndex][sourceIndex] == 1) {
                            throw DescriptorException.insertOrderCyclicalDependencyBetweenTwoTables(this, sourceTable, targetTable);
                        } else {
                            tableComparison[targetIndex][sourceIndex] =-1;
                            tableComparison[sourceIndex][targetIndex] = 1;
                        }
                    } else {
                        throw DescriptorException.insertOrderChildBeforeParent(this, sourceTable, targetTable);
                    }
                }
            }
        }
        return tableComparison;
    }
    
    /**
     * INTERNAL:
     * This helper method creates multipleTableInsertOrderFromComparison using comparison matrix 
     * created by createTableComparison(getTables()) method call. 
     */
    protected void createMultipleTableInsertOrderFromComparison(int[][] tableComparison, int nStart) {
        int nTables = getTables().size();
        int[] tableOrder = new int[nTables - nStart];
        boolean bOk = createTableOrder(0, nTables - nStart, tableOrder, tableComparison);
        if(bOk) {
            if(nStart == 0) {
                setMultipleTableInsertOrder(NonSynchronizedVector.newInstance(nTables));
            }
            for(int k=0; k < nTables - nStart; k++) {
                getMultipleTableInsertOrder().add(getTables().get(tableOrder[k] + nStart));
            }
        } else {
            throw DescriptorException.insertOrderCyclicalDependencyBetweenThreeOrMoreTables(this);
        }
    }
    
    /**
     * INTERNAL:
     * This helper method recursively puts indexes from 0 to nTables-1 into tableOrder according to tableComparison 2 dim array.
     * k is index in tableOrder that currently the method is working on - the method should be called with k = 0.
     */
    protected boolean createTableOrder(int k, int nTables, int[] tableOrder, int[][] tableComparison) {
        if(k == nTables) {
            return true;
        }

        // array of indexes not yet ordered
        int[] iAvailable = new int[nTables-k];
        int l = 0;
        for(int i=0; i < nTables; i++) {
            boolean isUsed = false;
            for(int j=0; j<k && !isUsed; j++) {
                if(i == tableOrder[j]) {
                    isUsed = true;
                }
            }
            if(!isUsed) {
                iAvailable[l] = i;
                l++;
            }
        }

        boolean bOk = false;
        for(int i=0; (i < nTables-k)  && !bOk; i++) {
            boolean isSmallest = true;
            for(int j=0; (j < nTables-k) && isSmallest; j++) {
                if(i != j) {
                    if(tableComparison[iAvailable[i]][iAvailable[j]] > 0) {
                        isSmallest = false;
                    }
                }
            }
            if(isSmallest) {
                // iAvailable[i] is less or equal according to tableComparison to all other remaining indexes - let's try to use it as tableOrder[k]
                tableOrder[k] = iAvailable[i];
                // now try to fill out the last remaining n - k - 1 elements of tableOrder
                bOk = createTableOrder(k + 1, nTables, tableOrder, tableComparison);
            }
        }
        return bOk;
    }
    
    /**
     * INTERNAL:
     * Clones the descriptor
     */
    public Object clone() {
        ClassDescriptor clonedDescriptor = null;

        // clones itself
        try {
            clonedDescriptor = (ClassDescriptor)super.clone();
        } catch (Exception exception) {
            ;
        }
        

        Vector mappingsVector = NonSynchronizedVector.newInstance();

        // All the mappings
        for (Enumeration mappingsEnum = getMappings().elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping;

            mapping = (DatabaseMapping)((DatabaseMapping)mappingsEnum.nextElement()).clone();
            mapping.setDescriptor(clonedDescriptor);
            mappingsVector.addElement(mapping);
        }
        clonedDescriptor.setMappings(mappingsVector);
        
        Map queryKeys = new HashMap(getQueryKeys().size() + 2);

        // All the query keys
        for (QueryKey queryKey : getQueryKeys().values()) {
            queryKey = (QueryKey)queryKey.clone();
            queryKey.setDescriptor(clonedDescriptor);
            queryKeys.put(queryKey.getName(), queryKey);
        }
        clonedDescriptor.setQueryKeys(queryKeys);

        // PrimaryKeyFields
        List primaryKeyVector = new ArrayList(getPrimaryKeyFields().size());
        List primaryKeyFields = getPrimaryKeyFields();
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField primaryKey = ((DatabaseField)primaryKeyFields.get(index)).clone();
            primaryKeyVector.add(primaryKey);
        }
        clonedDescriptor.setPrimaryKeyFields(primaryKeyVector);

        // fields.
        clonedDescriptor.setFields(NonSynchronizedVector.newInstance());

        // The inheritance policy
        if (clonedDescriptor.hasInheritance()) {
            clonedDescriptor.setInheritancePolicy((InheritancePolicy)getInheritancePolicy().clone());
            clonedDescriptor.getInheritancePolicy().setDescriptor(clonedDescriptor);
        }

        if (clonedDescriptor.hasSerializedObjectPolicy()) {
            clonedDescriptor.setSerializedObjectPolicy(getSerializedObjectPolicy().clone());
        }
        
        // The returning policy
        if (clonedDescriptor.hasReturningPolicy()) {
            clonedDescriptor.setReturningPolicy((ReturningPolicy)getReturningPolicy().clone());
            clonedDescriptor.getReturningPolicy().setDescriptor(clonedDescriptor);
        }

        // The Object builder
        clonedDescriptor.setObjectBuilder((ObjectBuilder)getObjectBuilder().clone());
        clonedDescriptor.getObjectBuilder().setDescriptor(clonedDescriptor);

        clonedDescriptor.setEventManager((DescriptorEventManager)getEventManager().clone());
        clonedDescriptor.getEventManager().setDescriptor(clonedDescriptor);

        // The Query manager
        clonedDescriptor.setQueryManager((DescriptorQueryManager)getQueryManager().clone());
        clonedDescriptor.getQueryManager().setDescriptor(clonedDescriptor);

        //fetch group
        if (hasFetchGroupManager()) {
            clonedDescriptor.setFetchGroupManager((FetchGroupManager)getFetchGroupManager().clone());
        }

        if (this.cachePolicy != null) {
            clonedDescriptor.setCachePolicy(this.cachePolicy.clone());
        }

        // Bug 3037701 - clone several more elements
        if (this.instantiationPolicy != null) {
            clonedDescriptor.setInstantiationPolicy((InstantiationPolicy)getInstantiationPolicy().clone());
        }
        if (this.copyPolicy != null) {
            clonedDescriptor.setCopyPolicy((CopyPolicy)getCopyPolicy().clone());
        }
        if (getOptimisticLockingPolicy() != null) {
            clonedDescriptor.setOptimisticLockingPolicy((OptimisticLockingPolicy)getOptimisticLockingPolicy().clone());
        }
        //bug 5171059 clone change tracking policies as well
        clonedDescriptor.setObjectChangePolicy(this.getObjectChangePolicyInternal());
        
        // Clone the tables
        Vector<DatabaseTable> tables = NonSynchronizedVector.newInstance(3);
        for (DatabaseTable table : getTables()) {
            tables.add(table.clone());
        }
        clonedDescriptor.setTables(tables);
        
        // Clone the default table
        if (getDefaultTable() != null) {
            clonedDescriptor.setDefaultTable(getDefaultTable().clone());
        }
        
        // Clone the CMPPolicy
        if (getCMPPolicy() != null) {
            clonedDescriptor.setCMPPolicy(getCMPPolicy().clone());
            clonedDescriptor.getCMPPolicy().setDescriptor(clonedDescriptor);
        }
        
        // Clone the sequence number field.
        if (getSequenceNumberField() != null) {
            clonedDescriptor.setSequenceNumberField(getSequenceNumberField().clone());
        }

        // Clone the multitenant policy.
        if (hasMultitenantPolicy()) {
            clonedDescriptor.setMultitenantPolicy(getMultitenantPolicy().clone(clonedDescriptor));
        }
        
        return clonedDescriptor;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this Descriptor to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Class redirectorClass = null;

        if (getJavaClassName() != null){
            Class descriptorClass = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        descriptorClass = AccessController.doPrivileged(new PrivilegedClassForName(getJavaClassName(), true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(getJavaClassName(), exception.getException());
                    }
                } else {
                    descriptorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getJavaClassName(), true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(getJavaClassName(), exc);
            }
            setJavaClass(descriptorClass);
        }

        if (getAmendmentClassName() != null) {
            Class amendmentClass = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        amendmentClass = AccessController.doPrivileged(new PrivilegedClassForName(getAmendmentClassName(), true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(getAmendmentClassName(), exception.getException());
                    }
                } else {
                    amendmentClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getAmendmentClassName(), true, classLoader);
                }            
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(getAmendmentClassName(), exc);
            }
            setAmendmentClass(amendmentClass);
        }

        if (copyPolicy == null && getCopyPolicyClassName() != null){
            Class copyPolicyClass = null;
            CopyPolicy newCopyPolicy = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        copyPolicyClass = AccessController.doPrivileged(new PrivilegedClassForName(getCopyPolicyClassName(), true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(getCopyPolicyClassName(), exception.getException());
                    }
                    try {
                        newCopyPolicy = (CopyPolicy)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(copyPolicyClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(getCopyPolicyClassName(), exception.getException());
                    }
                } else {
                    copyPolicyClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getCopyPolicyClassName(), true, classLoader);
                    newCopyPolicy = (CopyPolicy)org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(copyPolicyClass);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(getCopyPolicyClassName(), exc);
            } catch (IllegalAccessException ex){
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(getCopyPolicyClassName(), ex);
            } catch (InstantiationException e){
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(getCopyPolicyClassName(), e);   
            }
            setCopyPolicy(newCopyPolicy);
        }
        
        if (this.serializedObjectPolicy != null && this.serializedObjectPolicy instanceof SerializedObjectPolicyWrapper) {
            String serializedObjectPolicyClassName = ((SerializedObjectPolicyWrapper)this.serializedObjectPolicy).getSerializedObjectPolicyClassName();
            Class serializedObjectPolicyClass = null;
            SerializedObjectPolicy newSerializedObjectPolicy = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        serializedObjectPolicyClass = AccessController.doPrivileged(new PrivilegedClassForName(serializedObjectPolicyClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(serializedObjectPolicyClassName, exception.getException());
                    }
                    try {
                        newSerializedObjectPolicy = (SerializedObjectPolicy)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(serializedObjectPolicyClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(serializedObjectPolicyClassName, exception.getException());
                    }
                } else {
                    serializedObjectPolicyClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(serializedObjectPolicyClassName, true, classLoader);
                    newSerializedObjectPolicy = (SerializedObjectPolicy)org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(serializedObjectPolicyClass);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(serializedObjectPolicyClassName, exc);
            } catch (IllegalAccessException ex){
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(serializedObjectPolicyClassName, ex);
            } catch (InstantiationException e){
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(serializedObjectPolicyClassName, e);   
            }
            newSerializedObjectPolicy.setField(this.serializedObjectPolicy.getField());
            setSerializedObjectPolicy(newSerializedObjectPolicy);
        }

        //Create and set default QueryRedirector instances
        if (this.defaultQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultQueryRedirectorClassName, true, classLoader);
                    setDefaultQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultQueryRedirectorClassName, e);
            }
        }

        if (this.defaultReadObjectQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultReadObjectQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadObjectQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultReadObjectQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadObjectQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultReadObjectQueryRedirectorClassName, true, classLoader);
                    setDefaultReadObjectQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadObjectQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadObjectQueryRedirectorClassName, e);
            }
        }
        if (this.defaultReadAllQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultReadAllQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadAllQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultReadAllQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadAllQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultReadAllQueryRedirectorClassName, true, classLoader);
                    setDefaultReadAllQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadAllQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReadAllQueryRedirectorClassName, e);
            }
        }
        if (this.defaultReportQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultReportQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReportQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultReportQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReportQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultReportQueryRedirectorClassName, true, classLoader);
                    setDefaultReportQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReportQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultReportQueryRedirectorClassName, e);
            }
        }
        if (this.defaultInsertObjectQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultInsertObjectQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultInsertObjectQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultInsertObjectQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultInsertObjectQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultInsertObjectQueryRedirectorClassName, true, classLoader);
                    setDefaultInsertObjectQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultInsertObjectQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultInsertObjectQueryRedirectorClassName, e);
            }
        }
        if (this.defaultUpdateObjectQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultUpdateObjectQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultUpdateObjectQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultUpdateObjectQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultUpdateObjectQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultUpdateObjectQueryRedirectorClassName, true, classLoader);
                    setDefaultUpdateObjectQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultUpdateObjectQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultUpdateObjectQueryRedirectorClassName, e);
            }
        }
        if (this.defaultDeleteObjectQueryRedirectorClassName != null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        redirectorClass = AccessController.doPrivileged(new PrivilegedClassForName(defaultDeleteObjectQueryRedirectorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultDeleteObjectQueryRedirectorClassName, exception.getException());
                    }
                    try {
                        setDefaultDeleteObjectQueryRedirector((QueryRedirector) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(redirectorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(defaultDeleteObjectQueryRedirectorClassName, exception.getException());
                    }
                } else {
                    redirectorClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(defaultDeleteObjectQueryRedirectorClassName, true, classLoader);
                    setDefaultDeleteObjectQueryRedirector((QueryRedirector) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(redirectorClass));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultDeleteObjectQueryRedirectorClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(defaultDeleteObjectQueryRedirectorClassName, e);
            }
        }
        Iterator mappings = getMappings().iterator();
        while (mappings.hasNext()){
            ((DatabaseMapping)mappings.next()).convertClassNamesToClasses(classLoader);
        }
        if (this.inheritancePolicy != null){
            this.inheritancePolicy.convertClassNamesToClasses(classLoader);
        }
        if (this.interfacePolicy != null){
            this.interfacePolicy.convertClassNamesToClasses(classLoader);
        }
        if (this.instantiationPolicy != null){
            this.instantiationPolicy.convertClassNamesToClasses(classLoader);
        }
        if (hasCMPPolicy()) {
            getCMPPolicy().convertClassNamesToClasses(classLoader);
        }
        if(this.queryManager != null) {
            this.queryManager.convertClassNamesToClasses(classLoader);
        }
        if(this.cachePolicy != null) {
            this.cachePolicy.convertClassNamesToClasses(classLoader);
        }
        
        if (hasUnconvertedProperties()) {
            for (String propertyName : getUnconvertedProperties().keySet()) {
                List<String> valuePair = getUnconvertedProperties().get(propertyName);
                String value = valuePair.get(0);
                String valueTypeName = valuePair.get(1);
                Class valueType = String.class;
                
                if (valueTypeName != null) {
                    // Have to initialize the valueType now
                    try {
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                            try {
                                valueType = AccessController.doPrivileged(new PrivilegedClassForName(valueTypeName, true, classLoader));
                            } catch (PrivilegedActionException exception) {
                                throw ValidationException.classNotFoundWhileConvertingClassNames(valueTypeName, exception.getException());
                            }
                        } else {
                            valueType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(valueTypeName, true, classLoader);
                        }
                    } catch (ClassNotFoundException exc){
                        throw ValidationException.classNotFoundWhileConvertingClassNames(valueTypeName, exc);
                    }
                }

                // Add the converted property. If the value type is the same
                // as the source (value) type, no conversion is made.
                getProperties().put(propertyName, ConversionManager.getDefaultManager().convertObject(value, valueType));
            }
        }
    }

    /**
     * PUBLIC:
     * Create a copy policy of the type passed in as a string.
     */
    public void createCopyPolicy(String policyType) {
        if (policyType.equals("clone")) {
            useCloneCopyPolicy();
            return;
        }
        if (policyType.equals("constructor")) {
            useInstantiationCopyPolicy();
            return;
        }
    }

    /**
     * PUBLIC:
     * Create a instantiation policy of the type passed in as a string.
     */
    public void createInstantiationPolicy(String policyType) {
        if (policyType.equals("static method")) {
            //do nothing for now
            return;
        }
        if (policyType.equals("constructor")) {
            useDefaultConstructorInstantiationPolicy();
            return;
        }
        if (policyType.equals("factory")) {
            //do nothing for now
            return;
        }
    }

    /**
     * PUBLIC:
     * Sets the descriptor to be an aggregate.
     * An aggregate descriptor is contained within another descriptor's table.
     * Aggregate descriptors are insert/updated/deleted with their owner and cannot exist without their owner as they share the same row.
     * Aggregates are not cached (they are cached as part of their owner) and cannot be read/written/deleted/registered.
     * All aggregate descriptors must call this.
     */
    public void descriptorIsAggregate() {
        setDescriptorType(AGGREGATE);
    }

    /**
     * PUBLIC:
     * Sets the descriptor to be part of an aggregate collection.
     * An aggregate collection descriptor stored in a separate table but some of the fields (the primary key) comes from its owner.
     * Aggregate collection descriptors are insert/updated/deleted with their owner and cannot exist without their owner as they share the primary key.
     * Aggregate collections are not cached (they are cached as part of their owner) and cannot be read/written/deleted/registered.
     * All aggregate collection descriptors must call this.
     */
    public void descriptorIsAggregateCollection() {
        setDescriptorType(AGGREGATE_COLLECTION);
    }

    /**
     * PUBLIC:
     * Sets the descriptor to be for an interface.
     * An interface descriptor allows for other classes to reference an interface or one of several other classes.
     * The implementor classes can be completely unrelated in term of the database stored in distinct tables.
     * Queries can also be done for the interface which will query each of the implementor classes.
     * An interface descriptor cannot define any mappings as an interface is just API and not state,
     * a interface descriptor should define the common query key of its implementors to allow querying.
     * An interface descriptor also does not define a primary key or table or other settings.
     * If an interface only has a single implementor (i.e. a classes public interface or remote) then an interface
     * descriptor should not be defined for it and relationships should be to the implementor class not the interface,
     * in this case the implementor class can add the interface through its interface policy to map queries on the interface to it.
     */
    public void descriptorIsForInterface() {
        setDescriptorType(INTERFACE);
    }

    /**
     * PUBLIC:
     * Sets the descriptor to be normal.
     * This is the default and means the descriptor is not aggregate or for an interface.
     */
    public void descriptorIsNormal() {
        setDescriptorType(NORMAL);
    }

    /**
     * PUBLIC:
     * Allow for cache hits on primary key read object queries to be disabled.
     * This can be used with {@link #alwaysRefreshCache} or {@link #alwaysRefreshCacheOnRemote} to ensure queries always go to the database.
     */
    public void disableCacheHits() {
        setShouldDisableCacheHits(true);
    }

    /**
     * PUBLIC:
     * Allow for remote session cache hits on primary key read object queries to be disabled.
     * This can be used with alwaysRefreshCacheOnRemote() to ensure queries always go to the server session cache.
     *
     * @see #alwaysRefreshCacheOnRemote()
     */
    public void disableCacheHitsOnRemote() {
        setShouldDisableCacheHitsOnRemote(true);
    }

    /**
     * PUBLIC:
     * The descriptor is defined to not conform the results in unit of work in read query. Default.
     *
     */
    public void dontAlwaysConformResultsInUnitOfWork() {
        setShouldAlwaysConformResultsInUnitOfWork(false);
    }

    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldAlwaysRefreshCache} with an argument of <CODE>false</CODE>:
     * it ensures that a <CODE>ClassDescriptor</CODE> is not configured to always refresh the cache if data is received from the database by any query.
     *
     * @see #alwaysRefreshCache
     */
    public void dontAlwaysRefreshCache() {
        setShouldAlwaysRefreshCache(false);
    }

    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldAlwaysRefreshCacheOnRemote} with an argument of <CODE>false</CODE>:
     * it ensures that a <CODE>ClassDescriptor</CODE> is not configured to always remotely refresh the cache if data is received from the
     * database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     *
     * @see #alwaysRefreshCacheOnRemote
     */
    public void dontAlwaysRefreshCacheOnRemote() {
        setShouldAlwaysRefreshCacheOnRemote(false);
    }

    /**
     * PUBLIC:
     * Allow for cache hits on primary key read object queries.
     *
     * @see #disableCacheHits()
     */
    public void dontDisableCacheHits() {
        setShouldDisableCacheHits(false);
    }

    /**
     * PUBLIC:
     * Allow for remote session cache hits on primary key read object queries.
     *
     * @see #disableCacheHitsOnRemote()
     */
    public void dontDisableCacheHitsOnRemote() {
        setShouldDisableCacheHitsOnRemote(false);
    }

    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldOnlyRefreshCacheIfNewerVersion} with an argument of <CODE>false</CODE>:
     * it ensures that a <CODE>ClassDescriptor</CODE> is not configured to only refresh the cache if the data received from the database by
     * a query is newer than the data in the cache (as determined by the optimistic locking field).
     *
     * @see #onlyRefreshCacheIfNewerVersion
     */
    public void dontOnlyRefreshCacheIfNewerVersion() {
        setShouldOnlyRefreshCacheIfNewerVersion(false);
    }

    /**
     * INTERNAL:
     * The first table in the tables is always treated as default.
     */
    protected DatabaseTable extractDefaultTable() {
        if (getTables().isEmpty()) {
            if (isChildDescriptor()) {
                return getInheritancePolicy().getParentDescriptor().extractDefaultTable();
            } else {
                return null;
            }
        }

        return getTables().get(0);
    }

    /**
     * INTERNAL:
     * additionalAggregateCollectionKeyFields are used by aggregate descriptors to hold additional fields needed when they are stored in an AggregatateCollection
     * These fields are generally foreign key fields that are required in addition to the fields in the descriptor's 
     *  mappings to uniquely identify the Aggregate
     * @return
     */
    public List<DatabaseField> getAdditionalAggregateCollectionKeyFields(){
        if (additionalAggregateCollectionKeyFields == null){
            additionalAggregateCollectionKeyFields = new ArrayList<DatabaseField>();
        }
        return additionalAggregateCollectionKeyFields;
    }
    
    /**
     * INTERNAL:
     * This is used to map the primary key field names in a multiple table descriptor.
     */
    public Map<DatabaseTable, Map<DatabaseField, DatabaseField>> getAdditionalTablePrimaryKeyFields() {
        if (additionalTablePrimaryKeyFields == null) {
            additionalTablePrimaryKeyFields = new HashMap(5);
        }
        return additionalTablePrimaryKeyFields;
    }

    /**
     * INTERNAL:
     * Return a list of fields that are written by map keys
     * Used to determine if there is a multiple writable mappings issue
     * @return
     */
    public List<DatabaseField> getAdditionalWritableMapKeyFields() {
        if (additionalWritableMapKeyFields == null) {
            additionalWritableMapKeyFields = new ArrayList(2);
        }
        return additionalWritableMapKeyFields;
    }

    /**
     * PUBLIC:
     * Get the alias
     */
    public String getAlias() {

        /* CR3310: Steven Vo
         *   Default alias to the Java class name if the alias is not set
         */
        if ((alias == null) && (getJavaClassName() != null)) {
            alias = org.eclipse.persistence.internal.helper.Helper.getShortClassName(getJavaClassName());
        }
        return alias;
    }

    /**
     * INTERNAL:
     * Return all the fields which include all child class fields. 
     * By default it is initialized to the fields for the current descriptor.
     */
    public Vector<DatabaseField> getAllFields() {
        return allFields;
    }

    /**
     * INTERNAL:
     * Return all selection fields which include all child class fields. 
     * By default it is initialized to selection fields for the current descriptor.
     */
    public List<DatabaseField> getAllSelectionFields() {
        return allSelectionFields;
    }

    /**
     * INTERNAL:
     * Return all selection fields which include all child class fields. 
     * By default it is initialized to selection fields for the current descriptor.
     */
    public List<DatabaseField> getAllSelectionFields(ObjectLevelReadQuery query) {
        if (hasSerializedObjectPolicy() && query.shouldUseSerializedObjectPolicy()) {
            return this.serializedObjectPolicy.getAllSelectionFields();
        } else {
            return allSelectionFields;
        }
    }

    /**
     * PUBLIC:
     * Return the amendment class.
     * The amendment method will be called on the class before initialization to allow for it to initialize the descriptor.
     * The method must be a public static method on the class.
     */
    public Class getAmendmentClass() {
        return amendmentClass;
    }

    /**
     * INTERNAL:
     * Return amendment class name, used by the MW.
     */
    public String getAmendmentClassName() {
        if ((amendmentClassName == null) && (amendmentClass != null)) {
            amendmentClassName = amendmentClass.getName();
        }
        return amendmentClassName;
    }

    /**
     * PUBLIC:
     * Return the amendment method.
     * This will be called on the amendment class before initialization to allow for it to initialize the descriptor.
     * The method must be a public static method on the class.
     */
    public String getAmendmentMethodName() {
        return amendmentMethodName;
    }

    /**
     * @return the accessorTree
     */
    public List<AttributeAccessor> getAccessorTree() {
        return accessorTree;
    }

    
    /**
     * PUBLIC:
     * Return this objects ObjectChangePolicy.
     */
    public ObjectChangePolicy getObjectChangePolicy() {
        // part of fix for 4410581: project xml must save change policy
        // if no change-policy XML element, field is null: lazy-init to default
        if (changePolicy == null) {
            changePolicy = new DeferredChangeDetectionPolicy();
        }
        return changePolicy;
    }
    
    /**
     * INTERNAL:
     * Return this objects ObjectChangePolicy and do not lazy initialize
     */
    public ObjectChangePolicy getObjectChangePolicyInternal() {
        return changePolicy;
    }

    /**
     * PUBLIC:
     * Return this descriptor's HistoryPolicy.
     */
    public HistoryPolicy getHistoryPolicy() {
        return historyPolicy;
    }
    
    /**
     * PUBLIC:
     * Return the descriptor's partitioning policy.
     */
    public PartitioningPolicy getPartitioningPolicy() {
        return partitioningPolicy;
    }
    
    /**
     * PUBLIC:
     * Set the descriptor's partitioning policy.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    public void setPartitioningPolicy(PartitioningPolicy partitioningPolicy) {
        this.partitioningPolicy = partitioningPolicy;
    }
    
    /**
     * PUBLIC:
     * Return the name of the descriptor's partitioning policy.
     * A PartitioningPolicy with the same name must be defined on the Project.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    public String getPartitioningPolicyName() {
        return partitioningPolicyName;
    }
    
    /**
     * PUBLIC:
     * Set the name of the descriptor's partitioning policy.
     * A PartitioningPolicy with the same name must be defined on the Project.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    public void setPartitioningPolicyName(String partitioningPolicyName) {
        this.partitioningPolicyName = partitioningPolicyName;
    }

    /**
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.
     * 
     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public Class getCacheInterceptorClass() {
        return getCachePolicy().getCacheInterceptorClass();
    }
    
    /**
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.
     * 
     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public String getCacheInterceptorClassName() {
        return getCachePolicy().getCacheInterceptorClassName();
    }
    
    /**
     * PUBLIC:
     * Return the CacheInvalidationPolicy for this descriptor
     * For uninitialized cache invalidation policies, this will return a NoExpiryCacheInvalidationPolicy
     * @return CacheInvalidationPolicy
     * @see org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy
     */
    public CacheInvalidationPolicy getCacheInvalidationPolicy() {
        if (cacheInvalidationPolicy == null) {
            cacheInvalidationPolicy = new NoExpiryCacheInvalidationPolicy();
        }
        return cacheInvalidationPolicy;
    }

    /**
     * PUBLIC:
     * Get a value indicating the type of cache synchronization that will be used on objects of
     * this type. Possible values are:
     * SEND_OBJECT_CHANGES
     * INVALIDATE_CHANGED_OBJECTS
     * SEND_NEW_OBJECTS+WITH_CHANGES
     * DO_NOT_SEND_CHANGES
     */
    public int getCacheSynchronizationType() {
        return getCachePolicy().getCacheSynchronizationType();
    }

    /**
     * INTERNAL:
     */
    public List<CascadeLockingPolicy> getCascadeLockingPolicies() {
        if (this.cascadeLockingPolicies == null) {
            this.cascadeLockingPolicies = new ArrayList();
        }
        return cascadeLockingPolicies;
    }

    /**
     * ADVANCED:
     *  automatically orders database access through the foreign key information provided in 1:1 and 1:m mappings.
     * In some case when 1:1 are not defined it may be required to tell the descriptor about a constraint,
     * this defines that this descriptor has a foreign key constraint to another class and must be inserted after
     * instances of the other class.
     */
    public Vector getConstraintDependencies() {
        if (constraintDependencies == null) {
            constraintDependencies = NonSynchronizedVector.newInstance(1);
        }
        return constraintDependencies;
    }

    /**
     * INTERNAL:
     * Returns the copy policy.
     */
    public CopyPolicy getCopyPolicy() {
        // Lazy initialize for XML deployment.
        if (copyPolicy == null) {
            setCopyPolicy(new InstantiationCopyPolicy());
        }
        return copyPolicy;
    }
    
    /**
     * INTERNAL:
     * Returns the name of a Class that implements CopyPolicy
     * Will be instantiated as a copy policy at initialization times
     * using the no-args constructor
     */
    public String getCopyPolicyClassName(){
        return copyPolicyClassName;
    }

    /**
     * INTERNAL:
     * The first table in the tables is always treated as default.
     */
    public DatabaseTable getDefaultTable() {
        return defaultTable;
    }

    /**
     * ADVANCED:
     * return the descriptor type (NORMAL by default, others include INTERFACE, AGGREGATE, AGGREGATE COLLECTION)
     */
    public int getDescriptorType() {
        return descriptorType;
    }

    /**
     * INTERNAL:
     * This method is explicitly used by the XML reader.
     */
    public String getDescriptorTypeValue() {
        if (isAggregateCollectionDescriptor()) {
            return "Aggregate collection";
        } else if (isAggregateDescriptor()) {
            return "Aggregate";
        } else if (isDescriptorForInterface()) {
            return "Interface";
        } else {
            // Default.
            return "Normal";
        }
    }

    /**
     * ADVANCED:
     * Return the derives id mappings.
     */
    public Collection<DatabaseMapping> getDerivesIdMappinps() {
        return derivesIdMappings.values();
    }

    /**
     * INTERNAL:
     * DescriptorCustomizer is the JPA equivalent of an amendment method.
     */
    public String getDescriptorCustomizerClassName(){
        return descriptorCustomizerClassName;
    }

    /**
     * PUBLIC:
     * Get the event manager for the descriptor.  The event manager is responsible
     * for managing the pre/post selectors.
     */
    public DescriptorEventManager getDescriptorEventManager() {
        return getEventManager();
    }

    /**
     * PUBLIC:
     * Get the event manager for the descriptor.  The event manager is responsible
     * for managing the pre/post selectors.
     */
    @Override
    public DescriptorEventManager getEventManager() {
        // Lazy initialize for XML deployment.
        if (eventManager == null) {
            setEventManager(new org.eclipse.persistence.descriptors.DescriptorEventManager());
        }
        return eventManager;
    }

    /**
     * INTERNAL:
     * Return all the fields
     */
    public Vector<DatabaseField> getFields() {
        if (fields == null) {
            fields = NonSynchronizedVector.newInstance();
        }
        return fields;
    }

    /**
     * INTERNAL:
     * Return all selection fields
     */
    public List<DatabaseField> getSelectionFields() {
        return selectionFields;
    }

    /**
     * INTERNAL:
     * Return all selection fields
     */
    public List<DatabaseField> getSelectionFields(ObjectLevelReadQuery query) {
        if (hasSerializedObjectPolicy() && query.shouldUseSerializedObjectPolicy()) {
            return this.serializedObjectPolicy.getSelectionFields();
        } else {
            return selectionFields;
        }
    }

    /**
     * INTERNAL:
     */
    public Set<DatabaseField> getForeignKeyValuesForCaching() {
        return foreignKeyValuesForCaching;
    }

    /**
     * INTERNAL:
     * Return the class of identity map to be used by this descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public Class getIdentityMapClass() {
        return getCachePolicy().getIdentityMapClass();
    }

    /**
     * PUBLIC:
     * Return the size of the identity map.
     */
    public int getIdentityMapSize() {
        return getCachePolicy().getIdentityMapSize();
    }

    /**
     * PUBLIC:
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    public InheritancePolicy getDescriptorInheritancePolicy() {
        return getInheritancePolicy();
    }

    /**
     * PUBLIC:
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    @Override
    public InheritancePolicy getInheritancePolicy() {
        if (inheritancePolicy == null) {
            // Lazy initialize to conserve space in non-inherited classes.
            setInheritancePolicy(new org.eclipse.persistence.descriptors.InheritancePolicy(this));
        }
        return inheritancePolicy;
    }

    /**
     * INTERNAL:
     * Return the inheritance policy.
     */
    public InheritancePolicy getInheritancePolicyOrNull() {
        return inheritancePolicy;
    }

    /**
     * INTERNAL:
     * Returns the instantiation policy.
     */
    @Override
    public InstantiationPolicy getInstantiationPolicy() {
        // Lazy initialize for XML deployment.
        if (instantiationPolicy == null) {
            setInstantiationPolicy(new InstantiationPolicy());
        }
        return instantiationPolicy;
    }

    /**
     * PUBLIC:
     * Returns the InterfacePolicy.
     * The interface policy allows for a descriptor's public and variable interfaces to be defined.
     * Caution must be used in using this method as it lazy initializes an interface policy.
     * Calling this on a descriptor that does not use interfaces will cause problems, #hasInterfacePolicy() must always first be called.
     */
    public InterfacePolicy getInterfacePolicy() {
        if (interfacePolicy == null) {
            // Lazy initialize to conserve space in non-inherited classes.
            setInterfacePolicy(new InterfacePolicy(this));
        }
        return interfacePolicy;
    }

    /**
     * INTERNAL:
     * Returns the InterfacePolicy.
     */
    public InterfacePolicy getInterfacePolicyOrNull() {
        return interfacePolicy;
    }

    /**
     * PUBLIC:
     * Return the java class.
     */
    @Override
    public Class getJavaClass() {
        return javaClass;
    }

    /**
     * Return the class name, used by the MW.
     */
    public String getJavaClassName() {
        if ((javaClassName == null) && (javaClass != null)) {
            javaClassName = javaClass.getName();
        }
        return javaClassName;
    }

    /**
     * INTERNAL:
     * Returns a reference to the mappings that must be traverse when locking
     */
    public List<DatabaseMapping> getLockableMappings() {
        if (this.lockableMappings == null) {
            this.lockableMappings = new ArrayList();
        }
        return this.lockableMappings;
    }

    /**
     * PUBLIC:
     * Returns the mapping associated with a given attribute name.
     * This can be used to find a descriptors mapping in a amendment method before the descriptor has been initialized.
     */
    public DatabaseMapping getMappingForAttributeName(String attributeName) {
        // ** Don't use this internally, just for amendments, see getMappingForAttributeName on ObjectBuilder.
        for (Enumeration mappingsNum = mappings.elements(); mappingsNum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsNum.nextElement();
            if ((mapping.getAttributeName() != null) && mapping.getAttributeName().equals(attributeName)) {
                return mapping;
            }
        }
        return null;
    }

    /**
     * ADVANCED:
     * Removes the locally defined mapping associated with a given attribute name.
     * This can be used in a amendment method before the descriptor has been initialized.
     */
    public DatabaseMapping removeMappingForAttributeName(String attributeName) {
        DatabaseMapping mapping = getMappingForAttributeName(attributeName);
        getMappings().remove(mapping);
        return mapping;
    }

    /**
     * PUBLIC:
     * Returns mappings
     */
    public Vector<DatabaseMapping> getMappings() {
        return mappings;
    }

    /**
     * INTERNAL:
     * Returns the foreign key relationships used for multiple tables which were specified by the user. Used
     * by the Project XML writer to output these associations
     *
     * @see #adjustMultipleTableInsertOrder()
     */
    public Vector getMultipleTableForeignKeyAssociations() {
        Vector associations = new Vector(getAdditionalTablePrimaryKeyFields().size() * 2);
        Iterator tablesHashtable = getAdditionalTablePrimaryKeyFields().values().iterator();
        while (tablesHashtable.hasNext()) {
            Map tableHash = (Map)tablesHashtable.next();
            Iterator fieldEnumeration = tableHash.keySet().iterator();
            while (fieldEnumeration.hasNext()) {
                DatabaseField keyField = (DatabaseField)fieldEnumeration.next();

                //PRS#36802(CR#2057) contains() is changed to containsKey()
                if (getMultipleTableForeignKeys().containsKey(keyField.getTable())) {
                    Association association = new Association(keyField.getQualifiedName(), ((DatabaseField)tableHash.get(keyField)).getQualifiedName());
                    associations.addElement(association);
                }
            }
        }
        return associations;
    }

    /**
     * INTERNAL:
     * Returns the foreign key relationships used for multiple tables which were specified by the user. The key
     * of the Map is the field in the source table of the foreign key relationship. The value is the field
     * name of the target table.
     *
     * @see #adjustMultipleTableInsertOrder()
     */
    public Map<DatabaseTable, Set<DatabaseTable>> getMultipleTableForeignKeys() {
        if (multipleTableForeignKeys == null) {
            multipleTableForeignKeys = new HashMap(5); 
        }
        return multipleTableForeignKeys;
    }

    /**
     * INTERNAL:
     * Returns the List of DatabaseTables in the order which INSERTS should take place. This order is
     * determined by the foreign key fields which are specified by the user.
     */
    public List<DatabaseTable> getMultipleTableInsertOrder() throws DescriptorException {
        return multipleTableInsertOrder;
    }

    /**
     * INTERNAL:
     * Returns the foreign key relationships used for multiple tables which were specified by the user. Used
     * by the Project XML writer to output these associations
     *
     * @see #adjustMultipleTableInsertOrder()
     */
    public Vector getMultipleTablePrimaryKeyAssociations() {
        Vector associations = new Vector(getAdditionalTablePrimaryKeyFields().size() * 2);
        Iterator tablesHashtable = getAdditionalTablePrimaryKeyFields().values().iterator();
        while (tablesHashtable.hasNext()) {
            Map tableHash = (Map)tablesHashtable.next();
            Iterator fieldEnumeration = tableHash.keySet().iterator();
            while (fieldEnumeration.hasNext()) {
                DatabaseField keyField = (DatabaseField)fieldEnumeration.next();

                //PRS#36802(CR#2057) contains() is changed to containsKey()
                if (!getMultipleTableForeignKeys().containsKey(keyField.getTable())) {
                    Association association = new Association(keyField.getQualifiedName(), ((DatabaseField)tableHash.get(keyField)).getQualifiedName());
                    associations.addElement(association);
                }
            }
        }
        return associations;
    }
    
    /**
     * INTERNAL:
     * Retun the multitenant policy
     */
    public MultitenantPolicy getMultitenantPolicy() {
        return multitenantPolicy;
    }

    /**
     * INTERNAL:
     * Return the object builder
     */
    @Override
    public ObjectBuilder getObjectBuilder() {
        return objectBuilder;
    }

    /**
     * PUBLIC:
     * Returns the OptimisticLockingPolicy. By default this is an instance of VersionLockingPolicy.
     */
    public OptimisticLockingPolicy getOptimisticLockingPolicy() {
        return optimisticLockingPolicy;
    }

    /**
     * INTERNAL:
     * Set of mappings that require early delete behavior.
     * This is used to handle deletion constraints.
     */
    public List<DatabaseMapping> getPreDeleteMappings() {
        if (this.preDeleteMappings == null) {
            this.preDeleteMappings = new ArrayList<DatabaseMapping>();
        }
        return this.preDeleteMappings;
    }

    /**
     * INTERNAL:
     * Add the mapping to be notified before deletion.
     * Must also be added to child descriptors.
     */
    public void addPreDeleteMapping(DatabaseMapping mapping) {
        if (mapping.getAttributeName() == null) {
            System.out.println(mapping);
        }
        getPreDeleteMappings().add(mapping);
    }

    /**
     * PUBLIC:
     * Return the names of all the primary keys.
     */
    @Override
    public Vector<String> getPrimaryKeyFieldNames() {
        Vector<String> result = new Vector(getPrimaryKeyFields().size());
        List primaryKeyFields = getPrimaryKeyFields();
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            result.addElement(((DatabaseField)primaryKeyFields.get(index)).getQualifiedName());
        }

        return result;
    }

    /**
     * INTERNAL:
     * Return all the primary key fields
     */
    @Override
    public List<DatabaseField> getPrimaryKeyFields() {
        return primaryKeyFields;
    }

    /**
     * PUBLIC:
     * Returns the user defined properties.
     */
    public Map getProperties() {
        if (properties == null) {
            properties = new HashMap(5);
        }
        return properties;
    }

    /**
     * PUBLIC:
     * Returns the descriptor property associated the given String.
     */
    public Object getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * INTERNAL:
     * Return the query key with the specified name
     */
    public QueryKey getQueryKeyNamed(String queryKeyName) {
        return this.getQueryKeys().get(queryKeyName);
    }

    /**
     * PUBLIC:
     * Return the query keys.
     */
    public Map<String, QueryKey> getQueryKeys() {
        return queryKeys;
    }

    /**
     * PUBLIC:
     * Return the queryManager.
     * The query manager can be used to specify customization of the SQL
     * that  generates for this descriptor.
     */
    public DescriptorQueryManager getDescriptorQueryManager() {
        return this.getQueryManager();
    }

    /**
     * PUBLIC:
     * Return the queryManager.
     * The query manager can be used to specify customization of the SQL
     * that  generates for this descriptor.
     */
    public DescriptorQueryManager getQueryManager() {
        // Lazy initialize for XML deployment.
        if (queryManager == null) {
            setQueryManager(new org.eclipse.persistence.descriptors.DescriptorQueryManager());
        }
        return queryManager;
    }

    /**
     * INTERNAL:
     * Return the class of identity map to be used by this descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public Class getRemoteIdentityMapClass() {
        return getCachePolicy().getRemoteIdentityMapClass();
    }
    
    /**
     * PUBLIC:
     * This method returns the root descriptor for for this descriptor's class heirarchy.
     * If the user is not using inheritance then the root class will be this class.
     */
    public ClassDescriptor getRootDescriptor(){
        if (this.hasInheritance()){
            return this.getInheritancePolicy().getRootParentDescriptor();
        }
        return this;
    }

    /**
     * PUBLIC:
     * Return the size of the remote identity map.
     */
    public int getRemoteIdentityMapSize() {
        return getCachePolicy().getRemoteIdentityMapSize();
    }

    /**
     * PUBLIC:
     * Return returning policy.
     */
    public ReturningPolicy getReturningPolicy() {
        return returningPolicy;
    }

    /**
     * INTERNAL:
     * Get sequence number field
     */
    public DatabaseField getSequenceNumberField() {
        return sequenceNumberField;
    }

    /**
     * PUBLIC:
     * Get sequence number field name
     */
    public String getSequenceNumberFieldName() {
        if (getSequenceNumberField() == null) {
            return null;
        }
        return getSequenceNumberField().getQualifiedName();
    }

    /**
     * PUBLIC:
     * Get sequence number name
     */
    public String getSequenceNumberName() {
        return sequenceNumberName;
    }

    /**
     * INTERNAL:
     * Return the name of the session local to this descriptor.
     * This is used by the session broker.
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * INTERNAL:
     * Checks if table name exists with the current descriptor or not.
     */
    public DatabaseTable getTable(String tableName) throws DescriptorException {
        if (hasTablePerMultitenantPolicy()) {
            DatabaseTable table =  ((TablePerMultitenantPolicy) getMultitenantPolicy()).getTable(tableName);
            
            if (table != null) {
                return table;
            }
        }
        
        if (getTables().isEmpty()) {
            return null;// Assume aggregate descriptor.
        }

        for (Enumeration tables = getTables().elements(); tables.hasMoreElements();) {
            DatabaseTable table = (DatabaseTable)tables.nextElement();

            if(tableName.indexOf(' ') != -1) {
                //if looking for a table with a ' ' character, the name will have
                //been quoted internally. Check for match without quotes.
                String currentTableName = table.getName();
                if(currentTableName.substring(1, currentTableName.length() - 1).equals(tableName)) {
                    return table;
                }
            }
            if (table.getName().equals(tableName)) {
                return table;
            }
        }

        if (isAggregateDescriptor()) {
            return getDefaultTable();
        }
        throw DescriptorException.tableNotPresent(tableName, this);
    }

    /**
     * PUBLIC:
     * Return the name of the descriptor's first table.
     * This method must only be called on single table descriptors.
     */
    public String getTableName() {
        if (getTables().isEmpty()) {
            return null;
        } else {
            return getTables().get(0).getName();
        }
    }

    /**
     * PUBLIC:
     * Return the table names.
     */
    public Vector getTableNames() {
        Vector tableNames = new Vector(getTables().size());
        for (Enumeration fieldsEnum = getTables().elements(); fieldsEnum.hasMoreElements();) {
            tableNames.addElement(((DatabaseTable)fieldsEnum.nextElement()).getQualifiedName());
        }

        return tableNames;
    }

    /**
     * PUBLIC:
     * Returns the TablePerClassPolicy.
     * The table per class policy allows JPA users to configure the 
     * TABLE_PER_CLASS inheritance strategy. Calling this on a descriptor that 
     * does not use table per class will cause problems, 
     * #hasTablePerClassPolicy() must always first be called.
     * @see #setTablePerClassPolicy
     */
    public TablePerClassPolicy getTablePerClassPolicy() {
        return (TablePerClassPolicy) interfacePolicy;
    }
    
    /**
     * INTERNAL:
     * Return all the tables.
     */
    public Vector<DatabaseTable> getTables() {
        return tables;
    }

    /**
     * INTERNAL:
     * searches first descriptor than its ReturningPolicy for an equal field
     */
    @Override
    public DatabaseField getTypedField(DatabaseField field) {
        boolean mayBeMoreThanOne = hasMultipleTables() && !field.hasTableName();
        DatabaseField foundField = null;
        for (int index = 0; index < getFields().size(); index++) {
            DatabaseField descField = getFields().get(index);
            if (field.equals(descField)) {
                if (descField.getType() != null) {
                    foundField = descField;
                    if (!mayBeMoreThanOne || descField.getTable().equals(getDefaultTable())) {
                        break;
                    }
                }
            }
        }
        if ((foundField == null) && hasReturningPolicy()) {
            DatabaseField returnField = getReturningPolicy().getField(field);
            if ((returnField != null) && (returnField.getType() != null)) {
                foundField = returnField;
            }
        }
        if (foundField != null) {
            foundField = foundField.clone();
            if (!field.hasTableName()) {
                foundField.setTableName("");
            }
        }

        return foundField;
    }

    /**
     * ADVANCED:
     * Return the WrapperPolicy for this descriptor.
     * This advanced feature can be used to wrap objects with other classes such as CORBA TIE objects or EJBs.
     */
    public WrapperPolicy getWrapperPolicy() {
        return wrapperPolicy;
    }

    /**
     * INTERNAL:
     * Checks if the class has any private owned parts or other dependencies, (i.e. M:M join table).
     */
    public boolean hasDependencyOnParts() {
        for (DatabaseMapping mapping : getMappings()) {
            if (mapping.hasDependency()) {
                return true;
            }
        }

        return false;
    }

    /**
     * INTERNAL:
     * returns true if users have designated one or more mappings as IDs. Used 
     * for CMP3Policy primary key class processing. 
     */
    public boolean hasDerivedId() {
        return ! derivesIdMappings.isEmpty();
    }

    /**
     * INTERNAL:
     * returns true if a DescriptorEventManager has been set.
     */ 
    @Override
    public boolean hasEventManager() {
        return null != eventManager;
    }

    /**
     * INTERNAL:
     * Return if this descriptor is involved in inheritance, (is child or parent).
     * Note: If this class is part of table per class inheritance strategy this
     * method will return false. 
     * @see #hasTablePerClassPolicy()
     */
    @Override
    public boolean hasInheritance() {
        return (inheritancePolicy != null);
    }

    /**
     * INTERNAL:
     * Return if this descriptor is involved in interface, (is child or parent).
     */
    public boolean hasInterfacePolicy() {
        return (interfacePolicy != null);
    }
    
    /**
     * INTERNAL:
     * Check if descriptor has multiple tables
     */
    public boolean hasMultipleTables() {
        return (getTables().size() > 1);
    }

    /**
     * INTERNAL:
     * Calculates whether descriptor references an entity (directly or through a nested mapping).
     */
    public boolean hasNestedIdentityReference(boolean withChildren) {
        if (withChildren && hasInheritance() && getInheritancePolicy().hasChildren()) {
            for (ClassDescriptor childDescriptor : getInheritancePolicy().getAllChildDescriptors()) {
                // leaf children have all the mappings
                if (!childDescriptor.getInheritancePolicy().hasChildren()) {
                    if (childDescriptor.hasNestedIdentityReference(false)) {
                        return true;
                    }
                }
            }
        } else {
            for (DatabaseMapping mapping : getMappings()) {
                if (mapping.hasNestedIdentityReference()) {
                    return true;
                }
            }
        }
        return false;
    }        
    
    /**
     * @return the hasNoncacheableMappings
     */
    public boolean hasNoncacheableMappings() {
        return hasNoncacheableMappings;
    }

    /**
     * @return the preDeleteMappings
     */
    public boolean hasPreDeleteMappings() {
        return preDeleteMappings != null;
    }

    /**
     * INTERNAL:
     * Checks if the class has any private owned parts are not
     */
    public boolean hasPrivatelyOwnedParts() {
        for (Enumeration mappings = getMappings().elements(); mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (mapping.isPrivateOwned()) {
                return true;
            }
        }

        return false;
    }

    /**
     * INTERNAL:
     * Checks to see if it has a query key or mapping with the specified name or not.
     */
    public boolean hasQueryKeyOrMapping(String attributeName) {
        return (getQueryKeys().containsKey(attributeName) || (getObjectBuilder().getMappingForAttributeName(attributeName) != null));
    }

    /**
     * INTERNAL:
     *  return whether this descriptor has any relationships through its mappings, through inheritance, or through aggregates 
     * @return
     */
    public boolean hasRelationships() {
        return hasRelationships;
    }

    /**
     * INTERNAL:
     * This method returns true if this descriptor has either a ForeignReferenceMapping to 
     * an object aside from the one described by descriptor or more than one ForeignReferenceMapping
     * to descriptor.  (i.e. It determines if there is any mapping aside from a backpointer to a mapping
     * defined in descriptor)
     * @param descriptor
     * @return
     */
    public boolean hasRelationshipsExceptBackpointer(ClassDescriptor descriptor){
        Iterator<DatabaseMapping> i = mappings.iterator();
        boolean foundRelationship = false;
        while (i.hasNext()){
            DatabaseMapping mapping = i.next();
            if (mapping.isForeignReferenceMapping()){
                ForeignReferenceMapping frMapping = (ForeignReferenceMapping)mapping;
                if (frMapping.getReferenceDescriptor().equals(descriptor)){
                    if (foundRelationship){
                        return true;
                    } else {
                        foundRelationship = true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * INTERNAL:
     * Return if this descriptor has Returning policy.
     */
    public boolean hasReturningPolicy() {
        return (returningPolicy != null);
    }

    /**
     * INTERNAL:
     */
    public boolean hasSerializedObjectPolicy() {
        return this.serializedObjectPolicy != null;
    }
    
    /**
     * INTERNAL:
     */
    public SerializedObjectPolicy getSerializedObjectPolicy() {
        return this.serializedObjectPolicy;
    }
    
    /**
     * INTERNAL:
     */
    public void setSerializedObjectPolicy(SerializedObjectPolicy serializedObjectPolicy) {
        this.serializedObjectPolicy = serializedObjectPolicy;
        if (serializedObjectPolicy != null) {
            serializedObjectPolicy.setDescriptor(this);
        }
    }
    
    /**
     * INTERNAL:
     * Return if a wrapper policy is used.
     */
    public boolean hasWrapperPolicy() {
        return this.wrapperPolicy != null;
    }

    /**
     * INTERNAL:
     * Initialize the mappings as a separate step.
     * This is done as a separate step to ensure that inheritance has been first resolved.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        // Avoid repetitive initialization (this does not solve loops)
        if (isInitialized(INITIALIZED) || isInvalid()) {
            return;
        }

        setInitializationStage(INITIALIZED);
        
        // make sure that parent mappings are initialized?
        if (isChildDescriptor()) {
            ClassDescriptor parentDescriptor = getInheritancePolicy().getParentDescriptor(); 
            parentDescriptor.initialize(session);
            getCachePolicy().initializeFromParent(parentDescriptor.getCachePolicy(), this,
                    parentDescriptor, session);
            // Setup this early before useOptimisticLocking is called so that subclass
            // versioned by superclass are also covered
            getInheritancePolicy().initializeOptimisticLocking();
            // EL bug 336486
            getInheritancePolicy().initializeCacheInvalidationPolicy();
            if (parentDescriptor.hasSerializedObjectPolicy()) {
                if (!hasSerializedObjectPolicy()) {
                    // If SerializedObjectPolicy set on parent descriptor then should be set on children, too
                    setSerializedObjectPolicy(parentDescriptor.getSerializedObjectPolicy().instantiateChild());
                }
            }
        }

        // Mappings must be sorted before field are collected in the order of the mapping for indexes to work.
        // Sorting the mappings to ensure that all DirectToFields get merged before all other mappings
        // This prevents null key errors when merging maps
        if (shouldOrderMappings()) {
            Vector mappings = getMappings();
            Object[] mappingsArray = new Object[mappings.size()];
            for (int index = 0; index < mappings.size(); index++) {
                mappingsArray[index] = mappings.get(index);
            }
            Arrays.sort(mappingsArray, new MappingCompare());
            mappings = NonSynchronizedVector.newInstance(mappingsArray.length);
            for (int index = 0; index < mappingsArray.length; index++) {
                mappings.add(mappingsArray[index]);
            }
            setMappings(mappings);
        }

        boolean initializeCascadeLocking = (usesOptimisticLocking() && getOptimisticLockingPolicy().isCascaded()) || hasCascadeLockingPolicies();
        for (DatabaseMapping mapping : getMappings()) {
            validateMappingType(mapping);
            mapping.initialize(session);
            if (!mapping.isCacheable()){
                this.hasNoncacheableMappings = true;
            }

            if (mapping.isForeignReferenceMapping()){
                if(((ForeignReferenceMapping)mapping).getIndirectionPolicy() instanceof ProxyIndirectionPolicy) {
                    session.getProject().setHasProxyIndirection(true);
                }
                ClassDescriptor referencedDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                if (referencedDescriptor!= null){
                    referencedDescriptor.referencingClasses.add(this);
                }
            }

            if (mapping.isAggregateObjectMapping()) {
                ClassDescriptor referencedDescriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
                if (referencedDescriptor!= null){
                    referencedDescriptor.referencingClasses.add(this);
                }
            }
            // If this descriptor uses a cascaded version optimistic locking 
            // or has cascade locking policies set then prepare check the 
            // mappings.
            if (initializeCascadeLocking) {
                prepareCascadeLockingPolicy(mapping);
            }

            // JPA 2.0 Derived identities - build a map of derived id mappings.
            if (mapping.derivesId()) {
                this.derivesIdMappings.put(mapping.getAttributeName(), mapping);
            }
            
            // Add all the fields in the mapping to myself.
            Helper.addAllUniqueToVector(getFields(), mapping.getFields());
        }
        if (initializeCascadeLocking) {
            this.cascadedLockingInitialized = true;
        }
        
        if (hasMappingsPostCalculateChangesOnDeleted()) {
            session.getProject().setHasMappingsPostCalculateChangesOnDeleted(true);
        }

        // PERF: Don't initialize locking until after fields have been computed so
        // field is in correct position.
        if (!isAggregateDescriptor()) {
            if (!isChildDescriptor()) {
                // Add write lock field to getFields
                if (usesOptimisticLocking()) {
                    getOptimisticLockingPolicy().initializeProperties();
                }
            }
            if (hasSerializedObjectPolicy()) {
                getSerializedObjectPolicy().initializeField(session);
            }
        }

        // All the query keys should be initialized.
        for (Iterator queryKeys = getQueryKeys().values().iterator(); queryKeys.hasNext();) {
            QueryKey queryKey = (QueryKey)queryKeys.next();
            queryKey.initialize(this);
        }

        if (getPartitioningPolicyName() != null) {
            PartitioningPolicy policy = session.getProject().getPartitioningPolicy(getPartitioningPolicyName());
            if (policy == null) {
                session.getIntegrityChecker().handleError(DescriptorException.missingPartitioningPolicy(getPartitioningPolicyName(), this, null));
            }
            setPartitioningPolicy(policy);
        }
        
        // If this descriptor has inheritance then it needs to be initialized before all fields is set.
        if (hasInheritance()) {
            getInheritancePolicy().initialize(session);
            if (getInheritancePolicy().isChildDescriptor()) {
                ClassDescriptor parentDescriptor = getInheritancePolicy().getParentDescriptor();
                for (DatabaseMapping mapping : parentDescriptor.getMappings()) {
                    if (mapping.isAggregateObjectMapping() || ((mapping.isForeignReferenceMapping() && (!mapping.isDirectCollectionMapping())) && (!((ForeignReferenceMapping)mapping).usesIndirection()))) {
                        getLockableMappings().add(mapping);// add those mappings from the parent.
                    }
                    // JPA 2.0 Derived identities - build a map of derived id mappings.
                    if (mapping.derivesId()) {
                        this.derivesIdMappings.put(mapping.getAttributeName(), mapping);
                    }
                }
                if (parentDescriptor.hasPreDeleteMappings()) {
                    getPreDeleteMappings().addAll(parentDescriptor.getPreDeleteMappings());
                }
                if (parentDescriptor.hasMappingsPostCalculateChanges()) {
                    getMappingsPostCalculateChanges().addAll(parentDescriptor.getMappingsPostCalculateChanges());
                }
                if (parentDescriptor.hasMappingsPostCalculateChangesOnDeleted()) {
                    getMappingsPostCalculateChangesOnDeleted().addAll(parentDescriptor.getMappingsPostCalculateChangesOnDeleted());
                }
            }
        }

        // cr 4097  Ensure that the mappings are ordered after the superclasses mappings have been added.
        // This ensures that the mappings in the child class are ordered correctly
        // I am sorting the mappings to ensure that all DirectToFields get merged before all other mappings
        // This prevents null key errors when merging maps
        // This resort will change the previous sort order, only do it if has inheritance.
        if (hasInheritance() && shouldOrderMappings()) {
            Vector mappings = getMappings();
            Object[] mappingsArray = new Object[mappings.size()];
            for (int index = 0; index < mappings.size(); index++) {
                mappingsArray[index] = mappings.get(index);
            }
            Arrays.sort(mappingsArray, new MappingCompare());
            mappings = NonSynchronizedVector.newInstance(mappingsArray.length);
            for (int index = 0; index < mappingsArray.length; index++) {
                mappings.add(mappingsArray[index]);
            }
            setMappings(mappings);
        }
        
        // Initialize the allFields to its fields, this can be done now because the fields have been computed.
        setAllFields((Vector)getFields().clone());

        getObjectBuilder().initialize(session);
        
        // Initialize the multitenant policy only after the mappings have been
        // initialized.
        if (hasMultitenantPolicy()) {
            getMultitenantPolicy().initialize(session);
        }
        
        if (shouldOrderMappings()) {
            // PERF: Ensure direct primary key mappings are first.
            for (int index = getObjectBuilder().getPrimaryKeyMappings().size() - 1; index >= 0; index--) {
                DatabaseMapping mapping = getObjectBuilder().getPrimaryKeyMappings().get(index);
                if ((mapping != null) && mapping.isAbstractColumnMapping()) {
                    getMappings().remove(mapping);
                    getMappings().add(0, mapping);
                    DatabaseField field = ((AbstractColumnMapping)mapping).getField();
                    getFields().remove(field);
                    getFields().add(0, field);
                    getAllFields().remove(field);
                    getAllFields().add(0, field);
                }
            }
        }

        if (usesOptimisticLocking() && (!isChildDescriptor())) {
            getOptimisticLockingPolicy().initialize(session);
        }
        if (hasInterfacePolicy() || isDescriptorForInterface()) {
            interfaceInitialization(session);
        }
        if (hasWrapperPolicy()) {
            getWrapperPolicy().initialize(session);
        }
        if (hasReturningPolicy()) {
            getReturningPolicy().initialize(session);
        }
        if (hasSerializedObjectPolicy()) {
            getSerializedObjectPolicy().initialize(session);
        }
        getQueryManager().initialize(session);
        getEventManager().initialize(session);
        getCopyPolicy().initialize(session);
        getInstantiationPolicy().initialize(session);
        getCachePolicy().initialize(this, session);

        if (getHistoryPolicy() != null) {
            getHistoryPolicy().initialize(session);
        } else if (hasInheritance()) {
            // Only one level of inheritance needs to be checked as parent descriptors
            // are initialized before children are
            ClassDescriptor parentDescriptor = getInheritancePolicy().getParentDescriptor();
            if ((parentDescriptor != null) && (parentDescriptor.getHistoryPolicy() != null)) {
                setHistoryPolicy((HistoryPolicy)parentDescriptor.getHistoryPolicy().clone());
            }
        }

        if (getCMPPolicy() != null) {
            getCMPPolicy().initialize(this, session);
        }

        // Validate the fetch group setting during descriptor initialization.
        if (hasFetchGroupManager()) {
            getFetchGroupManager().initialize(session);
        }
                
        // By default if change policy is not configured set to attribute change tracking if weaved.
        if ((getObjectChangePolicyInternal() == null) && (ChangeTracker.class.isAssignableFrom(getJavaClass()))) {
            // Only auto init if this class "itself" was weaved for change tracking, i.e. not just a superclass.
            if (Arrays.asList(getJavaClass().getInterfaces()).contains(PersistenceWeavedChangeTracking.class)
                    || (DynamicEntityImpl.class.isAssignableFrom(getJavaClass()))) {
                // Must double check that this descriptor support change tracking,
                // when it was weaved it was not initialized, and may now know that it does not support change tracking.
                if (supportsChangeTracking(session.getProject())) {
                    setObjectChangePolicy(new AttributeChangeTrackingPolicy());
                }
            }
        }
        // 3934266 move validation to the policy allowing for this to be done in the sub policies.
        getObjectChangePolicy().initialize(session, this);
        
        // Setup default redirectors.  Any redirector that is not set will get assigned the
        // default redirector.
        if (this.defaultReadAllQueryRedirector == null){
            this.defaultReadAllQueryRedirector = this.defaultQueryRedirector;
        }
        if (this.defaultReadObjectQueryRedirector == null){
            this.defaultReadObjectQueryRedirector = this.defaultQueryRedirector;
        }
        if (this.defaultReportQueryRedirector == null){
            this.defaultReportQueryRedirector = this.defaultQueryRedirector;
        }
        if (this.defaultInsertObjectQueryRedirector == null){
            this.defaultInsertObjectQueryRedirector = this.defaultQueryRedirector;
        }

        if (this.defaultUpdateObjectQueryRedirector == null){
            this.defaultUpdateObjectQueryRedirector = this.defaultQueryRedirector;
        }
    }
    
    /**
     * INTERNAL:
     * Initialize the query manager specific to the descriptor type.
     */
    public void initialize(DescriptorQueryManager queryManager, AbstractSession session) {
        //PERF: set read-object query to cache generated SQL.
        if (!queryManager.hasReadObjectQuery()) {
            // Prepare static read object query always.
            ReadObjectQuery readObjectQuery = new ReadObjectQuery();
            readObjectQuery.setSelectionCriteria(getObjectBuilder().getPrimaryKeyExpression());
            queryManager.setReadObjectQuery(readObjectQuery);
        }
        queryManager.getReadObjectQuery().setName("read" + getJavaClass().getSimpleName());

        if (!queryManager.hasInsertQuery()) {
            // Prepare insert query always.
            queryManager.setInsertQuery(new InsertObjectQuery());
        }
        queryManager.getInsertQuery().setModifyRow(getObjectBuilder().buildTemplateInsertRow(session));

        if (!usesFieldLocking()) {
            //do not reset the query if we are using field locking
            if (!queryManager.hasDeleteQuery()) {
                // Prepare delete query always.
                queryManager.setDeleteQuery(new DeleteObjectQuery());
            }
            queryManager.getDeleteQuery().setModifyRow(new DatabaseRecord());
        }

        if (queryManager.hasUpdateQuery()) {
            // Do not prepare to update by default to allow minimal update.
            queryManager.getUpdateQuery().setModifyRow(getObjectBuilder().buildTemplateUpdateRow(session));
        }        
    }

    /**
     * INTERNAL:
     * This initialized method is used exclusively for inheritance.  It passes in
     * true if the child descriptor is isolated.
     *
     * This is needed by regular aggregate descriptors (because they require review);
     * but not by SDK aggregate descriptors.
     */
    public void initializeAggregateInheritancePolicy(AbstractSession session) {
        ClassDescriptor parentDescriptor = session.getDescriptor(getInheritancePolicy().getParentClass());
        parentDescriptor.getInheritancePolicy().addChildDescriptor(this);
    }

    /**
     * INTERNAL:
     * Rebuild the multiple table primary key map.
     */
    public void initializeMultipleTablePrimaryKeyFields() {
        int tableSize = getTables().size();
        int additionalTablesSize = tableSize - 1;
        boolean isChild = hasInheritance() && getInheritancePolicy().isChildDescriptor();
        if (isChild) {
            additionalTablesSize = tableSize - getInheritancePolicy().getParentDescriptor().getTables().size();
        }
        if (tableSize <= 1) {
            return;
        }
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression joinExpression = getQueryManager().getMultipleTableJoinExpression();
        for (int index = 1; index < tableSize; index++) {
            DatabaseTable table = getTables().get(index);
            Map<DatabaseField, DatabaseField> oldKeyMapping = getAdditionalTablePrimaryKeyFields().get(table);
            if (oldKeyMapping != null) {
                if (!getQueryManager().hasCustomMultipleTableJoinExpression()) {
                    Expression keyJoinExpression = null;
                    // Build the multiple table join expression resulting from the fk relationships.
                    for (Map.Entry<DatabaseField, DatabaseField> entry : oldKeyMapping.entrySet()) {
                        DatabaseField sourceTableField = entry.getKey();
                        DatabaseField targetTableField = entry.getValue();
                        // Must add this field to read, so translations work on database row, this could be either.
                        if (!getFields().contains(sourceTableField)) {
                            getFields().add(sourceTableField);
                        }
                        if (!getFields().contains(targetTableField)) {
                            getFields().add(targetTableField);
                        }
                        keyJoinExpression = builder.getField(targetTableField).equal(builder.getField(sourceTableField)).and(keyJoinExpression);
                    }
                    if (keyJoinExpression != null) {
                        joinExpression = keyJoinExpression.and(joinExpression);
                    }
                    getQueryManager().getTablesJoinExpressions().put(table, keyJoinExpression);
                }
            } else {
                // If the user has specified a custom multiple table join then we do not assume that the secondary tables have identically named pk as the primary table.
                // No additional fk info was specified so assume the pk field(s) are the named the same in the additional table.
                Map newKeyMapping = new HashMap(getPrimaryKeyFields().size());
                getAdditionalTablePrimaryKeyFields().put(table, newKeyMapping);

                Expression keyJoinExpression = null;
                // For each primary key field in the primary table, add a pk relationship from the primary table's pk field to the assumed identically named secondary pk field.
                for (DatabaseField primaryKeyField : getPrimaryKeyFields()) {
                    DatabaseField secondaryKeyField = primaryKeyField.clone();
                    secondaryKeyField.setTable(table);
                    newKeyMapping.put(primaryKeyField, secondaryKeyField);
                    // Must add this field to read, so translations work on database row.
                    getFields().add(buildField(secondaryKeyField));

                    if (!getQueryManager().hasCustomMultipleTableJoinExpression()) {
                        keyJoinExpression = builder.getField(secondaryKeyField).equal(builder.getField(primaryKeyField)).and(keyJoinExpression);
                    }
                }
                if (keyJoinExpression != null) {
                    joinExpression = keyJoinExpression.and(joinExpression);
                }
                getQueryManager().getTablesJoinExpressions().put(table, keyJoinExpression);
            }
        }
        if (joinExpression != null) {
            getQueryManager().setInternalMultipleTableJoinExpression(joinExpression);
        }
        if (getQueryManager().hasCustomMultipleTableJoinExpression()) {
            Map tablesJoinExpressions = SQLSelectStatement.mapTableToExpression(joinExpression, getTables());
            getQueryManager().getTablesJoinExpressions().putAll(tablesJoinExpressions);
        }
        if (isChild && (additionalTablesSize > 0)) {
            for (int index = tableSize - additionalTablesSize; index < tableSize; index++) {
                DatabaseTable table = getTables().get(index);
                getInheritancePolicy().addChildTableJoinExpressionToAllParents(table, getQueryManager().getTablesJoinExpressions().get(table));
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize the descriptor properties such as write lock and sequencing.
     */
    protected void initializeProperties(AbstractSession session) throws DescriptorException {
        if (!isAggregateDescriptor()) {
            if (!isChildDescriptor()) {
                // Initialize the primary key fields
                for (int index = 0; index < getPrimaryKeyFields().size(); index++) {
                    DatabaseField primaryKey = getPrimaryKeyFields().get(index);
                    primaryKey = buildField(primaryKey);
                    primaryKey.setPrimaryKey(true);
                    getPrimaryKeyFields().set(index, primaryKey);
                }
                List primaryKeyFields = (List)((ArrayList)getPrimaryKeyFields()).clone();
                // Remove non-default table primary key (MW used to set these as pk).
                for (int index = 0; index < primaryKeyFields.size(); index++) {
                    DatabaseField primaryKey = (DatabaseField)primaryKeyFields.get(index);
                    if (!primaryKey.getTable().equals(getDefaultTable())) {
                        getPrimaryKeyFields().remove(primaryKey);
                    }
                }
            }

            // build sequence number field
            if (getSequenceNumberField() != null) {
                setSequenceNumberField(buildField(getSequenceNumberField()));
            }
        }

        // Set the local session name for the session broker.
        setSessionName(session.getName());
    }

    /**
     * INTERNAL:
     * Allow the descriptor to initialize any dependencies on this session.
     */
    public void interfaceInitialization(AbstractSession session) throws DescriptorException {
        if (isInterfaceInitialized(INITIALIZED)) {
            return;
        }

        setInterfaceInitializationStage(INITIALIZED);

        if (isInterfaceChildDescriptor()) {
            for (Iterator<Class> interfaces = getInterfacePolicy().getParentInterfaces().iterator();
                     interfaces.hasNext();) {
                Class parentInterface = interfaces.next();
                ClassDescriptor parentDescriptor = session.getDescriptor(parentInterface);
                parentDescriptor.interfaceInitialization(session);

                if (isDescriptorForInterface()) {
                    setQueryKeys(Helper.concatenateMaps(getQueryKeys(), parentDescriptor.getQueryKeys()));
                } else {
                    //ClassDescriptor is a class, not an interface
                    for (Iterator parentKeys = parentDescriptor.getQueryKeys().keySet().iterator();
                             parentKeys.hasNext();) {
                        String queryKeyName = (String)parentKeys.next();
                        if (!hasQueryKeyOrMapping(queryKeyName)) {
                            //the parent descriptor has a query key not defined in the child
                            session.getIntegrityChecker().handleError(DescriptorException.childDoesNotDefineAbstractQueryKeyOfParent(this, parentDescriptor, queryKeyName));
                        }
                    }
                }

                if (parentDescriptor == this) {
                    return;
                }
            }
        }

        getInterfacePolicy().initialize(session);
    }

    /**
     * INTERNAL:
     * Convenience method to return true if the java class from this descriptor is abstract.
     */
    public boolean isAbstract() {
        return java.lang.reflect.Modifier.isAbstract(getJavaClass().getModifiers());
    }
    
    /**
     * PUBLIC:
     * Return true if this descriptor is an aggregate collection descriptor
     */
    public boolean isAggregateCollectionDescriptor() {
        return this.descriptorType == AGGREGATE_COLLECTION;
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is an aggregate descriptor
     */
    public boolean isAggregateDescriptor() {
        return this.descriptorType == AGGREGATE;
    }

    /**
     * PUBLIC:
     * Return if the descriptor defines inheritance and is a child.
     */
    public boolean isChildDescriptor() {
        return hasInheritance() && getInheritancePolicy().isChildDescriptor();
    }

    /**
     * PUBLIC:
     * Return if the descriptor maps to an EIS or NoSQL datasource.
     */
    public boolean isEISDescriptor() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if the descriptor maps to an object-relational structured type.
     */
    public boolean isObjectRelationalDataTypeDescriptor() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if the descriptor maps to XML.
     */
    public boolean isXMLDescriptor() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if the descriptor maps to a relational table.
     */
    public boolean isRelationalDescriptor() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if the java class is an interface.
     */
    public boolean isDescriptorForInterface() {
        return this.descriptorType == INTERFACE;
    }

    /**
     * PUBLIC
     * return true if this descriptor is any type of aggregate descriptor.
     */
    public boolean isDescriptorTypeAggregate(){
        return this.descriptorType == AGGREGATE_COLLECTION || this.descriptorType == AGGREGATE;
    }

    /**
     * INTERNAL:
     * return true if this descriptor is an entity.
     * (The descriptor may be a  mappedSuperclass - only in the internal case during metamodel processing)
     */
    public boolean isDescriptorTypeNormal(){
        return this.descriptorType == NORMAL;
    }
    
    /**
     * INTERNAL:
     * Check if the descriptor is finished initialization.
     */
    public boolean isFullyInitialized() {
        return this.initializationStage == POST_INITIALIZED;
    }

    /**
     * INTERNAL:
     * Check if descriptor is already initialized for the level of initialization.
     * 1 = pre
     * 2 = mapping
     * 3 = post
     */
    protected boolean isInitialized(int initializationStage) {
        return this.initializationStage >= initializationStage;
    }

    /**
     * INTERNAL:
     * Return if the descriptor defines inheritance and is a child.
     */
    public boolean isInterfaceChildDescriptor() {
        return hasInterfacePolicy() && getInterfacePolicy().isInterfaceChildDescriptor();
    }

    /**
     * INTERNAL:
     * Check if interface descriptor is already initialized for the level of initialization.
     * 1 = pre
     * 2 = mapping
     * 3 = post
     */
    protected boolean isInterfaceInitialized(int interfaceInitializationStage) {
        return this.interfaceInitializationStage >= interfaceInitializationStage;
    }

    /**
     * INTERNAL:
     * Return if an error occurred during initialization which should abort any further initialization.
     */
    public boolean isInvalid() {
        return this.initializationStage == ERROR;
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents an isolated class
     */
    public boolean isIsolated() {
        return getCachePolicy().isIsolated();
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents an isolated class
     */
    public boolean isProtectedIsolation() {
        return getCachePolicy().isProtectedIsolation();
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents an isolated class
     */
    public boolean isSharedIsolation() {
        return getCachePolicy().isSharedIsolation();
    }

    /**
     * INTERNAL:
     * Return if this descriptor has more than one table.
     */
    public boolean isMultipleTableDescriptor() {
        return getTables().size() > 1;
    }

    /**
     * INTERNAL:
     * Indicates whether pk or some of its components
     * set after insert into the database.
     * Shouldn't be called before ClassDescriptor has been initialized.
     */
    public boolean isPrimaryKeySetAfterInsert(AbstractSession session) {
        return (usesSequenceNumbers() && getSequence().shouldAcquireValueAfterInsert()) || (hasReturningPolicy() && getReturningPolicy().isUsedToSetPrimaryKey());
    }

    /**
     * ADVANCED:
     * When set to false, this setting will allow the UOW to avoid locking the shared cache instance in order to perform a clone.
     * Caution should be taken as setting this to false may allow cloning of partial updates
     */
    public boolean shouldLockForClone() {
        return this.shouldLockForClone;
    }

    /**
     * INTERNAL:
     * Return if change sets are required for new objects.
     */
    public boolean shouldUseFullChangeSetsForNewObjects() {
        return getCachePolicy().getCacheSynchronizationType() == CachePolicy.SEND_NEW_OBJECTS_WITH_CHANGES || shouldUseFullChangeSetsForNewObjects;
    }
    
    /**
     * PUBLIC:
     * This method is the equivalent of calling {@link #setShouldOnlyRefreshCacheIfNewerVersion} with an argument of <CODE>true</CODE>:
     * it configures a <CODE>ClassDescriptor</CODE> to only refresh the cache if the data received from the database by a query is newer than
     * the data in the cache (as determined by the optimistic locking field) and as long as one of the following is true:
     *
     * <UL>
     * <LI>the <CODE>ClassDescriptor</CODE> was configured by calling {@link #alwaysRefreshCache} or {@link #alwaysRefreshCacheOnRemote},</LI>
     * <LI>the query was configured by calling {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}, or</LI>
     * <LI>the query was a call to {@link org.eclipse.persistence.sessions.Session#refreshObject}</LI>
     * </UL>
     * <P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For example, by default,
     * when a query for a single object based on its primary key is executed, EclipseLink will first look in the cache for the object.
     * If the object is in the cache, the cached object is returned and data is not refreshed. To avoid cache hits, use
     * the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.
     *
     * @see #dontOnlyRefreshCacheIfNewerVersion
     */
    public void onlyRefreshCacheIfNewerVersion() {
        setShouldOnlyRefreshCacheIfNewerVersion(true);
    }

    /**
     * INTERNAL:
     * Post initializations after mappings are initialized.
     */
    public void postInitialize(AbstractSession session) throws DescriptorException {
        // These cached settings on the project must be set even if descriptor is initialized.
        if (getHistoryPolicy() != null) {
            session.getProject().setHasGenericHistorySupport(true);
        }
        
        // Avoid repetitive initialization (this does not solve loops)
        if (isInitialized(POST_INITIALIZED) || isInvalid()) {
            return;
        }

        setInitializationStage(POST_INITIALIZED);

        // Make sure that child is post initialized,
        // this initialize bottom up, unlike the two other phases that to top down.
        if (hasInheritance()) {
            for (ClassDescriptor child : getInheritancePolicy().getChildDescriptors()) {
                child.postInitialize(session);
            }
        }

        // Allow mapping to perform post initialization.
        for (DatabaseMapping mapping : getMappings()) {
            // This causes post init to be called multiple times in inheritance.
            mapping.postInitialize(session);
            // PERF: computed if deferred locking is required.
            if (!shouldAcquireCascadedLocks()) {
                if (mapping.isForeignReferenceMapping()){
                    if (!((ForeignReferenceMapping)mapping).usesIndirection()){
                        setShouldAcquireCascadedLocks(true);
                    }
                    hasRelationships = true;
                }
                if ((mapping instanceof AggregateObjectMapping)){
                    if (mapping.getReferenceDescriptor().shouldAcquireCascadedLocks()) {
                        setShouldAcquireCascadedLocks(true);
                    }
                    if (mapping.getReferenceDescriptor().hasRelationships()){
                        hasRelationships = true;
                    }
                }
            }
            if (getCachePolicy().isProtectedIsolation() &&
                    ((mapping.isForeignReferenceMapping() && !mapping.isCacheable())
                    || (mapping.isAggregateObjectMapping() && mapping.getReferenceDescriptor().hasNoncacheableMappings()))) {
                mapping.collectQueryParameters(this.foreignKeyValuesForCaching);
            }
            if (mapping.isLockableMapping()){
                getLockableMappings().add(mapping);
            }
            

        }

        if (hasInheritance()) {
            getInheritancePolicy().postInitialize(session);
        }

        //PERF: Ensure that the identical primary key fields are used to avoid equals.
        for (int index = (getPrimaryKeyFields().size() - 1); index >= 0; index--) {
            DatabaseField primaryKeyField = getPrimaryKeyFields().get(index);
            int fieldIndex = getFields().indexOf(primaryKeyField);

            // Aggregate/agg-collections may not have a mapping for pk field.
            if (fieldIndex != -1) {
                primaryKeyField = getFields().get(fieldIndex);
                getPrimaryKeyFields().set(index, primaryKeyField);
                primaryKeyField.setPrimaryKey(true);
            }
        }

        // List of fields selected by a query that uses SOP when descriptor has SOP. Used to index these fields.
        List<DatabaseField> sopSelectionFields = null;
        if (hasSerializedObjectPolicy()) {
            getSerializedObjectPolicy().postInitialize(session);
            this.selectionFields = (List<DatabaseField>)getFields().clone();
            this.selectionFields.remove(getSerializedObjectPolicy().getField());
            this.allSelectionFields = (List<DatabaseField>)getAllFields().clone();
            this.allSelectionFields.remove(getSerializedObjectPolicy().getField());
            sopSelectionFields = getSerializedObjectPolicy().getSelectionFields();
            if (sopSelectionFields.size() == getFields().size()) {
                // no need for sop field indexes - SOP uses all the field in the descriptor
                sopSelectionFields = null;
            }
        } else {
            this.selectionFields = getFields();
            this.allSelectionFields = getAllFields();
        }

        // Index and classify fields and primary key.
        // This is in post because it needs field classification defined in initializeMapping
        // this can come through a 1:1 so requires all descriptors to be initialized (mappings).
        // May 02, 2000 - Jon D.
        for (int index = 0; index < getFields().size(); index++) {
            DatabaseField field = getFields().elementAt(index);
            if (field.getType() == null){
                DatabaseMapping mapping = getObjectBuilder().getMappingForField(field);
                if (mapping != null) {
                    field.setType(mapping.getFieldClassification(field));
                }
            }
            // LOB may require lob writer which needs full row to fetch LOB.
            if ((field.getType() == ClassConstants.BLOB) || (field.getType() == ClassConstants.CLOB)) {
                setHasMultipleTableConstraintDependecy(true);
            }
            field.setIndex(index);
            if (sopSelectionFields != null) {
                int sopFieldIndex = sopSelectionFields.indexOf(field);
                if (sopFieldIndex != -1) {
                    field.setIndex(sopFieldIndex);
                }
            }
        }        
        
        // EL Bug 426500 - When a mapping has built its selection criteria early with partially 
        // initialized fields, post-initialize any source and target Expression fields.
        for (DatabaseMapping mapping : getMappings()) {
            mapping.postInitializeSourceAndTargetExpressions();
        }
        
        // Set cache key type.
        if (getCachePolicy().getCacheKeyType() == null || (getCachePolicy().getCacheKeyType() == CacheKeyType.AUTO)) {
            if ((getPrimaryKeyFields().size() > 1) || getObjectBuilder().isXMLObjectBuilder()) {
                setCacheKeyType(CacheKeyType.CACHE_ID);
            } else if ((getPrimaryKeyFields().size() == 1) && (getObjectBuilder().getPrimaryKeyClassifications().size() == 1)) {
                Class type = getObjectBuilder().getPrimaryKeyClassifications().get(0);
                if ((type == null) || type.isArray()) {
                    getCachePolicy().setCacheKeyType(CacheKeyType.CACHE_ID);
                } else {
                    getCachePolicy().setCacheKeyType(CacheKeyType.ID_VALUE);
                }
            } else {
                getCachePolicy().setCacheKeyType(CacheKeyType.CACHE_ID);                
            }
        } else if ((getCachePolicy().getCacheKeyType() == CacheKeyType.ID_VALUE) && (getPrimaryKeyFields().size() > 1)) {
            session.getIntegrityChecker().handleError(DescriptorException.cannotUseIdValueForCompositeId(this));
        }
        if (hasFetchGroupManager()) {
            getFetchGroupManager().postInitialize(session);
        }
        getObjectBuilder().postInitialize(session);
        getQueryManager().postInitialize(session);
        
        // Post initialize the multitenant policy after the query manager.
        if (hasMultitenantPolicy()) {
            getMultitenantPolicy().postInitialize(session);
        }
        
        getCachePolicy().postInitialize(this, session);
        
        validateAfterInitialization(session);

        checkDatabase(session);
    }

    /**
     * INTERNAL:
     * Configure all descriptors referencing this class to be protected and update their cache settings.
     */
    public void notifyReferencingDescriptorsOfIsolation(AbstractSession session) {
        for (ClassDescriptor descriptor : this.referencingClasses){
            if (descriptor.getCachePolicy().getCacheIsolation() == null || descriptor.getCachePolicy().getCacheIsolation() == CacheIsolationType.SHARED) {
                descriptor.getCachePolicy().setCacheIsolation(CacheIsolationType.PROTECTED);
                descriptor.getCachePolicy().postInitialize(descriptor, session);
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (mapping.isAggregateMapping() && (mapping.getReferenceDescriptor() != null)) {
                        mapping.getReferenceDescriptor().getCachePolicy().setCacheIsolation(CacheIsolationType.PROTECTED);
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Allow the descriptor to initialize any dependencies on this session.
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        // Avoid repetitive initialization (this does not solve loops)
        if (isInitialized(PREINITIALIZED)) {
            return;
        }
        setInitializationStage(PREINITIALIZED);
                
        assignDefaultValues(session);

        if (this.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables && !session.getPlatform().supportsDeleteOnCascade()) {
            this.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables = false;
        }
        // Set the fetchgroup manager is the class implements the tracking interface.
        if (FetchGroupTracker.class.isAssignableFrom(getJavaClass())) {
            if (getFetchGroupManager() == null && !isAggregateDescriptor()) {
                //aggregate descriptors will set fetchgroupmanager during mapping init.
                setFetchGroupManager(new FetchGroupManager());
            }
        }
        // PERF: Check if the class "itself" was weaved.
        // If weaved avoid reflection, use clone copy and empty new.
        if (Arrays.asList(getJavaClass().getInterfaces()).contains(PersistenceObject.class)) {
            // Cloning is only auto set for field access, as method access
            // may not have simple fields, same with empty new and reflection get/set.
            boolean isMethodAccess = false;
            for (Iterator iterator = getMappings().iterator(); iterator.hasNext(); ) {
                DatabaseMapping mapping = (DatabaseMapping)iterator.next();
                if (mapping.isUsingMethodAccess()) {
                    // Ok for lazy 1-1s
                    if (!mapping.isOneToOneMapping() || !((ForeignReferenceMapping)mapping).usesIndirection()) {
                        isMethodAccess = true;
                    }
                } else if (!mapping.isWriteOnly()) {
                    // Avoid reflection.
                    mapping.setAttributeAccessor(new PersistenceObjectAttributeAccessor(mapping.getAttributeName()));
                }
            }
            if (!isMethodAccess) {
                if (this.copyPolicy == null) {
                    setCopyPolicy(new PersistenceEntityCopyPolicy());
                }
                if (!isAbstract()) {
                    try {
                        if (this.instantiationPolicy == null) {
                            setInstantiationPolicy(new PersistenceObjectInstantiationPolicy((PersistenceObject)getJavaClass().newInstance()));
                        }
                    } catch (Exception ignore) { }
                }
            }
        }
        
        // 4924665 Check for spaces in table names, and add the appropriate quote character
        Iterator tables = this.getTables().iterator();
        while(tables.hasNext()) {
            DatabaseTable next = (DatabaseTable)tables.next();
            if(next.getName().indexOf(' ') != -1) {
                // EL Bug 382420 - set use delimiters to true if table name contains a space
                next.setUseDelimiters(true);
            }
        }
        
        // Allow mapping pre init, must be done before validate.
        for (DatabaseMapping mapping : getMappings()) {
            try {
                mapping.preInitialize(session);
            } catch (DescriptorException exception) {
                session.getIntegrityChecker().handleError(exception);
            }
        }

        validateBeforeInitialization(session);

        preInitializeInheritancePolicy(session);
        
        // Make sure that parent is already preinitialized
        if (hasInheritance()) {
            // The default table will be set in this call once the duplicate
            // tables have been removed.
            getInheritancePolicy().preInitialize(session);
        } else {
            // This must be done now, after validate, before init anything else.
            setInternalDefaultTable();
        }
        
        // Once the table and mapping information has been settled, we'll need 
        // to set tenant id fields on the descriptor for each table. These are 
        // at least used for DDL generation. Doesn't seem to interfere or 
        // duplicate anything else we have done to support tenant id fields.
        if (hasMultitenantPolicy()) {
            getMultitenantPolicy().preInitialize(session);
        }
        
        verifyTableQualifiers(session.getDatasourcePlatform());
        initializeProperties(session);
        if (!isAggregateDescriptor()) {
            // Adjust before you initialize ...
            adjustMultipleTableInsertOrder();
            initializeMultipleTablePrimaryKeyFields();
        }

        if (hasInterfacePolicy()) {
            preInterfaceInitialization(session);
        }

        getQueryManager().preInitialize(session);

    }

    /**
     * INTERNAL:
     */
    protected void prepareCascadeLockingPolicy(DatabaseMapping mapping) {
        if (mapping.isPrivateOwned() && mapping.isForeignReferenceMapping()) {
            if (mapping.isCascadedLockingSupported()) {
                // Even if the mapping says it is supported in general, there 
                // may be conditions where it is not. Need the following checks.
                if (((ForeignReferenceMapping)mapping).hasCustomSelectionQuery()) {
                    throw ValidationException.unsupportedCascadeLockingMappingWithCustomQuery(mapping);
                } else if (isDescriptorTypeAggregate()) {
                    throw ValidationException.unsupportedCascadeLockingDescriptor(this);
                } else {
                    mapping.prepareCascadeLockingPolicy();
                }
            } else {
                throw ValidationException.unsupportedCascadeLockingMapping(mapping);
            }
        }
    }

    /**
     * Hook together the inheritance policy tree.
     */
    protected void preInitializeInheritancePolicy(AbstractSession session) throws DescriptorException {
        if (isChildDescriptor() && (requiresInitialization(session))) {
            if (getInheritancePolicy().getParentClass().equals(getJavaClass())) {
                setInterfaceInitializationStage(ERROR);
                throw DescriptorException.parentClassIsSelf(this);
            }
            ClassDescriptor parentDescriptor = session.getDescriptor(getInheritancePolicy().getParentClass());
            parentDescriptor.getInheritancePolicy().addChildDescriptor(this);
            getInheritancePolicy().setParentDescriptor(parentDescriptor);
            parentDescriptor.preInitialize(session);
        }
    }

    /**
     * INTERNAL:
     * Allow the descriptor to initialize any dependencies on this session.
     */
    public void preInterfaceInitialization(AbstractSession session) throws DescriptorException {
        if (isInterfaceInitialized(PREINITIALIZED)) {
            return;
        }

        setInterfaceInitializationStage(PREINITIALIZED);

        assignDefaultValues(session);
        
        if (isInterfaceChildDescriptor()) {
            for (Iterator<Class> interfaces = getInterfacePolicy().getParentInterfaces().iterator();
                     interfaces.hasNext();) {
                Class parentInterface = interfaces.next();
                ClassDescriptor parentDescriptor = session.getDescriptor(parentInterface);
                if ((parentDescriptor == null) || (parentDescriptor.getJavaClass() == getJavaClass()) || parentDescriptor.getInterfacePolicy().usesImplementorDescriptor()) {
                    session.getProject().getDescriptors().put(parentInterface, this);
                    session.clearLastDescriptorAccessed();
                } else if (!parentDescriptor.isDescriptorForInterface()) {
                    throw DescriptorException.descriptorForInterfaceIsMissing(parentInterface.getName());
                } else {
                    parentDescriptor.preInterfaceInitialization(session);
                    parentDescriptor.getInterfacePolicy().addChildDescriptor(this);
                    getInterfacePolicy().addParentDescriptor(parentDescriptor);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Rehash any hashtables based on fields.
     * This is used to clone descriptors for aggregates, which hammer field names,
     * it is probably better not to hammer the field name and this should be refactored.
     */
    public void rehashFieldDependancies(AbstractSession session) {
        getObjectBuilder().rehashFieldDependancies(session);

        for (Enumeration enumtr = getMappings().elements(); enumtr.hasMoreElements();) {
            ((DatabaseMapping)enumtr.nextElement()).rehashFieldDependancies(session);
        }
    }

    /**
     * INTERNAL:
     * A user should not be setting which attributes to join or not to join
     * after descriptor initialization; provided only for backwards compatibility.
     */
    public void reInitializeJoinedAttributes() {
        if (!isInitialized(POST_INITIALIZED)) {
            // wait until the descriptor gets initialized first
            return;
        }
        getObjectBuilder().initializeJoinedAttributes();
        if (hasInheritance()) {
            for (ClassDescriptor child : getInheritancePolicy().getChildDescriptors()) {
                child.reInitializeJoinedAttributes(); 
            }
        }
    }

    /**
     * INTERNAL:
     * Used to initialize a remote descriptor.
     */
    public void remoteInitialization(DistributedSession session) {
        // These cached settings on the project must be set even if descriptor is initialized.
        if (getHistoryPolicy() != null) {
            session.getProject().setHasGenericHistorySupport(true);
        }

        // Record that there is an isolated class in the project.
        if (!getCachePolicy().isSharedIsolation()) {
            session.getProject().setHasIsolatedClasses(true);
        }
        if (!getCachePolicy().shouldIsolateObjectsInUnitOfWork() && !shouldBeReadOnly()) {
            session.getProject().setHasNonIsolatedUOWClasses(true);
        }
        
        for (DatabaseMapping mapping : getMappings()) {
            mapping.remoteInitialization(session);
        }

        getEventManager().remoteInitialization(session);
        getInstantiationPolicy().initialize(session);
        getCopyPolicy().initialize(session);

        if (hasInheritance()) {
            getInheritancePolicy().remoteInitialization(session);
        }

        if (getCMPPolicy() != null) {
            getCMPPolicy().remoteInitialize(this, session);
        }
    }

    /**
     * PUBLIC:
     * Remove the user defined property.
     */
    public void removeProperty(String property) {
        getProperties().remove(property);
    }

    /**
     * INTERNAL:
     * Aggregate and Interface descriptors do not require initialization as they are cloned and
     * initialized by each mapping. Descriptors with table per tenant policies are cloned per
     * client session (per tenant) so do not initialize the original descriptor.
     */
    public boolean requiresInitialization(AbstractSession session) {
        // If we are an aggregate or interface descriptor we do not require initialization.
        if (isDescriptorTypeAggregate() || isDescriptorForInterface()) {
            return false;
        }
        
        // If we have a table per tenant policy then check for our tenant 
        // context property. If it is available from the session, set it and
        // return true to initialize. Otherwise do not initialize the
        // descriptor (it will be initialized per client session).
        if (hasTablePerMultitenantPolicy()) {
            return ((TablePerMultitenantPolicy) getMultitenantPolicy()).shouldInitialize(session);
        }
        
        // By default it should be initialized.
        return true;
    }
    
    /**
     * INTERNAL:
     * Validate that the descriptor was defined correctly.
     * This allows for checks to be done that require the descriptor initialization to be completed.
     */
    protected void selfValidationAfterInitialization(AbstractSession session) throws DescriptorException {
        // This has to be done after, because read subclasses must be initialized.
        if ( (hasInheritance() && (getInheritancePolicy().shouldReadSubclasses() || isAbstract())) || hasTablePerClassPolicy() && isAbstract() ) {
            // Avoid building a new instance if the inheritance class is abstract.
            // There is an empty statement here, and this was done if anything for the 
            // readability sake of the statement logic.
        } else if (session.getIntegrityChecker().shouldCheckInstantiationPolicy()) {
            getInstantiationPolicy().buildNewInstance();
        }

        if (hasReturningPolicy()) {
            getReturningPolicy().validationAfterDescriptorInitialization(session);
        }
        getObjectBuilder().validate(session);
    }

    /**
     * INTERNAL:
     * Validate that the descriptor's non-mapping attribute are defined correctly.
     */
    protected void selfValidationBeforeInitialization(AbstractSession session) throws DescriptorException {
        if (isChildDescriptor()) {
            ClassDescriptor parentDescriptor = session.getDescriptor(getInheritancePolicy().getParentClass());

            if (parentDescriptor == null) {
                session.getIntegrityChecker().handleError(DescriptorException.parentDescriptorNotSpecified(getInheritancePolicy().getParentClass().getName(), this));
            }
        } else {
            if (getTables().isEmpty() && (!isAggregateDescriptor())) {
                session.getIntegrityChecker().handleError(DescriptorException.tableNotSpecified(this));
            }
        }

        if (!isChildDescriptor() && !isDescriptorTypeAggregate()) {
            if (getPrimaryKeyFieldNames().isEmpty()) {
                session.getIntegrityChecker().handleError(DescriptorException.primaryKeyFieldsNotSepcified(this));
            }
        }

        if ((getIdentityMapClass() == ClassConstants.NoIdentityMap_Class) && (getQueryManager().getDoesExistQuery().shouldCheckCacheForDoesExist())) {
            session.getIntegrityChecker().handleError(DescriptorException.identityMapNotSpecified(this));
        }

        if (((getSequenceNumberName() != null) && (getSequenceNumberField() == null)) || ((getSequenceNumberName() == null) && (getSequenceNumberField() != null))) {
            session.getIntegrityChecker().handleError(DescriptorException.sequenceNumberPropertyNotSpecified(this));
        }
    }

    /**
     * INTERNAL:
     * This is used to map the primary key field names in a multiple table
     * descriptor.
     */
    protected void setAdditionalTablePrimaryKeyFields(DatabaseTable table, DatabaseField field1, DatabaseField field2) {
        Map tableAdditionalPKFields = getAdditionalTablePrimaryKeyFields().get(table);

        if (tableAdditionalPKFields == null) {
            tableAdditionalPKFields = new HashMap(2);
            getAdditionalTablePrimaryKeyFields().put(table, tableAdditionalPKFields);
        }

        tableAdditionalPKFields.put(field1, field2);
    }

    /**
     * INTERNAL:
     * Eclipselink needs additionalTablePKFields entries to be associated with tables other than the main (getTables.get(0)) one.
     * Also in case of two non-main tables additionalTablePKFields entry should be associated with the one
     * father down insert order. 
     */
    protected void toggleAdditionalTablePrimaryKeyFields() {
        if(additionalTablePrimaryKeyFields == null) {
            // nothing to do
            return;
        }

        // nProcessedTables is a number of tables (first in egtTables() order) that don't require toggle - to, but may be toggled - from
        // (meaning by "toggle - to" table:    setAdditionalTablePrimaryKeyFields(table, .., ..);)
        // "Processed" tables always include the main table (getTables().get(0)) plus all the inherited tables.
        // Don't toggle between processed tables (that has been already done by the parent);
        // always toggle from processed to non-processed;
        // toggle between two non-processed to the one that is father down insert order.
        int nProcessedTables = 1;
        if (isChildDescriptor()) {
            nProcessedTables = getInheritancePolicy().getParentDescriptor().getTables().size();
            // if this is multiple table inheritance, we should include the table for this child in the processed tables
            if (getTables().size() > nProcessedTables){
                nProcessedTables++;
            }
        }

        // cache the original map in a new variable 
        Map<DatabaseTable, Map<DatabaseField, DatabaseField>> additionalTablePrimaryKeyFieldsOld = additionalTablePrimaryKeyFields;
        // nullify the original map variable - it will be re-created from scratch
        additionalTablePrimaryKeyFields = null;
        Iterator<Map.Entry<DatabaseTable, Map<DatabaseField, DatabaseField>>> itTable = additionalTablePrimaryKeyFieldsOld.entrySet().iterator();
        // loop through the cached original map and add all its entries (either toggled or unchanged) to the re-created map 
        while(itTable.hasNext()) {
            Map.Entry<DatabaseTable, Map<DatabaseField, DatabaseField>> entryTable = itTable.next();
            DatabaseTable sourceTable = entryTable.getKey();
            boolean isSourceProcessed = getTables().indexOf(sourceTable) < nProcessedTables;
            int sourceInsertOrderIndex = getMultipleTableInsertOrder().indexOf(sourceTable);
            Map<DatabaseField, DatabaseField> targetTableAdditionalPKFields = entryTable.getValue(); 
            Iterator<Map.Entry<DatabaseField, DatabaseField>> itField = targetTableAdditionalPKFields.entrySet().iterator();
            while(itField.hasNext()) {
                Map.Entry<DatabaseField, DatabaseField> entryField = itField.next();
                DatabaseField targetField = entryField.getKey();
                DatabaseField sourceField = entryField.getValue();
                DatabaseTable targetTable = targetField.getTable();
                boolean isTargetProcessed = getTables().indexOf(targetTable) < nProcessedTables;
                int targetInsertOrderIndex = getMultipleTableInsertOrder().indexOf(targetTable);
                // add the entry to the map
                if(!isTargetProcessed && (isSourceProcessed || (sourceInsertOrderIndex > targetInsertOrderIndex))) {
                    // source and target toggled
                    setAdditionalTablePrimaryKeyFields(targetTable, sourceField, targetField);
                } else {
                    // exactly the same as in the original map
                    setAdditionalTablePrimaryKeyFields(sourceTable, targetField, sourceField);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * This is used to map the primary key field names in a multiple table
     * descriptor.
     */
    public void setAdditionalTablePrimaryKeyFields(Map<DatabaseTable, Map<DatabaseField, DatabaseField>> additionalTablePrimaryKeyFields) {
        this.additionalTablePrimaryKeyFields = additionalTablePrimaryKeyFields;
    }

    /**
     * PUBLIC:
     * Set the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * INTERNAL:
     * Set all the fields.
     */
    protected void setAllFields(Vector<DatabaseField> allFields) {
        this.allFields = allFields;
    }

    /**
     * PUBLIC:
     * Set the amendment class.
     * The amendment method will be called on the class before initialization to allow for it to initialize the descriptor.
     * The method must be a public static method on the class.
     */
    public void setAmendmentClass(Class amendmentClass) {
        this.amendmentClass = amendmentClass;
    }

    /**
     * INTERNAL:
     * Return the amendment class name, used by the MW.
     */
    public void setAmendmentClassName(String amendmentClassName) {
        this.amendmentClassName = amendmentClassName;
    }

    /**
     * PUBLIC:
     * Set the amendment method.
     * This will be called on the amendment class before initialization to allow for it to initialize the descriptor.
     * The method must be a public static method on the class.
     */
    public void setAmendmentMethodName(String amendmentMethodName) {
        this.amendmentMethodName = amendmentMethodName;
    }

    /**
     * @param accessorTree the accessorTree to set
     */
    public void setAccessorTree(List<AttributeAccessor> accessorTree) {
        this.accessorTree = accessorTree;
    }

    /**
     * PUBLIC:
     * Set the type of cache synchronization that will be used on objects of this type.  Possible values
     * are:
     * SEND_OBJECT_CHANGES
     * INVALIDATE_CHANGED_OBJECTS
     * SEND_NEW_OBJECTS_WITH_CHANGES
     * DO_NOT_SEND_CHANGES
     * Note: Cache Synchronization type cannot be altered for descriptors that are set as isolated using
     * the setIsIsolated method.
     * @param type int  The synchronization type for this descriptor
     *
     */
    public void setCacheSynchronizationType(int type) {
        getCachePolicy().setCacheSynchronizationType(type);
    }

    /**
     * PUBLIC:
     * Set the ObjectChangePolicy for this descriptor.
     */
    public void setObjectChangePolicy(ObjectChangePolicy policy) {
        this.changePolicy = policy;
    }

    /**
     * PUBLIC:
     * Set the HistoryPolicy for this descriptor.
     */
    public void setHistoryPolicy(HistoryPolicy policy) {
        this.historyPolicy = policy;
        if (policy != null) {
            policy.setDescriptor(this);
        }
    }

    /**
     * PUBLIC:
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.

     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public void setCacheInterceptorClass(Class cacheInterceptorClass) {
        getCachePolicy().setCacheInterceptorClass(cacheInterceptorClass);
    }

    /**
     * PUBLIC:
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.

     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public void setCacheInterceptorClassName(String cacheInterceptorClassName) {
        getCachePolicy().setCacheInterceptorClassName(cacheInterceptorClassName);
    }
    
    /**
     * PUBLIC:
     * Set the Cache Invalidation Policy for this descriptor.
     * @see org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy
     */
    public void setCacheInvalidationPolicy(CacheInvalidationPolicy policy) {
        cacheInvalidationPolicy = policy;
    }

    /**
     * ADVANCED:
     *  automatically orders database access through the foreign key information provided in 1:1 and 1:m mappings.
     * In some case when 1:1 are not defined it may be required to tell the descriptor about a constraint,
     * this defines that this descriptor has a foreign key constraint to another class and must be inserted after
     * instances of the other class.
     */
    public void setConstraintDependencies(Vector constraintDependencies) {
        this.constraintDependencies = constraintDependencies;
    }

    /**
     * INTERNAL:
     * Set the copy policy.
     * This would be 'protected' but the EJB stuff in another
     * package needs it to be public
     */
    public void setCopyPolicy(CopyPolicy policy) {
        copyPolicy = policy;
        if (policy != null) {
            policy.setDescriptor(this);
        }
    }
    
    /**
     * INTERNAL:
     * Sets the name of a Class that implements CopyPolicy
     * Will be instantiatied as a copy policy at initialization times
     * using the no-args constructor
     */
    public void setCopyPolicyClassName(String className) {
        copyPolicyClassName = className;
    }
    
    /**
     * INTERNAL:
     * The descriptors default table can be configured if the first table is not desired.
     */
    public void setDefaultTable(DatabaseTable defaultTable) {
        this.defaultTable = defaultTable;
    }

    /**
     * PUBLIC:
     * The descriptors default table can be configured if the first table is not desired.
     */
    public void setDefaultTableName(String defaultTableName) {
        setDefaultTable(new DatabaseTable(defaultTableName));
    }

    /**
     * INTERNAL:
     * Sets the JPA DescriptorCustomizer class name.
     * DescriptorCustomizer is the JPA equivalent of an amendment method.
     */
    public void setDescriptorCustomizerClassName(String descriptorCustomizerClassName) {
        this.descriptorCustomizerClassName = descriptorCustomizerClassName;
    }

    /**
     * ADVANCED:
     * set the descriptor type (NORMAL by default, others include INTERFACE, AGGREGATE, AGGREGATE COLLECTION)
     */
    public void setDescriptorType(int descriptorType) {
        this.descriptorType = descriptorType;
    }

    /**
     * INTERNAL:
     * This method is explicitly used by the XML reader.
     */
    public void setDescriptorTypeValue(String value) {
        if (value.equals("Aggregate collection")) {
            descriptorIsAggregateCollection();
        } else if (value.equals("Aggregate")) {
            descriptorIsAggregate();
        } else if (value.equals("Interface")) {
            descriptorIsForInterface();
        } else {
            descriptorIsNormal();
        }
    }

  
    /**
     * INTERNAL:
     * Set the event manager for the descriptor.  The event manager is responsible
     * for managing the pre/post selectors.
     */
    @Override
    public void setEventManager(DescriptorEventManager eventManager) {
        this.eventManager = eventManager;
        if (eventManager != null) {
            eventManager.setDescriptor(this);
        }
    }

    /**
     * INTERNAL:
     * OBSOLETE - old reader.
     * This method is explicitly used by the Builder only.
     */
    public void setExistenceChecking(String token) throws DescriptorException {
        getQueryManager().setExistenceCheck(token);
    }

    /**
     * INTERNAL:
     * Set the fields used by this descriptor.
     */
    public void setFields(Vector<DatabaseField> fields) {
        this.fields = fields;
    }
    
    /**
     * INTERNAL:
     * This method is used by the  XML Deployment ClassDescriptor to read and write these mappings
     */
    public void setForeignKeyFieldNamesForMultipleTable(Vector associations) throws DescriptorException {
        Enumeration foreignKeys = associations.elements();
        
        while (foreignKeys.hasMoreElements()) {
            Association association = (Association) foreignKeys.nextElement();
            addForeignKeyFieldNameForMultipleTable((String) association.getKey(), (String) association.getValue());
        }
    }

    /**
     * @param fullyMergeEntity the fullyMergeEntity to set
     */
    public void setFullyMergeEntity(boolean fullyMergeEntity) {
        getCachePolicy().setFullyMergeEntity(fullyMergeEntity);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be used by this descriptor.
     * The default is the "FullIdentityMap".
     */
    public void setIdentityMapClass(Class theIdentityMapClass) {
        getCachePolicy().setIdentityMapClass(theIdentityMapClass);
    }

    /**
     * PUBLIC:
     * Set the size of the identity map to be used by this descriptor.
     * The default is the 100.
     */
    public void setIdentityMapSize(int identityMapSize) {
        getCachePolicy().setIdentityMapSize(identityMapSize);
    }

    /**
     * INTERNAL:
     * Sets the inheritance policy.
     */
    @Override
    public void setInheritancePolicy(InheritancePolicy inheritancePolicy) {
        this.inheritancePolicy = inheritancePolicy;
        if (inheritancePolicy != null) {
            inheritancePolicy.setDescriptor(this);
        }
    }

    /**
     * PUBLIC:
     * Sets the returning policy.
     */
    public void setReturningPolicy(ReturningPolicy returningPolicy) {
        this.returningPolicy = returningPolicy;
        if (returningPolicy != null) {
            returningPolicy.setDescriptor(this);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setInitializationStage(int initializationStage) {
        this.initializationStage = initializationStage;
    }

    /**
     * INTERNAL:
     * Sets the instantiation policy.
     */
    @Override
    public void setInstantiationPolicy(InstantiationPolicy instantiationPolicy) {
        this.instantiationPolicy = instantiationPolicy;
        if (instantiationPolicy != null) {
            instantiationPolicy.setDescriptor(this);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setInterfaceInitializationStage(int interfaceInitializationStage) {
        this.interfaceInitializationStage = interfaceInitializationStage;
    }

    /**
     * INTERNAL:
     * Sets the interface policy.
     */
    public void setInterfacePolicy(InterfacePolicy interfacePolicy) {
        this.interfacePolicy = interfacePolicy;
        if (interfacePolicy != null) {
            interfacePolicy.setDescriptor(this);
        }
    }
    
    /**
     * INTERNAL:
     * Set the default table if one if not already set. This method will
     * extract the default table.
     */
    public void setInternalDefaultTable() {
        if (getDefaultTable() == null) {
            setDefaultTable(extractDefaultTable());
        }
    }
    
    /**
     * INTERNAL:
     * Set the default table if one if not already set. This method will set
     * the table that is provided as the default.
     */
    public void setInternalDefaultTable(DatabaseTable defaultTable) {
        if (getDefaultTable() == null) {
            setDefaultTable(defaultTable);
        }
    }

    /**
     * PUBLIC:
     * Used to set if the class that this descriptor represents should be isolated from the shared cache.
     * Isolated objects will only be cached locally in the ClientSession, never in the ServerSession cache.
     * This is the best method for disabling caching.
     * Note: Calling this method with true will also set the cacheSynchronizationType to DO_NOT_SEND_CHANGES
     * since isolated objects cannot be sent by  cache synchronization.
     * 
     * @deprecated as of EclipseLink 2.2
     * @see #setCacheIsolation(CacheIsolationType)
     */
    @Deprecated
    public void setIsIsolated(boolean isIsolated) {
        getCachePolicy().setCacheIsolation( isIsolated ? CacheIsolationType.ISOLATED : CacheIsolationType.SHARED);
    }

    /**
     * INTERNAL:
     * Set entity @Cacheable annotation value in cache configuration object.
     * @param cacheable Entity @Cacheable annotation value for current class
     *        or <code>null</code> if @Cacheable annotation is not set. Parent
     *        values are ignored, value shall refer to current class only.
     *        This value should be set only when SharedCacheMode allows
     *        to override caching on entity level (DISABLE_SELECTIVE
     *        or ENABLE_SELECTIVE).
     */
    public void setCacheable(Boolean cacheable) {
        getCachePolicy().setCacheable(cacheable);
    }

    /**
     * PUBLIC:
     * Controls how the Entity instances will be cached.  See the CacheIsolationType for details on the options.
     * @return the isolationType
     */
    public CacheIsolationType getCacheIsolation() {
        return getCachePolicy().getCacheIsolation();
    }

    /**
     * PUBLIC:
     * Controls how the Entity instances and data will be cached.  See the CacheIsolationType for details on the options.
     * To disable all second level caching simply set CacheIsolationType.ISOLATED.  Note that setting the isolation
     * will automatically set the corresponding cacheSynchronizationType.   
     * ISOLATED = DO_NOT_SEND_CHANGES, PROTECTED and SHARED = SEND_OBJECT_CHANGES
     */
    public void setCacheIsolation(CacheIsolationType isolationType) {
        getCachePolicy().setCacheIsolation(isolationType);
    }

    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the session cache.
     * Objects will be built in the unit of work, and never merged into the session cache.
     */
    public boolean shouldIsolateObjectsInUnitOfWork() {
        return getCachePolicy().shouldIsolateObjectsInUnitOfWork();
    }
          
    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the IsolatedSession cache.
     * Objects will be built/merged into the unit of work and into the session cache.
     * but not built/merge into the IsolatedClientSession cache.
     */
    public boolean shouldIsolateProtectedObjectsInUnitOfWork() {
        return getCachePolicy().shouldIsolateProtectedObjectsInUnitOfWork();
    }
          
    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the session cache after an early transaction.
     */
    public boolean shouldIsolateObjectsInUnitOfWorkEarlyTransaction() {
        return getCachePolicy().shouldIsolateObjectsInUnitOfWorkEarlyTransaction();
    }
              
    /**
     * INTERNAL:
     * Return if the unit of work should use the session cache after an early transaction.
     */
    public boolean shouldUseSessionCacheInUnitOfWorkEarlyTransaction() {
        return getCachePolicy().shouldUseSessionCacheInUnitOfWorkEarlyTransaction();
    }
    
    /**
     * INTERNAL:
     * Used to store un-converted properties, which are subsequenctly converted
     * at runtime (through the convertClassNamesToClasses method.
     */
    public Map<String, List<String>> getUnconvertedProperties() {
        if (unconvertedProperties == null) {
            unconvertedProperties = new HashMap<String, List<String>>(5);
        }
        
        return unconvertedProperties;
    }
    
    /**
     * ADVANCED:
     * Return the unit of work cache isolation setting.
     * This setting configures how the session cache will be used in a unit of work.
     * @see #setUnitOfWorkCacheIsolationLevel(int)
     */
    public int getUnitOfWorkCacheIsolationLevel() {
        return getCachePolicy().getUnitOfWorkCacheIsolationLevel();
    }
    
    /**
     * ADVANCED:
     * This setting configures how the session cache will be used in a unit of work.
     * Most of the options only apply to a unit of work in an early transaction,
     * such as a unit of work that was flushed (writeChanges), issued a modify query, or acquired a pessimistic lock.
     * <p> USE_SESSION_CACHE_AFTER_TRANSACTION - Objects built from new data accessed after a unit of work early transaction are stored in the session cache.
     * This options is the most efficient as it allows the cache to be used after an early transaction. 
     * This should only be used if it is known that this class is not modified in the transaction,
     * otherwise this could cause uncommitted data to be loaded into the session cache.
     * ISOLATE_NEW_DATA_AFTER_TRANSACTION - Default (when using caching): Objects built from new data accessed after a unit of work early transaction are only stored in the unit of work.
     * This still allows previously cached objects to be accessed in the unit of work after an early transaction,
     * but ensures uncommitted data will never be put in the session cache by storing any object built from new data only in the unit of work.
     * ISOLATE_CACHE_AFTER_TRANSACTION - After a unit of work early transaction the session cache is no longer used for this class.
     * Objects will be directly built from the database data and only stored in the unit of work, even if previously cached.
     * Note that this may lead to poor performance as the session cache is bypassed after an early transaction.
     * ISOLATE_CACHE_ALWAYS - Default (when using isolated cache): The session cache will never be used for this class.
     * Objects will be directly built from the database data and only stored in the unit of work.
     * New objects and changes will also never be merged into the session cache.
     * Note that this may lead to poor performance as the session cache is bypassed,
     * however if this class is isolated or pessimistic locked and always accessed in a transaction, this can avoid having to build two copies of the object.
     */
    public void setUnitOfWorkCacheIsolationLevel(int unitOfWorkCacheIsolationLevel) {
        getCachePolicy().setUnitOfWorkCacheIsolationLevel(unitOfWorkCacheIsolationLevel);
    }

    /**
     * INTERNAL:
     * set whether this descriptor has any relationships through its mappings, through inheritance, or through aggregates 
     * @param hasRelationships
     */
    public void setHasRelationships(boolean hasRelationships) {
        this.hasRelationships = hasRelationships;
    }

    /**
    * PUBLIC:
    * Set the Java class that this descriptor maps.
    * Every descriptor maps one and only one class.
    */
    @Override
    public void setJavaClass(Class theJavaClass) {
        javaClass = theJavaClass;
    }

    /**
     * INTERNAL:
     * Return the java class name, used by the MW.
     */
    public void setJavaClassName(String theJavaClassName) {
        javaClassName = theJavaClassName;
    }

    /**
     * PUBLIC:
     * Sets the descriptor to be for an interface.
     * An interface descriptor allows for other classes to reference an interface or one of several other classes.
     * The implementor classes can be completely unrelated in term of the database stored in distinct tables.
     * Queries can also be done for the interface which will query each of the implementor classes.
     * An interface descriptor cannot define any mappings as an interface is just API and not state,
     * a interface descriptor should define the common query key of its implementors to allow querying.
     * An interface descriptor also does not define a primary key or table or other settings.
     * If an interface only has a single implementor (i.e. a classes public interface or remote) then an interface
     * descriptor should not be defined for it and relationships should be to the implementor class not the interface,
     * in this case the implementor class can add the interface through its interface policy to map queries on the interface to it.
     */
    public void setJavaInterface(Class theJavaInterface) {
        javaClass = theJavaInterface;
        descriptorIsForInterface();
    }

    /**
     * INTERNAL:
     * Return the java interface name, used by the MW.
     */
    public void setJavaInterfaceName(String theJavaInterfaceName) {
        javaClassName = theJavaInterfaceName;
        descriptorIsForInterface();
    }

    /**
     * INTERNAL:
     * Set the list of lockable mappings for this project
     * This method is provided for CMP use.  Normally, the lockable mappings are initialized
     * at descriptor initialization time.
     */
    public void setLockableMappings(List<DatabaseMapping> lockableMappings) {
        this.lockableMappings = lockableMappings;
    }

    /**
     * INTERNAL:
     * Set the mappings.
     */
    public void setMappings(Vector<DatabaseMapping> mappings) {
        // This is used from XML reader so must ensure that all mapping's descriptor has been set.
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();

            // For CR#2646, if the mapping already points to the parent descriptor then leave it.
            if (mapping.getDescriptor() == null) {
                mapping.setDescriptor(this);
            }
        }
        this.mappings = mappings;
    }

    /**
     * INTERNAL:
     *
     * @see #getMultipleTableForeignKeys
     */
    protected void setMultipleTableForeignKeys(Map<DatabaseTable, Set<DatabaseTable>> newValue) {
        this.multipleTableForeignKeys = newValue;
    }

    /**
     * ADVANCED:
     * Sets the List of DatabaseTables in the order which INSERTS should take place.
     * This is normally computed correctly by , however in advanced cases in it may be overridden.
     */
    public void setMultipleTableInsertOrder(List<DatabaseTable> newValue) {
        this.multipleTableInsertOrder = newValue;
    }

    /**
     * INTERNAL:
     * Set a multitenant policy on the descriptor.
     */
    public void setMultitenantPolicy(MultitenantPolicy multitenantPolicy) {
        this.multitenantPolicy = multitenantPolicy;
    }
    
    /**
     * ADVANCED:
     * Return if delete cascading has been set on the database for the descriptor's
     * multiple tables.
     */
    public boolean isCascadeOnDeleteSetOnDatabaseOnSecondaryTables() {
        return isCascadeOnDeleteSetOnDatabaseOnSecondaryTables;
    }

    /**
     * ADVANCED:
     * Set if delete cascading has been set on the database for the descriptor's
     * multiple tables.
     * This will avoid the delete SQL being generated for those tables.
     */
    public void setIsCascadeOnDeleteSetOnDatabaseOnSecondaryTables(boolean isCascadeOnDeleteSetOnDatabaseOnSecondaryTables) {
        this.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables = isCascadeOnDeleteSetOnDatabaseOnSecondaryTables;
    }
    
    /**
     * INTERNAL:
     * Set the ObjectBuilder.
     */
    @Override
    protected void setObjectBuilder(ObjectBuilder builder) {
        objectBuilder = builder;
    }

    /**
     * PUBLIC:
     * Set the OptimisticLockingPolicy.
     * This can be one of the provided locking policies or a user defined policy.
     * @see VersionLockingPolicy
     * @see TimestampLockingPolicy
     * @see FieldsLockingPolicy
     */
    public void setOptimisticLockingPolicy(OptimisticLockingPolicy optimisticLockingPolicy) {
        this.optimisticLockingPolicy = optimisticLockingPolicy;
        if (optimisticLockingPolicy != null) {
            optimisticLockingPolicy.setDescriptor(this);
        }
    }

    /**
     * PUBLIC:
     * Specify the primary key field of the descriptors table.
     * This should only be called if it is a singlton primary key field,
     * otherwise addPrimaryKeyFieldName should be called.
     * If the descriptor has many tables, this must be the primary key in all of the tables.
     *
     * @see #addPrimaryKeyFieldName(String)
     */
    public void setPrimaryKeyFieldName(String fieldName) {
        addPrimaryKeyFieldName(fieldName);
    }

    /**
     * PUBLIC:
     * User can specify a vector of all the primary key field names if primary key is composite.
     *
     * @see #addPrimaryKeyFieldName(String)
     */
    @Override
    public void setPrimaryKeyFieldNames(Vector primaryKeyFieldsName) {
        setPrimaryKeyFields(new ArrayList(primaryKeyFieldsName.size()));
        for (Enumeration keyEnum = primaryKeyFieldsName.elements(); keyEnum.hasMoreElements();) {
            addPrimaryKeyFieldName((String)keyEnum.nextElement());
        }
    }

    /**
     * INTERNAL:
     * Set the primary key fields
     */
    @Override
    public void setPrimaryKeyFields(List<DatabaseField> thePrimaryKeyFields) {
        primaryKeyFields = thePrimaryKeyFields;
    }

    /**
     * INTERNAL:
     * Set the user defined properties.
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * PUBLIC:
     * Set the user defined property.
     */
    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    /**
     * INTERNAL:
     * Set the query keys.
     */
    public void setQueryKeys(Map<String, QueryKey> queryKeys) {
        this.queryKeys = queryKeys;
    }

    /**
     * INTERNAL:
     * Set the query manager.
     */
    public void setQueryManager(DescriptorQueryManager queryManager) {
        this.queryManager = queryManager;
        if (queryManager != null) {
            queryManager.setDescriptor(this);
        }
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be used by this descriptor.
     * The default is the "FullIdentityMap".
     */
    public void setRemoteIdentityMapClass(Class theIdentityMapClass) {
        getCachePolicy().setRemoteIdentityMapClass(theIdentityMapClass);
    }

    /**
     * PUBLIC:
     * Set the size of the identity map to be used by this descriptor.
     * The default is the 100.
     */
    public void setRemoteIdentityMapSize(int identityMapSize) {
        getCachePolicy().setRemoteIdentityMapSize(identityMapSize);
    }

    /**
     * INTERNAL:
     * Set the sequence number field.
     */
    public void setSequenceNumberField(DatabaseField sequenceNumberField) {
        this.sequenceNumberField = sequenceNumberField;
    }

    /**
     * PUBLIC:
     * Set the sequence number field name.
     * This is the field in the descriptors table that needs its value to be generated.
     * This is normally the primary key field of the descriptor.
     */
    public void setSequenceNumberFieldName(String fieldName) {
        if (fieldName == null) {
            setSequenceNumberField(null);
        } else {
            setSequenceNumberField(new DatabaseField(fieldName));
        }
    }

    /**
     * PUBLIC:
     * Set the sequence number name.
     * This is the seq_name part of the row stored in the sequence table for this descriptor.
     * If using Oracle native sequencing this is the name of the Oracle sequence object.
     * If using Sybase native sequencing this name has no meaning, but should still be set for compatibility.
     * The name does not have to be unique among descriptors, as having descriptors share sequences can
     * improve pre-allocation performance.
     */
    public void setSequenceNumberName(String name) {
        sequenceNumberName = name;
    }

    /**
     * INTERNAL:
     * Set the name of the session local to this descriptor.
     * This is used by the session broker.
     */
    protected void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    /**
     * PUBLIC:
     * set if the descriptor is defined to always conform the results in unit of work in read query.
     *
     */
    public void setShouldAlwaysConformResultsInUnitOfWork(boolean shouldAlwaysConformResultsInUnitOfWork) {
        this.shouldAlwaysConformResultsInUnitOfWork = shouldAlwaysConformResultsInUnitOfWork;
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldAlwaysRefreshCache</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to always refresh the cache if data is received from
     * the database by any query.<P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured.
     * For example, by default, when a query for a single object based on its primary key is executed, OracleAS TopLink
     * will first look in the cache for the object. If the object is in the cache, the cached object is returned and
     * data is not refreshed. To avoid cache hits, use the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is not desired.
     * Normally, if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.<P>
     *
     * When the <CODE>shouldAlwaysRefreshCache</CODE> argument passed into this method is <CODE>false</CODE>, this method
     * ensures that a <CODE>ClassDescriptor</CODE> is not configured to always refresh the cache if data is received from the database by any query.
     *
     * @see #alwaysRefreshCache
     * @see #dontAlwaysRefreshCache
     */
    public void setShouldAlwaysRefreshCache(boolean shouldAlwaysRefreshCache) {
        getCachePolicy().setShouldAlwaysRefreshCache(shouldAlwaysRefreshCache);
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldAlwaysRefreshCacheOnRemote</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to always remotely refresh the cache if data is received from
     * the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For
     * example, by default, when a query for a single object based on its primary key is executed, OracleAS TopLink
     * will first look in the cache for the object. If the object is in the cache, the cached object is returned and
     * data is not refreshed. To avoid cache hits, use the {@link #disableCacheHitsOnRemote} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is
     * not desired. Normally, if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.<P>
     *
     * When the <CODE>shouldAlwaysRefreshCacheOnRemote</CODE> argument passed into this method is <CODE>false</CODE>,
     * this method ensures that a <CODE>ClassDescriptor</CODE> is not configured to always remotely refresh the cache if data
     * is received from the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     *
     * @see #alwaysRefreshCacheOnRemote
     * @see #dontAlwaysRefreshCacheOnRemote
     */
    public void setShouldAlwaysRefreshCacheOnRemote(boolean shouldAlwaysRefreshCacheOnRemote) {
        getCachePolicy().setShouldAlwaysRefreshCacheOnRemote(shouldAlwaysRefreshCacheOnRemote);
    }

    /**
     * PUBLIC:
     * Define if the descriptor reference class is read-only
     */
    public void setShouldBeReadOnly(boolean shouldBeReadOnly) {
        this.shouldBeReadOnly = shouldBeReadOnly;
    }

    /**
     * PUBLIC:
     * Set the descriptor to be read-only.
     * Declaring a descriptor is read-only means that instances of the reference class will never be modified.
     * Read-only descriptor is usually used in the unit of work to gain performance as there is no need for
     * the registration, clone and merge for the read-only classes.
     */
    public void setReadOnly() {
        setShouldBeReadOnly(true);
    }

    /**
     * PUBLIC:
     * Set if cache hits on primary key read object queries should be disabled.
     *
     * @see #alwaysRefreshCache()
     */
    public void setShouldDisableCacheHits(boolean shouldDisableCacheHits) {
        getCachePolicy().setShouldDisableCacheHits(shouldDisableCacheHits);
    }

    /**
     * PUBLIC:
     * Set if the remote session cache hits on primary key read object queries is allowed or not.
     *
     * @see #disableCacheHitsOnRemote()
     */
    public void setShouldDisableCacheHitsOnRemote(boolean shouldDisableCacheHitsOnRemote) {
        getCachePolicy().setShouldDisableCacheHitsOnRemote(shouldDisableCacheHitsOnRemote);
    }

    /**
     * ADVANCED:
     * When set to false, this setting will allow the UOW to avoid locking the shared cache instance in order to perform a clone.
     * Caution should be taken as setting this to false may allow cloning of partial updates
     */
     public void setShouldLockForClone(boolean shouldLockForClone) {
        this.shouldLockForClone = shouldLockForClone;
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldOnlyRefreshCacheIfNewerVersion</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to only refresh the cache if the data received from the database
     * by a query is newer than the data in the cache (as determined by the optimistic locking field) and as long as one of the following is true:
     *
     * <UL>
     * <LI>the <CODE>ClassDescriptor</CODE> was configured by calling {@link #alwaysRefreshCache} or {@link #alwaysRefreshCacheOnRemote},</LI>
     * <LI>the query was configured by calling {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}, or</LI>
     * <LI>the query was a call to {@link org.eclipse.persistence.sessions.Session#refreshObject}</LI>
     * </UL>
     * <P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For example, by default,
     * when a query for a single object based on its primary key is executed, OracleAS TopLink will first look in the cache for the object.
     * If the object is in the cache, the cached object is returned and data is not refreshed. To avoid cache hits, use
     * the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * When the <CODE>shouldOnlyRefreshCacheIfNewerVersion</CODE> argument passed into this method is <CODE>false</CODE>, this method
     * ensures that a <CODE>ClassDescriptor</CODE> is not configured to only refresh the cache if the data received from the database by a
     * query is newer than the data in the cache (as determined by the optimistic locking field).
     *
     * @see #onlyRefreshCacheIfNewerVersion
     * @see #dontOnlyRefreshCacheIfNewerVersion
     */
    public void setShouldOnlyRefreshCacheIfNewerVersion(boolean shouldOnlyRefreshCacheIfNewerVersion) {
        getCachePolicy().setShouldOnlyRefreshCacheIfNewerVersion(shouldOnlyRefreshCacheIfNewerVersion);
    }

    /**
     * PUBLIC:
     * This is set to turn off the ordering of mappings.  By Default this is set to true.
     * By ordering the mappings  insures that object are merged in the right order.
     * If the order of the mappings needs to be specified by the developer then set this to
     * false and  will use the order that the mappings were added to the descriptor
     */
    public void setShouldOrderMappings(boolean shouldOrderMappings) {
        this.shouldOrderMappings = shouldOrderMappings;
    }

    /**
     * INTERNAL:
     * Set to false to have queries conform to a UnitOfWork without registering
     * any additional objects not already in that UnitOfWork.
     * @see #shouldRegisterResultsInUnitOfWork
     * @bug 2612601
     */
    public void setShouldRegisterResultsInUnitOfWork(boolean shouldRegisterResultsInUnitOfWork) {
        this.shouldRegisterResultsInUnitOfWork = shouldRegisterResultsInUnitOfWork;
    }

    /**
     * PUBLIC:
     * Specify the table name for the class of objects the receiver describes.
     * If the table has a qualifier it should be specified using the dot notation,
     * (i.e. "userid.employee"). This method is used for single table.
     */
    public void setTableName(String tableName) throws DescriptorException {
        if (getTables().isEmpty()) {
            addTableName(tableName);
        } else {
            getTables().get(0).setPossiblyQualifiedName(tableName);
        }
    }

    /**
     * PUBLIC:
     * Specify the all table names for the class of objects the receiver describes.
     * If the table has a qualifier it should be specified using the dot notation,
     * (i.e. "userid.employee"). This method is used for multiple tables
     */
    public void setTableNames(Vector tableNames) {
        setTables(NonSynchronizedVector.newInstance(tableNames.size()));
        for (Enumeration tableEnum = tableNames.elements(); tableEnum.hasMoreElements();) {
            addTableName((String)tableEnum.nextElement());
        }
    }

    /**
     * INTERNAL:
     * Sets the table per class policy.
     */
    public void setTablePerClassPolicy(TablePerClassPolicy tablePerClassPolicy) {
        interfacePolicy = tablePerClassPolicy;
        if (interfacePolicy != null) {
            interfacePolicy.setDescriptor(this);
        }
    }
    
    /**
     * PUBLIC: Set the table Qualifier for this descriptor.  This table creator will be used for
     * all tables in this descriptor
     */
    public void setTableQualifier(String tableQualifier) {
        for (Enumeration enumtr = getTables().elements(); enumtr.hasMoreElements();) {
            DatabaseTable table = (DatabaseTable)enumtr.nextElement();
            table.setTableQualifier(tableQualifier);
        }
    }

    /**
     * INTERNAL:
     * Sets the tables
     */
    public void setTables(Vector<DatabaseTable> theTables) {
        tables = theTables;
    }

    /**
     * ADVANCED:
     * Sets the WrapperPolicy for this descriptor.
     * This advanced feature can be used to wrap objects with other classes such as CORBA TIE objects or EJBs.
     */
    public void setWrapperPolicy(WrapperPolicy wrapperPolicy) {
        this.wrapperPolicy = wrapperPolicy;

        // For bug 2766379 must be able to set the wrapper policy back to default
        // which is null.
        if (wrapperPolicy != null) {
            wrapperPolicy.setDescriptor(this);
        }
        getObjectBuilder().setHasWrapperPolicy(wrapperPolicy != null);
    }

    /**
     * PUBLIC:
     * Return if the descriptor is defined to always conform the results in unit of work in read query.
     *
     */
    public boolean shouldAlwaysConformResultsInUnitOfWork() {
        return shouldAlwaysConformResultsInUnitOfWork;
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to always refresh
     * the cache if data is received from the database by any query. Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldAlwaysRefreshCache
     */
    public boolean shouldAlwaysRefreshCache() {
        return getCachePolicy().shouldAlwaysRefreshCache();
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to always remotely
     * refresh the cache if data is received from the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     * Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldAlwaysRefreshCacheOnRemote
     */
    public boolean shouldAlwaysRefreshCacheOnRemote() {
        return getCachePolicy().shouldAlwaysRefreshCacheOnRemote();
    }

    /**
     * PUBLIC:
     * Return if the descriptor reference class is defined as read-only
     *
     */
    public boolean shouldBeReadOnly() {
        return shouldBeReadOnly;
    }

    /**
     * PUBLIC:
     * Return if for cache hits on primary key read object queries to be disabled.
     *
     * @see #disableCacheHits()
     */
    public boolean shouldDisableCacheHits() {
        return getCachePolicy().shouldDisableCacheHits();
    }

    /**
     * PUBLIC:
     * Return if the remote server session cache hits on primary key read object queries is aloowed or not.
     *
     * @see #disableCacheHitsOnRemote()
     */
    public boolean shouldDisableCacheHitsOnRemote() {
        return getCachePolicy().shouldDisableCacheHitsOnRemote();
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to only refresh the cache
     * if the data received from the database by a query is newer than the data in the cache (as determined by the
     * optimistic locking field). Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldOnlyRefreshCacheIfNewerVersion
     */
    public boolean shouldOnlyRefreshCacheIfNewerVersion() {
        return getCachePolicy().shouldOnlyRefreshCacheIfNewerVersion();
    }

    /**
     * INTERNAL:
     * Return if mappings should be ordered or not.  By default this is set to true
     * to prevent attributes from being merged in the wrong order
     *
     */
    public boolean shouldOrderMappings() {
        return shouldOrderMappings;
    }

    /**
     * INTERNAL:
     * PERF: Return if the primary key is simple (direct-mapped) to allow fast extraction.
     */
    public boolean hasSimplePrimaryKey() {
        return hasSimplePrimaryKey;
    }

    /**
     * INTERNAL:
     * Return if this descriptor is involved in a table per class inheritance.
     */
    public boolean hasTablePerClassPolicy() {
        return hasInterfacePolicy() && interfacePolicy.isTablePerClassPolicy();
    }
    
    /**
     * INTERNAL:
     * PERF: Set if the primary key is simple (direct-mapped) to allow fast extraction.
     */
    public void setHasSimplePrimaryKey(boolean hasSimplePrimaryKey) {
        this.hasSimplePrimaryKey = hasSimplePrimaryKey;
    }

    /**
     * INTERNAL:
     * PERF: Return if deferred locks should be used.
     * Used to optimize read locking.
     * This is determined based on if any relationships do not use indirection.
     */
    public boolean shouldAcquireCascadedLocks() {
        return shouldAcquireCascadedLocks;
    }

    /**
     * INTERNAL:
     * PERF: Set if deferred locks should be used.
     * This is determined based on if any relationships do not use indirection,
     * but this provides a backdoor hook to force on if require because of events usage etc.
     */
    public void setShouldAcquireCascadedLocks(boolean shouldAcquireCascadedLocks) {
        this.shouldAcquireCascadedLocks = shouldAcquireCascadedLocks;
    }

    /**
     * PUBLIC:
     * Return true if this descriptor should using an additional join expresison.
     */
    public boolean shouldUseAdditionalJoinExpression() {
        // Return true, if the query manager has an additional join expression 
        // and this descriptor is not part of an inheritance hierarchy using a 
        // view (CR#3701077)
        return ((getQueryManager().getAdditionalJoinExpression() != null) && ! (hasInheritance() && getInheritancePolicy().hasView()));
    }
    
    /**
     * PUBLIC:
     * Return true if this descriptor is using CacheIdentityMap
     */
    public boolean shouldUseCacheIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.CacheIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using FullIdentityMap
     */
    public boolean shouldUseFullIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.FullIdentityMap_Class);
    }
    
    /**
     * PUBLIC:
     * Return true if this descriptor is using SoftIdentityMap
     */
    public boolean shouldUseSoftIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.SoftIdentityMap_Class);
    }
    
    /**
     * PUBLIC:
     * Return true if this descriptor is using SoftIdentityMap
     */
    public boolean shouldUseRemoteSoftIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.SoftIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using HardCacheWeakIdentityMap.
     */
    public boolean shouldUseHardCacheWeakIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.HardCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using NoIdentityMap
     */
    public boolean shouldUseNoIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.NoIdentityMap_Class);
    }

    /**
     * INTERNAL:
     * Allows one to do conforming in a UnitOfWork without registering.
     * Queries executed on a UnitOfWork will only return working copies for objects
     * that have already been registered.
     * <p>Extreme care should be taken in using this feature, for a user will
     * get back a mix of registered and original (unregistered) objects.
     * <p>Best used with a WrapperPolicy where invoking on an object will trigger
     * its registration (CMP).  Without a WrapperPolicy {@link org.eclipse.persistence.sessions.UnitOfWork#registerExistingObject registerExistingObject}
     * should be called on any object that you intend to change.
     * @return true by default.
     * @see #setShouldRegisterResultsInUnitOfWork
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#shouldRegisterResultsInUnitOfWork
     * @bug 2612601
     */
    public boolean shouldRegisterResultsInUnitOfWork() {
        return shouldRegisterResultsInUnitOfWork;
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using CacheIdentityMap
     */
    public boolean shouldUseRemoteCacheIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.CacheIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using FullIdentityMap
     */
    public boolean shouldUseRemoteFullIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.FullIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using HardCacheWeakIdentityMap
     */
    public boolean shouldUseRemoteHardCacheWeakIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.HardCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using NoIdentityMap
     */
    public boolean shouldUseRemoteNoIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.NoIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using SoftCacheWeakIdentityMap
     */
    public boolean shouldUseRemoteSoftCacheWeakIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.SoftCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using WeakIdentityMap
     */
    public boolean shouldUseRemoteWeakIdentityMap() {
        return (getRemoteIdentityMapClass() == ClassConstants.WeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using SoftCacheWeakIdentityMap.
     */
    public boolean shouldUseSoftCacheWeakIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.SoftCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if this descriptor is using WeakIdentityMap
     */
    public boolean shouldUseWeakIdentityMap() {
        return (getIdentityMapClass() == ClassConstants.WeakIdentityMap_Class);
    }

    /**
     * INTERNAL:
     * Returns whether this descriptor is capable of supporting weaved change tracking.
     * This method is used before the project is initialized.
     */
    public boolean supportsChangeTracking(Project project){
        // Check the descriptor: if field-locking is used, cannot do
        // change tracking because field-locking requires backup clone.
        OptimisticLockingPolicy lockingPolicy = getOptimisticLockingPolicy();
        if (lockingPolicy != null && (lockingPolicy instanceof FieldsLockingPolicy)) {
            return false;
        }
        Vector mappings = getMappings();        
        for (Iterator iterator = mappings.iterator(); iterator.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping)iterator.next();               
            if (!mapping.isChangeTrackingSupported(project) ) {
                return false;
            }
        }
        return true; 
    }
    
    /**
     * PUBLIC:
     * Returns a brief string representation of the receiver.
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getJavaClassName() + " --> " + getTables() + ")";
    }

    /**
     * PUBLIC:
     * Set the locking policy an all fields locking policy.
     * A field locking policy is base on locking on all fields by comparing with their previous values to detect field-level collisions.
     * Note: the unit of work must be used for all updates when using field locking.
     * @see AllFieldsLockingPolicy
     */
    public void useAllFieldsLocking() {
        setOptimisticLockingPolicy(new AllFieldsLockingPolicy());
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the cache identity map.
     * This map caches the LRU instances read from the database.
     * Note: This map does not guarantee object identity.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useCacheIdentityMap() {
        setIdentityMapClass(ClassConstants.CacheIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the locking policy a changed fields locking policy.
     * A field locking policy is base on locking on all changed fields by comparing with their previous values to detect field-level collisions.
     * Note: the unit of work must be used for all updates when using field locking.
     * @see ChangedFieldsLockingPolicy
     */
    public void useChangedFieldsLocking() {
        setOptimisticLockingPolicy(new ChangedFieldsLockingPolicy());
    }

    /**
     * PUBLIC:
     * Specifies that the creation of clones within a unit of work is done by
     * sending the #clone() method to the original object. The #clone() method
     * must return a logical shallow copy of the original object.
     * This can be used if the default mechanism of creating a new instance
     * does not handle the object's non-persistent attributes correctly.
     *
     * @see #useCloneCopyPolicy(String)
     */
    public void useCloneCopyPolicy() {
        useCloneCopyPolicy("clone");
    }

    /**
     * PUBLIC:
     * Specifies that the creation of clones within a unit of work is done by
     * sending the cloneMethodName method to the original object. This method
     * must return a logical shallow copy of the original object.
     * This can be used if the default mechanism of creating a new instance
     * does not handle the object's non-persistent attributes correctly.
     *
     * @see #useCloneCopyPolicy()
     */
    public void useCloneCopyPolicy(String cloneMethodName) {
        CloneCopyPolicy policy = new CloneCopyPolicy();
        policy.setMethodName(cloneMethodName);
        setCopyPolicy(policy);
    }

    /**
     * PUBLIC:
     * Specifies that the creation of clones within a unit of work is done by building
     * a new instance using the
     * technique indicated by the descriptor's instantiation policy
     * (which by default is to use the
     * the default constructor). This new instance is then populated by using the
     * descriptor's mappings to copy attributes from the original to the clone.
     * This is the default.
     * If another mechanism is desired the copy policy allows for a clone method to be called.
     *
     * @see #useCloneCopyPolicy()
     * @see #useCloneCopyPolicy(String)
     * @see #useDefaultConstructorInstantiationPolicy()
     * @see #useMethodInstantiationPolicy(String)
     * @see #useFactoryInstantiationPolicy(Class, String)
     * @see #useFactoryInstantiationPolicy(Class, String, String)
     * @see #useFactoryInstantiationPolicy(Object, String)
     */
    public void useInstantiationCopyPolicy() {
        setCopyPolicy(new InstantiationCopyPolicy());
    }

    /**
     * PUBLIC:
     * Use the default constructor to create new instances of objects built from the database.
     * This is the default.
     * The descriptor's class must either define a default constructor or define
     * no constructors at all.
     *
     * @see #useMethodInstantiationPolicy(String)
     * @see #useFactoryInstantiationPolicy(Class, String)
     * @see #useFactoryInstantiationPolicy(Class, String, String)
     * @see #useFactoryInstantiationPolicy(Object, String)
     */
    public void useDefaultConstructorInstantiationPolicy() {
        getInstantiationPolicy().useDefaultConstructorInstantiationPolicy();
    }

    /**
     * PUBLIC:
     * Use an object factory to create new instances of objects built from the database.
     * The methodName is the name of the
     * method that will be invoked on the factory. When invoked, it must return a new instance
     * of the descriptor's class.
     * The factory will be created by invoking the factoryClass's default constructor.
     *
     * @see #useDefaultConstructorInstantiationPolicy()
     * @see #useMethodInstantiationPolicy(String)
     * @see #useFactoryInstantiationPolicy(Class, String, String)
     * @see #useFactoryInstantiationPolicy(Object, String)
     */
    public void useFactoryInstantiationPolicy(Class factoryClass, String methodName) {
        getInstantiationPolicy().useFactoryInstantiationPolicy(factoryClass, methodName);
    }

    /**
     * INTERNAL:
     * Set the factory class name, used by the MW.
     */
    public void useFactoryInstantiationPolicy(String factoryClassName, String methodName) {
        getInstantiationPolicy().useFactoryInstantiationPolicy(factoryClassName, methodName);
    }

    /**
     * PUBLIC:
     * Use an object factory to create new instances of objects built from the database.
     * The factoryMethodName is a static method declared by the factoryClass.
     * When invoked, it must return an instance of the factory. The methodName is the name of the
     * method that will be invoked on the factory. When invoked, it must return a new instance
     * of the descriptor's class.
     *
     * @see #useDefaultConstructorInstantiationPolicy()
     * @see #useFactoryInstantiationPolicy(Class, String)
     * @see #useFactoryInstantiationPolicy(Object, String)
     * @see #useMethodInstantiationPolicy(String)
     */
    public void useFactoryInstantiationPolicy(Class factoryClass, String methodName, String factoryMethodName) {
        getInstantiationPolicy().useFactoryInstantiationPolicy(factoryClass, methodName, factoryMethodName);
    }

    /**
     * INTERNAL:
     * Set the factory class name, used by the MW.
     */
    public void useFactoryInstantiationPolicy(String factoryClassName, String methodName, String factoryMethodName) {
        getInstantiationPolicy().useFactoryInstantiationPolicy(factoryClassName, methodName, factoryMethodName);
    }

    /**
     * PUBLIC:
     * Use an object factory to create new instances of objects built from the database.
     * The methodName is the name of the
     * method that will be invoked on the factory. When invoked, it must return a new instance
     * of the descriptor's class.
     *
     * @see #useDefaultConstructorInstantiationPolicy()
     * @see #useMethodInstantiationPolicy(String)
     * @see #useFactoryInstantiationPolicy(Class, String)
     * @see #useFactoryInstantiationPolicy(Class, String, String)
     */
    public void useFactoryInstantiationPolicy(Object factory, String methodName) {
        getInstantiationPolicy().useFactoryInstantiationPolicy(factory, methodName);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the full identity map.
     * This map caches all instances read and grows to accomodate them.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useFullIdentityMap() {
        getCachePolicy().useFullIdentityMap();
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the hard cache weak identity map.
     * This map uses weak references to only cache object in-memory.
     * It also includes a secondary fixed sized hard cache to improve caching performance.
     * This is provided because some Java VM's implement soft references differently.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useHardCacheWeakIdentityMap() {
        getCachePolicy().useHardCacheWeakIdentityMap();
    }
    
    /**
     * PUBLIC:
     * Set the class of identity map to be the soft identity map.
     * This map uses soft references to only cache all object in-memory, until memory is low.
     * Note that "low" is interpreted differently by different JVM's.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useSoftIdentityMap() {
        getCachePolicy().useSoftIdentityMap();
    }
    
    /**
     * PUBLIC:
     * Set the class of identity map to be the soft identity map.
     * This map uses soft references to only cache all object in-memory, until memory is low.
     * Note that "low" is interpreted differently by different JVM's.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteSoftIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.SoftIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Use the specified static method to create new instances of objects built from the database.
     * This method must be statically declared by the descriptor's class, and it must
     * return a new instance of the descriptor's class.
     *
     * @see #useDefaultConstructorInstantiationPolicy()
     * @see #useFactoryInstantiationPolicy(Class, String)
     * @see #useFactoryInstantiationPolicy(Class, String, String)
     * @see #useFactoryInstantiationPolicy(Object, String)
     */
    public void useMethodInstantiationPolicy(String staticMethodName) {
        getInstantiationPolicy().useMethodInstantiationPolicy(staticMethodName);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the no identity map.
     * This map does no caching.
     * Note: This map does not maintain object identity.
     * In general if caching is not desired a WeakIdentityMap should be used with an isolated descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     * @see #setIsIsolated(boolean)
     */
    public void useNoIdentityMap() {
        getCachePolicy().useNoIdentityMap();
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the cache identity map.
     * This map caches the LRU instances read from the database.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteCacheIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.CacheIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the full identity map.
     * This map caches all instances read and grows to accomodate them.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteFullIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.FullIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the hard cache weak identity map.
     * This map uses weak references to only cache object in-memory.
     * It also includes a secondary fixed sized soft cache to improve caching performance.
     * This is provided because some Java VM's do not implement soft references correctly.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteHardCacheWeakIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.HardCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the no identity map.
     * This map does no caching.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteNoIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.NoIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the soft cache weak identity map.
     * The SoftCacheIdentityMap holds a fixed number of objects is memory
     * (using SoftReferences) to improve caching.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteSoftCacheWeakIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.SoftCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the weak identity map.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useRemoteWeakIdentityMap() {
        getCachePolicy().setRemoteIdentityMapClass(ClassConstants.WeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the locking policy a selected fields locking policy.
     * A field locking policy is base on locking on the specified fields by comparing with their previous values to detect field-level collisions.
     * Note: the unit of work must be used for all updates when using field locking.
     * @see SelectedFieldsLockingPolicy
     */
    public void useSelectedFieldsLocking(Vector fieldNames) {
        SelectedFieldsLockingPolicy policy = new SelectedFieldsLockingPolicy();
        policy.setLockFieldNames(fieldNames);
        setOptimisticLockingPolicy(policy);
    }

    /**
     * INTERNAL:
     * Return true if the receiver uses either all or changed fields for optimistic locking.
     */
    public boolean usesFieldLocking() {
        return (usesOptimisticLocking() && (getOptimisticLockingPolicy() instanceof FieldsLockingPolicy));
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the soft cache weak identity map.
     * The SoftCacheIdentityMap holds a fixed number of objects is memory
     * (using SoftReferences) to improve caching.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useSoftCacheWeakIdentityMap() {
        setIdentityMapClass(ClassConstants.SoftCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Return true if the receiver uses write (optimistic) locking.
     */
    public boolean usesOptimisticLocking() {
        return (optimisticLockingPolicy != null);
    }
    
    /**
     * PUBLIC:
     * Return true if the receiver uses version optimistic locking.
     */
    public boolean usesVersionLocking() {
        return (usesOptimisticLocking() && (getOptimisticLockingPolicy() instanceof VersionLockingPolicy));
    }

    /**
     * PUBLIC:
     * Return true if the receiver uses sequence numbers.
     */
    public boolean usesSequenceNumbers() {
        return this.sequenceNumberField != null;
    }

    /**
     * PUBLIC:
     * Use the Timestamps locking policy and storing the value in the cache key
     * #see useVersionLocking(String)
     */
    public void useTimestampLocking(String writeLockFieldName) {
        useTimestampLocking(writeLockFieldName, true);
    }

    /**
     * PUBLIC:
     * Set the locking policy to use timestamp version locking.
     * This updates the timestamp field on all updates, first comparing that the field has not changed to detect locking conflicts.
     * Note: many database have limited precision of timestamps which can be an issue is highly concurrent systems.
     *
     * The parameter 'shouldStoreInCache' configures the version lock value to be stored in the cache or in the object.
     * Note: if using a stateless model where the object can be passed to a client and then later updated in a different transaction context,
     * then the version lock value should not be stored in the cache, but in the object to ensure it is the correct value for that object.
     * @see VersionLockingPolicy
     */
    public void useTimestampLocking(String writeLockFieldName, boolean shouldStoreInCache) {
        TimestampLockingPolicy policy = new TimestampLockingPolicy(writeLockFieldName);
        if (shouldStoreInCache) {
            policy.storeInCache();
        } else {
            policy.storeInObject();
        }
        setOptimisticLockingPolicy(policy);
    }

    /**
     * PUBLIC:
     * Default to use the version locking policy and storing the value in the cache key
     * #see useVersionLocking(String)
     */
    public void useVersionLocking(String writeLockFieldName) {
        useVersionLocking(writeLockFieldName, true);
    }

    /**
     * PUBLIC:
     * Set the locking policy to use numeric version locking.
     * This updates the version field on all updates, first comparing that the field has not changed to detect locking conflicts.
     *
     * The parameter 'shouldStoreInCache' configures the version lock value to be stored in the cache or in the object.
     * Note: if using a stateless model where the object can be passed to a client and then later updated in a different transaction context,
     * then the version lock value should not be stored in the cache, but in the object to ensure it is the correct value for that object.
     * @see TimestampLockingPolicy
     */
    public void useVersionLocking(String writeLockFieldName, boolean shouldStoreInCache) {
        VersionLockingPolicy policy = new VersionLockingPolicy(writeLockFieldName);
        if (shouldStoreInCache) {
            policy.storeInCache();
        } else {
            policy.storeInObject();
        }
        setOptimisticLockingPolicy(policy);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the weak identity map.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useWeakIdentityMap() {
        getCachePolicy().useWeakIdentityMap();
    }

    /**
     * INTERNAL:
     * Validate the entire post-initialization descriptor.
     */
    protected void validateAfterInitialization(AbstractSession session) {
        selfValidationAfterInitialization(session);
        for (DatabaseMapping mapping : getMappings()) {
            mapping.validateAfterInitialization(session);
        }
    }

    /**
     * INTERNAL:
     * Validate the entire pre-initialization descriptor.
     */
    protected void validateBeforeInitialization(AbstractSession session) {
        selfValidationBeforeInitialization(session);
        for (DatabaseMapping mapping : getMappings()) {
            mapping.validateBeforeInitialization(session);
        }
    }

    /**
     * INTERNAL:
     * Check that the qualifier on the table names are properly set.
     */
    protected void verifyTableQualifiers(Platform platform) {
        String tableQualifier = platform.getTableQualifier();
        if (tableQualifier.length() == 0) {
            return;
        }

        for (DatabaseTable table : getTables()) {
            if (table.getTableQualifier().length() == 0) {
                table.setTableQualifier(tableQualifier);
            }
        }
    }

    /**
     * ADVANCED:
     * Return the cmp descriptor that holds cmp specific information.  
     * A null return will mean that the descriptor does not represent an Entity,
     * however it may still represent a MappedSuperclass.
     * It will be null if it is not being used.
     */
    public CMPPolicy getCMPPolicy() {
        return cmpPolicy;
    }

    /**
     * ADVANCED:
     * Set the cmp descriptor that holds cmp specific information.
     */
    public void setCMPPolicy(CMPPolicy newCMPPolicy) {
        cmpPolicy = newCMPPolicy;
        if (cmpPolicy != null) {
            cmpPolicy.setDescriptor(this);
        }
    }

    /**
     * Return the cache policy.
     * The cache policy allows for the configuration of caching options.
     */
    public CachePolicy getCachePolicy() {
        if (this.cachePolicy == null) {
            this.cachePolicy = new CachePolicy();
        }
        return cachePolicy;
    }

    /**
     * ADVANCED:
     * Set cache policy for the descriptor.
     */
    public void setCachePolicy(CachePolicy cachePolicy) {
        this.cachePolicy = cachePolicy;
    }

    /**
     * INTERNAL:
     */
    public boolean hasPessimisticLockingPolicy() {
        return (cmpPolicy != null) && cmpPolicy.hasPessimisticLockingPolicy();
    }

    /**
     * PUBLIC:
     * Get the fetch group manager for the descriptor.  The fetch group manager is responsible
     * for managing the fetch group behaviors and operations.
     * To use the fetch group, the domain object must implement FetchGroupTracker interface. Otherwise,
     * a descriptor validation exception would throw during initialization.
     *
     * @see org.eclipse.persistence.queries.FetchGroupTracker
     */
    public FetchGroupManager getFetchGroupManager() {
        return this.fetchGroupManager;
    }

    /**
     * @return the fullyMergeEntity
     */
    public boolean getFullyMergeEntity() {
        return getCachePolicy().getFullyMergeEntity();
    }

    /**
     * PUBLIC:
     * Set the fetch group manager for the descriptor.  The fetch group manager is responsible
     * for managing the fetch group behaviors and operations.
     */
    public void setFetchGroupManager(FetchGroupManager fetchGroupManager) {
        this.fetchGroupManager = fetchGroupManager;
        if (fetchGroupManager != null) {
            //set the back reference
            fetchGroupManager.setDescriptor(this);
        }
    }

    /**
     * INTERNAL:
     * Return if the descriptor has a fetch group manager associated with.
     */
    public boolean hasFetchGroupManager() {
        return (fetchGroupManager != null);
    }

    /**
     * INTERNAL:
     */
    public boolean hasCascadeLockingPolicies() {
        return (this.cascadeLockingPolicies != null) && !this.cascadeLockingPolicies.isEmpty();
    }

    /**
     * INTERNAL:
     * Return if the descriptor has a CMP policy.
     */
    public boolean hasCMPPolicy() {
        return (cmpPolicy != null);
    }

    /**
     * INTERNAL:
     *
     * Return the default fetch group on the descriptor.
     * All read object and read all queries will use the default fetch group if
     * no fetch group is explicitly defined for the query.
     */
    public FetchGroup getDefaultFetchGroup() {
        if (!hasFetchGroupManager()) {
            //fetch group manager is not set, therefore no default fetch group.
            return null;
        }
        return getFetchGroupManager().getDefaultFetchGroup();
    }

    /**
     * INTERNAL:
     * Indicates if a return type is required for the field set on the
     * returning policy.  For relational descriptors, this should always
     * return true.
     */
    public boolean isReturnTypeRequiredForReturningPolicy() {
        return true;
    }

    /**
     * ADVANCED:
     * Set if the descriptor requires usage of a native (unwrapped) JDBC connection.
     * This may be required for some Oracle JDBC support when a wrapping DataSource is used.
     */
    public void setIsNativeConnectionRequired(boolean isNativeConnectionRequired) {
        this.isNativeConnectionRequired = isNativeConnectionRequired;
    }

    /**
     * ADVANCED:
     * Return if the descriptor requires usage of a native (unwrapped) JDBC connection.
     * This may be required for some Oracle JDBC support when a wrapping DataSource is used.
     */
    public boolean isNativeConnectionRequired() {
        return isNativeConnectionRequired;
    }

    /**
     * ADVANCED:
     * Set what types are allowed as a primary key (id).
     */
    public void setIdValidation(IdValidation idValidation) {
        this.idValidation = idValidation;
        if (getPrimaryKeyIdValidations() != null) {
            for (int index = 0; index < getPrimaryKeyIdValidations().size(); index++) {
                getPrimaryKeyIdValidations().set(index, idValidation);
            }
        }
    }

    /**
     * ADVANCED:
     * Return what types are allowed as a primary key (id).
     */
    public IdValidation getIdValidation() {
        return idValidation;
    }

    /**
     * ADVANCED:
     * Return what types are allowed in each primary key field (id).
     */    
    public List<IdValidation> getPrimaryKeyIdValidations() {
        return primaryKeyIdValidations;
    }

    /**
     * ADVANCED:
     * Return what types are allowed in each primary key field (id).
     */
    public void setPrimaryKeyIdValidations(List<IdValidation> primaryKeyIdValidations) {
        this.primaryKeyIdValidations = primaryKeyIdValidations;
    }

    /**
     * ADVANCED:
     * Set what cache key type to use to store the object in the cache.
     */
    public void setCacheKeyType(CacheKeyType cacheKeyType) {
        getCachePolicy().setCacheKeyType(cacheKeyType);
    }

    /**
     * ADVANCED:
     * Return what cache key type to use to store the object in the cache.
     */
    public CacheKeyType getCacheKeyType() {
        return getCachePolicy().getCacheKeyType();
    }

    /**
     * A Default Query Redirector will be applied to any executing object query
     * that does not have a more precise default (like the default
     * ReadObjectQuery Redirector) or a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultQueryRedirector() {
        return defaultQueryRedirector;
    }

    /**
     * A Default Query Redirector will be applied to any executing object query
     * that does not have a more precise default (like the default
     * ReadObjectQuery Redirector) or a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultQueryRedirector(QueryRedirector defaultRedirector) {
        this.defaultQueryRedirector = defaultRedirector;
    }

    /**
     * A Default ReadAllQuery Redirector will be applied to any executing
     * ReadAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultReadAllQueryRedirector() {
        return defaultReadAllQueryRedirector;
    }

    /**
     * A Default ReadAllQuery Redirector will be applied to any executing
     * ReadAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultReadAllQueryRedirector(
            QueryRedirector defaultReadAllQueryRedirector) {
        this.defaultReadAllQueryRedirector = defaultReadAllQueryRedirector;
    }

    /**
     * A Default ReadObjectQuery Redirector will be applied to any executing
     * ReadObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultReadObjectQueryRedirector() {
        return defaultReadObjectQueryRedirector;
    }

    /**
     * A Default ReadObjectQuery Redirector will be applied to any executing
     * ReadObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultReadObjectQueryRedirector(
            QueryRedirector defaultReadObjectQueryRedirector) {
        this.defaultReadObjectQueryRedirector = defaultReadObjectQueryRedirector;
    }

    /**
     * A Default ReportQuery Redirector will be applied to any executing
     * ReportQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultReportQueryRedirector() {
        return defaultReportQueryRedirector;
    }

    /**
     * A Default ReportQuery Redirector will be applied to any executing
     * ReportQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultReportQueryRedirector(
            QueryRedirector defaultReportQueryRedirector) {
        this.defaultReportQueryRedirector = defaultReportQueryRedirector;
    }

    /**
     * A Default UpdateObjectQuery Redirector will be applied to any executing
     * UpdateObjectQuery or UpdateAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultUpdateObjectQueryRedirector() {
        return defaultUpdateObjectQueryRedirector;
    }

    /**
     * A Default UpdateObjectQuery Redirector will be applied to any executing
     * UpdateObjectQuery or UpdateAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultUpdateObjectQueryRedirector(QueryRedirector defaultUpdateQueryRedirector) {
        this.defaultUpdateObjectQueryRedirector = defaultUpdateQueryRedirector;
    }

    /**
     * A Default InsertObjectQuery Redirector will be applied to any executing
     * InsertObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultInsertObjectQueryRedirector() {
        return defaultInsertObjectQueryRedirector;
    }

    /**
     * A Default InsertObjectQuery Redirector will be applied to any executing
     * InsertObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultInsertObjectQueryRedirector(QueryRedirector defaultInsertQueryRedirector) {
        this.defaultInsertObjectQueryRedirector = defaultInsertQueryRedirector;
    }

    /**
     * A Default DeleteObjectQuery Redirector will be applied to any executing
     * DeleteObjectQuery or DeleteAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public QueryRedirector getDefaultDeleteObjectQueryRedirector() {
        return defaultDeleteObjectQueryRedirector;
    }

    /**
     * A Default DeleteObjectQuery Redirector will be applied to any executing
     * DeleteObjectQuery or DeleteAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultDeleteObjectQueryRedirector(QueryRedirector defaultDeleteObjectQueryRedirector) {
        this.defaultDeleteObjectQueryRedirector = defaultDeleteObjectQueryRedirector;
    }

    /**
     * A Default Query Redirector will be applied to any executing object query
     * that does not have a more precise default (like the default
     * ReadObjectQuery Redirector) or a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultQueryRedirectorClassName(String defaultQueryRedirectorClassName) {
        this.defaultQueryRedirectorClassName = defaultQueryRedirectorClassName;
    }

    /**
     * A Default ReadAllQuery Redirector will be applied to any executing
     * ReadAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query exection preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultReadAllQueryRedirectorClassName(String defaultReadAllQueryRedirectorClassName) {
        this.defaultReadAllQueryRedirectorClassName = defaultReadAllQueryRedirectorClassName;
    }

    /**
     * A Default ReadObjectQuery Redirector will be applied to any executing
     * ReadObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultReadObjectQueryRedirectorClassName(
            String defaultReadObjectQueryRedirectorClassName) {
        this.defaultReadObjectQueryRedirectorClassName = defaultReadObjectQueryRedirectorClassName;
    }

    /**
     * A Default ReportQuery Redirector will be applied to any executing
     * ReportQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
   public void setDefaultReportQueryRedirectorClassName(
            String defaultReportQueryRedirectorClassName) {
        this.defaultReportQueryRedirectorClassName = defaultReportQueryRedirectorClassName;
    }

   /**
    * A Default UpdateObjectQuery Redirector will be applied to any executing
    * UpdateObjectQuery or UpdateAllQuery that does not have a redirector set directly on the query.
    * Query redirectors allow the user to intercept query execution preventing
    * it or alternately performing some side effect like auditing.
    * 
    * @see org.eclipse.persistence.queries.QueryRedirector
    */
    public void setDefaultUpdateObjectQueryRedirectorClassName(
            String defaultUpdateObjectQueryRedirectorClassName) {
        this.defaultUpdateObjectQueryRedirectorClassName = defaultUpdateObjectQueryRedirectorClassName;
    }

    /**
     * A Default InsertObjectQuery Redirector will be applied to any executing
     * InsertObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query exection preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultInsertObjectQueryRedirectorClassName(
            String defaultInsertObjectQueryRedirectorClassName) {
        this.defaultInsertObjectQueryRedirectorClassName = defaultInsertObjectQueryRedirectorClassName;
    }

    /**
     * A Default DeleteObjectQuery Redirector will be applied to any executing
     * DeleteObjectQuery or DeleteAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query exection preventing
     * it or alternately performing some side effect like auditing.
     * 
     * @see org.eclipse.persistence.queries.QueryRedirector
     */
    public void setDefaultDeleteObjectQueryRedirectorClassName(
            String defaultDeleteObjectQueryRedirectorClassName) {
        this.defaultDeleteObjectQueryRedirectorClassName = defaultDeleteObjectQueryRedirectorClassName;
    }
    
    /**
     * Return the descriptor's sequence.
     * This is normally set when the descriptor is initialized.
     */
    public Sequence getSequence() {
        return sequence;
    }
    
    /**
     * Set the descriptor's sequence.
     * This is normally set when the descriptor is initialized.
     */
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    /** 
     * Mappings that require postCalculateChanges method to be called 
     */
    public List<DatabaseMapping> getMappingsPostCalculateChanges() {
        if(mappingsPostCalculateChanges == null) {
            mappingsPostCalculateChanges = new ArrayList<DatabaseMapping>();
        }
        return mappingsPostCalculateChanges;
    }

    /** 
     * Are there any mappings that require postCalculateChanges method to be called. 
     */
    public boolean hasMappingsPostCalculateChanges() {
        return mappingsPostCalculateChanges != null;
    }

    /** 
     * Add a mapping to the list of mappings that require postCalculateChanges method to be called. 
     */
    public void addMappingsPostCalculateChanges(DatabaseMapping mapping) {
        getMappingsPostCalculateChanges().add(mapping);
    }

    /** 
     * Mappings that require mappingsPostCalculateChangesOnDeleted method to be called 
     */
    public List<DatabaseMapping> getMappingsPostCalculateChangesOnDeleted() {
        if (mappingsPostCalculateChangesOnDeleted == null) {
            mappingsPostCalculateChangesOnDeleted = new ArrayList<DatabaseMapping>();
        }
        return mappingsPostCalculateChangesOnDeleted;
    }

    /** 
     * Are there any mappings that require mappingsPostCalculateChangesOnDeleted method to be called. 
     */
    public boolean hasMappingsPostCalculateChangesOnDeleted() {
        return mappingsPostCalculateChangesOnDeleted != null;
    }

    /** 
     * Add a mapping to the list of mappings that require mappingsPostCalculateChangesOnDeleted method to be called. 
     */
    public void addMappingsPostCalculateChangesOnDeleted(DatabaseMapping mapping) {
        getMappingsPostCalculateChangesOnDeleted().add(mapping);
    }

    /**
     * Return if any mapping reference a field in a secondary table.
     * This is used to disable deferring multiple table writes.
     */
    public boolean hasMultipleTableConstraintDependecy() {
        return hasMultipleTableConstraintDependecy;
    }
    
    /**
     * Return true if the descriptor has a multitenant policy
     */
    public boolean hasMultitenantPolicy() {
        return multitenantPolicy != null;
    }
    /**
     * PUBLIC
     * @return true if this descriptor is configured with a table per tenant policy.
     */
    public boolean hasTablePerMultitenantPolicy() {
        return hasMultitenantPolicy() && getMultitenantPolicy().isTablePerMultitenantPolicy();
    }

    /**
     * INTERNAL:
     * Used to store un-converted properties, which are subsequenctly converted
     * at runtime (through the convertClassNamesToClasses method.
     */
    public boolean hasUnconvertedProperties() {
        return unconvertedProperties != null;
    }
    
    /**
     * Set if any mapping reference a field in a secondary table.
     * This is used to disable deferring multiple table writes.
     */
    public void setHasMultipleTableConstraintDependecy(boolean hasMultipleTableConstraintDependecy) {
        this.hasMultipleTableConstraintDependecy = hasMultipleTableConstraintDependecy;
    }
    
    /**
     * INTERNAL:
     * Return whether this descriptor uses property access. This information is used to
     * modify the behavior of some of our weaving features
     */
    public boolean usesPropertyAccessForWeaving(){
        return weavingUsesPropertyAccess;
    }
    

    /**
     * INTERNAL:
     * Record that this descriptor uses property access. This information is used to
     * modify the behavior of some of our weaving features
     */
    public void usePropertyAccessForWeaving(){
        weavingUsesPropertyAccess = true;
    }

    /** 
     * INTERNAL:
     * Return the list of virtual methods sets for this Entity.
     * This list is used to control which methods are weaved
     **/
    public List<VirtualAttributeMethodInfo> getVirtualAttributeMethods() {
        if (this.virtualAttributeMethods == null) {
            this.virtualAttributeMethods = new ArrayList<VirtualAttributeMethodInfo>();
        }
        return this.virtualAttributeMethods;
    }    

    /** 
     * INTERNAL:
     * Set the list of methods used my mappings with virtual access
     * this list is used to determine which methods to weave
     */
    public void setVirtualAttributeMethods(List<VirtualAttributeMethodInfo> virtualAttributeMethods) {
        this.virtualAttributeMethods = virtualAttributeMethods;
    }
    
    /** 
     * INTERNAL:
     * Indicates whether descriptor has at least one target foreign key mapping
     */
    public boolean hasTargetForeignKeyMapping(AbstractSession session) {
        for (DatabaseMapping mapping: getMappings()) {
            if (mapping.isCollectionMapping() || 
                    (mapping.isObjectReferenceMapping() && !((ObjectReferenceMapping)mapping).isForeignKeyRelationship()) || 
                    mapping.isAbstractCompositeDirectCollectionMapping()) {
                return true;
            } else if (mapping.isAggregateObjectMapping()) {
                ClassDescriptor referenceDescriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
                if (referenceDescriptor == null) {
                    // the mapping has not been initialized yet
                    referenceDescriptor = session.getDescriptor(((AggregateObjectMapping)mapping).getReferenceClass());
                }
                if (referenceDescriptor.hasTargetForeignKeyMapping(session)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public AttributeGroup getAttributeGroup(String name) {
        return super.getAttributeGroup(name);
    }

    @Override
    public Map<String, AttributeGroup> getAttributeGroups() {
        return super.getAttributeGroups();
    }
}
