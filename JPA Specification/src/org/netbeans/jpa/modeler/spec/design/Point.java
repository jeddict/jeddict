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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Point complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="Point">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       <attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlRootElement(name = "waypoint")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point")
public class Point {

    @XmlAttribute(required = true)
    protected double x;
    @XmlAttribute(required = true)
    protected double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    public Point(java.awt.Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    /**
     * Gets the value of the x property.
     *
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     */
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     */
    public void setY(double value) {
        this.y = value;
    }

}
