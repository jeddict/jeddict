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
 * Java class for JPAEdge complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="JPAEdge">
 *   <complexContent>
 *     <extension base="LabeledEdge">
 *       <sequence>
 *         <element ref="JPALabel" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="jpaElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       <attribute name="sourceElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       <attribute name="targetElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       <attribute name="messageVisibleKind" type="MessageVisibleKind" />
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlRootElement(name = "edge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Edge extends LabeledEdge {

    @XmlElement
    protected Label label;
    @XmlAttribute
    protected String elementRef;
    @XmlAttribute
    protected String sourceElement;
    @XmlAttribute
    protected String targetElement;

    /**
     * Gets the value of the jpaLabel property.
     *
     * @return possible object is {@link Label }
     *
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Sets the value of the jpaLabel property.
     *
     * @param value allowed object is {@link Label }
     *
     */
    public void setLabel(Label value) {
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

    /**
     * Gets the value of the sourceElement property.
     *
     * @return possible object is {@link QName }
     *
     */
    public String getSourceElement() {
        return sourceElement;
    }

    /**
     * Sets the value of the sourceElement property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setSourceElement(String value) {
        this.sourceElement = value;
    }

    /**
     * Gets the value of the targetElement property.
     *
     * @return possible object is {@link QName }
     *
     */
    public String getTargetElement() {
        return targetElement;
    }

    /**
     * Sets the value of the targetElement property.
     *
     * @param value allowed object is {@link QName }
     *
     */
    public void setTargetElement(String value) {
        this.targetElement = value;
    }

}
