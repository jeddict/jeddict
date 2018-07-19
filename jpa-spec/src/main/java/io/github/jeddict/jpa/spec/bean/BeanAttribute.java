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
package io.github.jeddict.jpa.spec.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanAttribute extends BaseAttribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;

    public BeanAttribute() {
    }

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }
    
}
