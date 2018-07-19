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
package io.github.jeddict.orm.generator.compiler.constraints;

import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

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
        if (constraint.getMessage() == null && constraint.getMin() == null && constraint.getMax() == null) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append(ORMConverterUtil.OPEN_PARANTHESES);

        if (constraint.getMin() != null) {
            builder.append("min=");
            builder.append(constraint.getMin());
            builder.append(ORMConverterUtil.COMMA);
        }
        if (constraint.getMax() != null) {
            builder.append("max=");
            builder.append(constraint.getMax());
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
