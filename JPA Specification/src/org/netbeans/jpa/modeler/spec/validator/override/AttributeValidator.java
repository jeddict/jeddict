/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.validator.override;

import java.util.Arrays;
import java.util.Optional;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.validator.MarshalValidator;
import org.netbeans.jpa.modeler.spec.validator.column.ColumnValidator;

public class AttributeValidator extends MarshalValidator<AttributeOverride> {

    @Override
    public AttributeOverride marshal(AttributeOverride attributeOverride) throws Exception {
        if (attributeOverride != null && isEmpty(attributeOverride)) {
            return null;
        }
        return attributeOverride;
    }

    public static boolean isEmpty(AttributeOverride attributeOverride) {
        return ColumnValidator.isEmpty(attributeOverride.getColumn());
    }

    public static void filter(Embedded embedded) {
        embedded.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), embedded.getConnectedClass())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }
    
    public static void filter(ElementCollection elementCollection) {
        elementCollection.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), elementCollection.getConnectedClass())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }

    static boolean isExist(String[] keys, Embeddable embeddable) {
        if (keys.length > 1) {
            Optional<Embedded> embeddedOptional = embeddable.getAttributes().getEmbedded().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (embeddedOptional.isPresent()) {
                return isExist(Arrays.copyOfRange(keys, 1, keys.length), embeddedOptional.get().getConnectedClass());
            } else {
                return false;
            }
        } else {
            Optional<Attribute> attrOptional = embeddable.getAttributes().getAllAttribute().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            return attrOptional.isPresent();
        }
    }


}
