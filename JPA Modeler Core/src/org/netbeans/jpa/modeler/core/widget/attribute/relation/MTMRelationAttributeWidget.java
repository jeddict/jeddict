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
package org.netbeans.jpa.modeler.core.widget.attribute.relation;

import java.awt.Image;
import org.netbeans.jpa.modeler.core.widget.flow.relation.MTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class MTMRelationAttributeWidget extends MultiRelationAttributeWidget<ManyToMany> {

    private MTMRelationFlowWidget manyToManyRelationFlowWidget;

    public MTMRelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
    @Override
    public void init() {
        super.init();
        AttributeValidator.scanMapKeyHandlerError(this);
    }

    /**
     * @return the manyToManyRelationFlowWidget
     */
    public MTMRelationFlowWidget getManyToManyRelationFlowWidget() {
        return manyToManyRelationFlowWidget;
    }

    /**
     * @param manyToManyRelationFlowWidget the manyToManyRelationFlowWidget to
     * set
     */
    public void setManyToManyRelationFlowWidget(MTMRelationFlowWidget manyToManyRelationFlowWidget) {
        this.manyToManyRelationFlowWidget = manyToManyRelationFlowWidget;
        this.setImage(this.getIcon());
    }

    @Override
    public String getIconPath() {
        if (manyToManyRelationFlowWidget instanceof Unidirectional) {
            return JPAModelerUtil.UMTM_ATTRIBUTE_ICON_PATH;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (manyToManyRelationFlowWidget instanceof Unidirectional) {
            return JPAModelerUtil.UMTM_ATTRIBUTE;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE;
        }
    }

    @Override
    public RelationFlowWidget getRelationFlowWidget() {
        return manyToManyRelationFlowWidget;
    }

}
