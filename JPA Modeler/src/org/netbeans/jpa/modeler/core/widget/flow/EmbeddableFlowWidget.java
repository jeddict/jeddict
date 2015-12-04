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
 *//**
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

import java.awt.Color;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author Gaurav_Gupta
 */
public abstract class EmbeddableFlowWidget extends AbstractEdgeWidget {

    private EmbeddableWidget targetEmbeddableWidget;
    private EmbeddedAttributeWidget sourceEmbeddedAttributeWidget;

    public EmbeddableFlowWidget(IModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        setAnchorGap(0);

    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    /**
     * @return the sourceNode
     */
//    public IFlowNodeWidget getSourceNode() {
//        return sourceNode;
//    }
    /**
     * @param sourceNode the sourceNode to set
     */
    public void setSourceNode(FlowNodeWidget sourceNode) {

    }

    /**
     * @return the targetNode
     */
//    public IFlowNodeWidget getTargetNode() {
//        return targetNode;
//    }
    /**
     * @param targetNode the targetNode to set
     */
    public void setTargetNode(FlowNodeWidget targetNode) {

    }

    private Color color;
    // private Float size;

    public Sheet.Set getVisualPropertiesSet(Sheet.Set set) throws NoSuchMethodException, NoSuchFieldException {
        set.put(new ElementPropertySupport(this, Color.class, "color", "Color", "The Line Color of the SequenceFlow Element."));
        return set;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
        this.setLineColor(color);
    }

    /**
     * @return the sequenceFlowSpec
     */
//    public TSequenceFlow getSequenceFlowSpec() {
//        return (TSequenceFlow) baseElementSpec;
//    }
    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public IFlowNodeWidget getSourceWidget() {
        throw new UnsupportedOperationException("Not supported yet."); //  return sourceNode;
    }

    @Override
    public IFlowNodeWidget getTargetWidget() {
        return getTargetEmbeddableWidget();
    }

//    /**
//     * @return the superclassWidget
//     */
//    public JavaClassWidget getSuperclassWidget() {
//        return getTargetEmbeddableWidget();
//    }
//
//    /**
//     * @param superclassWidget the superclassWidget to set
//     */
//    public void setSuperclassWidget(JavaClassWidget superclassWidget) {
//        this.setTargetEmbeddableWidget(superclassWidget);
////        if (superclassWidget != null) {
////            superclassWidget.addIncomingGeneralizationFlowWidget(this);
////        }
//    }
//
//    /**
//     * @return the subclassWidget
//     */
//    public JavaClassWidget getSubclassWidget() {
//        return getSourceJavaClassWidget();
//    }
//
//    /**
//     * @param subclassWidget the subclassWidget to set
//     */
//    public void setSubclassWidget(JavaClassWidget subclassWidget) {
//        this.setSourceJavaClassWidget(subclassWidget);
////        if (subclassWidget != null) {
////            subclassWidget.setOutgoingGeneralizationFlowWidget(this);
////        }
//    }
    /**
     * @return the targetEmbeddableWidget
     */
    public EmbeddableWidget getTargetEmbeddableWidget() {
        return targetEmbeddableWidget;
    }

    /**
     * @param targetEmbeddableWidget the targetEmbeddableWidget to set
     */
    public void setTargetEmbeddableWidget(EmbeddableWidget targetEmbeddableWidget) {
        this.targetEmbeddableWidget = targetEmbeddableWidget;
    }

    /**
     * @return the sourceEmbeddableAttributeWidget
     */
    public EmbeddedAttributeWidget getSourceEmbeddedAttributeWidget() {
        return sourceEmbeddedAttributeWidget;
    }

    /**
     * @param sourceEmbeddedAttributeWidget the sourceEmbeddableAttributeWidget
     * to set
     */
    public void setSourceEmbeddedAttributeWidget(EmbeddedAttributeWidget sourceEmbeddedAttributeWidget) {
        this.sourceEmbeddedAttributeWidget = sourceEmbeddedAttributeWidget;
    }

}
