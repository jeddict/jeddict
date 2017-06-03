/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.modeler.specification.model.file.action;

import org.netbeans.jsonb.modeler.specification.model.event.ShortcutListener;
import org.netbeans.jsonb.modeler.specification.model.scene.JSONBModelerScene;
import org.netbeans.jsonb.modeler.specification.model.util.JSONBModelerUtil;
import static org.netbeans.jsonb.modeler.specification.model.util.JSONBModelerUtil.TAB_ICON;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.widget.connection.relation.RelationValidator;
import org.netbeans.jsonb.modeler.manager.JSONBModelerRequestManager;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;


@ModelerConfig(palette = "org/netbeans/jsonb/modeler/resource/document/PaletteConfig.xml",
        document = "org/netbeans/jsonb/modeler/resource/document/DocumentConfig.xml",
        element = "org/netbeans/jsonb/modeler/resource/document/ElementConfig.xml")
@org.netbeans.modeler.specification.annotaton.DiagramModel(id = "JSONB", name = "JSONB Viewer", 
        modelerUtil = JSONBModelerUtil.class, modelerScene = JSONBModelerScene.class,
        relationValidator = RelationValidator.class,
        version = "4.2.2", architectureVersion = "1.4")
@org.openide.util.lookup.ServiceProvider(service = JSONBModelerRequestManager.class)
public class JSONBViewerActionListener extends ModelerFileActionListener implements JSONBModelerRequestManager {

    private EntityMappings mappings;
    private WorkSpace workSpace;

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.setIcon(TAB_ICON);
        modelerFile.getAttributes().put(EntityMappings.class.getSimpleName(), mappings);
        modelerFile.getAttributes().put(WorkSpace.class.getSimpleName(), workSpace);
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
    }

    @Override
    public void init(ModelerFile file, EntityMappings mappings, WorkSpace workSpace) {
        this.mappings = mappings;
        this.workSpace = workSpace;
        context = null;
        openModelerFile("JSONB", null, null, file, null);
    }

}
