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
import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.util.ClassHelper;

public class ColumnResultSnippet implements Snippet {

    private ClassHelper classHelper = new ClassHelper();
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

    public String getSnippet() throws InvalidDataException {

        if (name == null) {
            throw new InvalidDataException("Name is null");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@ColumnResult(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (classHelper.getClassName() != null) {
            builder.append("type=");
            builder.append(getType());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
//        return "@ColumnResult(name=\"" + name + ORMConverterUtil.QUOTE
//                + ORMConverterUtil.CLOSE_PARANTHESES;

    }

//    public Collection<String> getImportSnippets() throws InvalidDataException {
//        return Collections.singletonList("javax.persistence.ColumnResult");
//    }
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<String>();
        importSnippets.add("javax.persistence.ColumnResult");
        if (classHelper.getFQClassName() != null) {
            importSnippets.add(classHelper.getFQClassName());
        }
        return importSnippets;
    }
}
