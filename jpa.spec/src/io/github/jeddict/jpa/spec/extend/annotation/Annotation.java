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
package io.github.jeddict.jpa.spec.extend.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import io.github.jeddict.jpa.spec.extend.AnnotationLocation;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Annotation<L extends AnnotationLocation> implements Serializable {

    @XmlAttribute(name = "e")
    private boolean enable = true;
    @XmlAttribute(name = "n", required = true)
    private String name;
    @XmlElement(name="e")
    private List<AnnotationElement> elements;//todo

    public Annotation() {
    }

    public Annotation(String name) {
        this.name = name;
    }
  
    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable the enable to set
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the elements
     */
    public List<AnnotationElement> getAnnotationElements() {
        if(elements==null){
            elements = new ArrayList<>();
        }
        return elements;
    }

    public boolean addAnnotationElement(AnnotationElement e) {
        return getAnnotationElements().add(e);
    }

    public boolean removeAnnotationElement(AnnotationElement e) {
        return getAnnotationElements().remove(e);
    }

    /**
     * @param elements the elements to set
     */
    public void setAnnotationElements(List<AnnotationElement> elements) {
        this.elements = elements;
    }
    
    /**
     * @return the locationType
     */
    public abstract L getLocationType();

    /**
     * @param locationType the locationType to set
     */
    public abstract void setLocationType(L locationType);

    
    @Override
    public String toString() {
        return "Annotation{" + "name=" + name + ", elements=" + elements + '}';
    }
    
    

}
