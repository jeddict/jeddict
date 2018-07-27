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
package io.github.jeddict.jpa.modeler.properties.convert;

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import static io.github.jeddict.jcode.util.ProjectHelper.findSourceGroupForFile;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Converter;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.AttributeConverter;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.embedded.GenericEmbeddedEditor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
import org.openide.windows.WindowManager;

/**
 *
 * @author Gaurav Gupta
 */
public class ConvertPanel extends GenericEmbeddedEditor<Convert> {

    private final ModelerFile modelerFile;
    private Convert convert;

    public ConvertPanel(ModelerFile modelerFile) {
        this.modelerFile = modelerFile;
    }

    @Override
    public void init() {
        initComponents();
        converter_EditorPane = NBModelerUtil.getJavaSingleLineEditor(converter_WrapperPane, null, getMessage(ConvertPanel.class, "INFO_ATTRIBUTE_CONVERTER")).second();
    }

    @Override
    public Convert getValue() {
        AtomicBoolean validated = new AtomicBoolean(false);
        importAttributeConverter(converter_EditorPane.getText(), validated, modelerFile);
        if(!validated.get()){
            throw new IllegalStateException();
        }
        convert.setConverter(converter_EditorPane.getText());
        convert.setAttributeName(null);
        convert.setDisableConversion(disableConversion_CheckBox.isSelected());
        return convert;
    }

    @Override
    public void setValue(Convert convert) {
        this.convert = convert;
        converter_EditorPane.setText(convert.getConverter());
        disableConversion_CheckBox.setSelected(convert.isDisableConversion());
    }

    static void importAttributeConverter(String classHandle, AtomicBoolean validated, ModelerFile modelerFile) {
        if(StringUtils.isBlank(classHandle)){
            validated.set(true);
            return;
        }
        FileObject pkg = findSourceGroupForFile(modelerFile.getFileObject()).getRootFolder();
        try {
            JavaSource javaSource = JavaSource.create(ClasspathInfo.create(pkg));
            javaSource.runUserActionTask(controller -> {
                try {
                    controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                    TypeElement jc = controller.getElements().getTypeElement(classHandle);
                    EntityMappings entityMappings = (EntityMappings)modelerFile.getDefinitionElement();
                    Optional<Converter> converter = entityMappings.findConverter(classHandle);
                    if (jc != null) {
                        DeclaredType attributeConverterType = null;
                        if (!jc.getInterfaces().isEmpty()) { //fetch interface info
                            for (TypeMirror interfaceType : jc.getInterfaces()) {
                                if (interfaceType.getKind() == TypeKind.DECLARED
                                        && AttributeConverter.class.getName().equals(((DeclaredType) interfaceType).asElement().toString())) {
                                    attributeConverterType = (DeclaredType) interfaceType;
                                }
                            }
                        }
                        if (attributeConverterType != null && attributeConverterType.getTypeArguments().size() == 2) {
                            TypeMirror attributeType = attributeConverterType.getTypeArguments().get(0);
                            TypeMirror dbFieldType = attributeConverterType.getTypeArguments().get(1);
                            if (!entityMappings.addConverter(classHandle, attributeType.toString(), dbFieldType.toString())) {
                                message("MSG_ATTRIBUTE_CONVERTER_TYPE_CONFLICT", classHandle);
                            } else {
                                if(!converter.isPresent()) {
                                    message("MSG_ATTRIBUTE_CONVERTER_TYPE_REGISTERED", classHandle, attributeType.toString(), dbFieldType.toString());
                                }
                                validated.set(true);
                            }
                        } else {
                            message("MSG_ATTRIBUTE_CONVERTER_NOT_IMPLEMENTED", classHandle);
                        }
                    } else {
                        if(converter.isPresent()){
                            validated.set(true);
                        } else {
                           message("MSG_ARTIFACT_NOT_FOUND", classHandle, pkg.getPath());
                        }
                    }
                } catch (IOException t) {
                    ExceptionUtils.printStackTrace(t);
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void message(String id, Object... param){
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(ConvertPanel.class, id, param));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootLayeredPane = new javax.swing.JLayeredPane();
        converter_LayeredPane = new javax.swing.JLayeredPane();
        converter_Label = new javax.swing.JLabel();
        converter_WrapperPane = new javax.swing.JLayeredPane();
        dataType_Action = new javax.swing.JButton();
        disableConversion_CheckBox = new javax.swing.JCheckBox();

        converter_LayeredPane.setToolTipText(org.openide.util.NbBundle.getMessage(ConvertPanel.class, "OverrideConvertPanel.converter_LayeredPane.toolTipText")); // NOI18N
        converter_LayeredPane.setPreferredSize(new java.awt.Dimension(170, 27));
        converter_LayeredPane.setLayout(new java.awt.BorderLayout());

        converter_Label.setText(org.openide.util.NbBundle.getMessage(ConvertPanel.class, "OverrideConvertPanel.converter_Label.text")); // NOI18N
        converter_Label.setPreferredSize(new java.awt.Dimension(65, 14));
        converter_LayeredPane.add(converter_Label, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout converter_WrapperPaneLayout = new javax.swing.GroupLayout(converter_WrapperPane);
        converter_WrapperPane.setLayout(converter_WrapperPaneLayout);
        converter_WrapperPaneLayout.setHorizontalGroup(
            converter_WrapperPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 338, Short.MAX_VALUE)
        );
        converter_WrapperPaneLayout.setVerticalGroup(
            converter_WrapperPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        converter_LayeredPane.add(converter_WrapperPane, java.awt.BorderLayout.CENTER);

        dataType_Action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/properties/resource/searchbutton.png"))); // NOI18N
        dataType_Action.setPreferredSize(new java.awt.Dimension(37, 37));
        dataType_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataType_ActionActionPerformed(evt);
            }
        });
        converter_LayeredPane.add(dataType_Action, java.awt.BorderLayout.EAST);

        disableConversion_CheckBox.setText(org.openide.util.NbBundle.getMessage(ConvertPanel.class, "OverrideConvertPanel.disableConversion_CheckBox.text")); // NOI18N
        disableConversion_CheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(ConvertPanel.class, "INFO_DISABLE_CONVERSION")); // NOI18N

        rootLayeredPane.setLayer(converter_LayeredPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        rootLayeredPane.setLayer(disableConversion_CheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout rootLayeredPaneLayout = new javax.swing.GroupLayout(rootLayeredPane);
        rootLayeredPane.setLayout(rootLayeredPaneLayout);
        rootLayeredPaneLayout.setHorizontalGroup(
            rootLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(converter_LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(rootLayeredPaneLayout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(disableConversion_CheckBox)
                .addContainerGap(272, Short.MAX_VALUE))
        );
        rootLayeredPaneLayout.setVerticalGroup(
            rootLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootLayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(converter_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(disableConversion_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dataType_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataType_ActionActionPerformed
        String dataType = NBModelerUtil.browseClass(modelerFile, converter_EditorPane.getText());
        if (StringUtils.isNotEmpty(dataType)) {
            converter_EditorPane.setText(dataType);
        }
    }//GEN-LAST:event_dataType_ActionActionPerformed
    private JEditorPane converter_EditorPane;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel converter_Label;
    private javax.swing.JLayeredPane converter_LayeredPane;
    private javax.swing.JLayeredPane converter_WrapperPane;
    private javax.swing.JButton dataType_Action;
    private javax.swing.JCheckBox disableConversion_CheckBox;
    private javax.swing.JLayeredPane rootLayeredPane;
    // End of variables declaration//GEN-END:variables
}
