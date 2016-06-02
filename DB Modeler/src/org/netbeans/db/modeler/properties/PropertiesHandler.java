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
package org.netbeans.db.modeler.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.db.modeler.core.widget.table.TableWidget;
import org.netbeans.db.modeler.properties.uniqueconstraint.UniqueConstraintPanel;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.jpa.modeler.spec.UniqueConstraint;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.openide.nodes.PropertySupport;

public class PropertiesHandler {

    public static PropertySupport getUniqueConstraintProperties(TableWidget<? extends DBTable> tableWidget) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("UniqueConstraint", "Unique Constraint", "Unique Constraints of Database Table");
        attributeEntity.setCountDisplay(new String[]{"No UniqueConstraints exist", "One UniqueConstraint exist", "UniqueConstraints exist"});
        Set<UniqueConstraint> uniqueConstraints = tableWidget.getBaseElementSpec().getUniqueConstraints();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Columns", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new UniqueConstraintPanel(tableWidget));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = uniqueConstraints.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                Iterator<UniqueConstraint> itr = uniqueConstraints.iterator();
                while (itr.hasNext()) {
                    UniqueConstraint constraint = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = constraint;
                    row[1] = constraint.getName();
                    row[2] = constraint.toString();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                uniqueConstraints.clear();
                data.stream().forEach((row) -> {
                    uniqueConstraints.add((UniqueConstraint) row[0]);
                });
                this.data = data;
            }

        });

        return new NEntityPropertySupport(tableWidget.getModelerScene().getModelerFile(), attributeEntity);
    }

}
