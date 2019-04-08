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
package io.github.jeddict.relation.mapper.initializer;

import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import org.netbeans.api.db.explorer.DatabaseConnection;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.RELOAD_ICON;
import io.github.jeddict.jpa.spec.extend.cache.DBConnectionUtil;
import io.github.jeddict.jpa.modeler.initializer.DBUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.engine.ModelerDiagramEngine;

/**
 *
 * @author Gaurav Gupta
 */
public class RelationMapperDiagramEngine extends ModelerDiagramEngine {

    @Override
    public void buildToolBar(JToolBar bar) {
        buildReloadTool(bar);
        buildExportDocTool(bar);
        buildSatelliteTool(bar);
        buildSearchTool(bar);
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
        reloadButton.addActionListener(e -> DBUtil.openDBModeler(file.getParentFile()));
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
                DBUtil.openDBModeler(parentFile);
            }

        }
    }

}
