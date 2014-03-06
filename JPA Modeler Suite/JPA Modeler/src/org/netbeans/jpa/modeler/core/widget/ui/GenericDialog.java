/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import org.netbeans.modeler.locale.I18n;

/**
 *
 * @author Gaurav Gupta
 */
public class GenericDialog extends javax.swing.JDialog {

    public GenericDialog() {
        super(new javax.swing.JFrame(), true);

        javax.swing.KeyStroke escape = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, false);
        javax.swing.Action escapeAction = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cancelActionPerformed(e);
            }
        };

        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, I18n.getString("Global.Pane.Escape"));
        getRootPane().getActionMap().put(I18n.getString("Global.Pane.Escape"), escapeAction);

        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - this.getWidth()) / 2;
            int y = (screenSize.height - this.getHeight()) / 2;
            this.setLocation(x, y);
        }
        super.setVisible(visible);
    }

    protected void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        this.setDialogResult(javax.swing.JOptionPane.CLOSED_OPTION);
        dispose();
    }

    protected void cancelActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
        this.setDialogResult(javax.swing.JOptionPane.CANCEL_OPTION);
        dispose();
    }

    protected void saveActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
        this.setDialogResult(javax.swing.JOptionPane.OK_OPTION);
        dispose();
    }
    private int dialogResult;

    /**
     * @return the dialogResult
     */
    public int getDialogResult() {
        return dialogResult;
    }

    /**
     * @param dialogResult the dialogResult to set
     */
    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

}
