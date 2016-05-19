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
package org.netbeans.jpa.modeler.spec.validator.column;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.ConstraintMode;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.validator.MarshalValidator;

public class ForeignKeyValidator extends MarshalValidator<ForeignKey> {

    @Override
    public ForeignKey marshal(ForeignKey foreignKey) throws Exception {
        if (foreignKey != null && isEmpty(foreignKey)) {
            return null;
        }
        return foreignKey;
    }

    public static boolean isEmpty(ForeignKey foreignKey) {
        if(foreignKey==null){
            return true;
        }
        if (StringUtils.isBlank(foreignKey.getName()) 
                && StringUtils.isBlank(foreignKey.getForeignKeyDefinition())
                && (foreignKey.getConstraintMode()==null || foreignKey.getConstraintMode()==ConstraintMode.PROVIDER_DEFAULT)){
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(ForeignKey foreignKey) {
        return !isEmpty(foreignKey);
    }

    /**
     * Remove empty/invalid ForeignKey
     *
     * @param foreignKeys
     */
    public static void filter(List<ForeignKey> foreignKeys) {
        foreignKeys.removeIf(ForeignKeyValidator::isEmpty);
    }

}
