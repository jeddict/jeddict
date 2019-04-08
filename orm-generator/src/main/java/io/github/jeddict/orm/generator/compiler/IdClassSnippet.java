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

import static io.github.jeddict.jcode.JPAConstants.ID_CLASS;
import static io.github.jeddict.jcode.JPAConstants.ID_CLASS_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

public class IdClassSnippet implements Snippet {

    private final ClassHelper classHelper = new ClassHelper();

    public String getValue() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setValue(String value) {
        classHelper.setClassName(value);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(classHelper.getClassName())) {
            throw new InvalidDataException("value is a required");
        }

        return annotate(
                ID_CLASS, 
                getValue()
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();

        imports.add(ID_CLASS_FQN);
        if (classHelper.getFQClassName() != null) {
            imports.add(classHelper.getFQClassName());
        }

        return imports;
    }
}
