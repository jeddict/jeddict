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
package io.github.jeddict.jpa.modeler.widget.attribute.association;

import io.github.jeddict.jpa.modeler.widget.BeanClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.AssociationFlowWidget;
import io.github.jeddict.jpa.spec.bean.AssociationAttribute;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class AssociationAttributeWidget<E extends AssociationAttribute> extends AttributeWidget<E> {

    public AssociationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    @Override
    public void init() {
        this.getClassWidget().scanDuplicateAttributes(null, this.name);
        validateName(null, this.getName());
        addOpenSourceCodeAction();
    }

    public void setConnectedSibling(BeanClassWidget classWidget) {
        AssociationAttribute associationAttribute = this.getBaseElementSpec();
        associationAttribute.setConnectedClass(classWidget.getBaseElementSpec());
        setAttributeTooltip();
        visualizeDataType();
    }

    public void setConnectedSibling(BeanClassWidget classWidget, AssociationAttributeWidget<AssociationAttribute> attributeWidget) {
        AssociationAttribute associationAttribute = this.getBaseElementSpec();
        associationAttribute.setConnectedClass(classWidget.getBaseElementSpec());
        associationAttribute.setConnectedAttribute(attributeWidget.getBaseElementSpec());
        setAttributeTooltip();
        visualizeDataType();
    }

    public abstract AssociationFlowWidget getAssociationFlowWidget();
    
    public BeanClassWidget getConnectedClassWidget(){
            IFlowElementWidget flowElementWidget = ((AssociationAttribute)getBaseElementSpec()).isOwner() ? 
                    this.getAssociationFlowWidget().getTargetWidget() : 
                    this.getAssociationFlowWidget().getSourceWidget();
            BeanClassWidget connectedClassWidget = null;
            if (flowElementWidget instanceof BeanClassWidget) {
                connectedClassWidget = (BeanClassWidget) flowElementWidget;
            } else if (flowElementWidget instanceof AssociationAttributeWidget) {
                connectedClassWidget = (BeanClassWidget) ((AssociationAttributeWidget) flowElementWidget).getClassWidget();
            }
            return connectedClassWidget;
    }

}
