/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.reveng.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.j2ee.persistence.entitygenerator.GeneratedTables;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.UpdateType;
import org.openide.filesystems.FileObject;

/**
 *
 * @author jGauravGupta
 */
public class GenerateTablesImpl implements GeneratedTables {

    private String catalog; // for all the tables
    private String schema; // for all the tables
    private final Set<String> tableNames = new HashSet<>();
    private final Map<String, FileObject> rootFolders = new HashMap<>();
    private final Map<String, String> packageNames = new HashMap<>();
    private final Map<String, String> classNames = new HashMap<>();
    private final Map<String, UpdateType> updateTypes = new HashMap<>();
    private final Map<String, Set<List<String>>> allUniqueConstraints = new HashMap<>();

    @Override
    public Set<String> getTableNames() {
        return Collections.unmodifiableSet(tableNames);
    }

    public void addTable(String catalogName, String schemaName, String tableName,
            FileObject rootFolder, String packageName, String className,
            Set<List<String>> uniqueConstraints) {
        tableNames.add(tableName);
        catalog = catalogName;
        schema = schemaName;
        rootFolders.put(tableName, rootFolder);
        packageNames.put(tableName, packageName);
        classNames.put(tableName, className);
//            updateTypes.put(tableName, updateType);
        allUniqueConstraints.put(tableName, uniqueConstraints);
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public FileObject getRootFolder(String tableName) {
        return rootFolders.get(tableName);
    }

    @Override
    public String getPackageName(String tableName) {
        return packageNames.get(tableName);
    }

    @Override
    public String getClassName(String tableName) {
        return classNames.get(tableName);
    }

    @Override
    public UpdateType getUpdateType(String tableName) {
        return updateTypes.get(tableName);
    }

    @Override
    public Set<List<String>> getUniqueConstraints(String tableName) {
        return this.allUniqueConstraints.get(tableName);
    }
}
