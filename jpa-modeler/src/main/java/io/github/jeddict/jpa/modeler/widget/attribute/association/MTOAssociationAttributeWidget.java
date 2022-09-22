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
package io.github.jeddict.jpa.modeler.widget.attribute.association;

import java.awt.Image;
import io.github.jeddict.jpa.modeler.widget.flow.association.MTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.AssociationFlowWidget;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import io.github.jeddict.jpa.modeler.widget.flow.association.UnidirectionalAssociation;
import io.github.jeddict.jpa.spec.bean.ManyToOneAssociation;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BMTO_ATTRIBUTE_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.UMTO_ATTRIBUTE_ICON_PATH;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.BMTO_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.UMTO_ATTRIBUTE_ICON;

/**
 *
 * @author Gaurav_Gupta
 */
public class MTOAssociationAttributeWidget extends SingleAssociationAttributeWidget<ManyToOneAssociation> {

    private MTOAssociationFlowWidget manyToOneAssociationFlowWidget;

    public MTOAssociationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    /**
     * @return the manyToOneAssociationFlowWidget
     */
    public MTOAssociationFlowWidget getManyToOneAssociationFlowWidget() {
        return manyToOneAssociationFlowWidget;
    }

    /**
     * @param manyToOneAssociationFlowWidget the manyToOneAssociationFlowWidget
     * to set
     */
    public void setManyToOneAssociationFlowWidget(MTOAssociationFlowWidget manyToOneAssociationFlowWidget) {
        this.manyToOneAssociationFlowWidget = manyToOneAssociationFlowWidget;
        this.setImage(this.getIcon());
    }

    @Override
    public String getIconPath() {
        if (manyToOneAssociationFlowWidget instanceof UnidirectionalAssociation) {
            return UMTO_ATTRIBUTE_ICON_PATH;
        } else {
            return BMTO_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (manyToOneAssociationFlowWidget instanceof UnidirectionalAssociation) {
            return UMTO_ATTRIBUTE_ICON;
        } else {
            return BMTO_ATTRIBUTE_ICON;
        }
    }

    @Override
    public AssociationFlowWidget getAssociationFlowWidget() {
        return manyToOneAssociationFlowWidget;
    }

}
