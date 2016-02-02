/** *****************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/04/2013-2.5 Guy Pelletier
 *       - 389090: JPA 2.1 DDL Generation Support
 ***************************************************************************** */
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: This class is responsible for creating the tables defined in
 * the project. A specific subclass of this class is created for each project.
 * The specific table information is defined in the subclass.
 *
 * @since TopLink 2.0
 * @author Peter Krogh
 */
public class JPAMTableCreator {

    /**
     * Flag to disable table existence check before create.
     */
    public static boolean CHECK_EXISTENCE = true;

    protected List<TableDefinition> tableDefinitions;
    protected String name;
    protected boolean ignoreDatabaseException; //if true, DDL generation will continue even if exceptions occur

    public JPAMTableCreator() {
        this(new ArrayList<TableDefinition>());
    }

    public JPAMTableCreator(List<TableDefinition> tableDefinitions) {
        super();
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Add the table.
     */
    public void addTableDefinition(TableDefinition tableDefinition) {
        this.tableDefinitions.add(tableDefinition);
    }

    /**
     * Add a set of tables.
     */
    public void addTableDefinitions(Collection<TableDefinition> tableDefs) {
        this.tableDefinitions.addAll(tableDefs);
    }

    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session) {
        //CR2612669
        createConstraints(session, new JPAMSchemaManager(session));
    }

    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session, JPAMSchemaManager schemaManager) {
        createConstraints(session, schemaManager, true);
    }

    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        createConstraints(getTableDefinitions(), session, schemaManager, build);
    }

    /**
     * Create constraints.
     */
    public void createConstraints(List<TableDefinition> tables, DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        // Unique constraints should be generated before foreign key constraints,
        // because foreign key constraints can reference unique constraints
        for (TableDefinition table : tables) {
            try {
                schemaManager.createUniqueConstraints(table);
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }

        for (TableDefinition table : tables) {
            try {
                schemaManager.createForeignConstraints(table);
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
    }

    /**
     * This creates the tables on the database. If the table already exists this
     * will fail.
     */
    public void createTables(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        createTables(session, new JPAMSchemaManager(session));
    }

    /**
     * This creates the tables on the database. If the table already exists this
     * will fail.
     */
    public void createTables(DatabaseSession session, JPAMSchemaManager schemaManager) {
        createTables(session, schemaManager, true);
    }

    /**
     * This creates the tables on the database. If the table already exists this
     * will fail.
     */
    public void createTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        createTables(session, schemaManager, build, true, true, true);
    }

    /**
     * This creates the tables on the database. If the table already exists this
     * will fail.
     */
    public void createTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build, boolean check, boolean createSequenceTables, boolean createSequences) {
        buildConstraints(schemaManager, build);

        String sequenceTableName = getSequenceTableName(session);
        List<TableDefinition> missingTables = new ArrayList<TableDefinition>();
        for (TableDefinition table : getTableDefinitions()) {
            // Must not create sequence table as done in createSequences.
            if (!table.getName().equals(sequenceTableName)) {
//                boolean alreadyExists = false;
                // Check if the table already exists, to avoid logging create error.
//                if (check && CHECK_EXISTENCE && schemaManager.shouldWriteToDatabase()) {
//                    alreadyExists = schemaManager.checkTableExists(table);
//                }
//                if (!alreadyExists) {
                missingTables.add(table);
                try {
                    schemaManager.createObject(table);
                    session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_created", table.getFullName());
                } catch (DatabaseException ex) {
                    session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_already_existed", table.getFullName());
                    if (!shouldIgnoreDatabaseException()) {
                        throw ex;
                    }
                }
//                }
            }
        }

        createConstraints(missingTables, session, schemaManager, false);

        schemaManager.createOrReplaceSequences(createSequenceTables, createSequences);
        for (TableDefinition table : missingTables) {
            schemaManager.createMapping(table);
        }
        for (TableDefinition table : missingTables) {
            schemaManager.createReference(table);
        }
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session) {
        //CR2612669
        dropConstraints(session, new JPAMSchemaManager(session));
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session, JPAMSchemaManager schemaManager) {
        dropConstraints(session, schemaManager, true);
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        for (TableDefinition table : getTableDefinitions()) {
            try {
                schemaManager.dropConstraints(table);
            } catch (DatabaseException exception) {
                //ignore
            }
        }
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(DatabaseSession session) {
        //CR2612669
        dropTables(session, new JPAMSchemaManager(session));
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(DatabaseSession session, JPAMSchemaManager schemaManager) {
        dropTables(session, schemaManager, true);
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        // CR 3870467, do not log stack, or log at all if not fine
        boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();
        int level = session.getSessionLog().getLevel();
        if (shouldLogExceptionStackTrace) {
            session.getSessionLog().setShouldLogExceptionStackTrace(false);
        }
        if (level > SessionLog.FINE) {
            session.getSessionLog().setLevel(SessionLog.SEVERE);
        }
        try {
            dropConstraints(session, schemaManager, false);

            String sequenceTableName = getSequenceTableName(session);
            List<TableDefinition> tables = getTableDefinitions();
            int trys = 1;
            if (JPAMSchemaManager.FORCE_DROP) {
                trys = 5;
            }
            while ((trys > 0) && !tables.isEmpty()) {
                trys--;
                List<TableDefinition> failed = new ArrayList<TableDefinition>();
                for (TableDefinition table : tables) {
                    // Must not create sequence table as done in createSequences.
                    if (!table.getName().equals(sequenceTableName)) {
                        try {
                            schemaManager.dropObject(table);
                        } catch (DatabaseException exception) {
                            failed.add(table);
                            if (!shouldIgnoreDatabaseException()) {
                                throw exception;
                            }
                        }
                    }
                }
                tables = failed;
            }
        } finally {
            if (shouldLogExceptionStackTrace) {
                session.getSessionLog().setShouldLogExceptionStackTrace(true);
            }
            if (level > SessionLog.FINE) {
                session.getSessionLog().setLevel(level);
            }
        }
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the tables.
     */
    public List<TableDefinition> getTableDefinitions() {
        return tableDefinitions;
    }

    /**
     * Recreate the tables on the database. This will drop the tables if they
     * exist and recreate them.
     */
    public void replaceTables(DatabaseSession session) {
        replaceTables(session, new JPAMSchemaManager(session));
    }

    /**
     * Recreate the tables on the database. This will drop the tables if they
     * exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, JPAMSchemaManager schemaManager) {
        replaceTables(session, schemaManager, true, true);
    }

    /**
     * Recreate the tables on the database. This will drop the tables if they
     * exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean createSequenceTables) {
        replaceTables(session, schemaManager, createSequenceTables, false);
    }

    /**
     * Recreate the tables on the database. This will drop the tables if they
     * exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean createSequenceTables, boolean createSequences) {
        replaceTablesAndConstraints(schemaManager, session, createSequenceTables, createSequences);
    }

    protected void replaceTablesAndConstraints(JPAMSchemaManager schemaManager, DatabaseSession session, boolean createSequenceTables, boolean createSequences) {
        buildConstraints(schemaManager, true);
        boolean ignore = shouldIgnoreDatabaseException();
        setIgnoreDatabaseException(true);
        try {
            dropTables(session, schemaManager, false);
        } finally {
            setIgnoreDatabaseException(ignore);
        }
        createTables(session, schemaManager, false, false, createSequenceTables, createSequences);
    }

    protected void replaceTablesAndConstraints(JPAMSchemaManager schemaManager, DatabaseSession session) {
        replaceTables(session, schemaManager, false, false);
    }

    /**
     * Convert any field constraint to constraint objects.
     */
    protected void buildConstraints(JPAMSchemaManager schemaManager, boolean build) {
        if (build) {
            for (TableDefinition table : getTableDefinitions()) {
                schemaManager.buildFieldTypes(table);
            }
        }
    }

    /**
     * Set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the tables.
     */
    public void setTableDefinitions(Vector tableDefinitions) {
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Return true if DatabaseException is to be ignored.
     */
    public boolean shouldIgnoreDatabaseException() {
        return ignoreDatabaseException;
    }

    /**
     * Set flag whether DatabaseException should be ignored.
     */
    public void setIgnoreDatabaseException(boolean ignoreDatabaseException) {
        this.ignoreDatabaseException = ignoreDatabaseException;
    }

    /**
     * This returns the Sequence Table's qualified name, without delimiting.
     *
     * @param session
     * @return the qualified table name
     */
    protected String getSequenceTableName(Session session) {
        String sequenceTableName = null;
        if (session.getProject().usesSequencing()) {
            Sequence sequence = session.getLogin().getDefaultSequence();
            if (sequence instanceof TableSequence) {
                sequenceTableName = ((TableSequence) sequence).getQualifiedTableName();
            }
        }
        return sequenceTableName;
    }

    /**
     * Create or extend the tables on the database. This will alter existing
     * tables to add missing fields or create the table otherwise. It will also
     * create Sequences tables and objects.
     */
    public void extendTables(DatabaseSession session, JPAMSchemaManager schemaManager) {
        extendTablesAndConstraints(schemaManager, session);
        schemaManager.createOrReplaceSequences(true, true);
    }

    protected void extendTablesAndConstraints(JPAMSchemaManager schemaManager, DatabaseSession session) {
        buildConstraints(schemaManager, true);
        boolean ignore = shouldIgnoreDatabaseException();
        setIgnoreDatabaseException(true);
        try {
            extendTables(session, schemaManager, false);
        } finally {
            setIgnoreDatabaseException(ignore);
        }
    }

    /**
     * This creates/extends the tables on the database.
     */
    public void extendTables(DatabaseSession session, JPAMSchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        String sequenceTableName = getSequenceTableName(session);
        for (TableDefinition table : getTableDefinitions()) {
            // Must not create sequence table as done in createSequences.
            if (!table.getName().equals(sequenceTableName)) {
                AbstractSession abstractSession = (AbstractSession) session;
                boolean alreadyExists = false;
                // Check if the table already exists, to avoid logging create error.
//                if (CHECK_EXISTENCE && schemaManager.shouldWriteToDatabase()) {
//                    alreadyExists = schemaManager.checkTableExists(table);
//                }
                DatabaseException createTableException = null;
                if (!alreadyExists) {
                    //assume table does not exist
                    try {
                        schemaManager.createObject(table);
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_created", table.getFullName());
                    } catch (DatabaseException exception) {
                        createTableException = exception;
                        alreadyExists = true;
                    }
                }
                alreadyExists = true;
                if (alreadyExists) {
                    //Assume the table exists, so lookup the column info

                    //While SQL is case insensitive, getColumnInfo is and will not return the table info unless the name is passed in
                    //as it is stored internally.  
                    String tableName = table.getTable() == null ? table.getName() : table.getTable().getName();
                    boolean usesDelimiting = (table.getTable() != null && table.getTable().shouldUseDelimiters());
                    List<DatabaseRecord> columnInfo = null;

                    //I need the actual table catalog, schema and tableName for getTableInfo.
                    columnInfo = abstractSession.getAccessor().getColumnInfo(null, null, tableName, null, abstractSession);

                    if (!usesDelimiting && (columnInfo == null || columnInfo.isEmpty())) {
                        tableName = tableName.toUpperCase();
                        columnInfo = abstractSession.getAccessor().getColumnInfo(null, null, tableName, null, abstractSession);
                        if ((columnInfo == null || columnInfo.isEmpty())) {
                            tableName = tableName.toLowerCase();
                            columnInfo = abstractSession.getAccessor().getColumnInfo(null, null, tableName, null, abstractSession);
                        }
                    }
                    if (columnInfo != null && !columnInfo.isEmpty()) {
                        //Table exists, add individual fields as necessary

                        //hash the table's existing columns by name
                        Map<DatabaseField, DatabaseRecord> columns = new HashMap(columnInfo.size());
                        DatabaseField columnNameLookupField = new DatabaseField("COLUMN_NAME");
                        DatabaseField schemaLookupField = new DatabaseField("TABLE_SCHEM");
                        boolean schemaMatchFound = false;
                        // Determine the probably schema for the table, this is a heuristic, so should not cause issues if wrong.
                        String qualifier = table.getQualifier();
                        if ((qualifier == null) || (qualifier.length() == 0)) {
                            qualifier = session.getDatasourcePlatform().getTableQualifier();
                            if ((qualifier == null) || (qualifier.length() == 0)) {
                                qualifier = session.getLogin().getUserName();
                            }
                        }
                        boolean checkSchema = (qualifier != null) && (qualifier.length() > 0);
                        for (DatabaseRecord record : columnInfo) {
                            String fieldName = (String) record.get(columnNameLookupField);
                            if (fieldName != null && fieldName.length() > 0) {
                                DatabaseField column = new DatabaseField(fieldName);
                                if (session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                                    column.useUpperCaseForComparisons(true);
                                }
                                String schema = (String) record.get(schemaLookupField);
                                // Check the schema as well.  Ignore columns for other schema if a schema match is found.
                                if (schemaMatchFound) {
                                    if (qualifier.equalsIgnoreCase(schema)) {
                                        columns.put(column, record);
                                    }
                                } else {
                                    if (checkSchema) {
                                        if (qualifier.equalsIgnoreCase(schema)) {
                                            schemaMatchFound = true;
                                            // Remove unmatched columns from other schemas.
                                            columns.clear();
                                        }
                                    }
                                    // If none of the schemas match what is expected, assume what is expected is wrong, and use all columns.
                                    columns.put(column, record);
                                }
                            }
                        }

                        //Go through each field we need to have in the table to see if it already exists
                        for (FieldDefinition fieldDef : table.getFields()) {
                            DatabaseField dbField = fieldDef.getDatabaseField();
                            if (dbField == null) {
                                dbField = new DatabaseField(fieldDef.getName());
                            }
                            if (columns.get(dbField) == null) {
                                //field does not exist so add it to the table
                                try {
                                    table.addFieldOnDatabase(abstractSession, fieldDef);
                                } catch (DatabaseException addFieldEx) {
                                    session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "table_cannot_add_field", dbField.getName(), table.getFullName(), addFieldEx.getMessage());
                                    if (!shouldIgnoreDatabaseException()) {
                                        throw addFieldEx;
                                    }
                                }
                            }
                        }
                    } else if (createTableException != null) {
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "cannot_create_table", table.getFullName(), createTableException.getMessage());
                        if (!shouldIgnoreDatabaseException()) {
                            throw createTableException;
                        }
                    }
                }
            }
        }
        createConstraints(session, schemaManager, false);

        schemaManager.createSequences();
    }
}
