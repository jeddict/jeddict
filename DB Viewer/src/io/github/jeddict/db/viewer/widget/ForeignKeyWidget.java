/**
 * Copyright [2018] Gaurav Gupta
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
package io.github.jeddict.db.viewer.widget;

import io.github.jeddict.db.viewer.spec.DBForeignKey;
import io.github.jeddict.db.viewer.initializer.DBViewerScene;
import java.awt.Image;
import static org.netbeans.db.modeler.initializer.DBModelerUtil.FOREIGNKEY;
import static org.netbeans.db.modeler.initializer.DBModelerUtil.FOREIGNKEY_ICON_PATH;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class ForeignKeyWidget extends ColumnWidget<DBForeignKey> {

    public ForeignKeyWidget(DBViewerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        
    }
        
    @Override
    public String getIconPath() {
        return FOREIGNKEY_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return FOREIGNKEY;
    }

}
