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

import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.validator.MarshalValidator;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import static org.netbeans.jpa.modeler.spec.validator.override.AttributeValidator.isExist;
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
                -> !isExist(associationOverride.getName().split("\\."), embedded.getConnectedClass())
                || AssociationValidator.isEmpty(associationOverride)
        );
    }

    public static void filter(ElementCollection elementCollection) {
        elementCollection.getAssociationOverride().removeIf(associationOverride
                -> !isExist(associationOverride.getName().split("\\."), elementCollection.getConnectedClass())
                || AssociationValidator.isEmpty(associationOverride)
        );
    }

}
