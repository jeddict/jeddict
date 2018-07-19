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
package io.github.jeddict.jcode;

import io.github.jeddict.jcode.LayerConfigData;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.lang.reflect.ParameterizedType;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class LayerConfigPanel<T extends LayerConfigData> extends Panel implements ChangeListener {

    private T configData;
    protected ChangeSupport changeSupport = new ChangeSupport(this);

    public abstract void init(String _package, Project project, SourceGroup sourceGroup);

    public LayerConfigPanel() {
        changeSupport.addChangeListener(this);
    }
    
    public Class<T> getConfigDataClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * @return the configData
     */
    public T getConfigData() {
        return configData;
    }

    /**
     * @param configData the configData to set
     */
    public void setConfigData(T configData) {
        this.configData = configData;
    }

    public abstract boolean hasError();

    public abstract void read();

    public abstract void store();

    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            changeSupport.fireChange();
        }
    };
    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changeSupport.fireChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changeSupport.fireChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changeSupport.fireChange();
        }
    };

    protected void addChangeListener(JComponent component) {
        if (component instanceof JComboBox) {
            ((JComboBox) component).getEditor().getEditorComponent().addKeyListener(keyAdapter);
            ((JTextComponent) ((JComboBox) component).getEditor().getEditorComponent()).getDocument().addDocumentListener(documentListener);
        } else if (component instanceof JTextComponent) {
            ((JTextComponent) component).addKeyListener(keyAdapter);
            ((JTextComponent) component).getDocument().addDocumentListener(documentListener);
        }
    }

    protected void fire() {
        changeSupport.fireChange();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        hasError();
    }
    
}
