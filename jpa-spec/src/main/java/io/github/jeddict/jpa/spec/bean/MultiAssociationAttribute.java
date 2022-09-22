/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.spec.bean;

import io.github.jeddict.bv.constraints.Constraint;
import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class MultiAssociationAttribute extends AssociationAttribute implements CollectionTypeHandler {

    @XmlAttribute(name = "collection-type")
    private String collectionType;
    @XmlAttribute(name = "cit")
    private String collectionImplType;

    /**
     * @return the collectionType
     */
    @Override
    public String getCollectionType() {
        if (collectionType == null) {
            collectionType = List.class.getName();
        }
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    @Override
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * @return the collectionImplType
     */
    @Override
    public String getCollectionImplType() {
        return collectionImplType;
    }

    /**
     * @param collectionImplType the collectionImplType to set
     */
    @Override
    public void setCollectionImplType(String collectionImplType) {
        this.collectionImplType = collectionImplType;
    }

    @Override
    public String getDataTypeLabel() {
        return String.format("%s<%s>", getCollectionType(), getTargetClass());
    }

    @Override
    public Set<Class<? extends Constraint>> getAttributeConstraintsClass() {
        Set<Class<? extends Constraint>> classes = getCollectionTypeConstraintsClass();
        classes.add(Size.class);
        return classes;
    }

    @Override
    public Set<Class<? extends Constraint>> getKeyConstraintsClass() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<Class<? extends Constraint>> getValueConstraintsClass() {
        return getConstraintsClass(null);
    }

}
