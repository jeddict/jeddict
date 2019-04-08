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
package io.github.jeddict.jpa.modeler.widget.attribute.base;

import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ID_ATTRIBUTE_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ID_ATTRIBUTE_ICON_PATH;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getGeneratorProperty;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.spec.Id;
import java.awt.Image;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public class IdAttributeWidget extends BaseAttributeWidget<Id> {

    public IdAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(getIcon());
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        set.put("JPA_PROP", getGeneratorProperty(this));
    }

    @Override
    public void validateName(String previousName, String name) {
        super.validateName(previousName, name);
        if (this.getClassWidget() instanceof EntityWidget) {
            ((EntityWidget) this.getClassWidget()).scanKeyError();
        }
    }

    @Override
    public String getIconPath() {
        return ID_ATTRIBUTE_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return ID_ATTRIBUTE_ICON;
    }

}
