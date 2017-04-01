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
package org.netbeans.db.modeler.specification.model.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.netbeans.db.modeler.spec.DBMapping;
import org.netbeans.db.modeler.specification.model.util.SQLEditorUtil;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import static org.netbeans.jpa.modeler.specification.model.util.DBUtil.isolateEntityMapping;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;

/**
 *
 * @author Gaurav Gupta
 */
public class ShortcutListener extends KeyAdapter {

    private final ModelerFile file;

    public ShortcutListener(ModelerFile file) {
        this.file = file;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.isControlDown() == true) {
            if (e.getKeyCode() == KeyEvent.VK_L) {
                     SQLEditorUtil.openEditor(file, ((DBMapping) file.getModelerScene().getBaseElementSpec()).getSQL());
            } else if (e.getKeyCode() == KeyEvent.VK_F) {
                file.getModelerDiagramEngine().searchWidget();
            }
        }

    }

}
