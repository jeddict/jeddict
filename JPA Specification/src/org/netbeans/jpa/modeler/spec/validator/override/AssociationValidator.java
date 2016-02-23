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
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;
import org.netbeans.jpa.modeler.spec.validator.MarshalValidator;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;

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

    private static boolean isExist(String[] keys, Embeddable embeddable, AssociationOverride associationOverride) {
        if (keys.length > 1) {
            Optional<Embedded> embeddedOptional = embeddable.getAttributes().getEmbedded().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if (embeddedOptional.isPresent()) {
                return isExist(Arrays.copyOfRange(keys, 1, keys.length), embeddedOptional.get().getConnectedClass(),associationOverride);
            } else {
                return false;
            }
        } else {
            Optional<RelationAttribute> attrOptional = embeddable.getAttributes().getRelationAttributes().stream().filter(e -> e.getName().equalsIgnoreCase(keys[0])).findAny();
            if(attrOptional.isPresent()){
                RelationAttribute attribute = attrOptional.get();
                if(attribute instanceof SingleRelationAttribute){
                    associationOverride.getJoinTable().clear();
                } else if(attribute instanceof MultiRelationAttribute){
                    if(attribute instanceof OneToMany){
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
