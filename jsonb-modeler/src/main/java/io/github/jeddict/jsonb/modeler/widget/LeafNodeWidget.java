/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import io.github.jeddict.jcode.util.AttributeType;
import io.github.jeddict.jsonb.modeler.spec.JSONBLeafNode;
import io.github.jeddict.jsonb.modeler.initializer.JSONBModelerScene;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ARRAY_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ARRAY_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.BOOLEAN_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.BOOLEAN_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.DATE_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.DATE_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ENUM_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.ENUM_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.NUMBER_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.NUMBER_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.TEXT_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.TEXT_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.TIME_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.TIME_ICON_PATH;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.UNKNOWN_ICON;
import static io.github.jeddict.jsonb.modeler.initializer.JSONBModelerUtil.UNKNOWN_ICON_PATH;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.EnumTypeHandler;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class LeafNodeWidget extends JSONNodeWidget<JSONBLeafNode> {

    public LeafNodeWidget(JSONBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
        
    @Override
    public String getIconPath() {
        Attribute attr = this.getBaseElementSpec().getAttribute();
        if(attr instanceof ElementCollection || AttributeType.isArray(attr.getDataTypeLabel())){
            return ARRAY_ICON_PATH;
        } else if(AttributeType.isText(attr.getDataTypeLabel())){
            return TEXT_ICON_PATH;
        } else if(AttributeType.isNumber(attr.getDataTypeLabel())){
            return NUMBER_ICON_PATH;
        } else if(AttributeType.isBoolean(attr.getDataTypeLabel())){
            return BOOLEAN_ICON_PATH;
        } else if(AttributeType.isDate(attr.getDataTypeLabel()) || AttributeType.isDateTime(attr.getDataTypeLabel())){
            return DATE_ICON_PATH;
        } else if(AttributeType.isTime(attr.getDataTypeLabel())){
            return TIME_ICON_PATH;
        }
        
        if(attr instanceof EnumTypeHandler && ((EnumTypeHandler)attr).getEnumerated() != null){
            return ENUM_ICON_PATH;
        }
        return UNKNOWN_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        Attribute attr = this.getBaseElementSpec().getAttribute();
        if(attr instanceof ElementCollection || AttributeType.isArray(attr.getDataTypeLabel())){
            return ARRAY_ICON;
        } else if(AttributeType.isText(attr.getDataTypeLabel())){
            return TEXT_ICON;
        } else if(AttributeType.isNumber(attr.getDataTypeLabel())){
            return NUMBER_ICON;
        } else if(AttributeType.isBoolean(attr.getDataTypeLabel())){
            return BOOLEAN_ICON;
        } else if(AttributeType.isDate(attr.getDataTypeLabel()) || AttributeType.isDateTime(attr.getDataTypeLabel())){
            return DATE_ICON;
        } else if(AttributeType.isTime(attr.getDataTypeLabel())){
            return TIME_ICON;
        }
        
        if(attr instanceof EnumTypeHandler && ((EnumTypeHandler)attr).getEnumerated() != null){
            return ENUM_ICON;
        }
        return UNKNOWN_ICON;
    }
}
