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
package io.github.jeddict.jpa.spec;

import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.source.ClassExplorer;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;

public abstract class ManagedClass<T extends IPersistenceAttributes> extends JavaClass<T> {

    @XmlAttribute
    protected AccessType access;
    
    @XmlAttribute
    protected Boolean noSQL;

    @Override
    public void load(ClassExplorer clazz) {
        super.load(clazz);
        this.getAttributes().load(clazz);
        this.access = AccessType.load(clazz);
    }

    /**
     * Gets the value of the access property.
     *
     * @return possible object is {@link AccessType }
     *
     */
    public AccessType getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     *
     * @param value allowed object is {@link AccessType }
     *
     */
    public void setAccess(AccessType value) {
        this.access = value;
    }

    public Set<String> getAllConvert(){
        return getAttributes().getAllConvert();
    }

    public Boolean getNoSQL() {
        if (noSQL == null) {
            return false;
        }
        return noSQL;
    }

    public void setNoSQL(Boolean noSQL) {
        this.noSQL = noSQL;
    }

}
