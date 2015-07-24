/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec;

import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;

public abstract class IdentifiableClass extends ManagedClass implements PrimaryKeyContainer {

    @XmlAttribute(name = "jaxb-root-element")
    private Boolean xmlRootElement = false;

    @XmlElement(name = "id-class")
    protected IdClass idClass;
    protected Attributes attributes;

    public void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess) {
        super.load(entityMappings, element, fieldAccess);
        this.idClass = IdClass.load(element);

        if (this.getAttributes().getEmbeddedId() != null) {
            this.setCompositePrimaryKeyClass(this.getAttributes().getEmbeddedId().getAttributeType());
            this.setCompositePrimaryKeyType(CompositePrimaryKeyType.EMBEDDEDID);
        } else if (idClass != null) {
            this.setCompositePrimaryKeyClass(this.getIdClass().getClazz());
            this.setCompositePrimaryKeyType(CompositePrimaryKeyType.IDCLASS);
        } else {
            this.setCompositePrimaryKeyClass(null);
            this.setCompositePrimaryKeyType(null);
        }
    }

    /**
     * @return the xmlRootElement
     */
    public Boolean getXmlRootElement() {
        return xmlRootElement;
    }

    /**
     * @param xmlRootElement the xmlRootElement to set
     */
    public void setXmlRootElement(Boolean xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }

    /**
     * Gets the value of the idClass property.
     *
     * @return possible object is {@link IdClass }
     *
     */
    @Override
    public IdClass getIdClass() {
        return idClass;
    }

    /**
     * Sets the value of the idClass property.
     *
     * @param value allowed object is {@link IdClass }
     *
     */
    @Override
    public void setIdClass(IdClass value) {
        this.idClass = value;
    }

    /**
     * Gets the value of the attributes property.
     *
     * @return possible object is {@link Attributes }
     *
     */
    @Override
    public Attributes getAttributes() {
        if (attributes == null) {
            attributes = new Attributes();
        }
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     *
     * @param value allowed object is {@link Attributes }
     *
     */
    public void setAttributes(Attributes value) {
        this.attributes = value;
    }

}
