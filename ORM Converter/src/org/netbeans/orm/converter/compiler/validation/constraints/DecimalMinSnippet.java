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
package org.netbeans.orm.converter.compiler.validation.constraints;

import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.validation.constraints.DecimalMin;
import org.netbeans.orm.converter.compiler.*;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Gaurav Gupta
 */
public class DecimalMinSnippet extends ConstraintSnippet<DecimalMin> {

    public DecimalMinSnippet(DecimalMin decimalMin) {
        super(decimalMin);
    }

    @Override
    protected String getAPI() {
        return "DecimalMin";
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (constraint.getMessage() == null && StringUtils.isBlank(constraint.getValue())) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append(ORMConverterUtil.OPEN_PARANTHESES);

        if (!StringUtils.isBlank(constraint.getValue())) {
            builder.append("value=\"");
            builder.append(constraint.getValue());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }
        
         if (constraint.getMessage() != null) {
            builder.append("message=\"");
            builder.append(constraint.getMessage());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1) + ORMConverterUtil.CLOSE_PARANTHESES;
    }

}
