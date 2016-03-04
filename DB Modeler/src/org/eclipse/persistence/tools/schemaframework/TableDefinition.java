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
 *     Dies Koper - avoid generating constraints on platforms that do not support constraint generation
 *     Dies Koper - add support for creating indices on tables
 *     09/09/2011-2.3.1 Guy Pelletier
 *       - 356197: Add new VPD type to MultitenantType
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 *     12/07/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLCall;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating tables on the different platforms.
 * </p>
 */
public class TableDefinition extends DatabaseObjectDefinition {
    protected List<FieldDefinition> fields; //FieldDefinitions
    protected Map<String, ForeignKeyConstraint> foreignKeyMap; //key is the name of ForeignKeyConstraint
    protected List<UniqueKeyConstraint> uniqueKeys;
    protected List<IndexDefinition> indexes;
    protected String creationPrefix;
    protected String creationSuffix;
    private boolean createSQLFiles;
    private boolean createVPDCalls;
    private String tenantFieldName;
    //holds onto the name and delimiting info.
    protected DatabaseTable table;
    protected boolean hasUserDefinedForeignKeyConstraints;

    public TableDefinition() {
        createVPDCalls = false;
        hasUserDefinedForeignKeyConstraints = false;
        this.fields = new ArrayList<FieldDefinition>();
        this.indexes = new ArrayList<IndexDefinition>();
        this.foreignKeyMap = new HashMap<String, ForeignKeyConstraint>();
        this.uniqueKeys = new ArrayList<UniqueKeyConstraint>();
        this.creationPrefix = "CREATE TABLE ";
        this.creationSuffix = "";
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type) {
        this.addField(new FieldDefinition(fieldName, type));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize, int fieldSubSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize, fieldSubSize));
    }

    /**
     * PUBLIC:
     * Add the field to the type to a nested type.
     * @param typeName is the name of the nested type.
     */
    public void addField(String fieldName, String typeName) {
        addField(new FieldDefinition(fieldName, typeName));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     */
    public void addField(FieldDefinition field) {
        getFields().add(field);
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table to add the field to the table.  
     */
    public void addFieldOnDatabase(final AbstractSession session, FieldDefinition field){
        session.priviledgedExecuteNonSelectingCall(
                new SQLCall( buildAddFieldWriter(session, field, new StringWriter()).toString() ) );
    }

    /**
     * INTERNAL:
     * Return the alter table statement to add a field to the table.
     */
    public Writer buildAddFieldWriter(AbstractSession session, FieldDefinition field, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName() + " ");
            session.getPlatform().writeAddColumnClause(writer, session, this, field);
            writer.write(" ");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * PUBLIC:
     * Add a foreign key constraint to the table.
     * If there is a same name foreign key constraint already, nothing will happen.
     */
    public void addForeignKeyConstraint(String name, String sourceField, String targetField, String targetTable) {
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint(name, sourceField, targetField, targetTable);
        addForeignKeyConstraint(foreignKey);
    }

    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(String name, String sourceField) {
        UniqueKeyConstraint uniqueKey = new UniqueKeyConstraint(name, sourceField);
        addUniqueKeyConstraint(uniqueKey);
    }
    
    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(String name, String[] sourceFields) {
        UniqueKeyConstraint uniqueKey = new UniqueKeyConstraint(name, sourceFields);
        addUniqueKeyConstraint(uniqueKey);
    }

    /**
     * PUBLIC:
     * Add a foreign key constraint to the table.
     * If there is a same name foreign key constraint already, nothing will happen.
     */
    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKey) {
        if (! hasUserDefinedForeignKeyConstraints) {
            if (!foreignKeyMap.containsKey(foreignKey.getName())) {
                foreignKeyMap.put(foreignKey.getName(), foreignKey);
            }
        }
    }
    
    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(UniqueKeyConstraint uniqueKey) {
        getUniqueKeys().add(uniqueKey);
    }
    
    /**
     * PUBLIC:
     * Add an index to the table.
     */
    public void addIndex(IndexDefinition index) {
        getIndexes().add(index);
    }
    
    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * Identity fields are used on Sybase for native sequencing,
     * The field must be of number type and cannot have a subsize.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addIdentityField(String fieldName, Class type) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type);
        fieldDef.setIsIdentity(true);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * Identity fields are used on Sybase for native sequencing,
     * The field must be of number type and cannot have a subsize.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addIdentityField(String fieldName, Class type, int fieldSize) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type, fieldSize);
        fieldDef.setIsIdentity(true);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * This field is set as part of the primary key.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addPrimaryKeyField(String fieldName, Class type) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * This field is set as part of the primary key.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addPrimaryKeyField(String fieldName, Class type, int fieldSize) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type, fieldSize);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * INTERNAL:
     * Return the alter table statement to add the constraints.
     * This is done separately from the create because of dependencies.
     */
    public Writer buildConstraintCreationWriter(AbstractSession session, ForeignKeyConstraint foreignKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(" ADD CONSTRAINT ");
            if (!session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(foreignKey.getName() + " ");
            }
            foreignKey.appendDBString(writer, session);
            if (session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(" CONSTRAINT " + foreignKey.getName());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to drop the constraints.
     * This is done separately to allow constraints to be dropped before the tables.
     */
    public Writer buildConstraintDeletionWriter(AbstractSession session, ForeignKeyConstraint foreignKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(session.getPlatform().getConstraintDeletionString() + foreignKey.getName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to add the constraints.
     * This is done separately from the create because of dependencies.
     */
    public Writer buildUniqueConstraintCreationWriter(AbstractSession session, UniqueKeyConstraint uniqueKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(" ADD CONSTRAINT ");
            if (!session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(uniqueKey.getName() + " ");
            }
            uniqueKey.appendDBString(writer, session);
            if (session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(" CONSTRAINT " + uniqueKey.getName());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to drop the constraints.
     * This is done separately to allow constraints to be dropped before the tables.
     */
    public Writer buildUniqueConstraintDeletionWriter(AbstractSession session, UniqueKeyConstraint uniqueKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(session.getPlatform().getUniqueConstraintDeletionString() + uniqueKey.getName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }    

    /**
     * INTERNAL:
     * Return the index creation statement.
     */
    public IndexDefinition buildIndex(AbstractSession session, String key, List<String> columnNames, boolean isUniqueSetOnField) {
        String indexName = buildIndexName(getName(), key, session.getPlatform().getIndexNamePrefix(isUniqueSetOnField), session.getPlatform().getMaxIndexNameSize(), session.getPlatform());
        IndexDefinition index = new IndexDefinition();
        index.setName(indexName);
        index.setTargetTable(getFullName());
        index.getFields().addAll(columnNames);
        return index;
    }

    /**
     * INTERNAL:
     * Return the index drop statement.
     */
    public Writer buildIndexDeletionWriter(AbstractSession session, String key, Writer writer, boolean isUniqueSetOnField) {
            String indexName = buildIndexName(getName(), key, session.getPlatform().getIndexNamePrefix(isUniqueSetOnField),
                    session.getPlatform().getMaxIndexNameSize(), session.getPlatform());
        IndexDefinition index = new IndexDefinition();
        index.setName(indexName);
        index.setTargetTable(getFullName());
        index.buildDeletionWriter(session, writer);
        return writer;
    }

    
    /**
     * INTERNAL:
     * Return the beginning of the sql create statement - the part before the name.
     * Unless temp table is created should be "CREATE TABLE "
     */
    public String getCreationPrefix() {
        return creationPrefix;    
    }
    
    /**
     * INTERNAL:
     * Set the beginning of the sql create statement - the part before the name.
     * Use to create temp. table.
     */
    public void setCreationPrefix(String  creationPrefix) {
        this.creationPrefix = creationPrefix;
    }

    /**
     * INTERNAL:
     * Return the end of the sql create statement - the part after the field list.
     * Unless temp table is created should be empty.
     */
    public String getCreationSuffix() {
        return creationSuffix;
    }

    /**
     * PUBLIC:
     * Return the schema associated with this table.
     */
    @Override
    public String getDatabaseSchema() {
        return getTable().getTableQualifier();
    }
    
    /**
     * INTERNAL:
     * Set the end of the sql create statement - the part after the field list.
     */
    public void setCreationSuffix(String  creationSuffix) {
        this.creationSuffix = creationSuffix;
    }
    
    /**
     * INTERNAL:
     * Return the create table statement.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(getCreationPrefix() + getFullName() + " (");
            for (Iterator<FieldDefinition> itetrator = getFields().iterator(); itetrator.hasNext();) {
                FieldDefinition field = itetrator.next();
                field.appendDBString(writer, session, this);
                if (itetrator.hasNext()) {
                    writer.write(", ");
                }
            }
            List<String> keyFields = getPrimaryKeyFieldNames();
            if ((!keyFields.isEmpty()) && session.getPlatform().supportsPrimaryKeyConstraint()) {
                writer.write(", ");
                if (session.getPlatform().requiresNamedPrimaryKeyConstraints()) {
                    writer.write("CONSTRAINT " + getFullName() + "_PK ");
                }
                writer.write("PRIMARY KEY (");
                for (Iterator<String> iterator = keyFields.iterator(); iterator.hasNext();) {
                    writer.write(iterator.next());
                    if (iterator.hasNext()) {
                        writer.write(", ");
                    }
                }
                writer.write(")");
            }
            if (session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
                for (UniqueKeyConstraint constraint : getUniqueKeys()) {
                    writer.write(", ");
                    constraint.appendDBString(writer, session);
                }
            }
            writer.write(")");
            //let the platform write out the CreationSuffix and the platform's default tableCreationSuffix
            session.getPlatform().writeTableCreationSuffix(writer, getCreationSuffix());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop table statement.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP TABLE " + getFullName() + session.getPlatform().getDropCascadeString());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Writer buildVPDCreationPolicyWriter(AbstractSession session, Writer writer) {
        try {
            writer.write(session.getPlatform().getVPDCreationPolicyString(getName(), session));
            return writer;
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Writer buildVPDCreationFunctionWriter(AbstractSession session, Writer writer) {
        try {
            writer.write(session.getPlatform().getVPDCreationFunctionString(getName(), tenantFieldName));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        
        return writer;
    }
    
    /**
     * INTERNAL:
     * Build the create schema DDL.
     */
    protected Writer buildDatabaseSchemaCreationWriter(AbstractSession session, Writer writer, Set<String> createdDatabaseSchemas) {
        try {
            writer.write(session.getPlatform().getCreateDatabaseSchemaString(getDatabaseSchema()));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }

        // Tag that we created a schema (to avoid creating it again)
        createdDatabaseSchemas.add(getDatabaseSchema());
        return writer;
    }
    
    /**
     * INTERNAL:
     * Build the drop schema DDL.
     */
    protected Writer buildDatabaseSchemaDeletionWriter(AbstractSession session, Writer writer) {
        try {
            writer.write(session.getPlatform().getDropDatabaseSchemaString(getDatabaseSchema()));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }

        return writer;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Writer buildVPDDeletionWriter(AbstractSession session, Writer writer) {
        try {
            writer.write(session.getPlatform().getVPDDeletionString(getName(), session));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        
        return writer;
    }

    /**
     * INTERNAL:
     * Build the foreign key constraints.
     */
    protected void buildFieldTypes(AbstractSession session) {
        // The ForeignKeyConstraint object is the newer way of doing things.
        // We support FieldDefinition.getForeignKeyFieldName() due to backwards compatibility
        // by converting it. To allow mixing both ways, we just add converted one to foreignKeys list.
        for (FieldDefinition field : getFields()) {
            if (field.getForeignKeyFieldName() != null) {
                addForeignKeyConstraint(buildForeignKeyConstraint(field, session.getPlatform()));
            }
        }
    }

    /**
     * Build a foreign key constraint using FieldDefinition.getForeignKeyFieldName().
     */
    protected ForeignKeyConstraint buildForeignKeyConstraint(FieldDefinition field, DatabasePlatform platform) {
        Vector sourceFields = new Vector();
        Vector targetFields = new Vector();
        ForeignKeyConstraint fkConstraint = new ForeignKeyConstraint();
        DatabaseField tempTargetField = new DatabaseField(field.getForeignKeyFieldName());
        DatabaseField tempSourceField = new DatabaseField(field.getName());

        sourceFields.add(tempSourceField.getName());
        targetFields.add(tempTargetField.getName());

        fkConstraint.setSourceFields(sourceFields);
        fkConstraint.setTargetFields(targetFields);
        fkConstraint.setTargetTable(tempTargetField.getTable().getQualifiedNameDelimited(platform));
        String tempName = buildForeignKeyConstraintName(this.getName(), tempSourceField.getName(), platform.getMaxForeignKeyNameSize(), platform);

        fkConstraint.setName(tempName);
        return fkConstraint;
    }

    /**
     * Build a foreign key constraint.
     */
    protected ForeignKeyConstraint buildForeignKeyConstraint(List<String> fkFieldNames, List<String> pkFieldNames, TableDefinition targetTable, DatabasePlatform platform) {
        assert fkFieldNames.size() > 0 && fkFieldNames.size() == pkFieldNames.size();
        
        ForeignKeyConstraint fkConstraint = new ForeignKeyConstraint();
        for(int i=0; i<fkFieldNames.size(); i++) {
            fkConstraint.getSourceFields().add(fkFieldNames.get(i));
            fkConstraint.getTargetFields().add(pkFieldNames.get(i));
        }

        fkConstraint.setTargetTable(targetTable.getFullName());
        String fkFieldName = fkFieldNames.get(0);
        String name = buildForeignKeyConstraintName(this.getName(), fkFieldName, platform.getMaxForeignKeyNameSize(), platform);

        fkConstraint.setName(name);
        return fkConstraint;
    }

    /**
     * Return foreign key constraint name built from the table and field name with the specified maximum length. To
     * make the name short enough we
     * 1. Drop the "FK_" prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table and field name.
     * 4. Truncate the table name to zero length if necessary.
     */
    protected String buildForeignKeyConstraintName(String tableName, String fieldName, int maximumNameLength, DatabasePlatform platform) {
        String startDelimiter = "";
        String endDelimiter = "";
        boolean useDelimiters = !platform.getStartDelimiter().equals("") && (tableName.startsWith(platform.getStartDelimiter()) || fieldName.startsWith(platform.getStartDelimiter()));
        // we will only delimit our generated constraints if either of the names that composes them is already delimited
        if (useDelimiters){
            startDelimiter = platform.getStartDelimiter();
            endDelimiter = platform.getEndDelimiter();
        }
        String adjustedTableName = tableName;
        if(adjustedTableName.indexOf(' ') != -1 || adjustedTableName.indexOf('\"') != -1 || adjustedTableName.indexOf('`') != -1) {
            //if table name has spaces and/or is quoted, remove this from the constraint name.
            StringBuilder buff = new StringBuilder();
            for(int i = 0; i < tableName.length(); i++) {
                char c = tableName.charAt(i);
                if(c != ' ' && c != '\"' && c != '`') {
                    buff.append(c);
                }
            }
            adjustedTableName = buff.toString();
        }
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if(c != ' ' && c != '\"' && c != '`') {
                buff.append(c);
            }
        }
        String adjustedFieldName = buff.toString();
        String foreignKeyName = startDelimiter + "FK_" + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
        if (foreignKeyName.length() > maximumNameLength) {
            // First Remove the "FK_" prefix.
            foreignKeyName = startDelimiter + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
            if (foreignKeyName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                foreignKeyName = startDelimiter + Helper.removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength) + endDelimiter;
                if (foreignKeyName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = Helper.removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    foreignKeyName = startDelimiter + Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength) + endDelimiter;
                    if (foreignKeyName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = Helper.removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        int delimiterLength = startDelimiter.length() + endDelimiter.length();
                        if (shortenedFieldName.length() + delimiterLength >= maximumNameLength) {
                            foreignKeyName = startDelimiter + Helper.truncate(shortenedFieldName, maximumNameLength - delimiterLength) + endDelimiter;
                        } else {
                            foreignKeyName = startDelimiter + Helper.truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length() - delimiterLength) + shortenedFieldName + endDelimiter;
                        }
                    }
                }
            }
        }
        return foreignKeyName;
    }

    protected UniqueKeyConstraint buildUniqueKeyConstraint(String name, List<String> fieldNames, int serialNumber, DatabasePlatform platform) {
        assert fieldNames.size() > 0;
        
        UniqueKeyConstraint unqConstraint = new UniqueKeyConstraint();
        
        for (String fieldName : fieldNames) {
            unqConstraint.addSourceField(fieldName);
        }
        
        // If the name was not provided, default one, otherwise use the name provided.
        if (name == null || name.equals("")) {
            unqConstraint.setName(buildUniqueKeyConstraintName(getName(), serialNumber, platform.getMaxUniqueKeyNameSize()));
        } else {
            // Hack if off if it exceeds the max size.
            if (name.length() > platform.getMaxUniqueKeyNameSize()) {
                unqConstraint.setName(name.substring(0, platform.getMaxUniqueKeyNameSize() - 1));
            } else {
                unqConstraint.setName(name);
            }
        }
        
        return unqConstraint;
    }

    /**
     * Return unique key constraint name built from the table name and sequence 
     * number with the specified maximum length. To make the name short enough we
     * 1. Drop the "UNQ_" prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table name.
     * 4. Truncate the table name to zero length if necessary.
     */
    protected String buildUniqueKeyConstraintName(String tableName, int serialNumber, int maximumNameLength) {
        String uniqueKeyName = "UNQ_" + tableName + "_" + serialNumber;
        if (uniqueKeyName.length() > maximumNameLength) {
            // First Remove the "UNQ_" prefix.
            uniqueKeyName = tableName + serialNumber;
            if (uniqueKeyName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                uniqueKeyName = Helper.removeAllButAlphaNumericToFit(tableName + serialNumber, maximumNameLength);
                if (uniqueKeyName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(tableName, 0);
                    String serialName = String.valueOf(serialNumber);
                    uniqueKeyName = Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, serialName, maximumNameLength);
                    if (uniqueKeyName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and truncate the table name.
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        uniqueKeyName = Helper.truncate(shortenedTableName, maximumNameLength - serialName.length()) + serialName;
                    }
                }
            }
        }
        return uniqueKeyName;
    }

    /**
     * Return key constraint name built from the table and key name with the
     * specified maximum length and index prefix. If indexPrefix is null, 
     * "IX_" is used for prefix. To make the name short enough we:
     * 
     * <pre>
     * 1. Drop the prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table and key name.
     * 4. Truncate the table name to zero length if necessary.
     * </pre>
     */
    protected String buildIndexName(String tableName, String key, String indexPrefix, int maximumNameLength, DatabasePlatform platform) {
        String startDelimiter = "";
        String endDelimiter = "";
        boolean useDelimiters = !platform.getStartDelimiter().equals("") && (tableName.startsWith(platform.getStartDelimiter()) || key.startsWith(platform.getStartDelimiter()));
        // we will only delimit our generated indices if either of the names that composes them is already delimited
        if (useDelimiters){
            startDelimiter = platform.getStartDelimiter();
            endDelimiter = platform.getEndDelimiter();
        }
        String adjustedTableName = tableName;
        if(adjustedTableName.indexOf(' ') != -1 || adjustedTableName.indexOf('\"') != -1 || adjustedTableName.indexOf('`') != -1) {
            //if table name has spaces and/or is quoted, remove this from the constraint name.
            StringBuilder buff = new StringBuilder();
            for(int i = 0; i < tableName.length(); i++) {
                char c = tableName.charAt(i);
                if(c != ' ' && c != '\"' && c != '`') {
                    buff.append(c);
                }
            }
            adjustedTableName = buff.toString();
        }
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if(c != ' ' && c != '\"' && c != '`') {
                buff.append(c);
            }
        }
        String adjustedFieldName = buff.toString();
        if (indexPrefix == null) {
            indexPrefix = "IX_";
        }
        String indexName = startDelimiter + indexPrefix + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
        if (indexName.length() > maximumNameLength) {
            // First Remove the prefix.
            indexName = startDelimiter + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
            if (indexName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                indexName = startDelimiter + Helper.removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength) + endDelimiter;
                if (indexName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = Helper.removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    indexName = startDelimiter + Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength) + endDelimiter;
                    if (indexName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = Helper.removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        int delimiterLength = startDelimiter.length() + endDelimiter.length();
                        if (shortenedFieldName.length() + delimiterLength >= maximumNameLength) {
                            indexName = startDelimiter + Helper.truncate(shortenedFieldName, maximumNameLength - delimiterLength) + endDelimiter;
                        } else {
                            indexName = startDelimiter + Helper.truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length() - delimiterLength) + shortenedFieldName + endDelimiter;
                        }
                    }
                }
            }
        }
        return indexName;
    }

    /**
     * PUBLIC:
     * Performs a deep copy of this table definition.
     */
    public Object clone() {
        TableDefinition clone = (TableDefinition)super.clone();
        if (fields != null) {
            clone.setFields(new ArrayList<FieldDefinition>(fields.size()));
            for (FieldDefinition fieldDef : getFields()) {
                clone.addField((FieldDefinition)fieldDef.clone());
            }
        }
        if (foreignKeyMap != null) {
            clone.setForeignKeyMap(new HashMap(this.foreignKeyMap));
        }
        if (uniqueKeys != null) {
            clone.setUniqueKeys(new ArrayList(this.uniqueKeys));
        }        
        return clone;
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void createConstraints(AbstractSession session, Writer schemaWriter) throws EclipseLinkException {       
        createUniqueConstraints(session, schemaWriter);
        createForeignConstraints(session, schemaWriter);
    }

    void createUniqueConstraints(final AbstractSession session, final Writer schemaWriter) throws ValidationException {
        if (schemaWriter == null) {
            createUniqueConstraintsOnDatabase(session);
            return;
        }

        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            return;
        }
        for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
            buildUniqueConstraintCreationWriter(session, uniqueKey, schemaWriter).toString();
            writeLineSeperator(session, schemaWriter);
        }            
    }

    void createForeignConstraints(final AbstractSession session, final Writer schemaWriter) throws ValidationException {
        if (schemaWriter == null) {
            createForeignConstraintsOnDatabase(session);
            return;
        }

        if (session.getPlatform().supportsForeignKeyConstraints()) {
            for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
                if (! foreignKey.disableForeignKey()) {
                    buildConstraintCreationWriter(session, foreignKey, schemaWriter).toString();
                    writeLineSeperator(session, schemaWriter);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void createConstraintsOnDatabase(AbstractSession session) throws EclipseLinkException {
        createUniqueConstraintsOnDatabase(session);       
        createForeignConstraintsOnDatabase(session);
    }

    /**
     * INTERNAL:
     * Execute the DDL to create the database schema for this object.
     */
    @Override
    public void createDatabaseSchema(AbstractSession session, Writer writer, Set<String> createdDatabaseSchemas) throws EclipseLinkException {
        buildDatabaseSchemaCreationWriter(session, writer, createdDatabaseSchemas);
    }
    
    /**
     * INTERNAL:
     * Execute the DDL to create the database schema for this object.
     */
    @Override
    public void createDatabaseSchemaOnDatabase(AbstractSession session, Set<String> createdDatabaseSchemas) throws EclipseLinkException {        
        session.priviledgedExecuteNonSelectingCall(new SQLCall(buildDatabaseSchemaCreationWriter(session, new StringWriter(), createdDatabaseSchemas).toString()));
    }
    
    void createUniqueConstraintsOnDatabase(final AbstractSession session) throws ValidationException, DatabaseException {       
        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            return;
        }

        for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
            session.priviledgedExecuteNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(buildUniqueConstraintCreationWriter(session, uniqueKey, new StringWriter()).toString()));
        } 
    }

    void createForeignConstraintsOnDatabase(final AbstractSession session) throws ValidationException, DatabaseException {        
        if ((!session.getPlatform().supportsForeignKeyConstraints()) || getForeignKeyMap().isEmpty()) {
            return;
        }

        for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
            if (! foreignKey.disableForeignKey()) {
                session.priviledgedExecuteNonSelectingCall(new SQLCall(buildConstraintCreationWriter(session, foreignKey, new StringWriter()).toString()));
            }
        }
    }

    /**
     * INTERNAL:<br>
     * Write the SQL create index string to create index if
     * passed a writer, else delegate to a method that executes the string on
     * the database.
     * 
     * @throws ValidationException wraps any IOException from the writer
     */
    public void createIndexes(AbstractSession session, Writer writer) {
        if (!session.getPlatform().supportsIndexes()) {
            return;
        }
        // Primary key
        if (session.getPlatform().shouldCreateIndicesForPrimaryKeys()) {
            List<String> primKeyList = getPrimaryKeyFieldNames();
            if (!primKeyList.isEmpty()) {
                IndexDefinition index = buildIndex(session, primKeyList.get(0), primKeyList, false);
                if (writer == null) {
                    index.createOnDatabase(session);
                } else {
                    index.buildCreationWriter(session, writer);
                    writeLineSeperator(session, writer);
                }
            }
        }
        // Unique keys
        if (session.getPlatform().shouldCreateIndicesOnUniqueKeys()) {
            // indices for columns in unique key constraint declarations
            for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
                IndexDefinition index = buildIndex(session, uniqueKey.getName(), uniqueKey.getSourceFields(), false);
                if (writer == null) {
                    index.createOnDatabase(session);
                } else {
                    index.buildCreationWriter(session, writer);
                    writeLineSeperator(session, writer);
                }
            }

            // indices for columns with unique=true declarations
            for (FieldDefinition field : getFields()) {
                if (field.isUnique()) {
                    List<String> columnAsList = new ArrayList<String>();
                    columnAsList.add(field.getName());
                    IndexDefinition index = buildIndex(session, field.getName(), columnAsList, true);
                    if (writer == null) {
                        index.createOnDatabase(session);
                    } else {
                        index.buildCreationWriter(session, writer);
                        writeLineSeperator(session, writer);
                    }
                }
            }
        }
        // Foreign keys
        if (session.getPlatform().shouldCreateIndicesOnForeignKeys()) {
            // indices for columns in foreign key constraint declarations
            for (ForeignKeyConstraint foreignKey : getForeignKeys()) {
                if (!foreignKey.isDisableForeignKey()) {
                    // Do not re-index pk.
                    boolean alreadyIndexed = false;
                    List<String> primaryKeys = getPrimaryKeyFieldNames();
                    if ((primaryKeys.size() == foreignKey.getSourceFields().size())
                            && primaryKeys.containsAll(foreignKey.getSourceFields())) {
                        alreadyIndexed = true;
                    }
                    // Also check unique fields.
                    if (foreignKey.getSourceFields().size() == 1) {
                        FieldDefinition field = getField(foreignKey.getSourceFields().get(0));
                        if ((field != null) && field.isUnique()) {
                            alreadyIndexed = true;
                        }
                    }
                    for (UniqueKeyConstraint uniqueConstraint : getUniqueKeys()) {
                        if ((uniqueConstraint.getSourceFields().size() == foreignKey.getSourceFields().size())
                                && uniqueConstraint.getSourceFields().containsAll(foreignKey.getSourceFields())) {
                            alreadyIndexed = true;
                        }
                    }
                    if (!alreadyIndexed) {
                        IndexDefinition index = buildIndex(session, foreignKey.getName(), foreignKey.getSourceFields(), false);
                        if (writer == null) {
                            try {
                                index.createOnDatabase(session);
                            } catch (Exception failed) {
                                //ignore
                            }
                        } else {
                            index.buildCreationWriter(session, writer);
                            writeLineSeperator(session, writer);
                        }
                    }
                }
            }
        }
        // Indexes
        for (IndexDefinition index : getIndexes()) {
            if (writer == null) {
                index.createOnDatabase(session);
            } else {
                index.buildCreationWriter(session, writer);
                writeLineSeperator(session, writer);
            }
        }
    }

    public void writeLineSeperator(AbstractSession session, Writer writer) {
        try {
            if (this.createSQLFiles) {
                writer.write(session.getPlatform().getStoredProcedureTerminationToken());
            }
            writer.write("\n");
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the delete SQL string.
     */
    public String deletionStringFor(DatabaseAccessor accessor) {
        return "DROP TABLE " + this.getName();
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     */
    @Override
    public void dropDatabaseSchema(AbstractSession session, Writer writer) throws EclipseLinkException {
        buildDatabaseSchemaDeletionWriter(session, writer);
    }
    
    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     */
    @Override
    public void dropDatabaseSchemaOnDatabase(AbstractSession session) throws EclipseLinkException {        
        session.priviledgedExecuteNonSelectingCall(new SQLCall(buildDatabaseSchemaDeletionWriter(session, new StringWriter()).toString()));
    }
    
    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void dropConstraints(AbstractSession session, Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            dropConstraintsOnDatabase(session);
        } else {
            if (session.getPlatform().supportsForeignKeyConstraints()){
                for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
                    buildConstraintDeletionWriter(session, foreignKey, schemaWriter);
                    writeLineSeperator(session, schemaWriter);
                }
            }
            if (session.getPlatform().supportsUniqueKeyConstraints()
                    && (!session.getPlatform().requiresUniqueConstraintCreationOnTableCreate())) {
                for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
                    buildUniqueConstraintDeletionWriter(session, uniqueKey, schemaWriter);
                    writeLineSeperator(session, schemaWriter);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string. Exceptions are caught and masked so that all
     * the foreign keys are dropped (even if they don't exist).
     */
    public void dropConstraintsOnDatabase(AbstractSession session) throws EclipseLinkException {
        dropForeignConstraintsOnDatabase(session);
        dropUniqueConstraintsOnDatabase(session);        
    }

    private void dropUniqueConstraintsOnDatabase(final AbstractSession session) throws ValidationException {        
        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            return;
        }
        
        for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
            try {
                session.priviledgedExecuteNonSelectingCall(new SQLCall(buildUniqueConstraintDeletionWriter(session, uniqueKey, new StringWriter()).toString()));
            } catch (DatabaseException ex) {/* ignore */
            }
        }        
    }

    private void dropForeignConstraintsOnDatabase(final AbstractSession session) throws ValidationException {        
        if ((!session.getPlatform().supportsForeignKeyConstraints()) || getForeignKeyMap().isEmpty()) {
            return;
        }

        for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
            try {
                session.priviledgedExecuteNonSelectingCall(new SQLCall(buildConstraintDeletionWriter(session, foreignKey, new StringWriter()).toString()));
            } catch (DatabaseException ex) {/* ignore */
            }
        }
    }

    /**
     * INTERNAL:<br>
     * Write the SQL drop index string to drop indexes if passed a writer,
     * else delegate to a method that executes the string on the database.
     * @throws ValidationException wraps any IOException from the writer
     */
    public void dropIndexes(AbstractSession session, Writer writer) {
        if (!session.getPlatform().supportsIndexes()) {
            return;
        }
        // Primary key
        if (session.getPlatform().shouldCreateIndicesForPrimaryKeys()) {
            List<String> primKeyList = getPrimaryKeyFieldNames();
            if (!primKeyList.isEmpty()) {
                IndexDefinition index = buildIndex(session, primKeyList.get(0), primKeyList, false);
                if (writer == null) {
                    try {
                        index.dropFromDatabase(session);
                    } catch (Exception notThere) {
                        //ignore
                    }
                } else {
                    index.buildDeletionWriter(session, writer);
                    writeLineSeperator(session, writer);
                }
            }
        }
        // Unique keys
        if (session.getPlatform().shouldCreateIndicesOnUniqueKeys()) {
            // indices for columns in unique key constraint declarations
            for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
                IndexDefinition index = buildIndex(session, uniqueKey.getName(), uniqueKey.getSourceFields(), false);
                if (writer == null) {
                    try {
                        index.dropFromDatabase(session);
                    } catch (Exception notThere) {
                        //ignore
                    }
                } else {
                    index.buildDeletionWriter(session, writer);
                    writeLineSeperator(session, writer);
                }
            }

            // indices for columns with unique=true declarations
            for (FieldDefinition field : getFields()) {
                if (field.isUnique()) {
                    List<String> columnAsList = new ArrayList<String>();
                    columnAsList.add(field.getName());
                    IndexDefinition index = buildIndex(session, field.getName(), columnAsList, true);
                    if (writer == null) {
                        try {
                            index.dropFromDatabase(session);
                        } catch (Exception notThere) {
                            //ignore
                        }
                    } else {
                        index.buildDeletionWriter(session, writer);
                        writeLineSeperator(session, writer);
                    }
                }
            }
        }
        // Foreign keys
        if (session.getPlatform().shouldCreateIndicesOnForeignKeys()) {
            // indices for columns in foreign key constraint declarations
            for (ForeignKeyConstraint foreignKey : getForeignKeys()) {
                if (!foreignKey.isDisableForeignKey()) {
                    if (!foreignKey.isDisableForeignKey()) {
                        // Do not re-index pk.
                        boolean alreadyIndexed = false;
                        List<String> primaryKeys = getPrimaryKeyFieldNames();
                        if ((primaryKeys.size() == foreignKey.getSourceFields().size())
                                && primaryKeys.containsAll(foreignKey.getSourceFields())) {
                            alreadyIndexed = true;
                        }
                        // Also check unique fields.
                        if (foreignKey.getSourceFields().size() == 1) {
                            FieldDefinition field = getField(foreignKey.getSourceFields().get(0));
                            if ((field != null) && field.isUnique()) {
                                alreadyIndexed = true;
                            }
                        }
                        for (UniqueKeyConstraint uniqueConstraint : getUniqueKeys()) {
                            if ((uniqueConstraint.getSourceFields().size() == foreignKey.getSourceFields().size())
                                    && uniqueConstraint.getSourceFields().containsAll(foreignKey.getSourceFields())) {
                                alreadyIndexed = true;
                            }
                        }
                        if (!alreadyIndexed) {
                            IndexDefinition index = buildIndex(session, foreignKey.getName(), foreignKey.getSourceFields(), false);
                            if (writer == null) {
                                try {
                                    index.dropFromDatabase(session);
                                } catch (Exception notThere) {
                                    //ignore
                                }
                            } else {
                                index.buildDeletionWriter(session, writer);
                                writeLineSeperator(session, writer);
                            }
                        }
                    }
                }
            }
        }
        // Indexes
        for (IndexDefinition index : getIndexes()) {
            if (writer == null) {
                try {
                    index.dropFromDatabase(session);
                } catch (Exception notThere) {
                    //ignore
                }
            } else {
                index.buildDeletionWriter(session, writer);
                writeLineSeperator(session, writer);
            }
        }
    }

    /**
     * INTERNAL:
     */
    public Map<String, ForeignKeyConstraint> getForeignKeyMap() {
        return foreignKeyMap;
    }

    /**
     * INTERNAL:
     */
    public void setForeignKeyMap(Map<String, ForeignKeyConstraint> foreignKeyMap) {
        this.foreignKeyMap = foreignKeyMap;
    }

    /**
     * PUBLIC:
     * Return the field the corresponds to the name.
     */
    public FieldDefinition getField(String fieldName) {
        for (FieldDefinition field : getFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * PUBLIC:
     */
    public List<FieldDefinition> getFields() {
        return fields;
    }

    /**
     * PUBLIC:
     * Returns the ForeignKeyConstraint list.
     */
    public Collection<ForeignKeyConstraint> getForeignKeys() {
        return this.foreignKeyMap.values();
    }

    /**
     * PUBLIC:
     */
    public List<UniqueKeyConstraint> getUniqueKeys() {
        return uniqueKeys;
    }
    
    /**
     * PUBLIC:
     */
    public void setIndexes(List<IndexDefinition> indexes) {
        this.indexes = indexes;
    }
    
    /**
     * PUBLIC:
     */
    public void setCreateVPDCalls(boolean createVPDCalls, String tenantFieldName) {
        this.createVPDCalls = createVPDCalls;
        this.tenantFieldName = tenantFieldName;
    }

    /**
     * PUBLIC:
     */
    public List<IndexDefinition> getIndexes() {
        return indexes;
    }
    
    /**
     * PUBLIC:
     */
    public List<String> getPrimaryKeyFieldNames() {
        List<String> keyNames = new ArrayList<String>();

        for (FieldDefinition field : getFields()) {
            if (field.isPrimaryKey()) {
                keyNames.add(field.getName());
            }
        }
        return keyNames;
    }

    
    /**
     * Execute any statements required after the creation of the object
     * @param session
     * @param createSchemaWriter
     */
    public void postCreateObject(AbstractSession session, Writer createSchemaWriter, boolean createSQLFiles){
        // create indices on table's primary and unique keys (if required)
        setCreateSQLFiles(createSQLFiles);
        createIndexes(session, createSchemaWriter);
    }
    
    /**
     * Execute any statements required before the deletion of the object
     * @param session
     * @param dropSchemaWriter
     */
    public void preDropObject(AbstractSession session, Writer dropSchemaWriter, boolean createSQLFiles) {
        // drop indices on table's primary and unique keys (if required)
        setCreateSQLFiles(createSQLFiles);
        dropIndexes(session, dropSchemaWriter);
    }
    
    /**
     * PUBLIC:
     */
    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

    /**
     * PUBLIC:
     * Set the ForeignKeyConstraint list.
     * If the list contains the same name foreign key constraints, only the first one of that name will be added.
     */
    public void setForeignKeys(List<ForeignKeyConstraint> foreignKeys) {
        this.foreignKeyMap.clear();
        if (foreignKeys != null) {
            for( ForeignKeyConstraint foreignKey : foreignKeys) {
                this.foreignKeyMap.put(foreignKey.getName(), foreignKey);
            }
        }
    }
    
    /**
     * PUBLIC:
     */
    public void setUniqueKeys(List<UniqueKeyConstraint> uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }
    
    /**
     * PUBLIC:
     * Set the foreign key constraints for this table.
     */
    public void setUserDefinedForeignKeyConstraints(Map<String, ForeignKeyConstraint> foreignKeyConstraints) {
        foreignKeyMap = foreignKeyConstraints;
        hasUserDefinedForeignKeyConstraints = true;
    }
    
    /**
     * If this table has a schema (and catalog specified) make sure it is
     * created.
     */
    @Override
    public boolean shouldCreateDatabaseSchema(Set<String> createdDatabaseSchemas) {
        return hasDatabaseSchema() && ! createdDatabaseSchemas.contains(getDatabaseSchema());
    }
    
    /**
     * INTERNAL:
     * Subclasses who care should override this method.
     */
    public boolean shouldCreateVPDCalls(AbstractSession session) {
        if (createVPDCalls) {
            if (! session.getPlatform().supportsVPD()) {
                throw ValidationException.vpdNotSupported(session.getPlatform().getClass().getName());
            }
        }
        
        return createVPDCalls;
    }
    
    /**
     * PUBLIC:
     */
    public void setCreateSQLFiles(boolean genFlag) {
        this.createSQLFiles = genFlag;
    }

    public DatabaseTable getTable() {
        return table;
    }
    
    public void setTable(DatabaseTable table) {
        this.table = table;
    }
}
