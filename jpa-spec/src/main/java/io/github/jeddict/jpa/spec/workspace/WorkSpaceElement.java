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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.github.jeddict.jpa.spec.extend.Attribute;
import org.netbeans.modeler.widget.design.PinTextDesign;

/**
 *
 * @author jGauravGupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = WorkSpaceElementValidator.class)
public class WorkSpaceElement {
    
    @XmlAttribute(name = "ref")
    @XmlIDREF
    private Attribute attribute;

    @XmlElement(name="v")
    private PinTextDesign textDesign;
    
    @XmlElement(name="jbv")
    private PinTextDesign jsonbTextDesign;
    
    public WorkSpaceElement() {
    }

    public WorkSpaceElement(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public WorkSpaceElement(Attribute attribute, PinTextDesign textDesign) {
        this.attribute = attribute;
        this.textDesign = textDesign;
    }
    
    public WorkSpaceElement(Attribute attribute, PinTextDesign textDesign, PinTextDesign jsonbTextDesign) {
        this.attribute = attribute;
        this.textDesign = textDesign;
        this.jsonbTextDesign = jsonbTextDesign;
    }
    
    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the textDesign
     */
    public PinTextDesign getTextDesign() {
        if(textDesign == null){
            textDesign = new PinTextDesign(); 
        }
        return textDesign;
    }

    /**
     * @param textDesign the textDesign to set
     */
    public void setTextDesign(PinTextDesign textDesign) {
        this.textDesign = textDesign;
    }

    /**
     * @return the jsonbTextDesign
     */
    public PinTextDesign getJsonbTextDesign() {
        if(jsonbTextDesign == null){
            jsonbTextDesign = new PinTextDesign(); 
        }
        return jsonbTextDesign;
    }

    /**
     * @param jsonbTextDesign the jsonbTextDesign to set
     */
    public void setJsonbTextDesign(PinTextDesign jsonbTextDesign) {
        this.jsonbTextDesign = jsonbTextDesign;
    }
    
}
