/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.extend.annotation.Annotation;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class Attribute extends FlowPin implements JaxbVariableTypeHandler {

    @XmlElement(name="an")
    private List<Annotation> annotation;
    @XmlAttribute
    private boolean visibile = true;
    @XmlElement(name = "des")
    private String description;

    @XmlAttribute(name = "xvt", required = true)//(name = "jaxb-variable-type", required = true)
    private JaxbVariableType jaxbVariableType;
    @XmlElement(name = "xa")//(name = "jaxb-xml-attribute")
    private JaxbXmlAttribute jaxbXmlAttribute;
    @XmlElement(name = "xe")//(name = "jaxb-xml-element")
    private JaxbXmlElement jaxbXmlElement;
    @XmlElement(name = "xe")//(name = "jaxb-xml-element")
    @XmlElementWrapper(name = "xel")//(name = "jaxb-xml-element-list")
    private List<JaxbXmlElement> jaxbXmlElementList;
//    @XmlAttribute(name = "jaxb-xml-list")
//    private Boolean jaxbXmlList;

    @XmlAttribute(name = "name", required = true)
    protected String name;
    
    @XmlAttribute(name="ui")
    private Boolean includeInUI;
    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    @Override
    public void setName(String value) {
        this.name = value;
    }

    /**
     * @return the annotation
     */
    public List<Annotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }

    public void addAnnotation(Annotation annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        this.annotation.add(annotation_In);
    }

    public void removeAnnotation(Annotation annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        this.annotation.remove(annotation_In);
    }

    /**
     * @return the visibile
     */
    public boolean isVisibile() {
        return visibile;
    }

    /**
     * @param visibile the visibile to set
     */
    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    /**
     * @return the jaxbVariableType
     */
    @Override
    public JaxbVariableType getJaxbVariableType() {
        return jaxbVariableType;
    }

    /**
     * @param jaxbVariableType the jaxbVariableType to set
     */
    @Override
    public void setJaxbVariableType(JaxbVariableType jaxbVariableType) {
        this.jaxbVariableType = jaxbVariableType;
    }

    /**
     * @return the jaxbXmlAttribute
     */
    @Override
    public JaxbXmlAttribute getJaxbXmlAttribute() {
        return jaxbXmlAttribute;
    }

    /**
     * @param jaxbXmlAttribute the jaxbXmlAttribute to set
     */
    @Override
    public void setJaxbXmlAttribute(JaxbXmlAttribute jaxbXmlAttribute) {
        this.jaxbXmlAttribute = jaxbXmlAttribute;
    }

    /**
     * @return the jaxbXmlElement
     */
    @Override
    public JaxbXmlElement getJaxbXmlElement() {
        return jaxbXmlElement;
    }

    /**
     * @param jaxbXmlElement the jaxbXmlElement to set
     */
    @Override
    public void setJaxbXmlElement(JaxbXmlElement jaxbXmlElement) {
        this.jaxbXmlElement = jaxbXmlElement;
    }

    /**
     * @return the jaxbXmlElementList
     */
    @Override
    public List<JaxbXmlElement> getJaxbXmlElementList() {
        return jaxbXmlElementList;
    }

    /**
     * @param jaxbXmlElementList the jaxbXmlElementList to set
     */
    @Override
    public void setJaxbXmlElementList(List<JaxbXmlElement> jaxbXmlElementList) {
        this.jaxbXmlElementList = jaxbXmlElementList;
    }

//    /**
//     * @return the jaxbXmlList
//     */
//    public Boolean getJaxbXmlList() {
//        return jaxbXmlList;
//    }
//
//    /**
//     * @param jaxbXmlList the jaxbXmlList to set
//     */
//    public void setJaxbXmlList(Boolean jaxbXmlList) {
//        this.jaxbXmlList = jaxbXmlList;
//    }
    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        return Arrays.asList(JaxbVariableType.values());
    }

    @XmlTransient
    private BaseAttributes attributes;

    public JavaClass getJavaClass() {
        return attributes.getJavaClass();
    }

    public void setAttributes(BaseAttributes attributes) {
        this.attributes = attributes;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent instanceof BaseAttributes) {
            setAttributes((BaseAttributes) parent);
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Used to get data type title to display in ui component e.g Set<String>, Integer, List<Entity> etc.
     * 
     */
    public abstract String getDataTypeLabel();

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     * @return the includeInUI
     */
    public Boolean getIncludeInUI() {
        if(includeInUI==null){
            return true;
        }
        return includeInUI;
    }

    /**
     * @param includeInUI the includeInUI to set
     */
    public void setIncludeInUI(Boolean includeInUI) {
        this.includeInUI = includeInUI;
    }
    
    
}
