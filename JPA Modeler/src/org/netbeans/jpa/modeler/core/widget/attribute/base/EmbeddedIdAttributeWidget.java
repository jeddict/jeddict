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
package org.netbeans.jpa.modeler.core.widget.attribute.base;

import java.awt.Image;
import java.util.List;
import javax.swing.JMenuItem;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;import org.netbeans.modeler.specification.model.document.IModelerScene;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.EMBEDDED_ID_ATTRIBUTE_ICON_PATH;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.EMBEDDED_ID_ATTRIBUTE_ICON;

/**
 *
 * @author Gaurav Gupta
 */
public class EmbeddedIdAttributeWidget extends BaseAttributeWidget<EmbeddedId> {

    public EmbeddedIdAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(getIcon());
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();// Override(from AttributeWidget) to remove Delete Menu from Popup
        menuList.remove(0);//remove Delete PopupMenu
        return menuList;
    }

    @Override
    public String getIconPath() {
        return EMBEDDED_ID_ATTRIBUTE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return EMBEDDED_ID_ATTRIBUTE_ICON;
    }

}
