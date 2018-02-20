/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.spec;

import java.util.List;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.extend.Attribute;

public class DBEmbeddedAttributeColumn extends DBEmbeddedColumn<Attribute> {

    protected AttributeOverride attributeOverride;

    public DBEmbeddedAttributeColumn(String name, List<Embedded> embeddedList, Attribute managedAttribute) {
        super(name, embeddedList, managedAttribute);
        init();
    }
    
    protected void init(){
        if (getAttribute() instanceof ElementCollection) {

        } else {
            attributeOverride = embeddedList.get(0).findAttributeOverride(getKeyName());
            if (attributeOverride == null) {
                attributeOverride = new AttributeOverride();
                attributeOverride.setName(getKeyName());
                embeddedList.get(0).addAttributeOverride(attributeOverride);
            }
        }
    }

    /**
     * @return the attributeOverride
     */
    public AttributeOverride getAttributeOverride() {
        return attributeOverride;
    }

}
