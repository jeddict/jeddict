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
package io.github.jeddict.jpa.modeler.widget;

import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ABSTRACT_ENTITY_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ENTITY_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ENTITY_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_ABSTRACT_ENTITY_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_ABSTRACT_ENTITY_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_ENTITY_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_ENTITY_ICON_PATH;
import io.github.jeddict.jpa.modeler.properties.PropertiesHandler;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCacheableProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getConvertProperties;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getEntityDisplayProperty;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getInheritanceProperty;
import io.github.jeddict.jpa.modeler.rules.entity.ClassValidator;
import static io.github.jeddict.jpa.modeler.rules.entity.ClassValidator.MANY_PRIMARYKEY_GEN_EXIST;
import static io.github.jeddict.jpa.modeler.rules.entity.ClassValidator.NO_PRIMARYKEY_EXIST;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.BRANCH;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.LEAF;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.ROOT;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.RelationFlowWidget;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.InheritanceType;
import io.github.jeddict.jpa.spec.extend.InheritanceHandler;
import java.awt.Image;
import static java.lang.Boolean.TRUE;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static io.github.jeddict.util.StringUtils.isBlank;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.WARNING;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

public class EntityWidget extends PrimaryKeyContainerWidget<Entity> {

    private Boolean abstractEntity;
    private Set<RelationFlowWidget> unidirectionalRelationFlowWidget = new HashSet<>();

    public EntityWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        if("AbstractEntity".equals(nodeWidgetInfo.getModelerDocument().getId())){
            setAbstractEntity(true);
        }
        this.addPropertyChangeListener("abstract", (oldValue, value) -> {
            setImage(getIcon());
            scanDiscriminatorValue();
        });
        PropertyVisibilityHandler overridePropertyHandler = () -> {
            InheritanceStateType inheritanceState = this.getInheritanceState(true);
            return (inheritanceState == BRANCH || inheritanceState == LEAF) && !this.getBaseElementSpec().getNoSQL();
        };
        this.addPropertyVisibilityHandler("AttributeOverrides", overridePropertyHandler);
        this.addPropertyVisibilityHandler("AssociationOverrides", overridePropertyHandler);
        
        PropertyVisibilityHandler noSQLDisabled = () -> !this.getBaseElementSpec().getNoSQL();
        this.addPropertyVisibilityHandler("NamedQueries", noSQLDisabled);
        this.addPropertyVisibilityHandler("NamedStoredProcedureQueries", noSQLDisabled);
        this.addPropertyVisibilityHandler("ResultSetMappings", noSQLDisabled);
        this.addPropertyVisibilityHandler("NamedNativeQueries", noSQLDisabled);
        this.addPropertyVisibilityHandler("NamedEntityGraphs", noSQLDisabled);
        this.addPropertyVisibilityHandler("cacheable", noSQLDisabled);
        this.addPropertyVisibilityHandler("converters", noSQLDisabled);
    }

    @Override
    public void init() {
        super.init();
        Entity entity = this.getBaseElementSpec();
        if (entity.getAttributes().getAllAttribute().isEmpty() && this.getModelerScene().getModelerFile().isLoaded()) {
            addIdAttribute("id");
        }   

        if (entity.getClazz() == null || entity.getClazz().isEmpty()) {
            entity.setClazz(this.getModelerScene().getNextClassName("Entity_"));
            addNamedQuery(null, false);
        }

        setName(entity.getClazz());
        setLabel(entity.getClazz());
        this.setImage(getIcon());
        validateName(null, this.getName());
        scanKeyError();
    }
    
    @Override
    public String getIconPath() {
        if (TRUE.equals(this.getBaseElementSpec().getNoSQL())) {
            if (this.getBaseElementSpec().getAbstract()) {
                return NOSQL_ABSTRACT_ENTITY_ICON_PATH;
            } else {
                return NOSQL_ENTITY_ICON_PATH;
            }
        } else {
            if (this.getBaseElementSpec().getAbstract()) {
                return ABSTRACT_ENTITY_ICON_PATH;
            } else {
                return ENTITY_ICON_PATH;
            }
        }
    }

    @Override
    public Image getIcon() {
        if (TRUE.equals(this.getBaseElementSpec().getNoSQL())) {
            if (this.getBaseElementSpec().getAbstract()) {
                return NOSQL_ABSTRACT_ENTITY_ICON;
            } else {
                return NOSQL_ENTITY_ICON;
            }
        } else {
            if (this.getBaseElementSpec().getAbstract()) {
                return ABSTRACT_ENTITY_ICON;
            } else {
                return ENTITY_ICON;
            }
        }
    }
    

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        Entity entity = this.getBaseElementSpec();
        set.createPropertySet(this, entity.getTable(), getPropertyChangeListeners());

        if (entity instanceof InheritanceHandler) {
            set.put("ENTITY_PROP", getInheritanceProperty(this));
        }
        set.put("ENTITY_PROP", getCacheableProperty(this));
        set.put("ENTITY_PROP", getConvertProperties(this.getModelerScene(), entity));
        set.put("ENTITY_PROP", PropertiesHandler.getPrimaryKeyJoinColumnsProperty(this, entity));
        set.put("ENTITY_PROP", PropertiesHandler.getAttributeOverridesProperty(this.getModelerScene(), entity.getAttributeOverride()));
        set.put("ENTITY_PROP", PropertiesHandler.getAssociationOverridesProperty(this.getModelerScene(), entity.getAssociationOverride()));
                
        set.put("UI_PROP", getEntityDisplayProperty(this));

        set.put("QUERY", PropertiesHandler.getNamedQueryProperty(this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedNativeQueryProperty(this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedStoredProcedureQueryProperty(this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedEntityGraphProperty(this));
        set.put("QUERY", PropertiesHandler.getResultSetMappingsProperty(this.getModelerScene(), entity));        
    }

    @Override
    public InheritanceStateType getInheritanceState() {
        return getInheritanceState(false);   
    }
    @Override
    public InheritanceStateType getInheritanceState(boolean includeAllClass) {
        GeneralizationFlowWidget outgoingGeneralizationFlowWidget = this.getOutgoingGeneralizationFlowWidget();
        List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = this.getIncomingGeneralizationFlowWidgets();
        if (outgoingGeneralizationFlowWidget != null && outgoingGeneralizationFlowWidget.getSuperclassWidget() != null &&
          !includeAllClass && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
            outgoingGeneralizationFlowWidget = null;
        }
        InheritanceStateType type;
        if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = SINGLETON;
        } else if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = LEAF;
        } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = ROOT;
        } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = BRANCH;
        } else {
            throw new IllegalStateException("Illegal Inheritance State Exception Entity : " + this.getName());
        }
        return type;
    }

    public void scanKeyError() {
        scanPrimaryKeyError();
        scanCompositeKeyError();
    }

    public void scanPrimaryKeyError() {
        InheritanceStateType inheritanceState = this.getInheritanceState();
        if (SINGLETON == inheritanceState || ROOT == inheritanceState) {
            // Issue Fix #6041 Start
            boolean relationKey = this.getOneToOneRelationAttributeWidgets().stream().anyMatch(w -> w.getBaseElementSpec().isPrimaryKey()) ? true
                    : this.getManyToOneRelationAttributeWidgets().stream().anyMatch(w -> w.getBaseElementSpec().isPrimaryKey());
            List<Id> ids = this.getBaseElementSpec().getAttributes().getSuperId();
            if (ids.isEmpty() && this.isCompositePKPropertyAllow() == CompositePKProperty.NONE && !relationKey) {
                getSignalManager().fire(ERROR, NO_PRIMARYKEY_EXIST);
            } else {
                getSignalManager().clear(ERROR, NO_PRIMARYKEY_EXIST);
            }
            List<String> idGenList = ids.stream()
                    .filter(idAttr -> idAttr.getGeneratedValue() != null)
                    .filter(idAttr -> idAttr.getGeneratedValue().getStrategy() != null)
                    .map(Id::getName)
                    .collect(toList());
            if (idGenList.size() > 1) {
                getSignalManager().fire(ERROR, MANY_PRIMARYKEY_GEN_EXIST, idGenList.toString());
            } else {
                getSignalManager().clear(ERROR, MANY_PRIMARYKEY_GEN_EXIST);
            }
        } else {
            getSignalManager().clear(ERROR, ClassValidator.NO_PRIMARYKEY_EXIST);
        }
    }
    
    public void scanCompositeKeyError() {
        //TODO
    }

    public void scanDiscriminatorValue() {
        InheritanceHandler classSpec = (InheritanceHandler) this.getBaseElementSpec();
        InheritanceStateType type = this.getInheritanceState();
        boolean isAbstract = TRUE.equals(this.getBaseElementSpec().getAbstract()); 
        
        if (isAbstract || SINGLETON == type) {
            getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
        } else if (ROOT == type) {
            evaluateDiscriminatorValue(classSpec.getInheritance());
        } else if (BRANCH == type) {
            if (!evaluateDiscriminatorValue(classSpec.getInheritance())) {
                if (this.getSuperclassWidget().getBaseElementSpec() instanceof InheritanceHandler) {
                    evaluateDiscriminatorValue(((InheritanceHandler) this.getSuperclassWidget().getBaseElementSpec()).getInheritance());
                }
            }
        } else if (LEAF == type) {
            if (this.getSuperclassWidget().getBaseElementSpec() instanceof InheritanceHandler) {
                evaluateDiscriminatorValue(((InheritanceHandler) this.getSuperclassWidget().getBaseElementSpec()).getInheritance());
            }
        }
//        duplicateDiscriminatorValue();
    }
    
    private boolean evaluateDiscriminatorValue(Inheritance inheritance){
        boolean status = true;
        InheritanceHandler classSpec = (InheritanceHandler) this.getBaseElementSpec();
        if (inheritance != null
                && inheritance.getStrategy() == InheritanceType.TABLE_PER_CLASS) {
            getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
        } else if (isBlank(classSpec.getDiscriminatorValue())) {
            getSignalManager().fire(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
            status = false;
        } else {
            getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
        }
        return status;
    }

//    public void duplicateDiscriminatorValue() {
//        InheritanceStateType type = this.getInheritanceState();
//        if (ROOT == type) {
//            this.getSubclassWidgets();
//        }
//    }
    
//    @Override
//    protected List<JMenuItem> getPopupMenuItemList() {
//        List<JMenuItem> menuList = super.getPopupMenuItemList();
//        JMenuItem visDB = new JMenuItem("Micro DB", MICRO_DB);
//        visDB.addActionListener((ActionEvent e) -> {
//            ModelerFile file = this.getModelerScene().getModelerFile();
//            Entity entity = this.getBaseElementSpec();
//            DBUtil.openDBViewer(file, DBUtil.isolateEntityMapping(this.getModelerScene().getBaseElementSpec(), entity));
//        });
//
//        menuList.add(0, visDB);
//        return menuList;
//    }

    /**
     * @return the abstractEntity
     */
    public Boolean isAbstractEntity() {
        return abstractEntity;
    }

    /**
     * @param abstractEntity the abstractEntity to set
     */
    public void setAbstractEntity(Boolean abstractEntity) {
        this.abstractEntity = abstractEntity;
    }
    
    /**
     * @return the unidirectionalRelationFlowWidget
     */
    public Set<RelationFlowWidget> getUnidirectionalRelationFlowWidget() {
        return unidirectionalRelationFlowWidget;
    }

    /**
     * @param unidirectionalRelationFlowWidget the unidirectionalRelationFlowWidget to set
     */
    public void setUnidirectionalRelationFlowWidget(Set<RelationFlowWidget> unidirectionalRelationFlowWidget) {
        this.unidirectionalRelationFlowWidget = unidirectionalRelationFlowWidget;
    }
    
    public boolean addUnidirectionalRelationFlowWidget(RelationFlowWidget e) {
        return unidirectionalRelationFlowWidget.add(e);
    }

    public boolean removeUnidirectionalRelationFlowWidget(RelationFlowWidget e) {
        return unidirectionalRelationFlowWidget.remove(e);
    }

    public void clearUnidirectionalRelationFlowWidget() {
        unidirectionalRelationFlowWidget.clear();
    }
    
    @Override
    public Entity createBaseElementSpec() {
        Entity entity = new Entity();
        Boolean isAbstract = isAbstractEntity();
        if (isAbstract != null) {
            entity.setAbstract(isAbstract);
        }
        Boolean isNoSQL = isNoSQL();
        if (isNoSQL != null) {
            entity.setNoSQL(isNoSQL);
        }
        return entity;
    }

}
