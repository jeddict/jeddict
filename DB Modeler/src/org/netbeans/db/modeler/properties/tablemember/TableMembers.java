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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import org.netbeans.jpa.modeler.spec.OrderType;

/**
 *
 * @author Gaurav Gupta
 */
public class TableMembers {

    protected Map<String, OrderType> columns;
    
    public void addColumn(String column) {
         getColumns().put(column, OrderType.ASC);
    }
    
    public void addColumn(String column, OrderType orderType) {
         getColumns().put(column, orderType);
    }

    public boolean isExist(String column) {
        return getColumns().containsKey(column);
    }

    public void removeColumn(String column) {
         getColumns().remove(column);
    }

    /**
     * @return the columns
     */
    public Map<String, OrderType> getColumns() {
        if (columns == null) {
            columns = new LinkedHashMap<>();
        }
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(Map<String, OrderType> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return getColumns().keySet().stream().collect(Collectors.joining(", "));
    }

}
