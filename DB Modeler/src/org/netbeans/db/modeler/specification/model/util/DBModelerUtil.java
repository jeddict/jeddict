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
package org.netbeans.db.modeler.specification.model.util;

import org.netbeans.db.modeler.exception.DBConnectionNotFound;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor.Mode;
import org.netbeans.db.modeler.spec.DBEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.tools.schemaframework.JPAMSchemaManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.db.modeler.classloader.DynamicDriverClassLoader;
import org.netbeans.db.modeler.core.widget.column.BasicColumnWidget;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.core.widget.column.DiscriminatorColumnWidget;
import org.netbeans.db.modeler.core.widget.column.ForeignKeyWidget;
import org.netbeans.db.modeler.core.widget.column.IReferenceColumnWidget;
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
import org.netbeans.db.modeler.core.widget.flow.ReferenceFlowWidget;
import org.netbeans.db.modeler.core.widget.table.BaseTableWidget;
import org.netbeans.db.modeler.core.widget.table.CollectionTableWidget;
import org.netbeans.db.modeler.core.widget.table.RelationTableWidget;
import org.netbeans.db.modeler.core.widget.table.SecondaryTableWidget;
import org.netbeans.db.modeler.core.widget.table.TableWidget;
import org.netbeans.db.modeler.persistence.internal.jpa.deployment.JPAMPersistenceUnitProcessor;
import org.netbeans.db.modeler.persistence.internal.jpa.metadata.JPAMMetadataProcessor;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBDiscriminatorColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBJoinColumn;
import org.netbeans.db.modeler.spec.DBMapKeyColumn;
import org.netbeans.db.modeler.spec.DBMapKeyEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBMapKeyJoinColumn;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.spec.DBParentAssociationColumn;
import org.netbeans.db.modeler.spec.DBParentAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBParentAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBParentAttributeColumn;
import org.netbeans.db.modeler.spec.DBParentColumn;
import org.netbeans.db.modeler.spec.DBPrimaryKeyJoinColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.cache.DatabaseConnectionCache;
import static org.netbeans.jpa.modeler.spec.extend.cache.DatabaseConnectionCache.DEFAULT_DRIVER;
import static org.netbeans.jpa.modeler.spec.extend.cache.DatabaseConnectionCache.DEFAULT_URL;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.modeler.anchors.CustomRectangularAnchor;
import org.netbeans.modeler.border.ResizeBorder;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.shape.ShapeDesign;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.model.util.PModelerUtil;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.node.NodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modules.db.explorer.ConnectionList;
import org.netbeans.modules.db.explorer.action.ConnectAction;
import org.openide.windows.WindowManager;

public class DBModelerUtil implements PModelerUtil<DBModelerScene> {

    public static String BASE_TABLE_ICON_PATH;
    public static Image BASE_TABLE;
    public static String SECONDARY_TABLE_ICON_PATH;
    public static Image SECONDARY_TABLE;
    public static String COLLECTION_TABLE_ICON_PATH;
    public static Image COLLECTION_TABLE;
    public static String RELATION_TABLE_ICON_PATH;
    public static Image RELATION_TABLE;

    public static String COLUMN_ICON_PATH;
    public static String FOREIGNKEY_ICON_PATH;
    public static String PRIMARYKEY_ICON_PATH;
    public static Image COLUMN;
    public static Image FOREIGNKEY;
    public static Image PRIMARYKEY;
    public static Image TAB_ICON;
    public static ImageIcon RELOAD_ICON;
    public static ImageIcon VIEW_SQL;

    static {// required to load before init
        ClassLoader cl = DBModelerUtil.class.getClassLoader();
        VIEW_SQL = new ImageIcon(cl.getResource("org/netbeans/db/modeler/resource/image/VIEW_SQL.png"));//Eager Loading required
    }

    @Override
    public void init() {
        if (COLUMN == null) {
            ClassLoader cl = DBModelerUtil.class.getClassLoader();

            BASE_TABLE_ICON_PATH = "/org/netbeans/db/modeler/resource/image/TABLE.gif";
            SECONDARY_TABLE_ICON_PATH = "/org/netbeans/db/modeler/resource/image/SECONDARY_TABLE.gif";
            COLLECTION_TABLE_ICON_PATH = "/org/netbeans/db/modeler/resource/image/COLLECTION_TABLE.gif";
            RELATION_TABLE_ICON_PATH = "/org/netbeans/db/modeler/resource/image/JOIN_TABLE.png";
            COLUMN_ICON_PATH = "org/netbeans/db/modeler/resource/image/COLUMN.gif";
            FOREIGNKEY_ICON_PATH = "org/netbeans/db/modeler/resource/image/FOREIGN_KEY.gif";
            PRIMARYKEY_ICON_PATH = "org/netbeans/db/modeler/resource/image/PRIMARY_KEY.gif";
            TAB_ICON = new ImageIcon(cl.getResource("org/netbeans/db/modeler/resource/image/TAB_ICON.png")).getImage();
            RELOAD_ICON = new ImageIcon(cl.getResource("org/netbeans/db/modeler/resource/image/RELOAD.png"));
            BASE_TABLE = new ImageIcon(cl.getResource(BASE_TABLE_ICON_PATH)).getImage();
            SECONDARY_TABLE = new ImageIcon(cl.getResource(SECONDARY_TABLE_ICON_PATH)).getImage();
            COLLECTION_TABLE = new ImageIcon(cl.getResource(COLLECTION_TABLE_ICON_PATH)).getImage();
            RELATION_TABLE = new ImageIcon(cl.getResource(RELATION_TABLE_ICON_PATH)).getImage();
            COLUMN = new ImageIcon(cl.getResource(COLUMN_ICON_PATH)).getImage();
            FOREIGNKEY = new ImageIcon(cl.getResource(FOREIGNKEY_ICON_PATH)).getImage();
            PRIMARYKEY = new ImageIcon(cl.getResource(PRIMARYKEY_ICON_PATH)).getImage();
 
        }

    }

    @Override
    public void loadModelerFile(ModelerFile file) throws org.netbeans.modeler.core.exception.ProcessInterruptedException {
        try {
            loadModelerFileInternal(file);
        } catch (DatabaseException |  ValidationException | DBConnectionNotFound | NoClassDefFoundError ex) {
            ex.printStackTrace();
            DeploymentExceptionManager.handleException(file, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            file.handleException(ex);
            throw new ProcessInterruptedException(ex.getMessage());
        }
    }

    public void loadModelerFileInternal(ModelerFile file) throws DBConnectionNotFound, org.netbeans.modeler.core.exception.ProcessInterruptedException {
        try {
            
            DBModelerScene scene = (DBModelerScene) file.getModelerScene();
            scene.startSceneGeneration();
            
            EntityMappings entityMapping = (EntityMappings) file.getAttributes().get(EntityMappings.class.getSimpleName());
            WorkSpace workSpace = (WorkSpace) file.getAttributes().get(WorkSpace.class.getSimpleName());
            DBMapping dbMapping = createDBMapping(file, entityMapping, workSpace);
            scene.setBaseElementSpec(dbMapping);
            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMapping);

            dbMapping.getTables().stream().forEach(table -> loadTable(scene, table));
            loadFlowEdge(scene);
            scene.autoLayout();

            scene.commitSceneGeneration();


        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private DBMapping createDBMapping(ModelerFile file, EntityMappings entityMapping, WorkSpace workSpace) throws ClassNotFoundException, DBConnectionNotFound {
        DBMapping dbMapping = new DBMapping();
        DatabaseConnectionCache connection = entityMapping.getCache().getDatabaseConnectionCache();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        DatabaseLogin databaseLogin = new DatabaseLogin();

        ClassLoader dynamicClassLoader;
        DatabaseSessionImpl session = null;
        try {

            if (connection == null) {
                dynamicClassLoader = new DynamicDriverClassLoader(file);
                databaseLogin.setDatabaseURL(DEFAULT_URL);
                databaseLogin.setUserName("");
                databaseLogin.setPassword("");
                databaseLogin.setDriverClass(Class.forName(DEFAULT_DRIVER));
            } else {
                for (org.netbeans.modules.db.explorer.DatabaseConnection con : ConnectionList.getDefault().getConnections()) {
                    if (con.getDatabaseConnection().getDriverClass().equals(connection.getDriverClassName())) {
                        new ConnectAction.ConnectionDialogDisplayer().showDialog(con, false);
                        try {
                            connection.setDriverClass(con.getDatabaseConnection().getJDBCDriver().getDriver().getClass());
                            connection.setDatabaseConnection(con.getDatabaseConnection());
                        } catch (org.netbeans.api.db.explorer.DatabaseException ex) {
                            file.handleException(ex);
                        }
                        break;
                    }
                }
                try {
                    dynamicClassLoader = new DynamicDriverClassLoader(file, connection.getDriverClass());
                } catch (NullPointerException ex) {
                    throw new DBConnectionNotFound(ex.getMessage());
                }           
                Thread.currentThread().setContextClassLoader(dynamicClassLoader);
                databaseLogin.setDatabaseURL(connection.getUrl());
                databaseLogin.setUserName(connection.getUserName());
                databaseLogin.setPassword(connection.getPassword());
                databaseLogin.setDriverClass(connection.getDriverClass());
            }
            session = new DatabaseSessionImpl(databaseLogin);
            JPAMMetadataProcessor processor = new JPAMMetadataProcessor(session, dynamicClassLoader, true, false, true, true, false, null, null);
            XMLEntityMappings mapping = new DBEntityMappings(entityMapping, workSpace, dynamicClassLoader);
            JPAMPersistenceUnitProcessor.processORMetadata(mapping, processor, true, Mode.ALL);

            processor.setClassLoader(dynamicClassLoader);
            processor.createDynamicClasses();
            processor.createRestInterfaces();
            processor.addEntityListeners();
            session.getProject().convertClassNamesToClasses(dynamicClassLoader);
            processor.processCustomizers();
            try {
                session.loginAndDetectDatasource();
            } catch (Exception ex) {
                if (ex instanceof org.eclipse.persistence.exceptions.DatabaseException) {
                    throw new DBConnectionNotFound();
                } else {
                    throw ex;
                }
            }
            JPAMSchemaManager mgr = new JPAMSchemaManager(dbMapping, session);
            mgr.createDefaultTables(true);

        } finally {
            if (session != null) {
                session.logout();
            }
            if (connection != null) {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }
        return dbMapping;
    }

    private void loadTable(DBModelerScene scene, IFlowNode flowElement) {
        IModelerDocument document = null;
        ModelerDocumentFactory modelerDocumentFactory = scene.getModelerFile().getModelerDiagramModel().getModelerDocumentFactory();
        if (flowElement instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) flowElement;

            try {
                document = modelerDocumentFactory.getModelerDocument(flowElement);
            } catch (ModelerException ex) {
                scene.getModelerFile().handleException(ex);
            }
//            SubCategoryNodeConfig subCategoryNodeConfig = scene.getModelerFile().getVendorSpecification().getPaletteConfig().findSubCategoryNodeConfig(document);
            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(flowElement.getId(), document, new Point(0, 0));
            nodeWidgetInfo.setName(flowElement.getName());
            nodeWidgetInfo.setExist(Boolean.TRUE);//to Load JPA
            nodeWidgetInfo.setBaseElementSpec(flowElement);//to Load JPA
            INodeWidget nodeWidget = scene.createNodeWidget(nodeWidgetInfo);
            if (flowElement.getName() != null) {
                nodeWidget.setLabel(flowElement.getName());
            }
            if (flowNode.isMinimized()) {
                ((PNodeWidget) nodeWidget).setMinimized(true);
            }
            if (flowElement instanceof DBTable) {
                DBTable table = (DBTable) flowElement;
                table.sortColumns();
                TableWidget tableWidget = (TableWidget) nodeWidget;
                if (table.getColumns() != null) {
                    table.getColumns().stream().forEach((column) -> {
                        if (column instanceof DBDiscriminatorColumn) {
                            tableWidget.addDiscriminatorColumn(column.getName(), column);
                        } else if (column instanceof DBJoinColumn) {
                            tableWidget.addNewJoinKey(column.getName(), column);
                        } else if (column instanceof DBInverseJoinColumn) {
                            tableWidget.addNewInverseJoinKey(column.getName(), column);
                        } else if (column instanceof DBPrimaryKeyJoinColumn) {
                            tableWidget.addNewPrimaryKeyJoinColumn(column.getName(), column);
                        } else if (column instanceof DBEmbeddedColumn) {
                            if (column instanceof DBEmbeddedAttributeColumn) {
                                if (column instanceof DBMapKeyEmbeddedColumn) {
                                    tableWidget.addMapKeyEmbeddedColumn(column.getName(), (DBMapKeyEmbeddedColumn) column);
                                } else {
                                    tableWidget.addEmbeddedAttributeColumn(column.getName(), column);
                                }
                            } else if (column instanceof DBEmbeddedAttributeJoinColumn) {
                                tableWidget.addEmbeddedAttributeJoinColumn(column.getName(), column);
                            } else if (column instanceof DBEmbeddedAssociationColumn) {
                                if (column instanceof DBEmbeddedAssociationInverseJoinColumn) {
                                    tableWidget.addEmbeddedAssociationInverseJoinColumn(column.getName(), column);
                                } else if (column instanceof DBEmbeddedAssociationJoinColumn) {
                                    tableWidget.addEmbeddedAssociationJoinColumn(column.getName(), column);
                                }
                            }
                        } else if (column instanceof DBParentColumn) {
                            if (column instanceof DBParentAttributeColumn) {
                                if (column.isPrimaryKey()) {
                                    tableWidget.addParentPrimaryKeyAttributeColumn(column.getName(), column);
                                } else {
                                    tableWidget.addParentAttributeColumn(column.getName(), column);
                                }
                            } else if (column instanceof DBParentAssociationColumn) {
                                if (column instanceof DBParentAssociationInverseJoinColumn) {
                                    tableWidget.addParentAssociationInverseJoinColumn(column.getName(), column);
                                } else if (column instanceof DBParentAssociationJoinColumn) {
                                    tableWidget.addParentAssociationJoinColumn(column.getName(), column);
                                }
                            }
                        } else if (column instanceof DBMapKeyColumn) {
                            tableWidget.addMapKeyColumn(column.getName(), (DBMapKeyColumn) column);
                        } else if (column instanceof DBMapKeyJoinColumn) {
                            tableWidget.addMapKeyJoinColumn(column.getName(), (DBMapKeyJoinColumn) column);
                        } else if (column.isPrimaryKey()) {
                            tableWidget.addNewPrimaryKey(column.getName(), column);
                        } else {
                            tableWidget.addNewBasicColumn(column.getName(), column);
                        }
                    });
                    tableWidget.sortAttributes();
                }

            }

        }
    }

    private void loadFlowEdge(DBModelerScene scene) {

        scene.getBaseElements().stream().filter((baseElementWidget) -> (baseElementWidget instanceof TableWidget)).forEach((baseElementWidget) -> {
            TableWidget tableWidget = (TableWidget) baseElementWidget;
//tableWidget.getPrimaryKeyWidgets().stream().forEach((foreignKeyWidget) -> {
//                loadEdge(scene, tableWidget, (ForeignKeyWidget) foreignKeyWidget);
//            });
            tableWidget.getForeignKeyWidgets().stream().forEach((foreignKeyWidget) -> {
                loadEdge(scene, tableWidget, (ForeignKeyWidget) foreignKeyWidget);
            });

        });
    }

    private void loadEdge(DBModelerScene scene, TableWidget sourceTableWidget, ForeignKeyWidget foreignKeyWidget) {
//       ForeignKey => Source
//       ReferenceColumn => Target
        DBColumn sourceColumn = (DBColumn) foreignKeyWidget.getBaseElementSpec();
        if (sourceColumn.getReferenceColumn() == null || sourceColumn.getReferenceTable() == null) {// TODO remove this block
            return;
        }
        TableWidget targetTableWidget = (TableWidget) scene.getBaseElement(sourceColumn.getReferenceTable().getId());

        ColumnWidget targetColumnWidget = (ColumnWidget) targetTableWidget.findColumnWidget(sourceColumn.getReferenceColumn().getId());
        if (targetColumnWidget == null) { // TODO remove this block
            return;
        }

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourceTableWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetTableWidget.getNodeWidgetInfo().getId());
//      edgeInfo.setType(NBModelerUtil.getEdgeType(sourceTableWidget, targetTableWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourceTableWidget, targetTableWidget, edgeWidget, foreignKeyWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourceTableWidget, targetTableWidget, edgeWidget, targetColumnWidget));

    }

    @Override
    public void saveModelerFile(ModelerFile file) {
        file.getParentFile().getModelerUtil().saveModelerFile(file.getParentFile());
    }
    
    @Override
    public String getContent(ModelerFile file) {
        return file.getParentFile().getModelerUtil().getContent(file.getParentFile());
    }

    @Override
    public INodeWidget updateNodeWidgetDesign(ShapeDesign shapeDesign, INodeWidget inodeWidget) {
        PNodeWidget nodeWidget = (PNodeWidget) inodeWidget;
        //ELEMENT_UPGRADE
//        if (shapeDesign != null) {
//            if (shapeDesign.getOuterShapeContext() != null) {
//                if (shapeDesign.getOuterShapeContext().getBackground() != null) {
//                    nodeWidget.setOuterElementStartBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getStartColor());
//                    nodeWidget.setOuterElementEndBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getOuterShapeContext().getBorder() != null) {
//                    nodeWidget.setOuterElementBorderColor(shapeDesign.getOuterShapeContext().getBorder().getColor());
//                    nodeWidget.setOuterElementBorderWidth(shapeDesign.getOuterShapeContext().getBorder().getWidth());
//                }
//            }
//            if (shapeDesign.getInnerShapeContext() != null) {
//                if (shapeDesign.getInnerShapeContext().getBackground() != null) {
//                    nodeWidget.setInnerElementStartBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getStartColor());
//                    nodeWidget.setInnerElementEndBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getInnerShapeContext().getBorder() != null) {
//                    nodeWidget.setInnerElementBorderColor(shapeDesign.getInnerShapeContext().getBorder().getColor());
//                    nodeWidget.setInnerElementBorderWidth(shapeDesign.getInnerShapeContext().getBorder().getWidth());
//                }
//            }
//        }

        return (INodeWidget) nodeWidget;
    }

    @Override
    public Anchor getAnchor(INodeWidget inodeWidget) {
        INodeWidget nodeWidget = inodeWidget;
        Anchor sourceAnchor;
        if (nodeWidget instanceof IFlowNodeWidget) {
            sourceAnchor = new CustomRectangularAnchor(nodeWidget, -5, true);
        } else {
            throw new InvalidElmentException("Invalid JPA Process Element : " + nodeWidget);
        }
        return sourceAnchor;
    }

    @Override
    public void transformNode(IFlowNodeWidget flowNodeWidget, IModelerDocument document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IPinWidget attachPinWidget(DBModelerScene scene, INodeWidget nodeWidget, PinWidgetInfo widgetInfo) {
        IPinWidget widget = null;
        if (widgetInfo.getDocumentId().equals(BasicColumnWidget.class.getSimpleName())) {
            widget = new BasicColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(JoinColumnWidget.class.getSimpleName())) {
            widget = new JoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(InverseJoinColumnWidget.class.getSimpleName())) {
            widget = new InverseJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(PrimaryKeyWidget.class.getSimpleName())) {
            widget = new PrimaryKeyWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(EmbeddedAttributeColumnWidget.class.getSimpleName())) {
            widget = new EmbeddedAttributeColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(EmbeddedAttributeJoinColumnWidget.class.getSimpleName())) {
            widget = new EmbeddedAttributeJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(EmbeddedAssociationJoinColumnWidget.class.getSimpleName())) {
            widget = new EmbeddedAssociationJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(EmbeddedAssociationInverseJoinColumnWidget.class.getSimpleName())) {
            widget = new EmbeddedAssociationInverseJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(ParentAttributeColumnWidget.class.getSimpleName())) {
            widget = new ParentAttributeColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(ParentAttributePrimaryKeyWidget.class.getSimpleName())) {
            widget = new ParentAttributePrimaryKeyWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(ParentAssociationJoinColumnWidget.class.getSimpleName())) {
            widget = new ParentAssociationJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(ParentAssociationInverseJoinColumnWidget.class.getSimpleName())) {
            widget = new ParentAssociationInverseJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(PrimaryKeyJoinColumnWidget.class.getSimpleName())) {
            widget = new PrimaryKeyJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(DiscriminatorColumnWidget.class.getSimpleName())) {
            widget = new DiscriminatorColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MapKeyColumnWidget.class.getSimpleName())) {
            widget = new MapKeyColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MapKeyJoinColumnWidget.class.getSimpleName())) {
            widget = new MapKeyJoinColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MapKeyEmbeddedColumnWidget.class.getSimpleName())) {
            widget = new MapKeyEmbeddedColumnWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else {
            throw new InvalidElmentException("Invalid DB Element");
        }
        return widget;
    }

    @Override
    public void dettachEdgeSourceAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dettachEdgeTargetAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEdgeSourceAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        edgeWidget.setSourceAnchor(sourcePinWidget.createAnchor());

    }

    @Override
    public void attachEdgeSourceAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, INodeWidget sourceNodeWidget) { //BUG : Remove this method
        edgeWidget.setSourceAnchor(((IPNodeWidget) sourceNodeWidget).getNodeAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        edgeWidget.setTargetAnchor(targetPinWidget.createAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(DBModelerScene scene, IEdgeWidget edgeWidget, INodeWidget targetNodeWidget) { //BUG : Remove this method
        edgeWidget.setTargetAnchor(((IPNodeWidget) targetNodeWidget).getNodeAnchor());
    }

    @Override
    public IEdgeWidget attachEdgeWidget(DBModelerScene scene, EdgeWidgetInfo widgetInfo) {
        IEdgeWidget edgeWidget = getEdgeWidget(scene, widgetInfo);
        edgeWidget.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setRouter(scene.getRouter());
        ((IFlowEdgeWidget) edgeWidget).setName(widgetInfo.getName());

        return edgeWidget;
    }

    @Override
    public ResizeBorder getNodeBorder(INodeWidget nodeWidget) {
        nodeWidget.setWidgetBorder(NodeWidget.RECTANGLE_RESIZE_BORDER);
        return PNodeWidget.RECTANGLE_RESIZE_BORDER;
    }

    @Override
    public INodeWidget attachNodeWidget(DBModelerScene scene, NodeWidgetInfo widgetInfo) {
        IFlowNodeWidget widget = null;
        IModelerDocument modelerDocument = widgetInfo.getModelerDocument();
        switch (modelerDocument.getId()) {
            case "BaseTable":
                widget = new BaseTableWidget(scene, widgetInfo);
                break;
            case "SecondaryTable":
                widget = new SecondaryTableWidget(scene, widgetInfo);
                break;
            case "RelationTable":
                widget = new RelationTableWidget(scene, widgetInfo);
                break;
            case "CollectionTable":
                widget = new CollectionTableWidget(scene, widgetInfo);
                break;
            default:
                throw new InvalidElmentException("Invalid DB Element");
        }
        return (INodeWidget) widget;
    }

    private IEdgeWidget getEdgeWidget(DBModelerScene scene, EdgeWidgetInfo edgeWidgetInfo) {
        IEdgeWidget edgeWidget = new ReferenceFlowWidget(scene, edgeWidgetInfo);
        return edgeWidget;
    }

    @Override
    public String getEdgeType(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, String connectionContextToolId) {
        String edgeType = connectionContextToolId;
        return edgeType;
    }

    @Override
    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeSourcePinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, ColumnWidget sourceColumnWidget) {
        if (sourceNodeWidget instanceof TableWidget && targetNodeWidget instanceof TableWidget && edgeWidget instanceof ReferenceFlowWidget && sourceColumnWidget instanceof ForeignKeyWidget) {
            ReferenceFlowWidget referenceFlowWidget = (ReferenceFlowWidget) edgeWidget;
            TableWidget targetTableWidget = (TableWidget) targetNodeWidget;
            DBColumn sourceColumn = (DBColumn) sourceColumnWidget.getBaseElementSpec();
            IReferenceColumnWidget targetColumnWidget = targetTableWidget.findColumnWidget(sourceColumn.getReferenceColumn().getId());
            referenceFlowWidget.setReferenceColumnWidget(targetColumnWidget);
            referenceFlowWidget.setForeignKeyWidget((ForeignKeyWidget) sourceColumnWidget);
            return sourceColumnWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @Override
    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeTargetPinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, ColumnWidget targetColumnWidget) {
        if (sourceNodeWidget instanceof TableWidget && targetNodeWidget instanceof TableWidget
                && edgeWidget instanceof ReferenceFlowWidget && targetColumnWidget instanceof ColumnWidget) {
            return targetColumnWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static void inDev() {
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "This functionality is in developement");
    }

}
