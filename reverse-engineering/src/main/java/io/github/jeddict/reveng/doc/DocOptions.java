/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.reveng.doc;

import org.netbeans.modeler.properties.window.GenericDialog;
import org.openide.windows.WindowManager;

/**
 *
 * @author jGauravGupta
 */
public class DocOptions extends GenericDialog {

    private final String docName;
    private final String modelerFileName;

    public DocOptions(String docName, String modelerFileName) {
        super(WindowManager.getDefault().getMainWindow(), "Import File", true);
        this.docName = docName;
        this.modelerFileName = modelerFileName;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        contentLabel = new javax.swing.JLabel();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jpaCheckBox = new javax.swing.JCheckBox();
        jsonbCheckBox = new javax.swing.JCheckBox();
        jaxbCheckBox = new javax.swing.JCheckBox();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLayeredPane1.setLayout(new java.awt.GridLayout(3, 1));

        org.openide.awt.Mnemonics.setLocalizedText(contentLabel, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.contentLabel.text", docName, modelerFileName));

        jLayeredPane2.setLayer(contentLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
        );

        jLayeredPane1.add(jLayeredPane2);

        jLayeredPane3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 50, 15));

        jpaCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jpaCheckBox, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.jpaCheckBox.text")); // NOI18N
        jLayeredPane3.add(jpaCheckBox);

        org.openide.awt.Mnemonics.setLocalizedText(jsonbCheckBox, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.jsonbCheckBox.text")); // NOI18N
        jLayeredPane3.add(jsonbCheckBox);

        org.openide.awt.Mnemonics.setLocalizedText(jaxbCheckBox, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.jaxbCheckBox.text")); // NOI18N
        jLayeredPane3.add(jaxbCheckBox);

        jLayeredPane1.add(jLayeredPane3);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(DocOptions.class, "DocOptions.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLayeredPane4.setLayer(okButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane4.setLayer(cancelButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane4Layout.createSequentialGroup()
                .addContainerGap(367, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        jLayeredPane1.add(jLayeredPane4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        saveActionPerformed(evt);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancelButtonActionPerformed

    public boolean isJPASupport() {
        return jpaCheckBox.isSelected();
    }

    public boolean isJAXBSupport() {
        return jaxbCheckBox.isSelected();
    }

    public boolean isJSONBSupport() {
        return jsonbCheckBox.isSelected();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel contentLabel;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JCheckBox jaxbCheckBox;
    private javax.swing.JCheckBox jpaCheckBox;
    private javax.swing.JCheckBox jsonbCheckBox;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
