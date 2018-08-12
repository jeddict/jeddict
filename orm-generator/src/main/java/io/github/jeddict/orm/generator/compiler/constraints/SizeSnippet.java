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

import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static org.apache.commons.lang.StringUtils.isBlank;

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
        StringBuilder builder = new StringBuilder(AT);
        builder.append(getAPI());

        if (isBlank(constraint.getMessage())
                && isBlank(constraint.getMin())
                && isBlank(constraint.getMax())) {
            return builder.toString();
        }

        builder.append(OPEN_PARANTHESES)
                .append(buildExp("min", constraint.getMin()))
                .append(buildExp("max", constraint.getMax()))
                .append(buildString("message", constraint.getMessage()));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

}
