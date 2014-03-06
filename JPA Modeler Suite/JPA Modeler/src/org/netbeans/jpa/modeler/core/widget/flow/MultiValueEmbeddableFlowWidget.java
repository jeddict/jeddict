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

import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.node.vmd.internal.PNBColorScheme;
import static org.netbeans.modeler.widget.node.vmd.internal.PNBColorScheme.COLOR60_HOVER;
import static org.netbeans.modeler.widget.node.vmd.internal.PNBColorScheme.COLOR60_SELECT;
import org.netbeans.modeler.widget.node.vmd.internal.PNodeAnchor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gaurav Gupta
 */
public class MultiValueEmbeddableFlowWidget extends EmbeddableFlowWidget {

    private static final MultiValueEmbeddableFlowWidgetColorScheme embeddableScheme = new MultiValueEmbeddableFlowWidgetColorScheme();

    public MultiValueEmbeddableFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge, embeddableScheme);

//        setSourceAnchorShape(new DiamondAnchorShape(10, new Color(100, 130, 180), true, -5));
//        setSourceAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/composition-anchor.png"), true));
//        setTargetAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/modules/visual/resources/vmd-pin-60.png"), true));
    }

    static class MultiValueEmbeddableFlowWidgetColorScheme extends PNBColorScheme {

        @Override
        public void installUI(PEdgeWidget widget) {
//            widget.setSourceAnchorShape(new DiamondAnchorShape(20, new Color(100, 130, 180), true, 10));
            widget.setSourceAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/composition-anchor.png"), true));
            widget.setTargetAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/multi-value-anchor-shape.png"), true, 18, 20));
//            widget.setTargetAnchorShape(new IconAnchorShape(ImageUtilities.loadImage("org/netbeans/modules/visual/resources/vmd-pin-60.png"), true, 18, 7));
            widget.setPaintControlPoints(true);
        }

        @Override
        public void updateUI(PEdgeWidget widget, ObjectState previousState, ObjectState state) {
            if (state.isSelected()) {
                widget.setForeground(COLOR60_SELECT);
            } else if (state.isHighlighted()) {
                widget.setForeground(COLOR_HIGHLIGHTED);
            } else if (state.isHovered() || state.isFocused()) {
                widget.setForeground(COLOR60_HOVER);
            } else {
                widget.setForeground(COLOR_NORMAL);
            }

            if (state.isSelected()) {
                widget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
                widget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
                widget.setControlPointCutDistance(0);
            } else if (state.isHovered()) {
                widget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
                widget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
                widget.setControlPointCutDistance(0);
            } else {

                widget.setControlPointShape(PointShape.NONE);
                widget.setEndPointShape(PointShape.NONE);
                widget.setControlPointCutDistance(5);
            }
        }

        @Override
        public int getNodeAnchorGap(PNodeAnchor anchor) {
            return 0;
        }

    }
}
