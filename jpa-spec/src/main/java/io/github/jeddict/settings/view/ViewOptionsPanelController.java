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
package io.github.jeddict.settings.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        location = "Jeddict",
        displayName = "#DISPLAYNAME_VIEW",
        keywords = "#KEYWORDS_VIEW",
        keywordsCategory = "Jeddict/View"
)
@org.openide.util.NbBundle.Messages({"DISPLAYNAME_VIEW=View", "KEYWORDS_VIEW=Jeddict View"})
public final class ViewOptionsPanelController extends OptionsPanelController {

    private ViewPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
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
        return null; 
    }

        @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

        @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

        @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private ViewPanel getPanel() {
        if (panel == null) {
            panel = new ViewPanel();
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
