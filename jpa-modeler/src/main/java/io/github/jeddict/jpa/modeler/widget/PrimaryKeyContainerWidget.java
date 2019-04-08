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
package io.github.jeddict.jpa.modeler.widget;

import static io.github.jeddict.jpa.modeler.Constant.ID_ATTRIBUTE;
import static io.github.jeddict.jpa.modeler.Constant.VERSION_ATTRIBUTE;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCustomArtifact;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.ROOT;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedIdAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.IdAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.VersionAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.SingleRelationAttributeWidget;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.GeneratedValue;
import io.github.jeddict.jpa.spec.GenerationType;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.CompositePrimaryKeyType;
import io.github.jeddict.jpa.spec.extend.IAttributes;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.PrimaryKeyContainer;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.extend.SingleRelationAttribute;
import io.github.jeddict.settings.diagram.ClassDiagramSettings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.combobox.ActionHandler;
import org.netbeans.modeler.properties.combobox.ComboBoxListener;
import org.netbeans.modeler.properties.combobox.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

public abstract class PrimaryKeyContainerWidget<E extends IdentifiableClass> extends PersistenceClassWidget<E> {

    private EmbeddedIdAttributeWidget embeddedIdAttributeWidget;
    private final List<IdAttributeWidget> idAttributeWidgets = new ArrayList<>();
    private final List<VersionAttributeWidget> versionAttributeWidgets = new ArrayList<>();

    public PrimaryKeyContainerWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyVisibilityHandler("compositePrimaryKeyType", () -> {
            CompositePKProperty property = PrimaryKeyContainerWidget.this.isCompositePKPropertyAllow();
            return property == CompositePKProperty.ALL || property == CompositePKProperty.TYPE || property == CompositePKProperty.AUTO_CLASS;
        });
        this.addPropertyVisibilityHandler("compositePrimaryKeyClass", () -> {
            CompositePKProperty property = PrimaryKeyContainerWidget.this.isCompositePKPropertyAllow();
            return property == CompositePKProperty.ALL || property == CompositePKProperty.CLASS;
        });
        this.addPropertyChangeListener("compositePrimaryKeyClass", (PropertyChangeListener<String>) (oldName, newName) -> {
            scanReservedDefaultClass(oldName, newName);
//            scanDuplicateDefaultClass(oldName, newName);
        });
        PropertyVisibilityHandler noSQLDisabled = () -> !this.getBaseElementSpec().getNoSQL();
        this.addPropertyVisibilityHandler("Listeners", noSQLDisabled);
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        IdentifiableClass javaClass = this.getBaseElementSpec();
        set.put("JPA_PROP", getCompositePrimaryKeyProperty());
        set.put("ENTITY_PROP", getCustomArtifact(this.getModelerScene(), javaClass.getEntityListeners().getEntityListener(), "Listeners"));
    }

    @Override
    public List<AttributeWidget> getAttributeOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getAttributeOverrideWidgetsImpl());
        }
        return attributeWidgets;
    }

    private List<AttributeWidget> getAttributeOverrideWidgetsImpl() {//include self
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getAttributeOverrideWidgetsImpl());
        }
        attributeWidgets.addAll(this.getIdAttributeWidgets());
//        if (embeddedIdAttributeWidget != null) {
//            attributeWidgets.add(embeddedIdAttributeWidget);
//        }
        attributeWidgets.addAll(getBasicAttributeWidgets());
        attributeWidgets.addAll(getBasicCollectionAttributeWidgets());
        return attributeWidgets;
    }

    public List<AttributeWidget> getAssociationOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getAssociationOverrideWidgetsImpl());
        }
        return attributeWidgets;
    }

    private List<AttributeWidget> getAssociationOverrideWidgetsImpl() {//include self
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getAssociationOverrideWidgetsImpl());
        }
        attributeWidgets.addAll(this.getOneToOneRelationAttributeWidgets());
        attributeWidgets.addAll(this.getOneToManyRelationAttributeWidgets());
        attributeWidgets.addAll(this.getManyToOneRelationAttributeWidgets());
        attributeWidgets.addAll(this.getManyToManyRelationAttributeWidgets());
        return attributeWidgets;
    }

    public List<AttributeWidget> getEmbeddedOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getEmbeddedOverrideWidgetsImpl());
        }
        attributeWidgets.addAll(this.getSingleValueEmbeddedAttributeWidgets());
        attributeWidgets.addAll(this.getMultiValueEmbeddedAttributeWidgets());
        return attributeWidgets;
    }

    private List<AttributeWidget> getEmbeddedOverrideWidgetsImpl() {//include self
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getEmbeddedOverrideWidgetsImpl());
        }
        attributeWidgets.addAll(this.getSingleValueEmbeddedAttributeWidgets());
        attributeWidgets.addAll(this.getMultiValueEmbeddedAttributeWidgets());
        return attributeWidgets;
    }

    public IdAttributeWidget addIdAttribute(String name) {
        IdAttributeWidget idAttributeWidget = addIdAttribute(name, null);
        return idAttributeWidget;
    }

    public IdAttributeWidget addIdAttribute(String name, Id id) {
        ManagedClass<? extends IPrimaryKeyAttributes> javaClass = this.getBaseElementSpec();

        if (id == null) {
            id = new Id();
            id.setId(NBModelerUtil.getAutoGeneratedStringId());
            id.setAttributeType("Long");
            id.setName(name);
            if (javaClass.getAttributes().getId().isEmpty()) {
                GeneratedValue generatedValue = new GeneratedValue();
                generatedValue.setStrategy(GenerationType.DEFAULT);
                id.setGeneratedValue(generatedValue);
            }
            javaClass.getAttributes().addId(id);
        }

        IdAttributeWidget attributeWidget = createPinWidget(id, IdAttributeWidget.class, 
                widgetInfo -> new IdAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getIdAttributeWidgets().add(attributeWidget);
        sortAttributes();
        AttributeValidator.validateEmbeddedIdAndIdFound(this);
        if (this instanceof EntityWidget) {
            ((EntityWidget) this).scanKeyError();
        }
        this.getAllSubclassWidgets()
                .stream()
                .filter(classWidget -> classWidget instanceof EntityWidget)
                .forEach(classWidget -> ((EntityWidget) classWidget).scanKeyError());
        isCompositePKPropertyAllow();//to update default CompositePK class , type //for manual created attribute
        scanDuplicateAttributes(null, id.getName());
        return attributeWidget;
    }

    public VersionAttributeWidget addVersionAttribute(String name) {
        return addVersionAttribute(name, null);
    }

    public VersionAttributeWidget addVersionAttribute(String name, Version version) {
        ManagedClass javaClass = this.getBaseElementSpec();
        if (version == null) {
            version = new Version();
            version.setId(NBModelerUtil.getAutoGeneratedStringId());
            version.setAttributeType("long");
            version.setName(name);
            ((IPrimaryKeyAttributes) javaClass.getAttributes()).addVersion(version);
        }
        VersionAttributeWidget attributeWidget = createPinWidget(version, VersionAttributeWidget.class, 
                widgetInfo -> new VersionAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getVersionAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, version.getName());
        return attributeWidget;
    }

    public EmbeddedIdAttributeWidget addEmbeddedIdAttribute(String name) {
        return addEmbeddedIdAttribute(name, null);
    }

    public EmbeddedIdAttributeWidget addEmbeddedIdAttribute(String name, EmbeddedId embeddedId) {
        ManagedClass javaClass = this.getBaseElementSpec();
        if (embeddedId == null) {
            embeddedId = new EmbeddedId();
            embeddedId.setId(NBModelerUtil.getAutoGeneratedStringId());
            embeddedId.setName(name);
            ((IPrimaryKeyAttributes) javaClass.getAttributes()).setEmbeddedId(embeddedId);
        }

        EmbeddedIdAttributeWidget attributeWidget = createPinWidget(embeddedId, EmbeddedIdAttributeWidget.class, 
                widgetInfo -> new EmbeddedIdAttributeWidget(this.getModelerScene(), this, widgetInfo));
        setEmbeddedIdAttributeWidget(attributeWidget);
        sortAttributes();
        AttributeValidator.validateMultipleEmbeddedIdFound(this);
        AttributeValidator.validateEmbeddedIdAndIdFound(this);
        scanDuplicateAttributes(null, embeddedId.getName());
        return attributeWidget;
    }

    /**
     * @return the idAttributeWidgets
     */
    public List<IdAttributeWidget> getIdAttributeWidgets() {
        return idAttributeWidgets;
    }

    /**
     * @return the embeddedIdAttributeWidget
     */
    public EmbeddedIdAttributeWidget getEmbeddedIdAttributeWidget() {
        return embeddedIdAttributeWidget;
    }

    /**
     * @param embeddedIdAttributeWidget the embeddedIdAttributeWidget to set
     */
    public void setEmbeddedIdAttributeWidget(EmbeddedIdAttributeWidget embeddedIdAttributeWidget) {
        this.embeddedIdAttributeWidget = embeddedIdAttributeWidget;
    }

    /**
     * @return the versionAttributeWidgets
     */
    public List<VersionAttributeWidget> getVersionAttributeWidgets() {
        return versionAttributeWidgets;
    }

    public CompositePKProperty isCompositePKPropertyAllow() {
        if (this.getBaseElementSpec() instanceof PrimaryKeyContainer) {
            PrimaryKeyContainer primaryKeyContainerSpec = (PrimaryKeyContainer) this.getBaseElementSpec();
            InheritanceStateType inheritanceState = this.getInheritanceState();
            CompositePKProperty property = CompositePKProperty.NONE;
            List<SingleRelationAttributeWidget> derivedRelationAttributes = getDerivedRelationAttributeWidgets();

            boolean visible = false;
            if (this instanceof EntityWidget) {
                visible = getIdAttributeWidgets().size() + derivedRelationAttributes.size() > 1 && (inheritanceState == ROOT || inheritanceState == SINGLETON);
            } else if (this instanceof MappedSuperclassWidget) {
                visible = getIdAttributeWidgets().size() + derivedRelationAttributes.size() > 1;
            }

            if (!visible) {
//                2.4.1.3 Examples of Derived Identities
//                Example 5: The parent entity uses IdClass. The dependent's primary key class is of same type as that of the parent entity.
//                Case (a): The dependent entity uses IdClass:
//                CompositePKProperty.FIXED_CLASS
                if (derivedRelationAttributes.size() == 1) {//check for parent entity pk count
                    if ((this instanceof MappedSuperclassWidget) || (this instanceof EntityWidget && (inheritanceState == ROOT || inheritanceState == SINGLETON))) {
                        RelationAttributeWidget relationAttributeWidget = getDerivedRelationAttributeWidgets().get(0);
                        Entity targetEntitySpec = ((RelationAttribute) relationAttributeWidget.getBaseElementSpec()).getConnectedEntity();
                        EntityWidget targetEntityWidget = (EntityWidget) getModelerScene().getBaseElement(targetEntitySpec.getId());
                        if (targetEntityWidget.isCompositePKPropertyAllow() != CompositePKProperty.NONE) {
                            property = CompositePKProperty.AUTO_CLASS;
                        }
                    }
                }
            } else {
//                if ((primaryKeyContainerSpec.getCompositePrimaryKeyClass() == null || primaryKeyContainerSpec.getCompositePrimaryKeyClass().trim().isEmpty())
//                        && (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID || primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS)) {
//                    primaryKeyContainerSpec.manageCompositePrimaryKey();
//                    getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//                } //Don't remove comment
                if (property == CompositePKProperty.NONE) {
                    property = CompositePKProperty.ALL;
                }
            }

            return property;
        }
        return CompositePKProperty.NONE;
    }

    public void onCompositePrimaryKeyTypeChange(CompositePrimaryKeyType compositePrimaryKeyType) {
        if ((compositePrimaryKeyType == CompositePrimaryKeyType.EMBEDDEDID
                || (compositePrimaryKeyType == CompositePrimaryKeyType.DEFAULT && ClassDiagramSettings.isEmbeddedIdDefaultType()))) {
            if (embeddedIdAttributeWidget == null) {
                addEmbeddedIdAttribute(getNextAttributeName(this.getName() + "EmbeddedId"));
            }
        } else if (embeddedIdAttributeWidget != null) {
            embeddedIdAttributeWidget.remove();
        }
    }

    private ComboBoxPropertySupport getCompositePrimaryKeyProperty() {
        final JavaClassWidget javaClassWidget = this;
        final PrimaryKeyContainer primaryKeyContainerSpec = (PrimaryKeyContainer) javaClassWidget.getBaseElementSpec();
        ComboBoxListener<CompositePrimaryKeyType> comboBoxListener = new ComboBoxListener<CompositePrimaryKeyType>() {
            @Override
            public void setItem(ComboBoxValue<CompositePrimaryKeyType> value) {
                CompositePrimaryKeyType compositePrimaryKeyType = value.getValue();
                onCompositePrimaryKeyTypeChange(compositePrimaryKeyType);
                primaryKeyContainerSpec.setCompositePrimaryKeyType(compositePrimaryKeyType);
            }

            @Override
            public ComboBoxValue<CompositePrimaryKeyType> getItem() {
                if (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                    return new ComboBoxValue(CompositePrimaryKeyType.EMBEDDEDID, "Embedded Id");
                } else if (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                    return new ComboBoxValue(CompositePrimaryKeyType.IDCLASS, "Id Class");
                } else {
                    return new ComboBoxValue(CompositePrimaryKeyType.DEFAULT, String.format("Default (%s)", ClassDiagramSettings.getDefaultCompositePrimaryKeyType()));
                }
            }

            @Override
            public List<ComboBoxValue<CompositePrimaryKeyType>> getItemList() {
                List<ComboBoxValue<CompositePrimaryKeyType>> values = new ArrayList<>();
                values.add(new ComboBoxValue(CompositePrimaryKeyType.DEFAULT, String.format("Default (%s)", ClassDiagramSettings.getDefaultCompositePrimaryKeyType())));
                values.add(new ComboBoxValue(CompositePrimaryKeyType.IDCLASS, "Id Class"));
                values.add(new ComboBoxValue(CompositePrimaryKeyType.EMBEDDEDID, "Embedded Id"));
                return values;
            }

            @Override
            public String getDefaultText() {
                return "Id Class";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(this.getModelerScene().getModelerFile(), "compositePrimaryKeyType", "Composite PrimaryKey Type", "", comboBoxListener);
    }

    public void checkPrimaryKeyStatus() {
        if (isCompositePKPropertyAllow() == CompositePKProperty.NONE) {
            if (this.getEmbeddedIdAttributeWidget() != null) {
                this.getEmbeddedIdAttributeWidget().remove(false);
            }
            PrimaryKeyContainer primaryKeyContainer = (PrimaryKeyContainer) this.getBaseElementSpec();
            primaryKeyContainer.setCompositePrimaryKeyClass(null);
            primaryKeyContainer.setCompositePrimaryKeyType(null);
        }
    }

    @Deprecated //prefer PrimaryKeyAttributes.getSuperId
    public List<IdAttributeWidget> getAllIdAttributeWidgets() {
        List<IdAttributeWidget> idAttributeWidgetsResult = new ArrayList<>(this.getIdAttributeWidgets());
        List<JavaClassWidget> classWidgets = getAllSuperclassWidget();
        classWidgets.stream()
                .filter(classWidget -> (classWidget instanceof PrimaryKeyContainerWidget))
                .map(classWidget -> (PrimaryKeyContainerWidget) classWidget) //    .flatMap(classWidget -> classWidget.getIdAttributeWidgets().stream()).collect(toList())
                .forEach((classWidget) -> {
                    idAttributeWidgetsResult.addAll(classWidget.getIdAttributeWidgets());
                });
        return idAttributeWidgetsResult;
    }

    @Deprecated //prefer PrimaryKeyAttributes.getPrimaryKeyAttributes
    public List<AttributeWidget> getPrimaryKeyAttributeWidgets() {
        List<AttributeWidget> idAttributeWidgets_TMP = new ArrayList<>(this.getIdAttributeWidgets());
        idAttributeWidgets_TMP.addAll(getIdRelationAttributeWidgets());
        List<JavaClassWidget> classWidgets = getAllSuperclassWidget();
        classWidgets.stream()
                .filter(classWidget -> (classWidget instanceof PrimaryKeyContainerWidget))
                .map(classWidget -> (PrimaryKeyContainerWidget) classWidget)
                .forEach(classWidget -> {
                    idAttributeWidgets_TMP.addAll(classWidget.getIdAttributeWidgets());
                    idAttributeWidgets_TMP.addAll(classWidget.getIdRelationAttributeWidgets());
                });
        return idAttributeWidgets_TMP;
    }

    @Deprecated //prefer PersistenceAttributes.getDerivedRelationAttributes
    public List<SingleRelationAttributeWidget> getIdRelationAttributeWidgets() {
        List<SingleRelationAttributeWidget> list = new ArrayList<>(getOneToOneRelationAttributeWidgets());
        list.addAll(getManyToOneRelationAttributeWidgets());
        return list.stream()
                .filter(r -> ((SingleRelationAttribute) r.getBaseElementSpec()).isPrimaryKey())
                .collect(toList());
    }

    public List<EmbeddedIdAttributeWidget> getAllEmbeddedIdAttributeWidgets() {
        List<EmbeddedIdAttributeWidget> embeddedIdAttributeWidgets = new ArrayList<>();
        embeddedIdAttributeWidgets.add(this.getEmbeddedIdAttributeWidget());
        List<JavaClassWidget> classWidgets = getAllSuperclassWidget();
        for (JavaClassWidget classWidget : classWidgets) {
            if (classWidget instanceof PrimaryKeyContainerWidget) {
                PrimaryKeyContainerWidget primaryKeyContainerWidget = (PrimaryKeyContainerWidget) classWidget;
                if (primaryKeyContainerWidget.getEmbeddedIdAttributeWidget() != null) {
                    embeddedIdAttributeWidgets.add(primaryKeyContainerWidget.getEmbeddedIdAttributeWidget());
                }
            }
        }
        return embeddedIdAttributeWidgets;
    }

    @Override
    public Map<String, List<Widget>> getAttributeCategories() {
        Map<String, List<Widget>> categories = new LinkedHashMap<>();
        if (embeddedIdAttributeWidget != null) {
            List<Widget> embeddedIdAttributeCatWidget = new ArrayList<>();
            embeddedIdAttributeCatWidget.add(embeddedIdAttributeWidget);
            categories.put("Embedded Id", embeddedIdAttributeCatWidget);
        }
        if (!idAttributeWidgets.isEmpty()) {
            List<Widget> idAttributeCatWidget = new ArrayList<>();
            getIdAttributeWidgets().forEach((idAttributeWidget) -> {
                idAttributeCatWidget.add(idAttributeWidget);
            });
            categories.put("PrimaryKey", idAttributeCatWidget);
        }

        categories.putAll(super.getAttributeCategories());

        if (!versionAttributeWidgets.isEmpty()) {
            List<Widget> versionAttributeCatWidget = new ArrayList<>();
            getVersionAttributeWidgets().forEach((versionAttributeWidget) -> {
                versionAttributeCatWidget.add(versionAttributeWidget);
            });
            categories.put("Version", versionAttributeCatWidget);
        }
        return categories;
    }

    @Override
    public List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets(boolean includeParentClassAttibute) {
        List<AttributeWidget<? extends Attribute>> attributeWidgets = super.getAllAttributeWidgets(includeParentClassAttibute);
        attributeWidgets.addAll(idAttributeWidgets);
        if (embeddedIdAttributeWidget != null) {
            attributeWidgets.add(embeddedIdAttributeWidget);
        }
        attributeWidgets.addAll(getVersionAttributeWidgets());
        return attributeWidgets;
    }

    @Override
    public void deleteAttribute(AttributeWidget attributeWidget) {
        ManagedClass javaClass = this.getBaseElementSpec();
        IAttributes attributes = javaClass.getAttributes();
        if (attributeWidget == null) {
            return;
        }
        if (attributeWidget instanceof IdAttributeWidget) {
            getIdAttributeWidgets().remove((IdAttributeWidget) attributeWidget);
            ((IPrimaryKeyAttributes) attributes).getId().remove(((IdAttributeWidget) attributeWidget).getBaseElementSpec());
            AttributeValidator.validateEmbeddedIdAndIdFound(this);
            if (this instanceof EntityWidget) {
                ((EntityWidget) this).scanKeyError();
            }
            this.getAllSubclassWidgets().stream().filter((classWidget) -> (classWidget instanceof EntityWidget)).forEach((classWidget) -> {
                ((EntityWidget) classWidget).scanKeyError();
            });
            checkPrimaryKeyStatus();
        } else if (attributeWidget instanceof EmbeddedIdAttributeWidget) {
            embeddedIdAttributeWidget = null;
            ((IPrimaryKeyAttributes) attributes).setEmbeddedId(null);
            AttributeValidator.validateMultipleEmbeddedIdFound(this);
            AttributeValidator.validateEmbeddedIdAndIdFound(this);
        } else if (attributeWidget instanceof VersionAttributeWidget) {
            getVersionAttributeWidgets().remove((VersionAttributeWidget) attributeWidget);
            ((IPrimaryKeyAttributes) attributes).getVersion().remove(((VersionAttributeWidget) attributeWidget).getBaseElementSpec());
        } else {
            super.deleteAttribute(attributeWidget);
            return;
        }
        sortAttributes();
        scanDuplicateAttributes(attributeWidget.getBaseElementSpec().getName(), null);
        javaClass.updateArtifact((Attribute) attributeWidget.getBaseElementSpec());
    }

    @Override
    public void createPinWidget(String docId) {
        if (null != docId) {
            switch (docId) {
                case ID_ATTRIBUTE:
                    this.addIdAttribute(getNextAttributeName("id")).edit();
                    break;
                case VERSION_ATTRIBUTE:
                    this.addVersionAttribute(getNextAttributeName()).edit();
                    break;
                default:
                    super.createPinWidget(docId);
            }
        }
    }

//    /**
//     * To reserve DefaultClass name should not be used by any other DefaultClass
//     * with different attribute structure .
//     *
//     * @param previousName
//     * @param newName
//     */
//    public void scanDuplicateMismatchDefaultClass(String previousName, String newName) {
//        int previousNameCount = 0, newNameCount = 0;
//        String previousNameClass, newNameClass;
//        DefaultClass previousDefaultClass = null, newDefaultClass = null;
//        EntityMappings entityMappings = this.getModelerScene().getBaseElementSpec();
//        for (JavaClass javaClass : entityMappings.getJavaClass()) {
//            if (javaClass instanceof IdentifiableClass) {
//                IdentifiableClass ic = (IdentifiableClass) javaClass;
//                if (ic.getCompositePrimaryKeyType() != null) {
//                    if (ic.getCompositePrimaryKeyClass().equals(previousName)) {
//                        if (previousDefaultClass == null) {
//                            previousDefaultClass = ic.getDefaultClass();
//                            previousNameClass = ic.getClazz();
//                            ++previousNameCount;
//                        } else if(!previousDefaultClass.getAttributes().equals(ic.getDefaultClass().getAttributes())){
//                            ++previousNameCount;
//                        }
//                    }
//                    if (ic.getCompositePrimaryKeyClass().equals(newName)) {
//                        if (newDefaultClass == null) {
//                            newDefaultClass = ic.getDefaultClass();
//                            newNameClass = ic.getClazz();
//                            ++newNameCount;                        
//                        } else if(!newDefaultClass.getAttributes().equals(ic.getDefaultClass().getAttributes())){
//                            ++newNameCount;
//                        }
//                    }
//                }
//            }
//        }
////
////        List<JavaClassWidget> javaClassList = this.getModelerScene().getJavaClassWidges();
////        for (JavaClassWidget<JavaClass> javaClassWidget : javaClassList) {
////            JavaClass javaClass = javaClassWidget.getBaseElementSpec();
////            if (javaClass instanceof IdentifiableClass) {
////                IdentifiableClass ic = (IdentifiableClass) javaClass;
////                if (ic.getCompositePrimaryKeyType() != null) {
////                    if (ic.getCompositePrimaryKeyClass().equals(previousName)) {
////                        if (previousNameCount > 1) {
////                            javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS, previousName, previousNameClasses.toString());
////                        } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
////                            javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS);
////                        }
////                    }
////
////                    if (ic.getCompositePrimaryKeyClass().equals(newName)) {
////                        if (++newNameCount > 1) {
////                            javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS, newName, newNameClasses.toString());
////                        } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
////                            javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS);
////                        }
////                    }
////                }
////            }
////        }
//    }

}
