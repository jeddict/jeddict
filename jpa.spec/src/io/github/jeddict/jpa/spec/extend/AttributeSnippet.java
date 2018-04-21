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

/**
 *
 * @author jGauravGupta
 */
public class AttributeSnippet extends Snippet<AttributeSnippetLocationType> {

    @XmlAttribute(name="loc")
    protected AttributeSnippetLocationType locationType;
    /**
     * @return the locationType
     */
    @Override
    public AttributeSnippetLocationType getLocationType() {
        if (locationType == null) {
            return AttributeSnippetLocationType.AFTER_FIELD;
        }
        return locationType;
    }

    /**
     * @param locationType the locationType to set
     */
    @Override
    public void setLocationType(AttributeSnippetLocationType locationType) {
        if (locationType == AttributeSnippetLocationType.AFTER_FIELD) {
            locationType = null;
        }
        this.locationType = locationType;
    }
}
