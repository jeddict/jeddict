/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.apache.velocity.util.StringUtils;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BaseAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MultiRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.properties.classmember.ClassMemberPanel;
import org.netbeans.jpa.modeler.properties.classmember.ConstructorPanel;
import org.netbeans.jpa.modeler.properties.classmember.HashcodeEqualsPanel;
import org.netbeans.jpa.modeler.properties.entitygraph.NamedEntityGraphPanel;
import org.netbeans.jpa.modeler.properties.cascade.CascadeTypePanel;
import org.netbeans.jpa.modeler.properties.fieldtype.FieldTypePanel;
import org.netbeans.jpa.modeler.properties.idgeneration.IdGeneratorPanel;
import org.netbeans.jpa.modeler.properties.inheritence.InheritencePanel;
import org.netbeans.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import org.netbeans.jpa.modeler.properties.named.nativequery.NamedNativeQueryPanel;
import org.netbeans.jpa.modeler.properties.named.query.NamedQueryPanel;
import org.netbeans.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import org.netbeans.jpa.modeler.properties.named.storedprocedurequery.NamedStoredProcedureQueryPanel;
import org.netbeans.jpa.modeler.spec.AccessType;
import static org.netbeans.jpa.modeler.spec.AccessType.FIELD;
import static org.netbeans.jpa.modeler.spec.AccessType.PROPERTY;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.extend.AccessTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.Constructor;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_ELEMENT;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_TRANSIENT;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ActionHandler;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ComboBoxListener;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.support.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;
import org.openide.nodes.PropertySupport;
import static org.openide.util.NbBundle.getMessage;
import org.openide.windows.WindowManager;

public class PropertiesHandler {

    public static ComboBoxPropertySupport getAccessTypeProperty(JPAModelerScene modelerScene, final AccessTypeHandler accessTypeHandlerSpec) {
        ComboBoxListener<AccessType> comboBoxListener = new ComboBoxListener<AccessType>() {
            @Override
            public void setItem(ComboBoxValue<AccessType> value) {
                accessTypeHandlerSpec.setAccess(value.getValue());
            }

            @Override
            public ComboBoxValue<AccessType> getItem() {
                if (accessTypeHandlerSpec.getAccess() != null) {
                    return new ComboBoxValue<>(accessTypeHandlerSpec.getAccess(), accessTypeHandlerSpec.getAccess().value());
                } else {
                    return new ComboBoxValue<>(AccessType.getDefault(), AccessType.getDefault().value());
                }
            }

            @Override
            public List<ComboBoxValue<AccessType>> getItemList() {
                ComboBoxValue<AccessType>[] values;
                values = new ComboBoxValue[]{
                    new ComboBoxValue<>(FIELD, "Field"),
                    new ComboBoxValue<>(PROPERTY, "Property")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "Field";
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "accessType", "Access Type", "", comboBoxListener);
    }

    public static ComboBoxPropertySupport getCollectionTypeProperty(AttributeWidget<? extends Attribute> attributeWidget, final CollectionTypeHandler colSpec) {
        JPAModelerScene modelerScene = attributeWidget.getModelerScene();
        EntityMappings em = modelerScene.getBaseElementSpec();
        ModelerFile modelerFile = modelerScene.getModelerFile();
        ComboBoxListener<String> comboBoxListener = new ComboBoxListener<String>() {
            private final Set<String> value = new HashSet<>();

            @Override
            public void setItem(ComboBoxValue<String> value) {
                String prevType = colSpec.getCollectionType();
                String newType = value.getValue();
                colSpec.setCollectionType(newType);
                em.getCache().addCollectionClass(newType);//move item to top in cache

                Class prevClass = null;
                try {
                    prevClass = Class.forName(prevType);
                } catch (ClassNotFoundException ex) {
                }
                Class newClass = null;
                try {
                    newClass = Class.forName(newType);
                } catch (ClassNotFoundException ex) {
                }

                if ((prevClass != null && newClass != null && prevClass != newClass && (Map.class.isAssignableFrom(prevClass) || Map.class.isAssignableFrom(newClass)))
                        || (prevClass == null && newClass != null && Map.class.isAssignableFrom(newClass))
                        || (prevClass != null && newClass == null && Map.class.isAssignableFrom(prevClass))) {
                    if(newClass==null || !Map.class.isAssignableFrom(newClass)){
                       ((MapKeyHandler)attributeWidget.getBaseElementSpec()).setMapKeyAttribute(null);
                    }
                    attributeWidget.refreshProperties();
                }
            }

            @Override
            public ComboBoxValue<String> getItem() {
                if (!value.contains(colSpec.getCollectionType())) {
                    value.add(colSpec.getCollectionType());
                    em.getCache().addCollectionClass(colSpec.getCollectionType());
                }
                return new ComboBoxValue(colSpec.getCollectionType(), colSpec.getCollectionType().substring(colSpec.getCollectionType().lastIndexOf('.') + 1));
            }

            @Override
            public List<ComboBoxValue<String>> getItemList() {
                List<ComboBoxValue<String>> comboBoxValues = new ArrayList<>();
                value.addAll(em.getCache().getCollectionClasses());
                em.getCache().getCollectionClasses().stream().forEach((collection) -> {
                    Class _class;
                    try {
                        _class = Class.forName(collection);
                        comboBoxValues.add(new ComboBoxValue(_class.getName(), _class.getSimpleName()));
                    } catch (ClassNotFoundException ex) {
                        comboBoxValues.add(new ComboBoxValue(collection, collection + "(Not Exist)"));
                    }
                });
                return comboBoxValues;
            }

            @Override
            public String getDefaultText() {
                return EMPTY;
            }

            @Override
            public ActionHandler getActionHandler() {
                return ActionHandler.getInstance(() -> {
                    String collectionType = NBModelerUtil.browseClass(modelerFile);
                    return new ComboBoxValue<String>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
                })
                        .afterCreation(e -> em.getCache().addCollectionClass(e.getValue()))
                        .afterDeletion(e -> em.getCache().getCollectionClasses().remove(e.getValue()))
                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you sue you want to delete this collection class ?", "Delete Collection Class", JOptionPane.OK_CANCEL_OPTION));
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "collectionType", "Collection Type", "", comboBoxListener);
    }
    
    public static ComboBoxPropertySupport getMapKeyProperty(AttributeWidget<? extends Attribute> attributeWidget, final MapKeyHandler mapKeySpec, PropertyVisibilityHandler mapKeyVisibilityHandler) {
        JPAModelerScene modelerScene = attributeWidget.getModelerScene();
        EntityMappings em = modelerScene.getBaseElementSpec();
        ComboBoxListener<Attribute> comboBoxListener = new ComboBoxListener<Attribute>() {
            private final Set<Attribute> value = new HashSet<>();

            @Override
            public void setItem(ComboBoxValue<Attribute> value) {
                Attribute newType = value.getValue();
                mapKeySpec.setMapKeyAttribute(newType);
            }

            @Override
            public ComboBoxValue<Attribute> getItem() {
                Attribute attribute;
                if (mapKeySpec.getMapKeyAttribute() != null) {
                    attribute = mapKeySpec.getMapKeyAttribute();
                } else {
                    attribute = value.size() < 1 ? null : value.stream().findAny().get();
                }
                if (attribute != null) {
                    return new ComboBoxValue(attribute, attribute.getName() + " : " + JavaSourceHelper.getSimpleClassName(attribute.getDataTypeLabel()));
                } else {
                    return new ComboBoxValue(null, EMPTY);
                }
            }

            @Override
            public List<ComboBoxValue<Attribute>> getItemList() {
                List<ComboBoxValue<Attribute>> comboBoxValues = new ArrayList<>();
                
                if(attributeWidget instanceof MultiRelationAttributeWidget){
                    ((MultiRelationAttributeWidget)attributeWidget).getRelationFlowWidget().getTargetEntityWidget().
                    getAllAttributeWidgets().stream().filter(a -> !(a instanceof MultiValueEmbeddedAttributeWidget) &&
                            !(a instanceof TransientAttributeWidget) && !(a instanceof MultiRelationAttributeWidget) &&
                            !(a instanceof BasicCollectionAttributeWidget))
                           .forEach(classAttributeWidget -> {
                               Attribute attribute = ((AttributeWidget<? extends Attribute>)classAttributeWidget).getBaseElementSpec();
                               comboBoxValues.add(new ComboBoxValue(attribute, attribute.getName() + " : " +JavaSourceHelper.getSimpleClassName(attribute.getDataTypeLabel())));
                               value.add(attribute);
                   });
                }
                
                return comboBoxValues;
            }

            @Override
            public String getDefaultText() {
                return EMPTY;
            }

//            @Override
//            public ActionHandler getActionHandler() {
//                return ActionHandler.getInstance(() -> {
//                    String collectionType = NBModelerUtil.browseClass(modelerFile);
//                    return new ComboBoxValue<String>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
//                })
//                        .afterCreation(e -> em.getCache().addCollectionClass(e.getValue()))
//                        .afterDeletion(e -> em.getCache().getCollectionClasses().remove(e.getValue()))
//                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you sue you want to delete this collection class ?", "Delete Collection Class", JOptionPane.OK_CANCEL_OPTION));
//            }
        };
        attributeWidget.addPropertyVisibilityHandler("mapKey", () -> {
            Attribute attribute = attributeWidget.getBaseElementSpec();
            return mapKeyVisibilityHandler.isVisible() && attribute instanceof MultiRelationAttribute && ((MultiRelationAttribute)attribute).getMapKeyType() == MapKeyType.EXT;
        });
        
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "mapKey", "Map Key", "", comboBoxListener);
    }

    public static ComboBoxPropertySupport getFetchTypeProperty(JPAModelerScene modelerScene, final FetchTypeHandler fetchTypeHandlerSpec) {
        ComboBoxListener comboBoxListener = new ComboBoxListener() {
            @Override
            public void setItem(ComboBoxValue value) {
                fetchTypeHandlerSpec.setFetch((FetchType) value.getValue());
            }

            @Override
            public ComboBoxValue getItem() {
                if (fetchTypeHandlerSpec.getFetch() == FetchType.EAGER) {
                    return new ComboBoxValue(FetchType.EAGER, "Eager");
                } else if (fetchTypeHandlerSpec.getFetch() == FetchType.LAZY) {
                    return new ComboBoxValue(FetchType.LAZY, "Lazy");
                } else {
                    return new ComboBoxValue(null, "Default(Eager)");
                }
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                ComboBoxValue[] values = new ComboBoxValue[]{
                    new ComboBoxValue(null, "Default(Eager)"),
                    new ComboBoxValue(FetchType.EAGER, "Eager"),
                    new ComboBoxValue(FetchType.LAZY, "Lazy")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "fetchType", "Fetch Type", "", comboBoxListener);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<JoinColumn> joinColumnsSpec) {
        return getJoinColumnsProperty(id, name, desc, modelerScene, joinColumnsSpec, null);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<JoinColumn> joinColumnsSpec, Entity entity) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JoinColumnPanel(entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = joinColumnsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<JoinColumn> joinColumns = joinColumnsSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<JoinColumn> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    JoinColumn joinColumn = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = joinColumn;
                    row[1] = joinColumn.getName();
                    row[2] = joinColumn.getReferencedColumnName();//for representation
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                joinColumnsSpec.clear();
                data.stream().forEach((row) -> {
                    joinColumnsSpec.add((JoinColumn) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getResultSetMappingsProperty(String id, String name, String desc, JPAModelerScene modelerScene, final Entity entity) {
        final Set<SqlResultSetMapping> sqlResultSetMappingSpec = entity.getSqlResultSetMapping();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);

        attributeEntity.setCountDisplay(new String[]{"No ResultSet Mappings", "One ResultSet Mapping", " ResultSet Mappings"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("ResultSet Name", true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ResultSetMappingsPanel(modelerScene.getModelerFile(), entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                count = sqlResultSetMappingSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                for (SqlResultSetMapping resultSetMapping : sqlResultSetMappingSpec) {
                    Object[] row = new Object[2];
                    row[0] = resultSetMapping;
                    row[1] = resultSetMapping.getName();
                    data_local.add(row);
                }
//                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                sqlResultSetMappingSpec.clear();
                data.stream().map((row) -> (SqlResultSetMapping) row[0]).map((resultSetMapping) -> {
                    resultSetMapping.setIdentifiableClass(entity);
                    return resultSetMapping;
                }).forEach((resultSetMapping) -> {
                    sqlResultSetMappingSpec.add(resultSetMapping);
                });
                initData();
            }
        });
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedStoredProcedureQueryProperty(String id, String name, String desc, JPAModelerScene modelerScene, Entity entity) {
        final List<NamedStoredProcedureQuery> namedStoredProcedureQueriesSpec = entity.getNamedStoredProcedureQuery();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No NamedStoredProcedureQueries exist", "One NamedStoredProcedureQuery exist", "NamedStoredProcedureQueries exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("ProcedureName", false, String.class));
        columns.add(new Column("Parameters", false, Integer.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedStoredProcedureQueryPanel(modelerScene.getModelerFile()));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedStoredProcedureQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedStoredProcedureQuery> joinColumns = namedStoredProcedureQueriesSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<NamedStoredProcedureQuery> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    NamedStoredProcedureQuery namedStoredProcedureQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedStoredProcedureQuery;
                    row[1] = namedStoredProcedureQuery.getName();
                    row[2] = namedStoredProcedureQuery.getProcedureName();
                    row[3] = namedStoredProcedureQuery.getParameter().size();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedStoredProcedureQueriesSpec.clear();
                data.stream().forEach((row) -> {
                    namedStoredProcedureQueriesSpec.add((NamedStoredProcedureQuery) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedQueryProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<NamedQuery> namedQueriesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No NamedQueries exist", "One NamedQuery exist", "NamedQueries exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Lock Mode Type", false, true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedQueryPanel(modelerScene.getModelerFile()));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedQuery> joinColumns = namedQueriesSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<NamedQuery> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    NamedQuery namedQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedQuery;
                    row[1] = namedQuery.getName();
                    row[2] = namedQuery.getQuery();
                    row[3] = namedQuery.getLockMode();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedQueriesSpec.clear();
                data.stream().forEach((row) -> {
                    namedQueriesSpec.add((NamedQuery) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedEntityGraphProperty(String id, String name, String desc, final EntityWidget entityWidget) {
        JPAModelerScene modelerScene = entityWidget.getModelerScene();
        final List<NamedEntityGraph> entityGraphsSpec = entityWidget.getBaseElementSpec().getNamedEntityGraph();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No EntityGraphs exist", "One EntityGraph exist", "EntityGraphs exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedEntityGraphPanel(entityWidget));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = entityGraphsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedEntityGraph> entityGraphList = entityGraphsSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<NamedEntityGraph> itr = entityGraphList.iterator();
                while (itr.hasNext()) {
                    NamedEntityGraph entityGraph = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = entityGraph;
                    row[1] = entityGraph.getName();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                entityGraphsSpec.clear();
                data.stream().forEach((row) -> {
                    entityGraphsSpec.add((NamedEntityGraph) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedNativeQueryProperty(String id, String name, String desc, JPAModelerScene modelerScene, final Entity entity) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No Named Native Queries exist", "One Named Native Query exist", "Named Native Queries exist"});
        List<NamedNativeQuery> namedNativeQueriesSpec = entity.getNamedNativeQuery();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Result Class", false, true, String.class));
        columns.add(new Column("ResultSet Mapping", false, true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedNativeQueryPanel(modelerScene.getModelerFile(), entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedNativeQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedNativeQuery> namedNativeQueries = namedNativeQueriesSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<NamedNativeQuery> itr = namedNativeQueries.iterator();
                while (itr.hasNext()) {
                    NamedNativeQuery namedNativeQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedNativeQuery;
                    row[1] = namedNativeQuery.getName();
                    row[2] = namedNativeQuery.getQuery();
                    row[3] = namedNativeQuery.getResultClass();
                    row[4] = namedNativeQuery.getResultSetMapping();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedNativeQueriesSpec.clear();
                data.stream().forEach((row) -> {
                    namedNativeQueriesSpec.add((NamedNativeQuery) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static void getJaxbVarTypeProperty(final ElementPropertySet set, final AttributeWidget attributeWidget, final JaxbVariableTypeHandler varHandlerSpec) {

        final List<JaxbVariableType> jaxbVariableList = varHandlerSpec.getJaxbVariableList();

        ComboBoxListener comboBoxListener = new ComboBoxListener<JaxbVariableType>() {
            @Override
            public void setItem(ComboBoxValue<JaxbVariableType> value) {
                varHandlerSpec.setJaxbVariableType(value.getValue());
                varHandlerSpec.setJaxbXmlAttribute(null);
                varHandlerSpec.setJaxbXmlElement(null);
                varHandlerSpec.setJaxbXmlElementList(null);
                if (value.getValue() == JaxbVariableType.XML_ATTRIBUTE || value.getValue() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
                    varHandlerSpec.setJaxbXmlAttribute(new JaxbXmlAttribute());
                    set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlAttribute(), attributeWidget.getPropertyChangeListeners());
                } else if (value.getValue() == JaxbVariableType.XML_ELEMENT || value.getValue() == JaxbVariableType.XML_LIST_ELEMENT) {
                    varHandlerSpec.setJaxbXmlElement(new JaxbXmlElement());
                    set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlElement(), attributeWidget.getPropertyChangeListeners());
                } else if (value.getValue() == JaxbVariableType.XML_ELEMENTS) {
                    varHandlerSpec.setJaxbXmlElementList(new ArrayList<>());
//                     set.createPropertySet( attributeWidget , varHandlerSpec.get(), attributeWidget.getPropertyChangeListeners());
                }
                attributeWidget.refreshProperties();
            }

            @Override
            public ComboBoxValue<JaxbVariableType> getItem() {
                if (varHandlerSpec.getJaxbVariableType() == null) {
                    if (jaxbVariableList != null) {
                        return new ComboBoxValue<>(XML_ELEMENT, "Default(Element)");
                    } else {
                        return new ComboBoxValue<>(XML_TRANSIENT, XML_TRANSIENT.getDisplayText());
                    }
                } else {
                    return new ComboBoxValue<>(varHandlerSpec.getJaxbVariableType(), varHandlerSpec.getJaxbVariableType().getDisplayText());
                }
            }

            @Override
            public List<ComboBoxValue<JaxbVariableType>> getItemList() {
                List<ComboBoxValue<JaxbVariableType>> values = new ArrayList<>();
                if (jaxbVariableList != null) {
                    values.add(new ComboBoxValue<>(XML_ELEMENT, "Default(Element)"));
                    jaxbVariableList.stream().forEach((variableType) -> {
                        values.add(new ComboBoxValue<>(variableType, variableType.getDisplayText()));
                    });
                } else {
                    values.add(new ComboBoxValue<>(XML_TRANSIENT, XML_TRANSIENT.getDisplayText()));
                }
                return values;
            }

            @Override
            public String getDefaultText() {
                if (jaxbVariableList != null) {
                    return "Default(Element)";
                } else {
                    return JaxbVariableType.XML_TRANSIENT.getDisplayText();
                }
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        if (varHandlerSpec.getJaxbVariableType() == JaxbVariableType.XML_ATTRIBUTE) {
            set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlAttribute(), attributeWidget.getPropertyChangeListeners());
        } else if (varHandlerSpec.getJaxbVariableType() == JaxbVariableType.XML_ELEMENT) {
            set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlElement(), attributeWidget.getPropertyChangeListeners());
        }
        set.put("JAXB_PROP", new ComboBoxPropertySupport(attributeWidget.getModelerScene().getModelerFile(), "jaxbVariableType", "Variable Type", "", comboBoxListener, "root.jaxbSupport==true", varHandlerSpec));

    }

    public static EmbeddedPropertySupport getInheritenceProperty(EntityWidget entityWidget) {

        GenericEmbedded entity = new GenericEmbedded("inheritence", "Inheritence", "");
        try {
            entity.setEntityEditor(new InheritencePanel(entityWidget.getModelerScene().getModelerFile(), entityWidget));
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setDataListener(new EmbeddedDataListener<InheritenceHandler>() {
            private InheritenceHandler classSpec;

            @Override
            public void init() {
                classSpec = (InheritenceHandler) entityWidget.getBaseElementSpec();
            }

            @Override
            public InheritenceHandler getData() {
                return classSpec;
            }

            @Override
            public void setData(InheritenceHandler classSpec) {
                entityWidget.setBaseElementSpec((Entity) classSpec);
            }

            @Override
            public String getDisplay() {

                GeneralizationFlowWidget outgoingGeneralizationFlowWidget = entityWidget.getOutgoingGeneralizationFlowWidget();
                List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = entityWidget.getIncomingGeneralizationFlowWidgets();

                if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
                    outgoingGeneralizationFlowWidget = null;
                }
                if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
                    EntityWidget superEntityWidget = (EntityWidget) entityWidget.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
                    InheritenceHandler superClassSpec = (InheritenceHandler) superEntityWidget.getBaseElementSpec();
                    if (superClassSpec.getInheritance() != null && superClassSpec.getInheritance().getStrategy() != null) {
                        return superClassSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "ROOT";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "BRANCH";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else {
                    return "";
                }
            }

        });
        
        entityWidget.addPropertyVisibilityHandler("inheritence", (PropertyVisibilityHandler<String>) () -> {
            GeneralizationFlowWidget outgoingGeneralizationFlowWidget1 = entityWidget.getOutgoingGeneralizationFlowWidget();
            List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets1 = entityWidget.getIncomingGeneralizationFlowWidgets();
            if (outgoingGeneralizationFlowWidget1 != null && !(outgoingGeneralizationFlowWidget1.getSuperclassWidget() instanceof EntityWidget)) {
                outgoingGeneralizationFlowWidget1 = null;
            }
            if (outgoingGeneralizationFlowWidget1 != null || !incomingGeneralizationFlowWidgets1.isEmpty()) {
                return true;
            }
            return false;
        });
        return new EmbeddedPropertySupport(entityWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getHashcodeEqualsProperty(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("hashcode_equals", "hashcode() & equals()", "Define hashcode & equals implementation for the Entity");

        final JavaClass javaClassObj = persistenceClassWidget.getBaseElementSpec();
        entity.setEntityEditor(new HashcodeEqualsPanel(persistenceClassWidget));
        entity.setDataListener(new EmbeddedDataListener<JavaClass>() {
            private JavaClass javaClass;

            @Override
            public void init() {
                javaClass = javaClassObj;
            }

            @Override
            public JavaClass getData() {
                return javaClass;
            }

            @Override
            public void setData(JavaClass classMembers) {
                //IGNORE internal properties are modified
            }

            @Override
            public String getDisplay() {
                return String.format("hashcode{%s} equals{%s}",javaClass.getHashCodeMethod().getAttributes().size(), javaClass.getEqualsMethod().getAttributes().size());
            }

        });
        return new EmbeddedPropertySupport(persistenceClassWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getToStringProperty(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("toString", "toString()",getMessage(ClassMemberPanel.class, "LBL_tostring_select"));
        final ClassMembers classMembersObj = persistenceClassWidget.getBaseElementSpec().getToStringMethod();
        entity.setEntityEditor(new ClassMemberPanel(getMessage(ClassMemberPanel.class, "LBL_tostring_select"), persistenceClassWidget));
        entity.setDataListener(new EmbeddedDataListener<ClassMembers>() {
            private ClassMembers classMembers;

            @Override
            public void init() {
                classMembers = classMembersObj;
            }

            @Override
            public ClassMembers getData() {
                return classMembers;
            }

            @Override
            public void setData(ClassMembers classMembers) {
                //IGNORE internal properties are modified
            }

            @Override
            public String getDisplay() {
                return String.format("toString{%s}", classMembers.getAttributes().size());
            }

        });
        return new EmbeddedPropertySupport(persistenceClassWidget.getModelerScene().getModelerFile(), entity);
    }

    public static PropertySupport getConstructorProperties(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("constructor", "Constructor", "Constructor");
        attributeEntity.setCountDisplay(new String[]{"No Constructors exist", "One Constructor exist", "Constructors exist"});
        Set<Constructor> constructors = persistenceClassWidget.getBaseElementSpec().getConstructors();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Constructor List", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ConstructorPanel(persistenceClassWidget));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = constructors.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<Constructor> itr = constructors.iterator();
                while (itr.hasNext()) {
                    Constructor constructor = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = constructor;
                    row[1] = constructor.toString();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                constructors.clear();
                
                data.stream().forEach((row) -> {
                    constructors.add((Constructor) row[0]);
                });
                if(!constructors.isEmpty()){
                    constructors.add(Constructor.getNoArgsInstance());
                }
                
                this.data = data;
            }

        });

        return new NEntityPropertySupport(persistenceClassWidget.getModelerScene().getModelerFile(), attributeEntity);
    }

    public static EmbeddedPropertySupport getGeneratorProperty(IdAttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded("generator", "Id Generator", "");
        entity.setEntityEditor(new IdGeneratorPanel(attributeWidget.getModelerScene().getModelerFile()));

        entity.setDataListener(new EmbeddedDataListener<Id>() {
            private Id idAttribute;
            @Override
            public void init() {
                idAttribute = attributeWidget.getBaseElementSpec();
            }

            @Override
            public Id getData() {
                if (idAttribute.getGeneratedValue() == null) {
                    idAttribute.setGeneratedValue(new GeneratedValue());
                }
                return idAttribute;
            }

            @Override
            public void setData(Id classSpec) {
                attributeWidget.setBaseElementSpec(classSpec);
            }

            @Override
            public String getDisplay() {
                if (idAttribute.getGeneratedValue() != null && idAttribute.getGeneratedValue().getStrategy() != null) {
                    return StringUtils.firstLetterCaps(idAttribute.getGeneratedValue().getStrategy().toString());
                } else if (idAttribute.getGeneratedValue() == null || idAttribute.getGeneratedValue().getStrategy() == null) {
                    return "None";
                } else {
                    return "";
                }
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getFieldTypeProperty(String id, String name, String description,
            AttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded(id, name, description);
        if (attributeWidget.getBaseElementSpec() instanceof BaseAttribute) {
            if (attributeWidget.getBaseElementSpec() instanceof ElementCollection && ((ElementCollection) attributeWidget.getBaseElementSpec()).getConnectedClass() != null) {//SingleValueEmbeddableFlowWidget
                entity.setEntityEditor(null);
            } else if (attributeWidget.getBaseElementSpec() instanceof Embedded) {//to Disable it
                entity.setEntityEditor(null);
            } else {
                entity.setEntityEditor(new FieldTypePanel(attributeWidget.getModelerScene().getModelerFile()));
            }

        } else if (attributeWidget.getBaseElementSpec() instanceof RelationAttribute) {
            entity.setEntityEditor(null);
        }
        entity.setDataListener(new EmbeddedDataListener<Attribute>() {
            private Attribute attribute;
            private PersistenceClassWidget persistenceClassWidget = null;

            @Override
            public void init() {
                attribute = (Attribute) attributeWidget.getBaseElementSpec();
                if (attribute instanceof RelationAttribute) {
                    persistenceClassWidget = (PersistenceClassWidget)attributeWidget.getModelerScene().getBaseElement(((RelationAttribute) attribute).getConnectedEntity().getId());
                } else if (attribute instanceof ElementCollection && ((ElementCollection) attribute).getConnectedClass() != null) { //Embedded Collection
                    persistenceClassWidget = (PersistenceClassWidget) attributeWidget.getModelerScene().getBaseElement(((ElementCollection) attribute).getConnectedClass().getId());
                } else if (attribute instanceof Embedded) {
                    persistenceClassWidget = (PersistenceClassWidget) attributeWidget.getModelerScene().getBaseElement(((Embedded) attribute).getConnectedClass().getId());
                }
            }

            @Override
            public Attribute getData() {
                return attribute;
            }

            @Override
            public void setData(Attribute baseAttribute) {
                attributeWidget.setBaseElementSpec(baseAttribute);
                if (attributeWidget instanceof BaseAttributeWidget) {
                    ((BaseAttributeWidget)attributeWidget).createBeanValidationPropertySet(attributeWidget.getPropertyManager().getElementPropertySet());
                    attributeWidget.refreshProperties();
                }
            }

            @Override
            public String getDisplay() {
                return JavaSourceHelper.getSimpleClassName(attribute.getDataTypeLabel());
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), entity);
    }
 
    public static EmbeddedPropertySupport getCascadeProperty(RelationAttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded("cascadeType", "Cascade Type", "");
        entity.setEntityEditor(new CascadeTypePanel(attributeWidget.getModelerScene().getModelerFile()));

        entity.setDataListener(new EmbeddedDataListener<CascadeType>() {
            private RelationAttribute relationAttribute;

            @Override
            public void init() {
                relationAttribute = (RelationAttribute)attributeWidget.getBaseElementSpec();
            }

            @Override
            public CascadeType getData() {
                return relationAttribute.getCascade();
            }

            @Override
            public void setData(CascadeType cascadeType) {
                relationAttribute.setCascade(cascadeType);
            }

            @Override
            public String getDisplay() {
                StringBuilder display = new StringBuilder();
                CascadeType cascadeType = relationAttribute.getCascade();
                if (cascadeType == null) {
                    display.append("None");
                } else if (cascadeType.getCascadeAll() != null) {
                    display.append("All");
                } else {
                    if (cascadeType.getCascadeDetach() != null) {
                        display.append("Detach,");
                    }
                    if (cascadeType.getCascadeMerge() != null) {
                        display.append("Merge,");
                    }
                    if (cascadeType.getCascadePersist() != null) {
                        display.append("Persist,");
                    }
                    if (cascadeType.getCascadeRefresh() != null) {
                        display.append("Refresh,");
                    }
                    if (cascadeType.getCascadeRemove() != null) {
                        display.append("Remove,");
                    }
                    if (display.length() != 0) {
                        display.setLength(display.length() - 1);
                    }
                }

                return display.toString();
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), entity);
    }

}
