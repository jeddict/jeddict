/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.core.widget;

import org.netbeans.db.modeler.spec.DBParentAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBParentColumn;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class ParentAssociationInverseJoinColumnWidget extends ParentAssociationColumnWidget<DBParentAssociationInverseJoinColumn> {

    public ParentAssociationInverseJoinColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(ParentAssociationInverseJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected String evaluateName() {
        Column embeddableColumn = null;
        Attribute refAttribute = ((DBParentColumn) this.getBaseElementSpec()).getAttribute();
        PersistenceBaseAttribute baseRefAttribute = null;
        if (refAttribute instanceof PersistenceBaseAttribute) {
            baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
            embeddableColumn = baseRefAttribute.getColumn();
        }
        return baseRefAttribute.getDefaultColumnName();
    }

}
