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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for JPAShape complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 *
 */
@XmlRootElement(name = "JPAShape")
@XmlAccessorType(XmlAccessType.FIELD)
public class Shape
        extends LabeledShape {
//    @XmlElementWrapper( name="extensionElements" )
//    @XmlElement(name = "extension")
//    private JPAShapeDesign jpaShapeDesign;

    @XmlElement
    protected Label label;
    @XmlAttribute
    protected String elementRef;

    /**
     * Gets the value of the jpaLabel property.
     *
     * @return possible object is {@link Label }
     *
     */
    public Label getJPALabel() {
        return label;
    }

    /**
     * Sets the value of the jpaLabel property.
     *
     * @param value allowed object is {@link Label }
     *
     */
    public void setJPALabel(Label value) {
        this.label = value;
    }

    /**
     * Gets the value of the jpaElement property.
     *
     * @return possible object is {@link QName }
     *
     */
    public String getElementRef() {
        return elementRef;
    }

    /**
     * Sets the value of the jpaElement property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setElementRef(String value) {
        this.elementRef = value;
    }

}
