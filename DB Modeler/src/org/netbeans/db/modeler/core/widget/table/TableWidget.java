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
package org.netbeans.db.modeler.core.widget.table;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.swing.JMenuItem;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.db.modeler.core.widget.column.BasicColumnWidget;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.core.widget.column.DiscriminatorColumnWidget;
import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.core.widget.column.IReferenceColumnWidget;
import org.netbeans.db.modeler.core.widget.column.IPrimaryKeyWidget;
import org.netbeans.db.modeler.core.widget.column.InverseJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.JoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.PrimaryKeyJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.PrimaryKeyWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAssociationInverseJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAssociationJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAttributeColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAttributeJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.map.MapKeyColumnWidget;
import org.netbeans.db.modeler.core.widget.column.map.MapKeyEmbeddedColumnWidget;
import org.netbeans.db.modeler.core.widget.column.map.MapKeyJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAssociationInverseJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAssociationJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAttributeColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAttributePrimaryKeyWidget;
import org.netbeans.db.modeler.properties.PropertiesHandler;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBMapKeyColumn;
import org.netbeans.db.modeler.spec.DBMapKeyEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBMapKeyJoinColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.SQLEditorUtil;
import org.netbeans.jpa.modeler.core.widget.*;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public abstract class TableWidget<E extends DBTable> extends FlowNodeWidget<E, DBModelerScene> {

    private final Map<String, ColumnWidget> columnWidgets = new HashMap<>();
    private final Map<String, ForeignKeyWidget> foreignKeyWidgets = new HashMap<>();//ForeignKey Column
    private final Map<String, IPrimaryKeyWidget> primaryKeyWidgets = new HashMap<>();//PrimaryKey Column

    public TableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.setName(((DBTable)node.getBaseElementSpec()).getName()); 
        this.setImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
        this.getImageWidget().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.getImageWidget().getActions().addAction(new TableAction());

    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
           set.put("BASIC_PROP", PropertiesHandler.getIndexProperties(this));
           set.put("BASIC_PROP", PropertiesHandler.getUniqueConstraintProperties(this));
    }
         

    private final class TableAction extends WidgetAction.Adapter {

        @Override
        public WidgetAction.State mousePressed(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1  && event.getClickCount()==2) {
                SQLEditorUtil.openDBTable(TableWidget.this);
                return WidgetAction.State.CONSUMED;
            }
            return WidgetAction.State.REJECTED;
        }
    }

    public void addNewBasicColumn(String name, DBColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, BasicColumnWidget.class));
    }

    public void addEmbeddedAttributeColumn(String name, DBColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, EmbeddedAttributeColumnWidget.class));
    }

    public void addParentPrimaryKeyAttributeColumn(String name, DBColumn column) {
        primaryKeyWidgets.put(column.getId(), create(column.getId(), name, column, ParentAttributePrimaryKeyWidget.class));
    }
    
     public void addMapKeyColumn(String name, DBMapKeyColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, MapKeyColumnWidget.class));
    }
     
     public void addMapKeyJoinColumn(String name, DBMapKeyJoinColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, MapKeyJoinColumnWidget.class));
    }
     
    public void addMapKeyEmbeddedColumn(String name, DBMapKeyEmbeddedColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, MapKeyEmbeddedColumnWidget.class));
    }

    public void addDiscriminatorColumn(String name, DBColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, DiscriminatorColumnWidget.class));
    }

    public void addParentAttributeColumn(String name, DBColumn column) {
        columnWidgets.put(column.getId(), create(column.getId(), name, column, ParentAttributeColumnWidget.class));
    }

    public void addParentAssociationInverseJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, ParentAssociationInverseJoinColumnWidget.class));
    }

    public void addParentAssociationJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, ParentAssociationJoinColumnWidget.class));
    }

    public void addEmbeddedAttributeJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, EmbeddedAttributeJoinColumnWidget.class));
    }

    public void addEmbeddedAssociationInverseJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, EmbeddedAssociationInverseJoinColumnWidget.class));
    }

    public void addEmbeddedAssociationJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, EmbeddedAssociationJoinColumnWidget.class));
    }

    public void addNewJoinKey(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, JoinColumnWidget.class));
    }

    public void addNewInverseJoinKey(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, InverseJoinColumnWidget.class));
    }

    public void addNewPrimaryKeyJoinColumn(String name, DBColumn column) {
        foreignKeyWidgets.put(column.getId(), create(column.getId(), name, column, PrimaryKeyJoinColumnWidget.class));
    }

    public void addNewPrimaryKey(String name, DBColumn column) {
        primaryKeyWidgets.put(column.getId(), create(column.getId(), name, column, PrimaryKeyWidget.class));
    }
    
    
    private <W extends ColumnWidget> W create(String id, String name, IBaseElement baseElement, Class<W> widgetClass) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(widgetClass.getSimpleName());
        W widget = (W)createPinWidget(pinWidgetInfo);
        return widget;
    }

//    public abstract void deleteAttribute(AttributeWidget attributeWidget);
    // method should be called only onec in case of loadDocument
    public void sortAttributes() {
        Map<String, List<Widget>> categories = new LinkedHashMap<>();

        if (!primaryKeyWidgets.isEmpty()) {
            List<Widget> primaryKeyCatWidget = new ArrayList<>();
            primaryKeyWidgets.values().stream().forEach((primaryKeyWidget) -> {
                primaryKeyCatWidget.add((Widget) primaryKeyWidget);
            });
            categories.put("Primary Key", primaryKeyCatWidget);
        }

//        List<Widget> foreignKeyCatWidget = new ArrayList<>();
//        List<Widget> derivedIdentiyCatWidget = new ArrayList<>();

//        if (!foreignKeyWidgets.isEmpty()) {
//            foreignKeyWidgets.values().stream()
//                    .forEach((foreignKeyWidget) -> {
//                        if (((DBColumn) foreignKeyWidget.getBaseElementSpec()).isPrimaryKey()) {
//                            derivedIdentiyCatWidget.add(foreignKeyWidget);
//                        } else {
//                            foreignKeyCatWidget.add(foreignKeyWidget);
//                        }
//                    });
//        }
//        if (!derivedIdentiyCatWidget.isEmpty()) {
//                categories.put("Derived Identity", derivedIdentiyCatWidget);
//            }
//        if (!foreignKeyCatWidget.isEmpty()) {
//            categories.put("Foreign Key", foreignKeyCatWidget);
//        }
        
        if (!foreignKeyWidgets.isEmpty()) {
            categories.put("Foreign Key", new ArrayList<>(foreignKeyWidgets.values()));
        }
        
        if (!columnWidgets.isEmpty()) {
            categories.put("Basic", new ArrayList<>(columnWidgets.values()));
        }


        this.sortPins(categories);
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
    public ColumnWidget getColumnWidget(String id) {
        return columnWidgets.get(id);
    }

    /**
     * @return the referenceColumnWidgets
     */
    public ForeignKeyWidget getForeignKeyWidget(String id) {
        return foreignKeyWidgets.get(id);
    }

    /**
     * @return the columnWidgets
     */
    public Collection<ColumnWidget> getColumnWidgets() {
        return columnWidgets.values();
    }

    /**
     * @return the referenceColumnWidgets
     */
    public Collection<ForeignKeyWidget> getForeignKeyWidgets() {
        return foreignKeyWidgets.values();
    }

    /**
     * @return the primaryKeyWidgets
     */
    public IPrimaryKeyWidget getPrimaryKeyWidget(String id) {
        return primaryKeyWidgets.get(id);
    }

    public IReferenceColumnWidget findColumnWidget(String id) {
        IReferenceColumnWidget columnWidget = primaryKeyWidgets.get(id);
        if (columnWidget == null) {
            columnWidget = foreignKeyWidgets.get(id);
        }

        return columnWidget;
    }

    /**
     * @return the primaryKeyWidgets
     */
    public Collection<IPrimaryKeyWidget> getPrimaryKeyWidgets() {
        return primaryKeyWidgets.values();
    }
    
    public Collection<ColumnWidget> getPrimaryKeyColumnWidgets() {
        return getPrimaryKeyWidgets().stream()
                    .map(pkw -> (ColumnWidget)pkw)
                    .collect(toList());
    }

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return null;
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuItemList = new LinkedList<>();

        JMenuItem drive = new JMenuItem("Drive to Entity");
        drive.addActionListener((ActionEvent e) -> {
            DBTable table = TableWidget.this.getBaseElementSpec();
            Entity entity = table.getEntity();
            ModelerFile modelerFile = TableWidget.this.getModelerScene().getModelerFile();
            ModelerFile parentModelerFile = modelerFile.getParentFile();
            IModelerScene<IRootElement> parentScene = parentModelerFile.getModelerScene();
            INodeWidget widget = (INodeWidget) parentScene.getBaseElements()
                    .stream()
                    .filter(w -> w.getBaseElementSpec() == entity)
                    .findAny()
                    .get();
            JPAFileActionListener.open(parentModelerFile);
            parentModelerFile.getModelerDiagramEngine().moveToWidget(widget);

        });

        menuItemList.add(drive);
        menuItemList.add(getPropertyMenu());

        return menuItemList;
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
