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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.PlainClass;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

public class PlainClassWidget extends JavaClassWidget<PlainClass> {

    public PlainClassWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
    }
    
    @Override
    public String getIconPath() {
        if (this.getBaseElementSpec().getAbstract()) {
            return JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH;
        } else {
            return JPAModelerUtil.JAVA_CLASS_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (this.getBaseElementSpec().getAbstract()) {
            return JPAModelerUtil.ABSTRACT_ENTITY;
        } else {
            return JPAModelerUtil.JAVA_CLASS;
        }
    }
    

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
       
    }

    @Override
    public void deleteAttribute(AttributeWidget attributeWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sortAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InheritanceStateType getInheritanceState() {
        return getInheritanceState(false);
    }
    
    @Override
    public InheritanceStateType getInheritanceState(boolean includeAllClass){
        return InheritanceStateType.NONE;
    }

    @Override
    public void scanDuplicateAttributes(String previousName, String newName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets(boolean includeParentClassAttibute) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createPinWidget(SubCategoryNodeConfig subCategoryInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    @Override
    public PlainClass createBaseElementSpec() {
        return new PlainClass();
    }

    
        public void createPinWidget(String docId) {
        if (null != docId) {
            switch (docId) {
//                case "BASIC_ATTRIBUTE":
//                    this.addNewBasicAttribute(getNextAttributeName()).edit();
//                    break;
//                case "BASIC_COLLECTION_ATTRIBUTE":
//                    this.addNewBasicCollectionAttribute(getNextAttributeName(null, true)).edit();
//                    break;
//                case "TRANSIENT_ATTRIBUTE":
//                    this.addNewTransientAttribute(getNextAttributeName()).edit();
//                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        }
            }
        }
    }
    
}
