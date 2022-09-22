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

import static io.github.jeddict.jcode.JPAConstants.ENTITY_RESULT;
import static io.github.jeddict.jcode.JPAConstants.ENTITY_RESULT_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

public class EntityResultSnippet implements Snippet {

    private final ClassHelper classHelper = new ClassHelper();
    private String discriminatorColumn = null;

    private List<FieldResultSnippet> fieldResults = Collections.<FieldResultSnippet>emptyList();

    public void addFieldResult(FieldResultSnippet fieldResult) {

        if (fieldResults.isEmpty()) {
            fieldResults = new ArrayList<>();
        }

        fieldResults.add(fieldResult);
    }

    public String getEntityClass() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setEntityClass(String entityClass) {
        classHelper.setClassName(entityClass);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public String getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    public void setDiscriminatorColumn(String discriminatorColumn) {
        this.discriminatorColumn = discriminatorColumn;
    }

    public List<FieldResultSnippet> getFieldResults() {
        return fieldResults;
    }

    public void setFieldResults(List<FieldResultSnippet> fieldResults) {
        if (fieldResults != null) {
            this.fieldResults = fieldResults;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(classHelper.getClassName())) {
            throw new InvalidDataException("Entity Class missing");
        }

        return annotate(
                ENTITY_RESULT,
                attributeExp("entityClass", getEntityClass()),
                attribute("discriminatorColumn", discriminatorColumn),
                attributes("fields", fieldResults)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();

        imports.add(ENTITY_RESULT_FQN);
        if (!fieldResults.isEmpty()) {
            for (FieldResultSnippet fieldResult : fieldResults) {
                imports.addAll(fieldResult.getImportSnippets());
            }
        }
        imports.add(classHelper.getFQClassName());

        return imports;
    }
}
