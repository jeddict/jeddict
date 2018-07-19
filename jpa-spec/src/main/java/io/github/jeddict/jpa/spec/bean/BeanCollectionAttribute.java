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

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;

public class BeanCollectionAttribute extends BeanAttribute implements CollectionTypeHandler {

    @XmlAttribute(name = "ct")
    private String collectionType;
    @XmlAttribute(name = "cit")
    private String collectionImplType;

    public BeanCollectionAttribute() {
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
     * @param collectionImplType the collectionImplementationType to set
     */
    @Override
    public void setCollectionImplType(String collectionImplType) {
        this.collectionImplType = collectionImplType;
    }

}
