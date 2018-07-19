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
package io.github.jeddict.jcode.ui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class SourceRootCellRenderer extends JLabel
        implements ListCellRenderer {

    ListCellRenderer renderer;

    public SourceRootCellRenderer(ListCellRenderer hostRenderer) {
        renderer = hostRenderer;
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        Component comp = null;

        if (renderer != null) {
            comp = renderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
        }

        JLabel label = null;

        if (comp instanceof JLabel) {
            label = (JLabel) comp;
        } else {
            label = this;
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setOpaque(true);
        }

        if (value instanceof SourceGroup) {
            SourceGroup sg = (SourceGroup) value;
            String desc = sg.getDisplayName();
            if (desc == null || desc.length() == 0) {
                FileObject fo = sg.getRootFolder();
                desc = fo.getPath();
            }
            label.setText(desc);
        } else {
            label.setText(value == null ? " " : value.toString()); // NOI18N
        }
        return label;
    }
}
