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
package io.github.jeddict.db.modeler.action;

import io.github.jeddict.db.modeler.initializer.DBModelerActionListener;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.node.BaseNode;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataElementHandle;
import org.netbeans.modules.db.metadata.model.api.MetadataModel;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

public class DBViewerAction extends NodeAction {

    private static final RequestProcessor RP = new RequestProcessor(DBViewerAction.class);

    @Override
    public String getName() {
        return "View DB";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(DBViewerAction.class);
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        boolean enabled = false;

        if (activatedNodes.length == 1) {
            enabled = null != activatedNodes[0].getLookup().lookup(BaseNode.class);
        }

        return true;
    }

    @Override
    public void performAction(Node[] activatedNodes) {
        if (activatedNodes[0] == null) {
            return;
        }
        final BaseNode baseNode = activatedNodes[0].getLookup().lookup(BaseNode.class);
        SwingUtilities.invokeLater(() -> {
            DBModelerActionListener actionListener = new DBModelerActionListener();
            DatabaseConnection connection = baseNode.getLookup().lookup(DatabaseConnection.class);
            MetadataElementHandle schemaHandle = baseNode.getLookup().lookup(MetadataElementHandle.class);
            Schema schema = getSchema(connection, schemaHandle);
            actionListener.init(baseNode, schema);
        });
    }
    
    public static void reloadDBViewer(ModelerFile file) {
        BaseNode baseNode = (BaseNode) file.getAttribute(Node.class.getSimpleName());
        file.close();
        refresh(baseNode);
        DBModelerActionListener actionListener = new DBModelerActionListener();
        DatabaseConnection connection = baseNode.getLookup().lookup(DatabaseConnection.class);
        MetadataElementHandle schemaHandle = baseNode.getLookup().lookup(MetadataElementHandle.class);
        Schema schema = getSchema(connection, schemaHandle);
        actionListener.init(baseNode, schema);
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
}
