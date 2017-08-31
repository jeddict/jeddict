/**
 * Copyright [2013] Gaurav Gupta
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

import java.awt.Image;
import static java.lang.Boolean.TRUE;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.BRANCH;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.LEAF;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getCacheableProperty;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getConvertProperties;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getEntityDisplayProperty;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getInheritanceProperty;
import org.netbeans.jpa.modeler.rules.entity.ClassValidator;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.extend.InheritanceHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
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
        this.addPropertyChangeListener("abstract", (oldValue, value) -> {
            setImage(getIcon());
            scanDiscriminatorValue();
        });
        PropertyVisibilityHandler overridePropertyHandler = () -> {
            InheritanceStateType inheritanceState = this.getInheritanceState(true);
            return inheritanceState == InheritanceStateType.BRANCH || inheritanceState == InheritanceStateType.LEAF;
        };
        this.addPropertyVisibilityHandler("AttributeOverrides", overridePropertyHandler);
        this.addPropertyVisibilityHandler("AssociationOverrides", overridePropertyHandler);
    }

    @Override
    public void init() {
        super.init();
        Entity entity = this.getBaseElementSpec();
        if (entity.getAttributes().getAllAttribute().isEmpty() && this.getModelerScene().getModelerFile().isLoaded()) {
            addNewIdAttribute("id");
        }

        if (entity.getClazz() == null || entity.getClazz().isEmpty()) {
            entity.setClazz(this.getModelerScene().getNextClassName("Entity_"));
            addNamedQuery(null, false);
        }

        setName(entity.getClazz());
        setLabel(entity.getClazz());
        this.setImage(getIcon());
        validateName(null, this.getName());
    }
    
    @Override
    public String getIconPath() {
        if (this.getBaseElementSpec().getAbstract()) {
            return JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH;
        } else {
            return JPAModelerUtil.ENTITY_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (this.getBaseElementSpec().getAbstract()) {
            return JPAModelerUtil.ABSTRACT_ENTITY;
        } else {
            return JPAModelerUtil.ENTITY;
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
        
        set.put("ENTITY_PROP", PropertiesHandler.getPrimaryKeyJoinColumnsProperty("PrimaryKeyJoinColumns", "PrimaryKey Join Columns", "", this, entity));
        set.put("ENTITY_PROP", PropertiesHandler.getAttributeOverridesProperty("AttributeOverrides", "Attribute Overrides", "", this.getModelerScene(), entity.getAttributeOverride()));
        set.put("ENTITY_PROP", PropertiesHandler.getAssociationOverridesProperty("AssociationOverrides", "Association Overrides", "", this.getModelerScene(), entity.getAssociationOverride()));
                
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
            
            if (this.getAllIdAttributeWidgets().isEmpty() && this.isCompositePKPropertyAllow() == CompositePKProperty.NONE && !relationKey) {
                getSignalManager().fire(ERROR, ClassValidator.NO_PRIMARYKEY_EXIST);
            } else {
                getSignalManager().clear(ERROR, ClassValidator.NO_PRIMARYKEY_EXIST);
            }
            // Issue Fix #6041 End
          List<String> idGenList = this.getAllIdAttributeWidgets().stream().filter(idAttrWid -> idAttrWid.getBaseElementSpec().getGeneratedValue()!=null &&
                    idAttrWid.getBaseElementSpec().getGeneratedValue().getStrategy()!=null).map(IdAttributeWidget::getName).collect(toList());
            if(idGenList.size()> 1){
               getSignalManager().fire(ERROR, ClassValidator.MANY_PRIMARYKEY_GEN_EXIST, idGenList.toString());
            } else {
                getSignalManager().clear(ERROR, ClassValidator.MANY_PRIMARYKEY_GEN_EXIST);
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
        
        if(isAbstract || SINGLETON == type) {
            getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
        }else if(ROOT == type) {
                evaluateDiscriminatorValue(classSpec.getInheritance());
        }else if(BRANCH == type) {
            if (!evaluateDiscriminatorValue(classSpec.getInheritance())) {
                if (this.getSuperclassWidget().getBaseElementSpec() instanceof InheritanceHandler) {
                    evaluateDiscriminatorValue(((InheritanceHandler) this.getSuperclassWidget().getBaseElementSpec()).getInheritance());
                }
            }
        }else if(LEAF == type) {
            if(this.getSuperclassWidget().getBaseElementSpec() instanceof InheritanceHandler){
                evaluateDiscriminatorValue(((InheritanceHandler) this.getSuperclassWidget().getBaseElementSpec()).getInheritance());
            }
        }
    }
    
    private boolean evaluateDiscriminatorValue(Inheritance inheritance){
        boolean status = true;
        InheritanceHandler classSpec = (InheritanceHandler) this.getBaseElementSpec();
        if (inheritance != null && inheritance.getStrategy() == InheritanceType.TABLE_PER_CLASS) {
                getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
            } else {
                if (StringUtils.isBlank(classSpec.getDiscriminatorValue())) {
                    getSignalManager().fire(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
                    status = false;
                } else {
                    getSignalManager().clear(WARNING, ClassValidator.NO_DISCRIMINATOR_VALUE_EXIST);
                }
            }
        return status;
    }
    
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

    public boolean addUnidirectionalRelationFlowWidget(RelationFlowWidget e) {
        return getUnidirectionalRelationFlowWidget().add(e);
    }

    public boolean removeUnidirectionalRelationFlowWidget(RelationFlowWidget e) {
        return getUnidirectionalRelationFlowWidget().remove(e);
    }

    public void clearUnidirectionalRelationFlowWidget() {
        getUnidirectionalRelationFlowWidget().clear();
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
    
    @Override
    public Entity createBaseElementSpec() {
        Entity entity = new Entity();
        Boolean isAbstract = isAbstractEntity();
        if (isAbstract != null) {
            entity.setAbstract(isAbstract);
        }
        return entity;
    }

}
