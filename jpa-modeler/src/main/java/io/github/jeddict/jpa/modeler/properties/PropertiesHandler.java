/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.properties;

import io.github.jeddict.jaxb.spec.JaxbVariableType;
import static io.github.jeddict.jaxb.spec.JaxbVariableType.XML_DEFAULT;
import io.github.jeddict.jaxb.spec.JaxbVariableTypeHandler;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.jcode.util.JavaSourceHelper;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import io.github.jeddict.jpa.modeler.properties.annotation.AnnotationPanel;
import io.github.jeddict.jpa.modeler.properties.cascade.CascadeTypePanel;
import io.github.jeddict.jpa.modeler.properties.classmember.ClassMemberPanel;
import io.github.jeddict.jpa.modeler.properties.classmember.ConstructorPanel;
import io.github.jeddict.jpa.modeler.properties.classmember.HashcodeEqualsPanel;
import io.github.jeddict.jpa.modeler.properties.convert.ConvertPanel;
import io.github.jeddict.jpa.modeler.properties.convert.ConverterPanel;
import io.github.jeddict.jpa.modeler.properties.convert.OverrideConvertPanel;
import io.github.jeddict.jpa.modeler.properties.custom.snippet.CustomSnippetPanel;
import io.github.jeddict.jpa.modeler.properties.entitygraph.NamedEntityGraphPanel;
import io.github.jeddict.jpa.modeler.properties.extend.ClassSelectionPanel;
import io.github.jeddict.jpa.modeler.properties.fieldtype.FieldTypePanel;
import io.github.jeddict.jpa.modeler.properties.idgeneration.IdGeneratorPanel;
import io.github.jeddict.jpa.modeler.properties.implement.JavaClassArtifactPanel;
import io.github.jeddict.jpa.modeler.properties.inheritance.InheritancePanel;
import io.github.jeddict.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import io.github.jeddict.jpa.modeler.properties.named.nativequery.NamedNativeQueryPanel;
import io.github.jeddict.jpa.modeler.properties.named.query.NamedQueryPanel;
import io.github.jeddict.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import io.github.jeddict.jpa.modeler.properties.named.storedprocedurequery.NamedStoredProcedureQueryPanel;
import io.github.jeddict.jpa.modeler.properties.order.OrderPanel;
import io.github.jeddict.jpa.modeler.properties.pkjoincolumn.PrimaryKeyJoinColumnPanel;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.InheritanceStateType;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.BRANCH;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.LEAF;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.BasicCollectionAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.IdAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.TransientAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MultiRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.spec.AccessType;
import static io.github.jeddict.jpa.spec.AccessType.FIELD;
import static io.github.jeddict.jpa.spec.AccessType.PROPERTY;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.CascadeType;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Converter;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.FetchType;
import io.github.jeddict.jpa.spec.GeneratedValue;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.InheritanceType;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.NamedEntityGraph;
import io.github.jeddict.jpa.spec.NamedNativeQuery;
import io.github.jeddict.jpa.spec.NamedQuery;
import io.github.jeddict.jpa.spec.NamedStoredProcedureQuery;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.SqlResultSetMapping;
import io.github.jeddict.jpa.spec.extend.AccessModifierType;
import io.github.jeddict.jpa.spec.extend.AccessTypeHandler;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.AttributeAnnotation;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.ClassAnnotation;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import io.github.jeddict.jpa.spec.extend.Constructor;
import io.github.jeddict.jpa.spec.extend.ConvertContainerHandler;
import io.github.jeddict.jpa.spec.extend.ConvertHandler;
import io.github.jeddict.jpa.spec.extend.EnumTypeHandler;
import io.github.jeddict.jpa.spec.extend.FetchTypeHandler;
import io.github.jeddict.jpa.spec.extend.InheritanceHandler;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.MapKeyConvertContainerHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyConvertHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyType;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.extend.SortableAttribute;
import io.github.jeddict.jpa.spec.extend.TemporalTypeHandler;
import io.github.jeddict.jpa.spec.extend.annotation.Annotation;
import io.github.jeddict.jpa.spec.validator.ConvertValidator;
import io.github.jeddict.snippet.AttributeSnippet;
import io.github.jeddict.snippet.ClassSnippet;
import io.github.jeddict.snippet.Snippet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import io.github.jeddict.util.StringUtils;
import static io.github.jeddict.util.StringUtils.EMPTY;
import static io.github.jeddict.util.StringUtils.isNotBlank;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.combobox.ActionHandler;
import org.netbeans.modeler.properties.combobox.ComboBoxListener;
import org.netbeans.modeler.properties.combobox.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;
import org.openide.nodes.PropertySupport;
import static org.openide.util.NbBundle.getMessage;
import org.openide.windows.WindowManager;

public class PropertiesHandler {

    public static final String NONE_TYPE = "< none >";

    @Deprecated //use enummy
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
                boolean valid = validateCollectionType(collectionType, colSpec.getCollectionImplType(), true);
                boolean nextValidation = valid;
                if (!nextValidation) {
                    try {
                        if (StringUtils.isNotBlank(collectionType)) {
                            if (java.util.Collection.class.isAssignableFrom(Class.forName(collectionType.trim()))
                                    || java.util.Map.class.isAssignableFrom(Class.forName(collectionType.trim()))) {
                                nextValidation = true;
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                        //skip allow = false;
                    }
                }
                if (!nextValidation) {
                    collectionType = java.util.Collection.class.getName();
                }

                colSpec.setCollectionType(collectionType);
                em.getCache().addCollectionClass(collectionType);
                
                if (!valid) {
                    attributeWidget.refreshProperties();
                }
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
                em.getCache().getCollectionClasses()
                        .stream()
                        .filter(StringUtils::isNotEmpty)
                        .forEach((collection) -> {
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
                    try {
                        if (Collection.class.isAssignableFrom(Class.forName(collectionType))
                                || Map.class.isAssignableFrom(Class.forName(collectionType))) {
                            return new ComboBoxValue<>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
                        }
                    } catch (ClassNotFoundException ex) {

                    }
                    throw new IllegalStateException("Invalid Collection/Map type");
                })
                        .afterCreation(e -> em.getCache().addCollectionClass(e.getValue()))
                        .afterDeletion(e -> em.getCache().getCollectionClasses().remove(e.getValue()))
                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you sue you want to delete this collection class ?", "Delete Collection Class", JOptionPane.OK_CANCEL_OPTION));
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "collectionType", "Collection Type", "", comboBoxListener);
    }
    
    public static ComboBoxPropertySupport getCollectionImplTypeProperty(AttributeWidget<? extends Attribute> attributeWidget, final CollectionTypeHandler colSpec) {
        JPAModelerScene modelerScene = attributeWidget.getModelerScene();
        EntityMappings em = modelerScene.getBaseElementSpec();
        ModelerFile modelerFile = modelerScene.getModelerFile();
        ComboBoxListener<String> comboBoxListener = new ComboBoxListener<String>() {
            private final Set<String> value = new HashSet<>();

            @Override
            public void setItem(ComboBoxValue<String> value) {
                setCollectionImplType(value);
            }

            void setCollectionImplType(ComboBoxValue<String> value) {
                String collectionImplType = value.getValue();
               boolean valid = validateCollectionType(colSpec.getCollectionType(), collectionImplType, false);
                if (valid) {
                    colSpec.setCollectionImplType(collectionImplType);
                    em.getCache().addCollectionImplClass(collectionImplType);
                } else if (StringUtils.isEmpty(collectionImplType)) {
                    colSpec.setCollectionImplType(null);
                }
            }

            @Override
            public ComboBoxValue<String> getItem() {
                if (!value.contains(colSpec.getCollectionImplType())) {
                    value.add(colSpec.getCollectionImplType());
                    em.getCache().addCollectionImplClass(colSpec.getCollectionImplType());
                }
                if (StringUtils.isNotEmpty(colSpec.getCollectionImplType())) {
                    return new ComboBoxValue(colSpec.getCollectionImplType(),
                            colSpec.getCollectionImplType().substring(colSpec.getCollectionImplType().lastIndexOf('.') + 1));
                } else {
                    return new ComboBoxValue(null, EMPTY);
                }
            }

            @Override
            public List<ComboBoxValue<String>> getItemList() {
                List<ComboBoxValue<String>> comboBoxValues = new ArrayList<>();
                comboBoxValues.add(new ComboBoxValue(null, ""));
                value.addAll(em.getCache().getCollectionImplClasses());
                em.getCache().getCollectionImplClasses()
                        .stream()
                        .filter(StringUtils::isNotEmpty)
                        .forEach(collection -> {
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
                    try {
                        if (Collection.class.isAssignableFrom(Class.forName(collectionType))
                                || Map.class.isAssignableFrom(Class.forName(collectionType))) {
                            return new ComboBoxValue<>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
                        }
                    } catch (ClassNotFoundException ex) {

                    }
                    throw new IllegalStateException("Invalid Collection/Map type");
                })
                        .afterCreation(e -> em.getCache().addCollectionImplClass(e.getValue()))
                        .afterDeletion(e -> em.getCache().getCollectionImplClasses().remove(e.getValue()))
                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you sue you want to delete this collection implementation class ?", "Delete Collection Implementation Class", JOptionPane.OK_CANCEL_OPTION));
            }
        };
        org.netbeans.modeler.config.element.Attribute attribute = new org.netbeans.modeler.config.element.Attribute("collectionImplType", "Collection Implementation Type", "");
        attribute.setAfter("collectionType");
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), attribute, comboBoxListener);
    }
    
    //changedValue could be collectionType or implType
    private static boolean validateCollectionType(String collectionType, String implType, boolean collectionTypeChanged){
         boolean valid = false;
         String changedValue = collectionTypeChanged ? collectionType : implType;
                try {
                    if (StringUtils.isNotBlank(changedValue)) {
                        if (java.util.Collection.class.isAssignableFrom(Class.forName(changedValue))
                                || java.util.Map.class.isAssignableFrom(Class.forName(changedValue))) {
                            valid = true;
                        }

                        if (StringUtils.isNotBlank(collectionType) && StringUtils.isNotBlank(implType)) {
                            Class type1Class = Class.forName(collectionType);
                            if (java.util.Collection.class.isAssignableFrom(type1Class)
                                    && !type1Class.isAssignableFrom(Class.forName(implType))) {
                                valid = false;
                            }
                            if (java.util.Map.class.isAssignableFrom(type1Class)
                                    && !type1Class.isAssignableFrom(Class.forName(implType))) {
                                valid = false;
                            }
                        }

                    }
                } catch (ClassNotFoundException ex) {
                    //skip allow = false;
                }
                if(!valid) {
                    if (StringUtils.isNotBlank(collectionType) && StringUtils.isNotBlank(implType)) {
                        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), 
                                String.format("Incompatible Collection type [%s] and Implementation type [%s]", collectionType, implType), 
                                "Incompatible types", ERROR_MESSAGE);
                    } else if(!collectionTypeChanged && StringUtils.isBlank(implType) ){
                        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), 
                                "Set collection implementation type to generate add/remove method", 
                                "Add/Remove method", ERROR_MESSAGE);
                    }
                }
        return valid;
    }

    public static ComboBoxPropertySupport getMapKeyProperty(AttributeWidget<? extends Attribute> attributeWidget, final MapKeyHandler mapKeyHandler, PropertyVisibilityHandler mapKeyVisibilityHandler) {
        JPAModelerScene modelerScene = attributeWidget.getModelerScene();
        ComboBoxListener<Attribute> comboBoxListener = new ComboBoxListener<Attribute>() {

            @Override
            public void setItem(ComboBoxValue<Attribute> value) {
                Attribute newType = value.getValue();
                mapKeyHandler.setMapKeyAttribute(newType);
                AttributeValidator.scanMapKeyHandlerError(attributeWidget);
                attributeWidget.setAttributeTooltip();
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
                    classWidget = ((MultiRelationAttributeWidget)attributeWidget).getConnectedClassWidget();
                } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                    classWidget = ((MultiValueEmbeddedAttributeWidget)attributeWidget).getEmbeddableFlowWidget().getTargetEmbeddableWidget();
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
                if (entity.getLabelAttribute() != null) {
                    return new ComboBoxValue(entity.getLabelAttribute(), entity.getLabelAttribute().getName());
                } else {
                    return new ComboBoxValue(null, EMPTY);
                }
            }

            @Override
            public List<ComboBoxValue<Attribute>> getItemList() {
                List<ComboBoxValue<Attribute>> comboBoxValues = new ArrayList<>();
                comboBoxValues.add(new ComboBoxValue(null, EMPTY));
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
        org.netbeans.modeler.config.element.Attribute attribute = new org.netbeans.modeler.config.element.Attribute("labelRef", "UI Display Reference", "Select the attribute to represent the entity in UI");
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
                    return new ComboBoxValue(null, "Default");
                }
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                return Arrays.asList(
                        new ComboBoxValue(null, "Default"),
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
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JoinColumnPanel(entity));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(joinColumnsSpec,
                t -> Arrays.asList(t.getName(), t.getReferencedColumnName())));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getAttributeOverridesProperty(JPAModelerScene modelerScene, final Set<AttributeOverride> attributeOverridesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("AttributeOverrides", "Attribute Overrides", "");
        attributeEntity.setCountDisplay(new String[]{"No AttributeOverrides exist", "One AttributeOverride exist", "AttributeOverrides exist"});

        attributeEntity.setColumns(Arrays.asList(
                new Column("Attribute Name", false, String.class),
                new Column("Column Name", false, String.class)
        ));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(attributeOverridesSpec,
                t -> Arrays.asList(t.getName(), t.getColumn() != null ? t.getColumn().getName() : EMPTY)));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getAssociationOverridesProperty(JPAModelerScene modelerScene, final Set<AssociationOverride> associationOverridesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("AssociationOverrides", "Association Overrides", "");
        attributeEntity.setCountDisplay(new String[]{"No AssociationOverrides exist", "One AssociationOverride exist", "AssociationOverrides exist"});

        attributeEntity.setColumns(Arrays.asList(
                new Column("Association Name", false, String.class),
                new Column("JoinTable Name", false, String.class),
                new Column("JoinColumn Size", false, Integer.class)
        ));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(associationOverridesSpec,
                (t) -> Arrays.asList(t.getName(), t.getJoinTable().getName(), t.getJoinColumn().size())));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getPrimaryKeyJoinColumnsProperty(EntityWidget entityWidget, Entity entity) {
        JPAModelerScene modelerScene = entityWidget.getModelerScene();
        final List<? extends PrimaryKeyJoinColumn> primaryKeyJoinColumnsSpec = entity.getPrimaryKeyJoinColumn();
        final NAttributeEntity attributeEntity = new NAttributeEntity("PrimaryKeyJoinColumns", "PrimaryKey Join Columns", "");
        attributeEntity.setCountDisplay(new String[]{"No PrimaryKeyJoinColumns exist", "One PrimaryKeyJoinColumn exist", "PrimaryKeyJoinColumns exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new PrimaryKeyJoinColumnPanel(entity));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(primaryKeyJoinColumnsSpec,
                t -> Arrays.asList(t.getName(), t.getReferencedColumnName())));
        entityWidget.addPropertyVisibilityHandler("PrimaryKeyJoinColumns", () -> {
            InheritanceStateType state = entityWidget.getInheritanceState();
            return (state == BRANCH || state == LEAF) && !entity.getNoSQL();
        });
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getResultSetMappingsProperty(JPAModelerScene modelerScene, final Entity entity) {
        final Set<SqlResultSetMapping> sqlResultSetMappingSpec = entity.getSqlResultSetMapping();
        final NAttributeEntity attributeEntity = new NAttributeEntity("ResultSetMappings", "ResultSet Mappings", getMessage(PropertiesHandler.class, "INFO_RESULTSET_MAPPING"));

        attributeEntity.setCountDisplay(new String[]{"No ResultSet Mappings", "One ResultSet Mapping", " ResultSet Mappings"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("ResultSet Name", true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ResultSetMappingsPanel(modelerScene.getModelerFile(), entity));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(sqlResultSetMappingSpec,
                t -> Arrays.asList(t.getName()),
                (t, row) -> t.setIdentifiableClass(entity)));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedStoredProcedureQueryProperty(JPAModelerScene modelerScene, Entity entity) {
        final List<NamedStoredProcedureQuery> namedStoredProcedureQueriesSpec = entity.getNamedStoredProcedureQuery();
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedStoredProcedureQueries", "Named StoredProcedure Queries", getMessage(PropertiesHandler.class, "INFO_STORED_PROCEDURE_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No NamedStoredProcedureQueries exist", "One NamedStoredProcedureQuery exist", "NamedStoredProcedureQueries exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("ProcedureName", false, String.class));
        columns.add(new Column("Parameters", false, Integer.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedStoredProcedureQueryPanel(modelerScene.getModelerFile()));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(namedStoredProcedureQueriesSpec,
                t -> Arrays.asList(t.isEnable(), t.getName(), t.getProcedureName(), t.getParameter().size()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedQueryProperty(JPAModelerScene modelerScene, IdentifiableClass identifiableClass) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedQueries", "Named Queries", getMessage(PropertiesHandler.class, "INFO_JPQL_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No NamedQueries exist", "One NamedQuery exist", "NamedQueries exist"});
        final List<NamedQuery> namedQueriesSpec = identifiableClass.getNamedQuery();

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Lock Mode Type", false, true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedQueryPanel(identifiableClass, modelerScene.getModelerFile()));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(namedQueriesSpec,
                t -> Arrays.asList(t.isEnable(), getShortQueryName(identifiableClass, t.getName()), t.getQuery(), t.getLockMode()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getClassAnnoation(JPAModelerScene modelerScene, List<ClassAnnotation> snippets) {
        return getCustomAnnoation(modelerScene, snippets, ClassAnnotation.class);
    }

    public static PropertySupport getAttributeAnnoation(JPAModelerScene modelerScene, List<AttributeAnnotation> snippets) {
        return getCustomAnnoation(modelerScene, snippets, AttributeAnnotation.class);
    }

    public static <T extends Annotation> PropertySupport getCustomAnnoation(JPAModelerScene modelerScene, List<T> annotations, Class<T> annotationType) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("Annotations", "Annotations", "");
        attributeEntity.setCountDisplay(new String[]{"No Annotations exist", "One Annotation exist", "Annotations exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Annoation", false, String.class));
        columns.add(new Column("Location", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new AnnotationPanel(modelerScene.getModelerFile(), annotationType));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(annotations,
                t -> Arrays.asList(t.isEnable(), t.getName(), t.getLocationType().getTitle()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getCustomArtifact(JPAModelerScene modelerScene, Set<ReferenceClass> referenceClasses, String artifactType) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(artifactType, artifactType, "");
        attributeEntity.setCountDisplay(new String[]{String.format("No %s exist", artifactType), String.format("One %s exist", artifactType), String.format("%s exist", artifactType)});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column(artifactType, false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JavaClassArtifactPanel(modelerScene.getModelerFile(), artifactType));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(referenceClasses,
                t -> Arrays.asList(t.isEnable(), t.getName()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static EmbeddedPropertySupport getCustomParentClass(JavaClassWidget<? extends JavaClass> javaClassWidget) {
        GenericEmbedded entity = new GenericEmbedded("extends", "Extends", getMessage(PropertiesHandler.class, "INFO_EXTENDS_CLASS"));
        entity.setEntityEditor(new ClassSelectionPanel(javaClassWidget.getModelerScene().getModelerFile()));
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
        return getCustomSnippet(modelerScene, snippets, ClassSnippet.class);
    }

    public static PropertySupport getAttributeSnippet(JPAModelerScene modelerScene, List<AttributeSnippet> snippets) {
        return getCustomSnippet(modelerScene, snippets, AttributeSnippet.class);
    }

    public static <T extends Snippet> PropertySupport getCustomSnippet(JPAModelerScene modelerScene, List<T> snippets, Class<T> snippetType) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("Snippets", "Snippets", "");
        attributeEntity.setCountDisplay(new String[]{"No Snippets exist", "One Snippet exist", "Snippets exist"});

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Snippet", false, String.class));
        columns.add(new Column("Location", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new CustomSnippetPanel(modelerScene.getModelerFile(), snippetType));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(snippets,
                t -> Arrays.asList(t.isEnable(), t.getValue(), t.getLocationType().getTitle()),
                (t, row) -> t.setEnable((boolean) row[1])));
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
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedEntityGraphPanel(entityWidget));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(entityGraphsSpec,
                t -> Arrays.asList(t.isEnable(), t.getName()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedNativeQueryProperty(JPAModelerScene modelerScene, final Entity entity) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("NamedNativeQueries", "Named Native Queries", getMessage(PropertiesHandler.class, "INFO_NATIVE_QUERY"));
        attributeEntity.setCountDisplay(new String[]{"No Named Native Queries exist", "One Named Native Query exist", "Named Native Queries exist"});
        List<NamedNativeQuery> namedNativeQueriesSpec = entity.getNamedNativeQuery();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedNativeQueryPanel(modelerScene.getModelerFile(), entity));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(namedNativeQueriesSpec,
                t -> Arrays.asList(t.isEnable(), t.getName(), t.getQuery()),
                (t, row) -> t.setEnable((boolean) row[1])));
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static void getJaxbVarTypeProperty(final ElementPropertySet set, final AttributeWidget attributeWidget, final JaxbVariableTypeHandler varHandlerSpec) {

        final List<JaxbVariableType> jaxbVariableList = varHandlerSpec.getJaxbVariableList();

        ComboBoxListener comboBoxListener = new ComboBoxListener<JaxbVariableType>() {
            @Override
            public void setItem(ComboBoxValue<JaxbVariableType> value) {
                varHandlerSpec.setJaxbVariableType(value.getValue());
                attributeWidget.refreshProperties();
            }

            @Override
            public ComboBoxValue<JaxbVariableType> getItem() {
                if (varHandlerSpec.getJaxbVariableType() == null) {
                    return new ComboBoxValue<>(XML_DEFAULT, XML_DEFAULT.getDisplayText());
                } else {
                    return new ComboBoxValue<>(varHandlerSpec.getJaxbVariableType(), varHandlerSpec.getJaxbVariableType().getDisplayText());
                }
            }

            @Override
            public List<ComboBoxValue<JaxbVariableType>> getItemList() {
                return jaxbVariableList
                        .stream()
                        .map(type -> new ComboBoxValue<>(type, type.getDisplayText()))
                        .collect(toList());
            }

            @Override
            public String getDefaultText() {
                return XML_DEFAULT.getDisplayText();
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        set.put("JAXB_PROP", new ComboBoxPropertySupport(attributeWidget.getModelerScene().getModelerFile(), "jaxbVariableType", "Variable Type", "", comboBoxListener, "root.jaxbSupport==true", varHandlerSpec));
    }

    public static EmbeddedPropertySupport getInheritanceProperty(EntityWidget entityWidget) {
        ModelerFile modelerFile = entityWidget.getModelerScene().getModelerFile();
        GenericEmbedded entity = new GenericEmbedded("inheritance", "Inheritance", "");
        try {
            entity.setEntityEditor(new InheritancePanel(modelerFile, entityWidget));
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
                        .map(sw -> (EntityWidget) sw)
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
            return entityWidget.getInheritanceState() != SINGLETON && !entityWidget.getBaseElementSpec().getNoSQL();
        });
        return new EmbeddedPropertySupport(modelerFile, entity);
    }

    public static EmbeddedPropertySupport getEqualsHashcodeProperty(JavaClassWidget<? extends JavaClass> classWidget) {
        GenericEmbedded entity = new GenericEmbedded("hashcode_equals", "equals() & hashcode()", "Define equals & hashcode implementation for the Entity");

        final JavaClass javaClassObj = classWidget.getBaseElementSpec();
        HashcodeEqualsPanel panel = new HashcodeEqualsPanel(classWidget);
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
        return new EmbeddedPropertySupport(classWidget.getModelerScene().getModelerFile(), entity);
    }

    public static EmbeddedPropertySupport getToStringProperty(JavaClassWidget<? extends JavaClass> classWidget) {
        GenericEmbedded entity = new GenericEmbedded("toString", "toString()", getMessage(ClassMemberPanel.class, "LBL_tostring_select"));
        final ClassMembers classMembersObj = classWidget.getBaseElementSpec().getToStringMethod();
        ClassMemberPanel classMemberPanel = new ClassMemberPanel(getMessage(ClassMemberPanel.class, "LBL_tostring_select"), classWidget, false);
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
        return new EmbeddedPropertySupport(classWidget.getModelerScene().getModelerFile(), entity);
    }

    public static PropertySupport getConstructorProperties(JavaClassWidget<? extends JavaClass> classWidget) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("constructor", "Constructor", "Constructor");
        attributeEntity.setCountDisplay(new String[]{"No Constructors exist", "One Constructor exist", "Constructors exist"});
        List<Constructor> constructors = classWidget.getBaseElementSpec().getConstructors();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("#", true, Boolean.class));
        columns.add(new Column("Constructor List", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ConstructorPanel(classWidget));
        attributeEntity.setTableDataListener(new NEntityDataListener<>(constructors,
                t -> Arrays.asList(t.isEnable(), t.toString()),
                (t, row) -> t.setEnable((boolean) row[1]),
                () -> {
                    //add no-arg constructor, if no-arg constructor not exist and other constructor exist
                    if (!constructors.isEmpty() && !constructors.stream().anyMatch(con -> con.getAttributes().isEmpty())) {
                        constructors.add(Constructor.getNoArgsInstance());
                    }
                    //Enable no-args constructor and disable other no-args constructor , if more then one are available 
                    if (!constructors.isEmpty()) {
                        List<Constructor> noArgsConstructors = constructors.stream().filter(con -> con.isNoArgs()).collect(toList());
                        noArgsConstructors.forEach(con -> con.setEnable(false));
                        noArgsConstructors.get(0).setEnable(true);
                        noArgsConstructors.get(0).setAccessModifier(AccessModifierType.PUBLIC);
                    }
                }));
        return new NEntityPropertySupport(classWidget.getModelerScene().getModelerFile(), attributeEntity);
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
                    return firstUpper(idAttribute.getGeneratedValue().getStrategy().toString());
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
                attributeWidget.setAttributeTooltip();
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
        columns.add(new Column("Converter Class", false, String.class));
        columns.add(new Column("Attribute Type", false, String.class));
        columns.add(new Column("DB Field Type", false, String.class));
        columns.add(new Column("Auto Apply", true, Boolean.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setTableDataListener(new NEntityDataListener<>(converters,
                t -> Arrays.asList(t.getClazz(), t.getAttributeType(), t.getFieldType(), t.isAutoApply()),
                (t, row) -> t.setAutoApply((boolean) row[4])));
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
        columns.add(new Column("Converter Class", false, String.class));
        columns.add(new Column("AttributeName", false, String.class));
        columns.add(new Column("Disable Global Conversion", true, Boolean.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setTableDataListener(new NEntityDataListener<>(converts,
                t -> Arrays.asList(t.getConverter(), t.getAttributeName(), t.isDisableConversion()),
                (t, row) -> t.setDisableConversion((boolean) row[3])));
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

}
