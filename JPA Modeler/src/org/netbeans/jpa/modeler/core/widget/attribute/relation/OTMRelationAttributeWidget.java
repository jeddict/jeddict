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
import org.netbeans.jpa.modeler.core.widget.flow.relation.HierarchicalRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UnidirectionalRelation;

/**
 *
 * @author Gaurav_Gupta
 */
public class OTMRelationAttributeWidget extends MultiRelationAttributeWidget<OneToMany> {

    private HierarchicalRelationFlowWidget hierarchicalRelationFlowWidget;

    public OTMRelationAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    /**
     * @return the hierarchicalRelationFlowWidget
     */
    public HierarchicalRelationFlowWidget getHierarchicalRelationFlowWidget() {
        return hierarchicalRelationFlowWidget;
    }

    /**
     * @param hierarchicalRelationFlowWidget the hierarchicalRelationFlowWidget
     * to set
     */
    public void setHierarchicalRelationFlowWidget(HierarchicalRelationFlowWidget hierarchicalRelationFlowWidget) {
        this.hierarchicalRelationFlowWidget = hierarchicalRelationFlowWidget;
        this.setImage(this.getIcon());
    }

    @Override
    public String getIconPath() {
        if (hierarchicalRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UOTM_ATTRIBUTE_ICON_PATH;
        } else {
            return JPAModelerUtil.BOTM_ATTRIBUTE_ICON_PATH;
        }
    }

    @Override
    public Image getIcon() {
        if (hierarchicalRelationFlowWidget instanceof UnidirectionalRelation) {
            return JPAModelerUtil.UOTM_ATTRIBUTE_ICON;
        } else {
            return JPAModelerUtil.BOTM_ATTRIBUTE_ICON;
        }
    }

    @Override
    public RelationFlowWidget getRelationFlowWidget() {
        return hierarchicalRelationFlowWidget;
    }
}
