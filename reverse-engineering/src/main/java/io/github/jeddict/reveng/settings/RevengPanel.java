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
package io.github.jeddict.reveng.settings;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

public final class RevengPanel extends javax.swing.JPanel {

    RevengPanel() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootLayeredPane = new javax.swing.JLayeredPane();
        includeReferencedClassesCheckbox = new javax.swing.JCheckBox();

        rootLayeredPane.setLayout(new java.awt.GridLayout(9, 1, 0, 2));

        org.openide.awt.Mnemonics.setLocalizedText(includeReferencedClassesCheckbox, org.openide.util.NbBundle.getMessage(RevengPanel.class, "RevengPanel.includeReferencedClassesCheckbox.text")); // NOI18N
        rootLayeredPane.add(includeReferencedClassesCheckbox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rootLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rootLayeredPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        includeReferencedClassesCheckbox.setSelected(isIncludeReferencedClasses());
    }

    void store() {
        PREF.putBoolean("includeReferencedClasses", includeReferencedClassesCheckbox.isSelected());
        includeReferencedClasses = null;
    }

    private static Boolean includeReferencedClasses;

    public static boolean isIncludeReferencedClasses() {
        if (includeReferencedClasses == null) {
            includeReferencedClasses = PREF.getBoolean("includeReferencedClasses", Boolean.TRUE);
        }
        return includeReferencedClasses;
    }

    public boolean valid() {
        return true;
    }

    private static final Preferences PREF = NbPreferences.forModule(RevengPanel.class);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox includeReferencedClassesCheckbox;
    private javax.swing.JLayeredPane rootLayeredPane;
    // End of variables declaration//GEN-END:variables
}
