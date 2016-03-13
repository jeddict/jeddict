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
package org.netbeans.jpa.modeler.specification.model.event;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.actions.EventListener;
import org.netbeans.modeler.core.ModelerFile;

/**
 * @author Gaurav Gupta
 */
public class JPAEventListener extends EventListener {

    @Override
    public void registerEvent(JComponent component, ModelerFile modelerFile) {
        super.registerEvent(component, modelerFile);
        component.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, InputEvent.CTRL_MASK), "GEN_SRC");
        component.getActionMap().put("GEN_SRC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPAModelerUtil.generateSourceCode(modelerFile);
            }
        });
        component.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, InputEvent.CTRL_MASK), "DB_VIEWER");
        component.getActionMap().put("DB_VIEWER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPAModelerUtil.openDBViewer(modelerFile);
            }
        });
    }

}
