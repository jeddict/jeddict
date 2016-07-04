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
import java.util.List;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

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

        StringBuilder builder = new StringBuilder();

        builder.append("@StoredProcedureParameter(");
        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (mode != null) {
            builder.append("mode=ParameterMode.");
            builder.append(mode);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (classHelper.getClassName() != null) {
            builder.append("type=");
            builder.append(getType());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add("javax.persistence.StoredProcedureParameter");

        if (classHelper.getFQClassName() != null) {
            importSnippets.add(classHelper.getFQClassName());
        }
        if (mode != null) {
            importSnippets.add("javax.persistence.ParameterMode");
        }

        return importSnippets;
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
