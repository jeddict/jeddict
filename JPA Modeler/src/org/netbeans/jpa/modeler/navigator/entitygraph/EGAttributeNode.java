/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.modeler.navigator.entitygraph;

import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.AttributeOverrideHandler;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class EGAttributeNode extends PropertyNode {

    private final EntityWidget entityWidget;
    private AttributeWidget attributeWidget;

    public EGAttributeNode(EntityWidget entityWidget, AttributeWidget attributeWidget, Children children) {
        super(entityWidget.getModelerScene(), children ,Lookups.singleton(new CheckableAttributeNode()));
        this.entityWidget = entityWidget;
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        if (entityWidget.getBaseElementSpec() instanceof AttributeOverrideHandler) {
            Attribute attributeSpec = (Attribute) attributeWidget.getBaseElementSpec();
            AttributeOverrideHandler attributeOverrideHandler = (AttributeOverrideHandler) entityWidget.getBaseElementSpec();
            AttributeOverride attributeOverride = attributeOverrideHandler.getAttributeOverride(attributeSpec.getName());
            set.createPropertySet(attributeWidget, attributeOverride.getColumn(), attributeWidget.getPropertyChangeListeners(), attributeWidget.getPropertyVisibilityHandlers());
        }
    }
}
