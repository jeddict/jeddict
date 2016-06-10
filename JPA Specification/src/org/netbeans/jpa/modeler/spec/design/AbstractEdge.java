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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Edge complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * <complexType name="Edge">
 *   <complexContent>
 *     <extension base="DiagramElement">
 *       <sequence>
 *         <element name="waypoint" type="Point" maxOccurs="unbounded" minOccurs="2"/>
 *       </sequence>
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Edge", propOrder = {
    "waypoint"
})
@XmlSeeAlso({
    LabeledEdge.class
})
public abstract class AbstractEdge
        extends DiagramElement {

    @XmlElement(required = true)
    private List<Point> waypoint;

    /**
     * Gets the value of the waypoint property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the waypoint property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaypoint().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Point }
     *
     *
     */
    public List<Point> getWaypoint() {
        if (waypoint == null) {
            setWaypoint(new ArrayList<Point>());
        }
        return this.waypoint;
    }

    public List<java.awt.Point> getWaypointCollection() {
        if (waypoint == null) {
            setWaypoint(new ArrayList<Point>());
        }
        List<java.awt.Point> point_Col = new LinkedList<java.awt.Point>();
        for (Point point : getWaypoint()) {
            point_Col.add(new java.awt.Point((int) point.getX(), (int) point.getY()));
        }
        return point_Col;
    }

    /**
     * @param waypoint the waypoint to set
     */
    public void setWaypoint(List<Point> waypoint) {
        this.waypoint = waypoint;
    }

    public void addWaypoint(Point waypoint_In) {
        if (waypoint == null) {
            setWaypoint(new ArrayList<Point>());
        }
        this.waypoint.add(waypoint_In);
    }

    public void addWaypoint(java.awt.Point waypoint_In) {
        if (waypoint == null) {
            setWaypoint(new ArrayList<Point>());
        }
        this.waypoint.add(new Point(waypoint_In));
    }

}
