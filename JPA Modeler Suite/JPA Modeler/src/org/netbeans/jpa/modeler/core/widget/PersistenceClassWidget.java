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
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.IAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ActionHandler;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ComboBoxListener;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.support.ComboBoxPropertySupport;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class PersistenceClassWidget extends JavaClassWidget {

    private final List<RelationFlowWidget> inverseSideRelationFlowWidgets = new ArrayList<RelationFlowWidget>();
    private EmbeddedIdAttributeWidget embeddedIdAttributeWidget;
    private final List<IdAttributeWidget> idAttributeWidgets = new ArrayList<IdAttributeWidget>();
    private final List<VersionAttributeWidget> versionAttributeWidgets = new ArrayList<VersionAttributeWidget>();
    private final List<BasicAttributeWidget> basicAttributeWidgets = new ArrayList<BasicAttributeWidget>();
    private final List<BasicCollectionAttributeWidget> basicCollectionAttributeWidgets = new ArrayList<BasicCollectionAttributeWidget>();
    private final List<TransientAttributeWidget> transientAttributeWidgets = new ArrayList<TransientAttributeWidget>();
    private final List<OTORelationAttributeWidget> oneToOneRelationAttributeWidgets = new ArrayList<OTORelationAttributeWidget>();
    private final List<OTMRelationAttributeWidget> oneToManyRelationAttributeWidgets = new ArrayList<OTMRelationAttributeWidget>();
    private final List<MTORelationAttributeWidget> manyToOneRelationAttributeWidgets = new ArrayList<MTORelationAttributeWidget>();
    private final List<MTMRelationAttributeWidget> manyToManyRelationAttributeWidgets = new ArrayList<MTMRelationAttributeWidget>();

    private final List<SingleValueEmbeddedAttributeWidget> singleValueEmbeddedAttributeWidgets = new ArrayList<SingleValueEmbeddedAttributeWidget>();
    private final List<MultiValueEmbeddedAttributeWidget> multiValueEmbeddedAttributeWidgets = new ArrayList<MultiValueEmbeddedAttributeWidget>();

    public PersistenceClassWidget(IModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyVisibilityHandler("compositePrimaryKeyType", new PropertyVisibilityHandler<String>() {
            @Override
            public boolean isVisible() {
                return PersistenceClassWidget.this.isCompositePKPropertyAllow();
            }
        });
        this.addPropertyVisibilityHandler("compositePrimaryKeyClass", new PropertyVisibilityHandler<String>() {
            @Override
            public boolean isVisible() {
//                if (PersistenceClassWidget.this.getBaseElementSpec() instanceof PrimaryKeyContainer) {
//                    PrimaryKeyContainer primaryKeyContainerSpec = (PrimaryKeyContainer) PersistenceClassWidget.this.getBaseElementSpec();
//                }
                return PersistenceClassWidget.this.isCompositePKPropertyAllow();
            }
        });

    }

    public boolean isCompositePKPropertyAllow() {
        if (this.getBaseElementSpec() instanceof PrimaryKeyContainer) {
            PrimaryKeyContainer primaryKeyContainerSpec = (PrimaryKeyContainer) this.getBaseElementSpec();
            String inheritenceState = this.getInheritenceState();
            boolean visible = false;
            if (this instanceof EntityWidget) {
                visible = getIdAttributeWidgets().size() > 1 && ("ROOT".equals(inheritenceState) || "SINGLETON".equals(inheritenceState));
            } else if (this instanceof MappedSuperclassWidget) {
                visible = getIdAttributeWidgets().size() > 1;
            }
            if (visible) {
                if ((primaryKeyContainerSpec.getCompositePrimaryKeyClass() == null || primaryKeyContainerSpec.getCompositePrimaryKeyClass().trim().isEmpty())
                        && (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID || primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS)) {
                    primaryKeyContainerSpec.manageCompositePrimaryKeyClass();
                    getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
                }
            }
            return visible;
        }
        return false;
    }

    public List<IdAttributeWidget> getAllIdAttributeWidgets() {
        List<IdAttributeWidget> idAttributeWidgets = new ArrayList<IdAttributeWidget>(this.getIdAttributeWidgets());
        List<JavaClassWidget> classWidgets = getAllSuperclassWidget();
        for (JavaClassWidget classWidget : classWidgets) {
            if (classWidget instanceof PersistenceClassWidget) {
                idAttributeWidgets.addAll(((PersistenceClassWidget) classWidget).getIdAttributeWidgets());
            }
        }
        return idAttributeWidgets;
    }

    public List<EmbeddedIdAttributeWidget> getAllEmbeddedIdAttributeWidgets() {
        List<EmbeddedIdAttributeWidget> embeddedIdAttributeWidgets = new ArrayList<EmbeddedIdAttributeWidget>();
        embeddedIdAttributeWidgets.add(this.getEmbeddedIdAttributeWidget());
        List<JavaClassWidget> classWidgets = getAllSuperclassWidget();
        for (JavaClassWidget classWidget : classWidgets) {
            if (classWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) classWidget;
                if (persistenceClassWidget.getEmbeddedIdAttributeWidget() != null) {
                    embeddedIdAttributeWidgets.add(persistenceClassWidget.getEmbeddedIdAttributeWidget());
                }
            }
        }
        return embeddedIdAttributeWidgets;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        if (this.getBaseElementSpec() instanceof PrimaryKeyContainer) {
            set.put("BASIC_PROP", getCompositePrimaryKeyProperty());
        }

    }

    private ComboBoxPropertySupport getCompositePrimaryKeyProperty() {
        final JavaClassWidget javaClassWidget = this;
        final PrimaryKeyContainer primaryKeyContainerSpec = (PrimaryKeyContainer) javaClassWidget.getBaseElementSpec();
        ComboBoxListener comboBoxListener = new ComboBoxListener() {
            @Override
            public void setItem(ComboBoxValue value) {
                CompositePrimaryKeyType compositePrimaryKeyType = (CompositePrimaryKeyType) value.getValue();
                if (compositePrimaryKeyType == CompositePrimaryKeyType.EMBEDDEDID) {
                    PersistenceClassWidget.this.addNewEmbeddedIdAttribute(getNextAttributeName(PersistenceClassWidget.this.getName() + "EmbeddedId"));
                } else {
                    if (embeddedIdAttributeWidget != null) {
                        embeddedIdAttributeWidget.remove();
                    }
                }
                primaryKeyContainerSpec.setCompositePrimaryKeyType(compositePrimaryKeyType);
            }

            @Override
            public ComboBoxValue getItem() {
                if (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                    return new ComboBoxValue(CompositePrimaryKeyType.EMBEDDEDID, "Embedded Id");
                } else if (primaryKeyContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                    return new ComboBoxValue(CompositePrimaryKeyType.IDCLASS, "Id Class");
                } else {
                    return new ComboBoxValue(CompositePrimaryKeyType.NONE, "None");
                }
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                List<ComboBoxValue> values = new ArrayList<ComboBoxValue>();
                values.add(new ComboBoxValue(CompositePrimaryKeyType.NONE, "None"));
                values.add(new ComboBoxValue(CompositePrimaryKeyType.IDCLASS, "Id Class"));
                values.add(new ComboBoxValue(CompositePrimaryKeyType.EMBEDDEDID, "Embedded Id"));
                return values;
            }

            @Override
            public String getDefaultText() {
                return "None";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(this.getModelerScene().getModelerFile(), "compositePrimaryKeyType", "Composite PrimaryKey Type", "", comboBoxListener);
    }

    public RelationAttributeWidget findRelationAttributeWidget(String id, Class<? extends RelationAttributeWidget>... relationAttributeWidgetClasses) {
//        List<RelationAttributeWidget> relationAttributeWidgets = new ArrayList<RelationAttributeWidget>();
//        relationAttributeWidgets.addAll(oneToOneRelationAttributeWidgets);
//        relationAttributeWidgets.addAll(oneToManyRelationAttributeWidgets);
//        relationAttributeWidgets.addAll(manyToOneRelationAttributeWidgets);
//        relationAttributeWidgets.addAll(manyToManyRelationAttributeWidgets);

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
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
        IAttributes attributes = (IAttributes) javaClass.getAttributes();
        if (attributeWidget == null) {
            return;
        }
        if (attributeWidget instanceof IdAttributeWidget) {
            getIdAttributeWidgets().remove((IdAttributeWidget) attributeWidget);
            ((IPersistenceAttributes) attributes).getId().remove((Id) ((IdAttributeWidget) attributeWidget).getBaseElementSpec());
            AttributeValidator.validateEmbeddedIdAndIdFound(this);
        } else if (attributeWidget instanceof EmbeddedIdAttributeWidget) {
            embeddedIdAttributeWidget = null;
            ((IPersistenceAttributes) attributes).setEmbeddedId(null);
            AttributeValidator.validateMultipleEmbeddedIdFound(this);
            AttributeValidator.validateEmbeddedIdAndIdFound(this);
        } else if (attributeWidget instanceof VersionAttributeWidget) {
            versionAttributeWidgets.remove((VersionAttributeWidget) attributeWidget);
            ((IPersistenceAttributes) attributes).getVersion().remove((Version) ((VersionAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof BasicAttributeWidget) {
            basicAttributeWidgets.remove((BasicAttributeWidget) attributeWidget);
            attributes.getBasic().remove((Basic) ((BasicAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
            basicCollectionAttributeWidgets.remove((BasicCollectionAttributeWidget) attributeWidget);
            attributes.getElementCollection().remove((ElementCollection) ((BasicCollectionAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof TransientAttributeWidget) {
            transientAttributeWidgets.remove((TransientAttributeWidget) attributeWidget);
            attributes.getTransient().remove((Transient) ((TransientAttributeWidget) attributeWidget).getBaseElementSpec());
        } else if (attributeWidget instanceof RelationAttributeWidget) {
            if (attributeWidget instanceof OTORelationAttributeWidget) {
                OTORelationAttributeWidget otoRelationAttributeWidget = (OTORelationAttributeWidget) attributeWidget;
                otoRelationAttributeWidget.setLocked(true);
                otoRelationAttributeWidget.getOneToOneRelationFlowWidget().remove();
                otoRelationAttributeWidget.setLocked(false);
                oneToOneRelationAttributeWidgets.remove((OTORelationAttributeWidget) attributeWidget);
                attributes.getOneToOne().remove((OneToOne) ((OTORelationAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof OTMRelationAttributeWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget = (OTMRelationAttributeWidget) attributeWidget;
                otmRelationAttributeWidget.setLocked(true);
                otmRelationAttributeWidget.getHierarchicalRelationFlowWidget().remove();
                otmRelationAttributeWidget.setLocked(false);
                oneToManyRelationAttributeWidgets.remove((OTMRelationAttributeWidget) attributeWidget);
                attributes.getOneToMany().remove((OneToMany) ((OTMRelationAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof MTORelationAttributeWidget) {
                MTORelationAttributeWidget mtoRelationAttributeWidget = (MTORelationAttributeWidget) attributeWidget;
                mtoRelationAttributeWidget.setLocked(true);
                mtoRelationAttributeWidget.getManyToOneRelationFlowWidget().remove();
                mtoRelationAttributeWidget.setLocked(false);
                manyToOneRelationAttributeWidgets.remove((MTORelationAttributeWidget) attributeWidget);
                attributes.getManyToOne().remove((ManyToOne) ((MTORelationAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof MTMRelationAttributeWidget) {
                MTMRelationAttributeWidget mtmRelationAttributeWidget = (MTMRelationAttributeWidget) attributeWidget;
                mtmRelationAttributeWidget.setLocked(true);
                mtmRelationAttributeWidget.getManyToManyRelationFlowWidget().remove();
                mtmRelationAttributeWidget.setLocked(false);
                manyToManyRelationAttributeWidgets.remove((MTMRelationAttributeWidget) attributeWidget);
                attributes.getManyToMany().remove((ManyToMany) ((MTMRelationAttributeWidget) attributeWidget).getBaseElementSpec());
            }
        } else if (attributeWidget instanceof EmbeddedAttributeWidget) {
            if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
                singleValueEmbeddedAttributeWidgets.remove((SingleValueEmbeddedAttributeWidget) attributeWidget);
                attributes.getEmbedded().remove((Embedded) ((SingleValueEmbeddedAttributeWidget) attributeWidget).getBaseElementSpec());
            } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
                multiValueEmbeddedAttributeWidgets.remove((MultiValueEmbeddedAttributeWidget) attributeWidget);
                attributes.getElementCollection().remove((ElementCollection) ((MultiValueEmbeddedAttributeWidget) attributeWidget).getBaseElementSpec());
            } else {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
        }
        sortAttributes();
    }

    public EmbeddedIdAttributeWidget addNewEmbeddedIdAttribute(String name) {
        return addNewEmbeddedIdAttribute(name, null);
    }

    public EmbeddedIdAttributeWidget addNewEmbeddedIdAttribute(String name, EmbeddedId embeddedId) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
        if (embeddedId == null) {
            embeddedId = new EmbeddedId();
            embeddedId.setId(NBModelerUtil.getAutoGeneratedStringId());
            embeddedId.setName(name);
            ((IPersistenceAttributes) javaClass.getAttributes()).setEmbeddedId(embeddedId);
        }

        EmbeddedIdAttributeWidget attributeWidget = (EmbeddedIdAttributeWidget) PersistenceClassWidget.this.createPinWidget(EmbeddedIdAttributeWidget.create(embeddedId.getId(), name));
        setEmbeddedIdAttributeWidget(attributeWidget);
        attributeWidget.setBaseElementSpec(embeddedId);
        sortAttributes();
        AttributeValidator.validateMultipleEmbeddedIdFound(this);
        AttributeValidator.validateEmbeddedIdAndIdFound(this);
        return attributeWidget;
    }

    public IdAttributeWidget addNewIdAttribute(String name) {
        IdAttributeWidget idAttributeWidget = addNewIdAttribute(name, null);
        isCompositePKPropertyAllow();//to update default CompositePK class , type //for manual created attribute
        return idAttributeWidget;
    }

    public IdAttributeWidget addNewIdAttribute(String name, Id id) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (id == null) {
            id = new Id();
            id.setId(NBModelerUtil.getAutoGeneratedStringId());
            id.setAttributeType("Long");
            id.setName(name);
            ((IPersistenceAttributes) javaClass.getAttributes()).getId().add(id);

        }

        IdAttributeWidget attributeWidget = (IdAttributeWidget) PersistenceClassWidget.this.createPinWidget(IdAttributeWidget.create(id.getId(), name));
        getIdAttributeWidgets().add(attributeWidget);
        attributeWidget.setBaseElementSpec(id);
        sortAttributes();
        AttributeValidator.validateEmbeddedIdAndIdFound(this);
        return attributeWidget;
    }

    public VersionAttributeWidget addNewVersionAttribute(String name) {
        return addNewVersionAttribute(name, null);
    }

    public VersionAttributeWidget addNewVersionAttribute(String name, Version version) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (version == null) {
            version = new Version();
            version.setId(NBModelerUtil.getAutoGeneratedStringId());
            version.setAttributeType("int");
            version.setName(name);
            ((IPersistenceAttributes) javaClass.getAttributes()).getVersion().add(version);

        }

        VersionAttributeWidget attributeWidget = (VersionAttributeWidget) PersistenceClassWidget.this.createPinWidget(VersionAttributeWidget.create(version.getId(), name));
        versionAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(version);
        sortAttributes();
        return attributeWidget;
    }

    public BasicAttributeWidget addNewBasicAttribute(String name) {
        return addNewBasicAttribute(name, null);
    }

    public BasicAttributeWidget addNewBasicAttribute(String name, Basic basic) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();

        if (basic == null) {
            basic = new Basic();
            basic.setId(NBModelerUtil.getAutoGeneratedStringId());
            basic.setAttributeType("String");
            basic.setName(name);
            javaClass.getAttributes().getBasic().add(basic);
        }
        BasicAttributeWidget attributeWidget = (BasicAttributeWidget) PersistenceClassWidget.this.createPinWidget(BasicAttributeWidget.create(basic.getId(), name));
        basicAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(basic);
        sortAttributes();
        return attributeWidget;
    }

    public BasicCollectionAttributeWidget addNewBasicCollectionAttribute(String name) {
        return addNewBasicCollectionAttribute(name, null);
    }

    public BasicCollectionAttributeWidget addNewBasicCollectionAttribute(String name, ElementCollection elementCollection) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();

        if (elementCollection == null) {
            elementCollection = new ElementCollection();
            elementCollection.setId(NBModelerUtil.getAutoGeneratedStringId());
            elementCollection.setAttributeType("String");
            elementCollection.setName(name);
            javaClass.getAttributes().getElementCollection().add(elementCollection);
        }
        BasicCollectionAttributeWidget attributeWidget = (BasicCollectionAttributeWidget) PersistenceClassWidget.this.createPinWidget(BasicCollectionAttributeWidget.create(elementCollection.getId(), name));
        basicCollectionAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(elementCollection);
        sortAttributes();
        return attributeWidget;
    }

    public TransientAttributeWidget addNewTransientAttribute(String name) {
        return addNewTransientAttribute(name, null);
    }

    public TransientAttributeWidget addNewTransientAttribute(String name, Transient _transient) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (_transient == null) {
            _transient = new Transient();
            _transient.setId(NBModelerUtil.getAutoGeneratedStringId());
            _transient.setAttributeType("String");
            _transient.setName(name);
            javaClass.getAttributes().getTransient().add(_transient);
        }
        TransientAttributeWidget attributeWidget = (TransientAttributeWidget) PersistenceClassWidget.this.createPinWidget(TransientAttributeWidget.create(_transient.getId(), name));
        getTransientAttributeWidgets().add(attributeWidget);
        attributeWidget.setBaseElementSpec(_transient);
        sortAttributes();
        return attributeWidget;
    }

    public OTORelationAttributeWidget addNewOneToOneRelationAttribute(String name) {
        return addNewOneToOneRelationAttribute(name, null);
    }

    public OTORelationAttributeWidget addNewOneToOneRelationAttribute(String name, OneToOne oneToOne) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (oneToOne == null) {
            oneToOne = new OneToOne();
            oneToOne.setId(NBModelerUtil.getAutoGeneratedStringId());
            oneToOne.setName(name);
            javaClass.getAttributes().getOneToOne().add(oneToOne);
        }

        OTORelationAttributeWidget attributeWidget = (OTORelationAttributeWidget) PersistenceClassWidget.this.createPinWidget(OTORelationAttributeWidget.create(oneToOne.getId(), name));
        oneToOneRelationAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(oneToOne);
        sortAttributes();
        return attributeWidget;
    }

    public OTMRelationAttributeWidget addNewOneToManyRelationAttribute(String name) {
        return addNewOneToManyRelationAttribute(name, null);
    }

    public OTMRelationAttributeWidget addNewOneToManyRelationAttribute(String name, OneToMany oneToMany) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (oneToMany == null) {
            oneToMany = new OneToMany();
            oneToMany.setId(NBModelerUtil.getAutoGeneratedStringId());
            oneToMany.setName(name);
            javaClass.getAttributes().getOneToMany().add(oneToMany);
        }
        OTMRelationAttributeWidget attributeWidget = (OTMRelationAttributeWidget) PersistenceClassWidget.this.createPinWidget(OTMRelationAttributeWidget.create(oneToMany.getId(), name));
        getOneToManyRelationAttributeWidgets().add(attributeWidget);
        attributeWidget.setBaseElementSpec(oneToMany);
        sortAttributes();
        return attributeWidget;
    }

    public MTORelationAttributeWidget addNewManyToOneRelationAttribute(String name) {
        return addNewManyToOneRelationAttribute(name, null);
    }

    public MTORelationAttributeWidget addNewManyToOneRelationAttribute(String name, ManyToOne manyToOne) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (manyToOne == null) {
            manyToOne = new ManyToOne();
            manyToOne.setId(NBModelerUtil.getAutoGeneratedStringId());
            manyToOne.setName(name);
            javaClass.getAttributes().getManyToOne().add(manyToOne);
        }
        MTORelationAttributeWidget attributeWidget = (MTORelationAttributeWidget) PersistenceClassWidget.this.createPinWidget(MTORelationAttributeWidget.create(manyToOne.getId(), name));
        getManyToOneRelationAttributeWidgets().add(attributeWidget);
        attributeWidget.setBaseElementSpec(manyToOne);
        sortAttributes();
        return attributeWidget;
    }

    public MTMRelationAttributeWidget addNewManyToManyRelationAttribute(String name) {
        return addNewManyToManyRelationAttribute(name, null);
    }

    public MTMRelationAttributeWidget addNewManyToManyRelationAttribute(String name, ManyToMany manyToMany) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (manyToMany == null) {
            manyToMany = new ManyToMany();
            manyToMany.setId(NBModelerUtil.getAutoGeneratedStringId());
            manyToMany.setName(name);
            javaClass.getAttributes().getManyToMany().add(manyToMany);
        }
        MTMRelationAttributeWidget attributeWidget = (MTMRelationAttributeWidget) PersistenceClassWidget.this.createPinWidget(MTMRelationAttributeWidget.create(manyToMany.getId(), name));
        getManyToManyRelationAttributeWidgets().add(attributeWidget);
        attributeWidget.setBaseElementSpec(manyToMany);
        sortAttributes();
        return attributeWidget;
    }

    public SingleValueEmbeddedAttributeWidget addNewSingleValueEmbeddedAttribute(String name) {
        return addNewSingleValueEmbeddedAttribute(name, null);
    }

    public SingleValueEmbeddedAttributeWidget addNewSingleValueEmbeddedAttribute(String name, Embedded embedded) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
//        if (javaClass.getAttributes() == null) {
//            javaClass.setAttributes(new Attributes());
//        }
        if (embedded == null) {
            embedded = new Embedded();
            embedded.setId(NBModelerUtil.getAutoGeneratedStringId());
            embedded.setName(name);
            javaClass.getAttributes().getEmbedded().add(embedded);
        }
        SingleValueEmbeddedAttributeWidget attributeWidget = (SingleValueEmbeddedAttributeWidget) PersistenceClassWidget.this.createPinWidget(SingleValueEmbeddedAttributeWidget.create(embedded.getId(), name));
        singleValueEmbeddedAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(embedded);
        sortAttributes();
        return attributeWidget;
    }

    public MultiValueEmbeddedAttributeWidget addNewMultiValueEmbeddedAttribute(String name) {
        return addNewMultiValueEmbeddedAttribute(name, null);
    }

    public MultiValueEmbeddedAttributeWidget addNewMultiValueEmbeddedAttribute(String name, ElementCollection elementCollection) {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();

        if (elementCollection == null) {
            elementCollection = new ElementCollection();
            elementCollection.setId(NBModelerUtil.getAutoGeneratedStringId());
            elementCollection.setName(name);
            javaClass.getAttributes().getElementCollection().add(elementCollection);
        }
        MultiValueEmbeddedAttributeWidget attributeWidget = (MultiValueEmbeddedAttributeWidget) PersistenceClassWidget.this.createPinWidget(MultiValueEmbeddedAttributeWidget.create(elementCollection.getId(), name));
        multiValueEmbeddedAttributeWidgets.add(attributeWidget);
        attributeWidget.setBaseElementSpec(elementCollection);
        sortAttributes();
        return attributeWidget;
    }

    public String getNextAttributeName() {
        return getNextAttributeName(null);
    }

    public String getNextAttributeName(String attrName) {
        int index = 0;
        if (attrName == null || attrName.trim().isEmpty()) {
            attrName = "attribute";
        }
        attrName = Character.toLowerCase(attrName.charAt(0)) + (attrName.length() > 1 ? attrName.substring(1) : "");
        String nextAttrName = attrName + ++index;
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
        if (javaClass.getAttributes() == null) {
            return nextAttrName;
        }
        boolean isExist = true;
        while (isExist) {
            if (javaClass.getAttributes().isAttributeExist(nextAttrName)) {
                isExist = true;
                nextAttrName = attrName + ++index;
            } else {
                return nextAttrName;
            }
        }
        return nextAttrName;
    }

    @Override//sortAttributes() method should be called only onec in case of loadDocument
    public void sortAttributes() {
        Map<String, List<Widget>> categories = new LinkedHashMap<String, List<Widget>>();
        if (embeddedIdAttributeWidget != null) {
            List<Widget> embeddedIdAttributeCatWidget = new ArrayList<Widget>();
            embeddedIdAttributeCatWidget.add(embeddedIdAttributeWidget);
            categories.put("Embedded Id", embeddedIdAttributeCatWidget);
        }
        if (!idAttributeWidgets.isEmpty()) {
            List<Widget> idAttributeCatWidget = new ArrayList<Widget>();
            for (IdAttributeWidget idAttributeWidget : getIdAttributeWidgets()) {
                idAttributeCatWidget.add(idAttributeWidget);
            }
            categories.put("PrimaryKey", idAttributeCatWidget);
        }

        List<Widget> basicAttributeCatWidget = new ArrayList<Widget>();
        for (BasicAttributeWidget basicAttributeWidget : basicAttributeWidgets) {
            basicAttributeCatWidget.add(basicAttributeWidget);
        }
        for (BasicCollectionAttributeWidget basicCollectionAttributeWidget : basicCollectionAttributeWidgets) {
            basicAttributeCatWidget.add(basicCollectionAttributeWidget);
        }
        if (!basicAttributeCatWidget.isEmpty()) {
            categories.put("Basic", basicAttributeCatWidget);
        }
        List<EmbeddedAttributeWidget> embeddedAttributeWidgets = new LinkedList<EmbeddedAttributeWidget>(singleValueEmbeddedAttributeWidgets);
        embeddedAttributeWidgets.addAll(multiValueEmbeddedAttributeWidgets);
        if (!embeddedAttributeWidgets.isEmpty()) {
            List<Widget> embeddedAttributeCatWidget = new ArrayList<Widget>();
            for (EmbeddedAttributeWidget embeddedAttributeWidget : embeddedAttributeWidgets) {
                embeddedAttributeCatWidget.add(embeddedAttributeWidget);
            }
            categories.put("Embedded", embeddedAttributeCatWidget);
        }

        List<RelationAttributeWidget> relationAttributeWidgets = new LinkedList<RelationAttributeWidget>(oneToOneRelationAttributeWidgets);
        relationAttributeWidgets.addAll(oneToManyRelationAttributeWidgets);
        relationAttributeWidgets.addAll(manyToOneRelationAttributeWidgets);
        relationAttributeWidgets.addAll(manyToManyRelationAttributeWidgets);

        if (!relationAttributeWidgets.isEmpty()) {
            List<Widget> relationAttributeCatWidget = new ArrayList<Widget>();
            for (RelationAttributeWidget relationAttributeWidget : relationAttributeWidgets) {
                relationAttributeCatWidget.add(relationAttributeWidget);
            }
            categories.put("Relation", relationAttributeCatWidget);
        }
        if (!versionAttributeWidgets.isEmpty()) {
            List<Widget> versionAttributeCatWidget = new ArrayList<Widget>();
            for (VersionAttributeWidget versionAttributeWidget : versionAttributeWidgets) {
                versionAttributeCatWidget.add(versionAttributeWidget);
            }
            categories.put("Version", versionAttributeCatWidget);
        }
        if (!transientAttributeWidgets.isEmpty()) {
            List<Widget> transientAttributeCatWidget = new ArrayList<Widget>();
            for (TransientAttributeWidget transientAttributeWidget : transientAttributeWidgets) {
                transientAttributeCatWidget.add(transientAttributeWidget);
            }
            categories.put("Transient", transientAttributeCatWidget);
        }
        this.sortPins(categories);
    }

    public List<EmbeddedAttributeWidget> getEmbeddedAttributeWidgets() {
        List list = new ArrayList(singleValueEmbeddedAttributeWidgets);
        list.addAll(multiValueEmbeddedAttributeWidgets);
        return list;
    }

    public List<RelationAttributeWidget> getRelationAttributeWidgets() {
        List list = new ArrayList(oneToOneRelationAttributeWidgets);
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
     * @return the idAttributeWidgets
     */
    public List<IdAttributeWidget> getIdAttributeWidgets() {
        return idAttributeWidgets;
    }

//    public abstract void scanError();
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
}
