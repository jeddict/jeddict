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
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import static io.github.jeddict.jcode.util.ProjectHelper.findSourceGroupForFile;
import static io.github.jeddict.jcode.util.StringHelper.camelCase;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.JAVA_CLASS_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PACKAGE_ICON;
import io.github.jeddict.jpa.modeler.widget.BeanClassWidget;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.EnumType;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.reveng.JCREProcessor;
import io.github.jeddict.source.ClassExplorer;
import io.github.jeddict.source.SourceExplorer;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Collections.singleton;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.action.WidgetDropListener;
import org.netbeans.modeler.core.ModelerFile;
import static org.netbeans.modeler.core.NBModelerUtil.drawImage;
import static org.netbeans.modeler.core.NBModelerUtil.drawImageOnNodeWidget;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author jGauravGupta
 */
public class WidgetDropListenerImpl implements WidgetDropListener {

    private static final String PRIMARY_TYPE = "application";
    private static final String PACKAGE_TYPE = "x-java-org-netbeans-modules-java-project-packagenodednd";

    @Override
    public boolean isDroppable(Widget widget, Point point, Transferable transferable, IModelerScene scene) {
        if (widget == scene) {
            if (isJavaClassDrop(transferable)) {
                drawImage(JAVA_CLASS_ICON, point, scene);
            } else if (isPackageFlavor(transferable.getTransferDataFlavors())) {
                drawImage(PACKAGE_ICON, point, scene);
            }
        } else {
            if (isJavaClassDrop(transferable)) {
                drawImageOnNodeWidget(JAVA_CLASS_ICON, point, scene, widget);
            }
        }
        return true;
    }

    @Override
    public void drop(Widget widget, Point point, Transferable transferable, IModelerScene scene) {
        if (widget == scene) {
            dropOnScene(widget, point, transferable, scene);
        } else {
            dropOnWidget(widget, point, transferable, scene);
        }
    }

    private void dropOnWidget(Widget widget, Point point, Transferable transferable, IModelerScene scene) {

        List<File> files = new ArrayList<>();
        if (isJavaClassDrop(transferable)) {
            try {
                List<File> javaFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                files.addAll(javaFiles.stream()
                        .filter(file -> file.getPath().endsWith(JAVA_EXT_SUFFIX))
                        .collect(toList()));
            } catch (UnsupportedFlavorException | IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
        if (!files.isEmpty()) {
            File file = files.get(0);
            FileObject fileObject = FileUtil.toFileObject(file);
            SourceGroup sourceGroup = findSourceGroupForFile(fileObject);
            if (isNull(sourceGroup)) {
                return;
            }
            String clazzFQN = fileObject.getPath()
                    .substring(sourceGroup.getRootFolder().getPath().length() + 1, fileObject.getPath().lastIndexOf(JAVA_EXT_SUFFIX))
                    .replace('/', '.');
            String clazz = unqualify(clazzFQN);
            EntityMappings entityMappings = (EntityMappings) scene.getBaseElementSpec();
            SourceExplorer source = new SourceExplorer(sourceGroup.getRootFolder(), entityMappings, singleton(clazzFQN), false);
            Optional<ClassExplorer> classExplorerOpt = Optional.empty();
            try {
                classExplorerOpt = source.createClass(clazzFQN);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (classExplorerOpt.isPresent() && widget instanceof JavaClassWidget) {
                ClassExplorer classExplorer = classExplorerOpt.get();
                JavaClassWidget<JavaClass> javaClassWidget = (JavaClassWidget) widget;
                JavaClass javaClass = javaClassWidget.getBaseElementSpec();

                if (javaClass.getClazz().equals(clazz)) {
                    JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
                    processor.processDropedClasses(scene.getModelerFile(), files);
                } else if (classExplorer.isInterface()) {
                    interfaceDropOption(widget, scene, classExplorer, clazzFQN);
                } else if (classExplorer.isClass()) {
                    classDropOption(widget, scene, classExplorer, clazzFQN);
                } else if (classExplorer.isEnum()) {
                    enumDropOption(widget, clazzFQN);
                }
            }
        }
    }

    private void classDropOption(Widget widget, IModelerScene scene, ClassExplorer classExplorer, String clazzFQN) {
        ModelerFile modelerFile = scene.getModelerFile();
        String clazz = unqualify(clazzFQN);
        JavaClassWidget<JavaClass> javaClassWidget = (JavaClassWidget) widget;
        JavaClass javaClass = javaClassWidget.getBaseElementSpec();
        Runnable addAttributesAction = () -> {
            javaClass.getAttributes().load(classExplorer);
            modelerFile.save(true);
            modelerFile.close();
            JPAFileActionListener.open(modelerFile);
        };
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem extendClass = new JMenuItem("extend class " + clazz);
        extendClass.addActionListener(e -> javaClass.setSuperclassRef(new ReferenceClass(clazzFQN)));
        popup.add(extendClass);

        JMenuItem addAttributes = new JMenuItem("copy attributes from " + clazz);
        addAttributes.addActionListener(e -> addAttributesAction.run());
        popup.add(addAttributes);

        Component comp = WindowManager.getDefault().getMainWindow();
        Point location = MouseInfo.getPointerInfo().getLocation();
        popup.show(
                comp,
                (int) location.x - comp.getLocationOnScreen().x,
                (int) location.y - comp.getLocationOnScreen().y
        );
    }

    private void interfaceDropOption(Widget widget, IModelerScene scene, ClassExplorer classExplorer, String clazzFQN) {
        ModelerFile modelerFile = scene.getModelerFile();
        String clazz = unqualify(clazzFQN);
        JavaClassWidget<JavaClass> javaClassWidget = (JavaClassWidget) widget;
        JavaClass javaClass = javaClassWidget.getBaseElementSpec();
        Runnable addAttributesAction = () -> {
            javaClass.getAttributes().load(classExplorer);
            modelerFile.save(true);
            modelerFile.close();
            JPAFileActionListener.open(modelerFile);
        };
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem implementInterface = new JMenuItem("implement interface " + clazz);
        implementInterface.addActionListener(e -> javaClass.addInterface(new ReferenceClass(clazzFQN)));
        popup.add(implementInterface);

        JMenuItem implementInterfaceWithAttributes = new JMenuItem("implement interface " + clazz + " with attributes");
        implementInterfaceWithAttributes.addActionListener(e -> {
            javaClass.addInterface(new ReferenceClass(clazzFQN));
            addAttributesAction.run();
        });
        popup.add(implementInterfaceWithAttributes);

        JMenuItem addAttributes = new JMenuItem("add attributes from " + clazz);
        addAttributes.addActionListener(e -> addAttributesAction.run());
        popup.add(addAttributes);

        Component comp = WindowManager.getDefault().getMainWindow();
        Point location = MouseInfo.getPointerInfo().getLocation();
        popup.show(
                comp,
                (int) location.x - comp.getLocationOnScreen().x,
                (int) location.y - comp.getLocationOnScreen().y
        );
    }

    private void enumDropOption(Widget widget, String clazzFQN) {
        JavaClassWidget<? extends JavaClass> javaClassWidget = (JavaClassWidget) widget;
        String clazz = unqualify(clazzFQN);
        String name = camelCase(clazz);
        String type = clazzFQN;
        if (javaClassWidget instanceof BeanClassWidget) {
            ((BeanClassWidget) javaClassWidget)
                    .addBeanAttribute(name)
                    .getBaseElementSpec()
                    .setAttributeType(type);
        } else if (javaClassWidget instanceof PersistenceClassWidget) {
            Basic basic = ((PersistenceClassWidget) javaClassWidget)
                    .addBasicAttribute(name)
                    .getBaseElementSpec();
            basic.setAttributeType(type);
            basic.setEnumerated(EnumType.DEFAULT);
        }
    }


    private void dropOnScene(Widget widget, Point point, Transferable transferable, IModelerScene scene) {
        JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
        List<File> files = new ArrayList<>();

        if (isJavaClassDrop(transferable)) {
            try {
                List<File> javaFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                files.addAll(javaFiles.stream()
                        .filter(file -> file.getPath().endsWith(JAVA_EXT_SUFFIX))
                        .collect(toList()));
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
            processor.processDropedClasses(scene.getModelerFile(), files);
        }
        files.clear();

        try {
            List<File> jsonFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            files.addAll(jsonFiles.stream()
                    .filter(file
                            -> file.getPath().toLowerCase().endsWith(".json")
                    || file.getPath().toLowerCase().endsWith(".xml")
                    || file.getPath().toLowerCase().endsWith(".yml")
                    || file.getPath().toLowerCase().endsWith(".yaml")
                    || file.getPath().toLowerCase().endsWith(".jpa")
                    ).collect(toList()));
            if (!files.isEmpty()) {
                processor.processDropedDocument(scene.getModelerFile(), jsonFiles.get(0));
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }

    protected boolean isJavaClassDrop(Transferable transferable) {
        return transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    private boolean isPackageFlavor(DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (PACKAGE_TYPE.equals(flavors[i].getSubType())
                    && PRIMARY_TYPE.equals(flavors[i].getPrimaryType())) {
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
            if (PACKAGE_TYPE.equals(flavors[i].getSubType())
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
