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
package io.github.jeddict.relation.mapper.widget.column;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import io.github.jeddict.relation.mapper.widget.table.BaseTableWidget;
import io.github.jeddict.relation.mapper.spec.DBCollectionTable;
import io.github.jeddict.relation.mapper.spec.DBJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBRelationTable;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.relation.mapper.initializer.ColumnUtil;
import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.modeler.initializer.DBUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class JoinColumnWidget<E extends DBJoinColumn> extends ForeignKeyWidget<E> {

    public JoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("JoinColumn_name", (PropertyChangeListener<String>) (oldValue, value) -> setPropertyName(value));
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
                DBUtil.openDBModeler(parentFile);
            });
            menuList.add(0, joinTable);
            JeddictLogger.recordDBAction("Create Join Table");
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
