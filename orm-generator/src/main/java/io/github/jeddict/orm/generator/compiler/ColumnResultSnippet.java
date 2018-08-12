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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.COLUMN_RESULT;
import static io.github.jeddict.jcode.JPAConstants.COLUMN_RESULT_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ColumnResultSnippet implements Snippet {

    private final ClassHelper classHelper = new ClassHelper();

    private String name = null;

    public String getType() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setType(String entityClass) {
        classHelper.setClassName(entityClass);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name)) {
            throw new InvalidDataException("ColumnResult.name property must not be null");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(COLUMN_RESULT)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name));

        if (isNotBlank(classHelper.getClassName())) {
            builder.append("type=")
                    .append(getType())
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(COLUMN_RESULT_FQN);
        if (classHelper.getFQClassName() != null) {
            imports.add(classHelper.getFQClassName());
        }
        return imports;
    }
}
