/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.modeler.specification.model.file.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
/**
 * NetBeans added filter to add only direct extended ActionListener so using the proxy here
 * @author Gaurav Gupta
 */
@ActionID(
        category = "Edit",
        id = "jpa.file.JPAFileActionListenerProxy")
@ActionRegistration(
        displayName = "#CTL_JPAFileActionListenerProxy")
@ActionReference(path = "Loaders/text/jpa+xml/Actions", position = 0, separatorAfter = +50) // Issue Fix #5846
@Messages("CTL_JPAFileActionListenerProxy=Edit in Modeler")
public final class JPAFileActionListenerProxy implements ActionListener {

    private final JPAFileDataObject context;

    public JPAFileActionListenerProxy(JPAFileDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        new JPAFileActionListener(context).actionPerformed(ev);
    }
}
