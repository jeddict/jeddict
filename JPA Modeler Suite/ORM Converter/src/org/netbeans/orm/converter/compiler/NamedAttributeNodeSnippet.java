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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Shiwani Gupta
 */
public class NamedAttributeNodeSnippet implements Snippet {
    
    
    private String name;
    private String subgraph;
    private String keySubgraph;

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

    /**
     * @return the subgraph
     */
    public String getSubgraph() {
        return subgraph;
    }

    /**
     * @param subgraph the subgraph to set
     */
    public void setSubgraph(String subgraph) {
        this.subgraph = subgraph;
    }

    /**
     * @return the keySubgraph
     */
    public String getKeySubgraph() {
        return keySubgraph;
    }

    /**
     * @param keySubgraph the keySubgraph to set
     */
    public void setKeySubgraph(String keySubgraph) {
        this.keySubgraph = keySubgraph;
    }
    
       @Override
    public String getSnippet() throws InvalidDataException {
        if(getName()== null || getName().isEmpty()){
            return null;
        }
        
        StringBuilder builder = new StringBuilder();
        if (getSubgraph() != null && getKeySubgraph() != null) {
           builder.append("@NamedAttributeNode(\"");
        builder.append(getName());
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA); 
        } else {
            builder.append("@NamedAttributeNode(value=\"");
        builder.append(getName());
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);
        }
        
        

        if (getSubgraph() != null) {
            builder.append("subgraph =\"");
            builder.append(getSubgraph());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }
        
        if (getKeySubgraph() != null) {
            builder.append("keySubgraph =\"");
            builder.append(getSubgraph());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

       

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<String>();
        importSnippets.add("javax.persistence.NamedAttributeNode");
        return importSnippets;
    }

}
