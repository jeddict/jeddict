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
package org.netbeans.jpa.modeler.visiblity.javaclass;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import org.netbeans.jpa.modeler.core.widget.ui.GenericDialog;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;

public class ClassWidgetVisibilityController extends GenericDialog {

    private JavaClassListModel classListModel;
    private EntityMappings entityMappings;

    /**
     * Creates new form NewJDialog
     */
    public ClassWidgetVisibilityController(EntityMappings entityMappings) {
        this.entityMappings = entityMappings;
        initComponents();
        this.setTitle("Java Class Visibility Explorer");

        classListModel = new JavaClassListModel();
        classListModel.setClassElements(getClassList());

        class_List.setModel(classListModel);
        class_List.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        for (int i = 0; i < classListModel.getSize(); i++) {
            ComboBoxValue<JavaClass> classItem = (ComboBoxValue<JavaClass>) classListModel.getElementAt(i);
            if (!classItem.getValue().isVisibile()) {
                class_List.setSelectedIndex(i);
            }
        }
    }

    private List<ComboBoxValue<JavaClass>> getClassList() {
        List<ComboBoxValue<JavaClass>> values = new ArrayList<ComboBoxValue<JavaClass>>();
        for (Entity entity : entityMappings.getEntity()) {
            values.add(new ComboBoxValue<JavaClass>(entity.getId(), entity, entity.getClazz()));
        }
        for (MappedSuperclass mappedSuperclass : entityMappings.getMappedSuperclass()) {
            values.add(new ComboBoxValue<JavaClass>(mappedSuperclass.getId(), mappedSuperclass, mappedSuperclass.getClazz()));
        }
        for (Embeddable embeddable : entityMappings.getEmbeddable()) {
            values.add(new ComboBoxValue<JavaClass>(embeddable.getId(), embeddable, embeddable.getClazz()));
        }
        return values;
    }

    private void initClassVisibility() {
        for (Entity entity : entityMappings.getEntity()) {
            entity.setVisibile(true);
            for (RelationAttribute relationAttribute : entity.getAttributes().getRelationAttributes()) {
                relationAttribute.setVisibile(true);
            }
            for (Embedded embedded : entity.getAttributes().getEmbedded()) {
                embedded.setVisibile(true);
            }
            EmbeddedId embeddedId = entity.getAttributes().getEmbeddedId();
            if (embeddedId != null) {
                embeddedId.setVisibile(true);
            }
            for (ElementCollection elementCollection : entity.getAttributes().getElementCollection()) {
                if (elementCollection.getConnectedClassId() != null) {
                    elementCollection.setVisibile(true);
                }
            }
        }
        for (MappedSuperclass mappedSuperclass : entityMappings.getMappedSuperclass()) {
            mappedSuperclass.setVisibile(true);
            for (RelationAttribute relationAttribute : mappedSuperclass.getAttributes().getRelationAttributes()) {
                relationAttribute.setVisibile(true);
            }
            for (Embedded embedded : mappedSuperclass.getAttributes().getEmbedded()) {
                embedded.setVisibile(true);
            }
            EmbeddedId embeddedId = mappedSuperclass.getAttributes().getEmbeddedId();
            if (embeddedId != null) {
                embeddedId.setVisibile(true);
            }
            for (ElementCollection elementCollection : mappedSuperclass.getAttributes().getElementCollection()) {
                if (elementCollection.getConnectedClassId() != null) {
                    elementCollection.setVisibile(true);
                }
            }
        }
        for (Embeddable embeddable : entityMappings.getEmbeddable()) {
            embeddable.setVisibile(true);
            for (RelationAttribute relationAttribute : embeddable.getAttributes().getRelationAttributes()) {
                relationAttribute.setVisibile(true);
            }
            for (Embedded embedded : embeddable.getAttributes().getEmbedded()) {
                embedded.setVisibile(true);
            }
            for (ElementCollection elementCollection : embeddable.getAttributes().getElementCollection()) { // remove this block // Embeddable can not contain ElementCollection[Embeddable]
                if (elementCollection.getConnectedClassId() != null) {
                    elementCollection.setVisibile(true);
                }
            }
        }
    }

    private void manageClassVisibility() {
        for (Entity entity : entityMappings.getEntity()) {
            for (RelationAttribute relationAttribute : entity.getAttributes().getRelationAttributes()) {
                if (!entityMappings.getEntity(relationAttribute.getConnectedEntityId()).isVisibile()) {
                    relationAttribute.setVisibile(false);
                }
            }
            for (Embedded embedded : entity.getAttributes().getEmbedded()) {
                if (!entityMappings.getEmbedded(embedded.getConnectedClassId()).isVisibile()) {
                    embedded.setVisibile(false);
                }
            }
            EmbeddedId embeddedId = entity.getAttributes().getEmbeddedId();
            if (embeddedId != null && !entityMappings.getEmbedded(embeddedId.getConnectedClassId()).isVisibile()) {
                embeddedId.setVisibile(false);
            }
            for (ElementCollection elementCollection : entity.getAttributes().getElementCollection()) {
                if (elementCollection.getConnectedClassId() != null && !entityMappings.getEmbedded(elementCollection.getConnectedClassId()).isVisibile()) {
                    elementCollection.setVisibile(false);
                }
            }
        }
        for (MappedSuperclass mappedSuperclass : entityMappings.getMappedSuperclass()) {
            for (RelationAttribute relationAttribute : mappedSuperclass.getAttributes().getRelationAttributes()) {
                if (!entityMappings.getEntity(relationAttribute.getConnectedEntityId()).isVisibile()) {
                    relationAttribute.setVisibile(false);
                }
            }
            for (Embedded embedded : mappedSuperclass.getAttributes().getEmbedded()) {
                if (!entityMappings.getEmbedded(embedded.getConnectedClassId()).isVisibile()) {
                    embedded.setVisibile(false);
                }
            }
            EmbeddedId embeddedId = mappedSuperclass.getAttributes().getEmbeddedId();
            if (embeddedId != null && !entityMappings.getEmbedded(embeddedId.getConnectedClassId()).isVisibile()) {
                embeddedId.setVisibile(false);
            }
            for (ElementCollection elementCollection : mappedSuperclass.getAttributes().getElementCollection()) {
                if (elementCollection.getConnectedClassId() != null && !entityMappings.getEmbedded(elementCollection.getConnectedClassId()).isVisibile()) {
                    elementCollection.setVisibile(false);
                }
            }
        }
        for (Embeddable embeddable : entityMappings.getEmbeddable()) {
            for (RelationAttribute relationAttribute : embeddable.getAttributes().getRelationAttributes()) {
                if (!entityMappings.getEntity(relationAttribute.getConnectedEntityId()).isVisibile()) {
                    relationAttribute.setVisibile(false);
                }
            }
            for (Embedded embedded : embeddable.getAttributes().getEmbedded()) {
                if (!entityMappings.getEmbedded(embedded.getConnectedClassId()).isVisibile()) {
                    embedded.setVisibile(false);
                }
            }
            for (ElementCollection elementCollection : embeddable.getAttributes().getElementCollection()) {// remove this block // Embeddable can not contain ElementCollection[Embeddable]
                if (elementCollection.getConnectedClassId() != null && !entityMappings.getEmbedded(elementCollection.getConnectedClassId()).isVisibile()) {
                    elementCollection.setVisibile(false);
                }
            }
        }
    }

    private void manageInheritenceVisibility() {
        for (JavaClass javaClass : entityMappings.getJavaClass()) {
             isChildVisibile(entityMappings , javaClass);
        }
    }
    
    private boolean isChildVisibile(EntityMappings entityMappings , JavaClass javaClass) {
            if (!javaClass.isVisibile()) {
                List<JavaClass> javaClassList = entityMappings.getAllSubClass(javaClass.getId());
                boolean hidden = true;
                for (JavaClass javaClass_TMP : javaClassList) {
                   if (javaClass_TMP.isVisibile()){
                        hidden = false;
                       break;
                   } else if (!javaClass_TMP.isVisibile() && isChildVisibile(entityMappings , javaClass_TMP)) {
                        hidden = false;
                    }
                }
                if(!hidden){
                  javaClass.setVisibile(true);
                }
            } 
            return javaClass.isVisibile();
    }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_LayeredPane = new javax.swing.JLayeredPane();
        class_LayeredPane = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        class_List = new javax.swing.JList();
        action_LayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        class_LayeredPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(ClassWidgetVisibilityController.class, "ClassWidgetVisibilityController.class_LayeredPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        class_LayeredPane.setForeground(new java.awt.Color(51, 51, 51));

        jScrollPane1.setViewportView(class_List);

        javax.swing.GroupLayout class_LayeredPaneLayout = new javax.swing.GroupLayout(class_LayeredPane);
        class_LayeredPane.setLayout(class_LayeredPaneLayout);
        class_LayeredPaneLayout.setHorizontalGroup(
            class_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
        );
        class_LayeredPaneLayout.setVerticalGroup(
            class_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );
        class_LayeredPane.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(ClassWidgetVisibilityController.class, "ClassWidgetVisibilityController.save_Button.text")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(ClassWidgetVisibilityController.class, "ClassWidgetVisibilityController.cancel_Button.text")); // NOI18N
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout action_LayeredPaneLayout = new javax.swing.GroupLayout(action_LayeredPane);
        action_LayeredPane.setLayout(action_LayeredPaneLayout);
        action_LayeredPaneLayout.setHorizontalGroup(
            action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(save_Button)
                .addGap(18, 18, 18)
                .addComponent(cancel_Button)
                .addContainerGap())
        );
        action_LayeredPaneLayout.setVerticalGroup(
            action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_LayeredPaneLayout.createSequentialGroup()
                .addGroup(action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_Button)
                    .addComponent(cancel_Button))
                .addGap(0, 11, Short.MAX_VALUE))
        );
        action_LayeredPane.setLayer(save_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        action_LayeredPane.setLayer(cancel_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ClassWidgetVisibilityController.class, "ClassWidgetVisibilityController.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout root_LayeredPaneLayout = new javax.swing.GroupLayout(root_LayeredPane);
        root_LayeredPane.setLayout(root_LayeredPaneLayout);
        root_LayeredPaneLayout.setHorizontalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                        .addComponent(class_LayeredPane)
                        .addContainerGap())))
        );
        root_LayeredPaneLayout.setVerticalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(class_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)))
        );
        root_LayeredPane.setLayer(class_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(action_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_LayeredPane.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_LayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        initClassVisibility();
        if (class_List.getSelectedValues().length != 0) {
            for (Object obj : class_List.getSelectedValues()) {
                ComboBoxValue<JavaClass> classItem = (ComboBoxValue<JavaClass>) obj;
                classItem.getValue().setVisibile(false);
            }
            manageClassVisibility();
            manageInheritenceVisibility();
        }
        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_LayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLayeredPane class_LayeredPane;
    private javax.swing.JList class_List;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLayeredPane root_LayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables

    private class JavaClassListModel extends DefaultListModel {

        private List<ComboBoxValue<JavaClass>> classElements = new ArrayList<ComboBoxValue<JavaClass>>();

        @Override
        public int getSize() {
            return getClassElements().size();
        }

        @Override
        public Object getElementAt(int i) {
            return getClassElements().get(i);
        }

        /**
         * @return the errorElements
         */
        public List<ComboBoxValue<JavaClass>> getClassElements() {
            return classElements;
        }

        /**
         * @param classElements the errorElements to set
         */
        public void setClassElements(List<ComboBoxValue<JavaClass>> classElements) {
            this.classElements = classElements;
            class_List.setPreferredSize(new Dimension(250, classListModel.getSize() * 15));
        }

        public void addClassElement(ComboBoxValue<JavaClass> classElement) {
            super.addElement(classElement);
            this.classElements.add(classElement);
            class_List.setPreferredSize(new Dimension(250, classListModel.getSize() * 15));
        }

        public void removeClassElements(ComboBoxValue<JavaClass> classElement) {
            super.removeElement(classElement);
            this.classElements.remove(classElement);
            class_List.setPreferredSize(new Dimension(250, classListModel.getSize() * 15));
        }
    }

}
