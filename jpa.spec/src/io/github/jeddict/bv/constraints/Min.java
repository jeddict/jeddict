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

import javax.lang.model.element.AnnotationMirror;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import io.github.jeddict.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name = "mi")
public class Min extends Constraint {

    @XmlAttribute(name = "v")
    private Long value;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public void load(AnnotationMirror annotationMirror) {
        super.load(annotationMirror);
        this.value = (Long) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "value");
    }

    @Override
    public boolean isEmpty(){
        return value == null;
    }
    
    @Override
    protected void clearConstraint(){
        value = null;
    }
}
