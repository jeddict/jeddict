/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec;

import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;

public abstract class IdentifiableClass extends ManagedClass implements PrimaryKeyContainer {
    @XmlAttribute(name="jaxb-root-element")
 private Boolean xmlRootElement = false;

    /**
     * @return the xmlRootElement
     */
    public Boolean getXmlRootElement() {
        return xmlRootElement;
    }

    /**
     * @param xmlRootElement the xmlRootElement to set
     */
    public void setXmlRootElement(Boolean xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }
    
    
    
}
