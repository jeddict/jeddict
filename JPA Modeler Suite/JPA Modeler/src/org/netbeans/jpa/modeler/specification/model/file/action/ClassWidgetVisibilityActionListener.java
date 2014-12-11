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
package org.netbeans.jpa.modeler.specification.model.file.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene.fireEntityVisibilityAction;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.jpa.modeler.visiblity.javaclass.ClassWidgetVisibilityController;
import org.netbeans.modeler.core.ModelerCore;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.file.IModelerFileDataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Bugtracking",
        id = "org.netbeans.jpa.modeler.specification.model.file.action.ClassWidgetVisibilityActionListener"
)
@ActionRegistration(
        displayName = "#CTL_ClassWidgetVisibilityActionListener"
)
@ActionReference(path = "Loaders/text/jpa+xml/Actions", position = 25)
@Messages("CTL_ClassWidgetVisibilityActionListener=Manage Entity Visibility")
public final class ClassWidgetVisibilityActionListener implements ActionListener {

    private final IModelerFileDataObject context;

    public ClassWidgetVisibilityActionListener(IModelerFileDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileObject fileObject = context.getPrimaryFile();
        String path = fileObject.getPath();
        ModelerFile modelerFile = ModelerCore.getModelerFile(path);
        if (modelerFile == null) {
            File file = FileUtil.toFile(fileObject);
            EntityMappings entityMapping = JPAModelerUtil.getEntityMapping(file);
            ClassWidgetVisibilityController dialog = new ClassWidgetVisibilityController(entityMapping);
            dialog.setVisible(true);
            if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                JPAModelerUtil.saveFile(entityMapping, file);
                int option = JOptionPane.showConfirmDialog(null, "Are you want to open diagram now ?", "Open Diagram", JOptionPane.YES_NO_OPTION);
                if (option == javax.swing.JOptionPane.OK_OPTION) {
                    JPAFileActionListener fileListener = new JPAFileActionListener((JPAFileDataObject) context);
                    fileListener.actionPerformed(null);
                }
            }
        } else {
            JPAModelerScene.fireEntityVisibilityAction(modelerFile);
        }
    }
}
