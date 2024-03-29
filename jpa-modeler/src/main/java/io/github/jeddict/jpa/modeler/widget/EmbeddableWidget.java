/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.IdAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.VersionAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.EmbeddableFlowWidget;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.EmbeddableAttributes;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.EMBEDDABLE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.EMBEDDABLE_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_EMBEDDABLE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.NOSQL_EMBEDDABLE_ICON_PATH;
import static java.lang.Boolean.TRUE;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

public class EmbeddableWidget extends PersistenceClassWidget<Embeddable> {

    public EmbeddableWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
    }

    private List<EmbeddableFlowWidget> incomingEmbeddableFlowWidgets = new ArrayList<>();  //no need for reverse relation

    @Override
    public void init() {
        super.init();
        Embeddable embeddable = this.getBaseElementSpec();
        if (embeddable.getAttributes() == null) {
            embeddable.setAttributes(new EmbeddableAttributes());
        }
        if (embeddable.getClazz() == null || embeddable.getClazz().isEmpty()) {
            embeddable.setClazz(this.getModelerScene().getNextClassName("Embeddable_"));
        }
        setName(embeddable.getClazz());
        setLabel(embeddable.getClazz());
        validateName(null, this.getName());

    }

    /**
     * @return the incomingEmbeddableFlowWidgets
     */
    public List<EmbeddableFlowWidget> getIncomingEmbeddableFlowWidgets() {
        return incomingEmbeddableFlowWidgets;
    }

    /**
     * @param incomingEmbeddableFlowWidgets the incomingEmbeddableFlowWidgets to
     * set
     */
    public void setIncomingEmbeddableFlowWidgets(List<EmbeddableFlowWidget> incomingEmbeddableFlowWidgets) {
        this.incomingEmbeddableFlowWidgets = incomingEmbeddableFlowWidgets;
    }

    public void addIncomingEmbeddableFlowWidget(EmbeddableFlowWidget incomingEmbeddableFlowWidget) {
        this.incomingEmbeddableFlowWidgets.add(incomingEmbeddableFlowWidget);
    }

    public void removeIncomingEmbeddableFlowWidget(EmbeddableFlowWidget incomingEmbeddableFlowWidget) {
        this.incomingEmbeddableFlowWidgets.remove(incomingEmbeddableFlowWidget);
    }
    
    @Override
    public InheritanceStateType getInheritanceState() {
        return getInheritanceState(false);
    }
    
    @Override
    public InheritanceStateType getInheritanceState(boolean includeAllClass){
        return InheritanceStateType.NONE;
    }
    

//    An embeddable class (including an embeddable class within another embeddable class) that is contained within an element collection must not contain an element collection, nor may it contain a relationship to an entity other than a many-to-one or one-to-one relationship
    @Override
    public List<AttributeWidget> getAttributeOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof EmbeddableWidget) {
            attributeWidgets.addAll(((EmbeddableWidget) classWidget).getAttributeOverrideWidgets());
        }
        attributeWidgets.addAll(getBasicAttributeWidgets());
        attributeWidgets.addAll(getBasicCollectionAttributeWidgets());
        return attributeWidgets;
    }

    public List<AttributeWidget> getAssociationOverrideWidgets() {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        JavaClassWidget classWidget = this.getSuperclassWidget(); //super class will get other attribute from its own super class
        if (classWidget instanceof EmbeddableWidget) {
            attributeWidgets.addAll(((EmbeddableWidget) classWidget).getAssociationOverrideWidgets());
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
        if (classWidget instanceof EmbeddableWidget) {
            attributeWidgets.addAll(((EmbeddableWidget) classWidget).getEmbeddedOverrideWidgets());
        }
        attributeWidgets.addAll(this.getSingleValueEmbeddedAttributeWidgets());
        attributeWidgets.addAll(this.getMultiValueEmbeddedAttributeWidgets());
        return attributeWidgets;
    }
    
    @Override
     public boolean isValidPinWidget(SubCategoryNodeConfig subCategoryInfo){
         if(subCategoryInfo.getModelerDocument().getWidget() == IdAttributeWidget.class ||
               subCategoryInfo.getModelerDocument().getWidget() == VersionAttributeWidget.class){
             return false;
         }
        return true;
    }
     
    @Override
    public String getIconPath() {
        if (TRUE.equals(this.getBaseElementSpec().getNoSQL())) {
            return NOSQL_EMBEDDABLE_ICON_PATH;
        } else {
            return EMBEDDABLE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (TRUE.equals(this.getBaseElementSpec().getNoSQL())) {
            return NOSQL_EMBEDDABLE_ICON;
        } else {
            return EMBEDDABLE_ICON;
        }
    }
    
    @Override
    public Embeddable createBaseElementSpec() {
        Embeddable embeddable = new Embeddable();
        Boolean isNoSQL = isNoSQL();
        if (isNoSQL != null) {
            embeddable.setNoSQL(isNoSQL);
        }
        return embeddable;
    }

}
