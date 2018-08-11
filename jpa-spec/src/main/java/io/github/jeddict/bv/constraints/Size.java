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

import io.github.jeddict.source.AnnotationExplorer;
import io.github.jeddict.source.JavaSourceParserUtil;
import javax.lang.model.element.AnnotationMirror;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name = "si")
public class Size extends Constraint {

    @XmlAttribute(name = "mi")
    private String min;

    @XmlAttribute(name = "ma")
    private String max;

    /**
     * @return the min
     */
    public String getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public String getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(String max) {
        this.max = max;
    }

    @Override
    public void load(AnnotationMirror annotation) {
        super.load(annotation);
        this.min = (String) JavaSourceParserUtil.findAnnotationValue(annotation, "min");
        this.max = (String) JavaSourceParserUtil.findAnnotationValue(annotation, "max");
    }

    @Override
    public void load(AnnotationExplorer annotation) {
        super.load(annotation);
        annotation.getString("min").ifPresent(this::setMin);
        annotation.getString("max").ifPresent(this::setMax);
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
