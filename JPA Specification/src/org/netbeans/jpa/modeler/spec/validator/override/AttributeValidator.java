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

import org.netbeans.jpa.modeler.spec.AttributeOverride;
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

}
