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
package io.github.jeddict.reveng.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        location = "Jeddict",
        displayName = "#DISPLAYNAME_REVENG",
        keywords = "#KEYWORDS_REVENG",
        keywordsCategory = "Jeddict/Reveng"
)
@org.openide.util.NbBundle.Messages({"DISPLAYNAME_REVENG=Reverse Engineering", "KEYWORDS_REVENG=Jeddict Reverse Engineering"})
public final class RevengOptionsPanelController extends OptionsPanelController {

    private RevengPanel panel;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().store();
            changed = false;
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }

    private RevengPanel getPanel() {
        if (panel == null) {
            panel = new RevengPanel();
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            changeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        changeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
