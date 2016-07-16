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
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
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

    /**
     * Used to remove all stale AttributeOverride (ex : if Parent JavaClass
     * attribute is removed then AttributeOverride reference should be removed)
     */
    public static void filter(Entity entity) {
        ManagedClass parentclass = entity.getSuperclass() instanceof ManagedClass ? (ManagedClass) entity.getSuperclass() : null;
        entity.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName(), parentclass)
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }

    /**
     * Used to remove all stale AttributeOverride by nested scanning
     *
     * @param key key of AttributeOverride
     * @param javaClass parent class of entity to search AttributeOverride's key
     * @return
     */
    private static boolean isExist(String key, ManagedClass javaClass) {
        if (javaClass == null) {
            return false;
        }
        Optional<Attribute> attrOptional = javaClass.getAttributes().getNonRelationAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(key)).findAny();
        if (attrOptional.isPresent()) {
            return true;
        } else if (javaClass.getSuperclass() instanceof ManagedClass) {
            return isExist(key, (ManagedClass) javaClass.getSuperclass());
        } else {
            return false;
        }
    }

    /**
     * Used to remove all stale AttributeOverride (ex : if Embeddable attribute
     * is removed then AttributeOverride reference should be removed)
     *
     * @param embedded
     */
    public static void filter(Embedded embedded) {
        embedded.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), embedded.getConnectedClass())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }
    
        public static void filter(EmbeddedId embedded) {
        embedded.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), embedded.getConnectedClass())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }

    /**
     * Used to remove all stale AttributeOverride (ex : if Embeddable attribute
     * is removed then AttributeOverride reference should be removed)
     */
    public static void filter(ElementCollection elementCollection) {
        elementCollection.getAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), elementCollection.getConnectedClass())
                || AttributeValidator.isEmpty(attributeOverride)
        );
        
        
        elementCollection.getMapKeyAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), elementCollection.getMapKeyEmbeddable())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }
    
    /**
     * Used for MapKey filter
     * @param mapKeyHandler 
     */
        public static void filterMapKey(MapKeyHandler mapKeyHandler) {
        mapKeyHandler.getMapKeyAttributeOverride().removeIf(attributeOverride
                -> !isExist(attributeOverride.getName().split("\\."), mapKeyHandler.getMapKeyEmbeddable())
                || AttributeValidator.isEmpty(attributeOverride)
        );
    }

    /**
     *
     * @param keys arrays path to managedAttr separated by dots
     * @param embeddable next intrinsic element , incremented in each recursion
     */
    private static boolean isExist(String[] keys, Embeddable embeddable) {
        if(embeddable==null){
            return false;
        }
        if (keys.length > 1) {
            Optional<Embedded> embeddedOptional = embeddable.getAttributes().getEmbedded().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (embeddedOptional.isPresent()) {
                return isExist(Arrays.copyOfRange(keys, 1, keys.length), embeddedOptional.get().getConnectedClass());
            } else {
                return false;
            }
        } else {
            Optional<Attribute> attrOptional = embeddable.getAttributes().getNonRelationAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            return attrOptional.isPresent();
        }
    }
    
    /**
     * For EmbeddedId
     * @param keys
     * @param defaultClass
     * @return 
     */
        private static boolean isExist(String[] keys, DefaultClass defaultClass) {
        if (keys.length > 1) {
            Optional<DefaultAttribute> embeddedOptional = defaultClass.getAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (embeddedOptional.isPresent()) {
                return true;// TODO defaultattribute connected class => nested => isExist(Arrays.copyOfRange(keys, 1, keys.length), embeddedOptional.get().getConnectedClass());
            } else {
                return false;
            }
        } else {
            Optional<DefaultAttribute> attrOptional = defaultClass.getAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            return attrOptional.isPresent();
        }
    }

}
