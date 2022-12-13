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
package io.github.jeddict.generator.repository;

import io.github.jeddict.jcode.LayerConfigPanel;
import java.util.prefs.Preferences;
import javax.lang.model.SourceVersion;
import javax.swing.ComboBoxModel;
import javax.swing.text.JTextComponent;
import io.github.jeddict.util.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import static io.github.jeddict.jcode.util.JavaIdentifiers.isValidPackageName;
import io.github.jeddict.jcode.util.PreferenceUtils;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class SampleRepositoryConfigPanel extends LayerConfigPanel<SampleRepositoryConfigData> {

    private static final String DEFAULT_PACKAGE = "service.facade";
    private Preferences pref;

    public SampleRepositoryConfigPanel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        if (!isValidPackageName(getPackage())) {
            warningLabel.setText(NbBundle.getMessage(SampleRepositoryConfigPanel.class, "XTechnologyConfigPanel.invalidPackage.message"));
            return true;
        }
        String prefix = getPrefix();
        String suffix = getSuffix();
        
        if (StringUtils.isNotBlank(prefix) && !SourceVersion.isName(prefix)) {
            warningLabel.setText(NbBundle.getMessage(SampleRepositoryConfigPanel.class, "XTechnologyConfigPanel.invalidPrefix.message"));
            return true;
        }
        if (StringUtils.isNotBlank(suffix) && !SourceVersion.isName(prefix +'_'+ suffix)) {
            warningLabel.setText(NbBundle.getMessage(SampleRepositoryConfigPanel.class, "XTechnologyConfigPanel.invalidSuffix.message"));
            return true;
        }
        return false;
    }
    
        
    
    
    @Override
    public void read(){
        this.setConfigData(PreferenceUtils.get(pref, SampleRepositoryConfigData.class));
        SampleRepositoryConfigData data = this.getConfigData();
        if(StringUtils.isNotBlank(data.getPackage())){
            setPackage(data.getPackage());
        }
        
        if(StringUtils.isNotBlank(data.getPrefixName())){
            setPrefix(data.getPrefixName());
        }
        
        if(StringUtils.isNotBlank(data.getSuffixName())){
            setSuffix(data.getSuffixName());
        }
        
    }
    @Override
    public void store(){
        this.getConfigData().setPrefixName(getPrefix());
        this.getConfigData().setSuffixName(getSuffix());
        this.getConfigData().setPackage(getPackage());
        PreferenceUtils.set(pref, this.getConfigData());
    }
    
    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        pref = ProjectUtils.getPreferences(project, SampleRepositoryConfigData.class, true);
        if (sourceGroup != null) {
            packageCombo.setRenderer(PackageView.listRenderer());
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSize() > 0) {
                model.setSelectedItem(model.getElementAt(0));
            }
            packageCombo.setModel(model);
            addChangeListener(packageCombo);
            if (StringUtils.isBlank(_package)) {
                setPackage(DEFAULT_PACKAGE);
            } else {
                setPackage(_package + '.' + DEFAULT_PACKAGE);
            }
        }
        addChangeListener(prefixField);
        addChangeListener(suffixField);
    }

    public String getPackage() {
        return ((JTextComponent) packageCombo.getEditor().getEditorComponent()).getText().trim();
    }

    private void setPackage(String _package) {
        ComboBoxModel model = packageCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(_package)) {
                model.setSelectedItem(model.getElementAt(i));
                return;
            }
        }
        ((JTextComponent) packageCombo.getEditor().getEditorComponent()).setText(_package);
    }
    
    public String getSuffix() {
        return suffixField.getText().trim();
    }
    
    public String getPrefix() {
        return prefixField.getText().trim();
    }
    
      private void setPrefix(String prefix) {
        prefixField.setText(prefix);
    }
    private void setSuffix(String suffix) {
        suffixField.setText(suffix);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        suffixPanel = new javax.swing.JPanel();
        namePane = new javax.swing.JLayeredPane();
        prefixField = new javax.swing.JTextField();
        entityLabel = new javax.swing.JLabel();
        suffixField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        packagePanel = new javax.swing.JPanel();
        packageLabel = new javax.swing.JLabel();
        packageCombo = new javax.swing.JComboBox();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(2, 0, 0, 15));

        suffixPanel.setLayout(new java.awt.BorderLayout(10, 0));

        namePane.setLayout(new javax.swing.BoxLayout(namePane, javax.swing.BoxLayout.LINE_AXIS));

        prefixField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        prefixField.setText(org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.prefixField.text")); // NOI18N
        prefixField.setToolTipText(org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.prefixField.toolTipText")); // NOI18N
        prefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                prefixFieldPropertyChange(evt);
            }
        });
        namePane.add(prefixField);

        entityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        entityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(entityLabel, org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.entityLabel.text")); // NOI18N
        entityLabel.setPreferredSize(new java.awt.Dimension(58, 27));
        entityLabel.setRequestFocusEnabled(false);
        namePane.add(entityLabel);

        suffixField.setText(org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.suffixField.text")); // NOI18N
        suffixField.setToolTipText(org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.suffixField.toolTipText")); // NOI18N
        suffixField.setPreferredSize(new java.awt.Dimension(100, 27));
        suffixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                suffixFieldPropertyChange(evt);
            }
        });
        namePane.add(suffixField);

        suffixPanel.add(namePane, java.awt.BorderLayout.CENTER);

        nameLabel.setLabelFor(suffixField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.nameLabel.text")); // NOI18N
        nameLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        suffixPanel.add(nameLabel, java.awt.BorderLayout.WEST);

        jPanel1.add(suffixPanel);

        packagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        packageLabel.setLabelFor(packageCombo);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(SampleRepositoryConfigPanel.class, "SampleRepositoryConfigPanel.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        packagePanel.add(packageLabel, java.awt.BorderLayout.LINE_START);

        packageCombo.setEditable(true);
        packageCombo.setEditable(true);
        packageCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        packageCombo.setMinimumSize(new java.awt.Dimension(60, 27));
        packageCombo.setName(""); // NOI18N
        packageCombo.setPreferredSize(new java.awt.Dimension(60, 27));
        packageCombo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                packageComboPropertyChange(evt);
            }
        });
        packagePanel.add(packageCombo, java.awt.BorderLayout.CENTER);

        jPanel1.add(packagePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 615, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 132, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(101, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(61, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void suffixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_suffixFieldPropertyChange
     fire();
    }//GEN-LAST:event_suffixFieldPropertyChange

    private void packageComboPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_packageComboPropertyChange
        fire();
    }//GEN-LAST:event_packageComboPropertyChange

    private void prefixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_prefixFieldPropertyChange
        fire();
    }//GEN-LAST:event_prefixFieldPropertyChange
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel entityLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLayeredPane namePane;
    private javax.swing.JComboBox packageCombo;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JPanel packagePanel;
    private javax.swing.JTextField prefixField;
    private javax.swing.JTextField suffixField;
    private javax.swing.JPanel suffixPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}
