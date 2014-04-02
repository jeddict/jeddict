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

import org.netbeans.jpa.modeler.specification.JPASpecification;
import org.netbeans.jpa.modeler.specification.model.JPADefaultDiagramModel;
import org.netbeans.jpa.modeler.specification.model.engine.JPADiagramEngine;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.jpa.modeler.widget.connection.relation.RelationValidator;
import org.netbeans.modeler.component.IModelerPanel;
import org.netbeans.modeler.component.ModelerPanelTopComponent;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.ModelerSpecificationDiagramModel;
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
@org.netbeans.modeler.specification.annotaton.DiagramModel(id = "Default", name = "")
public class JPAFileActionListener extends ModelerFileActionListener {

    public JPAFileActionListener(JPAFileDataObject context) {
        super(context);
//        context.getPrimaryFile().ge
    }

    @Override
    public void initSpecification(ModelerFile modelerFile) {
        modelerFile.setModelerVendorSpecification(new JPASpecification());
        ModelerSpecificationDiagramModel diagramModel = new JPADefaultDiagramModel();
        modelerFile.getVendorSpecification().setModelerSpecificationDiagramModel(diagramModel);

        diagramModel.setModelerUtil(new JPAModelerUtil());
        diagramModel.setModelerDiagramEngine(new JPADiagramEngine());
        diagramModel.setModelerScene(new JPAModelerScene());
        diagramModel.setModelerPanelTopComponent((IModelerPanel) new ModelerPanelTopComponent());
        diagramModel.setRelationValidator(new RelationValidator());

    }
}
