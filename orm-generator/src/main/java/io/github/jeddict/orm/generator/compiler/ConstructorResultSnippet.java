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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.JPAConstants.CONSTRUCTOR_RESULT;
import static io.github.jeddict.jcode.JPAConstants.CONSTRUCTOR_RESULT_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class ConstructorResultSnippet implements Snippet {

    private ClassHelper classHelper = new ClassHelper();
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

        if (classHelper.getClassName() == null) {
            throw new InvalidDataException("Target Class missing");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(CONSTRUCTOR_RESULT).append("(targetClass=");
        builder.append(getTargetClass());
        builder.append(ORMConverterUtil.COMMA);

        if (!columnResults.isEmpty()) {
            builder.append("columns={");

            for (ColumnResultSnippet columnResult : columnResults) {
                builder.append(columnResult.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;

    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (columnResults.isEmpty()) {
            return Collections.singletonList(CONSTRUCTOR_RESULT_FQN);
        }

        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(CONSTRUCTOR_RESULT_FQN);
        for (ColumnResultSnippet columnResult : columnResults) {
            importSnippets.addAll(columnResult.getImportSnippets());
        }
        importSnippets.add(classHelper.getFQClassName());

        return importSnippets;
    }
}
