/**
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
package org.netbeans.db.modeler.specification.model.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.db.modeler.manager.DBModelerRequestManager;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.RELOAD_ICON;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.cache.DBConnectionUtil;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.engine.ModelerDiagramEngine;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public class DBDiagramEngine extends ModelerDiagramEngine {

    @Override
    public void buildToolBar(JToolBar bar) {
        buildReloadTool(bar);
        buildExportDocTool(bar);
        buildSatelliteTool(bar);
        bar.add(new JToolBar.Separator());
        buildDBCon(bar);
        bar.add(new JToolBar.Separator());
        buildSelectTool(bar);
        bar.add(new JToolBar.Separator());
        buildZoomTool(bar);
        bar.add(new JToolBar.Separator());
    }

    protected void buildReloadTool(JToolBar bar) {
        JButton reloadButton = new JButton(RELOAD_ICON);
        reloadButton.setToolTipText("Reload Diagram");
        bar.add(reloadButton);
        reloadButton.addActionListener(e -> {
            ModelerFile parentFile = file.getParentFile();
            EntityMappings entityMappings = (EntityMappings) parentFile.getModelerScene().getBaseElementSpec();
            DBUtil.openDBViewer(parentFile, entityMappings, entityMappings.getCurrentWorkSpace());
        });
    }

    private JComboBox dbConComboBox;

    private void buildDBCon(JToolBar bar) {
        dbConComboBox = new javax.swing.JComboBox();
        DBConnectionUtil.loadConnection(file.getParentFile(), dbConComboBox);
        bar.add(dbConComboBox);
        dbConComboBox.addItemListener(this::dbConComboBoxItemStateChanged);
    }

    private void dbConComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            DBConnectionUtil.saveConnection(file, dbConComboBox);
            DatabaseConnection connection = DBConnectionUtil.getConnection(dbConComboBox);
            if (connection != null) {
                ModelerFile parentFile = file.getParentFile();
                EntityMappings entityMappings = (EntityMappings) parentFile.getModelerScene().getBaseElementSpec();
                DBUtil.openDBViewer(parentFile, entityMappings, entityMappings.getCurrentWorkSpace());
//                IModelerScene scene = file.getModelerScene();
//                scene.getBaseElements().stream().filter(element -> element instanceof INodeWidget).forEach(element -> {
//                    ((INodeWidget) element).remove(false);
//                });
//                file.unload();
//                try {
//                    file.getModelerUtil().loadModelerFile(file);
//                } catch (Exception ex) {
//                    file.handleException(ex);
//                }
//                file.load();
//                DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);
//                dbModelerRequestManager.init(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
//                parentFile.addAttribute(DatabaseConnection.class.getName(), connection);

            }

        }
    }

}
