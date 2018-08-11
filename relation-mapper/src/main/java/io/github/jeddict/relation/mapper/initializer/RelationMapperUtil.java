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
package io.github.jeddict.relation.mapper.initializer;

import io.github.jeddict.db.modeler.exception.DBConnectionNotFound;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import io.github.jeddict.jpa.spec.extend.cache.DatabaseConnectionCache;
import static io.github.jeddict.jpa.spec.extend.cache.DatabaseConnectionCache.DEFAULT_DRIVER;
import static io.github.jeddict.jpa.spec.extend.cache.DatabaseConnectionCache.DEFAULT_URL;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.relation.mapper.classloader.DynamicDriverClassLoader;
import io.github.jeddict.relation.mapper.persistence.internal.jpa.deployment.JPAMPersistenceUnitProcessor;
import io.github.jeddict.relation.mapper.persistence.internal.jpa.metadata.JPAMMetadataProcessor;
import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.spec.DBDiscriminatorColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAssociationColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAssociationInverseJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAssociationJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAttributeColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAttributeJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedColumn;
import io.github.jeddict.relation.mapper.spec.DBEntityMappings;
import io.github.jeddict.relation.mapper.spec.DBInverseJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBMapKeyColumn;
import io.github.jeddict.relation.mapper.spec.DBMapKeyEmbeddedColumn;
import io.github.jeddict.relation.mapper.spec.DBMapKeyJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBMapping;
import io.github.jeddict.relation.mapper.spec.DBParentAssociationColumn;
import io.github.jeddict.relation.mapper.spec.DBParentAssociationInverseJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBParentAssociationJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBParentAttributeColumn;
import io.github.jeddict.relation.mapper.spec.DBParentColumn;
import io.github.jeddict.relation.mapper.spec.DBPrimaryKeyJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.relation.mapper.widget.column.ColumnWidget;
import io.github.jeddict.relation.mapper.widget.column.ForeignKeyWidget;
import io.github.jeddict.relation.mapper.widget.flow.ReferenceFlowWidget;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor.Mode;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.tools.schemaframework.JPAMSchemaManager;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.util.IModelerUtil;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modules.db.explorer.ConnectionList;
import org.netbeans.modules.db.explorer.action.ConnectAction;
import org.openide.windows.WindowManager;

public class RelationMapperUtil implements IModelerUtil<RelationMapperScene> {

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
        ClassLoader cl = RelationMapperUtil.class.getClassLoader();
        VIEW_SQL = new ImageIcon(cl.getResource("io/github/jeddict/relation/mapper/resource/image/VIEW_SQL.png"));//Eager Loading required
    }

    @Override
    public void init() {
        if (COLUMN == null) {
            ClassLoader cl = RelationMapperUtil.class.getClassLoader();

            BASE_TABLE_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/TABLE.gif";
            SECONDARY_TABLE_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/SECONDARY_TABLE.gif";
            COLLECTION_TABLE_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/COLLECTION_TABLE.gif";
            RELATION_TABLE_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/JOIN_TABLE.png";
            COLUMN_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/COLUMN.gif";
            FOREIGNKEY_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/FOREIGN_KEY.gif";
            PRIMARYKEY_ICON_PATH = "io/github/jeddict/relation/mapper/resource/image/PRIMARY_KEY.gif";
            TAB_ICON = new ImageIcon(cl.getResource("io/github/jeddict/relation/mapper/resource/image/TAB_ICON.png")).getImage();
            RELOAD_ICON = new ImageIcon(cl.getResource("io/github/jeddict/relation/mapper/resource/image/RELOAD.png"));
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
            
            RelationMapperScene scene = (RelationMapperScene) file.getModelerScene();
            scene.startSceneGeneration();
            
            EntityMappings entityMapping = (EntityMappings) file.getAttributes().get(EntityMappings.class.getSimpleName());
            WorkSpace workSpace = (WorkSpace) file.getAttributes().get(WorkSpace.class.getSimpleName());
            DBMapping dbMapping = createDBMapping(file, entityMapping, workSpace);
            scene.setBaseElementSpec(dbMapping);
            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMapping);

            dbMapping.getTables().forEach(table -> loadTable(scene, table));
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
                    throw new DBConnectionNotFound(ex);
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

    private void loadTable(RelationMapperScene scene, IFlowNode flowElement) {
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
            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(document, new Point(0, 0));
            nodeWidgetInfo.setId(flowElement.getId());
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
                    table.getColumns().forEach((column) -> {
                        if (column instanceof DBDiscriminatorColumn) {
                            tableWidget.addDiscriminatorColumn(column.getName(), column);
                        } else if (column instanceof DBJoinColumn) {
                            tableWidget.addJoinColumn(column.getName(), column);
                        } else if (column instanceof DBInverseJoinColumn) {
                            tableWidget.addInverseJoinColumn(column.getName(), column);
                        } else if (column instanceof DBPrimaryKeyJoinColumn) {
                            tableWidget.addPrimaryKeyJoinColumn(column.getName(), column);
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
                            tableWidget.addPrimaryKey(column.getName(), column);
                        } else {
                            tableWidget.addBasicColumn(column.getName(), column);
                        }
                    });
                    tableWidget.sortAttributes();
                }
                scene.reinstallColorScheme(tableWidget);
            }

        }
    }

    private void loadFlowEdge(RelationMapperScene scene) {

        scene.getBaseElements()
                .stream()
                .filter((baseElementWidget) -> baseElementWidget instanceof TableWidget)
                .forEach((baseElementWidget) -> {
                    TableWidget tableWidget = (TableWidget) baseElementWidget;
                    // tableWidget.getPrimaryKeyWidgets().forEach((foreignKeyWidget) -> {
                    //                loadEdge(scene, tableWidget, (ForeignKeyWidget) foreignKeyWidget);
                    //            });
                    tableWidget.getForeignKeyWidgets().forEach((foreignKeyWidget) -> {
                        loadEdge(scene, tableWidget, (ForeignKeyWidget) foreignKeyWidget);
                    });
                });
    }

    private void loadEdge(RelationMapperScene scene, TableWidget sourceTableWidget, ForeignKeyWidget foreignKeyWidget) {
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

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(e ->  new ReferenceFlowWidget(scene, e));
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourceTableWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetTableWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType("REFERENCE");
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(sourceTableWidget, targetTableWidget, foreignKeyWidget));
        scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(sourceTableWidget, targetTableWidget, targetColumnWidget));

    }

    @Override
    public void saveModelerFile(ModelerFile file) {
        file.getParentFile().getModelerUtil().saveModelerFile(file.getParentFile());
    }
    
    @Override
    public String getContent(ModelerFile file) {
        return file.getParentFile().getModelerUtil().getContent(file.getParentFile());
    }

    public static void inDev() {
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "This functionality is not supported yet, please raise a ticket");
    }
    
    @Override
    public void loadBaseElement(IBaseElementWidget parentConatiner, Map<IBaseElement,Rectangle> elements) {
        throw new UnsupportedOperationException("CPP not supported in DB Modeler");
    }
    
    @Override
    public List<IBaseElement> clone(List<IBaseElement> element){
        throw new UnsupportedOperationException("Clonning not supported in DB Modeler");
    }
}
