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
package org.netbeans.jeddict.jsonb.modeler.spec;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.FlowPin;

/**
 *
 * @author Gaurav Gupta
 */
public class JSONBNode extends FlowPin {

    private Attribute attribute;

    public JSONBNode(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getId() {
        return attribute.getId();
    }
    
    /**
     * @return the name
     */
    @Override
    public String getName() {
        if(!StringUtils.isBlank(attribute.getJsonbProperty())){
            return attribute.getJsonbProperty();
        }
        return attribute.getName();
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
       attribute.setJsonbProperty(name);
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}
