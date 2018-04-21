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
package io.github.jeddict.bv.constraints;

import java.util.Objects;
import javax.lang.model.element.AnnotationMirror;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.github.jeddict.source.JCREBVLoader;
import io.github.jeddict.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = ConstraintsValidator.class)
public abstract class Constraint implements JCREBVLoader {

    @XmlTransient
    private Boolean selected = false;

    @XmlAttribute(name = "m")
    private String message;

    public abstract boolean isEmpty();
    
    protected abstract void clearConstraint();
    
    public void clear(){ 
        selected = false;
        message = null;
        clearConstraint();
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        setSelected(true);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getClass().getSimpleName());
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
        if(!this.getClass().getSimpleName().equals(obj.getClass().getSimpleName())) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public void load(AnnotationMirror annotationMirror) {
        this.setSelected(true);
        this.message = JavaSourceParserUtil.findAnnotationValueAsString(annotationMirror, "message");
    }
    
    
}
