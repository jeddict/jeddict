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

import java.awt.Rectangle;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Bounds complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="Bounds">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       <attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       <attribute name="width" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       <attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlRootElement(name = "Bounds") // JJPA
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bounds")
public class Bounds {

    public Bounds() {
    }

    public Bounds(Rectangle rectangle) {
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
        this.x = rectangle.getX();
        this.y = rectangle.getY();

    }

    @Override
    public String toString() {
        return "Bounds{" + "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
    }

    public Rectangle toRectangle() {
        return new Rectangle((int) this.x, (int) this.y, (int) this.width, (int) this.height);
    }

    public java.awt.Point toPoint() {
        return new java.awt.Point((int) this.x, (int) this.y);
    }

    @XmlAttribute(required = true)
    protected double x;
    @XmlAttribute(required = true)
    protected double y;
    @XmlAttribute(required = true)
    protected double width;
    @XmlAttribute(required = true)
    protected double height;

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

    /**
     * Gets the value of the width property.
     *
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     */
    public void setWidth(double value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     */
    public void setHeight(double value) {
        this.height = value;
    }

}
