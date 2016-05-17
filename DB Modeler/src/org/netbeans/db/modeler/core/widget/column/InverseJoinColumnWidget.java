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
package org.netbeans.db.modeler.core.widget.column;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.db.modeler.core.widget.table.BaseTableWidget;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBInverseJoinColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.ColumnUtil;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class InverseJoinColumnWidget extends ForeignKeyWidget<DBInverseJoinColumn> {

    public InverseJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("JoinColumn_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(InverseJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected String evaluateName() {
        return InverseJoinColumnWidget.evaluateName(this.getBaseElementSpec().getAttribute(), this.getBaseElementSpec().getReferenceColumn());
    }

    public static String evaluateName(RelationAttribute attribute, DBColumn referenceColumn) {
        Entity entity = attribute.getConnectedEntity();
        Id id = (Id) referenceColumn.getAttribute();
        if (entity.getAttributes().getId().size() <= 1) {
            return attribute.getName() + "_" + id.getColumnName().toUpperCase();
        } else {
            return id.getColumnName().toUpperCase();
        }
    }

    @Override
    protected void updateName(String name) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumn();
        column.setName(name);
        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n)
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        JoinColumn joinColumn = this.getBaseElementSpec().getJoinColumn();
        set.createPropertySet("INVERSE_JOIN_COLUMN", this, joinColumn, getPropertyChangeListeners());
        set.createPropertySet("FOREIGN_KEY", this, joinColumn.getForeignKey() , null);
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        if (this.getTableWidget() instanceof BaseTableWidget) {
            JMenuItem joinTable = new JMenuItem("Create Join Table");//, MICRO_DB);
            joinTable.addActionListener((ActionEvent e) -> {
                String joinTableName = JOptionPane.showInputDialog((Component) InverseJoinColumnWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter join table name");
                convertToJoinTable(joinTableName);
                ModelerFile parentFile = InverseJoinColumnWidget.this.getModelerScene().getModelerFile().getParentFile();
                JPAModelerUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
            });
            menuList.add(0, joinTable);
        }
        return menuList;
    }

    void convertToJoinTable(String name) {
        DBInverseJoinColumn inverseJoinColumnSpec = (DBInverseJoinColumn) this.getBaseElementSpec();
        inverseJoinColumnSpec.getJoinColumns().removeIf(c -> true);
        inverseJoinColumnSpec.getAttribute().getJoinTable().setName(name);
    }
}
