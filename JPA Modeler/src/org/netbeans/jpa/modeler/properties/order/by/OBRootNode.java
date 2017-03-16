/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.order.by;

import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.navigator.nodes.RootNode;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.TreeChildFactory;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.OrderBy;

public class OBRootNode extends RootNode<OrderBy> {

    private final PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget;

    public OBRootNode(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget, OrderBy orderBy, TreeChildFactory childFactory, CheckableAttributeNode checkableNode) {
        super(orderBy, childFactory, checkableNode);
        this.persistenceClassWidget = persistenceClassWidget;
        
   }

    public PersistenceClassWidget getRootWidget() {
        return persistenceClassWidget;
    }

    @Override
    public void init() {
        ManagedClass managedClass = persistenceClassWidget.getBaseElementSpec();
        setDisplayName(managedClass.getClazz());
        setShortDescription(managedClass.getClazz());
        setIconBaseWithExtension(persistenceClassWidget.getIconPath());
    }

}
