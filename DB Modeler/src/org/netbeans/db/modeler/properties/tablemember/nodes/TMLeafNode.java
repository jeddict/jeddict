/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.properties.tablemember.nodes;

import java.util.List;
import org.netbeans.db.modeler.core.widget.column.ColumnWidget;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.LeafNode;
import org.netbeans.jpa.modeler.navigator.nodes.LeafNodeAction;
import org.netbeans.db.modeler.properties.tablemember.TableMembers;
import org.openide.nodes.Children;

public class TMLeafNode extends LeafNode<TableMembers> {

    private final ColumnWidget leafColumnWidget;

    public TMLeafNode(ColumnWidget leafAttributeWidget, TableMembers tableMembers, Children children, CheckableAttributeNode checkableNode, List<Class<? extends LeafNodeAction>> actions) {
        super(leafAttributeWidget.getModelerScene(), tableMembers, children, checkableNode, actions);
        this.leafColumnWidget = leafAttributeWidget;
    }

    @Override
    public void init() {
        getCheckableNode().setEnableWithParent(true);
        this.setIconBaseWithExtension(leafColumnWidget.getIconPath());
        DBColumn column =  (DBColumn)leafColumnWidget.getBaseElementSpec();
        this.setShortDescription(column.getName() + " <" + column.getDataType()+ ">");
    }

    private String htmlDisplayName;
    @Override
    public String getHtmlDisplayName() {
        if (htmlDisplayName == null) {
            DBColumn column =  (DBColumn)leafColumnWidget.getBaseElementSpec();
            htmlDisplayName = column.getName() + " : " + column.getDataType();
        }
        System.out.println("htmlDisplayName : " + htmlDisplayName);
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
            return String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            return String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
    }

    /**
     * @return the leafAttributeWidget
     */
    public ColumnWidget getLeafColumnWidget() {
        return leafColumnWidget;
    }
    
}
