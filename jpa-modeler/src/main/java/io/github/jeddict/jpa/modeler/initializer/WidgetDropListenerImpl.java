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
package io.github.jeddict.jpa.modeler.initializer;

import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.JAVA_CLASS_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PACKAGE_ICON;
import io.github.jeddict.reveng.JCREProcessor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.action.WidgetDropListener;
import static org.netbeans.modeler.core.NBModelerUtil.drawImage;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author jGauravGupta
 */
public class WidgetDropListenerImpl implements WidgetDropListener {

    static final String PRIMARY_TYPE = "application";
    static final String SUBTYPE = "x-java-org-netbeans-modules-java-project-packagenodednd";

    @Override
    public boolean isDroppable(Widget widget, Point point, Transferable transferable, IModelerScene scene) {
        if (isJavaClassDrop(transferable)) {
            drawImage(JAVA_CLASS_ICON, point, scene);
        } else if (isPackageFlavor(transferable.getTransferDataFlavors())) {
            drawImage(PACKAGE_ICON, point, scene);
        }
        return true;
    }

    @Override
    public void drop(Widget widget, Point point, Transferable transferable, IModelerScene scene) {
        List<File> files = new ArrayList<>();

        if (isJavaClassDrop(transferable)) {
            try {
                files.addAll((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }

        }

        if (isPackageFlavor(transferable.getTransferDataFlavors())) {
            files.addAll(
                    getPackageList(transferable)
                            .stream()
                            .map(FileUtil::toFile)
                            .filter(File::isDirectory)
                            .flatMap(dir -> Stream.of(dir.listFiles(file -> file.getPath().endsWith(JAVA_EXT_SUFFIX))))
                            .collect(toList())
            );
        }

        if (!files.isEmpty()) {
            JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
            processor.processDropedClasses(scene.getModelerFile(), files);
        }

    }

    protected boolean isJavaClassDrop(Transferable transferable) {
        return transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    private boolean isPackageFlavor(DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (SUBTYPE.equals(flavors[i].getSubType()) && PRIMARY_TYPE.equals(flavors[i].getPrimaryType())) {
                //Disable pasting into package, only paste into root is allowed
                return true;
            }
        }
        return false;
    }

    private List<FileObject> getPackageList(Transferable transferable) {
        List<FileObject> packages = new ArrayList<>();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (SUBTYPE.equals(flavors[i].getSubType())
                    && PRIMARY_TYPE.equals(flavors[i].getPrimaryType())) {
                FilterNode node;
                try {
                    node = (FilterNode) transferable.getTransferData(flavors[i]);
                    packages.add(node.getCookie(DataFolder.class).getPrimaryFile());
                } catch (UnsupportedFlavorException | IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return packages;
    }
}
