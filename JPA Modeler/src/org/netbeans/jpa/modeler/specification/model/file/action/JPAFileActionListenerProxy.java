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
