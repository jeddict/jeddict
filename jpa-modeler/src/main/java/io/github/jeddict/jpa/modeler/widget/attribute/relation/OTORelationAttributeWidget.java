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
package io.github.jeddict.jpa.modeler.widget.attribute.relation;

import java.awt.Image;
import io.github.jeddict.jpa.modeler.widget.flow.relation.OTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.RelationFlowWidget;
import io.github.jeddict.jpa.spec.OneToOne;
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
public class OTORelationAttributeWidget extends SingleRelationAttributeWidget<OneToOne> {

    private OTORelationFlowWidget oneToOneRelationFlowWidget;

    public OTORelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    /**
     * @return the oneToOneRelationFlowWidget
     */
    public OTORelationFlowWidget getOneToOneRelationFlowWidget() {
        return oneToOneRelationFlowWidget;
    }

    /**
     * @param oneToOneRelationFlowWidget the oneToOneRelationFlowWidget to set
     */
    public void setOneToOneRelationFlowWidget(OTORelationFlowWidget oneToOneRelationFlowWidget) {
        this.oneToOneRelationFlowWidget = oneToOneRelationFlowWidget;
        this.setImage(this.getIcon());
    }

    @Override
    public String getIconPath() {
        if (getBaseElementSpec().isPrimaryKey()) {
            if (oneToOneRelationFlowWidget instanceof UnidirectionalRelation) {
                return JPAModelerUtil.PK_UOTO_ATTRIBUTE_ICON_PATH;
            } else {
                return JPAModelerUtil.PK_BOTO_ATTRIBUTE_ICON_PATH;
            }
        } else if (oneToOneRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UOTO_ATTRIBUTE_ICON_PATH;
        } else {
            return JPAModelerUtil.BOTO_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (getBaseElementSpec().isPrimaryKey()) {
            if (oneToOneRelationFlowWidget instanceof UnidirectionalRelation) {
                return JPAModelerUtil.PK_UOTO_ATTRIBUTE_ICON;
            } else {
                return JPAModelerUtil.PK_BOTO_ATTRIBUTE_ICON;
            }
        } else if (oneToOneRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UOTO_ATTRIBUTE_ICON;
        } else {
            return JPAModelerUtil.BOTO_ATTRIBUTE_ICON;
        }
    }

    @Override
    public RelationFlowWidget getRelationFlowWidget() {
        return oneToOneRelationFlowWidget;
    }

}
