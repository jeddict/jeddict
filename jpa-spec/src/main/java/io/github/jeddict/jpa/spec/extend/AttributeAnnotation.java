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

import javax.xml.bind.annotation.XmlAttribute;
import io.github.jeddict.jpa.spec.extend.annotation.Annotation;

/**
 *
 * @author jGauravGupta
 */
public class AttributeAnnotation extends Annotation<AttributeAnnotationLocationType> {

    @XmlAttribute(name="loc")
    protected AttributeAnnotationLocationType locationType;
    
    public AttributeAnnotation() {
    }

    public AttributeAnnotation(String name) {
        super(name);
    }
    
    /**
     * @return the locationType
     */
    @Override
    public AttributeAnnotationLocationType getLocationType() {
        if (locationType == null) {
            return AttributeAnnotationLocationType.PROPERTY;
        }
        return locationType;
    }

    /**
     * @param locationType the locationType to set
     */
    @Override
    public void setLocationType(AttributeAnnotationLocationType locationType) {
        if (locationType == AttributeAnnotationLocationType.PROPERTY) {
            locationType = null;
        }
        this.locationType = locationType;
    }
}
