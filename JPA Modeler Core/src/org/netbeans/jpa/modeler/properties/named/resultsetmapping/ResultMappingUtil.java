/**
 * Copyright [2014] Gaurav Gupta
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

package org.netbeans.jpa.modeler.properties.named.resultsetmapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.FieldResult;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;


public class ResultMappingUtil {
    public static NAttributeEntity getColumnResult( final List<ColumnResult> columnResults, ModelerFile modelerFile) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("ColumnResult", "Column Result", "");
        attributeEntity.setCountDisplay(new String[]{"No Column Results", "One Column Result", " Column Results"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Class", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ColumnResultPanel(modelerFile));
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (columnResults!= null) {
                    count = columnResults.size();
                } else {
                    count = 0;
                }
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                if (columnResults != null) {
                    for (ColumnResult columnResult : new CopyOnWriteArrayList<>(columnResults)) {
                        Object[] row = new Object[3];
                        row[0] = columnResult;
                        row[1] = columnResult.getName();
                        row[2] = columnResult.getClazz();
                        data_local.add(row);
                    }
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                if (columnResults != null) {
                    columnResults.clear();
                }
                for (Object[] row : data) {
                    ColumnResult column = (ColumnResult) row[0];
                    columnResults.add(column); 
                }
                initData();
            }
        });
        return attributeEntity;
    }
    public static NAttributeEntity getFieldResult( final List<FieldResult> fieldResults, ModelerFile modelerFile) {
        final NAttributeEntity attributeEntity = new NAttributeEntity("FieldResult", "Field Result", "");
        attributeEntity.setCountDisplay(new String[]{"No Field Results", "One Field Result", " Field Results"});
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Column", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new FieldResultPanel());
        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<>();
            int count;

            @Override
            public void initCount() {
                if (fieldResults!= null) {
                    count = fieldResults.size();
                } else {
                    count = 0;
                }
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<>();
                if (fieldResults != null) {
                    for (FieldResult fieldResult : new CopyOnWriteArrayList<>(fieldResults)) {
                        Object[] row = new Object[3];
                        row[0] = fieldResult;
                        row[1] = fieldResult.getName();
                        row[2] = fieldResult.getColumn();
                        data_local.add(row);
                    }
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                if (fieldResults != null) {
                    fieldResults.clear();
                }
                for (Object[] row : data) {
                    FieldResult field = (FieldResult) row[0];
                    fieldResults.add(field); 
                }
                initData();
            }
        });
        return attributeEntity;
    }

    
}
