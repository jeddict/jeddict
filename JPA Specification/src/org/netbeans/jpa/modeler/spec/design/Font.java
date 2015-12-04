/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.design;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Font complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="Font">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="size" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       <attribute name="isBold" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       <attribute name="isItalic" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       <attribute name="isUnderline" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       <attribute name="isStrikeThrough" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Font")
public class Font {

    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected Double size;
    @XmlAttribute
    protected Boolean isBold;
    @XmlAttribute
    protected Boolean isItalic;
    @XmlAttribute
    protected Boolean isUnderline;
    @XmlAttribute
    protected Boolean isStrikeThrough;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the size property.
     *
     * @return possible object is {@link Double }
     *
     */
    public Double getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     *
     * @param value allowed object is {@link Double }
     *
     */
    public void setSize(Double value) {
        this.size = value;
    }

    /**
     * Gets the value of the isBold property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isIsBold() {
        return isBold;
    }

    /**
     * Sets the value of the isBold property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setIsBold(Boolean value) {
        this.isBold = value;
    }

    /**
     * Gets the value of the isItalic property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isIsItalic() {
        return isItalic;
    }

    /**
     * Sets the value of the isItalic property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setIsItalic(Boolean value) {
        this.isItalic = value;
    }

    /**
     * Gets the value of the isUnderline property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isIsUnderline() {
        return isUnderline;
    }

    /**
     * Sets the value of the isUnderline property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setIsUnderline(Boolean value) {
        this.isUnderline = value;
    }

    /**
     * Gets the value of the isStrikeThrough property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isIsStrikeThrough() {
        return isStrikeThrough;
    }

    /**
     * Sets the value of the isStrikeThrough property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setIsStrikeThrough(Boolean value) {
        this.isStrikeThrough = value;
    }

}
