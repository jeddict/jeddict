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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Attributes<T extends JavaClass> implements IAttributes {
    
    @XmlTransient
    private T _class;
    
    private final transient List<PropertyChangeListener> listener = new ArrayList<>();

    
    @Override
    public List<Attribute> findAllAttribute(String name) {
        return findAllAttribute(name,false);
    }
    @Override
    public List<Attribute> findAllAttribute(String name,boolean includeParentClassAttibute) {
        List<Attribute> attributes = new ArrayList<>();
        if(includeParentClassAttibute && this.getJavaClass().getSuperclass()!=null){
            attributes.addAll(this.getJavaClass().getSuperclass().getAttributes().findAllAttribute(name,true));
        }
        for (Attribute attribute : getAllAttribute(false)) {
            if (attribute.getName() != null && attribute.getName().equals(name)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    @Override
    public List<Attribute> getAllSortedAttribute() {
        return getAllSortedAttribute(false);
    }
    
    @Override
    public List<Attribute> getAllSortedAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = getAllAttribute(includeParentClassAttibute);
        attributes.sort(new AttributeLocationComparator());
        return attributes;
    }
    
    @Override
    public List<Attribute> getAllAttribute() {
        return getAllAttribute(false);
    }
    @Override
    public List<Attribute> getAllAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = new ArrayList<>();
        if(includeParentClassAttibute && this.getJavaClass().getSuperclass()!=null){
            attributes.addAll(this.getJavaClass().getSuperclass().getAttributes().getAllAttribute(true));
        }
        return attributes;
    }
    
    @Override
    public boolean isAttributeExist(String name) {
        //check from parent entities
        if (this.getJavaClass().getSuperclass() != null) {
            if (this.getJavaClass().getSuperclass().getAttributes().isAttributeExist(name)) {
                return true;
            }
        }
        return getAllAttribute(false)
                .stream()
                .filter(attr -> attr.getName() != null)
                .anyMatch(attr -> attr.getName().equals(name));
    }
    
    @Override
    public Set<String> getConnectedClass(){
        return getConnectedClass(new HashSet<>());
    }
    
    @Override
    public Set<String> getConnectedClass(Set<String> javaClasses){
        return javaClasses;
    }
    
    @Override
    public T getJavaClass() {
        return _class;
    }

    public void setJavaClass(T _class) {
        this._class = _class;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        setJavaClass((T) parent);
    }
    
    @Override
    public void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener propertyChangeListener : listener) {
            propertyChangeListener.propertyChange(new PropertyChangeEvent(object, property, oldValue, newValue));
        }
    }

    @Override
    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    @Override
    public void removeChangeListener(PropertyChangeListener newListener) {
        listener.remove(newListener);
    }

}