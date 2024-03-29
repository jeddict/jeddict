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
package io.github.jeddict.jpa.modeler.specification.model.file;

import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.JPA_FILE_TYPE;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.Callable;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.modeler.file.ModelerFileDataObject;
import org.netbeans.modeler.resource.toolbar.ImageUtil;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.CloneableEditorSupport.Pane;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_JPAModel_LOADER=Files of JPAModel"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_JPAModel_LOADER",
        mimeType = JPA_FILE_TYPE,
        extension = {"jpa", "JPA"})
@DataObject.Registration(
        mimeType = JPA_FILE_TYPE,
        iconBase = "io/github/jeddict/jpa/modeler/specification/model/file/JPA_FILE_ICON.png",
        displayName = "#LBL_JPAModel_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/text/jpa+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class JPAFileDataObject extends ModelerFileDataObject implements Callable<CloneableEditorSupport.Pane> {

    public JPAFileDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor(JPA_FILE_TYPE, true);
    }

    @Override
    public Image getIcon() {
        return ImageUtil.getInstance().getImage(JPAFileDataObject.class, "JPA_FILE_ICON.png");
    }

    @Override
    public Pane call() {
        return (Pane) MultiViews.createCloneableMultiView(JPA_FILE_TYPE, this);
    }

    @NbBundle.Messages("Source=&Source")
    @MultiViewElement.Registration(
            displayName = "#Source",
            iconBase = "io/github/jeddict/jpa/modeler/specification/model/file/JPA_FILE_ICON.png",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            mimeType = JPA_FILE_TYPE,
            preferredID = "jpa.source",
            position = 1
    )
    public static MultiViewEditorElement createMultiViewEditorElement(Lookup context) {
        return new MultiViewEditorElement(context);
    }

}
