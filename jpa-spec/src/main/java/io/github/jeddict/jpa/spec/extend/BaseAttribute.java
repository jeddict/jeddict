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
package io.github.jeddict.jpa.spec.extend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import io.github.jeddict.bv.constraints.Constraint;
import static io.github.jeddict.jcode.util.AttributeType.getArrayType;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import io.github.jeddict.jaxb.spec.JaxbVariableType;

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
        jaxbVariableTypeList.add(JaxbVariableType.XML_DEFAULT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ATTRIBUTE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_VALUE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_REF);
        return jaxbVariableTypeList;
    }
    
    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }

    @Override
    public Set<Class<? extends Constraint>> getAttributeConstraintsClass() {
//        if(isOptionalReturnType()){
//            return Collections.EMPTY_SET;
//        }
        return getConstraintsClass(getAttributeType());
    }
    
    @Override
    public Set<Class<? extends Constraint>> getValueConstraintsClass() {
        if (isArray(getAttributeType())) {
            return getConstraintsClass(getArrayType(getAttributeType()));
        } else {
            return Collections.EMPTY_SET;
        }
    }
    

    public boolean isTextAttributeType() {
        return isTextAttributeType(getAttributeType());
    }
    
    public boolean isBlobAttributeType() {
        return isBlobAttributeType(getAttributeType());
    }
    
    public boolean isPrecisionAttributeType() {
        return isPrecisionAttributeType(getAttributeType());
    }
    
    public boolean isScaleAttributeType() {
        return isScaleAttributeType(getAttributeType());
    }
    

}
