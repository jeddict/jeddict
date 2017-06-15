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
package org.netbeans.jpa.modeler.specification.model.workspace;

import static java.util.Collections.singletonMap;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import static javax.swing.SwingUtilities.invokeLater;
import org.netbeans.jeddict.analytics.JeddictLogger;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.IAttributes;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceElement;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceItem;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.CREATE_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.DELETE_ALL_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.DELETE_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.EDIT_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.HOME_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.WORKSPACE_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.widget.design.NodeTextDesign;
import org.netbeans.modeler.widget.design.PinTextDesign;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author jGauravGupta
 */
public class WorkSpaceManager {
    
    private final JMenu workSpaceMenu;
    private final JPAModelerScene scene;
    public final static String WORK_SPACE = "WORK_SPACE";
    public final static String MAIN_WORK_SPACE = "Main";
    
    public WorkSpaceManager(JPAModelerScene scene) {
        this.scene = scene;
        workSpaceMenu = new JMenu(Bundle.WORK_SPACE());
        workSpaceMenu.setIcon(WORKSPACE_ICON);
    }
    
    public void openWorkSpace(boolean force, WorkSpace workSpace) {
        ModelerFile file = scene.getModelerFile();
        boolean reload = force;
        if (!force) {
            int option = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), String.format(Bundle.OPEN_WORK_SPACE_CONTENT(), workSpace.getName()), Bundle.OPEN_WORK_SPACE_TITLE() , JOptionPane.YES_NO_OPTION);
            reload = option == javax.swing.JOptionPane.OK_OPTION;
        }
        if (reload) {
            invokeLater(() -> {
                file.getModelerPanelTopComponent().close();
                JPAFileActionListener fileListener = new JPAFileActionListener((JPAFileDataObject) file.getModelerFileDataObject());
                fileListener.openModelerFile(singletonMap(WORK_SPACE, workSpace));
            });
        } else {
            loadWorkspaceUI();
        }
    }

    @NbBundle.Messages({
        "CREATE_WORK_SPACE=Create new",
        "DELETE_ALL_WORK_SPACE=Delete All",
        "UPDATE_CURRENT_WORK_SPACE=Modify ",
        "DELETE_CURRENT_WORK_SPACE=Delete ",
        "DELETE_WORK_SPACE_TITLE=Delete WorkSpace",
        "DELETE_WORK_SPACE_CONTENT=Are you sure to delete workspace ?",
        "DELETE_ALL_WORK_SPACE_TITLE=Delete all WorkSpace",
        "DELETE_ALL_WORK_SPACE_CONTENT=Are you sure you want to delete all workspace ?",
        "OPEN_WORK_SPACE_TITLE=Open WorkSpace",
        "OPEN_WORK_SPACE_CONTENT=Are you want to open %s workspace now ?",
        "WORK_SPACE=WorkSpace"
    })
    public void loadWorkspaceUI() {
        EntityMappings entityMappings = scene.getBaseElementSpec();
        workSpaceMenu.removeAll();

        JMenuItem createWPItem = new JMenuItem(Bundle.CREATE_WORK_SPACE(), CREATE_ICON);
        createWPItem.addActionListener(e -> {
            WorkSpaceDialog workSpaceDialog = new WorkSpaceDialog(scene, null);
            workSpaceDialog.setVisible(true);
            if (workSpaceDialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                WorkSpace workSpace = workSpaceDialog.getWorkSpace();
                entityMappings.addWorkSpace(workSpace);
                openWorkSpace(false, workSpace);
                JeddictLogger.createWorkSpace();
            }
        });
        workSpaceMenu.add(createWPItem);

        if (entityMappings.getWorkSpaces().size() > 1) {
            JMenuItem deleteAllWPItem = new JMenuItem(Bundle.DELETE_ALL_WORK_SPACE(), DELETE_ALL_ICON);
            deleteAllWPItem.addActionListener(e -> {
                WorkSpaceTrashDialog workSpaceDialog = new WorkSpaceTrashDialog(scene);
                workSpaceDialog.setVisible(true);
                if (workSpaceDialog.isCurrentWorkSpaceDeleted()) {
                    openWorkSpace(true, entityMappings.getRootWorkSpace());
                } else {
                    loadWorkspaceUI();
                }
                JeddictLogger.deleteAllWorkSpace();
            });
            workSpaceMenu.add(deleteAllWPItem);
        }
        workSpaceMenu.addSeparator();

        if (entityMappings.getCurrentWorkSpace() != entityMappings.getRootWorkSpace()) {
            JMenuItem updateWPItem = new JMenuItem(Bundle.UPDATE_CURRENT_WORK_SPACE() + entityMappings.getCurrentWorkSpace().getName(), EDIT_ICON);
            updateWPItem.addActionListener(e -> {
                WorkSpaceDialog workSpaceDialog = new WorkSpaceDialog(scene, entityMappings.getCurrentWorkSpace());
                workSpaceDialog.setVisible(true);
                if (workSpaceDialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                    openWorkSpace(true, entityMappings.getCurrentWorkSpace());
                }
                JeddictLogger.updateWorkSpace();
            });
            workSpaceMenu.add(updateWPItem);

            JMenuItem deleteWPItem = new JMenuItem(Bundle.DELETE_CURRENT_WORK_SPACE() + entityMappings.getCurrentWorkSpace().getName(), DELETE_ICON);
            deleteWPItem.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), Bundle.DELETE_WORK_SPACE_CONTENT(), Bundle.DELETE_WORK_SPACE_TITLE(), JOptionPane.YES_NO_OPTION);
                if (option == javax.swing.JOptionPane.OK_OPTION) {
                    entityMappings.removeWorkSpace(entityMappings.getCurrentWorkSpace());
                    entityMappings.setNextWorkSpace(entityMappings.getRootWorkSpace());
                    scene.getModelerPanelTopComponent().changePersistenceState(false);
                    openWorkSpace(true, entityMappings.getRootWorkSpace());
                }
                JeddictLogger.deleteWorkSpace();
            });
            workSpaceMenu.add(deleteWPItem);
            workSpaceMenu.addSeparator();
        }

        int count = 0;
//        final int MAX_LIMIT = 10;
        for (WorkSpace ws : entityMappings.getWorkSpaces()) {
            JRadioButtonMenuItem workSpaceMenuItem;
            if (count == 0) {
                workSpaceMenuItem = new JRadioButtonMenuItem(ws.getName(), HOME_ICON);
            } else {
                workSpaceMenuItem = new JRadioButtonMenuItem(ws.getName());
            }
            ++count;
            if (entityMappings.getCurrentWorkSpace() == ws) {
                workSpaceMenuItem.setSelected(true);
            }
//            if (count < MAX_LIMIT) {
//                workSpaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(Character.forDigit(count, 10), InputEvent.SHIFT_DOWN_MASK));
//            }
            workSpaceMenuItem.addActionListener(e -> {
                openWorkSpace(true, ws);
                JeddictLogger.openWorkSpace(ws.getItems().size());
            });
            workSpaceMenu.add(workSpaceMenuItem);
        }
    }

    /**
     * @return the workSpaceMenu
     */
    public JMenu getWorkSpaceMenu() {
        return workSpaceMenu;
    }

    
    public void reloadMainWorkSpace(){
        EntityMappings entityMappings = scene.getBaseElementSpec();
        if (entityMappings.getRootWorkSpace().getItems().size() != entityMappings.getJavaClass().size()) {
            entityMappings.getRootWorkSpace().setItems(
                    entityMappings.getJavaClass()
                            .stream()
                            .map(WorkSpaceItem::new)
                            .collect(toSet())
            );
        }
    }
    
    public void loadDependentItems(WorkSpace workSpace) {
        Set<JavaClass<? extends IAttributes>> selectedClasses = workSpace.getItems()
                .stream()
                .map(wi -> (JavaClass<? extends IAttributes>) wi.getJavaClass())
                .collect(toSet());
        
        Set<JavaClass<? extends IAttributes>> dependantClasses = findDependents(selectedClasses);
        if (dependantClasses.size() > 0) {
            selectedClasses.addAll(dependantClasses);
            
            workSpace.setItems(
                    selectedClasses
                            .stream()
                            .map(WorkSpaceItem::new)
                            .collect(toSet())
            );
        }
    }

    static Set<JavaClass<? extends IAttributes>> findDependents(Set<JavaClass<? extends IAttributes>> selectedClasses){
        Set<JavaClass<? extends IAttributes>> dependantClasses = selectedClasses.stream()
                .flatMap(_class -> _class.getAllSuperclass().stream())
                .collect(toSet());
        dependantClasses.removeAll(selectedClasses);
        return dependantClasses;
    }
        
    public void syncWorkSpaceItem() {
        EntityMappings entityMappings = scene.getBaseElementSpec();
        for (WorkSpaceItem item : entityMappings.getCurrentWorkSpace().getItems()) {
            IBaseElementWidget widget = scene.getBaseElement(item.getJavaClass().getId());
            if (widget != null && widget instanceof JavaClassWidget) {
                JavaClassWidget<JavaClass> classWidget = (JavaClassWidget<JavaClass>) widget;
                if (!scene.isSceneGenerating()) {
                    item.setX(classWidget.getSceneViewBound().x);
                    item.setY(classWidget.getSceneViewBound().y);
                } 
                item.setTextDesign(classWidget.getTextDesign().isChanged()
                        ? (NodeTextDesign)classWidget.getTextDesign():null);
                Map<Attribute, WorkSpaceElement> cache = item.getWorkSpaceElementMap();
                item.setWorkSpaceElement(
                        classWidget.getAllAttributeWidgets(false)
                                .stream()
                                .map(attrWidget -> new WorkSpaceElement(attrWidget.getBaseElementSpec(), (PinTextDesign) attrWidget.getTextDesign()))
                                .collect(toList())
                );
                item.getWorkSpaceElement()
                        .stream()
                        .filter(wse -> cache.get(wse.getAttribute())!=null)
                        .forEach(wse -> wse.setJsonbTextDesign(
                                cache.get(wse.getAttribute()).getJsonbTextDesign().isChanged()?
                                        cache.get(wse.getAttribute()).getJsonbTextDesign():null
                        ));
            } else {
                item.setLocation(null);
            }
        }
    }
    

    
    public void updateWorkSpace() {
        EntityMappings entityMappings = scene.getBaseElementSpec();
        syncWorkSpaceItem();
        entityMappings.setCurrentWorkSpace(entityMappings.getNextWorkSpace());
        entityMappings.setJPADiagram(null);
    }
    
    
}
