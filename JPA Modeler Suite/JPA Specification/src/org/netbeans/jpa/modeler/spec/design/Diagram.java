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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.netbeans.modeler.specification.model.document.IDiagramElement;

/**
 * <p>
 * Java class for Diagram complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 *
 *
 *
 */
@XmlRootElement(name = "diagram") // J
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "diagram", propOrder = {
    "plane",
    "labelStyle"
})
public class Diagram
        extends AbstractDiagram implements IDiagramElement {

    @XmlElement(name = "plane", required = true)
    protected Plane plane;
    @XmlElement(name = "label-style")
    protected List<JPALabelStyle> labelStyle;

    /**
     * Gets the value of the jpaPlane property.
     *
     * @return possible object is {@link Plane }
     *
     */
    public Plane getJPAPlane() {
        return plane;
    }

    /**
     * Sets the value of the jpaPlane property.
     *
     * @param value allowed object is {@link Plane }
     *
     */
    public void setJPAPlane(Plane value) {
        this.plane = value;
    }

    /**
     * Gets the value of the jpaLabelStyle property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the jpaLabelStyle property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabelStyle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JPALabelStyle }
     *
     *
     */
    public List<JPALabelStyle> getLabelStyle() {
        if (labelStyle == null) {
            labelStyle = new ArrayList<JPALabelStyle>();
        }
        return this.labelStyle;
    }

}
