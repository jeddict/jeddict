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
package io.github.jeddict.relation.mapper.initializer;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.relation.mapper.widget.column.ForeignKeyWidget;
import io.github.jeddict.relation.mapper.widget.api.IPrimaryKeyWidget;
import io.github.jeddict.relation.mapper.widget.column.parent.ParentAssociationColumnWidget;
import io.github.jeddict.relation.mapper.widget.flow.ReferenceFlowWidget;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.spec.DBForeignKey;
import io.github.jeddict.relation.mapper.spec.DBParentAssociationColumn;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.extend.IJoinColumn;

/**
 *
 * @author Shiwani Gupta
 */
public class ColumnUtil {

    /**
     * Exception Description: The @JoinColumns on the annotated element [method
     * get] from the entity class [class Employee] is incomplete.
     *
     * When the source entity class uses a composite primary key, a @JoinColumn
     * must be specified for each join column using the @JoinColumns. Both the
     * name and the referencedColumnName elements must be specified in each such
     * '@JoinColumn'.
     */
    public static void syncronizeCompositeKeyJoincolumn(TableWidget<DBTable> sourceTableWidget, final TableWidget<DBTable> targetTableWidget) {
        if (sourceTableWidget.getPrimaryKeyWidgets().size() > 1) {
            for (IPrimaryKeyWidget<DBColumn<Id>> primaryKeyWidget : sourceTableWidget.getPrimaryKeyWidgets()) {
                Optional<ReferenceFlowWidget> optionalReferenceFlowWidget = primaryKeyWidget.getReferenceFlowWidget()
                        .stream()
                        .filter(r -> r.getForeignKeyWidget().getTableWidget() == targetTableWidget)
                        .findFirst();
                if (optionalReferenceFlowWidget.isPresent()) {
                    ForeignKeyWidget foreignKeyWidget = optionalReferenceFlowWidget.get().getForeignKeyWidget();
                    IJoinColumn joinColumn;
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
