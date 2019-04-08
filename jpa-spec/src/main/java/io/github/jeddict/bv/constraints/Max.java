/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
@XmlRootElement(name = "ma")
public class Max extends Constraint {

    @XmlAttribute(name = "v")
    private String value;

    @Override
    public void load(AnnotationMirror annotation) {
        super.load(annotation);
        this.value = (String) JavaSourceParserUtil.findAnnotationValue(annotation, "value");
    }

    @Override
    public void load(AnnotationExplorer annotation) {
        super.load(annotation);
        annotation.getString("value").ifPresent(this::setValue);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    protected void clearConstraint() {
        value = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
