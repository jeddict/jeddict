/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler.def;

import static io.github.jeddict.jcode.jpa.JPAConstants.EXCLUDE_DEFAULT_LISTENERS_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.EXCLUDE_SUPERCLASS_LISTENERS_FQN;
import io.github.jeddict.orm.generator.compiler.CacheableDefSnippet;
import io.github.jeddict.orm.generator.compiler.EntityListenersSnippet;
import io.github.jeddict.orm.generator.compiler.IdClassSnippet;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.NamedEntityGraphsSnippet;
import io.github.jeddict.orm.generator.compiler.NamedNativeQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.NamedQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.NamedStoredProcedureQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.PrimaryKeyJoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.SQLResultSetMappingsSnippet;
import io.github.jeddict.orm.generator.compiler.SecondaryTablesSnippet;
import io.github.jeddict.orm.generator.compiler.TableDefSnippet;
import io.github.jeddict.orm.generator.util.ImportSet;

public class IdentifiableClassDefSnippet extends ManagedClassDefSnippet {

    private TableDefSnippet tableDef;
    private IdClassSnippet idClass;
    private PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumns;

    private NamedQueriesSnippet namedQueries;
    private NamedNativeQueriesSnippet namedNativeQueries;
    private NamedEntityGraphsSnippet namedEntityGraphs;
    private NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries;
    private SQLResultSetMappingsSnippet sqlResultSetMappings;
    private EntityListenersSnippet entityListeners;
    private SecondaryTablesSnippet secondaryTables;
    private CacheableDefSnippet cacheableDef;

    private boolean excludeDefaultListener;
    private boolean excludeSuperClassListener;

    public NamedQueriesSnippet getNamedQueries() {
        return namedQueries;
    }

    public void setNamedQueries(NamedQueriesSnippet namedQueries) {
        this.namedQueries = namedQueries;
    }

    public EntityListenersSnippet getEntityListeners() {
        return entityListeners;
    }

    public void setEntityListeners(EntityListenersSnippet entityListeners) {
        this.entityListeners = entityListeners;
    }

    public NamedNativeQueriesSnippet getNamedNativeQueries() {
        return namedNativeQueries;
    }

    public void setNamedNativeQueries(NamedNativeQueriesSnippet namedNativeQueries) {
        this.namedNativeQueries = namedNativeQueries;
    }

    public SQLResultSetMappingsSnippet getSQLResultSetMappings() {
        return sqlResultSetMappings;
    }

    public void setSQLResultSetMappings(SQLResultSetMappingsSnippet sqlResultSetMappings) {
        this.sqlResultSetMappings = sqlResultSetMappings;
    }

    public NamedEntityGraphsSnippet getNamedEntityGraphs() {
        return namedEntityGraphs;
    }

    public void setNamedEntityGraphs(NamedEntityGraphsSnippet namedEntityGraphs) {
        this.namedEntityGraphs = namedEntityGraphs;
    }

    public NamedStoredProcedureQueriesSnippet getNamedStoredProcedureQueries() {
        return namedStoredProcedureQueries;
    }

    public void setNamedStoredProcedureQueries(NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries) {
        this.namedStoredProcedureQueries = namedStoredProcedureQueries;
    }

    public IdClassSnippet getIdClass() {
        return idClass;
    }

    public void setIdClass(IdClassSnippet idClass) {
        this.idClass = idClass;
    }

    public TableDefSnippet getTableDef() {
        return tableDef;
    }

    public void setTableDef(TableDefSnippet tableDef) {
        this.tableDef = tableDef;
    }

    public SecondaryTablesSnippet getSecondaryTables() {
        return secondaryTables;
    }

    public void setSecondaryTables(SecondaryTablesSnippet secondaryTables) {
        this.secondaryTables = secondaryTables;
    }

    public CacheableDefSnippet getCacheableDef() {
        return cacheableDef;
    }

    public void setCacheableDef(CacheableDefSnippet cacheableDef) {
        this.cacheableDef = cacheableDef;
    }

    public PrimaryKeyJoinColumnsSnippet getPrimaryKeyJoinColumns() {
        return primaryKeyJoinColumns;
    }

    public void setPrimaryKeyJoinColumns(PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumns) {
        this.primaryKeyJoinColumns = primaryKeyJoinColumns;
    }

    public boolean isDefaultExcludeListener() {
        return excludeDefaultListener;
    }

    public void setDefaultExcludeListener(boolean excludeDefaultListener) {
        this.excludeDefaultListener = excludeDefaultListener;
    }

    public boolean isExcludeSuperClassListener() {
        return excludeSuperClassListener;
    }

    public void setExcludeSuperClassListener(boolean excludeSuperClassListener) {
        this.excludeSuperClassListener = excludeSuperClassListener;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet importSnippets = super.getImportSet();

        if (namedQueries != null) {
            importSnippets.addAll(namedQueries.getImportSnippets());
        }

        if (namedNativeQueries != null) {
            importSnippets.addAll(namedNativeQueries.getImportSnippets());
        }

        if (namedEntityGraphs != null) {
            importSnippets.addAll(namedEntityGraphs.getImportSnippets());
        }

        if (namedStoredProcedureQueries != null) {
            importSnippets.addAll(namedStoredProcedureQueries.getImportSnippets());
        }

        if (sqlResultSetMappings != null) {
            importSnippets.addAll(sqlResultSetMappings.getImportSnippets());
        }

        if (entityListeners != null) {
            importSnippets.addAll(entityListeners.getImportSnippets());
        }

        if (idClass != null) {
            importSnippets.addAll(idClass.getImportSnippets());
        }

        if (tableDef != null) {
            importSnippets.addAll(tableDef.getImportSnippets());
        }

        if (secondaryTables != null) {
            importSnippets.addAll(secondaryTables.getImportSnippets());
        }

        if (cacheableDef != null) {
            importSnippets.addAll(cacheableDef.getImportSnippets());
        }

        if (primaryKeyJoinColumns != null) {
            importSnippets.addAll(primaryKeyJoinColumns.getImportSnippets());
        }

        if (excludeDefaultListener) {
            importSnippets.add(EXCLUDE_DEFAULT_LISTENERS_FQN);
        }

        if (excludeSuperClassListener) {
            importSnippets.add(EXCLUDE_SUPERCLASS_LISTENERS_FQN);
        }

        return importSnippets;
    }

}
