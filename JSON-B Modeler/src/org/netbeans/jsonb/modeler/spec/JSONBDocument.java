/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.modeler.spec;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class JSONBDocument extends FlowNode {

    private JavaClass javaClass;
    
    private final Map<String, JSONBNode> nodes = new LinkedHashMap<>();

      public JSONBDocument(JavaClass javaClass) {
        this.javaClass = javaClass;
        this.javaClass.addLookup(JSONBDocument.class, this);
    }
      
    void loadAttribute(){
        for(Attribute attribute : this.javaClass.getAttributes().getAllAttribute()){
            JSONBNode node;
            if(attribute instanceof RelationAttribute){
                node = new JSONBBranchNode(attribute);
            } else {
                node = new JSONBLeafNode(attribute);
            }
            nodes.put(node.getName(), node);
        }
    }
    
    public String getId() {
        return javaClass.getId();
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return javaClass.getClazz();
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        javaClass.setClazz(name);
    }

    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    /**
     * @return the nodes
     */
    public Collection<JSONBNode> getNodes() {
        return nodes.values();
    }

}
