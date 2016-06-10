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
package org.netbeans.jpa.modeler.spec.validator.table;

import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.validator.MarshalValidator;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;

public class JoinTableValidator extends MarshalValidator<JoinTable> {

    @Override
    public JoinTable marshal(JoinTable table) throws Exception {
        if (table != null && isEmpty(table)) {
            return null;
        }
        return table;
    }

    public static boolean isEmpty(JoinTable table) {
        JoinColumnValidator.filter(table.getJoinColumn());
        JoinColumnValidator.filter(table.getInverseJoinColumn());

        return StringUtils.isBlank(table.getName()) && StringUtils.isBlank(table.getSchema()) &&
                StringUtils.isBlank(table.getCatalog()) && 
                table.getJoinColumn().isEmpty() && table.getInverseJoinColumn().isEmpty() &&
                table.getIndex().isEmpty();
    }
}
