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
package io.github.jeddict.jpa.spec.extend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.github.jeddict.jpa.spec.validator.ClassMemberValidator;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = ClassMemberValidator.class)
public class ClassMembers {

    @XmlIDREF
    @XmlElement(name = "a")
    protected List<Attribute> attributes;
    @XmlAttribute(name = "e")
    private boolean enable = true;
    
    @XmlElement(name="pre")
    private String preCode;
    @XmlElement(name="post")
    private String postCode;

    public boolean addAttribute(Attribute attribute) {
        return getAttributes().add(attribute);
    }

    public boolean isExist(Attribute attribute) {
        return getAttributes().stream().filter(a -> a == attribute).findAny().isPresent();
    }

    public boolean removeAttribute(Attribute attribute) {
        return getAttributes().remove(attribute);
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public List<String> getAttributeNames() {
        return getAttributes().stream().map((Attribute a) -> a.getName()).collect(toList());
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable the enable to set
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    
    @Override
    public String toString() {
        return getAttributes().stream().map((Attribute a) -> a.getDataTypeLabel() + " " + a.getName()).collect(Collectors.joining(", "));
    }
    
    /**
     * @return the preCode
     */
    public String getPreCode() {
        return preCode;
    }

    /**
     * @param preCode the preCode to set
     */
    public void setPreCode(String preCode) {
        this.preCode = preCode;
    }

    /**
     * @return the postCode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * @param postCode the postCode to set
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

}
