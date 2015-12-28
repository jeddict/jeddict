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
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;


@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbXmlElement {

    /**
     * Name of the XML Schema element.
     * <p>
     * If the value is "##default", then element name is derived from the
     * JavaBean property name.
     */
    @XmlAttribute
    private String name;// default "##default";

    /**
     * Customize the element declaration to be nillable.
     * <p>
     * If nillable() is true, then the JavaBean property is mapped to a XML
     * Schema nillable element declaration.
     */
    @XmlAttribute
    private Boolean nillable = false;// default false;

    /**
     * Customize the element declaration to be required.
     * <p>
     * If required() is true, then Javabean property is mapped to an XML schema
     * element declaration with minOccurs="1". maxOccurs is "1" for a single
     * valued property and "unbounded" for a multivalued property.
     * <p>
     * If required() is false, then the Javabean property is mapped to XML
     * Schema element declaration with minOccurs="0". maxOccurs is "1" for a
     * single valued property and "unbounded" for a multivalued property.
     */
    @XmlAttribute
    private Boolean required = false;// default false;

    /**
     * XML target namespace of the XML Schema element.
     * <p>
     * If the value is "##default", then the namespace is determined as follows:
     * <ol>
     * <li>
     * If the enclosing package has {@link XmlSchema} annotation, and its
     * {@link XmlSchema#elementFormDefault() elementFormDefault} is
     * {@link XmlNsForm#QUALIFIED QUALIFIED}, then the namespace of the
     * enclosing class.
     *
     * <li>
     * Otherwise &#39;&#39; (which produces unqualified element in the default
     * namespace.
     * </ol>
     */
    @XmlAttribute
    private String namespace;// default "##default";

    /**
     * Default value of this element.
     *
     * <p>
     * The
     * <pre>'\u0000'</pre> value specified as a default of this annotation
     * element is used as a poor-man's substitute for null to allow
     * implementations to recognize the 'no default value' state.
     */
    @XmlAttribute
    private String defaultValue;// default "\u0000";

    /**
     * The Java class being referenced.
     */
    @XmlAttribute
    private Class type;// default DEFAULT.class;

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

    /**
     * @return the type
     */
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.type = type;
    }

}
