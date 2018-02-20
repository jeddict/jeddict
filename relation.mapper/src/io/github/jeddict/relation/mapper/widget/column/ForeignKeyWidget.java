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
package io.github.jeddict.relation.mapper.widget.column;

import io.github.jeddict.relation.mapper.widget.api.IReferenceColumnWidget;
import java.awt.Image;
import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.relation.mapper.initializer.RelationMapperUtil;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.FOREIGNKEY;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.FOREIGNKEY_ICON_PATH;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ForeignKeyWidget<E extends DBColumn> extends ColumnWidget<E> implements IReferenceColumnWidget<E>{

    public ForeignKeyWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        
    }
    
    @Override
    public void init() {
        super.init();
        if (((DBColumn) this.getBaseElementSpec()).isPrimaryKey()) {
            this.setImage(RelationMapperUtil.PRIMARYKEY);
        } else {
            this.setImage(RelationMapperUtil.FOREIGNKEY);
        }
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
