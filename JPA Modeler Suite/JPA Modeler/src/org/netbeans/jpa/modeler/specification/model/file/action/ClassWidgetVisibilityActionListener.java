/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
