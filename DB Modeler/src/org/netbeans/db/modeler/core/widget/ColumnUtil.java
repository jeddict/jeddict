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

import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBForeignKey;
import org.netbeans.db.modeler.spec.DBParentAssociationColumn;
import org.netbeans.jpa.modeler.spec.JoinColumn;

/**
 *
 * @author Shiwani Gupta
 */
public class ColumnUtil {

    /**
     * Exception Description: The @JoinColumns on the annotated element [method
     * get] from the entity class [class Employee] is incomplete. When the
     * source entity class uses a composite primary key, a @JoinColumn must be
     * specified for each join column using the
     *
     * @JoinColumns. Both the name and the referencedColumnName elements must be
     * specified in each such @JoinColumn.
     */
    public static void syncronizeCompositeKeyJoincolumn(TableWidget sourceTableWidget, final TableWidget targetTableWidget) {
        if (sourceTableWidget.getPrimaryKeyWidgets().size() > 1) {
            for (Object widget : sourceTableWidget.getPrimaryKeyWidgets()) {
                IPrimaryKeyWidget primaryKeyWidget = (IPrimaryKeyWidget) widget;
                Optional<ReferenceFlowWidget> optionalReferenceFlowWidget = primaryKeyWidget.getReferenceFlowWidget().stream().filter(r -> r.getForeignKeyWidget().getTableWidget() == targetTableWidget).findFirst();
                if (optionalReferenceFlowWidget.isPresent()) {
                    ForeignKeyWidget foreignKeyWidget = optionalReferenceFlowWidget.get().getForeignKeyWidget();
                    JoinColumn joinColumn;
                    if (foreignKeyWidget instanceof ParentAssociationColumnWidget) {
                        joinColumn = ((DBParentAssociationColumn) foreignKeyWidget.getBaseElementSpec()).getJoinColumnOverride();
                    } else {
                        joinColumn = ((DBForeignKey) foreignKeyWidget.getBaseElementSpec()).getJoinColumn();
                    }
                    if (StringUtils.isEmpty(joinColumn.getReferencedColumnName())) {
                        joinColumn.setReferencedColumnName(primaryKeyWidget.getName());
                    }
                    if (StringUtils.isEmpty(joinColumn.getName())) {
                        joinColumn.setName(foreignKeyWidget.getName());
                    }
                }
            }
        }

    }

}
