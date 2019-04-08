/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.widget;

import java.awt.Image;
import io.github.jeddict.jsonb.modeler.spec.JSONBBranchNode;
import io.github.jeddict.jsonb.modeler.initializer.JSONBModelerScene;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ARRAY_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ARRAY_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.OBJECT_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.OBJECT_ICON_PATH;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.MultiRelationAttribute;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class BranchNodeWidget extends JSONNodeWidget<JSONBBranchNode> {

    public BranchNodeWidget(JSONBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    @Override
    public String getIconPath() {
        Attribute attr = this.getBaseElementSpec().getAttribute();
        if (attr instanceof MultiRelationAttribute || attr instanceof ElementCollection) {
            return ARRAY_ICON_PATH;
        }
        return OBJECT_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        Attribute attr = this.getBaseElementSpec().getAttribute();
        if (attr instanceof MultiRelationAttribute || attr instanceof ElementCollection) {
            return ARRAY_ICON;
        }
        return OBJECT_ICON;
    }
}
