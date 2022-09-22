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
package io.github.jeddict.jpa.modeler.widget.attribute.base;

import java.awt.Image;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.TRANSIENT_ATTRIBUTE_ICON_PATH;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.TRANSIENT_ATTRIBUTE_ICON;

/**
 *
 * @author Gaurav Gupta
 */
public class TransientAttributeWidget extends BaseAttributeWidget<Transient> {

    public TransientAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(getIcon());
    }
    
    @Override
    public String getIconPath() {
        return TRANSIENT_ATTRIBUTE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return TRANSIENT_ATTRIBUTE_ICON;
    }

}
