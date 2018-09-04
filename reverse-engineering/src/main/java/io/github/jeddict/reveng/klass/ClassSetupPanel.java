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
package io.github.jeddict.reveng.klass;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

final class ClassSetupPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel, ChangeListener {

    private WizardDescriptor wizardDescriptor;
    private ClassSetupPanelVisual component;
    private Project project;

    /**
     * Create the wizard panel descriptor.
     */
    public ClassSetupPanel(Project project, WizardDescriptor wizardDescriptor) {
        this.project = project;
        this.wizardDescriptor = wizardDescriptor;
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new ClassSetupPanelVisual(project, wizardDescriptor);
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("org.netbeans.modules.j2ee.persistence.wizard.jpacontroller." + ClassSetupPanel.class.getSimpleName()); // NOI18N
    }

    @Override
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }

    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);

        // XXX hack, TemplateWizard in final setTemplateImpl() forces new wizard's title
        // this name is used in NewProjectWizard to modify the title
        Object substitute = ((JComponent) component).getClientProperty("NewProjectWizard_Title"); // NOI18N
        if (substitute != null) {
            wizardDescriptor.putProperty("NewProjectWizard_Title", substitute); // NOI18N
        }
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor wizardDescriptor = (WizardDescriptor) settings;
        component.store(wizardDescriptor);
         wizardDescriptor.putProperty("NewProjectWizard_Title", null); // NOI18N
    }

    protected final void fireChangeEvent(ChangeEvent ev) {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChangeEvent(e);
    }
}
