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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;

/**
 *
 * @author Gaurav Gupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseElement implements IBaseElement {

    @XmlAttribute
    @XmlIDREF
    private EntityMappings rootElement;

    @XmlAttribute
    @XmlID
    protected String id;

    protected ExtensionElements extensionElement;

    @XmlTransient
    private IBaseElement orignalObject;

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     *
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     *
     */
    @Override
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public Map<String, String> getCustomAttributes() {
        return null;
    }

    @Override
    public void setCustomAttributes(Map customAttributes) {
    }

    /**
     * @return the extensionElement
     */
    public ExtensionElements getExtensionElement() {
        return extensionElement;
    }

    /**
     * @param extensionElement the extensionElement to set
     */
    public void setExtensionElement(ExtensionElements extensionElement) {
        this.extensionElement = extensionElement;
    }

    private final transient List<PropertyChangeListener> listener = new ArrayList<>();

    protected void notifyListeners(String property, String oldValue, String newValue) {
        for (PropertyChangeListener propertyChangeListener : listener) {
            propertyChangeListener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    public void removeChangeListener(PropertyChangeListener newListener) {
        listener.remove(newListener);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseElement other = (BaseElement) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public EntityMappings getRootElement() {
        return this.rootElement;
    }

    @Override
    public void setRootElement(Object rootElement) {
        this.rootElement = (EntityMappings) rootElement;
    }

    /**
     * @return the orignalObject
     */
    public IBaseElement getOrignalObject() {
        return orignalObject;
    }

    /**
     * @param orignalObject the orignalObject to set
     */
    public void setOrignalObject(IBaseElement orignalObject) {
        this.orignalObject = orignalObject;
    }

    
}
