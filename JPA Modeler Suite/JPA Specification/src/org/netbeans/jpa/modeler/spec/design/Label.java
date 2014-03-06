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
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for JPALabel complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="JPALabel">
 *   <complexContent>
 *     <extension base="Label">
 *       <attribute name="labelStyle" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JPALabel")
public class Label
        extends AbstractLabel {

    @XmlAttribute
    protected QName labelStyle;

    /**
     * Gets the value of the labelStyle property.
     *
     * @return possible object is {@link QName }
     *
     */
    public QName getLabelStyle() {
        return labelStyle;
    }

    /**
     * Sets the value of the labelStyle property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setLabelStyle(QName value) {
        this.labelStyle = value;
    }

}
