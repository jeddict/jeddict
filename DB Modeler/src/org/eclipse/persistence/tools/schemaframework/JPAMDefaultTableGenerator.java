/** *****************************************************************************
 * Copyright (c) 1998, 2013 Oracle, Sei Syvalta. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Sei Syvalta  - Bug 330237 - Tables are created in unspecified order (DDL creation)
 *     01/06/2011-2.3 Guy Pelletier
 *       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
 *     04/05/2011-2.3 Guy Pelletier
 *       - 337323: Multi-tenant with shared schema support (part 3)
 *     09/09/2011-2.3.1 Guy Pelletier
 *       - 356197: Add new VPD type to MultitenantType
 *     11/10/2011-2.4 Guy Pelletier
 *       - 357474: Address primaryKey option from tenant discriminator column
 *     14/05/2012-2.4 Guy Pelletier
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     31/05/2012-2.4 Guy Pelletier
 *       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
 *     12/07/2012-2.5 Guy Pelletier
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     04/04/2013-2.4.3 Guy Pelletier
 *       - 388564: Generated DDL does not match annotation
 ***************************************************************************** */
package org.eclipse.persistence.tools.schemaframework;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DBRelationalDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.descriptors.FieldTransformation;
import org.eclipse.persistence.internal.descriptors.MethodBasedFieldTransformation;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.netbeans.jpa.modeler.db.accessor.EmbeddableSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

/**
 * DefaultTableGenerator is a utility class used to generate a default table
 * schema for a EclipseLink project object.
 *
 * The utility can be used in EclipseLink CMP for OC4J to perform the table auto
 * creation process, which can be triggered at deployment time when EclipseLink
 * project descriptor is absent (default mapping) or present.
 *
 * The utility can also be used to any EclipseLink application to perform the
 * table drop/creation at runtime.
 *
 * The utility handles all direct/relational mappings, inheritance, multiple
 * tables, interface with/without tables, optimistic version/timestamp lockings,
 * nested relationships, BLOB/CLOB generation.
 *
 * The utility is platform-agnostic.
 *
 * Usage: - CMP 1. set "autocreate-tables=true|false,
 * autodelete-tables=true|false" in oc4j application deployment descriptor files
 * (config/system-application.xml, config/application.xml, or
 * orion-application.xml in an .ear)
 *
 * 2. Default Mapping: the same as CMP, plus system properties setting
 * -Declipselink.defaultmapping.autocreate-tables='true|false' and
 * -Declipselink.defaultmapping.autodelete-tables='true|false'
 *
 * - Non-CMP: 1. Configuration: through sessions.xml 2. Directly runtime call
 * through schema framework: SchemaManager mgr = new SchemaManager(session);
 * mgr.replaceDefaultTables(); //drop and create mgr.createDefaultTables();
 * //create only
 *
 * The utility currently only supports relational project.
 *
 * @author King Wang
 * @since Oracle TopLink 10.1.3
 */
public class JPAMDefaultTableGenerator {

    /**
     * The project object used to generate the default data schema.
     */
    Project project = null;

    /**
     * the target database platform.
     */
    protected DatabasePlatform databasePlatform;

    /**
     * Used to track the table definition: keyed by the table name, and valued
     * by the table definition object.
     */
    protected Map<String, TableDefinition> tableMap = null;

    /**
     * Used to track the field definition: keyed by the database field object,
     * and valued by the field definition.
     */
    protected Map<DatabaseField, FieldDefinition> fieldMap = null;

    /**
     * DatabaseField pool (synchronized with above 'fieldMap')
     */
    protected Map<DatabaseField, DatabaseField> databaseFields;

    /**
     * When this flag is 'false' EclipseLink will not attempt to create fk
     * constraints.
     */
    protected boolean generateFKConstraints;

    /**
     * Default constructor
     */
    public JPAMDefaultTableGenerator(Project project) {
        this.project = project;
        if (project.getDatasourceLogin().getDatasourcePlatform() instanceof DatabasePlatform) {
            this.databasePlatform = (DatabasePlatform) project.getDatasourceLogin().getDatasourcePlatform();
            this.generateFKConstraints = this.databasePlatform.supportsForeignKeyConstraints();
        }
        this.tableMap = new LinkedHashMap();
        this.fieldMap = new LinkedHashMap();
        this.databaseFields = new LinkedHashMap();
    }

    /**
     * This constructor will create a DefaultTableGenerator that can be set to
     * create fk constraints
     */
    public JPAMDefaultTableGenerator(Project project, boolean generateFKConstraints) {
        this(project);
        this.generateFKConstraints = generateFKConstraints;
    }

    /**
     * Generate a default TableCreator object from the EclipseLink project
     * object.
     */
    public JPAMTableCreator generateDefaultTableCreator() {
        JPAMTableCreator tblCreator = new JPAMTableCreator();

        //go through each descriptor and build the table/field definitions out of mappings
        for (ClassDescriptor descriptor : this.project.getOrderedDescriptors()) {
//            if ((descriptor instanceof XMLDescriptor) || (descriptor instanceof EISDescriptor) || (descriptor instanceof ObjectRelationalDataTypeDescriptor)) {
//                //default table generator does not support ox, eis and object-relational descriptor
//                AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.DDL, "relational_descriptor_support_only", (Object[]) null, true);
//
//                return tblCreator;
//            }

            /**
             * Id : SUPERCLASS_ATTR_CLONE. Description : Fix for If class have super
             * class then super class mapping is cloned and copied to subclass,
             * to share the attributes but in the cloning process, Attribute
             * Spec property is missed.
             */
            List<DatabaseMapping> parentClassMapping = ((DBRelationalDescriptor) descriptor).getParentClassMapping();
            if (parentClassMapping != null) {
                parentClassMapping.stream().forEach((parentMapping) -> {
                    descriptor.getMappings().stream().filter((childMapping) -> (parentMapping.getAttributeName().equals(childMapping.getAttributeName()))).forEach((childMapping) -> {
                        childMapping.setProperty(Attribute.class, parentMapping.getProperty(Attribute.class));
                        childMapping.setProperty(Inheritance.class, true);
                    });
                });
            }
            // Aggregate descriptors do not contain table/field data and are 
            // processed through their owning entities. Aggregate descriptors
            // can not exist on their own.
            // Table per tenant descriptors will not be initialized.
            if (!descriptor.isDescriptorTypeAggregate() && !(descriptor.hasTablePerMultitenantPolicy() && !project.allowTablePerMultitenantDDLGeneration())) {
                initTableSchema(descriptor);
            }
        }

        //Post init the schema for relation table and direct collection/map tables, and several special mapping handlings.
        for (ClassDescriptor descriptor : this.project.getOrderedDescriptors()) {
            // Aggregate descriptors do not contain table/field data and are 
            // processed through their owning entities. Aggregate descriptors
            // can not exist on their own.
            // Table per tenant descriptors will not be initialized.
            if (!descriptor.isAggregateDescriptor() && !descriptor.isAggregateCollectionDescriptor() && !(descriptor.hasTablePerMultitenantPolicy() && !project.allowTablePerMultitenantDDLGeneration())) {
                postInitTableSchema(descriptor);

                // If VPD descriptor we need to generate some DDL for its default table.
                if (descriptor.hasMultitenantPolicy()) {
                    descriptor.getMultitenantPolicy().addToTableDefinition(getTableDefFromDBTable(descriptor.getDefaultTable()));
                }
            }
        }

        tblCreator.addTableDefinitions(tableMap.values());

        return tblCreator;
    }

    /**
     * Generate a default TableCreator object from the EclipseLink project
     * object, and perform the table existence check through jdbc table
     * metadata, and filter out tables which are already in the database.
     */
    public JPAMTableCreator generateFilteredDefaultTableCreator(AbstractSession session) throws DatabaseException {
        JPAMTableCreator tblCreator = generateDefaultTableCreator();

        try {
            //table exisitence check.
            java.sql.Connection conn = null;
            if (session.isServerSession()) {
                //acquire a connection from the pool
                conn = ((ServerSession) session).getDefaultConnectionPool().acquireConnection().getConnection();
            } else if (session.isDatabaseSession()) {
                conn = ((DatabaseSessionImpl) session).getAccessor().getConnection();
            }
            if (conn == null) {
                //TODO: this is not pretty, connection is not obtained for some reason. 
                return tblCreator;
            }
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, dbMetaData.getUserName(), null, new String[]{"TABLE"});
            List tablesInDatabase = new ArrayList();

            while (resultSet.next()) {
                //save all tables from the database
                tablesInDatabase.add(resultSet.getString("TABLE_NAME"));
            }

            resultSet.close();

            List existedTables = new ArrayList();
            List existedTableNames = new ArrayList();
            Iterator tblDefIter = tblCreator.getTableDefinitions().iterator();

            while (tblDefIter.hasNext()) {
                TableDefinition tblDef = (TableDefinition) tblDefIter.next();

                //check if the to-be-created table is already in the database
                if (tablesInDatabase.contains(tblDef.getFullName())) {
                    existedTables.add(tblDef);
                    existedTableNames.add(tblDef.getFullName());
                }
            }

            if (!existedTableNames.isEmpty()) {
                session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "skip_create_existing_tables", existedTableNames);

                //remove the existed tables, won't create them.
                tblCreator.getTableDefinitions().removeAll(existedTables);
            }
        } catch (SQLException sqlEx) {
            throw DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
        }

        return tblCreator;
    }

    
    private Attribute getManagedAttribute(ClassDescriptor refDescriptor, DatabaseField dbField, LinkedList<Attribute> intrinsicAttribute){
        if (refDescriptor != null) {
                    for (DatabaseMapping refMapping : refDescriptor.getMappings()) {
                        if(refMapping.getFields().size() > 1){
                            intrinsicAttribute.add((Attribute)refMapping.getProperty(Attribute.class));
                           return getManagedAttribute(refMapping.getReferenceDescriptor(),dbField, intrinsicAttribute);
                        } else
                            if (!refMapping.getFields().isEmpty() && refMapping.getFields().get(0)== dbField) {
                               intrinsicAttribute.add((Attribute)refMapping.getProperty(Attribute.class));
                                return (Attribute) refMapping.getProperty(Attribute.class);
                        }
                    }
                }
        return null;
    }
    
    /**
     * Build tables/fields information into the table creator object from a
     * EclipseLink descriptor. This should handle most of the direct/relational
     * mappings except many-to-many and direct collection/map mappings, which
     * must be down in postInit method.
     */
    protected void initTableSchema(ClassDescriptor baseDescriptor) {

        DBRelationalDescriptor descriptor = (DBRelationalDescriptor) baseDescriptor;
        LinkedList<Entity> intrinsicEntity = new LinkedList<>();
        if (descriptor.getAccessor() instanceof EntitySpecAccessor) {
            intrinsicEntity.add(((EntitySpecAccessor) descriptor.getAccessor()).getEntity());
        } else {
            throw new IllegalStateException("Invalid getAccessor Type : " + descriptor.getAccessor());
        }

        TableDefinition tableDefintion = null;
        if (descriptor.hasTablePerClassPolicy() && descriptor.isAbstract()) {
            return;
        }

        //create a table definition for each mapped database table
        for (DatabaseTable table : descriptor.getTables()) {
            tableDefintion = getTableDefFromDBTable(intrinsicEntity.peek(),null, intrinsicEntity, table);
        }

        //build each field definition and figure out which table it goes
        for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
            LinkedList<Attribute> intrinsicAttribute;

            ClassDescriptor refDescriptor = databaseMapping.getReferenceDescriptor();

            for (DatabaseField dbField : databaseMapping.getFields()) {
                intrinsicAttribute = new LinkedList<>();
                Attribute managedAttribute = (Attribute) databaseMapping.getProperty(Attribute.class);
                Boolean isInherited = (Boolean) databaseMapping.getProperty(Inheritance.class);
                isInherited = isInherited==null?false:isInherited;
                
                intrinsicAttribute.add(managedAttribute);
                Attribute attribute = getManagedAttribute(refDescriptor, dbField, intrinsicAttribute);
                if (attribute != null) {
                    managedAttribute = attribute;
                }
                if (dbField.isCreatable()) {
                    boolean isPKField = false;
                    boolean isFKField = false;
                    boolean isInverse = false;

                    //first check if the field is a pk field in the default table.
                    isPKField = descriptor.getPrimaryKeyFields().contains(dbField);

                    //then check if the field is a pk field in the secondary table(s), this is only applied to the multiple tables case.
                    Map secondaryKeyMap = descriptor.getAdditionalTablePrimaryKeyFields().get(dbField.getTable());

                    if (secondaryKeyMap != null) {
                        isPKField = isPKField || secondaryKeyMap.containsValue(dbField);
                    }

                    // Now check if it is a tenant discriminat column primary key field.
                    isPKField = isPKField || dbField.isPrimaryKey();
                    
                    if(managedAttribute instanceof RelationAttribute){
                        isFKField = true;
                        isInverse = true;
                    } 

                    //build or retrieve the field definition.
                    FieldDefinition fieldDef = getFieldDefFromDBField(intrinsicAttribute, managedAttribute, isInverse, isFKField, false, dbField);
                    if (isPKField) {
                        fieldDef.setIsPrimaryKey(true);
                        // Check if the generation strategy is IDENTITY
                        String sequenceName = descriptor.getSequenceNumberName();
                        DatabaseLogin login = this.project.getLogin();
                        Sequence seq = login.getSequence(sequenceName);
                        if (seq instanceof DefaultSequence) {
                            seq = login.getDefaultSequence();
                        }
                        //The native sequence whose value should be acquired after insert is identity sequence
                        boolean isIdentity = seq instanceof NativeSequence && seq.shouldAcquireValueAfterInsert();
                        fieldDef.setIsIdentity(isIdentity);
                    }

                    //find the table the field belongs to, and add it to the table, only if not already added.
                    tableDefintion = this.tableMap.get(dbField.getTableName());

                    if ((tableDefintion != null) && !tableDefintion.getFields().contains(fieldDef)) {
                        tableDefintion.addField(fieldDef);
                    }
                }
            }
        }
    }

    /**
     * Build additional table/field definitions for the descriptor, like
     * relation table and direct-collection, direct-map table, as well as reset
     * LOB type for serialized object mapping and type conversion mapping for
     * LOB usage
     */
        protected void postInitTableSchema(ClassDescriptor baseDescriptor) {
            postInitTableSchema(baseDescriptor, null, null);
        }
        /**
         * 
         * @param baseDescriptor
         * @param intrinsicEntity defines the Entity Object that contains embedded Object where Entity object will be intrinsicEntity and Embeddable object will be descriptorManagedClass
         * @param intrinsicAttribute 
         */
        protected void postInitTableSchema(ClassDescriptor baseDescriptor, LinkedList<Entity> intrinsicEntity, LinkedList<Attribute> intrinsicAttribute) {
        
        DBRelationalDescriptor descriptor = (DBRelationalDescriptor) baseDescriptor;
        ManagedClass descriptorManagedClass;// = intrinsicEntity.peek();
        
            if (intrinsicEntity == null) {
                if (descriptor.getAccessor() instanceof EntitySpecAccessor) {
                    intrinsicEntity = new LinkedList<>();
                    intrinsicAttribute = new LinkedList<>();
                    intrinsicEntity.offer(((EntitySpecAccessor) descriptor.getAccessor()).getEntity());
                    descriptorManagedClass = intrinsicEntity.peek();
                }else {
                    throw new IllegalStateException(descriptor.getAccessor() + " not supported");
                }
            } else {
                if (descriptor.getAccessor() instanceof EmbeddableSpecAccessor) {
                    descriptorManagedClass = ((EmbeddableSpecAccessor) descriptor.getAccessor()).getEmbeddable();
                } else {
                    throw new IllegalStateException(descriptor.getAccessor() + " not supported");
                }
            }
        

        for (DatabaseMapping mapping : descriptor.getMappings()) {
            ManagedClass managedClass = descriptorManagedClass;
            Attribute managedAttribute = (Attribute)mapping.getProperty(Attribute.class);
//            Entity intrinsicLocalEntity= intrinsicEntity.peek();
//            Attribute intrinsicLocalAttribute = intrinsicAttribute.peek();

            if (mapping.isForeignReferenceMapping()) {
                if (managedAttribute instanceof RelationAttribute) {
                    RelationAttribute relationAttribute = (RelationAttribute) managedAttribute;
                    if (!relationAttribute.isOwner()) {
                        managedClass = relationAttribute.getConnectedEntity();
                        managedAttribute = relationAttribute.getConnectedAttribute();
                    }
                } else if (managedAttribute instanceof ElementCollection) {
                }
            } else if (mapping.isAggregateMapping()) {
            }
            
            if(intrinsicAttribute.peek() == null){
                intrinsicAttribute.offer(managedAttribute);
//                intrinsicLocalAttribute = intrinsicAttribute.peek();
            }

            if (descriptor.isChildDescriptor() && descriptor.getInheritancePolicy().getParentDescriptor().getMappingForAttributeName(mapping.getAttributeName()) != null) {
                // If we are an inheritance subclass, do nothing. That is, don't 
                // generate mappings that will be generated by our parent,
                // otherwise the fields for that mapping will be generated n 
                // times for the same table.
            } else if (mapping.isManyToManyMapping()) {
                buildRelationTableDefinition(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, (ManyToManyMapping) mapping, ((ManyToManyMapping) mapping).getRelationTableMechanism(), ((ManyToManyMapping) mapping).getListOrderField(), mapping.getContainerPolicy());
            } else if (mapping.isDirectCollectionMapping()) {
                buildDirectCollectionTableDefinition(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, (DirectCollectionMapping) mapping, descriptor);
            } else if (mapping.isDirectToFieldMapping()) {
                Converter converter = ((DirectToFieldMapping) mapping).getConverter();
                if (converter != null) {
                    if (converter instanceof TypeConversionConverter) {
                        resetFieldTypeForLOB((DirectToFieldMapping) mapping);
                    }

                    if (converter instanceof SerializedObjectConverter) {
                        //serialized object mapping field should be BLOB/IMAGE
                        getFieldDefFromDBField(mapping.getField()).setType(((SerializedObjectConverter) converter).getSerializer().getType());
                    }
                }
            } else if (mapping.isAggregateCollectionMapping()) {
                //need to figure out the target foreign key field and add it into the aggregate target table
                createAggregateTargetTable(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, (AggregateCollectionMapping) mapping);
            } else if (mapping.isForeignReferenceMapping()) {
                if (mapping.isOneToOneMapping()) {
                    RelationTableMechanism relationTableMechanism = ((OneToOneMapping) mapping).getRelationTableMechanism();
                    if (relationTableMechanism == null) {
                        addForeignKeyFieldToSourceTargetTable(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,(OneToOneMapping) mapping);
                    } else {
                        buildRelationTableDefinition(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, (OneToOneMapping) mapping, relationTableMechanism, null, null);
                    }
                } else if (mapping.isOneToManyMapping()) {
                    addForeignKeyFieldToSourceTargetTable(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,(OneToManyMapping) mapping);
                    TableDefinition targTblDef = getTableDefFromDBTable(((OneToManyMapping) mapping).getReferenceDescriptor().getDefaultTable());//TODO pass entity
                    addFieldsForMappedKeyMapContainerPolicy(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,mapping.getContainerPolicy(), targTblDef);
                }
            } else if (mapping.isTransformationMapping()) {
                resetTransformedFieldType((TransformationMapping) mapping);
            } else if (mapping.isAggregateObjectMapping()) {
                postInitTableSchema(((AggregateObjectMapping) mapping).getReferenceDescriptor(),intrinsicEntity,intrinsicAttribute);
            }
            intrinsicAttribute.clear();
        }
        
        intrinsicEntity.clear();
        

        processAdditionalTablePkFields(descriptor);
    }

    /**
     * The ContainerPolicy may contain some additional fields that should be
     * added to the table
     *
     * @see MappedKeyMapContainerPolicy
     */
    protected void addFieldsForMappedKeyMapContainerPolicy(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute, ContainerPolicy cp, TableDefinition table) {
        if (cp.isMappedKeyMapPolicy()) {
            List<DatabaseField> keyFields = cp.getIdentityFieldsForMapKey();
            Iterator<DatabaseField> i = keyFields.iterator();
            while (i.hasNext()) {
                DatabaseField foreignKey = i.next();
                FieldDefinition fieldDef = getFieldDefFromDBField(foreignKey);
                if (!table.getFields().contains(fieldDef)) {
                    table.addField(fieldDef);
                }
            }
            Map<DatabaseField, DatabaseField> foreignKeys = ((MappedKeyMapContainerPolicy) cp).getForeignKeyFieldsForMapKey();
            if (foreignKeys != null) {
                addForeignMappingFkConstraint(managedClass, managedAttribute, intrinsicEntity, intrinsicAttribute, foreignKeys, false);
            }
        }
    }

    /**
     * Build relation table definitions for all many-to-many relationships in a
     * EclipseLink descriptor.
     */
    protected void buildRelationTableDefinition(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute, ForeignReferenceMapping mapping, RelationTableMechanism relationTableMechanism, DatabaseField listOrderField, ContainerPolicy cp) {
        //first create relation table
        TableDefinition table = getTableDefFromDBTable(managedClass, managedAttribute,intrinsicEntity, relationTableMechanism.getRelationTable());

        //add source foreign key fields into the relation table
        List<DatabaseField> srcFkFields = relationTableMechanism.getSourceRelationKeyFields();//Relation Table
        List<DatabaseField> srcKeyFields = relationTableMechanism.getSourceKeyFields();//Entity(Owner) Table

        buildRelationTableFields(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, false, mapping, table, srcFkFields, srcKeyFields);

        //add target foreign key fields into the relation table
        List<DatabaseField> targFkFields = relationTableMechanism.getTargetRelationKeyFields();//Relation Table
        List<DatabaseField> targKeyFields = relationTableMechanism.getTargetKeyFields();//Entity(MappedBy) Table

//        attribute.getConnectedAttribute()
        buildRelationTableFields(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute, true, mapping, table, targFkFields, targKeyFields);

        if (cp != null) {
            addFieldsForMappedKeyMapContainerPolicy(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,cp, table);
        }

        if (listOrderField != null) {
            FieldDefinition fieldDef = getFieldDefFromDBField(listOrderField);
            if (!table.getFields().contains(fieldDef)) {
                table.addField(fieldDef);
            }
        }
    }

    /**
     * Build field definitions and foreign key constraints for all many-to-many
     * relation table.
     */
    protected void buildRelationTableFields(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity, LinkedList<Attribute> intrinsicAttribute, boolean inverse, ForeignReferenceMapping mapping, TableDefinition table, List<DatabaseField> fkFields, List<DatabaseField> targetFields) {
        assert fkFields.size() > 0 && fkFields.size() == targetFields.size();

        DatabaseField fkField;
        DatabaseField targetField = null;
        List<String> fkFieldNames = new ArrayList();
        List<String> targetFieldNames = new ArrayList();

        for (int index = 0; index < fkFields.size(); index++) {
            fkField = fkFields.get(index);
            targetField = targetFields.get(index);
            fkFieldNames.add(fkField.getNameDelimited(databasePlatform));
            targetFieldNames.add(targetField.getNameDelimited(databasePlatform));

            fkField = resolveDatabaseField(fkField, targetField);
            setFieldToRelationTable(intrinsicAttribute,managedAttribute, inverse, fkField, table);
        }

        // add a foreign key constraint from fk field to target field
        DatabaseTable targetTable = targetField.getTable();
        TableDefinition targetTblDef = getTableDefFromDBTable(managedClass, managedAttribute, intrinsicEntity, targetTable);

        if (mapping.getDescriptor().hasTablePerClassPolicy()) {
            return;
        }
        if (mapping.getReferenceDescriptor().hasTablePerClassPolicy()
                && mapping.getReferenceDescriptor().getTablePerClassPolicy().hasChild()) {
            return;
        }
        addForeignKeyConstraint(table, targetTblDef, fkFieldNames, targetFieldNames, mapping.isCascadeOnDeleteSetOnDatabase());
    }

    /**
     * Build direct collection table definitions in a EclipseLink descriptor
     */
    protected void buildDirectCollectionTableDefinition(ManagedClass managedClass, Attribute managedAttribute, LinkedList<Entity> intrinsicEntity, LinkedList<Attribute> intrinsicAttribute, DirectCollectionMapping mapping, ClassDescriptor descriptor) {
        //first create direct collection table
        TableDefinition table = getTableDefFromDBTable(managedClass, managedAttribute, intrinsicEntity, mapping.getReferenceTable());

        DatabaseField dbField;
        DatabaseField targetField = null;
        List<String> fkFieldNames = new ArrayList();
        List<String> targetFieldNames = new ArrayList();
        List<DatabaseField> fkFields = mapping.getReferenceKeyFields();
        List<DatabaseField> targetFields = mapping.getSourceKeyFields();
        for (int index = 0; index < fkFields.size(); index++) {
            DatabaseField fkField = fkFields.get(index);
            targetField = targetFields.get(index);
            fkFieldNames.add(fkField.getNameDelimited(databasePlatform));
            targetFieldNames.add(targetField.getNameDelimited(databasePlatform));

            fkField = resolveDatabaseField(fkField, targetField);
            FieldDefinition fieldDef = getFieldDefFromDBField(intrinsicAttribute, managedAttribute, false, true, false, fkField);//todo pass entity
            // Avoid adding fields twice for table per class.
            if (!table.getFields().contains(fieldDef)) {
                table.addField(fieldDef);
            }
        }

        // add a foreign key constraint from fk field to target field
        DatabaseTable targetTable = targetField.getTable();
        TableDefinition targetTblDef = getTableDefFromDBTable(managedClass,intrinsicEntity, targetTable);

        //add the direct collection field to the table.
        FieldDefinition fieldDef = getFieldDefFromDBField(intrinsicAttribute,managedAttribute, false, false, false, mapping.getDirectField());
        if (!table.getFields().contains(fieldDef)) {
            table.addField(fieldDef);
        }

        //if the mapping is direct-map field, add the direct key field to the table as well.
        // TODO: avoid generating DDL for map key mappings for the time being.
        // Bug: 270814
        if (mapping.isDirectMapMapping() && !mapping.getContainerPolicy().isMappedKeyMapPolicy()) {
            dbField = ((DirectMapMapping) mapping).getDirectKeyField();
            fieldDef = getFieldDefFromDBField(dbField);
            if (!table.getFields().contains(fieldDef)) {
                table.addField(fieldDef);
            }
        } else {
            addFieldsForMappedKeyMapContainerPolicy(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,mapping.getContainerPolicy(), table);

            if (mapping.getListOrderField() != null) {
                fieldDef = getFieldDefFromDBField(mapping.getListOrderField());
                if (!table.getFields().contains(fieldDef)) {
                    table.addField(fieldDef);
                }
            }
        }
        if (mapping.getDescriptor().hasTablePerClassPolicy()) {
            return;
        }
        addForeignKeyConstraint(table, targetTblDef, fkFieldNames, targetFieldNames, mapping.isCascadeOnDeleteSetOnDatabase());
    }

    /**
     * Reset field type to use BLOB/CLOB with type conversion mapping fix for 4k
     * oracle thin driver bug.
     */
    protected void resetFieldTypeForLOB(DirectToFieldMapping mapping) {
        if (mapping.getFieldClassification().getName().equals("java.sql.Blob")) {
            //allow the platform to figure out what database field type gonna be used. 
            //For example, Oracle9 will generate BLOB type, SQL Server generats IMAGE.
            getFieldDefFromDBField(mapping.getField()).setType(Byte[].class);
        } else if (mapping.getFieldClassification().getName().equals("java.sql.Clob")) {
            //allow the platform to figure out what database field type gonna be used. 
            //For example, Oracle9 will generate CLOB type. SQL Server generats TEXT.
            getFieldDefFromDBField(mapping.getField()).setType(Character[].class);
        }
    }

    /**
     * Reset the transformation mapping field types
     */
    protected void resetTransformedFieldType(TransformationMapping mapping) {
        Iterator transIter = mapping.getFieldTransformations().iterator();
        while (transIter.hasNext()) {
            FieldTransformation transformation = (FieldTransformation) transIter.next();

            if (transformation instanceof MethodBasedFieldTransformation) {
                MethodBasedFieldTransformation methodTransformation = (MethodBasedFieldTransformation) transformation;
                try {
                    Class returnType = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodTransformation.getMethodName(), null).getReturnType();
                    getFieldDefFromDBField(methodTransformation.getField()).setType(returnType);
                } catch (NoSuchMethodException ex) {
                    // For some reason, the method type could not be retrieved, 
                    // use the default java.lang.String type
                }
            } else {
                // Must be a TransformerBasedFieldTransformation
                TransformerBasedFieldTransformation classTransformation = (TransformerBasedFieldTransformation) transformation;
                String methodName = "buildFieldValue";
                Class[] params = new Class[]{Object.class, String.class, Session.class};

                try {
                    Class returnType = Helper.getDeclaredMethod(classTransformation.getTransformerClass(), methodName, params).getReturnType();

                    if (returnType.equals(Object.class)) {
                        // User needs to be more specific with their class
                        // transformer return type if they are using DDL. Throw
                        // an exception.
                        throw ValidationException.missingFieldTypeForDDLGenerationOfClassTransformation(mapping.getDescriptor(), mapping.getAttributeName(), methodName);
                    }

                    getFieldDefFromDBField(classTransformation.getField()).setType(returnType);
                } catch (NoSuchMethodException ex) {
                    // For some reason, the method type could not be retrieved.
                    // Did the interface method change? Throw an exception. 
                    throw ValidationException.missingTransformerMethodForDDLGenerationOfClassTransformation(mapping.getDescriptor(), mapping.getAttributeName(), methodName);
                }
            }
        }
    }

    /**
     * Add the foreign key to the aggregate collection mapping target table.
     * Also add listOrderField if specified.
     */
    protected void createAggregateTargetTable(ManagedClass managedClass, Attribute elementCollection, LinkedList<Entity> intrinsicEntity, LinkedList<Attribute> intrinsicAttribute, AggregateCollectionMapping mapping) {
        //intrinsicEntity Table
        TableDefinition targetTable = getTableDefFromDBTable(managedClass, elementCollection, intrinsicEntity, mapping.getReferenceDescriptor().getDefaultTable());
        addFieldsForMappedKeyMapContainerPolicy(managedClass, elementCollection, intrinsicEntity,intrinsicAttribute,mapping.getContainerPolicy(), targetTable);

        Iterator aggregateFieldIterator = mapping.getReferenceDescriptor().getFields().iterator();
        while (aggregateFieldIterator.hasNext()) {
            DatabaseField dbField = (DatabaseField) aggregateFieldIterator.next();
            //add the target definition to the table definition
            FieldDefinition fieldDef = getFieldDefFromDBField(dbField);
            if (!targetTable.getFields().contains(fieldDef)) {
                targetTable.addField(fieldDef);
            }
        }

        //unlike normal one-to-many mapping, aggregate collection mapping does not have 1:1 back reference
        //mapping, so the target foreign key fields are not stored in the target descriptor.
        List<String> fkFieldNames = new ArrayList();
        List<String> targetFieldNames = new ArrayList();
        List<DatabaseField> fkFields = mapping.getTargetForeignKeyFields();
        List<DatabaseField> targetFields = mapping.getSourceKeyFields();
        DatabaseField targetField = null;
        for (int index = 0; index < fkFields.size(); index++) {
            DatabaseField fkField = fkFields.get(index);
            targetField = targetFields.get(index);
            fkFieldNames.add(fkField.getNameDelimited(databasePlatform));
            targetFieldNames.add(targetField.getNameDelimited(databasePlatform));

            fkField = resolveDatabaseField(fkField, targetField);
            FieldDefinition fieldDef = getFieldDefFromDBField(fkField);
            if (!targetTable.getFields().contains(fieldDef)) {
                targetTable.addField(fieldDef);
            }
        }

        // add a foreign key constraint from fk field to target field
        DatabaseTable sourceDatabaseTable = targetField.getTable();
        TableDefinition sourceTable = getTableDefFromDBTable(managedClass, intrinsicEntity, sourceDatabaseTable);

        if (mapping.getListOrderField() != null) {
            FieldDefinition fieldDef = getFieldDefFromDBField(mapping.getListOrderField());
            TableDefinition table = getTableDefFromDBTable(mapping.getListOrderField().getTable());
            if (!table.getFields().contains(fieldDef)) {
                table.addField(fieldDef);
            }
        }
        if (mapping.getDescriptor().hasTablePerClassPolicy()) {
            return;
        }
        addForeignKeyConstraint(targetTable, sourceTable, fkFieldNames, targetFieldNames, mapping.isCascadeOnDeleteSetOnDatabase());
    }

    protected void addForeignKeyFieldToSourceTargetTable(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute, OneToOneMapping mapping) {
        if (!mapping.isForeignKeyRelationship()
                || (mapping.getReferenceDescriptor().hasTablePerClassPolicy()
                && mapping.getReferenceDescriptor().getTablePerClassPolicy().hasChild())) {
            return;
        }
        boolean cascadeDelete = false;
        // Find mappedBy target mapping to check constraint cascade.
        for (DatabaseField foreignKey : mapping.getSourceToTargetKeyFields().values()) {
            DatabaseMapping mappedBy = mapping.getReferenceDescriptor().getObjectBuilder().getMappingForField(foreignKey);
            if (mappedBy != null && mappedBy.isOneToOneMapping()) {
                cascadeDelete = ((OneToOneMapping) mappedBy).isCascadeOnDeleteSetOnDatabase();
            } else {
                List<DatabaseMapping> readOnlyMappings = mapping.getReferenceDescriptor().getObjectBuilder().getReadOnlyMappingsForField(foreignKey);
                if (readOnlyMappings != null) {
                    for (DatabaseMapping mappedByPK : readOnlyMappings) {
                        if (mappedByPK.isOneToOneMapping()) {
                            cascadeDelete = ((OneToOneMapping) mappedByPK).isCascadeOnDeleteSetOnDatabase();
                            if (cascadeDelete) {
                                break;
                            }
                        }
                    }
                }
            }
            if (cascadeDelete) {
                break;
            }
        }

        // If the mapping is optional and uses primary key join columns, don't 
        // generate foreign key constraints which would require the target to 
        // always be set.
        if (!mapping.isOptional() || !mapping.isOneToOnePrimaryKeyRelationship()) {
            addForeignMappingFkConstraint(managedClass, managedAttribute, intrinsicEntity,intrinsicAttribute,mapping.getSourceToTargetKeyFields(), cascadeDelete);
        }
    }

    protected void addForeignKeyFieldToSourceTargetTable(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute,OneToManyMapping mapping) {
        if (mapping.getDescriptor().hasTablePerClassPolicy()) {
            return;
        }
        addForeignMappingFkConstraint(managedClass, managedAttribute, intrinsicEntity, intrinsicAttribute,mapping.getTargetForeignKeysToSourceKeys(), mapping.isCascadeOnDeleteSetOnDatabase());
        if (mapping.getListOrderField() != null) {
            FieldDefinition fieldDef = getFieldDefFromDBField(mapping.getListOrderField());
            TableDefinition table = getTableDefFromDBTable(mapping.getListOrderField().getTable());
            if (!table.getFields().contains(fieldDef)) {
                table.addField(fieldDef);
            }
        }
    }

    protected void addForeignMappingFkConstraint(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute,final Map<DatabaseField, DatabaseField> srcFields, boolean cascadeOnDelete) {
        // srcFields map from the foreign key field to the target key field

        if (srcFields.size() == 0) {
            return;
        }

        List<DatabaseField> fkFields = new ArrayList<DatabaseField>();
        List<DatabaseField> targetFields = new ArrayList<DatabaseField>();

        for (DatabaseField fkField : srcFields.keySet()) {
            fkFields.add(fkField);
            targetFields.add(srcFields.get(fkField));
        }
        addJoinColumnsFkConstraint(managedClass, managedAttribute, intrinsicEntity, intrinsicAttribute,fkFields, targetFields, cascadeOnDelete);
    }

    /**
     * Build a table definition object from a database table object
     */
    protected TableDefinition getTableDefFromDBTable(DatabaseTable databaseTable) {
        return getTableDefFromDBTable(null, null, null, databaseTable);
    }
//   protected TableDefinition getTableDefFromDBTable(LinkedList<Entity> intrinsicEntity, DatabaseTable databaseTable) {
//        return getTableDefFromDBTable(intrinsicEntity, null, intrinsicEntity,databaseTable);
//    }
    protected TableDefinition getTableDefFromDBTable(ManagedClass managedClass, LinkedList<Entity> intrinsicEntity, DatabaseTable databaseTable) {
        return getTableDefFromDBTable(managedClass, null, intrinsicEntity,databaseTable);
    }

    protected TableDefinition getTableDefFromDBTable(ManagedClass managedClass, Attribute managedAttribute, LinkedList<Entity> intrinsicEntity, DatabaseTable databaseTable) {
        TableDefinition tableDefinition = this.tableMap.get(databaseTable.getName());

        if (tableDefinition == null) {
            //table not built yet, simply built it
            String tableName = databaseTable.getNameDelimited(databasePlatform);
            tableDefinition = new JPAMTableDefinition(managedClass, managedAttribute, intrinsicEntity);
            tableDefinition.setTable(databaseTable);
            tableDefinition.setName(tableName);
            tableDefinition.setQualifier(databaseTable.getTableQualifier());
            System.out.println("Table :" + tableDefinition.getName());
            if (databaseTable.hasUniqueConstraints()) {
                addUniqueKeyConstraints(tableDefinition, databaseTable.getUniqueConstraints());
            }
            if (databaseTable.hasIndexes()) {
                tableDefinition.getIndexes().addAll(databaseTable.getIndexes());
            }
            if (databaseTable.getCreationSuffix() != null) {
                tableDefinition.setCreationSuffix(databaseTable.getCreationSuffix());
            }

            // Add the foreign key constraints that were set on the table.
            if (databaseTable.hasForeignKeyConstraints()) {
                tableDefinition.setUserDefinedForeignKeyConstraints(databaseTable.getForeignKeyConstraints());
            }

            tableMap.put(databaseTable.getName(), tableDefinition);
        }

        return tableDefinition;
    }

    /**
     * Resolve the foreign key database field metadata in relation table or
     * direct collection/map table. Those metadata includes type, and maybe
     * dbtype/size/subsize if DatabaseField carries those info.
     */
    protected DatabaseField resolveDatabaseField(DatabaseField childField, DatabaseField parentField) {
        //set through the type from the source table key field to the relation or direct collection table key field.        
        DatabaseField resolvedDatabaseField = new DatabaseField();
        // find original field in the parent table, which contains actual type definitions
        // if 'resolvedParentField' is null, there is no corresponding field definition (typo?)
        DatabaseField resolvedParentField = databaseFields.get(parentField);

        resolvedDatabaseField.setName(childField.getName());
        //Table should be set, otherwise other same name field will be used wrongly because equals() is true.
        //Fix for GF#1392 the same name column for the entity and many-to-many table cause wrong pk constraint.
        resolvedDatabaseField.setTable(childField.getTable());

        // type definitions from parent field definition
        if (resolvedParentField != null) {
            resolvedDatabaseField.setType(resolvedParentField.getType());
            resolvedDatabaseField.setScale(resolvedParentField.getScale());
            resolvedDatabaseField.setLength(resolvedParentField.getLength());
            resolvedDatabaseField.setPrecision(resolvedParentField.getPrecision());
        }

        // these are defined in childField definition(see @JoinColumn)
        resolvedDatabaseField.setUnique(childField.isUnique());
        resolvedDatabaseField.setNullable(childField.isNullable());
        resolvedDatabaseField.setUpdatable(childField.isUpdatable());
        resolvedDatabaseField.setInsertable(childField.isInsertable());
        resolvedDatabaseField.setUseDelimiters(childField.shouldUseDelimiters());
        resolvedDatabaseField.useUpperCaseForComparisons(childField.getUseUpperCaseForComparisons());
        resolvedDatabaseField.setNameForComparisons(childField.getNameForComparisons());

        String columnDef = childField.getColumnDefinition();
        if (columnDef == null || columnDef.trim().equals("")) {
            // if childField has no column definition, follow the definition of the parent field
            if (resolvedParentField != null) {
                resolvedDatabaseField.setColumnDefinition(resolvedParentField.getColumnDefinition());
            }
        } else {
            resolvedDatabaseField.setColumnDefinition(columnDef);
        }

        return resolvedDatabaseField;
    }

    protected FieldDefinition getFieldDefFromDBField(DatabaseField dbField) {
        return getFieldDefFromDBField(null,null, false, false,false, dbField);
    }

    /**
     * Build a field definition object from a database field.
     */
    protected FieldDefinition getFieldDefFromDBField(LinkedList<Attribute> intrinsicAttribute, Attribute managedAttribute, boolean inverse, boolean foriegnKey,boolean relationTable, DatabaseField dbField) {
        FieldDefinition fieldDef = this.fieldMap.get(dbField);
        if (fieldDef == null) {
            //not built yet, build one
            fieldDef = new JPAMFieldDefinition(intrinsicAttribute,managedAttribute, inverse, foriegnKey, relationTable);
            fieldDef.setName(dbField.getNameDelimited(databasePlatform));

            System.out.println("Field :" + fieldDef.getName());
            //added for extending tables where the field needs to be looked up
            fieldDef.setDatabaseField(dbField);

            if (dbField.getColumnDefinition() != null && dbField.getColumnDefinition().length() > 0) {
                // This column definition would include the complete definition of the  
                // column like type, size,  "NULL/NOT NULL" clause, unique key clause 
                fieldDef.setTypeDefinition(dbField.getColumnDefinition());
            } else {
                Class fieldType = dbField.getType();
                FieldTypeDefinition fieldTypeDef = (fieldType == null) ? null : databasePlatform.getFieldTypeDefinition(fieldType);

                // Check if the user field is a String and only then allow the length specified
                // in the @Column annotation to be set on the field.
                if (fieldType != null) {
                    // If a length has been specified, set it, otherwise let the
                    // field def from individual platforms handle it.
                    if (dbField.getLength() > 0) {
                        fieldDef.setSize(dbField.getLength());
                    } else if (dbField.getPrecision() > 0) {
                        fieldDef.setSize(dbField.getPrecision());
                        fieldDef.setSubSize(dbField.getScale());
                    }
                }

                if ((fieldType == null) || (!fieldType.isPrimitive() && (fieldTypeDef == null))) {
                    //TODO: log a warning for inaccessible type or not convertable type.
                    AbstractSessionLog.getLog().log(SessionLog.CONFIG, SessionLog.METADATA, "field_type_set_to_java_lang_string", dbField.getQualifiedName(), fieldType);

                    //set the default type (lang.String) to all un-resolved java type, like null, Number, util.Date, NChar/NType, Calendar
                    //sql.Blob/Clob, Object, or unknown type). Please refer to bug 4352820.
                    fieldDef.setType(ClassConstants.STRING);
                } else {
                    //need to convert the primitive type if applied.
                    fieldDef.setType(ConversionManager.getObjectClass(fieldType));
                }

                fieldDef.setShouldAllowNull(dbField.isNullable());
                fieldDef.setUnique(dbField.isUnique());
            }
            this.fieldMap.put(dbField, fieldDef);
            this.databaseFields.put(dbField, dbField);
        }

        return fieldDef;
    }

    /**
     * Build and add a field definition object to relation table
     */
    protected void setFieldToRelationTable(LinkedList<Attribute> intrinsicAttribute, Attribute managedAttribute,  boolean inverse, DatabaseField dbField, TableDefinition table) {
        FieldDefinition fieldDef = getFieldDefFromDBField(intrinsicAttribute,managedAttribute, inverse, true, true, dbField);

        if (!table.getFields().contains(fieldDef)) {
            //only add the field once, to avoid add twice if m:m is bi-directional.
            table.addField(getFieldDefFromDBField(dbField));
            fieldDef.setIsPrimaryKey(true); // make this a PK as we will be creating constrains later
        }
    }

    protected void processAdditionalTablePkFields(ClassDescriptor descriptor) {
        // only if there are additional tables
        if (!descriptor.hasMultipleTables()) {
            return;
        }

        DatabaseTable databaseTable = null;
        Iterator dbTblIter = descriptor.getTables().iterator();
        while (dbTblIter.hasNext()) {
            databaseTable = (DatabaseTable) dbTblIter.next();
            Map<DatabaseField, DatabaseField> srcFields = descriptor.getAdditionalTablePrimaryKeyFields().get(databaseTable);
            if ((null != srcFields) && srcFields.size() > 0) {
                // srcFields is from the secondary field to the primary key field
                // Let's make fk constraint from the secondary field to the primary key field
                List<DatabaseField> fkFields = new ArrayList<>();
                List<DatabaseField> pkFields = new ArrayList<>();

                for (DatabaseField pkField : srcFields.keySet()) {
                    pkFields.add(pkField);
                    fkFields.add(srcFields.get(pkField));
                }
                addJoinColumnsFkConstraint(null,null,null,null, fkFields, pkFields, descriptor.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables());
            }
        }
    }

    protected void addJoinColumnsFkConstraint(ManagedClass managedClass, Attribute managedAttribute,LinkedList<Entity> intrinsicEntity,LinkedList<Attribute> intrinsicAttribute,List<DatabaseField> fkFields, List<DatabaseField> targetFields, boolean cascadeOnDelete) {
        assert fkFields.size() == targetFields.size();

        if (fkFields.isEmpty()) {
            return;
        }

        DatabaseField fkField = null;
        DatabaseField targetField = null;
        List<String> fkFieldNames = new ArrayList();
        List<String> targetFieldNames = new ArrayList();

        DatabaseTable sourceTable = fkFields.get(0).getTable();
        TableDefinition sourceTableDef = getTableDefFromDBTable(sourceTable);

        for (int i = 0; i < fkFields.size(); i++) {
            fkField = fkFields.get(i);
            targetField = targetFields.get(i);
            fkFieldNames.add(fkField.getNameDelimited(this.databasePlatform));
            targetFieldNames.add(targetField.getNameDelimited(this.databasePlatform));

            FieldDefinition fkFieldDef = fieldMap.get(fkField);
            FieldDefinition targetFieldDef = fieldMap.get(targetField);

            if (targetFieldDef != null) {
                // UnidirectionalOneToOneMapping case
                if (fkFieldDef == null) {
                    fkFieldDef = getFieldDefFromDBField(intrinsicAttribute, managedAttribute,false,true,false,fkField);//TODO confirm boolean
                    if (!sourceTableDef.getFields().contains(fkFieldDef)) {
                        sourceTableDef.addField(fkFieldDef);
                    }
                }

                // Set the fkFieldDef type definition to the that of the target if one is not set.
                if (fkFieldDef.getTypeDefinition() == null || fkFieldDef.getTypeDefinition().trim().equals("")) {
                    fkFieldDef.setTypeDefinition(targetFieldDef.getTypeDefinition());
                }

                // Also ensure that the type, size and subsize of the foreign key field is 
                // same as that of the original field.
                fkFieldDef.setType(targetFieldDef.getType());
                fkFieldDef.setSize(targetFieldDef.getSize());
                fkFieldDef.setSubSize(targetFieldDef.getSubSize());
            }
        }

        // add a foreign key constraint
        DatabaseTable targetTable = targetField.getTable();
        TableDefinition targetTableDef = getTableDefFromDBTable(targetTable);

        addForeignKeyConstraint(sourceTableDef, targetTableDef, fkFieldNames, targetFieldNames, cascadeOnDelete);
    }

    /**
     * Add a foreign key constraint to the source table.
     */
    protected void addForeignKeyConstraint(TableDefinition sourceTableDef, TableDefinition targetTableDef,
            List<String> fkFields, List<String> targetFields, boolean cascadeOnDelete) {

        // Only generate FK constraints if instructed to
        if (!this.generateFKConstraints) {
            return;
        }
        assert fkFields.size() > 0 && fkFields.size() == targetFields.size();

        // target keys could be primary keys or candidate(unique) keys of the target table
        List<String> fkFieldNames = fkFields;
        List<String> targetFieldNames = targetFields;

        if (fkFields.size() > 1) {
            // if composite key, we should consider the order of keys.
            // Foreign Key constraint should follow the primary/unique key order of the target table.
            // e.g. if the primary key constraint of the target table is (p2, p1),
            // foreign key constraint should be "(f2, f1) REFERENCES TARGET (p2, p1)".

            // we try to reorder keys using primary keys or unique keys order of the target table,
            // but if we might not resolve it due to incorrect field name, then let it as it is.
            // This will trigger underlying database exception so users can recognize errors.
            boolean resolved = false;
            boolean error = false;

            Map<String, String> targetToFkField = new LinkedHashMap<String, String>();
            for (int index = 0; index < fkFields.size(); index++) {
                String targetField = targetFields.get(index);
                if (targetToFkField.containsKey(targetField)) {
                    //target key column appears more than once
                    error = true;
                    break;
                }
                targetToFkField.put(targetField, fkFields.get(index));
            }

            List<String> orderedFkFields = new ArrayList<String>(fkFields.size());
            List<String> orderedTargetFields = new ArrayList<String>(targetFields.size());

            if (!error) {
                // if target fields are primary keys
                resolved = true;
                for (String pkField : targetTableDef.getPrimaryKeyFieldNames()) {
                    String fkField = targetToFkField.get(pkField);
                    if (fkField == null) {
                        //primary key column not found
                        resolved = false;
                        break;
                    }
                    orderedFkFields.add(fkField);
                    orderedTargetFields.add(pkField);
                }
            }

            if (!error && !resolved) {
                // if target fields are unique keys
                for (UniqueKeyConstraint uniqueConstraint : targetTableDef.getUniqueKeys()) {
                    orderedFkFields.clear();
                    orderedTargetFields.clear();

                    resolved = true;
                    for (String ukField : uniqueConstraint.getSourceFields()) {
                        String fkField = targetToFkField.get(ukField);
                        if (fkField == null) {
                            //unique key column not found
                            resolved = false;
                            break;
                        }
                        orderedFkFields.add(fkField);
                        orderedTargetFields.add(ukField);
                    }
                    if (resolved) {
                        break;
                    }
                }
            }

            if (resolved) {
                fkFieldNames = orderedFkFields;
                targetFieldNames = orderedTargetFields;
            }
        }

        // For bidirectional relationships both side of mapping will make the same FK constraint twice.
        // TableDefinition.addForeignKeyConstraint() will ignore the same FK constraint.
        ForeignKeyConstraint constraint = ((JPAMTableDefinition) sourceTableDef).buildForeignKeyConstraint(fkFieldNames, targetFieldNames,
                targetTableDef, this.databasePlatform);
        constraint.setShouldCascadeOnDelete(cascadeOnDelete);
        sourceTableDef.addForeignKeyConstraint(constraint);
    }

    protected void addUniqueKeyConstraints(TableDefinition sourceTableDef, Map<String, List<List<String>>> uniqueConstraintsMap) {
        int serialNumber = -1;

        for (String name : uniqueConstraintsMap.keySet()) {
            List<List<String>> uniqueConstraints = uniqueConstraintsMap.get(name);

            for (List<String> uniqueConstraint : uniqueConstraints) {
                if (uniqueConstraint != null) {
                    // To keep the serialNumber consecutive, increment it only
                    // if the name is not specified.
                    if (name == null || name.equals("")) {
                        serialNumber++;
                    }

                    sourceTableDef.addUniqueKeyConstraint(sourceTableDef.buildUniqueKeyConstraint(name, uniqueConstraint, serialNumber, databasePlatform));
                }
            }
        }
    }
}
