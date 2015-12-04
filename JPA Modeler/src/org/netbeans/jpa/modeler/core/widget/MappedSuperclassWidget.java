/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget;

import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class MappedSuperclassWidget extends PrimaryKeyContainerWidget {

    public MappedSuperclassWidget(IModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
    }

    @Override
    public void init() {
        MappedSuperclass mappedSuperclass = (MappedSuperclass) this.getBaseElementSpec();
        if (mappedSuperclass.getAttributes() == null) {
            mappedSuperclass.setAttributes(new Attributes());
//            addNewIdAttribute("id");
//            sortAttributes();
        }
        if (mappedSuperclass.getClazz() == null || mappedSuperclass.getClazz().isEmpty()) {
            mappedSuperclass.setClazz(((JPAModelerScene) this.getModelerScene()).getNextClassName("MappedSuperclass_"));
        }
        setName(mappedSuperclass.getClazz());
        setLabel(mappedSuperclass.getClazz());

    }

//    @Override
//    protected List<JMenuItem> getPopupMenuItemList() {
//        List<JMenuItem> menuList = super.getPopupMenuItemList();
//        JMenuItem addIdAttr = new JMenuItem("Add Id Attribute");
//        addIdAttr.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addNewIdAttribute(getNextAttributeName("id"));
//                MappedSuperclassWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//            }
//        });
//
//        JMenuItem addBasicAttr = new JMenuItem("Add Basic Attribute");
//        addBasicAttr.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addNewBasicAttribute(getNextAttributeName());
//                MappedSuperclassWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//
//            }
//        });
//        JMenuItem addBasicCollectionAttr = new JMenuItem("Add Basic Collection Attribute");
//        addBasicCollectionAttr.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addNewBasicCollectionAttribute(getNextAttributeName());
//                MappedSuperclassWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//
//            }
//        });
//        JMenuItem addTransientAttr = new JMenuItem("Add Transient Attribute");
//        addTransientAttr.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addNewTransientAttribute(getNextAttributeName());
//                MappedSuperclassWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//            }
//        });
//        JMenuItem addVersionAttr = new JMenuItem("Add Version Attribute");
//        addVersionAttr.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addNewVersionAttribute(getNextAttributeName());
//                MappedSuperclassWidget.this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
//            }
//        });
//        menuList.add(0, addIdAttr);
//        menuList.add(1, addBasicAttr);
//        menuList.add(2, addBasicCollectionAttr);
//        menuList.add(3, addTransientAttr);
//        menuList.add(4, addVersionAttr);
//        menuList.add(5, null);
//
//        return menuList;
//    }
    @Override
    public String getInheritenceState() {
        return "NONE";//Not implemented yet
    }
}
