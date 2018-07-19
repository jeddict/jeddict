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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name = "pt")
public class Pattern extends Constraint {

    @XmlAttribute(name = "r")
    private String regexp;

    @XmlElement(name = "f")
    private String flags;
    
    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public void load(AnnotationMirror annotationMirror) {
        super.load(annotationMirror);
        this.regexp = JavaSourceParserUtil.findAnnotationValueAsString(annotationMirror, "regexp");
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(regexp);
    }

    @Override
    protected void clearConstraint() {
        regexp = null;
    }

    /**
     * @return the flags
     */
    public String getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(String flags) {
        this.flags = flags;
    }
}
