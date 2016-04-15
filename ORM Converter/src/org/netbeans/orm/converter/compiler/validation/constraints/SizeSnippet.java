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

import org.netbeans.jpa.modeler.spec.validation.constraints.Size;
import org.netbeans.orm.converter.compiler.*;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Gaurav Gupta
 */
public class SizeSnippet extends ConstraintSnippet<Size> {

    public SizeSnippet(Size size) {
        super(size);
    }

    @Override
    protected String getAPI() {
        return "Size";
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (constraint.getMessage() == null && constraint.getMax() == null && constraint.getMessage() == null) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append(ORMConverterUtil.OPEN_PARANTHESES);

        if (constraint.getMin() != null) {
            builder.append("min=");
            builder.append(constraint.getMin());
        }
        if (constraint.getMax() != null) {
            builder.append("max=");
            builder.append(constraint.getMax());
        }
        if (constraint.getMessage() != null) {
            builder.append("message=\"");
            builder.append(constraint.getMessage());
            builder.append(ORMConverterUtil.QUOTE);
        }

        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        return builder.toString();
    }

}
