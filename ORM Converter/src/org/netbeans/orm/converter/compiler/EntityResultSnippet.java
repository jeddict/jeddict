/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.ENTITY_RESULT;
import static org.netbeans.jcode.jpa.JPAConstants.ENTITY_RESULT_FQN;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class EntityResultSnippet implements Snippet {

    private ClassHelper classHelper = new ClassHelper();
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

        if (classHelper.getClassName() == null) {
            throw new InvalidDataException("Entity Class missing");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(ENTITY_RESULT).append("(entityClass=");
        builder.append(getEntityClass());
        builder.append(ORMConverterUtil.COMMA);

        if (discriminatorColumn != null) {
            builder.append("discriminatorColumn=\"");
            builder.append(discriminatorColumn);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!fieldResults.isEmpty()) {
            builder.append("fields={");

            for (FieldResultSnippet fieldResult : fieldResults) {
                builder.append(fieldResult.getSnippet());
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
        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(ENTITY_RESULT_FQN);
        if (!fieldResults.isEmpty()) {
            for (FieldResultSnippet fieldResult : fieldResults) {
                importSnippets.addAll(fieldResult.getImportSnippets());
            }
        }
        importSnippets.add(classHelper.getFQClassName());

        return importSnippets;
    }
}
