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
package io.github.jeddict.jpa.modeler.widget.flow.association;

import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import io.github.jeddict.jpa.modeler.widget.BeanClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.AssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.MTMAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.MTOAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.OTMAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.OTOAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.AbstractEdgeWidget;
import io.github.jeddict.jpa.spec.bean.AssociationAttribute;
import java.awt.Color;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.nodes.Sheet;

public abstract class AssociationFlowWidget extends AbstractEdgeWidget<JPAModelerScene> {

    private AssociationAttributeWidget sourceAssociationAttributeWidget;

    public AssociationFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setName(value);
            AssociationFlowWidget.this.setLabel(name);
        });
    }

    @Override
    public void init() {
        sourceAssociationAttributeWidget.setAnchorGap(4);
        if (this.getTargetWidget() instanceof IPNodeWidget) {
            ((IPNodeWidget) this.getTargetWidget()).setAnchorGap(4);
        } else if (this.getTargetWidget() instanceof IPinWidget) {
            ((IPinWidget) this.getTargetWidget()).setAnchorGap(4);
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet elementPropertySet) {
    }

    /**
     * @return the sourceAssociationAttributeWidget
     */
    public AssociationAttributeWidget getSourceAssociationAttributeWidget() {
        return sourceAssociationAttributeWidget;
    }

    /**
     * @param sourceAssociationAttributeWidget the sourceAssociationAttributeWidget to
     * set
     */
    public void setSourceAssociationAttributeWidget(AssociationAttributeWidget sourceAssociationAttributeWidget) {
        this.sourceAssociationAttributeWidget = sourceAssociationAttributeWidget;
    }
    
    public BeanClassWidget getTargetClassWidget() {
        if(this instanceof BidirectionalAssociation){
           return (BeanClassWidget) ((BidirectionalAssociation)this).getTargetAssociationAttributeWidget().getClassWidget();
        } else {
           return (BeanClassWidget) ((UnidirectionalAssociation)this).getTargetClassWidget(); 
        }
    }

    @Override
    public AssociationAttributeWidget getSourceWidget() {
        return sourceAssociationAttributeWidget;
    }
    private Color color;

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

    @Override
    public void destroy() {
    }

    
    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceAttributeWidget) {
        AssociationFlowWidget edgeWidget = this;
        if (sourceNodeWidget instanceof BeanClassWidget && targetNodeWidget instanceof BeanClassWidget) {
            BeanClassWidget sourceBeanClassWidget = (BeanClassWidget) sourceNodeWidget;
            BeanClassWidget targetBeanClassWidget = (BeanClassWidget) targetNodeWidget;
            AssociationFlowWidget associationFlowWidget = (AssociationFlowWidget) edgeWidget;
            AssociationAttributeWidget<? extends AssociationAttribute> associationAttributeWidget = null;
            if (associationFlowWidget instanceof OTOAssociationFlowWidget) {
                OTOAssociationFlowWidget otoAssociationFlowWidget = (OTOAssociationFlowWidget) associationFlowWidget;
                OTOAssociationAttributeWidget otoAssociationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otoAssociationAttributeWidget = sourceBeanClassWidget.addOneToOneAssociationAttribute(sourceBeanClassWidget.getNextAttributeName(targetBeanClassWidget.getName()));
                } else {
                    otoAssociationAttributeWidget = (OTOAssociationAttributeWidget) sourceAttributeWidget;
                }
                otoAssociationAttributeWidget.setOneToOneAssociationFlowWidget(otoAssociationFlowWidget);
                associationAttributeWidget = otoAssociationAttributeWidget;
            } else if (associationFlowWidget instanceof OTMAssociationFlowWidget) {
                OTMAssociationAttributeWidget otmAssociationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otmAssociationAttributeWidget = sourceBeanClassWidget.addOneToManyAssociationAttribute(sourceBeanClassWidget.getNextAttributeName(targetBeanClassWidget.getName()));
                } else {
                    otmAssociationAttributeWidget = (OTMAssociationAttributeWidget) sourceAttributeWidget;
                }
                otmAssociationAttributeWidget.setHierarchicalAssociationFlowWidget((OTMAssociationFlowWidget) associationFlowWidget);
                associationAttributeWidget = otmAssociationAttributeWidget;
            } else if (associationFlowWidget instanceof MTOAssociationFlowWidget) {
                MTOAssociationFlowWidget mtoAssociationFlowWidget = (MTOAssociationFlowWidget) associationFlowWidget;
                MTOAssociationAttributeWidget mtoAssociationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtoAssociationAttributeWidget = sourceBeanClassWidget.addManyToOneAssociationAttribute(sourceBeanClassWidget.getNextAttributeName(targetBeanClassWidget.getName()));
                } else {
                    mtoAssociationAttributeWidget = (MTOAssociationAttributeWidget) sourceAttributeWidget;
                }
                mtoAssociationAttributeWidget.setManyToOneAssociationFlowWidget(mtoAssociationFlowWidget);
                associationAttributeWidget = mtoAssociationAttributeWidget;
            } else if (associationFlowWidget instanceof MTMAssociationFlowWidget) {
                MTMAssociationAttributeWidget mtmAssociationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtmAssociationAttributeWidget = sourceBeanClassWidget.addManyToManyAssociationAttribute(sourceBeanClassWidget.getNextAttributeName(targetBeanClassWidget.getName()));
                } else {
                    mtmAssociationAttributeWidget = (MTMAssociationAttributeWidget) sourceAttributeWidget;
                }
                mtmAssociationAttributeWidget.setManyToManyAssociationFlowWidget((MTMAssociationFlowWidget) associationFlowWidget);
                associationAttributeWidget = mtmAssociationAttributeWidget;
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            associationFlowWidget.setSourceAssociationAttributeWidget(associationAttributeWidget);
            return associationAttributeWidget.getPinWidgetInfo();

        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getTargetPinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget targetAttributeWidget) {
        AssociationFlowWidget edgeWidget = this;
        if (sourceNodeWidget instanceof BeanClassWidget && targetNodeWidget instanceof BeanClassWidget) {
            BeanClassWidget targetClassWidget = (BeanClassWidget) targetNodeWidget;
            BeanClassWidget sourceClassWidget = (BeanClassWidget) sourceNodeWidget;
            if (edgeWidget instanceof UnidirectionalAssociation) {
                UnidirectionalAssociation uAssociationFlowWidget = (UnidirectionalAssociation) edgeWidget;
                uAssociationFlowWidget.setTargetClassWidget(targetClassWidget);
                targetClassWidget.addInverseSideAssociationFlowWidget((AssociationFlowWidget) uAssociationFlowWidget);
                if (targetAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    AssociationAttributeWidget sourceAssociationAttributeWidget = uAssociationFlowWidget.getSourceAssociationAttributeWidget();
                    sourceAssociationAttributeWidget.setConnectedSibling(targetClassWidget);
                }
                return targetClassWidget.getInternalPinWidgetInfo();
            } else if (edgeWidget instanceof BidirectionalAssociation) {
                if (edgeWidget instanceof BOTOAssociationFlowWidget) {
                    BOTOAssociationFlowWidget botoAssociationFlowWidget = (BOTOAssociationFlowWidget) edgeWidget;
                    OTOAssociationAttributeWidget targetOTOAssociationAttributeWidget;
                    if (targetAttributeWidget == null) {
                        targetOTOAssociationAttributeWidget = targetClassWidget.addOneToOneAssociationAttribute(targetClassWidget.getNextAttributeName(sourceClassWidget.getName()));
                        AssociationAttributeWidget sourceOTOAssociationAttributeWidget = botoAssociationFlowWidget.getSourceAssociationAttributeWidget();
                        sourceOTOAssociationAttributeWidget.setConnectedSibling(targetClassWidget, targetOTOAssociationAttributeWidget);
                        targetOTOAssociationAttributeWidget.setConnectedSibling(sourceClassWidget, sourceOTOAssociationAttributeWidget);
                    } else {
                        targetOTOAssociationAttributeWidget = (OTOAssociationAttributeWidget) targetAttributeWidget;
                    }
                    targetOTOAssociationAttributeWidget.setOneToOneAssociationFlowWidget(botoAssociationFlowWidget);
                    botoAssociationFlowWidget.setTargetAssociationAttributeWidget(targetOTOAssociationAttributeWidget);

                    return targetOTOAssociationAttributeWidget.getPinWidgetInfo();

                } else {
                    if (edgeWidget instanceof BMTOAssociationFlowWidget) {
                        BMTOAssociationFlowWidget bmtoAssociationFlowWidget = (BMTOAssociationFlowWidget) edgeWidget;
                        OTMAssociationAttributeWidget targetMTOAssociationAttributeWidget;
                        if (targetAttributeWidget == null) {
                            targetMTOAssociationAttributeWidget = targetClassWidget.addOneToManyAssociationAttribute(targetClassWidget.getNextAttributeName(sourceClassWidget.getName(), true));
                            AssociationAttributeWidget sourceMTOAssociationAttributeWidget = bmtoAssociationFlowWidget.getSourceAssociationAttributeWidget();
                            sourceMTOAssociationAttributeWidget.setConnectedSibling(targetClassWidget, targetMTOAssociationAttributeWidget);
                            targetMTOAssociationAttributeWidget.setConnectedSibling(sourceClassWidget, sourceMTOAssociationAttributeWidget);
                        } else {
                            targetMTOAssociationAttributeWidget = (OTMAssociationAttributeWidget) targetAttributeWidget;
                        }
                        targetMTOAssociationAttributeWidget.setHierarchicalAssociationFlowWidget(bmtoAssociationFlowWidget);
                        bmtoAssociationFlowWidget.setTargetAssociationAttributeWidget(targetMTOAssociationAttributeWidget);
                        return targetMTOAssociationAttributeWidget.getPinWidgetInfo();
                    } else {
                        if (edgeWidget instanceof BMTMAssociationFlowWidget) {
                            BMTMAssociationFlowWidget bmtmAssociationFlowWidget = (BMTMAssociationFlowWidget) edgeWidget;
                            MTMAssociationAttributeWidget targetMTMAssociationAttributeWidget;
                            if (targetAttributeWidget == null) {
                                targetMTMAssociationAttributeWidget = targetClassWidget.addManyToManyAssociationAttribute(targetClassWidget.getNextAttributeName(sourceClassWidget.getName(), true));
                                AssociationAttributeWidget sourceMTMAssociationAttributeWidget = bmtmAssociationFlowWidget.getSourceAssociationAttributeWidget();
                                sourceMTMAssociationAttributeWidget.setConnectedSibling(targetClassWidget, targetMTMAssociationAttributeWidget);
                                targetMTMAssociationAttributeWidget.setConnectedSibling(sourceClassWidget, sourceMTMAssociationAttributeWidget);

                            } else {
                                targetMTMAssociationAttributeWidget = (MTMAssociationAttributeWidget) targetAttributeWidget;
                            }
                            targetMTMAssociationAttributeWidget.setManyToManyAssociationFlowWidget(bmtmAssociationFlowWidget);
                            bmtmAssociationFlowWidget.setTargetAssociationAttributeWidget(targetMTMAssociationAttributeWidget);
                            return targetMTMAssociationAttributeWidget.getPinWidgetInfo();
                        } else {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
