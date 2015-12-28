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
import java.util.Set;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.navigator.entitygraph.NamedEntityGraphPanel;
import org.netbeans.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import org.netbeans.jpa.modeler.properties.named.nativequery.NamedNativeQueryPanel;
import org.netbeans.jpa.modeler.properties.named.query.NamedQueryPanel;
import org.netbeans.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import org.netbeans.jpa.modeler.properties.named.storedprocedurequery.NamedStoredProcedureQueryPanel;
import org.netbeans.jpa.modeler.spec.AccessType;
import static org.netbeans.jpa.modeler.spec.AccessType.FIELD;
import static org.netbeans.jpa.modeler.spec.AccessType.PROPERTY;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.extend.AccessTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_ELEMENT;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_TRANSIENT;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ActionHandler;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ComboBoxListener;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.support.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Gaurav Gupta
 */
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

    public static ComboBoxPropertySupport getCollectionTypeProperty(JPAModelerScene modelerScene, final CollectionTypeHandler colSpec) {
        EntityMappings em = modelerScene.getBaseElementSpec();
        ModelerFile modelerFile = modelerScene.getModelerFile();
        ComboBoxListener<String> comboBoxListener = new ComboBoxListener<String>() {
            private final Set<String> value = new HashSet<>();
            @Override
            public void setItem(ComboBoxValue<String> value) {
                colSpec.setCollectionType(value.getValue());
                em.getCache().addCollectionClass(value.getValue());
            }

            @Override
            public ComboBoxValue<String> getItem() {
                if(!value.contains(colSpec.getCollectionType())){
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
                return "";
            }

            @Override
            public ActionHandler getActionHandler() {
                return ActionHandler.getInstance(() -> {
                    String collectionType = NBModelerUtil.browseClass(modelerFile);
                    return new ComboBoxValue<String>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
                })
                        .afterCreation(e -> em.getCache().addCollectionClass(e.getValue()))
                        .afterDeletion(e -> em.getCache().getCollectionClasses().remove(e.getValue()))
                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(null, "Are you sue you want to delete this collection class ?", "Delete Collection Class", JOptionPane.OK_CANCEL_OPTION));
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "collectionType", "Collection Type", "", comboBoxListener);
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
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JoinColumnPanel());

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
                    row[2] = joinColumn.getReferencedColumnName();
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
                    resultSetMapping.setEntity(entity);
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
        attributeEntity.setCustomDialog(new NamedStoredProcedureQueryPanel(modelerScene.getModelerFile(), entity));

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
        columns.add(new Column("Lock Mode Type", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedQueryPanel());

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

    public static PropertySupport getNamedNativeQueryProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<NamedNativeQuery> namedNativeQueriesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No Named Native Queries exist", "One Named Native Query exist", "Named Native Queries exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Result Class", false, String.class));
        columns.add(new Column("ResultSet Mapping", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedNativeQueryPanel());

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

}
