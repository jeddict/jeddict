/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.widget.flow.relation;

import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class UMTORelationFlowWidget extends MTORelationFlowWidget implements UnidirectionalRelation {

    private EntityWidget targetEntityWidget;

    public UMTORelationFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
    }

    /**
     * @return the targetEntityWidget
     */
    @Override
    public EntityWidget getTargetEntityWidget() {
        return targetEntityWidget;
    }

    /**
     * @param targetEntityWidget the targetEntityWidget to set
     */
    @Override
    public void setTargetEntityWidget(EntityWidget targetEntityWidget) {
        this.targetEntityWidget = targetEntityWidget;
        if (targetEntityWidget != null) {
            targetEntityWidget.addUnidirectionalRelationFlowWidget(this);
        }
    }

    @Override
    public IFlowElementWidget getTargetWidget() {
        return targetEntityWidget;
    }

    @Override
    public void destroy() {
        if (targetEntityWidget != null) {
            targetEntityWidget.removeUnidirectionalRelationFlowWidget(this);
        }
    }

}
