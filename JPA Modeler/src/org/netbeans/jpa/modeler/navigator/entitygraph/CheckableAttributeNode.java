/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.modeler.navigator.entitygraph;

import org.openide.explorer.view.CheckableNode;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class CheckableAttributeNode implements CheckableNode{
boolean data=false;
                /**
                 * Tell the view to display a check-box for this node.
                 *
                 * @return <code>true</code> if the check-box should be displayed, <code>false</code> otherwise.
                 */
                @Override
                public boolean isCheckable() {
                    return true;
                }

                /**
                 * Provide the enabled state of the check-box.
                 *
                 * @return <code>true</code> if the check-box should be enabled, <code>false</code> otherwise.
                 */
                @Override
                public boolean isCheckEnabled() {
                    return true;
                }

                /**
                 * Provide the selected state of the check-box.
                 *
                 * @return <code>true</code> if the check-box should be selected,
                 *         <code>false</code> if it should be unselected and
                 *         <code>null</code> if the state is unknown.
                 */
                @Override
                public Boolean isSelected() {
                    return data;//true;//data.isSelected();
                }

                /**
                 * Called by the view when the check-box gets selected/unselected
                 *
                 * @param selected <code>true</code> if the check-box was selected,
                 *                 <code>false</code> if the check-box was unselected.
                 */
                @Override
                public void setSelected(Boolean selected) {
                    data=selected;//data.setSelected(selected);
                }
            }