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
package io.github.jeddict.db.modeler.widget;

import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.db.modeler.ddl.RenameTableDDL;
import io.github.jeddict.db.modeler.spec.DBColumn;
import io.github.jeddict.db.modeler.spec.DBTable;
import io.github.jeddict.db.modeler.initializer.DBModelerScene;
import io.github.jeddict.db.modeler.spec.DBSchema;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.BASE_TABLE;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.BASE_TABLE_ICON_PATH;
import io.github.jeddict.relation.mapper.widget.api.IPrimaryKeyWidget;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JMenuItem;
import io.github.jeddict.util.StringUtils;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import static io.github.jeddict.jpa.modeler.rules.entity.ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD;
import static io.github.jeddict.jpa.modeler.rules.entity.ClassValidator.NON_UNIQUE_TABLE_NAME;
import io.github.jeddict.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.action.ViewDataAction;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.openide.nodes.Node;

public class TableWidget extends FlowNodeWidget<DBTable, DBModelerScene> {

    private final Map<String, BasicColumnWidget> columnWidgets = new HashMap<>();
    private final Map<String, ForeignKeyWidget> foreignKeyWidgets = new HashMap<>();
    private final Map<String, PrimaryKeyWidget> primaryKeyWidgets = new HashMap<>();

    public TableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.setName(((DBTable) node.getBaseElementSpec()).getName());
        this.setImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
        this.getImageWidget().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.getImageWidget().getActions().addAction(new TableAction());
        getNodeNameWidget().getActions().removeAction(editAction);
    }

    @Override
    public void setName(String name) {
        String oldName = this.name;
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                ModelerFile file = this.getModelerScene().getModelerFile();
                Node node = (Node) file.getAttribute(Node.class.getSimpleName());
                DatabaseConnection connection = node.getLookup().lookup(DatabaseConnection.class);
                Schema schema = (Schema) file.getAttribute(Schema.class.getSimpleName());
                Specification spec = connection.getConnector().getDatabaseSpecification();
                new RenameTableDDL(spec, schema.getName(), oldName, name).execute();
            }
        } else {
            setLabel(this.name);
        }
        validateName(this.name);
    }

    private void validateName(String name) {
        if (SQLKeywords.isSQL99ReservedKeyword(name)) {
            this.getSignalManager().fire(ERROR, CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getSignalManager().clear(ERROR, CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBSchema schema = this.getModelerScene().getBaseElementSpec();
        if (schema.findAllTable(name).size() > 1) {
            getSignalManager().fire(ERROR, NON_UNIQUE_TABLE_NAME);
        } else {
            getSignalManager().clear(ERROR, NON_UNIQUE_TABLE_NAME);
        }
    }

    @Override
    public String getIconPath() {
        return BASE_TABLE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return BASE_TABLE;
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuItemList = new LinkedList<>();
        menuItemList.add(getPropertyMenu());
        return menuItemList;
    }

    private final class TableAction extends WidgetAction.Adapter {

        @Override
        public WidgetAction.State mousePressed(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
                TableWidget tableWidget = TableWidget.this;
                ModelerFile file = tableWidget.getModelerScene().getModelerFile();
                Node tablesNode = (Node) file.getAttribute(Node.class.getSimpleName());

                Optional<Node> nodeOptional = Arrays.stream(tablesNode.getChildren().getNodes())
                        .filter(tableNode -> tableNode.getName().equalsIgnoreCase(tableWidget.getName()))
                        .findFirst();
                if (nodeOptional.isPresent()) {
                    ViewDataAction viewDataAction = new ViewDataAction();
                    viewDataAction.performAction(new Node[]{nodeOptional.get()});
                    return WidgetAction.State.CONSUMED;
                }
            }
            return WidgetAction.State.REJECTED;
        }
    }

    public void addBasicColumn(String name, DBColumn column) {
        columnWidgets.put(column.getId(), createPinWidget(column, BasicColumnWidget.class, w -> new BasicColumnWidget(this.getModelerScene(), this, w)));
    }

    public void addPrimaryKeyColumn(String name, DBColumn column) {
        primaryKeyWidgets.put(column.getId(), createPinWidget(column, PrimaryKeyWidget.class, w -> new PrimaryKeyWidget(this.getModelerScene(), this, w)));
    }

    public void addForeignKeyColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), createPinWidget(column, ForeignKeyWidget.class, w -> new ForeignKeyWidget(this.getModelerScene(), this, w)));
    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setNodeName(label.replaceAll("\\s+", ""));
        }
    }

    /**
     * @return the columnWidgets
     */
    public BasicColumnWidget getColumnWidget(String id) {
        return columnWidgets.get(id);
    }

    /**
     * @return the columnWidgets
     */
    public Collection<BasicColumnWidget> getColumnWidgets() {
        return columnWidgets.values();
    }

    public PrimaryKeyWidget getPrimaryKeyWidget(String id) {
        return primaryKeyWidgets.get(id);
    }

    /**
     * @return the primaryKeyWidgets
     */
    public Collection<PrimaryKeyWidget> getPrimaryKeyWidgets() {
        return primaryKeyWidgets.values();
    }

    /**
     * @return the referenceColumnWidgets
     */
    public ForeignKeyWidget getForeignKeyWidget(String id) {
        return foreignKeyWidgets.get(id);
    }

    /**
     * @return the referenceColumnWidgets
     */
    public Collection<ForeignKeyWidget> getForeignKeyWidgets() {
        return foreignKeyWidgets.values();
    }

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return null;
    }

    public ColumnWidget findColumnWidget(String id) {
        ColumnWidget columnWidget = primaryKeyWidgets.get(id);
        if (columnWidget == null) {
            columnWidget = foreignKeyWidgets.get(id);
        }
        if (columnWidget == null) {
            columnWidget = columnWidgets.get(id);
        }
        return columnWidget;
    }

    public void removeColumnWidget(ColumnWidget key) {
        if (key instanceof ForeignKeyWidget) {
            foreignKeyWidgets.remove(key.getId());
        } else if (key instanceof IPrimaryKeyWidget) {
            primaryKeyWidgets.remove(key.getId());
        } else {
            columnWidgets.remove(key.getId());
        }
    }

    public void removeForeignKeyWidget(ForeignKeyWidget key) {
        foreignKeyWidgets.remove(key.getId());
    }

    public void removePrimaryKeyWidget(IPrimaryKeyWidget key) {
        primaryKeyWidgets.remove(key.getId());
    }

    @Override
    public void createPinWidget(SubCategoryNodeConfig subCategoryInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
