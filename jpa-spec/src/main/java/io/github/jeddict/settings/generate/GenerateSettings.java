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
package io.github.jeddict.settings.generate;

import java.util.prefs.Preferences;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.openide.util.NbPreferences;

public final class GenerateSettings extends javax.swing.JPanel {

    GenerateSettings() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootLayeredPane = new javax.swing.JLayeredPane();
        syncExistingSourceCodeComp = new javax.swing.JCheckBox();
        javaseWrapperPanel1 = new javax.swing.JLayeredPane();
        generateFluentAPIComp = new javax.swing.JCheckBox();
        fluentAPIPrefixWrapperPanel = new javax.swing.JLayeredPane();
        fluentAPIPrefix_Label = new javax.swing.JLabel();
        fluentAPIPrefixComp = new javax.swing.JTextField();
        enableIntrospectionComp = new javax.swing.JCheckBox();
        generateDefaultValueComp = new javax.swing.JCheckBox();
        javaDocPanel = new javax.swing.JLayeredPane();
        javaDoc_Label = new javax.swing.JLabel();
        javadocSetting_LayeredPane = new javax.swing.JLayeredPane();
        propertyJavaDocComp = new javax.swing.JCheckBox();
        setterJavaDocComp = new javax.swing.JCheckBox();
        getterJavaDocComp = new javax.swing.JCheckBox();
        fluentAPIJavaDocComp = new javax.swing.JCheckBox();

        rootLayeredPane.setLayout(new java.awt.GridLayout(9, 1, 0, 2));

        org.openide.awt.Mnemonics.setLocalizedText(syncExistingSourceCodeComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.syncExistingSourceCodeComp.text")); // NOI18N
        syncExistingSourceCodeComp.setToolTipText(org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.syncExistingSourceCodeComp.toolTipText")); // NOI18N
        rootLayeredPane.add(syncExistingSourceCodeComp);

        javaseWrapperPanel1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(generateFluentAPIComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.generateFluentAPIComp.text")); // NOI18N
        javaseWrapperPanel1.add(generateFluentAPIComp, java.awt.BorderLayout.WEST);

        fluentAPIPrefixWrapperPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.openide.awt.Mnemonics.setLocalizedText(fluentAPIPrefix_Label, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.fluentAPIPrefix_Label.text")); // NOI18N
        fluentAPIPrefixWrapperPanel.add(fluentAPIPrefix_Label);

        fluentAPIPrefixComp.setText(org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.fluentAPIPrefixComp.text")); // NOI18N
        fluentAPIPrefixComp.setPreferredSize(new java.awt.Dimension(120, 20));
        fluentAPIPrefixWrapperPanel.add(fluentAPIPrefixComp);

        javaseWrapperPanel1.add(fluentAPIPrefixWrapperPanel, java.awt.BorderLayout.CENTER);

        rootLayeredPane.add(javaseWrapperPanel1);

        org.openide.awt.Mnemonics.setLocalizedText(enableIntrospectionComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.enableIntrospectionComp.text")); // NOI18N
        enableIntrospectionComp.setToolTipText(org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.enableIntrospectionComp.toolTipText")); // NOI18N
        rootLayeredPane.add(enableIntrospectionComp);

        org.openide.awt.Mnemonics.setLocalizedText(generateDefaultValueComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.generateDefaultValueComp.text")); // NOI18N
        rootLayeredPane.add(generateDefaultValueComp);

        javaDocPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(javaDoc_Label, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.javaDoc_Label.text")); // NOI18N
        javaDocPanel.add(javaDoc_Label, java.awt.BorderLayout.WEST);

        javadocSetting_LayeredPane.setPreferredSize(new java.awt.Dimension(253, 17));
        javadocSetting_LayeredPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 18, 5));

        propertyJavaDocComp.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(propertyJavaDocComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.propertyJavaDocComp.text")); // NOI18N
        javadocSetting_LayeredPane.add(propertyJavaDocComp);

        org.openide.awt.Mnemonics.setLocalizedText(setterJavaDocComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.setterJavaDocComp.text")); // NOI18N
        javadocSetting_LayeredPane.add(setterJavaDocComp);

        org.openide.awt.Mnemonics.setLocalizedText(getterJavaDocComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.getterJavaDocComp.text")); // NOI18N
        javadocSetting_LayeredPane.add(getterJavaDocComp);

        org.openide.awt.Mnemonics.setLocalizedText(fluentAPIJavaDocComp, org.openide.util.NbBundle.getMessage(GenerateSettings.class, "GenerateSettings.fluentAPIJavaDocComp.text")); // NOI18N
        javadocSetting_LayeredPane.add(fluentAPIJavaDocComp);

        javaDocPanel.add(javadocSetting_LayeredPane, java.awt.BorderLayout.CENTER);

        rootLayeredPane.add(javaDocPanel);

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
            .addComponent(rootLayeredPane)
        );
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        syncExistingSourceCodeComp.setSelected(isSyncExistingSourceCode());
        propertyJavaDocComp.setSelected(isPropertyJavaDoc());
        getterJavaDocComp.setSelected(isGetterJavaDoc());
        setterJavaDocComp.setSelected(isSetterJavaDoc());
        fluentAPIJavaDocComp.setSelected(isFluentAPIJavaDoc());
        generateFluentAPIComp.setSelected(isGenerateFluentAPI());
        fluentAPIPrefixComp.setText(getFluentAPIPrefix());
        generateDefaultValueComp.setSelected(isGenerateDefaultValue());
        enableIntrospectionComp.setSelected(isIntrospectionEnabled());
    }

    void store() {
        pref.putBoolean("syncExistingSourceCode", syncExistingSourceCodeComp.isSelected());
        pref.putBoolean("propertyJavaDoc", propertyJavaDocComp.isSelected());
        pref.putBoolean("getterJavaDoc", getterJavaDocComp.isSelected());
        pref.putBoolean("setterJavaDoc", setterJavaDocComp.isSelected());
        pref.putBoolean("fluentAPIJavaDoc", fluentAPIJavaDocComp.isSelected());
        pref.putBoolean("generateFluentAPI", generateFluentAPIComp.isSelected());
        pref.put("fluentAPIPrefix", fluentAPIPrefixComp.getText());
        pref.putBoolean("generateDefaultValue", generateDefaultValueComp.isSelected());
        pref.putBoolean("enableIntrospection", enableIntrospectionComp.isSelected());

        syncExistingSourceCode = null;
        propertyJavaDoc = null;
        getterJavaDoc = null;
        setterJavaDoc = null;
        fluentAPIJavaDoc = null;
        generateFluentAPI = null;
        fluentAPIPrefix = null;
        generateDefaultValue = null;
        enableIntrospection = null;
    }

    private static Boolean syncExistingSourceCode;
    private static Boolean propertyJavaDoc;
    private static Boolean getterJavaDoc;
    private static Boolean setterJavaDoc;
    private static Boolean fluentAPIJavaDoc;
    private static Boolean generateFluentAPI;
    private static String fluentAPIPrefix;
    private static Boolean generateDefaultValue;
    private static Boolean enableIntrospection;

    public static boolean isSyncExistingSourceCode() {
        if (syncExistingSourceCode == null) {
            syncExistingSourceCode = pref.getBoolean("syncExistingSourceCode", Boolean.TRUE);
        }
        return syncExistingSourceCode;
    }

    public static boolean isPropertyJavaDoc() {
        if (propertyJavaDoc == null) {
            propertyJavaDoc = pref.getBoolean("propertyJavaDoc", Boolean.TRUE);
        }
        return propertyJavaDoc;
    }

    public static boolean isGetterJavaDoc() {
        if (getterJavaDoc == null) {
            getterJavaDoc = pref.getBoolean("getterJavaDoc", Boolean.FALSE);
        }
        return getterJavaDoc;
    }

    public static boolean isSetterJavaDoc() {
        if (setterJavaDoc == null) {
            setterJavaDoc = pref.getBoolean("setterJavaDoc", Boolean.FALSE);
        }
        return setterJavaDoc;
    }
    
    public static boolean isFluentAPIJavaDoc() {
        if (fluentAPIJavaDoc == null) {
            fluentAPIJavaDoc = pref.getBoolean("fluentAPIJavaDoc", Boolean.FALSE);
        }
        return fluentAPIJavaDoc;
    }

    public static void setFluentAPIEnabled(boolean status) {
        generateFluentAPI = status;
        pref.putBoolean("generateFluentAPI", status);
    }

    public static boolean isGenerateFluentAPI() {
        if (generateFluentAPI == null) {
            generateFluentAPI = pref.getBoolean("generateFluentAPI", Boolean.FALSE);
        }
        return generateFluentAPI;
    }

    public static String getFluentAPIPrefix() {
        if (fluentAPIPrefix == null) {
            fluentAPIPrefix = pref.get("fluentAPIPrefix", EMPTY);
        }
        return fluentAPIPrefix;
    }

    public static boolean isGenerateDefaultValue() {
        if (generateDefaultValue == null) {
            generateDefaultValue = pref.getBoolean("generateDefaultValue", Boolean.FALSE);
        }
        return generateDefaultValue;
    }

    public static boolean isIntrospectionEnabled() {
        if (enableIntrospection == null) {
            enableIntrospection = pref.getBoolean("enableIntrospection", Boolean.FALSE);
        }
        return enableIntrospection;
    }

    public static void setIntrospectionEnabled(boolean status) {
        enableIntrospection = status;
        pref.putBoolean("enableIntrospection", status);
    }

    public static String getIntrospectionPrefix(boolean booleanTypeAttribute) {
        if (booleanTypeAttribute) {
            return isIntrospectionEnabled() ? "get" : "is";
        } else {
            return "get";
        }
    }


    public boolean valid() {
        return true;
    }

    private static final Preferences pref = NbPreferences.forModule(GenerateSettings.class);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox enableIntrospectionComp;
    private javax.swing.JCheckBox fluentAPIJavaDocComp;
    private javax.swing.JTextField fluentAPIPrefixComp;
    private javax.swing.JLayeredPane fluentAPIPrefixWrapperPanel;
    private javax.swing.JLabel fluentAPIPrefix_Label;
    private javax.swing.JCheckBox generateDefaultValueComp;
    private javax.swing.JCheckBox generateFluentAPIComp;
    private javax.swing.JCheckBox getterJavaDocComp;
    private javax.swing.JLayeredPane javaDocPanel;
    private javax.swing.JLabel javaDoc_Label;
    private javax.swing.JLayeredPane javadocSetting_LayeredPane;
    private javax.swing.JLayeredPane javaseWrapperPanel1;
    private javax.swing.JCheckBox propertyJavaDocComp;
    private javax.swing.JLayeredPane rootLayeredPane;
    private javax.swing.JCheckBox setterJavaDocComp;
    private javax.swing.JCheckBox syncExistingSourceCodeComp;
    // End of variables declaration//GEN-END:variables
}
