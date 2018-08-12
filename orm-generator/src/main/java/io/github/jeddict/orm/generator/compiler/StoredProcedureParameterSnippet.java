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

import static io.github.jeddict.jcode.JPAConstants.PARAMETER_MODE;
import static io.github.jeddict.jcode.JPAConstants.PARAMETER_MODE_FQN;
import static io.github.jeddict.jcode.JPAConstants.STORED_PROCEDURE_PARAMETER;
import static io.github.jeddict.jcode.JPAConstants.STORED_PROCEDURE_PARAMETER_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class StoredProcedureParameterSnippet implements Snippet {

    private final ClassHelper classHelper = new ClassHelper();
    private String name;
//    private String clazz;
    private String mode;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (classHelper.getClassName() == null) {
            throw new InvalidDataException("Type required");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(STORED_PROCEDURE_PARAMETER)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name));

        if (isNotBlank(mode)) {
            builder.append("mode=").append(PARAMETER_MODE).append(".");
            builder.append(mode);
            builder.append(COMMA);
        }

        if (classHelper.getClassName() != null) {
            builder.append("type=");
            builder.append(getType());
            builder.append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(STORED_PROCEDURE_PARAMETER_FQN);

        if (classHelper.getFQClassName() != null) {
            imports.add(classHelper.getFQClassName());
        }
        if (isNotBlank(mode)) {
            imports.add(PARAMETER_MODE_FQN);
        }

        return imports;
    }

    public String getType() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setType(String resultClass) {
        classHelper.setClassName(resultClass);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

}
