/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.reveng.database;

import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import io.github.jeddict.reveng.database.generator.DBModelGenerator;
import io.github.jeddict.reveng.database.generator.DbSchemaEntityGenerator;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityClass;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityRelation;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityRelation.CollectionType;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityRelation.FetchType;
import org.netbeans.modules.j2ee.persistence.entitygenerator.GeneratedTables;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.DBSchemaFileList;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.Table;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.TableClosure;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.TableSource;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.UpdateType;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class ImportHelper {

    private final Project project;
    private final FileObject configFilesFolder;
    private final DBSchemaFileList dbschemaFileList;

    private SchemaElement schemaElement;
    private DatabaseConnection dbconn;
    private FileObject dbschemaFile;
    private String datasourceName;

    private TableClosure tableClosure;
    private SelectedTables selectedTables;

    private SourceGroup location;
    private String packageName;

    private boolean useColumnNamesInRelationships = true;
    private boolean generateUnresolvedRelationships = false;
    private boolean useDefaults = false;

    private DbSchemaEntityGenerator generator;

    private TableSource tableSource;

    private String fileName;

    // Global mapping options added in NB 6.5
    private boolean fullyQualifiedTableNames = false;
    private FetchType fetchType = FetchType.DEFAULT;
    private boolean regenTablesAttrs = false;
    private CollectionType collectionType = CollectionType.COLLECTION;

    public ImportHelper(Project project, FileObject configFilesFolder) {
        this.project = project;
        this.configFilesFolder = configFilesFolder;
        tableSource = TableSource.get(project);
        dbschemaFileList = new DBSchemaFileList(project, configFilesFolder);
    }

    public Project getProject() {
        return project;
    }

    FileObject getConfigFilesFolder() {
        return configFilesFolder;
    }

    public DBSchemaFileList getDBSchemaFileList() {
        return dbschemaFileList;
    }

    public void setTableClosure(TableClosure tableClosure) {
        assert tableClosure != null;
        this.tableClosure = tableClosure;
    }

    public TableClosure getTableClosure() {
        return tableClosure;
    }

    public void setSelectedTables(SelectedTables selectedTables) {
        this.selectedTables = selectedTables;
    }

    /**
     * Sets the source of the tables when the source is a database connection
     * (possibly retrieved from a data source).
     *
     * @param schemaElement the SchemaElement instance containing the database
     * tables.
     * @param dbconn the database connection which was used to retrieve
     * <code>schemaElement</code>.
     * @param dataSourceName the JNDI name of the
     * {@link org.netbeans.modules.j2ee.deployment.common.api.Datasource data source}
     * which was used to retrieve <code>dbconn</code> or null if the connection
     * was not retrieved from a data source.
     */
    public void setTableSource(SchemaElement schemaElement, DatabaseConnection dbconn, String datasourceName) {
        this.schemaElement = schemaElement;
        this.dbconn = dbconn;
        this.dbschemaFile = null;
        this.datasourceName = datasourceName;

        updateTableSource();
    }

    /**
     * Sets the source of the tables when the source is a dbschema file.
     *
     * @param schemaElement the SchemaElement instance containing the database
     * tables.
     * @param dbschemaFile the dbschema file which was used to retrieve
     * <code>schemaElement</code>.
     */
    public void setTableSource(SchemaElement schemaElement, FileObject dbschemaFile) {
        this.schemaElement = schemaElement;
        this.dbconn = null;
        this.dbschemaFile = dbschemaFile;
        this.datasourceName = null;

        updateTableSource();
    }

    public TableSource getTableSource() {
        return tableSource;
    }

    private void updateTableSource() {
        if (dbconn != null) {
            if (datasourceName != null) {
                tableSource = new TableSource(datasourceName, TableSource.Type.DATA_SOURCE);
            } else {
                tableSource = new TableSource(dbconn.getName(), TableSource.Type.CONNECTION);
            }
        } else if (dbschemaFile != null) {
            tableSource = new TableSource(FileUtil.toFile(dbschemaFile).getAbsolutePath(), TableSource.Type.SCHEMA_FILE);
        } else {
            tableSource = null;
        }
    }

    public SchemaElement getSchemaElement() {
        return schemaElement;
    }

    public DatabaseConnection getDatabaseConnection() {
        return dbconn;
    }

    public FileObject getDBSchemaFile() {
        return dbschemaFile;
    }

    /**
     * Returns the package for bean and module generation.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package for bean and module generation.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public FileObject getPackageObject() throws IOException {
        return getFolderForPackage(getLocation(), getPackageName());
    }

    public SourceGroup getLocation() {
        return location;
    }

    public void setLocation(SourceGroup location) {
        this.location = location;
    }

    public boolean isUseColumnNamesInRelationships() {
        return useColumnNamesInRelationships;
    }

    public void setUseColumnNamesInRelationships(boolean useColumnNamesInRelationships) {
        this.useColumnNamesInRelationships = useColumnNamesInRelationships;
    }

    public boolean isFullyQualifiedTableNames() {
        return fullyQualifiedTableNames;
    }

    public void setFullyQualifiedTableNames(boolean fullyQualifiedNames) {
        this.fullyQualifiedTableNames = fullyQualifiedNames;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    public boolean isRegenTablesAttrs() {
        return regenTablesAttrs;
    }

    public void setRegenTablesAttrs(boolean regenSchemaAttrs) {
        this.regenTablesAttrs = regenSchemaAttrs;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(CollectionType type) {
        collectionType = type;
    }

    public void buildBeans() {
        TableSource.put(project, tableSource);

        if (selectedTables != null) {
            GenerateTablesImpl genTables = new GenerateTablesImpl();
            FileObject rootFolder = getLocation().getRootFolder();
            String pkgName = getPackageName();

            for (Table table : selectedTables.getTables()) {
                genTables.addTable(
                        table.getCatalog(),
                        table.getSchema(),
                        table.getName(),
                        rootFolder,
                        pkgName,
                        selectedTables.getClassName(table),
                        table.getUniqueConstraints()
                );
            }
            buildBeans(genTables);
        }
    }

    public void buildBeans(GenerateTablesImpl genTables) {
        generator = new DbSchemaEntityGenerator(genTables, schemaElement, collectionType, useColumnNamesInRelationships, useDefaults, generateUnresolvedRelationships);
    }

    public EntityClass[] getBeans() {
        return generator.getBeans();
    }

    public EntityRelation[] getRelations() {
        return generator.getRelations();
    }

    /**
     * @return the generateUnresolvedRelationships
     */
    public boolean isGenerateUnresolvedRelationships() {
        return generateUnresolvedRelationships;
    }

    /**
     * @param generateUnresolvedRelationships the
     * generateUnresolvedRelationships to set
     */
    public void setGenerateUnresolvedRelationships(boolean generateUnresolvedRelationships) {
        this.generateUnresolvedRelationships = generateUnresolvedRelationships;
    }

    /**
     * @return the useDefaults
     */
    public boolean isUseDefaults() {
        return useDefaults;
    }

    /**
     * @param useDefaults the useDefaults to set
     */
    public void setUseDefaults(boolean useDefaults) {
        this.useDefaults = useDefaults;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
