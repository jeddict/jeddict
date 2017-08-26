/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jeddict.jsonb.modeler.core.widget;

import java.awt.Image;
import org.netbeans.jcode.core.util.AttributeType;
import org.netbeans.jeddict.jsonb.modeler.spec.JSONBLeafNode;
import org.netbeans.jeddict.jsonb.modeler.specification.model.scene.JSONBModelerScene;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.ARRAY_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.ARRAY_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.BOOLEAN_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.BOOLEAN_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.DATE_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.DATE_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.ENUM_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.ENUM_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.NUMBER_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.NUMBER_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.TEXT_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.TEXT_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.TIME_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.TIME_ICON_PATH;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.UNKNOWN_ICON;
import static org.netbeans.jeddict.jsonb.modeler.specification.model.util.JSONBModelerUtil.UNKNOWN_ICON_PATH;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.EnumTypeHandler;
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
