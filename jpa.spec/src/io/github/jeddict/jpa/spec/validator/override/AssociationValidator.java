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
package io.github.jeddict.jpa.spec.validator.override;

import java.util.Arrays;
import java.util.Optional;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.MultiRelationAttribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.extend.SingleRelationAttribute;
import io.github.jeddict.jpa.spec.validator.MarshalValidator;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;

public class AssociationValidator extends MarshalValidator<AssociationOverride> {

    @Override
    public AssociationOverride marshal(AssociationOverride associationOverride) throws Exception {
        if (associationOverride != null && isEmpty(associationOverride)) {
            return null;
        }
        return associationOverride;
    }

    public static boolean isEmpty(AssociationOverride associationOverride) {
        JoinColumnValidator.filter(associationOverride.getJoinColumn());
        return JoinTableValidator.isEmpty(associationOverride.getJoinTable())
                && associationOverride.getJoinColumn().isEmpty();
    }

    /**
     * Used to remove all stale AssociationOverride (ex : if Parent JavaClass
     * association is removed then AssociationOverride reference should be
     * removed) There are two way : either to remove on attribute deletion or at
     * the time of DB Modeler creation
     *
     */
    public static void filter(Entity entity) {
        ManagedClass parentclass = entity.getSuperclass() instanceof ManagedClass ? (ManagedClass) entity.getSuperclass() : null;
        entity.getAssociationOverride().removeIf(associationOverride
                -> !isExist(associationOverride.getName(), parentclass)
                || AssociationValidator.isEmpty(associationOverride)
        );
    }

    /**
     *
     * @param key key of AttributeOverride
     * @param managedClass parent class of entity to search AttributeOverride's key
     * @return
     */
    private static boolean isExist(String key, ManagedClass<IPersistenceAttributes> managedClass) {
        if (managedClass == null) {
            return false;
        }
        Optional<RelationAttribute> attrOptional = managedClass.getAttributes()
                .getRelationAttributes()
                .stream()
                .filter(e -> e.getName().equalsIgnoreCase(key))
                .findAny();
        if (attrOptional.isPresent()) {
            return true;
        } else if (managedClass.getSuperclass() instanceof ManagedClass) {
            return isExist(key, (ManagedClass) managedClass.getSuperclass());
        } else {
            return false;
        }
    }

    public static void filter(Embedded embedded) {
        embedded.getAssociationOverride().removeIf(associationOverride
                -> !isExist(associationOverride.getName().split("\\."), embedded.getConnectedClass(), associationOverride)
                || AssociationValidator.isEmpty(associationOverride)
        );
    }

    public static void filter(ElementCollection elementCollection) {
        elementCollection.getAssociationOverride().removeIf(associationOverride
                -> !isExist(associationOverride.getName().split("\\."), elementCollection.getConnectedClass(), associationOverride)
                || AssociationValidator.isEmpty(associationOverride)
        );
    }

    /**
     *
     * @param keys arrays path to managedAttr separated by dots
     * @param embeddable next intrinsic element , incremented in each recursion
     */
    private static boolean isExist(String[] keys, Embeddable embeddable, AssociationOverride associationOverride) {
        if (keys.length > 1) {
            Optional<Embedded> embeddedOptional = embeddable.getAttributes().getEmbedded().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (embeddedOptional.isPresent()) {
                return isExist(Arrays.copyOfRange(keys, 1, keys.length), embeddedOptional.get().getConnectedClass(), associationOverride);
            } else {
                return false;
            }
        } else {
            Optional<RelationAttribute> attrOptional = embeddable.getAttributes().getRelationAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (attrOptional.isPresent()) {
                RelationAttribute attribute = attrOptional.get();
                if (attribute instanceof SingleRelationAttribute) {
                    associationOverride.getJoinTable().clear();
                } else if (attribute instanceof MultiRelationAttribute) {
                    if (attribute instanceof OneToMany) {
                        associationOverride.getJoinColumn().clear();
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

}
