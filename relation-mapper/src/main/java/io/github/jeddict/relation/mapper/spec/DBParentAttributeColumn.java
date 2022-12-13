/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.spec;

import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.extend.Attribute;

public class DBParentAttributeColumn extends DBParentColumn<Attribute> {

    private AttributeOverride attributeOverride;

    public DBParentAttributeColumn(String name, Entity intrinsicClass, Attribute managedAttribute) {
        super(name, intrinsicClass, managedAttribute);
        if (managedAttribute instanceof Id || managedAttribute instanceof Basic 
                || managedAttribute instanceof Version ||  managedAttribute instanceof ElementCollection) {
            attributeOverride = intrinsicClass.findAttributeOverride(getKeyName());
            if (attributeOverride == null) {
                attributeOverride = new AttributeOverride();
                attributeOverride.setName(getKeyName());
                intrinsicClass.addAttributeOverride(attributeOverride);
            }
        } else {
            throw new IllegalStateException(managedAttribute + " not supported.");
        }
    }

    /**
     * @return the attributeOverride
     */
    public AttributeOverride getAttributeOverride() {
        return attributeOverride;
    }

}
