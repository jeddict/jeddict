/**
 * Copyright [2017] Gaurav Gupta
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
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.velocity.util.StringUtils.firstLetterCaps;
import org.netbeans.jcode.core.util.JavaIdentifiers;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.InheritanceStateType;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MultiRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.properties.annotation.AnnotationPanel;
import org.netbeans.jpa.modeler.properties.classmember.ClassMemberPanel;
import org.netbeans.jpa.modeler.properties.classmember.ConstructorPanel;
import org.netbeans.jpa.modeler.properties.classmember.HashcodeEqualsPanel;
import org.netbeans.jpa.modeler.properties.entitygraph.NamedEntityGraphPanel;
import org.netbeans.jpa.modeler.properties.cascade.CascadeTypePanel;
import org.netbeans.jpa.modeler.properties.convert.ConvertPanel;
import org.netbeans.jpa.modeler.properties.convert.ConverterPanel;
import org.netbeans.jpa.modeler.properties.convert.OverrideConvertPanel;
import org.netbeans.jpa.modeler.properties.custom.snippet.CustomSnippetPanel;
import org.netbeans.jpa.modeler.properties.extend.ClassExtendPanel;
import org.netbeans.jpa.modeler.properties.fieldtype.FieldTypePanel;
import org.netbeans.jpa.modeler.properties.idgeneration.IdGeneratorPanel;
import org.netbeans.jpa.modeler.properties.implement.JavaClassArtifactPanel;
import org.netbeans.jpa.modeler.properties.inheritance.InheritancePanel;
import org.netbeans.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import org.netbeans.jpa.modeler.properties.named.nativequery.NamedNativeQueryPanel;
import org.netbeans.jpa.modeler.properties.named.query.NamedQueryPanel;
import org.netbeans.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import org.netbeans.jpa.modeler.properties.named.storedprocedurequery.NamedStoredProcedureQueryPanel;
import org.netbeans.jpa.modeler.properties.order.OrderPanel;
import org.netbeans.jpa.modeler.properties.pkjoincolumn.PrimaryKeyJoinColumnPanel;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.spec.AccessType;
import static org.netbeans.jpa.modeler.spec.AccessType.FIELD;
import static org.netbeans.jpa.modeler.spec.AccessType.PROPERTY;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.Convert;
import org.netbeans.jpa.modeler.spec.Converter;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.PrimaryKeyJoinColumn;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.extend.AccessModifierType;
import org.netbeans.jpa.modeler.spec.extend.AccessTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.AttributeSnippet;
import org.netbeans.jpa.modeler.spec.extend.AttributeSnippetLocationType;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.ClassSnippet;
import org.netbeans.jpa.modeler.spec.extend.ClassSnippetLocationType;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.Constructor;
import org.netbeans.jpa.modeler.spec.extend.ConvertContainerHandler;
import org.netbeans.jpa.modeler.spec.extend.ConvertHandler;
import org.netbeans.jpa.modeler.spec.extend.EnumTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.Snippet;
import org.netbeans.jpa.modeler.spec.extend.annotation.Annotation;
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
import org.openide.windows.WindowManager;
import org.netbeans.jpa.modeler.spec.extend.InheritanceHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertContainerHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyConvertHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.extend.ReferenceClass;
import org.netbeans.jpa.modeler.spec.extend.SnippetLocation;
import org.netbeans.jpa.modeler.spec.extend.SortableAttribute;
import org.netbeans.jpa.modeler.spec.extend.TemporalTypeHandler;
import org.netbeans.jpa.modeler.spec.validator.ConvertValidator;
import static org.openide.util.NbBundle.getMessage;

public class PropertiesHandler {

    public static final String NONE_TYPE = "< none >";

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
                setCollectionType(value);
                manageMapType(prevType, newType);
                attributeWidget.setAttributeTooltip();
                attributeWidget.visualizeDataType();
            }

            void setCollectionType(ComboBoxValue<String> value) {
                String collectionType = value.getValue();
                boolean valid = false;
                try {
                    if (collectionType != null && !collectionType.trim().isEmpty()) {
                        if (java.util.Collection.class.isAssignableFrom(Class.forName(collectionType.trim()))
                                || java.util.Map.class.isAssignableFrom(Class.forName(collectionType.trim()))) {
                            valid = true;
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    //skip allow = false;
                }
                if (!valid) {
                    collectionType = java.util.Collection.class.getName();
                }

                colSpec.setCollectionType(collectionType);
                em.getCache().addCollectionClass(collectionType);//move item to top in cache
            }

            void manageMapType(String prevType, String newType) {

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

                if ((prevClass != null && newClass != null && prevClass != newClass
                        && (Map.class.isAssignableFrom(prevClass) || Map.class.isAssignableFrom(newClass)))
                        || (prevClass == null && newClass != null && Map.class.isAssignableFrom(newClass))
                        || (prevClass != null && newClass == null && Map.class.isAssignableFrom(prevClass))) {
                    if (newClass == null || !Map.class.isAssignableFrom(newClass)) {
                        ((MapKeyHandler) attributeWidget.getBaseElementSpec()).resetMapAttribute();
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

    public static ComboBoxPropertySupport getMapKeyProperty(AttributeWidget<? extends Attribute> attributeWidget, final MapKeyHandler mapKeyHandler, PropertyVisibilityHandler mapKeyVisibilityHandler) {
        JPAModelerScene modelerScene = attributeWidget.getModelerScene();
        EntityMappings em = modelerScene.getBaseElementSpec();
        ComboBoxListener<Attribute> comboBoxListener = new ComboBoxListener<Attribute>() {

            @Override
            public void setItem(ComboBoxValue<Attribute> value) {
                Attribute newType = value.getValue();
                mapKeyHandler.setMapKeyAttribute(newType);
                AttributeValidator.scanMapKeyHandlerError(attributeWidget);
                attributeWidget.visualizeDataType();
                attributeWidget.refreshProperties();
            }

            @Override
            public ComboBoxValue<Attribute> getItem() {
                Attribute attribute = null;
                if (mapKeyHandler.getMapKeyAttribute() != null) {
                    attribute = mapKeyHandler.getMapKeyAttribute();
                } else { //select any attribute if not found //TODO ensure PK
                    List<AttributeWidget<? extends Attribute>> attributeWidgets = getAllAttributeWidgets();
                    if (!attributeWidgets.isEmpty()) {
                        attribute = attributeWidgets.get(0).getBaseElementSpec();
                        mapKeyHandler.setMapKeyAttribute(attribute);
                        AttributeValidator.scanMapKeyHandlerError(attributeWidget);
                    }
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
                getAllAttributeWidgets().forEach(classAttributeWidget -> {
                    Attribute attribute = ((AttributeWidget<? extends Attribute>) classAttributeWidget).getBaseElementSpec();
                    comboBoxValues.add(new ComboBoxValue(attribute, attribute.getName() + " : " + JavaSourceHelper.getSimpleClassName(attribute.getDataTypeLabel())));
                });
                return comboBoxValues;
            }

            private List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets() {
                JavaClassWidget classWidget;
                if (attributeWidget instanceof MultiRelationAttributeWidget) {
                    classWidget = ((MultiRelationAttributeWidget) attributeWidget).getRelationFlowWidget().getTargetEntityWidget();
                } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                    classWidget = ((MultiValueEmbeddedAttributeWidget) attributeWidget).getEmbeddableFlowWidget().getTargetEmbeddableWidget();
                } else {
                    classWidget = attributeWidget.getClassWidget();
                }
                return (List<AttributeWidget<? extends Attribute>>) classWidget.getAllAttributeWidgets()
                        .stream()
                        .filter(a -> !(a instanceof MultiValueEmbeddedAttributeWidget)
                        && !(a instanceof TransientAttributeWidget) && !(a instanceof MultiRelationAttributeWidget)
                        && !(a instanceof BasicCollectionAttributeWidget))
                        .collect(toList());
            }

            @Override
            public String getDefaultText() {
                return EMPTY;
            }

        };
        org.netbeans.modeler.config.element.Attribute attribute = new org.netbeans.modeler.config.element.Attribute("mapKey", "Map Key", "");
        attribute.setAfter("mapKeyType");
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), attribute, comboBoxListener);
    }

    public static ComboBoxPropertySupport getEntityDisplayProperty(PersistenceClassWidget<Entity> classWidget) {
        JPAModelerScene modelerScene = classWidget.getModelerScene();
        Entity entity = classWidget.getBaseElementSpec();
        ComboBoxListener<Attribute> comboBoxListener = new ComboBoxListener<Attribute>() {

            @Override
            public void setItem(ComboBoxValue<Attribute> value) {
                Attribute newType = value.getValue();
                entity.setLabelAttribute(newType);
            }

            @Override
            public ComboBoxValue<Attribute> getItem() {
                Attribute attribute = null;
                if (entity.getLabelAttribute() != null) {
                    attribute = entity.getLabelAttribute();
                } else { //select any attribute if not found 
                    List<AttributeWidget<? extends Attribute>> attributeWidgets = getAllAttributeWidgets();
                    if (!attributeWidgets.isEmpty()) {
                        attribute = attributeWidgets.get(0).getBaseElementSpec();
                        entity.setLabelAttribute(attribute);
                    }
                }
                if (attribute != null) {
                    return new ComboBoxValue(attribute, attribute.getName());
                } else {
                    return new ComboBoxValue(null, EMPTY);
                }
            }

            @Override
            public List<ComboBoxValue<Attribute>> getItemList() {
                List<ComboBoxValue<Attribute>> comboBoxValues = new ArrayList<>();
                getAllAttributeWidgets().forEach(classAttributeWidget -> {
                    Attribute attribute = ((AttributeWidget<? extends Attribute>) classAttributeWidget).getBaseElementSpec();
                    comboBoxValues.add(new ComboBoxValue(attribute, attribute.getName()));
                });
                return comboBoxValues;
            }

            private List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets() {
                return (List<AttributeWidget<? extends Attribute>>) classWidget.getAllAttributeWidgets().stream().filter(a -> !(a instanceof EmbeddedAttributeWidget)
                        && !(a instanceof TransientAttributeWidget) && !(a instanceof RelationAttributeWidget)
                        && !(a instanceof BasicCollectionAttributeWidget)).collect(toList());
            }

            @Override
            public String getDefaultText() {
                return EMPTY;
            }

        };
        org.netbeans.modeler.config.element.Attribute attribute = new org.netbeans.modeler.config.element.Attribute("label", "UI Display Reference", "Select the attribute to represent the entity in UI");
//        attribute.setAfter("mapKeyType");
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), attribute, comboBoxListener);
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
                return Arrays.asList(
                        new ComboBoxValue(null, "Default(Eager)"),
                        new ComboBoxValue(FetchType.EAGER, "Eager"),
                        new ComboBoxValue(FetchType.LAZY, "Lazy"));
            }

            @Override
            public String getDefaultText() {
                return EMPTY;
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "fetchType", "Fetch Type", "", comboBoxListener);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<? extends JoinColumn> joinColumnsSpec) {
        return getJoinColumnsProperty(id, name, desc, modelerScene, joinColumnsSpec, null);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, JPAModelerScene modelerScene, final List<? extends JoinColumn> joinColumnsSpec, Entity entity) {
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
                List<? extends JoinColumn> joinColumns = joinColumnsSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<? extends JoinColumn> itr = joinColumns.iterator();
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
                    ((List<JoinColumn>) joinColumnsSpec).add((JoinColumn) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getAttributeOverridesProperty(String id, String name, String desc, JPAModelerScene modelerScene, final Set<AttributeOverride> attributeOverridesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No AttributeOverrides exist", "One AttributeOverride exist", "AttributeOverrides exist"});

        attributeEntity.setColumns(Arrays.asList(
                new Column("OBJECT", false, true, Object.class),
                new Column("Attribute Name", false, String.class),
                new Column("Column Name", false, String.class)
        ));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = attributeOverridesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                Set<AttributeOverride> attributeOverrides = attributeOverridesSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<? extends AttributeOverride> itr = attributeOverrides.iterator();
                while (itr.hasNext()) {
                    AttributeOverride attributeOverride = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = attributeOverride;
                    row[1] = attributeOverride.getName();
                    row[2] = attributeOverride.getColumn() != null ? attributeOverride.getColumn().getName() : EMPTY;//for representation
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
                attributeOverridesSpec.clear();
                data.stream().forEach((row) -> {
                    AttributeOverride attributeOverride = (AttributeOverride) row[0];
                    attributeOverridesSpec.add(attributeOverride);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getAssociationOverridesProperty(String id, String name, String desc, JPAModelerScene modelerScene, final Set<AssociationOverride> associationOverridesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No AssociationOverrides exist", "One AssociationOverride exist", "AssociationOverrides exist"});

        attributeEntity.setColumns(Arrays.asList(
                new Column("OBJECT", false, true, Object.class),
                new Column("Association Name", false, String.class),
                new Column("JoinTable Name", false, String.class),
                new Column("JoinColumn Size", false, Integer.class)
        ));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = associationOverridesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                Set<AssociationOverride> associationOverrides = associationOverridesSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<? extends AssociationOverride> itr = associationOverrides.iterator();
                while (itr.hasNext()) {
                    AssociationOverride attributeOverride = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = attributeOverride;
                    row[1] = attributeOverride.getName();
                    row[2] = attributeOverride.getJoinTable().getName();
                    row[3] = attributeOverride.getJoinColumn().size();
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
                associationOverridesSpec.clear();
                data.stream().forEach((row) -> {
                    AssociationOverride associationOverride = (AssociationOverride) row[0];
                    associationOverridesSpec.add(associationOverride);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getPrimaryKeyJoinColumnsProperty(String id, String name, String desc, EntityWidget entityWidget, Entity entity) {
        JPAModelerScene modelerScene = entityWidget.getModelerScene();
        final List<? extends PrimaryKeyJoinColumn> primaryKeyJoinColumnsSpec = entity.getPrimaryKeyJoinColumn();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No PrimaryKeyJoinColumns exist", "One PrimaryKeyJoinColumn exist", "PrimaryKeyJoinColumns exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new PrimaryKeyJoinColumnPanel(entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = primaryKeyJoinColumnsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<? extends PrimaryKeyJoinColumn> primaryKeyJoinColumns = primaryKeyJoinColumnsSpec;
                List<Object[]> data_local = new LinkedList<>();
                Iterator<? extends PrimaryKeyJoinColumn> itr = primaryKeyJoinColumns.iterator();
                while (itr.hasNext()) {
                    PrimaryKeyJoinColumn primaryKeyJoinColumn = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = primaryKeyJoinColumn;
                    row[1] = primaryKeyJoinColumn.getName();
                    row[2] = primaryKeyJoinColumn.getReferencedColumnName();
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
                primaryKeyJoinColumnsSpec.clear();
                data.stream().forEach((row) -> {
                    ((List<PrimaryKeyJoinColumn>) primaryKeyJoinColumnsSpec).add((PrimaryKeyJoinColumn) row[0]);
                });
                this.data = data;
            }

        });

        entityWidget.addPropertyVisibilityHandler("PrimaryKeyJoinColumns", () -> {
            InheritanceStateType inheritanceState = entityWidget.getInheritanceState();
            return inheritanceState == InheritanceStateType.BRANCH || inheritanceState == InheritanceStateType.LEAF;
        });
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getResultSetMappingsProperty(JPAModelerScene modelerScene, final Entity entity) {
        final Set<SqlResultSetMapping> sqlResultSetMappingSpec = entity.getSqlResultSetMapping();
        final NAttributeEntity attributeEntity = new NAttributeEntity("ResultSetMappings", "ResultSet Mappings", getMessage(PropertiesHandler.class, "INFO_RESULTSET_MAPPING"));

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

    public static PropertySupport getNamedStoredProcedureQueryProperty(JPAModelerScene modelerScene, Entity entity) {
        final List<NamedStoredProcedureQuery> namedStoredProcedureQueriesSpec = entity.getNamedStoredProcedureQuery();
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedStoredProcedureQueries", "Named StoredProcedure Queries", getMessage(PropertiesHandler.class, "INFO_STORED_PROCEDURE_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No NamedStoredProcedureQueries exist", "One NamedStoredProcedureQuery exist", "NamedStoredProcedureQueries exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
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
                    row[1] = namedStoredProcedureQuery.isEnable();
                    row[2] = namedStoredProcedureQuery.getName();
                    row[3] = namedStoredProcedureQuery.getProcedureName();
                    row[4] = namedStoredProcedureQuery.getParameter().size();
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
                    NamedStoredProcedureQuery procedureQuery = (NamedStoredProcedureQuery) row[0];
                    procedureQuery.setEnable((boolean) row[1]);
                    namedStoredProcedureQueriesSpec.add(procedureQuery);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedQueryProperty(JPAModelerScene modelerScene, IdentifiableClass identifiableClass) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedQueries", "Named Queries", getMessage(PropertiesHandler.class, "INFO_JPQL_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No NamedQueries exist", "One NamedQuery exist", "NamedQueries exist"});
        final List<NamedQuery> namedQueriesSpec = identifiableClass.getNamedQuery();

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Lock Mode Type", false, true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedQueryPanel(identifiableClass, modelerScene.getModelerFile()));

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
                    row[1] = namedQuery.isEnable();
                    row[2] = getShortQueryName(identifiableClass, namedQuery.getName());
                    row[3] = namedQuery.getQuery();
                    row[4] = namedQuery.getLockMode();
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
                    NamedQuery namedQuery = (NamedQuery) row[0];
                    namedQuery.setEnable((boolean) row[1]);
                    namedQueriesSpec.add(namedQuery);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getCustomAnnoation(JPAModelerScene modelerScene, List<Annotation> annotations) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("Annotations", "Annotations", "");
        attributeEntity.setCountDisplay(new String[]{"No Annotations exist", "One Annotation exist", "Annotations exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Annoation", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new AnnotationPanel(modelerScene.getModelerFile()));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = annotations.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<Annotation> itr = annotations.iterator();
                while (itr.hasNext()) {
                    Annotation annotation = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = annotation;
                    row[1] = annotation.isEnable();
                    row[2] = annotation.getName();
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
                annotations.clear();
                data.stream().forEach((row) -> {
                    Annotation annotationElement = (Annotation) row[0];
                    annotationElement.setEnable((boolean) row[1]);
                    annotations.add(annotationElement);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getCustomArtifact(JPAModelerScene modelerScene, Set<ReferenceClass> referenceClasses, String artifactType) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(artifactType, artifactType, "");
        attributeEntity.setCountDisplay(new String[]{String.format("No %s exist", artifactType), String.format("One %s exist", artifactType), String.format("%s exist", artifactType)});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column(artifactType, false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JavaClassArtifactPanel(modelerScene.getModelerFile(), artifactType));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = referenceClasses.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<ReferenceClass> itr = referenceClasses.iterator();
                while (itr.hasNext()) {
                    ReferenceClass referenceClass = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = referenceClass;
                    row[1] = referenceClass.isEnable();
                    row[2] = referenceClass.getName();
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
                referenceClasses.clear();
                data.stream().forEach((row) -> {
                    ReferenceClass referenceClass = (ReferenceClass) row[0];
                    referenceClass.setEnable((boolean) row[1]);
                    referenceClasses.add(referenceClass);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static EmbeddedPropertySupport getCustomParentClass(JavaClassWidget<? extends JavaClass> javaClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("extends", "Extends", getMessage(PropertiesHandler.class, "INFO_EXTENDS_CLASS"));
        entity.setEntityEditor(new ClassExtendPanel(javaClassWidget.getModelerScene().getModelerFile()));
        entity.setDataListener(new EmbeddedDataListener<ReferenceClass>() {
            private JavaClass javaClass;

            @Override
            public void init() {
                javaClass = javaClassWidget.getBaseElementSpec();
            }

            @Override
            public ReferenceClass getData() {
                return javaClass.getSuperclassRef();
            }

            @Override
            public void setData(ReferenceClass referenceClass) {
                javaClass.setSuperclassRef(referenceClass);
            }

            @Override
            public String getDisplay() {
                ReferenceClass referenceClass = javaClass.getSuperclassRef();
                if (referenceClass == null) {
                    return EMPTY;
                } else {
                    return JavaIdentifiers.unqualifyGeneric(referenceClass.getName());
                }
            }

        });
        javaClassWidget.addPropertyVisibilityHandler("extends", () -> {
            InheritanceStateType inheritanceStateType = javaClassWidget.getInheritanceState();
            return inheritanceStateType != InheritanceStateType.BRANCH && inheritanceStateType != InheritanceStateType.LEAF;
        });
        return new EmbeddedPropertySupport(javaClassWidget.getModelerScene().getModelerFile(), entity);
    }

    public static PropertySupport getClassSnippet(JPAModelerScene modelerScene, List<ClassSnippet> snippets) {
        return getCustomSnippet(modelerScene, snippets, ClassSnippet.class, ClassSnippetLocationType.class);
    }

    public static PropertySupport getAttributeSnippet(JPAModelerScene modelerScene, List<AttributeSnippet> snippets) {
        return getCustomSnippet(modelerScene, snippets, AttributeSnippet.class, AttributeSnippetLocationType.class);
    }

    public static <T extends Snippet> PropertySupport getCustomSnippet(JPAModelerScene modelerScene, List<T> snippets, Class<T> snippetType, Class<? extends SnippetLocation> snippetLocationType) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("Snippets", "Snippets", "");
        attributeEntity.setCountDisplay(new String[]{"No Snippets exist", "One Snippet exist", "Snippets exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Snippet", false, String.class));
        columns.add(new Column("Location", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new CustomSnippetPanel(modelerScene.getModelerFile(), snippetType, snippetLocationType));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = snippets.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<T> itr = snippets.iterator();
                while (itr.hasNext()) {
                    T snippet = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = snippet;
                    row[1] = snippet.isEnable();
                    row[2] = snippet.getValue();
                    row[3] = snippet.getLocationType().getTitle();
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
                snippets.clear();
                data.stream().forEach((row) -> {
                    T snippet = (T) row[0];
                    snippet.setEnable((boolean) row[1]);
                    snippets.add(snippet);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static String getShortQueryName(IdentifiableClass identifiableClass, String queryFullName) {
        String clazz = identifiableClass.getClazz();
        if (queryFullName.startsWith(clazz + '.')) {
            return queryFullName.substring(clazz.length() + 1);
        } else {
            return queryFullName;
        }
    }

    public static PropertySupport getNamedEntityGraphProperty(final EntityWidget entityWidget) {
        JPAModelerScene modelerScene = entityWidget.getModelerScene();
        final List<NamedEntityGraph> entityGraphsSpec = entityWidget.getBaseElementSpec().getNamedEntityGraph();
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedEntityGraphs", "Named Entity Graphs", getMessage(PropertiesHandler.class, "INFO_ENTITY_GRAPH"));
        attributeEntity.setCountDisplay(new String[]{"No EntityGraphs exist", "One EntityGraph exist", "EntityGraphs exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
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
                    row[1] = entityGraph.isEnable();
                    row[2] = entityGraph.getName();
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
                    NamedEntityGraph entityGraph = (NamedEntityGraph) row[0];
                    entityGraph.setEnable((boolean) row[1]);
                    entityGraphsSpec.add(entityGraph);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedNativeQueryProperty(JPAModelerScene modelerScene, final Entity entity) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedNativeQueries", "Named Native Queries", getMessage(PropertiesHandler.class, "INFO_NATIVE_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No Named Native Queries exist", "One Named Native Query exist", "Named Native Queries exist"});
        List<NamedNativeQuery> namedNativeQueriesSpec = entity.getNamedNativeQuery();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
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
                    row[1] = namedNativeQuery.isEnable();
                    row[2] = namedNativeQuery.getName();
                    row[3] = namedNativeQuery.getQuery();
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
                    NamedNativeQuery nativeQuery = (NamedNativeQuery) row[0];
                    nativeQuery.setEnable((boolean) row[1]);
                    namedNativeQueriesSpec.add(nativeQuery);
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

    public static EmbeddedPropertySupport getInheritanceProperty(EntityWidget entityWidget) {

        GenericEmbedded entity = new GenericEmbedded("inheritance", "Inheritance", "");
        try {
            entity.setEntityEditor(new InheritancePanel(entityWidget.getModelerScene().getModelerFile(), entityWidget));
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setDataListener(new EmbeddedDataListener<InheritanceHandler>() {
            private InheritanceHandler classSpec;

            @Override
            public void init() {
                classSpec = (InheritanceHandler) entityWidget.getBaseElementSpec();
            }

            @Override
            public InheritanceHandler getData() {
                return classSpec;
            }

            @Override
            public void setData(InheritanceHandler classSpec) {
                entityWidget.scanDiscriminatorValue();
                entityWidget.getSubclassWidgets()
                        .stream()
                        .filter(sw -> sw instanceof EntityWidget)
                        .map(sw -> (EntityWidget)sw)
                        .forEach(sw -> sw.scanDiscriminatorValue());
            }

            @Override
            public String getDisplay() {
                InheritanceStateType inheritencaState = entityWidget.getInheritanceState();
                if (null != inheritencaState) {
                    switch (inheritencaState) {
                        case LEAF:
                            EntityWidget superEntityWidget = (EntityWidget) entityWidget.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
                            InheritanceHandler superClassSpec = (InheritanceHandler) superEntityWidget.getBaseElementSpec();
                            if (superClassSpec.getInheritance() != null && superClassSpec.getInheritance().getStrategy() != null) {
                                return superClassSpec.getInheritance().getStrategy().toString();
                            } else {
                                return InheritanceType.SINGLE_TABLE.toString();
                            }
                        case ROOT:
                        case BRANCH:
                            if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                                return classSpec.getInheritance().getStrategy().toString();
                            } else {
                                return InheritanceType.SINGLE_TABLE.toString();
                            }
                    }
                }
                return EMPTY;
            }

        });

        entityWidget.addPropertyVisibilityHandler("inheritance", () -> {
            return entityWidget.getInheritanceState() != InheritanceStateType.SINGLETON;
        });
        return new EmbeddedPropertySupport(entityWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getHashcodeEqualsProperty(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("hashcode_equals", "equals() & hashcode()", "Define equals & hashcode implementation for the Entity");

        final JavaClass javaClassObj = persistenceClassWidget.getBaseElementSpec();
        HashcodeEqualsPanel panel = new HashcodeEqualsPanel(persistenceClassWidget);
        panel.postConstruct();
        entity.setEntityEditor(panel);
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
                int hashcode = javaClass.getHashCodeMethod().getAttributes().size();
                int equals = javaClass.getEqualsMethod().getAttributes().size();
                return String.format("equals{%s} hashcode{%s}", equals, hashcode);
            }

        });
        return new EmbeddedPropertySupport(persistenceClassWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getToStringProperty(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("toString", "toString()", getMessage(ClassMemberPanel.class, "LBL_tostring_select"));
        final ClassMembers classMembersObj = persistenceClassWidget.getBaseElementSpec().getToStringMethod();
        ClassMemberPanel classMemberPanel = new ClassMemberPanel(getMessage(ClassMemberPanel.class, "LBL_tostring_select"), persistenceClassWidget, false);
        classMemberPanel.postConstruct();
        entity.setEntityEditor(classMemberPanel);
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
        List<Constructor> constructors = persistenceClassWidget.getBaseElementSpec().getConstructors();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("#", true, Boolean.class));
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
                    row[1] = constructor.isEnable();
                    row[2] = constructor.toString();
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
                    Constructor constructorElement = (Constructor) row[0];
                    constructorElement.setEnable((boolean) row[1]);
                    constructors.add(constructorElement);
                });
                //add no-arg constructor, if no-arg constructor not exist and other constructor exist
                if (!constructors.isEmpty() && !constructors.stream().anyMatch(con -> con.getAttributes().isEmpty())) {
                    constructors.add(Constructor.getNoArgsInstance());
                }

                //Enable no-args constructor and disable other no-args constructor , if more then one are available 
                if (!constructors.isEmpty()) {
                    List<Constructor> noArgsConstructors = constructors.stream().filter(con -> con.isNoArgs()).collect(toList());
                    noArgsConstructors.stream().forEach(con -> con.setEnable(false));
                    noArgsConstructors.get(0).setEnable(true);
                    noArgsConstructors.get(0).setAccessModifier(AccessModifierType.PUBLIC);
                }

                this.data = data;
            }

        });

        return new NEntityPropertySupport(persistenceClassWidget.getModelerScene().getModelerFile(), attributeEntity);
    }

    public static EmbeddedPropertySupport getGeneratorProperty(IdAttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded("generator", "Id Generator", "");
        entity.setEntityEditor(new IdGeneratorPanel());

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
                if (attributeWidget.getClassWidget() instanceof EntityWidget) {
                    ((EntityWidget) attributeWidget.getClassWidget()).scanKeyError();
                } else {
                    attributeWidget.getClassWidget().getAllSubclassWidgets().stream()
                            .filter(cw -> cw instanceof EntityWidget).findFirst().ifPresent(ew -> ((EntityWidget) ew).scanKeyError());
                }

//                attributeWidget.setBaseElementSpec(classSpec);
            }

            @Override
            public String getDisplay() {
                if (idAttribute.getGeneratedValue() != null && idAttribute.getGeneratedValue().getStrategy() != null) {
                    return firstLetterCaps(idAttribute.getGeneratedValue().getStrategy().toString());
                } else if (idAttribute.getGeneratedValue() == null || idAttribute.getGeneratedValue().getStrategy() == null) {
                    return NONE_TYPE;
                } else {
                    return "";
                }
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getFieldTypeProperty(String id, String name, String description, boolean mapKey,
            AttributeWidget attributeWidget) {

        GenericEmbedded property = new GenericEmbedded(id, name, description);

        if (mapKey) {
            property.setEntityEditor(new FieldTypePanel(attributeWidget.getModelerScene().getModelerFile(), true));
            property.setAfter("mapKeyType");
        } else {
            if (attributeWidget.getBaseElementSpec() instanceof BaseAttribute) {
                if (attributeWidget.getBaseElementSpec() instanceof ElementCollection && ((ElementCollection) attributeWidget.getBaseElementSpec()).getConnectedClass() != null) {//SingleValueEmbeddableFlowWidget
                    property.setEntityEditor(null);
                } else if (attributeWidget.getBaseElementSpec() instanceof Embedded) {//to Disable it
                    property.setEntityEditor(null);
                } else {
                    property.setEntityEditor(new FieldTypePanel(attributeWidget.getModelerScene().getModelerFile(), false));
                    property.setBefore("collectionType");
                }

            } else if (attributeWidget.getBaseElementSpec() instanceof RelationAttribute) {
                property.setEntityEditor(null);
                property.setBefore("collectionType");
            }
        }
        property.setDataListener(new EmbeddedDataListener<Attribute>() {
            private Attribute attribute;

            @Override
            public void init() {
                attribute = (Attribute) attributeWidget.getBaseElementSpec();
            }

            @Override
            public Attribute getData() {
                return attribute;
            }

            @Override
            public void setData(Attribute baseAttribute) {
//                if (attributeWidget instanceof BaseAttributeWidget) {
//                    ((BaseAttributeWidget)attributeWidget).createBeanValidationPropertySet(attributeWidget.getPropertyManager().getElementPropertySet());
//                }
                if (mapKey) {
                    AttributeValidator.scanMapKeyHandlerError(attributeWidget);
                }
                attributeWidget.refreshProperties();
                attributeWidget.visualizeDataType();
            }

            @Override
            public String getDisplay() {
                if (mapKey) {
                    return JavaSourceHelper.getSimpleClassName(((MapKeyHandler) attribute).getMapKeyDataTypeLabel());
                } else {
                    return JavaSourceHelper.getSimpleClassName(attribute.getDataTypeLabel());
                }
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), property);
    }

    public static EmbeddedPropertySupport getCascadeProperty(RelationAttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded("cascadeType", "Cascade Type", "");
        entity.setEntityEditor(new CascadeTypePanel(attributeWidget.getModelerScene().getModelerFile()));

        entity.setDataListener(new EmbeddedDataListener<CascadeType>() {
            private RelationAttribute relationAttribute;

            @Override
            public void init() {
                relationAttribute = (RelationAttribute) attributeWidget.getBaseElementSpec();
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
                    display.append(NONE_TYPE);
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

    public static EmbeddedPropertySupport getOrderProperty(AttributeWidget attributeWidget) {

        GenericEmbedded entity = new GenericEmbedded("order", "Order", "");
        OrderPanel orderPanel = new OrderPanel(attributeWidget);
        entity.setEntityEditor(orderPanel);

        entity.setDataListener(new EmbeddedDataListener<SortableAttribute>() {
            private SortableAttribute sortableAttribute;

            @Override
            public void init() {
                sortableAttribute = (SortableAttribute) attributeWidget.getBaseElementSpec();
            }

            @Override
            public SortableAttribute getData() {
                return sortableAttribute;
            }

            @Override
            public void setData(SortableAttribute attribute) {
            }

            @Override
            public String getDisplay() {
                return OrderPanel.getStateDisplay(sortableAttribute);
            }

        });
        return new EmbeddedPropertySupport(attributeWidget.getModelerScene().getModelerFile(), entity);
    }

    public static ComboBoxPropertySupport getCacheableProperty(EntityWidget entityWidget) {
        Entity entity = entityWidget.getBaseElementSpec();
        ComboBoxListener<Boolean> comboBoxListener = new ComboBoxListener<Boolean>() {
            @Override
            public void setItem(ComboBoxValue<Boolean> value) {
                entity.setCacheable(value.getValue());
            }

            @Override
            public ComboBoxValue<Boolean> getItem() {
                if (entity.getCacheable() != null) {
                    return new ComboBoxValue<>(entity.getCacheable(), entity.getCacheable()
                            ? getMessage(PropertiesHandler.class, "LBL_ENABLE")
                            : getMessage(PropertiesHandler.class, "LBL_FORCE_DISABLE"));
                } else {
                    return new ComboBoxValue<>(null, getMessage(PropertiesHandler.class, "LBL_DISABLE"));
                }
            }

            @Override
            public List<ComboBoxValue<Boolean>> getItemList() {
                ComboBoxValue<Boolean>[] values;
                values = new ComboBoxValue[]{
                    new ComboBoxValue<>(null, getMessage(PropertiesHandler.class, "LBL_DISABLE")),
                    new ComboBoxValue<>(true, getMessage(PropertiesHandler.class, "LBL_ENABLE")),
                    new ComboBoxValue<>(false, getMessage(PropertiesHandler.class, "LBL_FORCE_DISABLE"))};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return getMessage(PropertiesHandler.class, "LBL_DISABLE");
            }
        };
        return new ComboBoxPropertySupport(entityWidget.getModelerScene().getModelerFile(), "cacheable", "Cacheable", getMessage(PropertiesHandler.class, "INFO_CACHEABLE"), comboBoxListener);
    }

    public static PropertySupport getConverterProperties(JPAModelerScene scene, List<Converter> converters) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("converters", "Converters", getMessage(PropertiesHandler.class, "INFO_COVERTER"));
        attributeEntity.setCountDisplay(new String[]{"No Converter exist", "One Converter exist", "Converters exist"});
        attributeEntity.setCustomDialog(new ConverterPanel(scene.getModelerFile()));

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Converter Class", false, String.class));
        columns.add(new Column("Attribute Type", false, String.class));
        columns.add(new Column("DB Field Type", false, String.class));
        columns.add(new Column("Auto Apply", true, Boolean.class));
        attributeEntity.setColumns(columns);

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = converters.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<Converter> itr = converters.iterator();
                while (itr.hasNext()) {
                    Converter converter = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = converter;
                    row[1] = converter.getClazz();
                    row[2] = converter.getAttributeType();
                    row[3] = converter.getFieldType();
                    row[4] = converter.isAutoApply();
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
                converters.clear();
                converters.addAll(data.stream().map((row) -> {
                    Converter converter = (Converter) row[0];
                    converter.setAutoApply((boolean) row[4]);
                    return converter;
                }).collect(toList()));
                this.data = data;
            }

        });

        return new NEntityPropertySupport(scene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getConvertProperties(JPAModelerScene scene, ConvertContainerHandler convertContainer) {
        final List<Convert> converts = convertContainer.getConverts();
        final NAttributeEntity attributeEntity = getConvertPropertiesEntity("converts", "Converts", "Converts", converts);
        attributeEntity.setCustomDialog(new OverrideConvertPanel(scene.getModelerFile(), convertContainer, false));
        return new NEntityPropertySupport(scene.getModelerFile(), attributeEntity);
    }

    private static PropertyVisibilityHandler getMapKeyConvertVisibilityHandler(AttributeWidget<? extends Attribute> attributeWidget, Predicate<MapKeyHandler> filter) {
        PropertyVisibilityHandler mapKeyVisibility = AttributeWidget.getMapKeyVisibilityHandler(attributeWidget.getBaseElementSpec());
        return () -> {
            if (mapKeyVisibility.isVisible()) {
                MapKeyHandler handler = (MapKeyHandler) attributeWidget.getBaseElementSpec();
                return handler.getValidatedMapKeyType() == MapKeyType.NEW && filter.test(handler);
            }
            return false;
        };
    }

    public static PropertySupport getMapKeyConvertProperties(AttributeWidget<? extends Attribute> attributeWidget, JPAModelerScene scene, MapKeyConvertContainerHandler convertContainer) {
        attributeWidget.addPropertyVisibilityHandler("mapKeyConverts", getMapKeyConvertVisibilityHandler(attributeWidget, handler -> handler.getMapKeyEmbeddable() != null));
        final List<Convert> converts = convertContainer.getMapKeyConverts();
        final NAttributeEntity attributeEntity = getConvertPropertiesEntity("mapKeyConverts", "MapKey Converts", "MapKey Converts", converts);
        attributeEntity.setCustomDialog(new OverrideConvertPanel(scene.getModelerFile(), convertContainer, true));
        return new NEntityPropertySupport(scene.getModelerFile(), attributeEntity);
    }

    private static NAttributeEntity getConvertPropertiesEntity(String id, String name, String description, final List<Convert> converts) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, description);
        attributeEntity.setCountDisplay(new String[]{"No Convert exist", "One Convert exist", "Converts exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Converter Class", false, String.class));
        columns.add(new Column("AttributeName", false, String.class));
        columns.add(new Column("Disable Global Conversion", true, Boolean.class));
        attributeEntity.setColumns(columns);

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = converts.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<Convert> itr = converts.iterator();
                while (itr.hasNext()) {
                    Convert convert = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = convert;
                    row[1] = convert.getConverter();
                    row[2] = convert.getAttributeName();
                    row[3] = convert.isDisableConversion();
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
                converts.clear();
                converts.addAll(data.stream().map((row) -> {
                    Convert convert = (Convert) row[0];
                    convert.setDisableConversion((boolean) row[3]);
                    return convert;
                }).collect(toList()));
                this.data = data;
            }

        });

        return attributeEntity;
    }

    public static EmbeddedPropertySupport getConvertProperty(AttributeWidget<? extends Attribute> attributeWidget, JPAModelerScene scene, final ConvertHandler convertHandler) {
        TemporalTypeHandler temporalType = (TemporalTypeHandler) attributeWidget.getBaseElementSpec();
        EnumTypeHandler enumType = (EnumTypeHandler) attributeWidget.getBaseElementSpec();
        attributeWidget.addPropertyVisibilityHandler("convert", () -> temporalType.getTemporal() == null && enumType.getEnumerated() == null);
        return getConvertPropertySupport("convert", "Convert", "Convert", null, scene, () -> convertHandler.getConvert());
    }

    public static EmbeddedPropertySupport getMapKeyConvertProperty(AttributeWidget<? extends Attribute> attributeWidget, JPAModelerScene scene, final MapKeyConvertHandler convertHandler) {
        attributeWidget.addPropertyVisibilityHandler("mapKeyConvert", getMapKeyConvertVisibilityHandler(attributeWidget, handler
                -> handler.getMapKeyAttributeType() != null && handler.getMapKeyTemporal() == null && handler.getMapKeyEnumerated() == null));
        //reset/clear unused in list with convertHandler.getMapKeyConvert()
        return getConvertPropertySupport("mapKeyConvert", "MapKey Convert", "MapKey Convert", "key", scene, () -> convertHandler.getMapKeyConvert());
    }

    private static EmbeddedPropertySupport getConvertPropertySupport(String id, String name, String description,
            String keyAttribute, JPAModelerScene scene, final java.util.function.Supplier<Convert> convertProucer) { //Producer is used to get Convert and call getConvert/getMapKeyConvert everytime to clear unused converter list  
        GenericEmbedded entity = new GenericEmbedded(id, name, description);
        entity.setEntityEditor(new ConvertPanel(scene.getModelerFile()));
        entity.setDataListener(new EmbeddedDataListener<Convert>() {
            private Convert convert;

            @Override
            public void init() {
                convert = convertProucer.get();
            }

            @Override
            public Convert getData() {
                return convert;
            }

            @Override
            public void setData(Convert convert) {
                if (keyAttribute != null) {
                    convert.setAttributeName(keyAttribute);
                }
                convertProucer.get();//remove all unused converter item from list except at index 0
                //skip
            }

            @Override
            public String getDisplay() {
                if (ConvertValidator.isEmpty(convert)) {
                    return NONE_TYPE;
                } else {
                    return isNotBlank(convert.getConverter()) ? convert.getConverter() : "Disable Conversion";
                }
            }

        });
        return new EmbeddedPropertySupport(scene.getModelerFile(), entity);
    }

//    public static PropertySupport getWorkSpaceProperty(final JPAModelerScene modelerScene) {
//        final List<WorkSpace> workSpaces = modelerScene.getBaseElementSpec().getWorkSpaces();
//        final NAttributeEntity attributeEntity = new NAttributeEntity("WorkSpaces", "WorkSpace", "");//getMessage(PropertiesHandler.class, "INFO_ENTITY_GRAPH"));
//        attributeEntity.setCountDisplay(new String[]{"No WorkSpace exist", "One WorkSpace exist", "WorkSpace exist"});
//
//        List<Column> columns = new ArrayList<>();
//        columns.add(new Column("OBJECT", false, true, Object.class));
////        columns.add(new Column("#", true, Boolean.class));
//        columns.add(new Column("Name", false, String.class));
//        attributeEntity.setColumns(columns);
//        attributeEntity.setCustomDialog(new NamedEntityGraphPanel(entityWidget));
//
//        attributeEntity.setTableDataListener(new NEntityDataListener() {
//            List<Object[]> data;
//            int count;
//
//            @Override
//            public void initCount() {
//                count = workSpaces.size();
//            }
//
//            @Override
//            public int getCount() {
//                return count;
//            }
//
//            @Override
//            public void initData() {
//                List<Object[]> data_local = new LinkedList<>();
//                Iterator<WorkSpace> itr = workSpaces.iterator();
//                while (itr.hasNext()) {
//                    WorkSpace workSpace = itr.next();
//                    Object[] row = new Object[attributeEntity.getColumns().size()];
//                    row[0] = workSpace;
//                    data_local.add(row);
//                }
//                this.data = data_local;
//            }
//
//            @Override
//            public List<Object[]> getData() {
//                return data;
//            }
//
//            @Override
//            public void setData(List<Object[]> data) {
//                workSpaces.clear();
//                data.stream().forEach((row) -> {
//                    WorkSpace workSpace = (WorkSpace) row[0];
//                    workSpaces.add(workSpace);
//                });
//                this.data = data;
//            }
//
//        });
//
//        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
//    }

}
