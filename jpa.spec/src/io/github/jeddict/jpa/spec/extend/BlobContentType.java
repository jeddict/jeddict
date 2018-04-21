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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import org.netbeans.modeler.properties.type.Enumy;

/**
 *
 * @author jGauravGupta
 */
@XmlType(name = "binary-type")
@XmlEnum
public enum BlobContentType implements Enumy {
    @XmlEnumValue("")
    NONE("< None >", null),
    @XmlEnumValue("I")
    IMAGE("Image", "image"),
    @XmlEnumValue("T")
    TEXT("Text", "text"),
    @XmlEnumValue("A")
    ANY("Any", "any");
    
    private final String display, value;

    private BlobContentType(String display, String value) {
        this.display = display;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public Enumy getDefault() {
        return NONE;
    }
}
