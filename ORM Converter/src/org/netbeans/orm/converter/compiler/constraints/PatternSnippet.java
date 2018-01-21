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
package org.netbeans.orm.converter.compiler.constraints;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.bean.validation.constraints.Flag;
import org.netbeans.bean.validation.constraints.Pattern;
import static org.netbeans.jcode.beanvalidation.BeanVaildationConstants.BEAN_VAILDATION_PACKAGE;
import org.netbeans.orm.converter.compiler.*;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_BRACES;

/**
 *
 * @author Gaurav Gupta
 */
public class PatternSnippet extends ConstraintSnippet<Pattern> {

    private List<Flag> flags = Collections.EMPTY_LIST;

    public PatternSnippet(Pattern pattern) {
        super(pattern);
        if (!StringUtils.isBlank(constraint.getFlags())) {
            flags = Arrays.stream(constraint.getFlags().split(","))
                    .map(flag -> Flag.fromValue(flag.trim()))
                    .filter(flag -> flag != null)
                    .collect(toList());
        }
    }

    @Override
    protected String getAPI() {
        return "Pattern";
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (constraint.getMessage() == null && StringUtils.isBlank(constraint.getRegexp()) && flags.isEmpty()) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append(ORMConverterUtil.OPEN_PARANTHESES);

        if (!StringUtils.isBlank(constraint.getRegexp())) {
            builder.append("regexp=\"");
            builder.append(constraint.getRegexp());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!flags.isEmpty()) {
            builder.append("flags=").append(OPEN_BRACES);
            builder.append(flags.stream().map(Flag::name).collect(joining(", ")));
            builder.append(ORMConverterUtil.CLOSE_BRACES);
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

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Collection<String> imports = super.getImportSnippets();
        imports.addAll(
                flags.stream()
                        .map(Flag::name)
                        .map(flag -> "static " + BEAN_VAILDATION_PACKAGE + ".Pattern.Flag." + flag)
                        .collect(toList())
        );
        return imports;
    }

}
