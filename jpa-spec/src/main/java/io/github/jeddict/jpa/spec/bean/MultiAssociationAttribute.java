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
package io.github.jeddict.jpa.spec.bean;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.bv.constraints.Constraint;
import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class MultiAssociationAttribute extends AssociationAttribute implements CollectionTypeHandler {

    @XmlAttribute(name = "own")
    private Boolean owner;
    @XmlTransient//(name = "mapped-by")
    protected String mappedBy;
    @XmlAttribute(name = "collection-type")
    private String collectionType;
    @XmlAttribute(name = "cit")
    private String collectionImplType;

    /**
     * Gets the value of the mappedBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMappedBy() {
        if (Boolean.FALSE.equals(isOwner())) {
            if (mappedBy != null) {
                return mappedBy;
            }
            if (getConnectedAttribute() != null) {
                return getConnectedAttribute().getName();
            }
        }
        return null;
    }

    /**
     * Sets the value of the mappedBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMappedBy(String value) {
        this.mappedBy = value;
        this.owner = StringUtils.isBlank(mappedBy);
    }

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

    /**
     * @return the owner
     */
    @Override
    public boolean isOwner() {
        if (owner == null) {
            return Boolean.FALSE;
        }
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    @Override
    public void setOwner(boolean owner) {
        this.owner = owner;
        if (owner) {
            mappedBy = null;
        }

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
//        if(!isMap(getCollectionType())){
        return Collections.EMPTY_SET;
//        }
//        return getConstraintsClass(getMapKeyDataTypeLabel());
    }

    @Override
    public Set<Class<? extends Constraint>> getValueConstraintsClass() {
        return getConstraintsClass(null);
    }

}
