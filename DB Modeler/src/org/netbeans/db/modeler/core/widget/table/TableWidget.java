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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAssociationColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAssociationInverseJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAssociationJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAttributeColumnWidget;
import org.netbeans.db.modeler.core.widget.column.embedded.EmbeddedAttributeJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAssociationColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAssociationInverseJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAssociationJoinColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAttributeColumnWidget;
import org.netbeans.db.modeler.core.widget.column.parent.ParentAttributePrimaryKeyWidget;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.SQLEditorUtil;
import org.netbeans.jpa.modeler.core.widget.*;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import static org.netbeans.modeler.core.engine.ModelerDiagramEngine.NODE_WIDGET_SELECT_PROVIDER;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

public abstract class TableWidget<E extends DBTable> extends FlowNodeWidget<E, DBModelerScene> {

    private final Map<String, ColumnWidget> columnWidgets = new HashMap<>();
    private final Map<String, ForeignKeyWidget> foreignKeyWidgets = new HashMap<>();//ForeignKey Column
    private final Map<String, IPrimaryKeyWidget> primaryKeyWidgets = new HashMap<>();//PrimaryKey Column

    public TableWidget(DBModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.setImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
        this.getImageWidget().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.getImageWidget().getActions().addAction(new TableAction());

    }

    private final class TableAction extends WidgetAction.Adapter {

        @Override
        public WidgetAction.State mousePressed(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1 || event.getButton() == MouseEvent.BUTTON2) {
                SQLEditorUtil.openDBTable(TableWidget.this);
                return WidgetAction.State.CONSUMED;
            }
            return WidgetAction.State.REJECTED;
        }
    }

    public ColumnWidget addNewBasicColumn(String name, DBColumn column) {
//        E table = this.getBaseElementSpec();
//        if (column == null) {
//            column = new DBColumn();
//            column.setId(NBModelerUtil.getAutoGeneratedStringId());
//            column.setName(name);
//            table.addColumn(column);
//        }

        BasicColumnWidget widget = (BasicColumnWidget) createPinWidget(BasicColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        columnWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addEmbeddedAttributeColumn(String name, DBColumn column) {
        EmbeddedAttributeColumnWidget widget = (EmbeddedAttributeColumnWidget) createPinWidget(EmbeddedAttributeColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        columnWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addParentPrimaryKeyAttributeColumn(String name, DBColumn column) {
        ParentAttributePrimaryKeyWidget widget = (ParentAttributePrimaryKeyWidget) createPinWidget(ParentAttributePrimaryKeyWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        primaryKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addDiscriminatorColumn(String name, DBColumn column) {
        DiscriminatorColumnWidget widget = (DiscriminatorColumnWidget) createPinWidget(DiscriminatorColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        columnWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addParentAttributeColumn(String name, DBColumn column) {
        ParentAttributeColumnWidget widget = (ParentAttributeColumnWidget) createPinWidget(ParentAttributeColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        columnWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addParentAssociationInverseJoinColumn(String name, DBColumn column) {
        ParentAssociationColumnWidget widget = (ParentAssociationColumnWidget) createPinWidget(ParentAssociationInverseJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addParentAssociationJoinColumn(String name, DBColumn column) {
        ParentAssociationColumnWidget widget = (ParentAssociationColumnWidget) createPinWidget(ParentAssociationJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addEmbeddedAttributeJoinColumn(String name, DBColumn column) {
        EmbeddedAttributeJoinColumnWidget widget = (EmbeddedAttributeJoinColumnWidget) createPinWidget(EmbeddedAttributeJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addEmbeddedAssociationInverseJoinColumn(String name, DBColumn column) {
        EmbeddedAssociationColumnWidget widget = (EmbeddedAssociationColumnWidget) createPinWidget(EmbeddedAssociationInverseJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addEmbeddedAssociationJoinColumn(String name, DBColumn column) {
        EmbeddedAssociationColumnWidget widget = (EmbeddedAssociationColumnWidget) createPinWidget(EmbeddedAssociationJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addNewJoinKey(String name, DBColumn column) {
//        E table = this.getBaseElementSpec();
//        if (column == null) {
//            column = new DBColumn();
//            column.setId(NBModelerUtil.getAutoGeneratedStringId());
//            column.setName(name);
//            table.addColumn(column);
//        }

        JoinColumnWidget widget = (JoinColumnWidget) createPinWidget(JoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addNewInverseJoinKey(String name, DBColumn column) {
//        E table = this.getBaseElementSpec();
//        if (column == null) {
//            column = new DBColumn();
//            column.setId(NBModelerUtil.getAutoGeneratedStringId());
//            column.setName(name);
//            table.addColumn(column);
//        }

        InverseJoinColumnWidget widget = (InverseJoinColumnWidget) createPinWidget(InverseJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addNewPrimaryKeyJoinColumn(String name, DBColumn column) {
        PrimaryKeyJoinColumnWidget widget = (PrimaryKeyJoinColumnWidget) createPinWidget(PrimaryKeyJoinColumnWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        foreignKeyWidgets.put(column.getId(), widget);
        return widget;
    }

    public ColumnWidget addNewPrimaryKey(String name, DBColumn column) {
//        E table = this.getBaseElementSpec();
//        if (column == null) {
//            column = new DBColumn();
//            column.setId(NBModelerUtil.getAutoGeneratedStringId());
//            column.setName(name);
//            table.addColumn(column);
//        }

        PrimaryKeyWidget widget = (PrimaryKeyWidget) createPinWidget(PrimaryKeyWidget.create(column.getId(), name, column));
        widget.setDatatypeTooltip();
        primaryKeyWidgets.put(column.getId(), widget);

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

        List<Widget> foreignKeyCatWidget = new ArrayList<>();
        if (!foreignKeyWidgets.isEmpty()) {
            List<Widget> derivedIdentiyCatWidget = new ArrayList<>();
            foreignKeyWidgets.values().stream().forEach((foreignKeyWidget) -> {
                if (((DBColumn) foreignKeyWidget.getBaseElementSpec()).isPrimaryKey()) {
                    derivedIdentiyCatWidget.add(foreignKeyWidget);
                } else {
                    foreignKeyCatWidget.add(foreignKeyWidget);
                }
            });
            if (!derivedIdentiyCatWidget.isEmpty()) {
                categories.put("Derived Identity", derivedIdentiyCatWidget);
            }
        }

        if (!columnWidgets.isEmpty()) {
            List<Widget> columnCatWidget = new ArrayList<>();
            columnWidgets.values().stream().forEach((columnWidget) -> {
                columnCatWidget.add(columnWidget);
            });
            categories.put("Basic", columnCatWidget);
        }

        if (!foreignKeyCatWidget.isEmpty()) {
            categories.put("Foreign Key", foreignKeyCatWidget);
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

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return null;
    }

//        private void moveVisibleRect (IModelerScene modelerScene, Point center) {
//        JComponent component = modelerScene.getView ();
//        if (component == null)
//            return;
//        double zoomFactor = modelerScene.getZoomFactor ();
//        Rectangle bounds = modelerScene.getBounds ();
//        Dimension size = getSize ();
//
//        double sx = bounds.width > 0 ? (double) size.width / bounds.width : 0.0;
//        double sy = bounds.width > 0 ? (double) size.height / bounds.height : 0.0;
//        double scale = Math.min (sx, sy);
//
//        int vw = (int) (scale * bounds.width);
//        int vh = (int) (scale * bounds.height);
//        int vx = (size.width - vw) / 2;
//        int vy = (size.height - vh) / 2;
//
//        int cx = (int) ((double) (center.x - vx) / scale * zoomFactor);
//        int cy = (int) ((double) (center.y - vy) / scale * zoomFactor);
//
//        Rectangle visibleRect = component.getVisibleRect ();
//        visibleRect.x = cx - visibleRect.width / 2;
//        visibleRect.y = cy - visibleRect.height / 2;
//        component.scrollRectToVisible (visibleRect);
//
//    }
    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuItemList = new LinkedList<>();

        JMenuItem drive = new JMenuItem("Drive to Entity");
        drive.addActionListener((ActionEvent e) -> {
            DBTable table = TableWidget.this.getBaseElementSpec();
            Entity entity = table.getEntity();
            ModelerFile modelerFile = TableWidget.this.getModelerScene().getModelerFile();
            modelerFile = modelerFile.getParentFile();

            Widget widget = (Widget) ((JPAModelerScene) modelerFile.getModelerScene()).getBaseElements().stream().filter(w -> w.getBaseElementSpec() == entity).findAny().get();
            modelerFile.getModelerScene().setFocusedWidget(widget);

            Rectangle visibleRect = modelerFile.getModelerScene().getView().getVisibleRect();
            Rectangle widetRec = new Rectangle(widget.getLocation());
            Rectangle sceneRec = widget.getScene().getBounds();

            int x = 0, y = 0;
            if (widetRec.y + visibleRect.height / 2 > sceneRec.height && widetRec.y + visibleRect.height / 2 < sceneRec.height) {
//                System.out.println("Center Vertcal");
                y = widetRec.y - visibleRect.height / 2;
            } else if (widetRec.y + visibleRect.height / 2 > sceneRec.height) {
//                System.out.println("Bottom");
                y = sceneRec.height;
            } else if (widetRec.y + visibleRect.height / 2 < sceneRec.height) {
//                System.out.println("Top");
                y = 0;
            }

            if (widetRec.x + visibleRect.width / 2 > sceneRec.width && widetRec.x + visibleRect.width / 2 < sceneRec.width) {
//                System.out.println("Center Horizontal");
                x = widetRec.x - visibleRect.width / 2;
            } else if (widetRec.x + visibleRect.width / 2 > sceneRec.width) {
//                System.out.println("Right");
                x = sceneRec.width;
            } else if (widetRec.x + visibleRect.width / 2 < sceneRec.width) {
//                System.out.println("Left");
                x = 0;
            }

            NODE_WIDGET_SELECT_PROVIDER.select(widget, null, false);
            modelerFile.getModelerScene().getView().scrollRectToVisible(new Rectangle(x, y, widget.getBounds().width, widget.getBounds().height));
            JPAFileActionListener.open(modelerFile);

        });

//        menuItemList.add(drive);
        menuItemList.add(getPropertyMenu());

        return menuItemList;
    }
    
        @Override
    public void createPinWidget(SubCategoryNodeConfig subCategoryInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
