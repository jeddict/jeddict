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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.validation.constraints.Constraints;
import org.netbeans.jpa.modeler.spec.validation.constraints.Max;
import org.netbeans.jpa.modeler.spec.validation.constraints.Min;
import org.netbeans.jpa.modeler.spec.validation.constraints.NotNull;
import org.netbeans.jpa.modeler.spec.validation.constraints.Size;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseAttribute extends Attribute {

    /**
     * @return the attributeType
     */
    public abstract String getAttributeType();

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_ATTRIBUTE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_VALUE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        return jaxbVariableTypeList;
    }
    
    @XmlElementWrapper(name = "bv")
    @XmlElements({
        @XmlElement(name="nn", type = NotNull.class),
        @XmlElement(name="si", type = Size.class),
        @XmlElement(name="ma", type = Max.class),
        @XmlElement(name="mi", type = Min.class)
    })
    private List<Constraints> constraints;

    
        /**
     * @return the constraints
     */
    public List<Constraints> getConstraints() {
        if(constraints == null){
            constraints = new ArrayList<>();
        }
        return constraints;
    }
    
    public Map<Class<? extends Constraints>,Constraints> getConstraintsMap() {
        Map<Class<? extends Constraints>,Constraints> constraintsMap = new HashMap<>();
        if(constraints == null){
            constraints = new ArrayList<>();
        }
        for(Constraints constraint :constraints){
            constraintsMap.put(constraint.getClass(), constraint);
        }
        return constraintsMap;
    }
    
   public List<Constraints> getNewConstraints() {
//       List<Constraints> savedConstraints = constraints;
        Map<Class<? extends Constraints>,Constraints> constraintsMap = getConstraintsMap();
        constraints = new ArrayList<>();
        List<Class<? extends Constraints>> classes = getConstraintsClass();
        for(Class<? extends Constraints> constraintClass : classes){
           Constraints constraint = constraintsMap.get(constraintClass);
           if(constraint!=null){
               constraints.add(constraint);
               constraint.setSelected(Boolean.TRUE);
           } else {
               try {
                   constraints.add(constraintClass.newInstance());
               } catch (InstantiationException | IllegalAccessException ex) {
                   Exceptions.printStackTrace(ex);
               }
           }
        }
        return constraints;
    }
   
   public List<Class<? extends Constraints>> getConstraintsClass(){
       List<Class<? extends Constraints>> classes = new ArrayList<>();
       classes.add(NotNull.class);

       if("String".equals(getAttributeType())){
           classes.add(Size.class);
       } else {
           
       }
           return classes;
   }
   
   
   

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(List<Constraints> constraints) {
        this.constraints = constraints;
    }

    public boolean add(Constraints constraints) {
        return getConstraints().add(constraints);
    }

    public boolean remove(Constraints constraints) {
        return getConstraints().remove(constraints);
    }

}
