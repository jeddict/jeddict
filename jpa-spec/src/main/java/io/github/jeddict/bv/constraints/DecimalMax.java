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

import static java.lang.Boolean.FALSE;
import javax.lang.model.element.AnnotationMirror;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name = "dma")
public class DecimalMax extends Constraint {

    @XmlAttribute(name = "v")
    private String value;
    
    @XmlAttribute(name = "i")
    private Boolean inclusive = true;

    @Override
    public void load(AnnotationMirror annotationMirror) {
        super.load(annotationMirror);
        this.value = JavaSourceParserUtil.findAnnotationValueAsString(annotationMirror, "value");
        this.inclusive = (Boolean)JavaSourceParserUtil.findAnnotationValue(annotationMirror, "inclusive");
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(value) && !FALSE.equals(inclusive);
    }
    
    @Override
    protected void clearConstraint(){
        value = null;
        inclusive = null;
    }

    /**
     * @return the inclusive
     */
    public Boolean getInclusive() {
        if(inclusive == null){
            return true;
        }
        return inclusive;
    }

    /**
     * @param inclusive the inclusive to set
     */
    public void setInclusive(Boolean inclusive) {
        this.inclusive = inclusive;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
