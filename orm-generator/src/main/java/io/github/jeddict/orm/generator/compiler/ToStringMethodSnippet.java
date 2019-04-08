/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ClassMembers;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.SINGLE_QUOTE;
import java.util.Collection;
import java.util.Collections;
import static java.util.Objects.nonNull;

public class ToStringMethodSnippet implements Snippet {

    private final String className;
    private final ClassMembers classMembers;

    public ToStringMethodSnippet(String className, ClassMembers classMembers) {
        this.className = className;
        this.classMembers = classMembers;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        String classTemplate = "\"%s{\" + ";
        builder.append(String.format(classTemplate, className));

        String attrTemplate = " %s=\" + %s + ";

        for (int i = 0; i < classMembers.getAttributes().size(); i++) {
            Attribute attribute = classMembers.getAttributes().get(i);

            if (attribute instanceof Id) {
                IdentifiableClass identifiableClass = (IdentifiableClass) attribute.getJavaClass();
                if (nonNull(identifiableClass.getAttributes().getEmbeddedId())) {
                    continue;
                }
            }

            builder.append(QUOTE);
            if (i != 0) {
                builder.append(COMMA);
            }
            builder.append(String.format(attrTemplate, attribute.getName(), attribute.getName()));
        }
        builder.append(SINGLE_QUOTE).append(CLOSE_BRACES).append(SINGLE_QUOTE);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.<String>emptyList();
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the classMembers
     */
    public ClassMembers getClassMembers() {
        return classMembers;
    }
}
