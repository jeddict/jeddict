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
package io.github.jeddict.jsonb.modeler.initializer;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.jpa.modeler.widget.connection.relation.RelationValidator;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.jsonb.modeler.JSONBModeler;
import io.github.jeddict.jsonb.modeler.event.ShortcutListener;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.TAB_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;


@ModelerConfig(palette = "io/github/jeddict/jsonb/modeler/resource/document/PaletteConfig.xml",
        document = "io/github/jeddict/jsonb/modeler/resource/document/DocumentConfig.xml",
        element = "io/github/jeddict/jsonb/modeler/resource/document/ElementConfig.xml")
@org.netbeans.modeler.specification.annotaton.DiagramModel(
        id = "JSONB",
        name = "JSONB Viewer",
        modelerUtil = JSONBModelerUtil.class,
        modelerScene = JSONBModelerScene.class,
        relationValidator = RelationValidator.class,
        version = "5.4.3",
        architectureVersion = "1.4"
)
@org.openide.util.lookup.ServiceProvider(service = JSONBModeler.class)
public class JSONBViewerActionListener extends ModelerFileActionListener implements JSONBModeler {

    private EntityMappings mappings;
    private WorkSpace workSpace;

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.setIcon(TAB_ICON);
        modelerFile.getAttributes().put(EntityMappings.class.getSimpleName(), mappings);
        modelerFile.getAttributes().put(WorkSpace.class.getSimpleName(), workSpace);
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
        JeddictLogger.openModelerFile("JSONB");
    }

    @Override
    public void init(ModelerFile file, EntityMappings mappings, WorkSpace workSpace) {
        this.mappings = mappings;
        this.workSpace = workSpace;
        context = null;
        openModelerFile("JSONB", null, null, file, null);
    }

}
