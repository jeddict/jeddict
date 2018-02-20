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
package io.github.jeddict.jpa.modeler.properties.implement;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.jcode.util.SourceGroupSupport;
import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.jpa.modeler.internal.jpqleditor.ModelerPanel;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.source.JavaSourceParserUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.EntityComponent;
import org.netbeans.modeler.properties.spec.Entity;
import org.netbeans.modeler.properties.spec.RowValue;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class JavaClassArtifactPanel extends EntityComponent<ReferenceClass> implements ModelerPanel {

    private ReferenceClass referenceClass;
    private final ModelerFile modelerFile;
    private final String artifactType;

    public JavaClassArtifactPanel(ModelerFile modelerFile, String artifactType) {
        this.modelerFile = modelerFile;
        this.artifactType = artifactType;
    }

    @Override
    public void postConstruct() {
        initComponents();
        class_EditorPane = NBModelerUtil.getJavaSingleLineEditor(class_wrapperPanel, null, null).second();

    }

    @Override
    public void init() {
    }

    @Override
    public void createEntity(Class<? extends Entity> entityWrapperType) {
        this.setTitle(String.format("Add new %s ", artifactType));
        if (entityWrapperType == RowValue.class) {
            this.setEntity(new RowValue(new Object[5]));
        }
        referenceClass = null;
        class_EditorPane.setText(EMPTY);
        dataType_ActionActionPerformed(null);
    }

    @Override
    public void updateEntity(Entity<ReferenceClass> entityValue) {
        this.setTitle(String.format("Update %s ", artifactType));
        if (entityValue.getClass() == RowValue.class) {
            this.setEntity(entityValue);
            Object[] row = ((RowValue) entityValue).getRow();
            referenceClass = (ReferenceClass) row[0];
            class_EditorPane.setText(referenceClass.getName());
        }

    }

    private void importFields(ElementHandle<TypeElement> classHandle) {
        FileObject pkg = SourceGroupSupport.findSourceGroupForFile(modelerFile.getFileObject()).getRootFolder();
        try {
            JavaSource javaSource = JavaSource.create(ClasspathInfo.create(pkg));
            javaSource.runUserActionTask((CompilationController controller) -> {
                try {
                    controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);//classHandle.resolve(controller);//
                    TypeElement jc = controller.getElements().getTypeElement(classHandle.getQualifiedName());
                    if (jc != null) {
                        Map<String, String> elements = new LinkedHashMap<>();
                        for (ExecutableElement method : JavaSourceParserUtil.getMethods(jc)) {
                            try {
                                String methodName = method.getSimpleName().toString();
                                if (methodName.startsWith("get") || methodName.startsWith("is")) {
//                                    elements.put
                                }
                            } catch (TypeNotPresentException ex) {
                                ex.printStackTrace();
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(JavaClassArtifactPanel.class, "MSG_ARTIFACT_NOT_FOUND"));
                    }
                } catch (IOException t) {
                    ExceptionUtils.printStackTrace(t);
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_jLayeredPane = new javax.swing.JLayeredPane();
        action_jLayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        dataType_Action = new javax.swing.JButton();
        class_wrapperPanel = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(JavaClassArtifactPanel.class, "JavaClassArtifactPanel.save_Button.text")); // NOI18N
        save_Button.setToolTipText(org.openide.util.NbBundle.getMessage(JavaClassArtifactPanel.class, "JavaClassArtifactPanel.save_Button.toolTipText")); // NOI18N
        save_Button.setSelected(true);
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(JavaClassArtifactPanel.class, "JavaClassArtifactPanel.cancel_Button.text")); // NOI18N
        cancel_Button.setToolTipText(org.openide.util.NbBundle.getMessage(JavaClassArtifactPanel.class, "JavaClassArtifactPanel.cancel_Button.toolTipText")); // NOI18N
        cancel_Button.setPreferredSize(new java.awt.Dimension(60, 23));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        action_jLayeredPane.setLayer(save_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        action_jLayeredPane.setLayer(cancel_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout action_jLayeredPaneLayout = new javax.swing.GroupLayout(action_jLayeredPane);
        action_jLayeredPane.setLayout(action_jLayeredPaneLayout);
        action_jLayeredPaneLayout.setHorizontalGroup(
            action_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_jLayeredPaneLayout.createSequentialGroup()
                .addComponent(save_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        action_jLayeredPaneLayout.setVerticalGroup(
            action_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(save_Button)
                .addComponent(cancel_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout class_wrapperPanelLayout = new javax.swing.GroupLayout(class_wrapperPanel);
        class_wrapperPanel.setLayout(class_wrapperPanelLayout);
        class_wrapperPanelLayout.setHorizontalGroup(
            class_wrapperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        class_wrapperPanelLayout.setVerticalGroup(
            class_wrapperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        root_jLayeredPane.setLayer(action_jLayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(dataType_Action, javax.swing.JLayeredPane.DEFAULT_LAYER);
        root_jLayeredPane.setLayer(class_wrapperPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_jLayeredPaneLayout = new javax.swing.GroupLayout(root_jLayeredPane);
        root_jLayeredPane.setLayout(root_jLayeredPaneLayout);
        root_jLayeredPaneLayout.setHorizontalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(class_wrapperPanel)
                    .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                        .addComponent(dataType_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                        .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        root_jLayeredPaneLayout.setVerticalGroup(
            root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_jLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(class_wrapperPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(root_jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(action_jLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataType_Action, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root_jLayeredPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean validateField() {
        String _class = this.class_EditorPane.getText().trim();
        int genericIndex = _class.indexOf('<');//generic type
        if (_class.length() <= 0 /*|| Pattern.compile("[^\\w-]").matcher(this.id_TextField.getText().trim()).find()*/) {
            JOptionPane.showMessageDialog(this, String.format("%s can't be empty", artifactType), "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (genericIndex > 1 ? !JavaIdentifiers.isValidPackageName(_class.substring(0, genericIndex)) : !JavaIdentifiers.isValidPackageName(_class)) {
            JOptionPane.showMessageDialog(this, String.format("Invalid %s type", artifactType), "Invalid Value", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        if (!validateField()) {
            return;
        }
        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            if (row[0] == null) {
                referenceClass = new ReferenceClass();
            } else {
                referenceClass = (ReferenceClass) row[0];
            }
        }

        referenceClass.setName(class_EditorPane.getText().trim());

        if (this.getEntity().getClass() == RowValue.class) {
            Object[] row = ((RowValue) this.getEntity()).getRow();
            row[0] = referenceClass;
            row[1] = referenceClass.isEnable();
            row[2] = referenceClass.getName();
        }

        saveActionPerformed(evt);
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        Optional<ElementHandle<TypeElement>> dataTypeHandler = NBModelerUtil.browseElement(modelerFile, class_EditorPane.getText());
//        if (StringUtils.isNotEmpty(dataType)) {
        if (dataTypeHandler.isPresent()) {
//            importFields(dataTypeHandler.get());
            class_EditorPane.setText(dataTypeHandler.get().getQualifiedName());
        }
    }//GEN-LAST:event_dataType_ActionActionPerformed
    private JEditorPane class_EditorPane;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_jLayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLayeredPane class_wrapperPanel;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JLayeredPane root_jLayeredPane;
    private javax.swing.JButton save_Button;
    // End of variables declaration//GEN-END:variables

    @Override
    public ModelerFile getModelerFile() {
        return modelerFile;
    }
}
