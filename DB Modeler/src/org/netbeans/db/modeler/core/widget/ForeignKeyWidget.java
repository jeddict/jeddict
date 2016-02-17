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
package org.netbeans.db.modeler.core.widget;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.DBModelerUtil;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ForeignKeyWidget<E extends DBColumn> extends ColumnWidget<E> {

    public ForeignKeyWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(DBModelerUtil.FOREIGNKEY);
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        if (this.getTableWidget() instanceof BaseTableWidget) {
            JMenuItem joinTable = new JMenuItem("Create Join Table");//, MICRO_DB);
            joinTable.addActionListener((ActionEvent e) -> {
                String joinTableName = JOptionPane.showInputDialog((Component)ForeignKeyWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter join table name");
                convertToJoinTable(joinTableName);
                ModelerFile parentFile = ForeignKeyWidget.this.getModelerScene().getModelerFile().getParentFile();
                JPAModelerUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
            });
            menuList.add(0, joinTable);
        }
        return menuList;
    }

    abstract void convertToJoinTable(String name);

}
