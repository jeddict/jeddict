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

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;
import io.github.jeddict.source.JCRELoader;

/**
 *
 * @author jGauravGupta
 */
public interface IAttributes extends JCRELoader {

    List<Attribute> findAllAttribute(String name);
    
    List<Attribute> findAllAttribute(String name,boolean includeParentClassAttibute);
    
    List<Attribute> getAllAttribute();
    
    List<Attribute> getAllAttribute(boolean includeParentClassAttibute);
    
    boolean isAttributeExist(String name);

    Set<String> getConnectedClass();
    
    Set<String> getConnectedClass(Set<String> javaClasses);

    void notifyListeners(Object source, String property, String oldValue, String newValue);

    void addChangeListener(PropertyChangeListener newListener);

    void removeChangeListener(PropertyChangeListener newListener);
            
    JavaClass getJavaClass();

    List<Attribute> getAllSortedAttribute();
    
    List<Attribute> getAllSortedAttribute(boolean includeParentClassAttibute);

}
