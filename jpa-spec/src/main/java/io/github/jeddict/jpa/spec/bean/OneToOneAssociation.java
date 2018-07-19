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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class OneToOneAssociation extends SingleAssociationAttribute {

    @XmlAttribute(name = "own")
    private Boolean owner;//default true/null
    @XmlTransient
    protected String mappedBy;

    /**
     * Gets the value of the mappedBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMappedBy() {
        if (Boolean.FALSE.equals(isOwner())) {
            if (mappedBy != null) {
                return mappedBy;
            }
            if (getConnectedAttribute() != null) {
                return getConnectedAttribute().getName();
            }
        }
        return null;
    }

    /**
     * Sets the value of the mappedBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
      public void setMappedBy(String value) {
        this.mappedBy = value;
        this.owner =  StringUtils.isBlank(mappedBy);
    }

    /**
     * @return the owner
     */
    @Override
    public boolean isOwner() {
        if (owner == null) {
            return Boolean.FALSE;
        }
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    @Override
    public void setOwner(boolean owner) {
        this.owner = owner;
        if(owner){
            mappedBy = null;
        } 
    }

}