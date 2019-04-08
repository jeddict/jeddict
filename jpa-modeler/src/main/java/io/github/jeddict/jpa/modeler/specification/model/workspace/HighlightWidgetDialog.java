/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.specification.model.workspace;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import io.github.jeddict.analytics.JeddictLogger;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.properties.window.GenericDialog;
import org.netbeans.modeler.scene.vmd.AbstractPModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.design.ITextDesign;
import org.netbeans.modeler.widget.node.IWidget;

/**
 *
 * @author jGauravGupta
 */
public class HighlightWidgetDialog extends GenericDialog {

    private final ITextDesign textDesign;
    private final IModelerScene modelerScene;
    private final IWidget widget;

    /**
     * Creates new form HighlightWidgetDialog
     */
    public HighlightWidgetDialog(IWidget widget, ITextDesign textDesign) {
        this.textDesign = textDesign;
        this.modelerScene = widget.getModelerScene();
        this.widget = widget;
        initComponents();
        setCompValue(textDesign.getStyle(), textDesign.getSize(), textDesign.getColor());
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root_LayeredPane = new javax.swing.JLayeredPane();
        layout_LayeredPane2 = new javax.swing.JLayeredPane();
        color_LayeredPane = new javax.swing.JLayeredPane();
        foregroundColor_Label = new javax.swing.JLabel();
        colorComp_LayeredPane = new javax.swing.JLayeredPane();
        color_Button = new ColorChooserButton();
        style_LayeredPane = new javax.swing.JLayeredPane();
        style_Label = new javax.swing.JLabel();
        styleComp_LayeredPane = new javax.swing.JLayeredPane();
        style_ComboBox = new javax.swing.JComboBox<>();
        size_LayeredPane = new javax.swing.JLayeredPane();
        size_Label = new javax.swing.JLabel();
        sizeComp_LayeredPane = new javax.swing.JLayeredPane();
        size_Spinner = new javax.swing.JSpinner();
        action_LayeredPane = new javax.swing.JLayeredPane();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        reset_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.title")); // NOI18N

        layout_LayeredPane2.setLayout(new java.awt.GridLayout(3, 1));

        color_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(foregroundColor_Label, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.foregroundColor_Label.text")); // NOI18N
        color_LayeredPane.add(foregroundColor_Label, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(color_Button, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.color_Button.text")); // NOI18N

        colorComp_LayeredPane.setLayer(color_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout colorComp_LayeredPaneLayout = new javax.swing.GroupLayout(colorComp_LayeredPane);
        colorComp_LayeredPane.setLayout(colorComp_LayeredPaneLayout);
        colorComp_LayeredPaneLayout.setHorizontalGroup(
            colorComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(color_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        colorComp_LayeredPaneLayout.setVerticalGroup(
            colorComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colorComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(color_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        color_LayeredPane.add(colorComp_LayeredPane, java.awt.BorderLayout.CENTER);

        layout_LayeredPane2.add(color_LayeredPane);

        style_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(style_Label, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.style_Label.text")); // NOI18N
        style_Label.setPreferredSize(new java.awt.Dimension(35, 14));
        style_LayeredPane.add(style_Label, java.awt.BorderLayout.WEST);

        loadStyleType();

        styleComp_LayeredPane.setLayer(style_ComboBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout styleComp_LayeredPaneLayout = new javax.swing.GroupLayout(styleComp_LayeredPane);
        styleComp_LayeredPane.setLayout(styleComp_LayeredPaneLayout);
        styleComp_LayeredPaneLayout.setHorizontalGroup(
            styleComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(styleComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(style_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        styleComp_LayeredPaneLayout.setVerticalGroup(
            styleComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(styleComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(style_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        style_LayeredPane.add(styleComp_LayeredPane, java.awt.BorderLayout.CENTER);

        layout_LayeredPane2.add(style_LayeredPane);

        size_LayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(size_Label, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.size_Label.text")); // NOI18N
        size_Label.setPreferredSize(new java.awt.Dimension(35, 14));
        size_LayeredPane.add(size_Label, java.awt.BorderLayout.WEST);

        size_Spinner.setModel(new javax.swing.SpinnerNumberModel(12, 1, 32, 1));

        sizeComp_LayeredPane.setLayer(size_Spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout sizeComp_LayeredPaneLayout = new javax.swing.GroupLayout(sizeComp_LayeredPane);
        sizeComp_LayeredPane.setLayout(sizeComp_LayeredPaneLayout);
        sizeComp_LayeredPaneLayout.setHorizontalGroup(
            sizeComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sizeComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(size_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sizeComp_LayeredPaneLayout.setVerticalGroup(
            sizeComp_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sizeComp_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(size_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        size_LayeredPane.add(sizeComp_LayeredPane, java.awt.BorderLayout.CENTER);

        layout_LayeredPane2.add(size_LayeredPane);

        root_LayeredPane.setLayer(layout_LayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout root_LayeredPaneLayout = new javax.swing.GroupLayout(root_LayeredPane);
        root_LayeredPane.setLayout(root_LayeredPaneLayout);
        root_LayeredPaneLayout.setHorizontalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(layout_LayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        root_LayeredPaneLayout.setVerticalGroup(
            root_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(root_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layout_LayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(save_Button, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.save_Button.text")); // NOI18N
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });
        setDefaultButton(save_Button);

        org.openide.awt.Mnemonics.setLocalizedText(cancel_Button, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.cancel_Button.text")); // NOI18N
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        reset_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/jeddict/jpa/modeler/resource/image/misc/reset.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(reset_Button, org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.reset_Button.text")); // NOI18N
        reset_Button.setToolTipText(org.openide.util.NbBundle.getMessage(HighlightWidgetDialog.class, "HighlightWidgetDialog.reset_Button.toolTipText")); // NOI18N
        reset_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_ButtonActionPerformed(evt);
            }
        });

        action_LayeredPane.setLayer(save_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        action_LayeredPane.setLayer(cancel_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        action_LayeredPane.setLayer(reset_Button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout action_LayeredPaneLayout = new javax.swing.GroupLayout(action_LayeredPane);
        action_LayeredPane.setLayout(action_LayeredPaneLayout);
        action_LayeredPaneLayout.setHorizontalGroup(
            action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_LayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reset_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(save_Button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_Button))
        );
        action_LayeredPaneLayout.setVerticalGroup(
            action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(action_LayeredPaneLayout.createSequentialGroup()
                .addGroup(action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(reset_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(action_LayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(save_Button)
                        .addComponent(cancel_Button)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(root_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(root_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(action_LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        modelerScene.getModelerPanelTopComponent().changePersistenceState(false);

        textDesign.setColor(((ColorChooserButton) color_Button).getSelectedColor());
        textDesign.setStyle(((ComboBoxValue<Integer>) style_ComboBox.getSelectedItem()).getValue());
        textDesign.setSize(((Integer) size_Spinner.getValue()).floatValue());

        ((AbstractPModelerScene)modelerScene).reinstallColorScheme(widget);
        saveActionPerformed(evt);
        JeddictLogger.recordAction("Highlight"); 
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void reset_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_ButtonActionPerformed
        setCompValue(
                textDesign.getDefaultStyle(), 
                textDesign.getDefaultSize(), 
                textDesign.getDefaultColor()
        );
    }//GEN-LAST:event_reset_ButtonActionPerformed

    private void loadStyleType() {
        style_ComboBox.addItem(new ComboBoxValue(Font.PLAIN, "Plain"));
        style_ComboBox.addItem(new ComboBoxValue(Font.BOLD, "Bold"));
        style_ComboBox.addItem(new ComboBoxValue(Font.ITALIC, "Italic"));
        style_ComboBox.addItem(new ComboBoxValue(Font.BOLD + Font.ITALIC, "Bold Italic"));
    }

    private void setStyleType(int style) {
        for (int i = 0; i < style_ComboBox.getItemCount(); i++) {
            if (((ComboBoxValue<Integer>) style_ComboBox.getItemAt(i)).getValue() == style) {
                style_ComboBox.setSelectedIndex(i);
            }
        }
    }
    
 
    private void setCompValue(int style, float size, Color color){
        ((ColorChooserButton) color_Button).setSelectedColor(color);
        setStyleType(style);
        size_Spinner.setValue(Float.valueOf(size).intValue());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane action_LayeredPane;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLayeredPane colorComp_LayeredPane;
    private javax.swing.JButton color_Button;
    private javax.swing.JLayeredPane color_LayeredPane;
    private javax.swing.JLabel foregroundColor_Label;
    private javax.swing.JLayeredPane layout_LayeredPane2;
    private javax.swing.JButton reset_Button;
    private javax.swing.JLayeredPane root_LayeredPane;
    private javax.swing.JButton save_Button;
    private javax.swing.JLayeredPane sizeComp_LayeredPane;
    private javax.swing.JLabel size_Label;
    private javax.swing.JLayeredPane size_LayeredPane;
    private javax.swing.JSpinner size_Spinner;
    private javax.swing.JLayeredPane styleComp_LayeredPane;
    private javax.swing.JComboBox<ComboBoxValue> style_ComboBox;
    private javax.swing.JLabel style_Label;
    private javax.swing.JLayeredPane style_LayeredPane;
    // End of variables declaration//GEN-END:variables
}

class ColorChooserButton extends javax.swing.JButton {

    private Color current;

    public ColorChooserButton() {
        addActionListener(arg -> {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", current);
            setSelectedColor(newColor);
        });
    }

    public Color getSelectedColor() {
        return current;
    }

    public void setSelectedColor(Color newColor) {
        setSelectedColor(newColor, true);
    }

    public void setSelectedColor(Color newColor, boolean notify) {
        current = newColor;
        setIcon(createIcon(current, 16, 16));
        repaint();
    }

    public static ImageIcon createIcon(Color main, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(main);
        graphics.fillRect(0, 0, width, height);
        graphics.setXORMode(Color.DARK_GRAY);
        graphics.drawRect(0, 0, width - 1, height - 1);
        image.flush();
        ImageIcon icon = new ImageIcon(image);
        return icon;
    }
}
