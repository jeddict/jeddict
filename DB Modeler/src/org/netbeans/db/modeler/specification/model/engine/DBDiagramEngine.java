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
import org.netbeans.jpa.modeler.spec.extend.cache.Cache.DBConnectionUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.engine.ModelerDiagramEngine;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public class DBDiagramEngine extends ModelerDiagramEngine {

    @Override
    public void buildToolBar(JToolBar bar) {

        buildReloadTool(bar);
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
        reloadButton.addActionListener((ActionEvent e) -> {
            ModelerFile parentFile = file.getParentFile();
            file.getModelerPanelTopComponent().close();
            DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);//new DefaultSourceCodeGeneratorFactory();//SourceGeneratorFactoryProvider.getInstance();//
            dbModelerRequestManager.init(parentFile);
        });
    }

    private JComboBox dbConComboBox;

    private void buildDBCon(JToolBar bar) {
        dbConComboBox = new javax.swing.JComboBox();
        DBConnectionUtil.loadConnection((EntityMappings) file.getParentFile().getDefinitionElement(), dbConComboBox);
        bar.add(dbConComboBox);
        dbConComboBox.addItemListener(this::dbConComboBoxItemStateChanged);
    }

    private void dbConComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            DBConnectionUtil.saveConnection(file, dbConComboBox);
            DatabaseConnection connection = DBConnectionUtil.getConnection(dbConComboBox);
            if (connection != null) {
                ModelerFile parentFile = file.getParentFile();
                file.getModelerPanelTopComponent().close();
                DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);//new DefaultSourceCodeGeneratorFactory();//SourceGeneratorFactoryProvider.getInstance();//
                dbModelerRequestManager.init(parentFile);
                parentFile.addAttribute(DatabaseConnection.class.getName(), connection);
            }

        }
    }

}
