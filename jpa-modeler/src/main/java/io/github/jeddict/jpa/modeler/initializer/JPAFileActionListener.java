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
package io.github.jeddict.jpa.modeler.initializer;

import io.github.jeddict.analytics.JeddictLogger;
import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.jpa.modeler.specification.export.ExportManagerImpl;
import io.github.jeddict.jpa.modeler.specification.model.event.ShortcutListener;
import io.github.jeddict.jpa.modeler.widget.connection.relation.RelationValidator;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.file.IModelerFileDataObject;
import org.netbeans.modeler.specification.annotaton.ModelerConfig;
import org.netbeans.modeler.specification.model.file.action.ModelerFileActionListener;

@ModelerConfig(
        palette = "io/github/jeddict/jpa/modeler/resource/document/PaletteConfig.xml",
        document = "io/github/jeddict/jpa/modeler/resource/document/DocumentConfig.xml",
        element = "io/github/jeddict/jpa/modeler/resource/document/ElementConfig.xml"
)
@org.netbeans.modeler.specification.annotaton.DiagramModel(
        id = "JPA",
        name = "JPA 3.1 Specification",
        version = "6.4.0",
        architectureVersion = "1.4",
        modelerUtil = JPAModelerUtil.class,
        modelerScene = JPAModelerScene.class,
        exportManager = ExportManagerImpl.class,
        relationValidator = RelationValidator.class,
        exceptionHandler = ExceptionUtils.class
)
public class JPAFileActionListener extends ModelerFileActionListener {

    public JPAFileActionListener(IModelerFileDataObject context) {
        super(context);
    }

    @Override
    public void initSpecification(final ModelerFile modelerFile) {
        modelerFile.getModelerPanelTopComponent().addKeyListener(new ShortcutListener(modelerFile));
        JeddictLogger.openModelerFile("JPA");
    }

    public static void open(ModelerFile file) {
        new JPAFileActionListener(file.getModelerFileDataObject()).openModelerFile();
    }

}
