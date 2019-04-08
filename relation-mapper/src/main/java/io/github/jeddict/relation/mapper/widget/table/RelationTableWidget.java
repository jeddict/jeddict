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
package io.github.jeddict.relation.mapper.widget.table;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import io.github.jeddict.util.StringUtils;
import io.github.jeddict.relation.mapper.spec.DBMapping;
import io.github.jeddict.relation.mapper.spec.DBRelationTable;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.RELATION_TABLE;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.RELATION_TABLE_ICON_PATH;
import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.modeler.rules.entity.ClassValidator;
import io.github.jeddict.jpa.modeler.rules.entity.SQLKeywords;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.JoinTable;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.JoinColumnHandler;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.modeler.initializer.DBUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class RelationTableWidget extends TableWidget<DBRelationTable> {

    public RelationTableWidget(RelationMapperScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("JoinTable_name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setName(value);
            setLabel(name);
        });
    }

    private void setDefaultName() {
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
        this.name = getDefaultJoinTableName();
        attribute.getJoinTable().setName(null);
        setLabel(name);//setLabel escaped by inplace editor in case of blank value
    }

    @Override
    public void setName(String name) {

        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
                attribute.getJoinTable().setName(this.name);
            }

        } else {
            setDefaultName();
        }
        if (SQLKeywords.isSQL99ReservedKeyword(RelationTableWidget.this.getName())) {
            this.getSignalManager().fire(ERROR, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getSignalManager().clear(ERROR, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBMapping mapping = RelationTableWidget.this.getModelerScene().getBaseElementSpec();
        if (mapping.findAllTable(RelationTableWidget.this.getName()).size() > 1) {
            getSignalManager().fire(ERROR, ClassValidator.NON_UNIQUE_TABLE_NAME);
        } else {
            getSignalManager().clear(ERROR, ClassValidator.NON_UNIQUE_TABLE_NAME);
        }

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
        JoinTable joinTable = attribute.getJoinTable();
        set.createPropertySet(this, joinTable, getPropertyChangeListeners());
        set.createPropertySet("FOREIGN_KEY", this, joinTable.getForeignKey() , null);
        set.createPropertySet("INVERSE_FOREIGN_KEY", this, joinTable.getInverseForeignKey(), null);
    }

    private String getDefaultJoinTableName() {
        Entity entity = this.getBaseElementSpec().getEntity();
        RelationAttribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute.isOwner()) {
            return entity.getTableName().toUpperCase() + "_" + attribute.getConnectedEntity().getTableName().toUpperCase();
        } else {
            return attribute.getConnectedEntity().getTableName().toUpperCase() + "_" + entity.getTableName().toUpperCase();
        }
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        DBRelationTable relationTable = this.getBaseElementSpec();
        if (relationTable.getAttribute() instanceof JoinColumnHandler) {
            JMenuItem joinTable = new JMenuItem("Delete Join Table");
            joinTable.addActionListener((ActionEvent e) -> {
                convertToJoinColumn();
                ModelerFile parentFile = RelationTableWidget.this.getModelerScene().getModelerFile().getParentFile();
                DBUtil.openDBModeler(parentFile);
                JeddictLogger.recordDBAction("Delete Join Table");
            });
            menuList.add(0, joinTable);
        }
        return menuList;
    }

    private void convertToJoinColumn() {
        DBRelationTable relationTable = this.getBaseElementSpec();
        relationTable.getAttribute().getJoinTable().clear();
        if (relationTable.getAttribute() instanceof OneToMany) {
            String joinColumnName = JOptionPane.showInputDialog((Component) RelationTableWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter join column name (required) :");
            ((JoinColumnHandler) relationTable.getAttribute()).getJoinColumn().clear();
            JoinColumn joinColumn = new JoinColumn();
            joinColumn.setName(joinColumnName);
            ((JoinColumnHandler) relationTable.getAttribute()).addJoinColumn(joinColumn);
        }

    }

    @Override
    public String getIconPath() {
        return RELATION_TABLE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return RELATION_TABLE;
    }

}
