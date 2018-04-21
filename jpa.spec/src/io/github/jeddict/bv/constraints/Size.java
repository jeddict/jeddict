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
@XmlRootElement(name = "si")
public class Size extends Constraint {

    @XmlAttribute(name = "mi")
    private Integer min;

    @XmlAttribute(name = "ma")
    private Integer max;

    /**
     * @return the min
     */
    public Integer getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public Integer getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public void load(AnnotationMirror annotationMirror) {
        super.load(annotationMirror);
        this.min = (Integer) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "min");
        this.max = (Integer) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "max");
    }

    @Override
    public boolean isEmpty() {
        return min == null && max == null;
    }

    @Override
    protected void clearConstraint() {
        min = null;
        max = null;
    }
}
