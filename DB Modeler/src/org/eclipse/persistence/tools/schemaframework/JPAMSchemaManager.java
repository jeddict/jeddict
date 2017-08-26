/** *****************************************************************************
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
 *     Dies Koper - add support for creating indices on tables
 *     01/11/2013-2.5 Guy Pelletier
 *       - 389090: JPA 2.1 DDL Generation Support
 *     02/04/2013-2.5 Guy Pelletier
 *       - 389090: JPA 2.1 DDL Generation Support
 *     04/12/2013-2.5 Guy Pelletier
 *       - 405640: JPA 2.1 schema generation drop operation fails to include dropping defaulted fk constraints.
 ***************************************************************************** */
package org.eclipse.persistence.tools.schemaframework;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.spec.DBTable;

/**
 * <p>
 * <b>Purpose</b>: Define all user level protocol for development time database
 * manipulation.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define protocol for schema creation.
 * <li> Define any useful testing specific protocol.
 * </ul>
 */
public class JPAMSchemaManager {

    protected DatabaseSessionImpl session;
    protected Writer createSchemaWriter;
    protected Writer dropSchemaWriter;
    protected boolean createSQLFiles = true; //if true, schema writer will add terminator string.
    protected JPAMTableCreator defaultTableCreator;

    /**
     * Allow table creator to occur "fast" by just deleting all the rows.
     */
    public static boolean FAST_TABLE_CREATOR = false;

    /**
     * Allow replacing of table to force the drop, this may require several
     * passes.
     */
    public static boolean FORCE_DROP = true;

    /**
     * Flag to determine if database schemas should be created during DDL
     * generation
     */
    protected boolean createDatabaseSchemas = false;
    protected HashSet<String> createdDatabaseSchemas = new HashSet<>();
    protected HashSet<String> createdDatabaseSchemasOnDatabase = new HashSet<>();
    protected HashMap<String, DatabaseObjectDefinition> dropDatabaseSchemas = new HashMap<>();

    private DBMapping dbMapping;

    public JPAMSchemaManager(DBMapping dbMapping, DatabaseSessionImpl session) {
        this.dbMapping = dbMapping;
        this.session = session;
    }

    public JPAMSchemaManager(org.eclipse.persistence.sessions.DatabaseSession session) {
        this.session = ((DatabaseSessionImpl) session);
    }

    protected Writer getDropSchemaWriter() {
        return (dropSchemaWriter == null) ? createSchemaWriter : dropSchemaWriter;
    }

    /**
     * PUBLIC: If the schema manager is writing to a writer, append this string
     * to that writer.
     */
    public void appendToDDLWriter(String stringToWrite) {
        // If this method is called, we know that it is the old case and
        // it would not matter which schemaWriter we use as both the
        // create and drop schemaWriters are essentially the same.
        // So just pick one.
        appendToDDLWriter(createSchemaWriter, stringToWrite);
    }

    public void appendToDDLWriter(Writer schemaWriter, String stringToWrite) {
        if (schemaWriter == null) {
            return; //do nothing.  Ignore append request
        }

        try {
            schemaWriter.write(stringToWrite);
            schemaWriter.flush();
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL: builds the field names based on the type read in from the
     * builder
     */
    public void buildFieldTypes(TableDefinition tableDef) {
        ((JPAMTableDefinition) tableDef).buildFieldTypes(getSession());//        ((JPAMTableDefinition)tableDef).createFieldTypes(getSession());
    }

    /**
     * PUBLIC: Close the schema writer.
     */
    public void closeDDLWriter() {
        closeDDLWriter(createSchemaWriter);
        closeDDLWriter(dropSchemaWriter);
        createSchemaWriter = null;
        dropSchemaWriter = null;
    }

    public void closeDDLWriter(Writer schemaWriter) {
        if (schemaWriter == null) {
            return;
        }

        try {
            schemaWriter.flush();
            schemaWriter.close();
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL: Called when dropping tables. Will build a map of those schemas
     * (and a table that references it) that should be dropped.
     */
    protected void collectDatabaseSchemasForDrop(DatabaseObjectDefinition databaseObjectDefinition) {
        if (createDatabaseSchemas && databaseObjectDefinition.hasDatabaseSchema()) {
            if (!dropDatabaseSchemas.containsKey(databaseObjectDefinition.getDatabaseSchema())) {
                dropDatabaseSchemas.put(databaseObjectDefinition.getDatabaseSchema(), databaseObjectDefinition);
            }
        }
    }

    /**
     * Use the table definition to add the constraints to the database, this is
     * normally done in two steps to avoid dependencies.
     */
    public void createConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                tableDefinition.createConstraintsOnDatabase(getSession());
            } else {
                tableDefinition.setCreateSQLFiles(createSQLFiles);
                tableDefinition.createConstraints(getSession(), createSchemaWriter);
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    void createUniqueConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        if (shouldWriteToDatabase()) {
            ((JPAMTableDefinition) tableDefinition).createUniqueConstraintsOnDatabase(getSession());
        } else {
            tableDefinition.setCreateSQLFiles(createSQLFiles);
            tableDefinition.createUniqueConstraints(getSession(), createSchemaWriter);
        }

        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || tableDefinition.getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            return;
        }

        for (UniqueKeyConstraint uniqueKey : tableDefinition.getUniqueKeys()) {
            String query = tableDefinition.buildUniqueConstraintCreationWriter(session, uniqueKey, new StringWriter()).toString();
            System.out.println("unique : " + query);
        }
    }

    void createForeignConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        //        if (shouldWriteToDatabase()) {
        //            tableDefinition.createForeignConstraintsOnDatabase(getSession());
        //        } else {
        //            tableDefinition.setCreateSQLFiles(createSQLFiles);
        //            tableDefinition.createForeignConstraints(getSession(), createSchemaWriter);
        //        }

        if ((!session.getPlatform().supportsForeignKeyConstraints()) || tableDefinition.getForeignKeyMap().isEmpty()) {
            return;
        }

        for (ForeignKeyConstraint foreignKey : tableDefinition.getForeignKeyMap().values()) {
            if (!foreignKey.disableForeignKey()) {
                String query = tableDefinition.buildConstraintCreationWriter(session, foreignKey, new StringWriter()).toString();
                getDBMapping().putAlterQuery(tableDefinition.getName(), query);

            }
        }

    }

    public void createMapping(DatabaseObjectDefinition databaseObjectDefinition) {
        JPAMTableDefinition tableDefinition = ((JPAMTableDefinition) databaseObjectDefinition);
        tableDefinition.buildDBTable(session, getDBMapping());

    }

    public void createReference(DatabaseObjectDefinition databaseObjectDefinition) {

        JPAMTableDefinition tableDefinition = ((JPAMTableDefinition) databaseObjectDefinition);
        DBTable sourceTable = getDBMapping().getTable(tableDefinition.getFullName());

        if ((!session.getPlatform().supportsForeignKeyConstraints()) || tableDefinition.getForeignKeyMap().isEmpty()) {
            return;
            //TODO : issue #65
        }

        for (ForeignKeyConstraint foreignKey : tableDefinition.getForeignKeyMap().values()) {
            if (!foreignKey.disableForeignKey()) {
                //                String query = buildConstraintCreationWriter(session, foreignKey, new StringWriter()).toString();
                //foreignKey.appendDBString(new StringWriter(), session);

                if (foreignKey.hasForeignKeyDefinition()) {
                    //TODO : foreignKey.getForeignKeyDefinition()
                } else {
                    DBTable targetTable = getDBMapping().getTable(foreignKey.getTargetTable());

                    for (int i = 0; i < foreignKey.getSourceFields().size(); i++) {
                        DBColumn sourceColumn = sourceTable.getColumn(foreignKey.getSourceFields().get(i));
                        DBColumn targetColumn = targetTable.getColumn(foreignKey.getTargetFields().get(i));
                        sourceColumn.setForeignKey(true);
                        sourceColumn.setReferenceColumn(targetColumn);
                        sourceColumn.setReferenceTable(targetTable);
                    }
                }

            }
        }

    }

    /**
     * Use the definition object to create the schema entity on the database.
     * This is used for creating tables, views, procedures ... etc ...
     */
    public String createObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        //        boolean usesBatchWriting = false;
        //
        //        if (getSession().getPlatform().usesBatchWriting()) {
        //            usesBatchWriting = true;
        //            getSession().getPlatform().setUsesBatchWriting(false);
        //        }

        //        try {
        //            if (shouldWriteToDatabase()) {
        //                // Check if we should create a database schema for this
        //                // database object definition on the database. It is only
        //                // create once and for the first database object definition
        //                // that references it.
        //                if (shouldCreateDatabaseSchema(databaseObjectDefinition, createdDatabaseSchemasOnDatabase)) {
        //                    databaseObjectDefinition.createDatabaseSchemaOnDatabase(getSession(), createdDatabaseSchemasOnDatabase);
        //                }
        //
        //                databaseObjectDefinition.createOnDatabase(getSession());
        String query = null;
        if (databaseObjectDefinition instanceof TableDefinition) {
            query = ((TableDefinition) databaseObjectDefinition).buildCreationWriter(session, new StringWriter()).toString();
        } else if (databaseObjectDefinition instanceof SequenceObjectDefinition) {
            query = ((SequenceObjectDefinition) databaseObjectDefinition).buildCreationWriter(session, new StringWriter()).toString();
        } else if (databaseObjectDefinition instanceof TableSequenceDefinition) {
            query = ((TableSequenceDefinition) databaseObjectDefinition).buildCreationWriter(session, new StringWriter()).toString();
        }
//        System.out.println("query : " + query);
        //            } else {
        //                // Check if we should create a database schema for this
        //                // database object definition on the database. It is only
        //                // create once and for the first database object definition
        //                // that references it.
        //        if (shouldCreateDatabaseSchema(databaseObjectDefinition, createdDatabaseSchemas)) {
        //        databaseObjectDefinition.createDatabaseSchema(getSession(), createSchemaWriter, createdDatabaseSchemas);
        //        appendToDDLWriter(createSchemaWriter, "\n");
        ////        }
        //
        //        databaseObjectDefinition.createObject(getSession(), createSchemaWriter);
        //        if (createSQLFiles) {
        //            this.appendToDDLWriter(createSchemaWriter, getSession().getPlatform().getStoredProcedureTerminationToken());
        //        }
        //        this.appendToDDLWriter(createSchemaWriter, "\n");
        //            }
        //            databaseObjectDefinition.postCreateObject(getSession(), createSchemaWriter, createSQLFiles);
        //        } finally {
        //            if (usesBatchWriting) {
        //                getSession().getPlatform().setUsesBatchWriting(true);
        //            }
        //        }
        return query;
    }

    /**
     * Create all the receiver's sequences on the database for all of the loaded
     * descriptors.
     */
    public void createSequences() throws EclipseLinkException {
        createOrReplaceSequences(true);
    }

    /**
     * INTERNAL: Set to true if database schemas should be built during the DDL
     * generation.
     */
    public void setCreateDatabaseSchemas(boolean createDatabaseSchemas) {
        this.createDatabaseSchemas = createDatabaseSchemas;
    }

    public void setCreateSQLFiles(boolean genFlag) {
        this.createSQLFiles = genFlag;
    }

    /**
     * Drop and recreate all the receiver's sequences on the database for all of
     * the loaded descriptors.
     */
    public void replaceSequences() throws EclipseLinkException {
        createOrReplaceSequences(false);
    }

    /**
     * Common implementor for createSequence and replaceSequence
     *
     * @param create - true to create the sequences, false to replace them
     * (dropped then create)
     */
    protected void createOrReplaceSequences(boolean create) throws EclipseLinkException {
        createOrReplaceSequences(create, create);
    }

    /**
     * Common implementor for createSequence and replaceSequence, distinguishes
     * between sequence tables and sequence objects
     *
     * @param createSequenceTables - true to create the sequences tables, false
     * to replace them (dropped then create)
     * @param createSequences - true to create the sequences objects, false to
     * replace them (dropped then create)
     */
    protected void createOrReplaceSequences(boolean createSequenceTables, boolean createSequences) throws EclipseLinkException {
        // PERF: Allow a special "fast" flag to be set on the session causes a delete from the table instead of a replace.
        boolean fast = FAST_TABLE_CREATOR;
        if (fast) {
            // Assume sequences already created.
            return;
        }

        processSequenceDefinitions(createSequenceTables, createSequences, true);
    }

    /**
     * This will drop the database schemas if managing the database schemas.
     */
    protected void dropSequences() {
        processSequenceDefinitions(false, false, false);
    }

    /**
     * Method creates database tables/objects. If create is true, it will
     * attempt to create the object and silently ignore exceptions. If create is
     * false, it will drop the object ignoring any exceptions, then create it if
     * the replace flag is true (otherwise a drop only).
     *
     * @param definition - the sequence definition
     * @param createTables - true if table sequence table definitions should be
     * created.
     * @param createSequences - true if the sequence definition should be
     * created, false if it should be dropped.
     * @param replace - true if table definitions and sequence definitions
     * should be replaced.
     * @throws EclipseLinkException
     */
    protected void processSequenceDefinition(SequenceDefinition definition, final boolean createTables, final boolean createSequences, final boolean replace, HashSet<String> createdTableNames, HashSet<String> droppedTableNames) throws EclipseLinkException {
        try {
            // Handle the table definitions first.
            if (definition.isTableSequenceDefinition()) {
                TableDefinition tableDefinition = ((TableSequenceDefinition) definition).buildTableDefinition();
                // Check that we haven't already created the table.
                if (!createdTableNames.contains(tableDefinition.getFullName())) {
                    createdTableNames.add(tableDefinition.getFullName());

                    // Check if it exists on the database. NOTE: when writing to scripts only with
                    // no connection, this of course will always return false hence the need for
                    // the createdSequenceTableNames collection above.
                    boolean exists = checkTableExists(tableDefinition);

                    if (createTables) {
                        // Don't create it if it already exists on the database.
                        // In all all other cases, write it out.
                        if ((shouldWriteToDatabase() && !exists) || !shouldWriteToDatabase()) {
                            String query = createObject(tableDefinition);
                            getDBMapping().putQuery(tableDefinition.getName(), query);
                        }
                    } else // Don't check exists since if writing to scripts only with no connection,
                    // we'll never write the sql out. When executing to the database, the drop
                    // will fail and we'll ignore it. Note: TableSequenceDefinition's will drop
                    // their table definitions as needed (i.e.) when the jpa create database
                    // schemas flag is set and the table definition has a schema. Otherwise,
                    // we should not drop sequence tables since they may be re-used across
                    // persistence units (default behavior right now).
                    // TODO: We should drop them really unless it is the default SEQUENCE table??
                    {
                        if (replace) {
                            dropObject(tableDefinition);
                            String query = createObject(tableDefinition);
                            getDBMapping().putQuery(tableDefinition.getName(), query);
                        }
                    }
                }
            }
        } catch (DatabaseException exception) {
            // ignore any database exceptions here and keep going.
        }

        // Handle the sequence objects second.
        try {
            if (createSequences) {
                String query = createObject(definition);
                getDBMapping().putQuery(definition.getName(), query);
            } else {
                try {
                    // If the sequence definition has and will drop a table definition, then check
                    // if we have already dropped it. Table definitions are dropped as a whole if
                    // they have a schema name and the jpa create database schemas flag is set to true.
                    if (definition.isTableSequenceDefinition()) {
                        if (((TableSequenceDefinition) definition).shouldDropTableDefinition()) {
                            String tableDefinitionTableName = ((TableSequenceDefinition) definition).getSequenceTableName();

                            // If we have already dropped it, move on, otherwise drop it!
                            if (droppedTableNames.contains(tableDefinitionTableName)) {
                                return; // cut out early, we've already seen this table.
                            } else {
                                droppedTableNames.add(tableDefinitionTableName);
                            }
                        }
                    }

                    dropObject(definition);
                } catch (DatabaseException exception) {
                    // Ignore table not found for first creation
                }

                // Drop only scripts we don't want to replace.
                if (replace) {
                    String query = createObject(definition);
                    getDBMapping().putQuery(definition.getName(), query);
                }
            }
        } catch (Exception exception) {
            // ignore any database exceptions here and keep chugging
        }
    }

    /**
     * Common implementor for createSequence and replaceSequence, distinguishes
     * between sequence tables and sequence objects
     *
     * @param createSequenceTables - true to create the sequences tables, false
     * to replace them (dropped then create)
     * @param createSequences - true to create the sequences objects, false to
     * replace them (dropped then create)
     * @param replaceSequences - true to actually replace, false to drop only.
     */
    protected void processSequenceDefinitions(boolean createSequenceTables, boolean createSequences, boolean replaceSequences) throws EclipseLinkException {
        Sequencing sequencing = getSession().getSequencing();

        // Not required on Sybase native etc.
        if (sequencing != null && sequencing.whenShouldAcquireValueForAll() != Sequencing.AFTER_INSERT) {
            // Build the sequence definitions.
            HashSet<SequenceDefinition> sequenceDefinitions = buildSequenceDefinitions();

            // Now process the sequence definitions.
            // CR 3870467, do not log stack
            boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();
            session.getSessionLog().setShouldLogExceptionStackTrace(false);
            HashSet<String> createdSequenceTableNames = new HashSet();
            HashSet<String> droppedSequenceTableNames = new HashSet();

            for (SequenceDefinition sequenceDefinition : sequenceDefinitions) {
                processSequenceDefinition(sequenceDefinition, createSequenceTables, createSequences, replaceSequences, createdSequenceTableNames, droppedSequenceTableNames);
            }

            // Set the log stack trace flag back.
            session.getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
    }

    /**
     * INTERNAL: Build the sequence definitions.
     */
    protected HashSet<SequenceDefinition> buildSequenceDefinitions() {
        // Remember the processed - to handle each sequence just once.
        HashSet processedSequenceNames = new HashSet();
        HashSet<SequenceDefinition> sequenceDefinitions = new HashSet<>();

        for (ClassDescriptor descriptor : getSession().getDescriptors().values()) {
            if (descriptor.usesSequenceNumbers()) {
                String seqName = descriptor.getSequenceNumberName();

                if (seqName == null) {
                    seqName = getSession().getDatasourcePlatform().getDefaultSequence().getName();
                }

                if (!processedSequenceNames.contains(seqName)) {
                    processedSequenceNames.add(seqName);

                    Sequence sequence = getSession().getDatasourcePlatform().getSequence(seqName);
                    SequenceDefinition sequenceDefinition = buildSequenceDefinition(sequence);

                    if (sequenceDefinition != null) {
                        sequenceDefinitions.add(sequenceDefinition);
                    }
                }
            }
        }

        return sequenceDefinitions;
    }

    /**
     * Check if the table exists by issuing a select.
     */
    public boolean checkTableExists(TableDefinition table) {
        String column = null;
        for (FieldDefinition field : table.getFields()) {
            if (column == null) {
                column = field.getName();
            } else if (field.isPrimaryKey()) {
                column = field.getName();
                break;
            }
        }
        String sql = "SELECT " + column + " FROM " + table.getFullName() + " WHERE " + column + " <> " + column;
        DataReadQuery query = new DataReadQuery(sql);
        query.setMaxRows(1);
        //        boolean loggingOff = session.isLoggingOff();
        try {
            //            session.setLoggingOff(true);
            session.executeQuery(query);
            return true;
        } catch (Exception notFound) {
            return false;
        } finally {
            //            session.setLoggingOff(loggingOff);
        }
    }

    protected SequenceDefinition buildSequenceDefinition(Sequence sequence) {
        if (sequence.shouldAcquireValueAfterInsert()) {
            return null;
        }
        if (sequence instanceof TableSequence
                || (sequence instanceof DefaultSequence && ((DefaultSequence) sequence).getDefaultSequence() instanceof TableSequence)) {
            return new TableSequenceDefinition(sequence, createDatabaseSchemas);
        } else if (sequence instanceof UnaryTableSequence
                || (sequence instanceof DefaultSequence && ((DefaultSequence) sequence).getDefaultSequence() instanceof UnaryTableSequence)) {
            return new UnaryTableSequenceDefinition(sequence, createDatabaseSchemas);
        } else if (sequence instanceof NativeSequence
                || (sequence instanceof DefaultSequence && ((DefaultSequence) sequence).getDefaultSequence() instanceof NativeSequence)) {
            NativeSequence nativeSequence = null;
            if (sequence instanceof NativeSequence) {
                nativeSequence = (NativeSequence) sequence;
            } else {
                nativeSequence = (NativeSequence) ((DefaultSequence) sequence).getDefaultSequence();
            }
            if (nativeSequence.hasDelegateSequence()) {
                return buildSequenceDefinition(((NativeSequence) sequence).getDelegateSequence());
            }
            return new SequenceObjectDefinition(sequence);
        } else {
            return null;
        }
    }

    /**
     * Use the table definition to drop the constraints from the table, this is
     * normally done in two steps to avoid dependencies.
     */
    public void dropConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                tableDefinition.dropConstraintsOnDatabase(getSession());
            } else {
                tableDefinition.setCreateSQLFiles(createSQLFiles);
                tableDefinition.dropConstraints(getSession(), getDropSchemaWriter());
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Use the definition object to drop the schema entity from the database.
     * This is used for dropping tables, views, procedures ... etc ...
     */
    public void dropObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            // If the object definition has a database schema collect it.
            collectDatabaseSchemasForDrop(databaseObjectDefinition);

            databaseObjectDefinition.preDropObject(getSession(), getDropSchemaWriter(), this.createSQLFiles);
            if (shouldWriteToDatabase()) {
                // drop actual object
                databaseObjectDefinition.dropFromDatabase(getSession());
            } else {
                Writer dropSchemaWriter = getDropSchemaWriter();

                // drop actual object
                databaseObjectDefinition.dropObject(getSession(), dropSchemaWriter, createSQLFiles);
                if (this.createSQLFiles) {
                    this.appendToDDLWriter(dropSchemaWriter, getSession().getPlatform().getStoredProcedureTerminationToken());
                }
                this.appendToDDLWriter(dropSchemaWriter, "\n");
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Drop (delete) the table named tableName from the database.
     */
    public void dropTable(String tableName) throws EclipseLinkException {
        TableDefinition tableDefinition;

        tableDefinition = new TableDefinition();
        tableDefinition.setName(tableName);
        dropObject(tableDefinition);
    }

    /**
     * INTERNAL: Close the schema writer when the schema manger is garbage
     * collected
     */
    @Override
    public void finalize() {
        try {
            this.closeDDLWriter();
        } catch (ValidationException exception) {
            // do nothing
        }
    }

    /**
     * Return the appropriate accessor. Assume we are dealing with a JDBC
     * accessor.
     */
    protected DatabaseAccessor getAccessor() {
        return (DatabaseAccessor) getSession().getAccessor();
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>
     * Each column description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>COLUMN_NAME</B> String {@literal =>} column name
     * <LI><B>DATA_TYPE</B> short {@literal =>} SQL type from java.sql.Types
     * <LI><B>TYPE_NAME</B> String {@literal =>} Data source dependent type name
     * <LI><B>COLUMN_SIZE</B> int {@literal =>} column size. For char or date
     * types this is the maximum number of characters, for numeric or decimal
     * types this is precision.
     * <LI><B>BUFFER_LENGTH</B> is not used.
     * <LI><B>DECIMAL_DIGITS</B> int {@literal =>} the number of fractional
     * digits
     * <LI><B>NUM_PREC_RADIX</B> int {@literal =>} Radix (typically either 10 or
     * 2)
     * <LI><B>NULLABLE</B> int {@literal =>} is NULL allowed?
     * <UL>
     * <LI> columnNoNulls - might not allow NULL values
     * <LI> columnNullable - definitely allows NULL values
     * <LI> columnNullableUnknown - nullability unknown
     * </UL>
     * <LI><B>REMARKS</B> String {@literal =>} comment describing column (may be
     * null)
     * <LI><B>COLUMN_DEF</B> String {@literal =>} default value (may be null)
     * <LI><B>SQL_DATA_TYPE</B> int {@literal =>} unused
     * <LI><B>SQL_DATETIME_SUB</B> int {@literal =>} unused
     * <LI><B>CHAR_OCTET_LENGTH</B> int {@literal =>} for char types the maximum
     * number of bytes in the column
     * <LI><B>ORDINAL_POSITION</B> int {@literal =>} index of column in table
     * (starting at 1)
     * <LI><B>IS_NULLABLE</B> String {@literal =>} "NO" means column definitely
     * does not allow NULL values; "YES" means the column might allow NULL
     * values. An empty string means nobody knows.
     * </OL>
     *
     * @param tableName a table name pattern
     * @return a Vector of Records.
     */
    public Vector getAllColumnNames(String tableName) throws DatabaseException {
        return getAccessor().getColumnInfo(null, null, tableName, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>
     * Each column description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>COLUMN_NAME</B> String {@literal =>} column name
     * <LI><B>DATA_TYPE</B> short {@literal =>} SQL type from java.sql.Types
     * <LI><B>TYPE_NAME</B> String {@literal =>} Data source dependent type name
     * <LI><B>COLUMN_SIZE</B> int {@literal =>} column size. For char or date
     * types this is the maximum number of characters, for numeric or decimal
     * types this is precision.
     * <LI><B>BUFFER_LENGTH</B> is not used.
     * <LI><B>DECIMAL_DIGITS</B> int {@literal =>} the number of fractional
     * digits
     * <LI><B>NUM_PREC_RADIX</B> int {@literal =>} Radix (typically either 10 or
     * 2)
     * <LI><B>NULLABLE</B> int {@literal =>} is NULL allowed?
     * <UL>
     * <LI> columnNoNulls - might not allow NULL values
     * <LI> columnNullable - definitely allows NULL values
     * <LI> columnNullableUnknown - nullability unknown
     * </UL>
     * <LI><B>REMARKS</B> String {@literal =>} comment describing column (may be
     * null)
     * <LI><B>COLUMN_DEF</B> String {@literal =>} default value (may be null)
     * <LI><B>SQL_DATA_TYPE</B> int {@literal =>} unused
     * <LI><B>SQL_DATETIME_SUB</B> int {@literal =>} unused
     * <LI><B>CHAR_OCTET_LENGTH</B> int {@literal =>} for char types the maximum
     * number of bytes in the column
     * <LI><B>ORDINAL_POSITION</B> int {@literal =>} index of column in table
     * (starting at 1)
     * <LI><B>IS_NULLABLE</B> String {@literal =>} "NO" means column definitely
     * does not allow NULL values; "YES" means the column might allow NULL
     * values. An empty string means nobody knows.
     * </OL>
     *
     * @param creatorName a schema name pattern; "" retrieves those without a
     * schema
     * @param tableName a table name pattern
     * @return a Vector of Records.
     */
    public Vector getAllColumnNames(String creatorName, String tableName) throws DatabaseException {
        return getAccessor().getColumnInfo(null, creatorName, tableName, null, getSession());
    }

    /**
     * Get a description of tables available in a catalog.
     *
     * <P>
     * Each table description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>TABLE_TYPE</B> String {@literal =>} table type. Typical types are
     * "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY",
     * "ALIAS", "SYNONYM".
     * <LI><B>REMARKS</B> String {@literal =>} explanatory comment on the table
     * </OL>
     *
     * <P>
     * <B>Note:</B> Some databases may not return information for all tables.
     *
     * @return a Vector of Records.
     */
    public Vector getAllTableNames() throws DatabaseException {
        return getAccessor().getTableInfo(null, null, null, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>
     * Each column description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>COLUMN_NAME</B> String {@literal =>} column name
     * <LI><B>DATA_TYPE</B> short {@literal =>} SQL type from java.sql.Types
     * <LI><B>TYPE_NAME</B> String {@literal =>} Data source dependent type name
     * <LI><B>COLUMN_SIZE</B> int {@literal =>} column size. For char or date
     * types this is the maximum number of characters, for numeric or decimal
     * types this is precision.
     * <LI><B>BUFFER_LENGTH</B> is not used.
     * <LI><B>DECIMAL_DIGITS</B> int {@literal =>} the number of fractional
     * digits
     * <LI><B>NUM_PREC_RADIX</B> int {@literal =>} Radix (typically either 10 or
     * 2)
     * <LI><B>NULLABLE</B> int {@literal =>} is NULL allowed?
     * <UL>
     * <LI> columnNoNulls - might not allow NULL values
     * <LI> columnNullable - definitely allows NULL values
     * <LI> columnNullableUnknown - nullability unknown
     * </UL>
     * <LI><B>REMARKS</B> String {@literal =>} comment describing column (may be
     * null)
     * <LI><B>COLUMN_DEF</B> String {@literal =>} default value (may be null)
     * <LI><B>SQL_DATA_TYPE</B> int {@literal =>} unused
     * <LI><B>SQL_DATETIME_SUB</B> int {@literal =>} unused
     * <LI><B>CHAR_OCTET_LENGTH</B> int {@literal =>} for char types the maximum
     * number of bytes in the column
     * <LI><B>ORDINAL_POSITION</B> int {@literal =>} index of column in table
     * (starting at 1)
     * <LI><B>IS_NULLABLE</B> String {@literal =>} "NO" means column definitely
     * does not allow NULL values; "YES" means the column might allow NULL
     * values. An empty string means nobody knows.
     * </OL>
     *
     * @param creatorName a schema name pattern; "" retrieves those without a
     * schema
     * @return a Vector of Records.
     */
    public Vector getAllTableNames(String creatorName) throws DatabaseException {
        return getAccessor().getTableInfo(null, creatorName, null, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>
     * Only column descriptions matching the catalog, schema, table and column
     * name criteria are returned. They are ordered by TABLE_SCHEM, TABLE_NAME
     * and ORDINAL_POSITION.
     *
     * <P>
     * Each column description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>COLUMN_NAME</B> String {@literal =>} column name
     * <LI><B>DATA_TYPE</B> short {@literal =>} SQL type from java.sql.Types
     * <LI><B>TYPE_NAME</B> String {@literal =>} Data source dependent type name
     * <LI><B>COLUMN_SIZE</B> int {@literal =>} column size. For char or date
     * types this is the maximum number of characters, for numeric or decimal
     * types this is precision.
     * <LI><B>BUFFER_LENGTH</B> is not used.
     * <LI><B>DECIMAL_DIGITS</B> int {@literal =>} the number of fractional
     * digits
     * <LI><B>NUM_PREC_RADIX</B> int {@literal =>} Radix (typically either 10 or
     * 2)
     * <LI><B>NULLABLE</B> int {@literal =>} is NULL allowed?
     * <UL>
     * <LI> columnNoNulls - might not allow NULL values
     * <LI> columnNullable - definitely allows NULL values
     * <LI> columnNullableUnknown - nullability unknown
     * </UL>
     * <LI><B>REMARKS</B> String {@literal =>} comment describing column (may be
     * null)
     * <LI><B>COLUMN_DEF</B> String {@literal =>} default value (may be null)
     * <LI><B>SQL_DATA_TYPE</B> int {@literal =>} unused
     * <LI><B>SQL_DATETIME_SUB</B> int {@literal =>} unused
     * <LI><B>CHAR_OCTET_LENGTH</B> int {@literal =>} for char types the maximum
     * number of bytes in the column
     * <LI><B>ORDINAL_POSITION</B> int {@literal =>} index of column in table
     * (starting at 1)
     * <LI><B>IS_NULLABLE</B> String {@literal =>} "NO" means column definitely
     * does not allow NULL values; "YES" means the column might allow NULL
     * values. An empty string means nobody knows.
     * </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a catalog; null
     * means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those without a schema
     * @param tableName a table name pattern
     * @param columnName a column name pattern
     * @return a Vector of Records.
     */
    public Vector getColumnInfo(String catalog, String schema, String tableName, String columnName) throws DatabaseException {
        return getAccessor().getColumnInfo(catalog, schema, tableName, columnName, getSession());
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * Get a description of tables available in a catalog.
     *
     * <P>
     * Only table descriptions matching the catalog, schema, table name and type
     * criteria are returned. They are ordered by TABLE_TYPE, TABLE_SCHEM and
     * TABLE_NAME.
     *
     * <P>
     * Each table description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@literal =>} table catalog (may be null)
     * <LI><B>TABLE_SCHEM</B> String {@literal =>} table schema (may be null)
     * <LI><B>TABLE_NAME</B> String {@literal =>} table name
     * <LI><B>TABLE_TYPE</B> String {@literal =>} table type. Typical types are
     * "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY",
     * "ALIAS", "SYNONYM".
     * <LI><B>REMARKS</B> String {@literal =>} explanatory comment on the table
     * </OL>
     *
     * <P>
     * <B>Note:</B> Some databases may not return information for all tables.
     *
     * @param catalog a catalog name; "" retrieves those without a catalog; null
     * means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those without a schema
     * @param tableName a table name pattern
     * @param types a list of table types to include; null returns all types
     * @return a Vector of Records.
     */
    public Vector getTableInfo(String catalog, String schema, String tableName, String[] types) throws DatabaseException {
        return getAccessor().getTableInfo(catalog, schema, tableName, types, getSession());
    }

    /**
     * PUBLIC: Output all DDL statements directly to the database.
     */
    public void outputDDLToDatabase() {
        this.createSchemaWriter = null;
        this.dropSchemaWriter = null;
    }

    /**
     * PUBLIC: Output all DDL statements to a file writer specified by the name
     * in the parameter.
     */
    public void outputDDLToFile(String fileName) {
        this.createSchemaWriter = getWriter(fileName);
    }

    public void outputCreateDDLToFile(String fileName) {
        this.createSchemaWriter = getWriter(fileName);
    }

    public void outputDropDDLToFile(String fileName) {
        this.dropSchemaWriter = getWriter(fileName);
    }

    protected Writer getWriter(String fileName) {
        try {
            return new java.io.FileWriter(fileName);
        } catch (java.io.IOException ioException) {
            // Try a url next, otherwise throw the existing error.
            try {
                URL url = new URL(fileName);
                return new java.io.FileWriter(url.getFile());
            } catch (Exception e) {
                // MalformedURLException and IOException
                throw ValidationException.fileError(ioException);
            }
        }
    }

    /**
     * PUBLIC: Output all DDL statements to a writer specified in the parameter.
     */
    public void outputDDLToWriter(Writer schemaWriter) {
        this.createSchemaWriter = schemaWriter;
    }

    public void outputCreateDDLToWriter(Writer createWriter) {
        this.createSchemaWriter = createWriter;
    }

    public void outputDropDDLToWriter(Writer dropWriter) {
        this.dropSchemaWriter = dropWriter;
    }

    /**
     * Use the definition object to drop and recreate the schema entity on the
     * database. This is used for dropping tables, views, procedures ... etc ...
     * This handles and ignore any database error while dropping in case the
     * object did not previously exist.
     */
    public void replaceObject(DatabaseObjectDefinition databaseDefinition) throws EclipseLinkException {
        // PERF: Allow a special "fast" flag to be set on the session causes a delete from the table instead of a replace.
        boolean fast = FAST_TABLE_CREATOR;
        if (fast && (databaseDefinition instanceof TableDefinition)) {
            session.executeNonSelectingSQL("DELETE FROM " + databaseDefinition.getName());
        } else if (fast && (databaseDefinition instanceof StoredProcedureDefinition)) {
            // do nothing
        } else {
            // CR 3870467, do not log stack
            boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();

            if (shouldLogExceptionStackTrace) {
                getSession().getSessionLog().setShouldLogExceptionStackTrace(false);
            }
            try {
                dropObject(databaseDefinition);
            } catch (DatabaseException exception) {
                // Ignore error
            } finally {
                if (shouldLogExceptionStackTrace) {
                    getSession().getSessionLog().setShouldLogExceptionStackTrace(true);
                }
            }

            String query = createObject(databaseDefinition);
            getDBMapping().putCreateQuery(databaseDefinition.getName(), query);
        }
    }

    /**
     * Construct the default TableCreator. If the default TableCreator is
     * already created, just returns it.
     */
    protected JPAMTableCreator getDefaultTableCreator(boolean generateFKConstraints) {
        if (defaultTableCreator == null) {
            defaultTableCreator = new JPAMDefaultTableGenerator(session.getProject(), generateFKConstraints).generateDefaultTableCreator();
            defaultTableCreator.setIgnoreDatabaseException(true);
        }
        return defaultTableCreator;
    }

    /**
     * Create the default table schema for the project this session associated
     * with.
     */
    public void createDefaultTables(boolean generateFKConstraints) {
        //Create each table w/o throwing exception and/or exit if some of them are already existed in the db.
        //If a table is already existed, skip the creation.

        //        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        //        getSession().getSessionLog().setShouldLogExceptionStackTrace(false);
        //
        //        try {
        JPAMTableCreator tableCreator = getDefaultTableCreator(generateFKConstraints);
        tableCreator.createTables(this.session, this);
        //        } catch (DatabaseException ex) {
        //            // Ignore error
        //        } finally {
        //            getSession().getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        //        }
        //        // Reset database change events to new tables.
        //        if (this.session.getDatabaseEventListener() != null) {
        //            this.session.getDatabaseEventListener().remove(this.session);
        //            this.session.getDatabaseEventListener().register(this.session);
        //        }
    }

    /**
     * INTERNAL: Iterate over the schemas that need to be dropped.
     */
    public void dropDatabaseSchemas() {
        for (String databaseSchema : dropDatabaseSchemas.keySet()) {
            if (shouldWriteToDatabase()) {
                dropDatabaseSchemas.get(databaseSchema).dropDatabaseSchemaOnDatabase(getSession());
            } else {
                dropDatabaseSchemas.get(databaseSchema).dropDatabaseSchema(getSession(), getDropSchemaWriter());
                appendToDDLWriter(getDropSchemaWriter(), "\n");
            }
        }
    }

    /**
     * Create the default table schema for the project this session associated
     * with.
     */
    public void dropDefaultTables() {
        // Drop each table w/o throwing exception and/or exit if some don't exist.
        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        getSession().getSessionLog().setShouldLogExceptionStackTrace(false);

        try {
            // Drop the tables.
            JPAMTableCreator tableCreator = getDefaultTableCreator(true);
            tableCreator.dropTables(this.session, this);

            // Drop the sequences.
            dropSequences();

            // Drop all the database schemas now if set to do so. This must be
            // called after all the constraints, tables etc. are dropped.
            dropDatabaseSchemas();
        } catch (DatabaseException ex) {
            // Ignore error
        } finally {
            getSession().getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
        // Reset database change events to new tables.
        if (this.session.getDatabaseEventListener() != null) {
            this.session.getDatabaseEventListener().remove(this.session);
            this.session.getDatabaseEventListener().register(this.session);
        }
    }

    /**
     * Drop and recreate the default table schema for the project this session
     * associated with.
     */
    public void replaceDefaultTables() throws EclipseLinkException {
        replaceDefaultTables(true, true, true);
    }

    /**
     * Drop and recreate the default table schema for the project this session
     * associated with.
     */
    public void replaceDefaultTables(boolean createSequenceTables, boolean generateFKConstraints) throws EclipseLinkException {
        replaceDefaultTables(createSequenceTables, false, generateFKConstraints);
    }

    /**
     * Drop and recreate the default table schema for the project this session
     * associated with.
     */
    public void replaceDefaultTables(boolean createSequenceTables, boolean createSequences, boolean generateFKConstraints) throws EclipseLinkException {
        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        this.session.getSessionLog().setShouldLogExceptionStackTrace(false);

        try {
            JPAMTableCreator tableCreator = getDefaultTableCreator(generateFKConstraints);
            tableCreator.replaceTables(this.session, this, createSequenceTables, createSequences);

            // Drop all the database schemas now if set to do so. This must be
            // called after all the constraints, tables etc. are dropped.
            dropDatabaseSchemas();
        } catch (DatabaseException exception) {
            // Ignore error
        } finally {
            this.session.getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
        // Reset database change events to new tables.
        if (this.session.getDatabaseEventListener() != null) {
            this.session.getDatabaseEventListener().remove(this.session);
            this.session.getDatabaseEventListener().register(this.session);
        }
    }

    public void setSession(DatabaseSessionImpl session) {
        this.session = session;
    }

    /**
     * INTERNAL: Returns true if a database schema should be created during ddl
     * generation for the given databaseObjectDefinition.
     */
    protected boolean shouldCreateDatabaseSchema(DatabaseObjectDefinition databaseObjectDefinition, Set<String> createdDatabaseSchemas) {
        return (createDatabaseSchemas && databaseObjectDefinition.shouldCreateDatabaseSchema(createdDatabaseSchemas));
    }

    /**
     * PUBLIC: Return true if this SchemaManager should write to the database
     * directly
     */
    public boolean shouldWriteToDatabase() {
        return ((this.createSchemaWriter == null) && (this.dropSchemaWriter == null));
    }

    /**
     * Use the definition to alter sequence.
     */
    public void alterSequence(SequenceDefinition sequenceDefinition) throws EclipseLinkException {
        if (!sequenceDefinition.isAlterSupported(getSession())) {
            return;
        }

        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                sequenceDefinition.alterOnDatabase(getSession());
            } else {
                sequenceDefinition.alter(getSession(), createSchemaWriter);
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Create or extend the default table schema for the project this session
     * associated with.
     */
    public void extendDefaultTables(boolean generateFKConstraints) throws EclipseLinkException {
//        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
//        this.session.getSessionLog().setShouldLogExceptionStackTrace(false);
//
//        try {
        JPAMTableCreator tableCreator = getDefaultTableCreator(generateFKConstraints);
        tableCreator.extendTables(this.session, this);
//        } catch (DatabaseException exception) {
//            // Ignore error
//        } finally {
//            this.session.getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
//        }
//        // Reset database change events to new tables.
//        if (this.session.getDatabaseEventListener() != null) {
//            this.session.getDatabaseEventListener().remove(this.session);
//            this.session.getDatabaseEventListener().register(this.session);
//        }
    }

    /**
     * @return the dbMapping
     */
    public DBMapping getDBMapping() {
        return dbMapping;
    }

}
