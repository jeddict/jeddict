/**
 * Copyright [2013] Gaurav Gupta
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

import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.listener.ShortcutListener;
import org.netbeans.jpa.modeler.specification.export.ExportManagerImpl;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.jpa.modeler.widget.connection.relation.RelationValidator;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.file.IModelerFileDataObject;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Build",
        id = "jpa.file.JPAFileActionListener")
@ActionRegistration(
        displayName = "#CTL_JPAFileActionListener")
@ActionReference(path = "Loaders/text/jpa+xml/Actions", position = 0, separatorAfter = +50) // Issue Fix #5846
@Messages("CTL_JPAFileActionListener=Edit in Modeler")
@ModelerConfig(palette = "org/netbeans/jpa/modeler/resource/document/PaletteConfig.xml",
        document = "org/netbeans/jpa/modeler/resource/document/DocumentConfig.xml",
        element = "org/netbeans/jpa/modeler/resource/document/ElementConfig.xml")
@org.netbeans.modeler.specification.annotaton.Vendor(id = "JPA", version = 2.0F, name = "JPA", displayName = "JPA 2.0 Specification")
@org.netbeans.modeler.specification.annotaton.DiagramModel(id = "Default", name = "",
        modelerUtil = JPAModelerUtil.class, modelerScene = JPAModelerScene.class, exportManager = ExportManagerImpl.class,
        relationValidator = RelationValidator.class, exceptionHandler = ExceptionUtils.class)
public class JPAFileActionListener extends ModelerFileActionListener {

    public JPAFileActionListener(IModelerFileDataObject context) {
        super(context);
    }

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
    }

}
