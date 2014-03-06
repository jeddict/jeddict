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

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.HierarchicalRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav_Gupta
 */
public class OTMRelationAttributeWidget extends RelationAttributeWidget {

    private HierarchicalRelationFlowWidget hierarchicalRelationFlowWidget;

    public OTMRelationAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    public static PinWidgetInfo create(String id, String name) {
        PinWidgetInfo pinWidgetInfo = AttributeWidget.create(id, name);
        pinWidgetInfo.setDocumentId(OTMRelationAttributeWidget.class.getSimpleName());
        return pinWidgetInfo;
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
        if (hierarchicalRelationFlowWidget instanceof Unidirectional) {
            this.setIcon(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/uotm-attribute.png"));
        } else {
            this.setIcon(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/botm-attribute.png"));
        }
    }

    @Override
    public RelationFlowWidget getRelationFlowWidget() {
        return hierarchicalRelationFlowWidget;
    }
}
