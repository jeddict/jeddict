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
package io.github.jeddict.jpa.modeler.navigator.dbview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.PrimaryKeyContainerWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.properties.view.manager.PropertyNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class AttributeChildFactory extends ChildFactory<ColumnDef> {

    private final EntityWidget entityWidget;

    public AttributeChildFactory(EntityWidget entityWidget) {
        this.entityWidget = entityWidget;
    }

    @Override
    protected boolean createKeys(List<ColumnDef> columnDefList) {
        return createKeys(null, entityWidget, columnDefList);
    }

    private boolean createKeys(String flowPreFix, PersistenceClassWidget<? extends ManagedClass> classWidget, List<ColumnDef> columnDefList) {
        List<AttributeWidget> attributeWidgets = new ArrayList<>();
        if (classWidget instanceof PrimaryKeyContainerWidget) {
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getIdAttributeWidgets());
            attributeWidgets.addAll(((PrimaryKeyContainerWidget) classWidget).getVersionAttributeWidgets());
        }
        attributeWidgets.addAll(classWidget.getBasicAttributeWidgets());
        for (AttributeWidget attributeWidget : attributeWidgets) {
            Attribute attribute = (Attribute) attributeWidget.getBaseElementSpec();
            ColumnDef columnDef = new ColumnDef();
            columnDef.setAttributeWidget(attributeWidget);
            if (flowPreFix == null) {
                columnDef.setFlowTitle(attribute.getName());
            } else {
                columnDef.setFlowTitle(flowPreFix + "." + attribute.getName());
            }
            columnDef.setColumnName(attribute.getName().toUpperCase());
            columnDefList.add(columnDef);
        }

        for (EmbeddedAttributeWidget embeddedAttributeWidgets : classWidget.getEmbeddedAttributeWidgets()) {
            Embedded embeddedSpec = (Embedded) embeddedAttributeWidgets.getBaseElementSpec();
            EmbeddableWidget embeddableWidget = embeddedAttributeWidgets.getEmbeddableFlowWidget().getTargetEmbeddableWidget();
//            Embeddable embeddable = embeddableWidget.getBaseElementSpec();
            createKeys(embeddedSpec.getName(), embeddableWidget, columnDefList);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(final ColumnDef columnDef) {
        AbstractNode node = new PropertyNode(columnDef.getAttributeWidget().getModelerScene(), Children.LEAF, Lookups.singleton(columnDef)) {

            @Override
            public void createPropertySet(ElementPropertySet set) {
                if (columnDef.getAttributeWidget().getBaseElementSpec() instanceof PersistenceBaseAttribute) {
                    PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) columnDef.getAttributeWidget().getBaseElementSpec();
                    if (persistenceBaseAttribute.getColumn() == null) {
                        persistenceBaseAttribute.setColumn(new Column());
                    }
                    set.createPropertySet(columnDef.getAttributeWidget(), persistenceBaseAttribute.getColumn(), columnDef.getAttributeWidget().getPropertyChangeListeners(), columnDef.getAttributeWidget().getPropertyVisibilityHandlers());
                }
            }

            @Override
            public Action[] getActions(boolean context) {
                Action[] result = new Action[]{
                    SystemAction.get(DeleteAction.class),
                    SystemAction.get(PropertiesAction.class)
                };
                return result;
            }

            @Override
            public boolean canDestroy() {
                Entity customer = this.getLookup().lookup(Entity.class);
                return customer != null;
            }

            @Override
            public void destroy() throws IOException {
//                if (deleteEntity(this.getLookup().lookup(Entity.class).getEntityId())) {
//                    super.destroy();
//                    EntityTopComponent.refreshNode();
//                }
            }

        };
        node.setDisplayName(columnDef.getFlowTitle() + " -> " + columnDef.getColumnName());
        node.setShortDescription(columnDef.getColumnName());
        AttributeWidget attributeWidget = columnDef.getAttributeWidget();
        node.setIconBaseWithExtension(attributeWidget.getIconPath());
        return node;
    }

}

class ColumnDef {

    private AttributeWidget attributeWidget;
    private Attribute attributeSpec;
    private String columnName;
    private String flowTitle;

    /**
     * @return the attributeWidget
     */
    public AttributeWidget getAttributeWidget() {
        return attributeWidget;
    }

    /**
     * @param attributeWidget the attributeWidget to set
     */
    public void setAttributeWidget(AttributeWidget attributeWidget) {
        this.attributeWidget = attributeWidget;
    }

    /**
     * @return the attributeSpec
     */
    public Attribute getAttributeSpec() {
        return attributeSpec;
    }

    /**
     * @param attributeSpec the attributeSpec to set
     */
    public void setAttributeSpec(Attribute attributeSpec) {
        this.attributeSpec = attributeSpec;
    }

    /**
     * @return the name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param name the name to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the flowTitle
     */
    public String getFlowTitle() {
        return flowTitle;
    }

    /**
     * @param flowTitle the flowTitle to set
     */
    public void setFlowTitle(String flowTitle) {
        this.flowTitle = flowTitle;
    }

}
