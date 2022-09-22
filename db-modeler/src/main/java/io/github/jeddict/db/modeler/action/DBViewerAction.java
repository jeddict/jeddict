/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.db.modeler.action;

import io.github.jeddict.db.modeler.initializer.DBModelerActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import static java.util.stream.Collectors.toList;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.node.BaseNode;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.node.SchemaNode;
import org.netbeans.modules.db.explorer.node.TableListNode;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.MetadataElementHandle;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import static org.openide.util.HelpCtx.DEFAULT_HELP;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

public class DBViewerAction extends NodeAction {

    private static final RequestProcessor RP = new RequestProcessor(DBViewerAction.class);
    
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return DEFAULT_HELP;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        String database = null; // return false, if table selected across different database
        BaseNode previousBaseNode = null;   // return false, if table and tablelist selected
        for(Node activatedNode : activatedNodes){
            BaseNode baseNode = activatedNode.getLookup().lookup(BaseNode.class);
            if(baseNode == null){ //this
                return false;
            }
            if(baseNode.getClass() == SchemaNode.class) {
                name = getMessage(DBViewerAction.class, "SchemaNode.title");
            } else if(baseNode.getClass() == TableListNode.class) {
                name = getMessage(DBViewerAction.class, "TableListNode.title");
            } else if(baseNode.getClass() == TableNode.class) {
                name = getMessage(DBViewerAction.class, "TableNode.title");
            }
            if (previousBaseNode == null) {
                previousBaseNode = baseNode;
            } else if (previousBaseNode.getClass() != baseNode.getClass()) {
                return false;
            }
            DatabaseConnection connection = baseNode.getLookup().lookup(DatabaseConnection.class);
            if(database == null){
                database = connection.getDatabase();
            } else if(!database.equals(connection.getDatabase())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void performAction(Node[] activatedNodes) {
        if (activatedNodes[0] == null) {
            return;
        }
        List<BaseNode> baseNodes = Arrays.stream(activatedNodes)
                .map(activatedNode -> activatedNode.getLookup().lookup(BaseNode.class))
                .collect(toList());
        final BaseNode baseNode = baseNodes.get(0);
        
        SwingUtilities.invokeLater(() -> {
            DBModelerActionListener actionListener = new DBModelerActionListener();
            DatabaseConnection connection = baseNode.getLookup().lookup(DatabaseConnection.class);
            MetadataElementHandle handle = baseNode.getLookup().lookup(MetadataElementHandle.class);
            Schema schema;
            if (baseNode instanceof TableNode) {
                schema = getSchemaFromTable(connection, handle);
            } else {
                schema = getSchema(connection, handle);
            }
            actionListener.init(baseNode, new TableNodeList(baseNodes), schema);
        });
    }
    
    public static void reloadDBViewer(ModelerFile file) {
        file.close();
        BaseNode baseNode = (BaseNode) file.getAttribute(Node.class.getSimpleName());
        TableNodeList tableNodeList = (TableNodeList) file.getAttribute(TableNodeList.class.getSimpleName());
        refresh(baseNode);
        DBModelerActionListener actionListener = new DBModelerActionListener();
        DatabaseConnection connection = baseNode.getLookup().lookup(DatabaseConnection.class);
        MetadataElementHandle handle = baseNode.getLookup().lookup(MetadataElementHandle.class);
        Schema schema;
        if (baseNode instanceof TableNode) {
            schema = getSchemaFromTable(connection, handle);
        } else {
            schema = getSchema(connection, handle);
        }
        actionListener.init(baseNode, tableNodeList, schema);
    }
    
    private static void refresh(BaseNode baseNode) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            RP.post(() -> {
                MetadataModel model = baseNode.getLookup().lookup(DatabaseConnection.class).getMetadataModel();
                if (model != null) {
                    try {
                        model.runReadAction(metadata -> {
                            metadata.refresh();
                            latch.countDown();
                        });
                    } catch (MetadataModelException e) {
                        Exceptions.printStackTrace(e);
                    }
                }
                baseNode.refresh();
            });
            latch.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static Schema getSchema(DatabaseConnection connection, MetadataElementHandle<Schema> schemaHandle) {
        final Schema[] array = {null};
        MetadataModel metaDataModel = connection.getMetadataModel();
        if (connection.isConnected() && metaDataModel != null) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                RP.post(() -> {
                    try {
                        metaDataModel.runReadAction(metaData -> {
                            array[0] = schemaHandle.resolve(metaData);
                            latch.countDown();
                        });
                    } catch (MetadataModelException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                latch.await();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return array[0];
    }
    private static Schema getSchemaFromTable(DatabaseConnection connection, MetadataElementHandle<Table> tableHandle) {
        final Table[] array = {null};
        MetadataModel metaDataModel = connection.getMetadataModel();
        if (connection.isConnected() && metaDataModel != null) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                RP.post(() -> {
                    try {
                        metaDataModel.runReadAction(metaData -> {
                            array[0] = tableHandle.resolve(metaData);
                            latch.countDown();
                        });
                    } catch (MetadataModelException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                latch.await();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return array[0].getParent();
    }
    
 }
