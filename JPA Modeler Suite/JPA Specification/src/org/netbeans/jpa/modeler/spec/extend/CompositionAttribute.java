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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class CompositionAttribute extends BaseAttribute implements AccessTypeHandler {

    @XmlAttribute(name = "connected-class-id", required = true)
    private String connectedClassId;

    /**
     * @return the connectedClassId
     */
    public String getConnectedClassId() {
        return connectedClassId;
    }

    /**
     * @param connectedClassId the connectedClassId to set
     */
    public void setConnectedClassId(String connectedClassId) {
        this.connectedClassId = connectedClassId;
    }

    
    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<JaxbVariableType>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        return jaxbVariableTypeList;
    }
}
