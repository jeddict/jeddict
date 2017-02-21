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

import org.netbeans.db.modeler.core.widget.table.BaseTableWidget;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.db.modeler.specification.model.util.ColumnUtil;
import org.netbeans.db.modeler.spec.DBCollectionTable;
import org.netbeans.db.modeler.spec.DBJoinColumn;
import org.netbeans.db.modeler.spec.DBRelationTable;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class JoinColumnWidget<E extends DBJoinColumn> extends ForeignKeyWidget<E> {

    public JoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("JoinColumn_name", (PropertyChangeListener<String>) (String value) -> {
            setPropertyName(value);
        });
    }

    @Override
    protected void updateName(String name) {
        JoinColumn column = this.getBaseElementSpec().getJoinColumn();
        column.setName(name);
        ColumnUtil.syncronizeCompositeKeyJoincolumn(this.getReferenceFlowWidget().get(0).getReferenceColumnWidget().getTableWidget(), this.getTableWidget());//TODO get(n) // TODO applicable here ?
    }

    //         BI-DIRECTIONAL              RelationTable                      CollectionTable
    // 1PK     True                        ConAttrName_IdColName
    // 1PK     False                       Entity_IdColName                   Entity_IdColName
    // nPK     True                        ConAttrName_IdColName              nIdColName
    // nPK     False                       Entity_IdColName                   nIdColName
    @Override
    protected boolean prePersistName() {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof OneToMany && !this.getBaseElementSpec().isRelationTableExist()) {
            return false;//OneToMany by default creates JoinTable
        }
        return true;
    }

    @Override
    protected String evaluateName() {
        DBTable table = (DBTable) this.getTableWidget().getBaseElementSpec();
        Id id = (Id) this.getBaseElementSpec().getReferenceColumn().getAttribute();
        return evaluateName(table, id);
    }

    public static String evaluateName(DBTable table, Id id) {
        Entity entity = table.getEntity();
        if (entity.getAttributes().getId().size() <= 1) {
            if (table instanceof DBRelationTable) {
                if (((DBRelationTable) table).getAttribute().getConnectedAttribute() != null) {
                    return ((DBRelationTable) table).getAttribute().getConnectedAttribute().getName() + "_" + id.getColumnName().toUpperCase();
                } else {
                    return entity.getClazz() + "_" + id.getColumnName().toUpperCase();
                }
            } else if (table instanceof DBCollectionTable) {
                return entity.getClazz() + "_" + id.getColumnName().toUpperCase();
            }
        } else {
            return id.getColumnName().toUpperCase();
//            if (table instanceof DBRelationTable) {
//                if (((DBRelationTable) table).getAttribute().getConnectedAttribute() != null) {
//                    return id.getColumnName().toUpperCase();
//                } else {
//                    return id.getColumnName().toUpperCase();
//                }
//            } else if (table instanceof DBCollectionTable) {
//                return id.getColumnName().toUpperCase();
//            }
        }
        return null;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        JoinColumn joinColumn = this.getBaseElementSpec().getJoinColumn();
        set.createPropertySet("JOIN_COLUMN", this, joinColumn, getPropertyChangeListeners());
        set.createPropertySet("FOREIGN_KEY", this, joinColumn.getForeignKey() , null);
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        if (this.getTableWidget() instanceof BaseTableWidget) {
            JMenuItem joinTable = new JMenuItem("Create Join Table");//, MICRO_DB);
            joinTable.addActionListener((ActionEvent e) -> {
                String joinTableName = JOptionPane.showInputDialog((Component) JoinColumnWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter join table name");
                convertToJoinTable(joinTableName);
                ModelerFile parentFile = JoinColumnWidget.this.getModelerScene().getModelerFile().getParentFile();
                DBUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
            });
            menuList.add(0, joinTable);
        }
        return menuList;
    }

    private void convertToJoinTable(String name) {
        DBJoinColumn joinColumn = this.getBaseElementSpec();
        if (joinColumn.getAttribute() instanceof RelationAttribute) {
            joinColumn.getJoinColumns().clear();
            ((RelationAttribute) joinColumn.getAttribute()).getJoinTable().setName(name);
        }
    }
}
