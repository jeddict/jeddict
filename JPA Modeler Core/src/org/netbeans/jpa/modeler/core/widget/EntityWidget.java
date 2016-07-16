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
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.BRANCH;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.LEAF;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getInheritenceProperty;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.MICRO_DB;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

public class EntityWidget extends PrimaryKeyContainerWidget<Entity> {

    private Boolean abstractEntity;
    private Set<RelationFlowWidget> unidirectionalRelationFlowWidget = new HashSet<>();

    public EntityWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyChangeListener("abstract", (input) -> setImage(getIcon()));

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
        scanKeyError();
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

        if (entity instanceof InheritenceHandler) {
            set.put("BASIC_PROP", getInheritenceProperty(EntityWidget.this));
        }

        set.put("QUERY", PropertiesHandler.getNamedQueryProperty("NamedQueries", "Named Queries", "", this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedNativeQueryProperty("NamedNativeQueries", "Named Native Queries", "", this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedStoredProcedureQueryProperty("NamedStoredProcedureQueries", "Named StoredProcedure Queries", "", this.getModelerScene(), entity));
        set.put("QUERY", PropertiesHandler.getNamedEntityGraphProperty("NamedEntityGraphs", "Named Entity Graphs", "", this));
        set.put("QUERY", PropertiesHandler.getResultSetMappingsProperty("ResultSetMappings", "ResultSet Mappings", "", this.getModelerScene(), entity));        
    }


    @Override
    public InheritenceStateType getInheritenceState() {
        GeneralizationFlowWidget outgoingGeneralizationFlowWidget = this.getOutgoingGeneralizationFlowWidget();
        List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = this.getIncomingGeneralizationFlowWidgets();
        if (outgoingGeneralizationFlowWidget != null && outgoingGeneralizationFlowWidget.getSuperclassWidget() != null &&
                !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
            outgoingGeneralizationFlowWidget = null;
        }
        InheritenceStateType type;
        if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = SINGLETON;
        } else if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = LEAF;
        } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = ROOT;
        } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = BRANCH;
        } else {
            throw new IllegalStateException("Illegal Inheritence State Exception Entity : " + this.getName());
        }
        return type;
    }

    public void scanKeyError() {
        scanPrimaryKeyError();
        scanCompositeKeyError();
    }

    public void scanPrimaryKeyError() {
        InheritenceStateType inheritenceState = this.getInheritenceState();
        if (SINGLETON == inheritenceState || ROOT == inheritenceState) {
            // Issue Fix #6041 Start
            boolean relationKey = this.getOneToOneRelationAttributeWidgets().stream().anyMatch(w -> w.getBaseElementSpec().isPrimaryKey()) ? true
                    : this.getManyToOneRelationAttributeWidgets().stream().anyMatch(w -> w.getBaseElementSpec().isPrimaryKey());

            if (this.getAllIdAttributeWidgets().isEmpty() && this.isCompositePKPropertyAllow() == CompositePKProperty.NONE && !relationKey) {
                getErrorHandler().throwError(EntityValidator.NO_PRIMARYKEY_EXIST);
            } else {
                getErrorHandler().clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
            }
            // Issue Fix #6041 End
        } else {
            getErrorHandler().clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
        }
    }

    public void scanCompositeKeyError() {
        //TODO
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        JMenuItem visDB = new JMenuItem("Micro DB", MICRO_DB);
        visDB.addActionListener((ActionEvent e) -> {
            ModelerFile file = this.getModelerScene().getModelerFile();
            JPAModelerUtil.openDBViewer(file, JPAModelerUtil.isolateEntityMapping(this.getModelerScene().getBaseElementSpec(), this.getBaseElementSpec()));
        });

        menuList.add(0, visDB);
        return menuList;
    }

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

}
