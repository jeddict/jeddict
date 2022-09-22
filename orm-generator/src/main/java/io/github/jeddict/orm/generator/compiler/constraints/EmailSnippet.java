/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.bv.constraints.Email;
import io.github.jeddict.bv.constraints.Flag;
import static io.github.jeddict.jcode.BeanVaildationConstants.BV_CONSTRAINTS_PACKAGE;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static io.github.jeddict.util.StringUtils.isBlank;
import static io.github.jeddict.util.StringUtils.isNotBlank;

/**
 *
 * @author Gaurav Gupta
 */
public class EmailSnippet extends ConstraintSnippet<Email> {

    private List<Flag> flags = Collections.<Flag>emptyList();

    public EmailSnippet(Email email) {
        super(email);
        if (isNotBlank(constraint.getFlags())) {
            flags = Arrays.stream(constraint.getFlags().split(","))
                    .map(flag -> Flag.fromValue(flag.trim()))
                    .filter(flag -> flag != null)
                    .collect(toList());
        }
    }

    @Override
    protected String getAPI() {
        return "Email";
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(getAPI());

        if (isBlank(constraint.getMessage())
                && isBlank(constraint.getRegexp())
                && flags.isEmpty()) {
            return builder.toString();
        }

        builder.append(OPEN_PARANTHESES);

        if (isNotBlank(constraint.getRegexp())) {
            builder.append("regexp=")
                    .append(QUOTE)
                    .append(constraint.getRegexp())
                    .append(QUOTE)
                    .append(COMMA);
        }

        if (!flags.isEmpty()) {
            builder.append("flags=")
                    .append(OPEN_BRACES)
                    .append(flags.stream().map(Flag::name).collect(joining(", ")))
                    .append(CLOSE_BRACES)
                    .append(COMMA);
        }

        if (isNotBlank(constraint.getMessage())) {
            builder.append("message=")
                    .append(QUOTE)
                    .append(constraint.getMessage())
                    .append(QUOTE)
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Collection<String> imports = super.getImportSnippets();
        imports.addAll(flags.stream()
                        .map(Flag::name)
                        .map(flag -> "static " + BV_CONSTRAINTS_PACKAGE + ".Email.Flag." + flag)
                        .collect(toList())
        );
        return imports;
    }

}
