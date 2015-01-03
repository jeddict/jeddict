/**
 * Copyright [2015] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;


@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbXmlAttribute {

    /**
     * Name of the XML Schema attribute. By default, the XML Schema attribute
     * name is derived from the JavaBean property name.
     *
     */
    @XmlAttribute(name="name")
    private String name;// default "##default";

    /**
     * Specifies if the XML Schema attribute is optional or required. If true,
     * then the JavaBean property is mapped to a XML Schema attribute that is
     * required. Otherwise it is mapped to a XML Schema attribute that is
     * optional.
     *
     */
    @XmlAttribute
    private Boolean required = false;// default false;

    /**
     * Specifies the XML target namespace of the XML Schema attribute.
     *
     */
    @XmlAttribute
    private String namespace;// default "##default" ;

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
     * @return the required
     */
    public Boolean getRequired() {
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
}
