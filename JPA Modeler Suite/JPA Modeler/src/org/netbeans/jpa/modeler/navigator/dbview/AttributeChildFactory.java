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
package org.netbeans.jpa.modeler.navigator.dbview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTORelationAttributeWidget;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.properties.view.manager.BasePropertyViewManager;
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

    private EntityWidget entityWidget;

    public AttributeChildFactory(EntityWidget entityWidget) {
        this.entityWidget = entityWidget;
    }

    @Override
    protected boolean createKeys(List<ColumnDef> columnDefList) {
        return createKeys(null, entityWidget, columnDefList);
    }

    private boolean createKeys(String flowPreFix, PersistenceClassWidget classWidget, List<ColumnDef> columnDefList) {
        List<AttributeWidget> attributeWidgets = new ArrayList<AttributeWidget>();
        attributeWidgets.addAll(classWidget.getIdAttributeWidgets());
        attributeWidgets.addAll(classWidget.getVersionAttributeWidgets());
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
//            Embeddable embeddable = (Embeddable) embeddableWidget.getBaseElementSpec();
            createKeys(embeddedSpec.getName(), embeddableWidget, columnDefList);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(final ColumnDef columnDef) {
//        Attribute attribute = (Attribute) columnDef.getAttributeWidget().getBaseElementSpec();
        AbstractNode node = new PropertyNode(columnDef.getAttributeWidget().getModelerScene(), Children.LEAF, Lookups.singleton(columnDef)) {

            @Override
            public void createPropertySet(ElementPropertySet set) {
                if (columnDef.getAttributeWidget().getBaseElementSpec() instanceof PersistenceBaseAttribute) {
                    PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) columnDef.getAttributeWidget().getBaseElementSpec();
                    if (persistenceBaseAttribute.getColumn() == null) {
                        persistenceBaseAttribute.setColumn(new Column());
                    }
                    ElementConfigFactory elementConfigFactory = columnDef.getAttributeWidget().getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
                    elementConfigFactory.createPropertySet(set, persistenceBaseAttribute.getColumn(), columnDef.getAttributeWidget().getPropertyChangeListeners(), columnDef.getAttributeWidget().getPropertyVisibilityHandlers());
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
        if (attributeWidget instanceof IdAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.ID_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof EmbeddedIdAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ID_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof VersionAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.VERSION_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof BasicAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.BASIC_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.BASIC_COLLECTION_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof SingleValueEmbeddedAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof MultiValueEmbeddedAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.MULTIVALUE_EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof OTORelationAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof OTMRelationAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof MTORelationAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof MTMRelationAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.EMBEDDED_ATTRIBUTE_ICON_PATH);
        } else if (attributeWidget instanceof TransientAttributeWidget) {
            node.setIconBaseWithExtension(JPAModelerUtil.TRANSIENT_ATTRIBUTE_ICON_PATH);
        }
//        node.setIconBaseWithExtension("org/netbeans/jpa/modeler/resource/image/basic-attribute.png");
        return node;
    }

//    private static boolean deleteEntity(int customerId) {
//        EntityManager entityManager = Persistence.createEntityManagerFactory("EntityDBAccessPU").createEntityManager();
//        entityManager.getTransaction().begin();
//        try {
//            Entity toDelete = entityManager.find(Entity.class, customerId);
//            entityManager.remove(toDelete);
//            // so far so good
//            entityManager.getTransaction().commit();
//        } catch(Exception e) {
//            Logger.getLogger(EntityChildFactory.class.getName()).log(
//                    Level.WARNING, "Cannot delete a customer with id {0}, cause: {1}", new Object[]{customerId, e});
//            entityManager.getTransaction().rollback();
//        }
//        return true;
//    }
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
