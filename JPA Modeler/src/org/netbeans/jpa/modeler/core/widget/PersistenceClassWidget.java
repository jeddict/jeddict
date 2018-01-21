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
package org.netbeans.jpa.modeler.core.widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jcode.core.util.StringHelper;
import static org.netbeans.jpa.modeler.Constant.BASIC_ATTRIBUTE;
import static org.netbeans.jpa.modeler.Constant.BASIC_COLLECTION_ATTRIBUTE;
import static org.netbeans.jpa.modeler.Constant.TRANSIENT_ATTRIBUTE;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.SingleRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getConstructorProperties;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getToStringProperty;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.ClassValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.WARNING;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.util.RequestProcessor;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BidirectionalRelation;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getEqualsHashcodeProperty;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class PersistenceClassWidget<E extends ManagedClass<? extends IPersistenceAttributes>> extends JavaClassWidget<E> {

    private final List<BasicAttributeWidget> basicAttributeWidgets = new ArrayList<>();
    private final List<BasicCollectionAttributeWidget> basicCollectionAttributeWidgets = new ArrayList<>();
    private final List<TransientAttributeWidget> transientAttributeWidgets = new ArrayList<>();
    private final List<OTORelationAttributeWidget> oneToOneRelationAttributeWidgets = new ArrayList<>();
    private final List<OTMRelationAttributeWidget> oneToManyRelationAttributeWidgets = new ArrayList<>();
    private final List<MTORelationAttributeWidget> manyToOneRelationAttributeWidgets = new ArrayList<>();
    private final List<MTMRelationAttributeWidget> manyToManyRelationAttributeWidgets = new ArrayList<>();
    private final List<SingleValueEmbeddedAttributeWidget> singleValueEmbeddedAttributeWidgets = new ArrayList<>();
    private final List<MultiValueEmbeddedAttributeWidget> multiValueEmbeddedAttributeWidgets = new ArrayList<>();
    private final List<RelationFlowWidget> inverseSideRelationFlowWidgets = new ArrayList<>();
       
    public PersistenceClassWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (oldValue, tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    getSignalManager().fire(WARNING, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    getSignalManager().clear(WARNING, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                getSignalManager().clear(WARNING, ClassValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
    }
    
    @Override
    public List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets(boolean includeParentClassAttibute) {
        List<AttributeWidget<? extends Attribute>> attributeWidgets = new ArrayList<>();
        JavaClassWidget superClassWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (includeParentClassAttibute && superClassWidget instanceof PersistenceClassWidget) {
            attributeWidgets.addAll(((PersistenceClassWidget) superClassWidget).getAllAttributeWidgets(includeParentClassAttibute));
        }
        attributeWidgets.addAll(basicAttributeWidgets);
        attributeWidgets.addAll(basicCollectionAttributeWidgets);
        attributeWidgets.addAll(singleValueEmbeddedAttributeWidgets);
        attributeWidgets.addAll(multiValueEmbeddedAttributeWidgets);
        attributeWidgets.addAll(oneToOneRelationAttributeWidgets);
        attributeWidgets.addAll(oneToManyRelationAttributeWidgets);
        attributeWidgets.addAll(manyToOneRelationAttributeWidgets);
        attributeWidgets.addAll(manyToManyRelationAttributeWidgets);
        attributeWidgets.addAll(transientAttributeWidgets);
        return attributeWidgets;
    }

    public List<RelationAttributeWidget> getAllRelationAttributeWidgets() {
        return getAllRelationAttributeWidgets(false);
    }

    public List<RelationAttributeWidget> getAllRelationAttributeWidgets(boolean includeParentClassAttribute) {
        List<RelationAttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (includeParentClassAttribute && classWidget instanceof PersistenceClassWidget) {
            attributeWidgets.addAll(((PersistenceClassWidget) classWidget).getAllRelationAttributeWidgets(includeParentClassAttribute));
        }
        attributeWidgets.addAll(oneToOneRelationAttributeWidgets);
        attributeWidgets.addAll(oneToManyRelationAttributeWidgets);
        attributeWidgets.addAll(manyToOneRelationAttributeWidgets);
        attributeWidgets.addAll(manyToManyRelationAttributeWidgets);
        return attributeWidgets;
    }

    public void scanDuplicateInheritedAttributes() {
        this.getAllAttributeWidgets().forEach((attributeWidget) -> {
            scanDuplicateAttributes(null, attributeWidget.getBaseElementSpec().getName());
        });
    }

    public RelationAttributeWidget findRelationAttributeWidget(String id, Class<? extends RelationAttributeWidget>... relationAttributeWidgetClasses) {
        for (Class<? extends RelationAttributeWidget> relationAttributeWidgetClass : relationAttributeWidgetClasses) {
            if (relationAttributeWidgetClass == OTORelationAttributeWidget.class) {
                for (OTORelationAttributeWidget oneToOneRelationAttributeWidget : oneToOneRelationAttributeWidgets) {
                    if (oneToOneRelationAttributeWidget.getId().equals(id)) {
                        return oneToOneRelationAttributeWidget;
                    }
                }
            } else if (relationAttributeWidgetClass == OTMRelationAttributeWidget.class) {
                for (OTMRelationAttributeWidget oneToManyRelationAttributeWidget : oneToManyRelationAttributeWidgets) {
                    if (oneToManyRelationAttributeWidget.getId().equals(id)) {
                        return oneToManyRelationAttributeWidget;
                    }
                }
            } else if (relationAttributeWidgetClass == MTORelationAttributeWidget.class) {
                for (MTORelationAttributeWidget manyToOneRelationAttributeWidget : manyToOneRelationAttributeWidgets) {
                    if (manyToOneRelationAttributeWidget.getId().equals(id)) {
                        return manyToOneRelationAttributeWidget;
                    }
                }
            } else if (relationAttributeWidgetClass == MTMRelationAttributeWidget.class) {
                for (MTMRelationAttributeWidget manyToManyRelationAttributeWidget : manyToManyRelationAttributeWidgets) {
                    if (manyToManyRelationAttributeWidget.getId().equals(id)) {
                        return manyToManyRelationAttributeWidget;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deleteAttribute(AttributeWidget attributeWidget) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        IPersistenceAttributes attributes = javaClass.getAttributes();
        if (attributeWidget instanceof BasicAttributeWidget) {
            basicAttributeWidgets.remove((BasicAttributeWidget) attributeWidget);
            attributes.removeBasic(((BasicAttributeWidget) attributeWidget).getBaseElementSpec());
            removeNamedQuery((Attribute)attributeWidget.getBaseElementSpec());
        } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
            basicCollectionAttributeWidgets.remove((BasicCollectionAttributeWidget) attributeWidget);
            attributes.removeElementCollection(((BasicCollectionAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof TransientAttributeWidget) {
            transientAttributeWidgets.remove((TransientAttributeWidget) attributeWidget);
            attributes.removeTransient(((TransientAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof RelationAttributeWidget) {
            if (attributeWidget instanceof OTORelationAttributeWidget) {
                OTORelationAttributeWidget otoRelationAttributeWidget = (OTORelationAttributeWidget) attributeWidget;
                OneToOne oneToOneSpec = ((OTORelationAttributeWidget) attributeWidget).getBaseElementSpec();
                otoRelationAttributeWidget.setLocked(true);
                otoRelationAttributeWidget.getOneToOneRelationFlowWidget().remove();
                otoRelationAttributeWidget.setLocked(false);
                oneToOneRelationAttributeWidgets.remove((OTORelationAttributeWidget) attributeWidget);
                attributes.removeOneToOne(oneToOneSpec);

                if (oneToOneSpec.isPrimaryKey()) {
                    AttributeValidator.validateEmbeddedIdAndIdFound(this);
                    if (this instanceof EntityWidget) {
                        ((EntityWidget) this).scanKeyError();
                    }
                    this.getAllSubclassWidgets().stream().filter((classWidget) -> (classWidget instanceof EntityWidget)).forEach((classWidget) -> {
                        ((EntityWidget) classWidget).scanKeyError();
                    });
                    if (this instanceof PrimaryKeyContainerWidget) {
                        ((PrimaryKeyContainerWidget) this).checkPrimaryKeyStatus();
                    }
                }

            } else if (attributeWidget instanceof OTMRelationAttributeWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget = (OTMRelationAttributeWidget) attributeWidget;
                otmRelationAttributeWidget.setLocked(true);
                otmRelationAttributeWidget.getHierarchicalRelationFlowWidget().remove();
                otmRelationAttributeWidget.setLocked(false);
                oneToManyRelationAttributeWidgets.remove((OTMRelationAttributeWidget) attributeWidget);
                attributes.removeOneToMany(((OTMRelationAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof MTORelationAttributeWidget) {
                MTORelationAttributeWidget mtoRelationAttributeWidget = (MTORelationAttributeWidget) attributeWidget;
                ManyToOne manyToOneSpec = ((MTORelationAttributeWidget) attributeWidget).getBaseElementSpec();
                mtoRelationAttributeWidget.setLocked(true);
                mtoRelationAttributeWidget.getManyToOneRelationFlowWidget().remove();
                mtoRelationAttributeWidget.setLocked(false);
                manyToOneRelationAttributeWidgets.remove((MTORelationAttributeWidget) attributeWidget);
                attributes.removeManyToOne(manyToOneSpec);

                if (manyToOneSpec.isPrimaryKey()) {
                    AttributeValidator.validateEmbeddedIdAndIdFound(this);
                    if (this instanceof EntityWidget) {
                        ((EntityWidget) this).scanKeyError();
                    }
                    this.getAllSubclassWidgets().stream().filter((classWidget) -> (classWidget instanceof EntityWidget)).forEach((classWidget) -> {
                        ((EntityWidget) classWidget).scanKeyError();
                    });
                    if (this instanceof PrimaryKeyContainerWidget) {
                        ((PrimaryKeyContainerWidget) this).checkPrimaryKeyStatus();
                    }
                }
            } else if (attributeWidget instanceof MTMRelationAttributeWidget) {
                MTMRelationAttributeWidget mtmRelationAttributeWidget = (MTMRelationAttributeWidget) attributeWidget;
                mtmRelationAttributeWidget.setLocked(true);
                mtmRelationAttributeWidget.getManyToManyRelationFlowWidget().remove();
                mtmRelationAttributeWidget.setLocked(false);
                manyToManyRelationAttributeWidgets.remove((MTMRelationAttributeWidget) attributeWidget);
                attributes.removeManyToMany(((MTMRelationAttributeWidget) attributeWidget).getBaseElementSpec());
            }
        } else if (attributeWidget instanceof EmbeddedAttributeWidget) {
            if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
                singleValueEmbeddedAttributeWidgets.remove((SingleValueEmbeddedAttributeWidget) attributeWidget);
                attributes.removeEmbedded(((SingleValueEmbeddedAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                multiValueEmbeddedAttributeWidgets.remove((MultiValueEmbeddedAttributeWidget) attributeWidget);
                attributes.removeElementCollection(((MultiValueEmbeddedAttributeWidget) attributeWidget).getBaseElementSpec());
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        sortAttributes();
        scanDuplicateAttributes(attributeWidget.getBaseElementSpec().getName(), null);
        javaClass.updateArtifact((Attribute)attributeWidget.getBaseElementSpec());
    }

    public BasicAttributeWidget addBasicAttribute(String name) {
        return addBasicAttribute(name, null);
    }

    public BasicAttributeWidget addBasicAttribute(String name, Basic basic) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();

        if (basic == null) {
            basic = new Basic();
            basic.setId(NBModelerUtil.getAutoGeneratedStringId());
            basic.setAttributeType(String.class.getName());
            basic.setName(name);
            javaClass.getAttributes().addBasic(basic);
            addNamedQuery(basic, false);
        }
        BasicAttributeWidget attributeWidget = createPinWidget(basic, BasicAttributeWidget.class, 
                widgetInfo -> new BasicAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getBasicAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, basic.getName());
        return attributeWidget;
    }

    public BasicCollectionAttributeWidget addBasicCollectionAttribute(String name) {
        return addBasicCollectionAttribute(name, null);
    }

    public BasicCollectionAttributeWidget addBasicCollectionAttribute(String name, ElementCollection elementCollection) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();

        if (elementCollection == null) {
            elementCollection = new ElementCollection();
            elementCollection.setId(NBModelerUtil.getAutoGeneratedStringId());
            elementCollection.setTargetClass(String.class.getName());
            elementCollection.setName(name);
            elementCollection.setCollectionType(List.class.getName());
            elementCollection.setCollectionImplType(ArrayList.class.getName());
            javaClass.getAttributes().addElementCollection(elementCollection);
        }
        BasicCollectionAttributeWidget attributeWidget = createPinWidget(elementCollection, BasicCollectionAttributeWidget.class, 
                widgetInfo -> new BasicCollectionAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getBasicCollectionAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, elementCollection.getName());
        return attributeWidget;
    }

    public TransientAttributeWidget addTransientAttribute(String name) {
        return addTransientAttribute(name, null);
    }

    public TransientAttributeWidget addTransientAttribute(String name, Transient _transient) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        if (_transient == null) {
            _transient = new Transient();
            _transient.setId(NBModelerUtil.getAutoGeneratedStringId());
            _transient.setAttributeType(String.class.getName());
            _transient.setName(name);
            _transient.setIncludeInUI(false);
            javaClass.getAttributes().addTransient(_transient);
        }
        TransientAttributeWidget attributeWidget = createPinWidget(_transient, TransientAttributeWidget.class, 
                widgetInfo -> new TransientAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getTransientAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, _transient.getName());
        return attributeWidget;
    }

    public OTORelationAttributeWidget addOneToOneRelationAttribute(String name, boolean primaryKey) {
        return addOneToOneRelationAttribute(name, primaryKey,null);
    }

    public OTORelationAttributeWidget addOneToOneRelationAttribute(String name,boolean primaryKey, OneToOne oneToOne) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        if (oneToOne == null) {
            oneToOne = new OneToOne();
            oneToOne.setPrimaryKey(primaryKey);
            oneToOne.setId(NBModelerUtil.getAutoGeneratedStringId());
            oneToOne.setName(name);
            javaClass.getAttributes().addRelationAttribute(oneToOne);
        }
        OTORelationAttributeWidget attributeWidget = createPinWidget(oneToOne, OTORelationAttributeWidget.class, 
                widgetInfo -> new OTORelationAttributeWidget(this.getModelerScene(), this, widgetInfo));
        oneToOneRelationAttributeWidgets.add(attributeWidget);
        return attributeWidget;
    }

    public OTMRelationAttributeWidget addOneToManyRelationAttribute(String name) {
        return addOneToManyRelationAttribute(name, null);
    }

    public OTMRelationAttributeWidget addOneToManyRelationAttribute(String name, OneToMany oneToMany) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        if (oneToMany == null) {
            oneToMany = new OneToMany();
            oneToMany.setId(NBModelerUtil.getAutoGeneratedStringId());
            oneToMany.setName(name);
            oneToMany.setCollectionType(List.class.getName());
            oneToMany.setCollectionImplType(ArrayList.class.getName());
            javaClass.getAttributes().addRelationAttribute(oneToMany);
        }
        OTMRelationAttributeWidget attributeWidget = createPinWidget(oneToMany, OTMRelationAttributeWidget.class, 
                widgetInfo -> new OTMRelationAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getOneToManyRelationAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, oneToMany.getName());
        return attributeWidget;
    }

    public MTORelationAttributeWidget addManyToOneRelationAttribute(String name, boolean primaryKey) {
        return addManyToOneRelationAttribute(name, primaryKey, null);
    }

    public MTORelationAttributeWidget addManyToOneRelationAttribute(String name, boolean primaryKey, ManyToOne manyToOne) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        if (manyToOne == null) {
            manyToOne = new ManyToOne();
            manyToOne.setPrimaryKey(primaryKey);
            manyToOne.setId(NBModelerUtil.getAutoGeneratedStringId());
            manyToOne.setName(name);
            javaClass.getAttributes().addRelationAttribute(manyToOne);
        }
        MTORelationAttributeWidget attributeWidget = createPinWidget(manyToOne, MTORelationAttributeWidget.class, 
                widgetInfo -> new MTORelationAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getManyToOneRelationAttributeWidgets().add(attributeWidget);
        return attributeWidget;
    }

    public MTMRelationAttributeWidget addManyToManyRelationAttribute(String name) {
        return addManyToManyRelationAttribute(name, null);
    }

    public MTMRelationAttributeWidget addManyToManyRelationAttribute(String name, ManyToMany manyToMany) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
        if (manyToMany == null) {
            manyToMany = new ManyToMany();
            manyToMany.setId(NBModelerUtil.getAutoGeneratedStringId());
            manyToMany.setName(name);
            manyToMany.setCollectionType(List.class.getName());
            manyToMany.setCollectionImplType(ArrayList.class.getName());
            javaClass.getAttributes().addRelationAttribute(manyToMany);
        }
        MTMRelationAttributeWidget attributeWidget = createPinWidget(manyToMany, MTMRelationAttributeWidget.class, 
                widgetInfo -> new MTMRelationAttributeWidget(this.getModelerScene(), this, widgetInfo));
        getManyToManyRelationAttributeWidgets().add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, manyToMany.getName());
        return attributeWidget;
    }

    public SingleValueEmbeddedAttributeWidget addSingleValueEmbeddedAttribute(String name) {
        return addSingleValueEmbeddedAttribute(name, null);
    }

    public SingleValueEmbeddedAttributeWidget addSingleValueEmbeddedAttribute(String name, Embedded embedded) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (embedded == null) {
            embedded = new Embedded();
            embedded.setId(NBModelerUtil.getAutoGeneratedStringId());
            embedded.setName(name);
            javaClass.getAttributes().addEmbedded(embedded);
        }
        SingleValueEmbeddedAttributeWidget attributeWidget = createPinWidget(embedded, SingleValueEmbeddedAttributeWidget.class, 
                widgetInfo -> new SingleValueEmbeddedAttributeWidget(this.getModelerScene(), this, widgetInfo));
        singleValueEmbeddedAttributeWidgets.add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, embedded.getName());
        return attributeWidget;
    }

    public MultiValueEmbeddedAttributeWidget addMultiValueEmbeddedAttribute(String name) {
        return addMultiValueEmbeddedAttribute(name, null);
    }

    public MultiValueEmbeddedAttributeWidget addMultiValueEmbeddedAttribute(String name, ElementCollection elementCollection) {
        ManagedClass<? extends IPersistenceAttributes> javaClass = this.getBaseElementSpec();

        if (elementCollection == null) {
            elementCollection = new ElementCollection();
            elementCollection.setId(NBModelerUtil.getAutoGeneratedStringId());
            elementCollection.setName(name);
            elementCollection.setCollectionType(List.class.getName());
            elementCollection.setCollectionImplType(ArrayList.class.getName());
            javaClass.getAttributes().addElementCollection(elementCollection);
        }
        MultiValueEmbeddedAttributeWidget attributeWidget = createPinWidget(elementCollection, MultiValueEmbeddedAttributeWidget.class, 
                widgetInfo -> new MultiValueEmbeddedAttributeWidget(this.getModelerScene(), this, widgetInfo));
        multiValueEmbeddedAttributeWidgets.add(attributeWidget);
        sortAttributes();
        scanDuplicateAttributes(null, elementCollection.getName());
        return attributeWidget;
    }

    protected void addNamedQuery(Attribute attribute, boolean enable) {
        ManagedClass managedClass = this.getBaseElementSpec();
        if (managedClass instanceof Entity) {
            IdentifiableClass identifiableClass = (IdentifiableClass) managedClass;
            NamedQuery namedQuery;
            if(attribute!=null){
                namedQuery = NamedQuery.getTemplate(identifiableClass, attribute);
            } else {
                namedQuery = NamedQuery.getTemplate(identifiableClass);
            }
            namedQuery.setEnable(enable);
            identifiableClass.addNamedQuery(namedQuery);
        }
    }
    
    private void removeNamedQuery(Attribute attribute) {
        ManagedClass managedClass = this.getBaseElementSpec();
        if (CodePanel.isDeleteQuery() && managedClass instanceof Entity) {
            IdentifiableClass identifiableClass = (IdentifiableClass) managedClass;
            Optional<NamedQuery> value = identifiableClass.findNamedQuery(attribute);
            if(value.isPresent()){
             identifiableClass.removeNamedQuery(value.get());
            }
        }
    }
        
    public Map<String, List<Widget>> getAttributeCategories() {
        Map<String, List<Widget>> categories = new LinkedHashMap<>();
        List<Widget> basicAttributeCatWidget = new LinkedList<>(basicAttributeWidgets);
        basicAttributeCatWidget.addAll(basicCollectionAttributeWidgets);
        if (!basicAttributeCatWidget.isEmpty()) {
            categories.put("Basic", basicAttributeCatWidget);
        }
        
        List<Widget> embeddedAttributeWidgets = new LinkedList<>(singleValueEmbeddedAttributeWidgets);
        embeddedAttributeWidgets.addAll(multiValueEmbeddedAttributeWidgets);
        if (!embeddedAttributeWidgets.isEmpty()) {
            categories.put("Embedded", embeddedAttributeWidgets);
        }

        List<Widget> relationAttributeWidgets = new LinkedList<>(oneToOneRelationAttributeWidgets);
        relationAttributeWidgets.addAll(manyToOneRelationAttributeWidgets);
        relationAttributeWidgets.addAll(oneToManyRelationAttributeWidgets);
        relationAttributeWidgets.addAll(manyToManyRelationAttributeWidgets);
        if (!relationAttributeWidgets.isEmpty()) {
            categories.put("Relation", relationAttributeWidgets);
        }
        
        if (!transientAttributeWidgets.isEmpty()) {
            categories.put("Transient", new LinkedList<>(transientAttributeWidgets));
        }
        return categories;
    }

    public List<EmbeddedAttributeWidget> getEmbeddedAttributeWidgets() {
        List<EmbeddedAttributeWidget> list = new ArrayList<>(singleValueEmbeddedAttributeWidgets);
        list.addAll(multiValueEmbeddedAttributeWidgets);
        return list;
    }

    public List<RelationAttributeWidget> getRelationAttributeWidgets() {
        List<RelationAttributeWidget> list = new ArrayList<>(oneToOneRelationAttributeWidgets);
        list.addAll(oneToManyRelationAttributeWidgets);
        list.addAll(manyToOneRelationAttributeWidgets);
        list.addAll(manyToManyRelationAttributeWidgets);

        return list;
    }


    /**
     * @return the oneToOneRelationAttributeWidgets
     */
    public List<OTORelationAttributeWidget> getOneToOneRelationAttributeWidgets() {
        return oneToOneRelationAttributeWidgets;
    }

    /**
     * @return the oneToManyRelationAttributeWidgets
     */
    public List<OTMRelationAttributeWidget> getOneToManyRelationAttributeWidgets() {
        return oneToManyRelationAttributeWidgets;
    }

    /**
     * @return the manyToOneRelationAttributeWidgets
     */
    public List<MTORelationAttributeWidget> getManyToOneRelationAttributeWidgets() {
        return manyToOneRelationAttributeWidgets;
    } 

    /**
     * @return the manyToManyRelationAttributeWidgets
     */
    public List<MTMRelationAttributeWidget> getManyToManyRelationAttributeWidgets() {
        return manyToManyRelationAttributeWidgets;
    }

    /**
     * @return the transientAttributeWidgets
     */
    public List<TransientAttributeWidget> getTransientAttributeWidgets() {
        return transientAttributeWidgets;
    }

    /**
     * @return the inverseSideRelationFlowWidgets
     */
    public List<RelationFlowWidget> getInverseSideRelationFlowWidgets() {
        return inverseSideRelationFlowWidgets;
    }

    public void addInverseSideRelationFlowWidget(RelationFlowWidget relationFlowWidget) {
        inverseSideRelationFlowWidgets.add(relationFlowWidget);
    }

    public void removeInverseSideRelationFlowWidget(RelationFlowWidget relationFlowWidget) {
        inverseSideRelationFlowWidgets.remove(relationFlowWidget);
    }

    /**
     * @return the singleValueEmbeddedAttributeWidgets
     */
    public List<SingleValueEmbeddedAttributeWidget> getSingleValueEmbeddedAttributeWidgets() {
        return singleValueEmbeddedAttributeWidgets;
    }

    /**
     * @return the multiValueEmbeddedAttributeWidgets
     */
    public List<MultiValueEmbeddedAttributeWidget> getMultiValueEmbeddedAttributeWidgets() {
        return multiValueEmbeddedAttributeWidgets;
    }

    /**
     * @return the basicAttributeWidgets
     */
    public List<BasicAttributeWidget> getBasicAttributeWidgets() {
        return basicAttributeWidgets;
    }

    /**
     * @return the basicCollectionAttributeWidgets
     */
    public List<BasicCollectionAttributeWidget> getBasicCollectionAttributeWidgets() {
        return basicCollectionAttributeWidgets;
    }
    
    public List<AttributeWidget> getMapKeyAttributeWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>(basicCollectionAttributeWidgets);
        attributeWidgets.addAll(multiValueEmbeddedAttributeWidgets);
        attributeWidgets.addAll(manyToOneRelationAttributeWidgets);
        attributeWidgets.addAll(manyToManyRelationAttributeWidgets);
        return attributeWidgets;
    }
    
    public abstract List<AttributeWidget> getAttributeOverrideWidgets();
    
    @Deprecated //prefer PersistenceAttributes.getDerivedRelationAttributes
    public List<SingleRelationAttributeWidget> getDerivedRelationAttributeWidgets() {
        List<SingleRelationAttributeWidget> relationAttributeWidget = new ArrayList<>();
        oneToOneRelationAttributeWidgets.stream()
                .filter((oneToOneRelationAttributeWidget) -> (oneToOneRelationAttributeWidget.getBaseElementSpec().isPrimaryKey()))
                .forEach((oneToOneRelationAttributeWidget) -> {
                    relationAttributeWidget.add(oneToOneRelationAttributeWidget);
                });
        manyToOneRelationAttributeWidgets.stream()
                .filter((manyToOneRelationAttributeWidget) -> (manyToOneRelationAttributeWidget.getBaseElementSpec().isPrimaryKey()))
                .forEach((manyToOneRelationAttributeWidget) -> {
                    relationAttributeWidget.add(manyToOneRelationAttributeWidget);
                });
        return relationAttributeWidget;
    }

    private void refractorReference(String previousName, String newName) {
        if (previousName == null) {
            return;
        }
        ModelerFile modelerFile = this.getModelerScene().getModelerFile();
        RequestProcessor.getDefault().post(() -> {
            try {
                
                String singularPreName = previousName;
                String pluralPreName = English.plural(singularPreName);
                String singularNewName = newName;
                String pluralNewName = English.plural(singularNewName);
                
                String pluralPreVarName = StringHelper.firstLower(pluralPreName);
                String pluralNewVarName = StringHelper.firstLower(pluralNewName);
                String singularPreVarName = StringHelper.firstLower(singularPreName);
                String singularNewVarName = StringHelper.firstLower(singularNewName);
                for (RelationAttributeWidget attributeWidget : this.getAllRelationAttributeWidgets(true)) {
                    if (attributeWidget.getRelationFlowWidget() instanceof BidirectionalRelation) {
                        BidirectionalRelation flowWidget = (BidirectionalRelation) attributeWidget.getRelationFlowWidget();
                        RelationAttributeWidget<RelationAttribute> relationAttributeWidget = flowWidget.getTargetRelationAttributeWidget();
                        if (relationAttributeWidget == attributeWidget) { // refractoring not from owner side
                            relationAttributeWidget = flowWidget.getSourceRelationAttributeWidget();
                        }
                        if (relationAttributeWidget.getBaseElementSpec() instanceof MultiRelationAttribute) {
                            if (relationAttributeWidget.getName().equals(pluralPreVarName)) {
                                relationAttributeWidget.setName(pluralNewVarName);
                                relationAttributeWidget.setLabel(pluralNewVarName);
                            }
                        } else if (relationAttributeWidget.getName().equals(singularPreVarName)) {
                            relationAttributeWidget.setName(singularNewVarName);
                            relationAttributeWidget.setLabel(singularNewVarName);
                        }
                    }
                }

                if (this.getBaseElementSpec() instanceof Entity) {
                    for (RelationFlowWidget relationFlowWidget : ((EntityWidget) this).getUnidirectionalRelationFlowWidget()) {
                        RelationAttributeWidget<RelationAttribute> relationAttributeWidget = relationFlowWidget.getSourceRelationAttributeWidget();
                        if (relationAttributeWidget.getBaseElementSpec() instanceof MultiRelationAttribute) {
                            if (relationAttributeWidget.getName().equals(pluralPreVarName)) {
                                relationAttributeWidget.setName(pluralNewVarName);
                                relationAttributeWidget.setLabel(pluralNewVarName);
                            }
                        } else {
                            if (relationAttributeWidget.getName().equals(singularPreVarName)) {
                                relationAttributeWidget.setName(singularNewVarName);
                                relationAttributeWidget.setLabel(singularNewVarName);
                            }
                        }
                    }
                    ((EntityWidget) this).getBaseElementSpec().getNamedEntityGraph().forEach((NamedEntityGraph obj) -> {
                       if (!obj.refractorName(singularPreName, singularNewName)) {
                            obj.refractorName(pluralPreName, pluralNewName);
                        }
                    });
                }
                
                //Refractor NamedQuery, NamedNativeQuery
                if (this.getBaseElementSpec() instanceof IdentifiableClass) {
                    ((IdentifiableClass) this.getBaseElementSpec()).getNamedQuery().forEach((NamedQuery obj) -> {
                        obj.refractorName(singularPreName, singularNewName);
                        obj.refractorName(pluralPreName, pluralNewName);
                        obj.refractorQuery(singularPreName, singularNewName);
                        obj.refractorQuery(pluralPreName, pluralNewName);
                    });
                    ((IdentifiableClass) this.getBaseElementSpec()).getNamedNativeQuery().forEach((NamedNativeQuery obj) -> {
                        obj.refractorName(singularPreName, singularNewName);
                        obj.refractorName(pluralPreName, pluralNewName);
                        obj.refractorQuery(singularPreName, singularNewName);
                        obj.refractorQuery(pluralPreName, pluralNewName);
                    });
                }

                if (this instanceof EmbeddableWidget) {
                    for (EmbeddableFlowWidget embeddableFlowWidget : ((EmbeddableWidget) this).getIncomingEmbeddableFlowWidgets()) {
                        EmbeddedAttributeWidget embeddedAttributeWidget = embeddableFlowWidget.getSourceEmbeddedAttributeWidget();
                        if (embeddedAttributeWidget.getBaseElementSpec() instanceof ElementCollection) {
                            if (embeddedAttributeWidget.getName().equals(pluralPreVarName)) {
                                embeddedAttributeWidget.setName(pluralNewVarName);
                                embeddedAttributeWidget.setLabel(pluralNewVarName);
                            }
                        } else if (embeddedAttributeWidget.getName().equals(singularPreVarName)) {
                            embeddedAttributeWidget.setName(singularNewVarName);
                            embeddedAttributeWidget.setLabel(singularNewVarName);
                        }
                    }
                }

            } catch (Throwable t) {
                modelerFile.handleException(t);
            }
        });
    }

    @Override
    public void setName(String name) {
        String previousName = this.name;
        if (StringUtils.isNotBlank(name)) {
            this.name = filterName(name);
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                getBaseElementSpec().setClazz(this.name);
                refractorReference(previousName, this.name);
            }
            validateName(previousName, this.getName());
        }

    }

    @Override
    public void createPinWidget(SubCategoryNodeConfig subCategoryInfo) {
        createPinWidget(subCategoryInfo.getModelerDocument().getId().toUpperCase() + "_ATTRIBUTE");
    }

    @Override
    public void createPinWidget(String docId) {
        if (null != docId) {
            switch (docId) {
                case BASIC_ATTRIBUTE:
                    this.addBasicAttribute(getNextAttributeName()).edit();
                    break;
                case BASIC_COLLECTION_ATTRIBUTE:
                    this.addBasicCollectionAttribute(getNextAttributeName(null, true)).edit();
                    break;
                case TRANSIENT_ATTRIBUTE:
                    this.addTransientAttribute(getNextAttributeName()).edit();
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
            }
        }
    }
}