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
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;

/**
 *
 * @author Gaurav Gupta
 */
public class ProjectCellRenderer extends JLabel
        implements ListCellRenderer {

    ListCellRenderer renderer;

    public ProjectCellRenderer(ListCellRenderer hostRenderer) {
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

        if (value instanceof Project) {
            ProjectInformation pi = ProjectUtils.getInformation((Project) value);

            label.setText(pi.getDisplayName());
            label.setIcon(pi.getIcon());
        } else {
            label.setText(value == null ? " " : value.toString()); // NOI18N
            label.setIcon(null);
        }
        return label;
    }
}
