/* 
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.external.jpqleditor;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modules.j2ee.persistence.api.PersistenceEnvironment;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.editor.JPAEditorUtil;
import org.netbeans.modules.j2ee.persistence.jpqleditor.JPQLExecutor;
//import org.netbeans.modules.j2ee.persistence.jpqleditor.JPQLResult;
import org.netbeans.modules.j2ee.persistence.jpqleditor.Utils;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.Provider;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

/**
 * JPQL Editor controller. Controls overall JPQL query execution.
 */
public class JPQLExternalEditorController {

    private static final Logger logger = Logger.getLogger(JPQLExternalEditorController.class.getName());
    private JPQLExternalEditorTopComponent editorTopComponent = null;

    private enum AnnotationAccessType {

        FIELD_TYPE,
        METHOD_TYPE;
    };

    public void executeJPQLQuery(final String jpql,
            final PersistenceUnit pu,
            final PersistenceEnvironment pe,
            final int maxRowCount,
            final ProgressHandle ph) {
        final List<URL> localResourcesURLList = new ArrayList<URL>();

        //
        final HashMap<String, String> props = new HashMap<String, String>();
        final List<String> initialProblems = new ArrayList<String>();
        //connection open
        final DatabaseConnection dbconn = JPAEditorUtil.findDatabaseConnection(pu, pe.getProject());
        if (dbconn != null) {
            if (dbconn.getJDBCConnection() == null) {
                Mutex.EVENT.readAccess(new Mutex.Action<DatabaseConnection>() {
                    @Override
                    public DatabaseConnection run() {
                        ConnectionManager.getDefault().showConnectionDialog(dbconn);
                        return dbconn;
                    }
                });
            }
        }
        //
        final boolean containerManaged = Util.isSupportedJavaEEVersion(pe.getProject());
        final Provider provider = ProviderUtil.getProvider(pu.getProvider(), pe.getProject());
        if (containerManaged && provider!=null) {
            Utils.substitutePersistenceProperties(pe, pu, dbconn, props);
        }
        final ClassLoader defClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ph.progress(10);
            ph.setDisplayName(NbBundle.getMessage(JPQLExternalEditorTopComponent.class, "queryExecutionPrepare"));
            // Construct custom classpath here.
            initialProblems.addAll(Utils.collectClassPathURLs(pe, pu, dbconn, localResourcesURLList));

            ClassLoader customClassLoader = pe.getProjectClassLoader(
                    localResourcesURLList.toArray(new URL[]{}));
            Thread.currentThread().setContextClassLoader(customClassLoader);
            Thread t = new Thread() {
                @Override
                public void run() {
                    ClassLoader customClassLoader = Thread.currentThread().getContextClassLoader();
                    CustomJPQLResult jpqlResult = new CustomJPQLResult();
                    if (initialProblems.isEmpty()) {
                        JPQLExecutor queryExecutor = new JPQLExecutor();
                        try {
                            // Parse POJOs from JPQL
                            // Check and if required compile POJO files mentioned in JPQL

                            ph.progress(50);
                            ph.setDisplayName(NbBundle.getMessage(JPQLExternalEditorTopComponent.class, "queryExecutionPassControlToProvider"));
                            jpqlResult = (CustomJPQLResult)queryExecutor.execute(jpql, pu, pe, props, provider, maxRowCount, ph, true);
                            ph.progress(80);
                            ph.setDisplayName(NbBundle.getMessage(JPQLExternalEditorTopComponent.class, "queryExecutionProcessResults"));

                        } catch (Exception e) {
                            logger.log(Level.INFO, "Problem in executing JPQL", e);
                            jpqlResult.getExceptions().add(e);
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (String txt : initialProblems) {
                            sb.append(txt).append("\n");
                        }
                        jpqlResult.setQueryProblems(sb.toString());
                        jpqlResult.getExceptions().add(new Exception(sb.toString()));
                    }
                    final CustomJPQLResult jpqlResult0 = jpqlResult;
                    final ClassLoader customClassLoader0 = customClassLoader;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            editorTopComponent.setResult(jpqlResult0, customClassLoader0);
                        }
                    });

                    Thread.currentThread().setContextClassLoader(defClassLoader);
                }
            };
            t.setContextClassLoader(customClassLoader);
            t.start();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(defClassLoader);
        }
    }

    public void init(ModelerFile modelerFile) {
        editorTopComponent = new JPQLExternalEditorTopComponent(this);
        editorTopComponent.open();
        editorTopComponent.requestActive();
        editorTopComponent.setFocusToEditor();
        PUDataObject pud;
        try {
            Project project = modelerFile.getProject();
            pud = ProviderUtil.getPUDataObject(project);
            editorTopComponent.fillPersistenceConfigurations(pud);
        } catch (InvalidPersistenceXmlException ex) {
            modelerFile.handleException(ex);
        }
    }
}
