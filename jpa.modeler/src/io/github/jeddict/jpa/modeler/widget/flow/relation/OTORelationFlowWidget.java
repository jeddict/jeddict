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
package io.github.jeddict.jpa.modeler.widget.flow.relation;

import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.OTOR_SOURCE_ANCHOR_SHAPE;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.OTOR_TARGET_ANCHOR_SHAPE;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class OTORelationFlowWidget extends RelationFlowWidget {

    private static final IconAnchorShape SOURCE_ANCHOR_SHAPE = new IconAnchorShape(OTOR_SOURCE_ANCHOR_SHAPE, true);
    private static final IconAnchorShape TARGET_ANCHOR_SHAPE = new IconAnchorShape(OTOR_TARGET_ANCHOR_SHAPE, true);

    public OTORelationFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setSourceAnchorShape(SOURCE_ANCHOR_SHAPE);
        setTargetAnchorShape(TARGET_ANCHOR_SHAPE);
    }

}
