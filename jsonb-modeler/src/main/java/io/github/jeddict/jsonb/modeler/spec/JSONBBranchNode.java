/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.spec;

import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;

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
            Embedded embedded = (Embedded)attribute;
            this.documentReference = embedded.getConnectedClass().getLookup(JSONBDocument.class);
        } else if(attribute instanceof ElementCollection){
            ElementCollection elementCollection = (ElementCollection)attribute;
            this.documentReference = elementCollection.getConnectedClass().getLookup(JSONBDocument.class);
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
