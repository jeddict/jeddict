/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import io.github.jeddict.util.StringUtils;

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
    public void load(AnnotationExplorer annotation) {
        super.load(annotation);
        annotation.getString("regexp").ifPresent(this::setRegexp);
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
