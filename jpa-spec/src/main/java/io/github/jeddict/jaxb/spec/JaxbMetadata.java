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
package io.github.jeddict.jaxb.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.github.jeddict.jaxb.spec.validator.JaxbMetadataValidator;
import org.netbeans.modeler.properties.type.Embedded;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = JaxbMetadataValidator.class)
public class JaxbMetadata implements Embedded {

    @XmlAttribute(name = "nm")
    private String name;

    @XmlAttribute(name = "nil")
    private Boolean nillable;

    @XmlAttribute(name = "req")
    private Boolean required;

    @XmlAttribute(name = "ns")
    private String namespace;// default "##default";

    @XmlAttribute(name = "dv")
    private String defaultValue;// default "\u0000";

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
     * @return the nillable
     */
    public Boolean getNillable() {
        if (nillable == null) {
            return false;
        }
        return nillable;
    }

    /**
     * @param nillable the nillable to set
     */
    public void setNillable(Boolean nillable) {
        this.nillable = nillable;
    }

    /**
     * @return the required
     */
    public Boolean getRequired() {
        if (required == null) {
            return false;
        }
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
