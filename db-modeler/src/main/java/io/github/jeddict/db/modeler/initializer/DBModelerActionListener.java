/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.db.modeler.initializer;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.db.modeler.action.TableNodeList;
import io.github.jeddict.jpa.modeler.widget.connection.relation.RelationValidator;
import io.github.jeddict.relation.mapper.event.ShortcutListener;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.TAB_ICON;
import org.netbeans.api.db.explorer.node.BaseNode;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.annotaton.DiagramModel;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.openide.nodes.Node;

@ModelerConfig(palette = "io/github/jeddict/db/modeler/resource/document/PaletteConfig.xml",
        document = "io/github/jeddict/db/modeler/resource/document/DocumentConfig.xml",
        element = "io/github/jeddict/db/modeler/resource/document/ElementConfig.xml")
@DiagramModel(
        id = "NATIVE_DB", 
        name = "Native DB Viewer",
        modelerUtil = DBModelerUtil.class, 
        modelerScene = DBModelerScene.class,
        relationValidator = RelationValidator.class, 
        modelerDiagramEngine = DBDiagramEngine.class,
        version = "5.1",
        architectureVersion = "1.0"
)
public class DBModelerActionListener extends ModelerFileActionListener {

    private Schema schema;
    private BaseNode node;
    private TableNodeList nodeList;

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.getAttributes().put(Node.class.getSimpleName(), node);
        modelerFile.getAttributes().put(TableNodeList.class.getSimpleName(), nodeList);
        modelerFile.getAttributes().put(Schema.class.getSimpleName(), schema);
        
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
        JeddictLogger.openModelerFile("NATIVE_DB");
    }

    public void init(BaseNode node, TableNodeList nodeList, Schema schema) {
        this.node = node;
        this.nodeList = nodeList;
        this.schema = schema;
        context = null;
        DatabaseConnection connection = node.getLookup().lookup(DatabaseConnection.class);
        
        String id = connection.getDatabase() + "/" + connection.getSchema();
        if(node instanceof TableNode && nodeList.getBaseNodes().size() == 1){
            id = id + "/" + node.getName();
        } else if(node instanceof TableNode && nodeList.getBaseNodes().size() > 1){
            id = id + "/*";
        }

        ModelerFile file = new ModelerFile();
        file.setName(id);
        file.setIcon(TAB_ICON);
        openModelerFile(id, id, connection.getName(), file, null);
    }

}
