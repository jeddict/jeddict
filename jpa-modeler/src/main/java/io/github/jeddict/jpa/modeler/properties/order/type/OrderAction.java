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
package io.github.jeddict.jpa.modeler.properties.order.type;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import io.github.jeddict.jpa.modeler.navigator.nodes.LeafNode;
import io.github.jeddict.jpa.modeler.navigator.nodes.actions.LeafNodeAction;
import io.github.jeddict.jpa.spec.OrderType;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
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

    @Override
    public String getName() {
        return getMessage(OrderAction.class, "OrderAction.name");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu submenu = new JMenu(this);
        JRadioButtonMenuItem ASC_item = new JRadioButtonMenuItem(getMessage(OrderAction.class, "OrderAction.asc"));
        ASC_item.addActionListener((ActionEvent e) -> {
            if (getNode() instanceof OrderTypeColumn) {
                ((OrderTypeColumn) getNode()).setOrder(OrderType.ASC);
                ASC_item.setSelected(true);
            }
        });
        JRadioButtonMenuItem DESC_item = new JRadioButtonMenuItem(getMessage(OrderAction.class, "OrderAction.desc"));
        DESC_item.addActionListener((ActionEvent e) -> {
            if (getNode() instanceof OrderTypeColumn) {
                ((OrderTypeColumn) getNode()).setOrder(OrderType.DESC);
                DESC_item.setSelected(true);
            }
        });
        submenu.add(ASC_item);
        submenu.add(DESC_item);
        if (getNode() instanceof OrderTypeColumn) {
            if(((OrderTypeColumn) getNode()).getOrder() == OrderType.DESC){
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
