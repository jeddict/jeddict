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
package io.github.jeddict.relation.mapper.widget.column.embedded;

import io.github.jeddict.relation.mapper.spec.DBEmbeddedAssociationInverseJoinColumn;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class EmbeddedAssociationInverseJoinColumnWidget extends EmbeddedAssociationColumnWidget<DBEmbeddedAssociationInverseJoinColumn> {

    public EmbeddedAssociationInverseJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    @Override
    protected String evaluateName() {
        AssociationOverride associationOverride = this.getBaseElementSpec().getAssociationOverride();
        Column embeddableColumn = null;
        Attribute refAttribute = this.getBaseElementSpec().getAttribute();
        PersistenceBaseAttribute baseRefAttribute = null;
        if (refAttribute instanceof PersistenceBaseAttribute) {
            baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
            embeddableColumn = baseRefAttribute.getColumn();
        }

//        if (StringUtils.isNotBlank(associationOverride.getColumn().getName())) {
//            return associationOverride.getColumn().getName();
//        } else if (StringUtils.isNotBlank(embeddableColumn.getName())) {
//            return embeddableColumn.getName();
//        } else {
        return baseRefAttribute.getDefaultColumnName();
//        }

    }

}
