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
package io.github.jeddict.relation.mapper.initializer;

import org.netbeans.api.db.explorer.DatabaseConnection;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.spec.extend.cache.DBConnectionUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modules.db.explorer.sql.editor.SQLEditorSupport;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Shiwani Gupta
 */
public class SQLEditorUtil {

    private static final RequestProcessor RP = new RequestProcessor("Generated SQL");
    private static final String SQL_PREFIX = "select * from ";
    public static void openEditor(ModelerFile modelerFile, String sql) {
        final DatabaseConnection connection = DBConnectionUtil.getConnection(modelerFile);
        RP.post(() -> {
            try {
                SQLEditorSupport.openSQLEditor(connection, sql, false);
                JeddictLogger.recordDBAction("Open SQL Editor");
            } catch (Exception exc) {
                modelerFile.handleException(exc);
            }
        });
    }
    
    public static void openDBTable(TableWidget tableWidget) {
        ModelerFile modelerFile = tableWidget.getModelerScene().getModelerFile();
        String tableName = tableWidget.getName();
        final DatabaseConnection connection = DBConnectionUtil.getConnection(modelerFile);
        RP.post(() -> {
            try {
                SQLEditorSupport.openSQLEditor(connection, SQL_PREFIX + tableName, true); //NOI18N
                JeddictLogger.recordDBAction("Open DB Table");
            } catch (Exception exc) {
                modelerFile.handleException(exc);
            }
        });
    }
}
