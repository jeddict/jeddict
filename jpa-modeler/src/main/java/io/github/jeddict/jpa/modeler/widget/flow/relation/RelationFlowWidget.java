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

import java.awt.Color;
import static io.github.jeddict.jpa.modeler.Constant.PKBMTO_RELATION;
import static io.github.jeddict.jpa.modeler.Constant.PKBOTO_RELATION;
import static io.github.jeddict.jpa.modeler.Constant.PKUMTO_RELATION;
import static io.github.jeddict.jpa.modeler.Constant.PKUOTO_RELATION;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.MappedSuperclassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MTMRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MTORelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.OTMRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.OTORelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.AbstractEdgeWidget;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.nodes.Sheet;

public abstract class RelationFlowWidget extends AbstractEdgeWidget<JPAModelerScene> {

    private RelationAttributeWidget sourceRelationAttributeWidget;

    public RelationFlowWidget(JPAModelerScene scene, EdgeWidgetInfo edge) {
        super(scene, edge);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (oldValue, value) -> {
            setName(value);
            RelationFlowWidget.this.setLabel(name);
        });
    }

    @Override
    public void init() {
        sourceRelationAttributeWidget.setAnchorGap(4);
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
     * @return the sourceRelationAttributeWidget
     */
    public RelationAttributeWidget getSourceRelationAttributeWidget() {
        return sourceRelationAttributeWidget;
    }

    /**
     * @param sourceRelationAttributeWidget the sourceRelationAttributeWidget to
     * set
     */
    public void setSourceRelationAttributeWidget(RelationAttributeWidget sourceRelationAttributeWidget) {
        this.sourceRelationAttributeWidget = sourceRelationAttributeWidget;
    }

    public EntityWidget getTargetEntityWidget() { // will always return Entity
        if (this instanceof BidirectionalRelation) {
            return (EntityWidget) ((BidirectionalRelation) this).getTargetRelationAttributeWidget().getClassWidget();
        } else {
            return (EntityWidget) ((UnidirectionalRelation) this).getTargetEntityWidget();
        }
    }

//
    @Override
    public RelationAttributeWidget getSourceWidget() {
        return sourceRelationAttributeWidget;
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

    @Override
    public void destroy() {
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget) {
        return getSourcePinWidget(sourceNodeWidget, targetNodeWidget, null);
    }

    @Override
    public PinWidgetInfo getSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IPinWidget sourceAttributeWidget) {
        if (sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EntityWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            RelationFlowWidget relationFlowWidget = this;
            RelationAttributeWidget<? extends RelationAttribute> relationAttributeWidget = null;
            if (relationFlowWidget instanceof OTORelationFlowWidget) {
                OTORelationFlowWidget otoRelationFlowWidget = (OTORelationFlowWidget) relationFlowWidget;
                OTORelationAttributeWidget otoRelationAttributeWidget;
                boolean primaryKey = otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKUOTO_RELATION) || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKBOTO_RELATION);
                if (sourceAttributeWidget == null) {
                    otoRelationAttributeWidget = sourcePersistenceWidget.addOneToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()), primaryKey);
                } else {
                    otoRelationAttributeWidget = (OTORelationAttributeWidget) sourceAttributeWidget;
                }

                if (otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKUOTO_RELATION) || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKBOTO_RELATION)) {
                    otoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                }
                otoRelationAttributeWidget.setOneToOneRelationFlowWidget(otoRelationFlowWidget);
                relationAttributeWidget = otoRelationAttributeWidget;
            } else if (relationFlowWidget instanceof OTMRelationFlowWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otmRelationAttributeWidget = sourcePersistenceWidget.addOneToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName(), true));
                } else {
                    otmRelationAttributeWidget = (OTMRelationAttributeWidget) sourceAttributeWidget;
                }
                otmRelationAttributeWidget.setHierarchicalRelationFlowWidget((OTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = otmRelationAttributeWidget;
            } else if (relationFlowWidget instanceof MTORelationFlowWidget) {
                MTORelationFlowWidget mtoRelationFlowWidget = (MTORelationFlowWidget) relationFlowWidget;
                MTORelationAttributeWidget mtoRelationAttributeWidget;
                boolean primaryKey = mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKUMTO_RELATION) || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKBMTO_RELATION);
                if (sourceAttributeWidget == null) {
                    mtoRelationAttributeWidget = sourcePersistenceWidget.addManyToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()), primaryKey);
                } else {
                    mtoRelationAttributeWidget = (MTORelationAttributeWidget) sourceAttributeWidget;
                }

                if (mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKUMTO_RELATION) || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals(PKBMTO_RELATION)) {
                    mtoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                }
                mtoRelationAttributeWidget.setManyToOneRelationFlowWidget(mtoRelationFlowWidget);
                relationAttributeWidget = mtoRelationAttributeWidget;

            } else if (relationFlowWidget instanceof MTMRelationFlowWidget) {
                MTMRelationAttributeWidget mtmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtmRelationAttributeWidget = sourcePersistenceWidget.addManyToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName(), true));
                } else {
                    mtmRelationAttributeWidget = (MTMRelationAttributeWidget) sourceAttributeWidget;
                }
                mtmRelationAttributeWidget.setManyToManyRelationFlowWidget((MTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = mtmRelationAttributeWidget;
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            relationFlowWidget.setSourceRelationAttributeWidget(relationAttributeWidget);
            relationAttributeWidget.getBaseElementSpec().setOwner(true);
            return relationAttributeWidget.getPinWidgetInfo();

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
        RelationFlowWidget edgeWidget = this;
        if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget) {
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            EntityWidget sourceEntityWidget = (EntityWidget) sourceNodeWidget;
            if (edgeWidget instanceof UnidirectionalRelation) {
                UnidirectionalRelation uRelationFlowWidget = (UnidirectionalRelation) edgeWidget;
                uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                targetEntityWidget.addInverseSideRelationFlowWidget((RelationFlowWidget) uRelationFlowWidget);
                if (targetAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                }
                return targetEntityWidget.getInternalPinWidgetInfo();
            } else if (edgeWidget instanceof BidirectionalRelation) {
                if (edgeWidget instanceof BOTORelationFlowWidget) {
                    BOTORelationFlowWidget botoRelationFlowWidget = (BOTORelationFlowWidget) edgeWidget;
                    OTORelationAttributeWidget targetOTORelationAttributeWidget;
                    if (targetAttributeWidget == null) {
                        targetOTORelationAttributeWidget = targetEntityWidget.addOneToOneRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()), false);
                        RelationAttributeWidget sourceOTORelationAttributeWidget = botoRelationFlowWidget.getSourceRelationAttributeWidget();
                        sourceOTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetOTORelationAttributeWidget);
                        targetOTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceOTORelationAttributeWidget);
                    } else {
                        targetOTORelationAttributeWidget = (OTORelationAttributeWidget) targetAttributeWidget;
                    }
                    targetOTORelationAttributeWidget.setOneToOneRelationFlowWidget(botoRelationFlowWidget);
                    botoRelationFlowWidget.setTargetRelationAttributeWidget(targetOTORelationAttributeWidget);

                    return targetOTORelationAttributeWidget.getPinWidgetInfo();

                } else {
                    if (edgeWidget instanceof BMTORelationFlowWidget) {
                        BMTORelationFlowWidget bmtoRelationFlowWidget = (BMTORelationFlowWidget) edgeWidget;
                        OTMRelationAttributeWidget targetMTORelationAttributeWidget;
                        if (targetAttributeWidget == null) {
                            targetMTORelationAttributeWidget = targetEntityWidget.addOneToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName(), true));
                            RelationAttributeWidget sourceMTORelationAttributeWidget = bmtoRelationFlowWidget.getSourceRelationAttributeWidget();
                            sourceMTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTORelationAttributeWidget);
                            targetMTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTORelationAttributeWidget);
                        } else {
                            targetMTORelationAttributeWidget = (OTMRelationAttributeWidget) targetAttributeWidget;
                        }
                        targetMTORelationAttributeWidget.setHierarchicalRelationFlowWidget(bmtoRelationFlowWidget);
                        bmtoRelationFlowWidget.setTargetRelationAttributeWidget(targetMTORelationAttributeWidget);
                        return targetMTORelationAttributeWidget.getPinWidgetInfo();
                    } else {
                        if (edgeWidget instanceof BMTMRelationFlowWidget) {
                            BMTMRelationFlowWidget bmtmRelationFlowWidget = (BMTMRelationFlowWidget) edgeWidget;
                            MTMRelationAttributeWidget targetMTMRelationAttributeWidget;
                            if (targetAttributeWidget == null) {
                                targetMTMRelationAttributeWidget = targetEntityWidget.addManyToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName(), true));
                                RelationAttributeWidget sourceMTMRelationAttributeWidget = bmtmRelationFlowWidget.getSourceRelationAttributeWidget();
                                sourceMTMRelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTMRelationAttributeWidget);
                                targetMTMRelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTMRelationAttributeWidget);

                            } else {
                                targetMTMRelationAttributeWidget = (MTMRelationAttributeWidget) targetAttributeWidget;
                            }
                            targetMTMRelationAttributeWidget.setManyToManyRelationFlowWidget(bmtmRelationFlowWidget);
                            bmtmRelationFlowWidget.setTargetRelationAttributeWidget(targetMTMRelationAttributeWidget);
                            return targetMTMRelationAttributeWidget.getPinWidgetInfo();
                        } else {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        } else if ((sourceNodeWidget instanceof MappedSuperclassWidget || sourceNodeWidget instanceof EmbeddableWidget)
                && targetNodeWidget instanceof EntityWidget) {
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            if (edgeWidget instanceof UnidirectionalRelation) {
                UnidirectionalRelation uRelationFlowWidget = (UnidirectionalRelation) edgeWidget;
                uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                if (targetAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                }
                return targetEntityWidget.getInternalPinWidgetInfo();
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
