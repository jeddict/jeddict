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
package org.netbeans.db.modeler.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.modeler.specification.model.document.IDefinitionElement;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;

/**
 *
 * @author Gaurav Gupta
 */
public class DBMapping implements IDefinitionElement, IRootElement{
    
    private String id;
    private String name;
    private List<Table> tables;

    /**
     * @return the tables
     */
    public List<Table> getTables() {
        if(tables==null){
            tables = new ArrayList<>();
        }
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public void removeBaseElement(IBaseElement baseElement_In) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addBaseElement(IBaseElement baseElement_In) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getCustomAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCustomAttributes(Map<String, String> customAttributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getRootElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRootElement(Object rootElement) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
}
