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
package org.netbeans.jpa.modeler.core.widget.flow;

import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav Gupta
 */
public class SingleValueEmbeddableFlowWidget extends EmbeddableFlowWidget {

    private static final IconAnchorShape SOURCE_ANCHOR_SHAPE = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/composition-anchor.png"), true);
    private static final IconAnchorShape TARGET_ANCHOR_SHAPE = new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/single-value-anchor-shape.png"), true, 18, 7);

    public SingleValueEmbeddableFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setSourceAnchorShape(SOURCE_ANCHOR_SHAPE);
        setTargetAnchorShape(TARGET_ANCHOR_SHAPE);
    }

}
