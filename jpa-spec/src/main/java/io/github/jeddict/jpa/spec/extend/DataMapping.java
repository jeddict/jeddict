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
package io.github.jeddict.jpa.spec.extend;

import java.util.regex.Pattern;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import io.github.jeddict.util.StringUtils;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.settings.diagram.ClassDiagramSettings;
import static io.github.jeddict.jpa.spec.NamedQuery.FIND_BY;
import jakarta.xml.bind.annotation.XmlElement;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataMapping {

    
    @XmlAttribute(name = "e")
    private boolean enable = true;
    @XmlAttribute(required = true)
    protected String name;
    
    @XmlElement(name="desc")
    protected String description;

    public boolean refractorName(String prevName, String newName) {
        if (ClassDiagramSettings.isRefractorQuery()) {
            if (StringUtils.containsIgnoreCase(this.getName(), FIND_BY + prevName)) {
                this.setName(this.getName().replaceAll("\\b(?i)" + Pattern.quote(FIND_BY + prevName) + "\\b", FIND_BY + StringHelper.firstUpper(newName)));
                return true;
            } else if (StringUtils.containsIgnoreCase(this.getName(), prevName)) {
                this.setName(this.getName().replaceAll("\\b(?i)" + Pattern.quote(prevName) + "\\b", newName));
                return true;
            }
        }
        return false;
    }
    
     /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }


    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable the enable to set
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    
        /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }
}
