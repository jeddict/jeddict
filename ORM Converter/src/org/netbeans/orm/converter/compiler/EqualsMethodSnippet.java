/**
 * Copyright [2016] Gaurav Gupta
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

import java.util.Collections;
import java.util.List;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_BRACES;

public class EqualsMethodSnippet implements Snippet {

    private final String className;
    private final ClassMembers classMembers;

    public EqualsMethodSnippet(String className, ClassMembers classMembers) {
        this.className = className;
        this.classMembers = classMembers;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("if (obj == null) {return false;}");
        builder.append("if (getClass() != obj.getClass()) {return false;}");
        builder.append(String.format("final %s other = (%s) obj;", className, className));

        for (int i = 0; i < classMembers.getAttributes().size(); i++) {
            Attribute attribute = classMembers.getAttributes().get(i);
            String expression;
            if (attribute instanceof BaseAttribute) {
                expression = JavaHashcodeEqualsUtil.getEqualExpression(((BaseAttribute) attribute).getAttributeType(), attribute.getName());
            } else {
                expression = JavaHashcodeEqualsUtil.getEqualExpression(attribute.getName());
            }
            builder.append(String.format("if (%s) {", expression));
            builder.append("return false;");
            builder.append(CLOSE_BRACES);

        }
        builder.append("return true;");
        return builder.toString();
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.EMPTY_LIST;
    }
}
