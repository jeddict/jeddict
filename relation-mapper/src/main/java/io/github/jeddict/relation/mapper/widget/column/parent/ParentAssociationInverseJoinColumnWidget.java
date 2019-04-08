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
package io.github.jeddict.relation.mapper.widget.column.parent;

import io.github.jeddict.relation.mapper.widget.column.InverseJoinColumnWidget;
import io.github.jeddict.relation.mapper.spec.DBParentAssociationInverseJoinColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class ParentAssociationInverseJoinColumnWidget extends ParentAssociationColumnWidget<DBParentAssociationInverseJoinColumn> {

    public ParentAssociationInverseJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    @Override
    protected String evaluateName() {//only RelationAttribute are inverse in nature
        return InverseJoinColumnWidget.evaluateName((RelationAttribute)this.getBaseElementSpec().getAttribute(), this.getBaseElementSpec().getReferenceColumn());
    }

}
