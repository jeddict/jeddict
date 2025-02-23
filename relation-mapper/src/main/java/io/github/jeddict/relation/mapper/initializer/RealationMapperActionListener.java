/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.initializer;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.modeler.widget.connection.relation.RelationValidator;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.relation.mapper.RelationMapper;
import io.github.jeddict.relation.mapper.event.ShortcutListener;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.TAB_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;

//@ActionID(
//        category = "Build",
//        id = "jpa.file.RealationMapperActionListener")
//@ActionRegistration(
//        displayName = "#CTL_DBViewerActionListener")
//@ActionReference(path = "Loaders/text/jpa+xml/Actions", position = 0, separatorAfter = +50) // Issue Fix #5846
//@Messages("CTL_DBViewerActionListener=Edit in Modeler")
@ModelerConfig(palette = "io/github/jeddict/relation/mapper/resource/document/PaletteConfig.xml",
        document = "io/github/jeddict/relation/mapper/resource/document/DocumentConfig.xml",
        element = "io/github/jeddict/relation/mapper/resource/document/ElementConfig.xml")
@org.netbeans.modeler.specification.annotaton.DiagramModel(
        id = "JPA_DB",
        name = "DB Viewer",
        modelerUtil = RelationMapperUtil.class,
        modelerScene = RelationMapperScene.class,
        relationValidator = RelationValidator.class,
        modelerDiagramEngine = RelationMapperDiagramEngine.class,
        version = "6.5.0",
        architectureVersion = "1.4"
)
@org.openide.util.lookup.ServiceProvider(service = RelationMapper.class)
public class RealationMapperActionListener extends ModelerFileActionListener implements RelationMapper {

    private EntityMappings mappings;
    private WorkSpace workSpace;

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.setIcon(TAB_ICON);
        modelerFile.getAttributes().put(EntityMappings.class.getSimpleName(), mappings);
        modelerFile.getAttributes().put(WorkSpace.class.getSimpleName(), workSpace);
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
        JeddictLogger.openModelerFile("DB");
    }

    @Override
    public void init(ModelerFile file, EntityMappings mappings, WorkSpace workSpace) {
        this.mappings = mappings;
        this.workSpace = workSpace;
        context = null;
        openModelerFile("DB", null, null, file, null);
    }

}
