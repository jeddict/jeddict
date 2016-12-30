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
package org.netbeans.db.modeler.specification.model.file.action;

import org.netbeans.db.modeler.manager.DBModelerRequestManager;
import org.netbeans.db.modeler.specification.model.engine.DBDiagramEngine;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.DBModelerUtil;
import static org.netbeans.db.modeler.specification.model.util.DBModelerUtil.TAB_ICON;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.widget.connection.relation.RelationValidator;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;

//@ActionID(
//        category = "Build",
//        id = "jpa.file.DBViewerActionListener")
//@ActionRegistration(
//        displayName = "#CTL_DBViewerActionListener")
//@ActionReference(path = "Loaders/text/jpa+xml/Actions", position = 0, separatorAfter = +50) // Issue Fix #5846
//@Messages("CTL_DBViewerActionListener=Edit in Modeler")
@ModelerConfig(palette = "org/netbeans/db/modeler/resource/document/PaletteConfig.xml",
        document = "org/netbeans/db/modeler/resource/document/DocumentConfig.xml",
        element = "org/netbeans/db/modeler/resource/document/ElementConfig.xml")
@org.netbeans.modeler.specification.annotaton.DiagramModel(id = "JPA_DB", name = "DB Viewer", 
        modelerUtil = DBModelerUtil.class, modelerScene = DBModelerScene.class,
        relationValidator = RelationValidator.class, modelerDiagramEngine = DBDiagramEngine.class,
        version = "2.8", architectureVersion = "1.4")
@org.openide.util.lookup.ServiceProvider(service = DBModelerRequestManager.class)
public class DBViewerActionListener extends ModelerFileActionListener implements DBModelerRequestManager {

    private EntityMappings mappings;

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.setIcon(TAB_ICON);
        modelerFile.getAttributes().put(EntityMappings.class.getSimpleName(), mappings);
    }

    @Override
    public void init(ModelerFile file, EntityMappings mappings) {
        this.mappings = mappings;
        context = null;
        openModelerFile("DB", null, null, file);
    }

}
