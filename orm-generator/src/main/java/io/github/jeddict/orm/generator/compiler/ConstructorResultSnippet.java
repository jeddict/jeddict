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

import static io.github.jeddict.jcode.JPAConstants.CONSTRUCTOR_RESULT;
import static io.github.jeddict.jcode.JPAConstants.CONSTRUCTOR_RESULT_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

public class ConstructorResultSnippet implements Snippet {

    private final ClassHelper classHelper = new ClassHelper();
    private List<ColumnResultSnippet> columnResults = Collections.<ColumnResultSnippet>emptyList();

    public void addColumnResult(ColumnResultSnippet columnResult) {
        if (columnResults.isEmpty()) {
            columnResults = new ArrayList<>();
        }
        columnResults.add(columnResult);
    }

    public String getTargetClass() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setTargetClass(String targetClass) {
        classHelper.setClassName(targetClass);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public List<ColumnResultSnippet> getColumnResults() {
        return columnResults;
    }

    public void setColumnResults(List<ColumnResultSnippet> columnResults) {
        if (columnResults != null) {
            this.columnResults = columnResults;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(classHelper.getClassName())) {
            throw new InvalidDataException("ColumnResult.targetClass value must not be null");
        }

        return annotate(
                CONSTRUCTOR_RESULT,
                attributeExp("targetClass", getTargetClass()),
                attributes("columns", columnResults)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (columnResults.isEmpty()) {
            return singleton(CONSTRUCTOR_RESULT_FQN);
        }

        Set<String> imports = new HashSet<>();
        imports.add(CONSTRUCTOR_RESULT_FQN);
        for (ColumnResultSnippet columnResult : columnResults) {
            imports.addAll(columnResult.getImportSnippets());
        }
        imports.add(classHelper.getFQClassName());
        return imports;
    }
}
