/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.properties.order;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.netbeans.jpa.modeler.navigator.nodes.LeafNode;
import org.netbeans.jpa.modeler.navigator.nodes.LeafNodeAction;
import org.netbeans.jpa.modeler.spec.OrderType;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;

public class OrderAction extends NodeAction implements Presenter.Popup, LeafNodeAction {

    private LeafNode leafNode;

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @NbBundle.Messages("OrderAction.name=Order Type")
    @Override
    public String getName() {
        return Bundle.OrderAction_name();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @NbBundle.Messages({
        "Order.ASC=ASC",
        "Order.DESC=DESC"
    })
    @Override
    public JMenuItem getPopupPresenter() {
        JMenu submenu = new JMenu(this);
        JRadioButtonMenuItem ASC_item = new JRadioButtonMenuItem(Bundle.Order_ASC());
        ASC_item.addActionListener((ActionEvent e) -> {
            if (getNode() instanceof OrderColumn) {
                ((OrderColumn) getNode()).setOrder(OrderType.ASC);
                ASC_item.setSelected(true);
            }
        });
        JRadioButtonMenuItem DESC_item = new JRadioButtonMenuItem(Bundle.Order_DESC());
        DESC_item.addActionListener((ActionEvent e) -> {
            if (getNode() instanceof OrderColumn) {
                ((OrderColumn) getNode()).setOrder(OrderType.DESC);
                DESC_item.setSelected(true);
            }
        });
        submenu.add(ASC_item);
        submenu.add(DESC_item);
        if (getNode() instanceof OrderColumn) {
            if(((OrderColumn) getNode()).getOrder() == OrderType.DESC){
                DESC_item.setSelected(true);
            } else {
                ASC_item.setSelected(true);
            }
        }
        return submenu;
    }

    @Override
    public void setNode(LeafNode node) {
        this.leafNode = node;
    }

    @Override
    public LeafNode getNode() {
        return leafNode;
    }
}
