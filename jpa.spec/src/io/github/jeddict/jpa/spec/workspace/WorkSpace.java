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
package io.github.jeddict.jpa.spec.workspace;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import io.github.jeddict.jpa.spec.extend.BaseElement;
import io.github.jeddict.jpa.spec.extend.JavaClass;

/**
 *
 * @author jGauravGupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkSpace extends BaseElement {
    
    @XmlAttribute(name="n")
    private String name;
    
    @XmlAttribute(name="t")
    private WorkSpaceType type;
    
    @XmlElement(name="i")
    private Set<WorkSpaceItem> items;

    public WorkSpace() {
    }

    public WorkSpace(Set<WorkSpaceItem> items) {
        this.items = items;
    }
    
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public WorkSpaceType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(WorkSpaceType type) {
        this.type = type;
    }

    /**
     * @return the items
     */
    public Set<WorkSpaceItem> getItems() {
        if(items == null) {
            items = new HashSet<>();
        }
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(Set<WorkSpaceItem> items) {
        this.items = items;
    }

    public void addItem(WorkSpaceItem e) {
        getItems().add(e);
    }

    public void removeItem(WorkSpaceItem o) {
        getItems().remove(o);
    }
    
    public boolean hasItem(JavaClass javaClass){
        return getItems().contains(new WorkSpaceItem(javaClass));
    }

    @Override
    public int hashCode() {
        return getItems().size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkSpace other = (WorkSpace) obj;
        if (!getItems().equals(other.getItems())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
        
}
