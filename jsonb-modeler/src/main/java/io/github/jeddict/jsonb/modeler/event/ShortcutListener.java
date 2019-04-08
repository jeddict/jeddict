/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import io.github.jeddict.jpa.modeler.initializer.DBUtil;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
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
            if (e.getKeyCode() == KeyEvent.VK_D) {
                DBUtil.openDBModeler(file.getParentFile());
            } else if (e.getKeyCode() == KeyEvent.VK_G) {
                JPAModelerUtil.generateSourceCode(file.getParentFile());
            }
        }
    }

}
