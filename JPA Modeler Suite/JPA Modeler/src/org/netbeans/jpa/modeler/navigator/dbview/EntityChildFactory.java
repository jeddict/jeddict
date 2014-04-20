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
import java.util.List;
import javax.swing.Action;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

public class EntityChildFactory extends ChildFactory<EntityWidget> {

    private JPAModelerScene modelerScene;

    public EntityChildFactory(JPAModelerScene modelerScene) {
        this.modelerScene = modelerScene;
    }

    @Override
    protected boolean createKeys(List<EntityWidget> entityWidgets) {
        for (EntityWidget entityWidget : modelerScene.getEntityWidgets()) {
            entityWidgets.add(entityWidget);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(EntityWidget entityWidget) {
        AbstractNode node = new AttributeRootNode(Children.create(new AttributeChildFactory(entityWidget), true)) {

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
                EntityWidget customer = this.getLookup().lookup(EntityWidget.class);
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
        node.setDisplayName(entityWidget.getNodeName());
        node.setShortDescription(entityWidget.getNodeName());
        node.setIconBaseWithExtension("org/netbeans/jpa/modeler/resource/element/java/ENTITY.png");
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
