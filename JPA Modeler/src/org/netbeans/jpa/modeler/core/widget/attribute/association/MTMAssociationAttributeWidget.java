/**
 * Copyright [2018] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget.attribute.association;

import java.awt.Image;
import org.netbeans.jpa.modeler.core.widget.flow.association.MTMAssociationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.association.AssociationFlowWidget;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.jpa.modeler.core.widget.flow.association.UnidirectionalAssociation;
import org.netbeans.jpa.modeler.spec.bean.ManyToManyAssociation;

/**
 *
 * @author Gaurav_Gupta
 */
public class MTMAssociationAttributeWidget extends MultiAssociationAttributeWidget<ManyToManyAssociation> {

    private MTMAssociationFlowWidget manyToManyAssociationFlowWidget;

    public MTMAssociationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
    /**
     * @return the manyToManyAssociationFlowWidget
     */
    public MTMAssociationFlowWidget getManyToManyAssociationFlowWidget() {
        return manyToManyAssociationFlowWidget;
    }

    /**
     * @param manyToManyAssociationFlowWidget the manyToManyAssociationFlowWidget to
     * set
     */
    public void setManyToManyAssociationFlowWidget(MTMAssociationFlowWidget manyToManyAssociationFlowWidget) {
        this.manyToManyAssociationFlowWidget = manyToManyAssociationFlowWidget;
        this.setImage(this.getIcon());
    }

    @Override
    public String getIconPath() {
        if (manyToManyAssociationFlowWidget instanceof UnidirectionalAssociation) {
            return JPAModelerUtil.UMTM_ATTRIBUTE_ICON_PATH;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (manyToManyAssociationFlowWidget instanceof UnidirectionalAssociation) {
            return JPAModelerUtil.UMTM_ATTRIBUTE_ICON;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE_ICON;
        }
    }

    @Override
    public AssociationFlowWidget getAssociationFlowWidget() {
        return manyToManyAssociationFlowWidget;
    }

}
