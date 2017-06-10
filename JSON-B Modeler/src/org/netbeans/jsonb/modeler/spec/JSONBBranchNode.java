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

import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

/**
 *
 * @author jGauravGupta
 */
public class JSONBBranchNode extends JSONBNode {
   
    private JSONBDocument documentReference;
    
    public JSONBBranchNode(Attribute attribute) {
        super(attribute);
        if(attribute instanceof RelationAttribute){
            RelationAttribute relationAttribute = (RelationAttribute)attribute;
            this.documentReference = relationAttribute.getConnectedEntity().getLookup(JSONBDocument.class);
        } else if(attribute instanceof Embedded){
            Embedded relationAttribute = (Embedded)attribute;
            this.documentReference = relationAttribute.getConnectedClass().getLookup(JSONBDocument.class);
        } else if(attribute instanceof ElementCollection){
            ElementCollection relationAttribute = (ElementCollection)attribute;
            this.documentReference = relationAttribute.getConnectedClass().getLookup(JSONBDocument.class);
        }
    }

    /**
     * @return the documentReference
     */
    public JSONBDocument getDocumentReference() {
        return documentReference;
    }

    /**
     * @param documentReference the documentReference to set
     */
    public void setDocumentReference(JSONBDocument documentReference) {
        this.documentReference = documentReference;
    }
    
}
