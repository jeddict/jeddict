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
package io.github.jeddict.jpa.modeler.navigator.overrideview;

import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.spec.Entity;
import org.netbeans.modeler.node.ModelerNavigationNode;
import org.openide.nodes.Children;

public class OverrideAttributeRootNode extends ModelerNavigationNode {

    public OverrideAttributeRootNode(EntityWidget entityWidget) {
        super(Children.create(new OverrideAttributeChildFactory(entityWidget), true));
        Entity entity = entityWidget.getBaseElementSpec();
        setDisplayName(entity.getClazz());
        setShortDescription(entity.getClazz());
        setIconBaseWithExtension(entityWidget.getIconPath());
    }
}
