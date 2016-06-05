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
package org.netbeans.db.modeler.properties.tablemember;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.netbeans.jpa.modeler.spec.OrderType;
import org.netbeans.jpa.modeler.spec.extend.OrderbyItem;

/**
 *
 * @author Gaurav Gupta
 */
public class TableMembers {

    protected Set<OrderbyItem> columns;
    
    public void addColumn(String column) {
         getColumns().add(new OrderbyItem(column, OrderType.ASC));
    }
    
    public void addColumn(String column, OrderType orderType) {
         getColumns().add(new OrderbyItem(column, orderType));
    }

    public boolean isExist(String column) {
        return getColumns().contains(new OrderbyItem(column, OrderType.ASC));
    }

    public void removeColumn(String column) {
         getColumns().remove(new OrderbyItem(column, OrderType.ASC));
    }

    /**
     * @return the columns
     */
    public Set<OrderbyItem> getColumns() {
        if (columns == null) {
            columns = new LinkedHashSet<>();
        }
        return columns;
    }
    
    public Optional<OrderbyItem> findColumn(String name){
        return getColumns().stream().filter(c -> c.getColumn().equals(name)).findAny();
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(Set<OrderbyItem> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return getColumns().stream().map(c -> c.getColumn()).collect(Collectors.joining(", "));
    }

}
