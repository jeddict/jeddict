/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.spec.workspace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import org.netbeans.modeler.widget.design.NodeTextDesign;

/**
 *
 * @author jGauravGupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = WorkSpaceItemValidator.class)
public class WorkSpaceItem {

    @XmlAttribute(name = "ref")
    @XmlIDREF
    private JavaClass javaClass;

    @XmlAttribute(name = "x")
    private Integer x;
    @XmlAttribute(name = "y")
    private Integer y;

    @XmlElement(name = "v")
    private NodeTextDesign textDesign;
    
    @XmlElement(name = "jbv")
    private NodeTextDesign jsonbTextDesign;

    @XmlElementWrapper(name = "el")
    @XmlElement(name = "e")
    private List<WorkSpaceElement> workSpaceElement;

    @XmlTransient
    private Map<Attribute, WorkSpaceElement> workSpaceElementCache;

    public WorkSpaceItem() {
    }

    public WorkSpaceItem(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    public WorkSpaceItem(JavaClass javaClass, Integer x, Integer y) {
        this.javaClass = javaClass;
        this.x = x;
        this.y = y;
    }
    
    public void beforeMarshal(Marshaller marshaller) {
        if (workSpaceElement != null
                && (workSpaceElement.isEmpty()
                || workSpaceElement.stream().allMatch(WorkSpaceElementValidator::isEmpty))) {
            workSpaceElement = null;
        }
    }

    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    /**
     * @return the x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(Integer y) {
        this.y = y;
    }

    public Point getLocation() {
        if (x == null || y == null) {
            return null;
        }
        return new Point(x, y);
    }

    public void setLocation(Point point) {
        if (point == null) {
            x = null;
            y = null;
        } else {
            x = point.x;
            y = point.y;
        }
    }

    @Override
    public int hashCode() {
        Integer hash = 7;
        if (this.javaClass != null) {
            hash = 37 * hash + Objects.hashCode(this.javaClass.getId());
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkSpaceItem other = (WorkSpaceItem) obj;
        if (this.javaClass == null || other.javaClass == null) {
            return false;
        }
        if (!Objects.equals(this.javaClass.getId(), other.javaClass.getId())) {
            return false;
        }
        return true;
    }

    /**
     * @return the workSpaceElement
     */
    public List<WorkSpaceElement> getWorkSpaceElement() {
        if (workSpaceElement == null) {
            workSpaceElement = new ArrayList<>();
        }
        return workSpaceElement;
    }

    
    public void addWorkSpaceElement(WorkSpaceElement workSpaceElement) {
        getWorkSpaceElement().add(workSpaceElement);
        this.workSpaceElementCache = null;
    }

    public Map<Attribute, WorkSpaceElement> getWorkSpaceElementMap() {
        if (workSpaceElementCache == null) {
            workSpaceElementCache = getWorkSpaceElement()
                    .stream()
                    .collect(toMap(WorkSpaceElement::getAttribute, identity()));
        }
        return workSpaceElementCache;
    }

    /**
     * @param workSpaceElement the workSpaceElement to set
     */
    public void setWorkSpaceElement(List<WorkSpaceElement> workSpaceElement) {
        this.workSpaceElementCache = null;
        this.workSpaceElement = workSpaceElement;
    }

    /**
     * @return the textDesign
     */
    public NodeTextDesign getTextDesign() {
        if(textDesign == null){
            textDesign = new NodeTextDesign(); 
        }
        return textDesign;
    }

    /**
     * @param textDesign the textDesign to set
     */
    public void setTextDesign(NodeTextDesign textDesign) {
        this.textDesign = textDesign;
    }

    /**
     * @return the jsonbTextDesign
     */
    public NodeTextDesign getJsonbTextDesign() {
        if(jsonbTextDesign == null){
            jsonbTextDesign = new NodeTextDesign(); 
        }
        return jsonbTextDesign;
    }

    /**
     * @param jsonbTextDesign the jsonbTextDesign to set
     */
    public void setJsonbTextDesign(NodeTextDesign jsonbTextDesign) {
        this.jsonbTextDesign = jsonbTextDesign;
    }

}
