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

import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.JoinTable;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class RelationAttribute extends Attribute implements AccessTypeHandler, FetchTypeHandler {

    @XmlAttribute(name = "connected-entity-id", required = true)
    private String connectedEntityId;
    @XmlAttribute(name = "connected-attribute-id", required = true)
    private String connectedAttributeId;

    /**
     * @return the connectedEntityId
     */
    public String getConnectedEntityId() {
        return connectedEntityId;
    }

    /**
     * @param connectedEntityId the connectedEntityId to set
     */
    public void setConnectedEntityId(String connectedEntityId) {
        this.connectedEntityId = connectedEntityId;
    }

    /**
     * @return the connectedAttributeId
     */
    public String getConnectedAttributeId() {
        return connectedAttributeId;
    }

    /**
     * @param connectedAttributeId the connectedAttributeId to set
     */
    public void setConnectedAttributeId(String connectedAttributeId) {
        this.connectedAttributeId = connectedAttributeId;
    }

    public abstract CascadeType getCascade();

    public abstract void setCascade(CascadeType value);

    public abstract String getTargetEntity();

    public abstract void setTargetEntity(String value);

    public abstract JoinTable getJoinTable();

    public abstract void setJoinTable(JoinTable value);

}
