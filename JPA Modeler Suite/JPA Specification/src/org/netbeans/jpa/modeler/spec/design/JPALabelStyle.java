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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for JPALabelStyle complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="JPALabelStyle">
 *   <complexContent>
 *     <extension base="Style">
 *       <sequence>
 *         <element ref="Font"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JPALabelStyle", propOrder = {
    "font"
})
public class JPALabelStyle
        extends Style {

    @XmlElement(name = "Font", required = true)
    protected Font font;

    /**
     * Gets the value of the font property.
     *
     * @return possible object is {@link Font }
     *
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     *
     * @param value allowed object is {@link Font }
     *
     */
    public void setFont(Font value) {
        this.font = value;
    }

}
