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
package io.github.jeddict.jpa.modeler.widget.attribute.relation;

import java.awt.Image;
import io.github.jeddict.jpa.modeler.widget.flow.relation.MTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.RelationFlowWidget;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UnidirectionalRelation;

/**
 *
 * @author Gaurav_Gupta
 */
public class MTMRelationAttributeWidget extends MultiRelationAttributeWidget<ManyToMany> {

    private MTMRelationFlowWidget manyToManyRelationFlowWidget;

    public MTMRelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
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
        if (manyToManyRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UMTM_ATTRIBUTE_ICON_PATH;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (manyToManyRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UMTM_ATTRIBUTE_ICON;
        } else {
            return JPAModelerUtil.BMTM_ATTRIBUTE_ICON;
        }
    }

    @Override
    public RelationFlowWidget getRelationFlowWidget() {
        return manyToManyRelationFlowWidget;
    }

}
