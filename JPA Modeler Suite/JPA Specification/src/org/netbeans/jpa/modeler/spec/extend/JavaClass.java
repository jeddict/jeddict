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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class JavaClass extends FlowNode {

    @XmlAttribute
    private String superclassId;
    @XmlTransient
    private JavaClass superclass;
    @XmlTransient
    private Set<JavaClass> subclassList;

    @XmlAttribute
    private boolean visibile = true;

    private List<String> annotation;

    public abstract String getClazz();

    public abstract void setClazz(String value);

    public abstract IAttributes getAttributes();

    public abstract void setAttributes(IAttributes attributes);

    /**
     * @return the annotation
     */
    public List<String> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<String>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public void addAnnotation(String annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<String>();
        }
        this.annotation.add(annotation_In);
    }

    public void removeAnnotation(String annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<String>();
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
     * @return the superclassRef
     */
    public JavaClass getSuperclass() {
        return superclass;
    }

    /**
     * @param superclassRef the superclassRef to set
     */
    public void addSuperclass(JavaClass superclassRef) {
        if(this.superclass==superclassRef){
            return;
        }
        if(this.superclass != null){
            throw new RuntimeException("JavaClass.addSuperclass > superclass is already exist [remove it first to add the new one]");
        }
        this.superclass = superclassRef;
        if (this.superclass != null) {
            this.superclassId = this.superclass.getId();
            this.superclass.addSubclass(this);
        } else {
            throw new RuntimeException("JavaClass.addSuperclass > superclassRef is null");
        }
    }
    public void removeSuperclass(JavaClass superclassRef) {
        
        if (superclassRef != null) {
            superclassRef.removeSubclass(this);
        } else {
            throw new RuntimeException("JavaClass.removeSuperclass > superclassRef is null");
        }
        this.superclassId = null;
        this.superclass = null;
    }

    /**
     * @return the subclassList
     */
    public Set<JavaClass> getSubclassList() {
        return subclassList;
    }

    /**
     * @param subclassList the subclassList to set
     */
    public void setSubclassList(Set<JavaClass> subclassList) {
        if(this.subclassList == null){
            this.subclassList = new HashSet<JavaClass>();
        }
        this.subclassList = subclassList;
    }
    
    public void addSubclass(JavaClass subclass) {
        if(this.subclassList == null){
            this.subclassList = new HashSet<JavaClass>();
        }
        this.subclassList.add(subclass);
    }
    public void removeSubclass(JavaClass subclass) {
        if(this.subclassList == null){
            this.subclassList = new HashSet<JavaClass>();
        }
        this.subclassList.remove(subclass);
    }

    /**
     * @return the superclassId
     */
    public String getSuperclassId() {
        return superclassId;
    }

}
