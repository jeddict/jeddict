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
package io.github.jeddict.db.modeler.widget;

import io.github.jeddict.db.modeler.initializer.DBModelerScene;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.PRIMARYKEY;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.PRIMARYKEY_ICON_PATH;
import java.awt.Image;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class PrimaryKeyWidget extends BasicColumnWidget {

    public PrimaryKeyWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
    @Override
    public String getIconPath() {
        return PRIMARYKEY_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return PRIMARYKEY;
    }



}
