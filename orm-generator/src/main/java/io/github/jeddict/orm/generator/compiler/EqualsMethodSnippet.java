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
package io.github.jeddict.orm.generator.compiler;

import io.github.jeddict.jpa.spec.DefaultAttribute;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import io.github.jeddict.jpa.spec.extend.CompositionAttribute;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import java.util.Collection;
import static java.util.Collections.singleton;
import static java.util.Objects.nonNull;
import io.github.jeddict.util.StringUtils;

public class EqualsMethodSnippet implements Snippet {

    private final String className;
    private final ClassMembers classMembers;

    public EqualsMethodSnippet(String className, ClassMembers classMembers) {
        this.className = className;
        this.classMembers = classMembers;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("if (obj == null) {return false;}\n");
        builder.append("        ")
                .append("if (!Objects.equals(getClass(), obj.getClass())) {return false;}\n");
        builder.append("        ")
                .append(String.format("final %s other = (%s) obj;\n", className, className));

        if (StringUtils.isNotBlank(classMembers.getPreCode())) {
            builder.append(classMembers.getPreCode()).append(NEW_LINE);
        }

        for (int i = 0; i < classMembers.getAttributes().size(); i++) {
            Attribute attribute = classMembers.getAttributes().get(i);

            if (attribute instanceof Id) {
                IdentifiableClass identifiableClass = (IdentifiableClass) attribute.getJavaClass();
                if (nonNull(identifiableClass.getAttributes().getEmbeddedId())) {
                    continue;
                }
            }

            if (attribute instanceof DefaultAttribute) {
                attribute = ((DefaultAttribute) attribute).getConnectedAttribute();
            }

            String expression;
            boolean optionalType = attribute.isOptionalReturnType();
            if (attribute instanceof BaseAttribute && !(attribute instanceof CompositionAttribute)) {
                expression = JavaHashcodeEqualsUtil.getEqualExpression(((BaseAttribute) attribute).getAttributeType(), attribute.getName(), optionalType);
            } else {
                expression = JavaHashcodeEqualsUtil.getEqualExpression(attribute.getDataTypeLabel(), attribute.getName(), optionalType);
            }

            builder.append("        ")
                   .append(String.format("if (%s) {", expression));
            builder.append("        ")
                   .append("return false;");
            builder.append("        ")
                   .append(CLOSE_BRACES).append(NEW_LINE);

        }
        if (StringUtils.isNotBlank(classMembers.getPostCode())) {
            builder.append(classMembers.getPostCode()).append(NEW_LINE);
        }
        builder.append("        ")
                   .append("return true;");
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton("java.util.Objects");
    }
}
