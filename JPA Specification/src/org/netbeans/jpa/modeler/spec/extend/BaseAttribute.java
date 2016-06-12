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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.BIGINTEGER;
import static org.netbeans.jcode.core.util.AttributeType.BOOLEAN;
import static org.netbeans.jcode.core.util.AttributeType.BYTE;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DATE;
import static org.netbeans.jcode.core.util.AttributeType.INT;
import static org.netbeans.jcode.core.util.AttributeType.INT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.LONG_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.SHORT;
import static org.netbeans.jcode.core.util.AttributeType.SHORT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.validation.constraints.AssertFalse;
import org.netbeans.jpa.modeler.spec.validation.constraints.AssertTrue;
import org.netbeans.jpa.modeler.spec.validation.constraints.Constraint;
import org.netbeans.jpa.modeler.spec.validation.constraints.DecimalMax;
import org.netbeans.jpa.modeler.spec.validation.constraints.DecimalMin;
import org.netbeans.jpa.modeler.spec.validation.constraints.Digits;
import org.netbeans.jpa.modeler.spec.validation.constraints.Future;
import org.netbeans.jpa.modeler.spec.validation.constraints.Max;
import org.netbeans.jpa.modeler.spec.validation.constraints.Min;
import org.netbeans.jpa.modeler.spec.validation.constraints.NotNull;
import org.netbeans.jpa.modeler.spec.validation.constraints.Null;
import org.netbeans.jpa.modeler.spec.validation.constraints.Past;
import org.netbeans.jpa.modeler.spec.validation.constraints.Pattern;
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
        @XmlElement(name = "nu", type = Null.class),
        @XmlElement(name = "nn", type = NotNull.class),
        @XmlElement(name = "af", type = AssertFalse.class),
        @XmlElement(name = "at", type = AssertTrue.class),
        @XmlElement(name = "pa", type = Past.class),
        @XmlElement(name = "fu", type = Future.class),
        @XmlElement(name = "si", type = Size.class),
        @XmlElement(name = "pt", type = Pattern.class),
        @XmlElement(name = "mi", type = Min.class),
        @XmlElement(name = "ma", type = Max.class),
        @XmlElement(name = "dmi", type = DecimalMin.class),
        @XmlElement(name = "dma", type = DecimalMax.class),
        @XmlElement(name = "di", type = Digits.class)
    })
    private Set<Constraint> constraints;

    /**
     * @return the constraints
     */
    public Set<Constraint> getConstraints() {
        if (constraints == null) {
            constraints = new LinkedHashSet<>();
        }
        return constraints;
    }

    @XmlTransient
    Map<String, Constraint> constraintsMap;
    
    public Map<String, Constraint> getConstraintsMap() {
        if (constraintsMap == null) {
            constraintsMap = new HashMap<>();
            getConstraints().stream().forEach((constraint) -> {
                constraintsMap.put(constraint.getClass().getSimpleName(), constraint);
            });
        }        

        return constraintsMap;
        
    }

    public Set<Constraint> getNewConstraints() {
      Set<Constraint> newConstraints = new LinkedHashSet<>();
        List<Class<? extends Constraint>> classes = getConstraintsClass();
        Map<String, Constraint> constraintsMapTmp = getConstraintsMap();
        for (Class<? extends Constraint> constraintClass : classes) {
            Constraint constraint = constraintsMapTmp.get(constraintClass.getSimpleName());
            if (constraint != null) {
                newConstraints.add(constraint);
            } else {
                try {
                    newConstraints.add(constraintClass.newInstance());
                } catch (InstantiationException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        this.constraintsMap = null;//reset
        this.constraints = newConstraints;
        return newConstraints;
    }

    public List<Class<? extends Constraint>> getConstraintsClass() {
        List<Class<? extends Constraint>> classes = new ArrayList<>();
        classes.add(NotNull.class);
        classes.add(Null.class);
        if (StringUtils.isNotBlank(getAttributeType())) {
            switch (getAttributeType()) {
                case BOOLEAN:
                    classes.add(AssertTrue.class);
                    classes.add(AssertFalse.class);
                    break;
                case STRING:
                    classes.add(Size.class);//array, collection, map pending
                    classes.add(Pattern.class);
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
                case CALENDAR:
                case DATE:
                    classes.add(Past.class);
                    classes.add(Future.class);
                    break;
                case BIGDECIMAL:
                case BIGINTEGER:
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                case BYTE_WRAPPER:
                case SHORT_WRAPPER:
                case INT_WRAPPER:
                case LONG_WRAPPER:
                    classes.add(Min.class);
                    classes.add(Max.class);
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
            }
        }
        return classes;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(Set<Constraint> constraints) {
        this.constraints = constraints;
    }

    public boolean add(Constraint constraints) {
        return getConstraints().add(constraints);
    }

    public boolean remove(Constraint constraints) {
        return getConstraints().remove(constraints);
    }
    
    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }

}
