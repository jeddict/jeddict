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
import org.netbeans.jpa.modeler.spec.extend.AccessModifierType;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_BRACES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.COMMA;
import static org.netbeans.orm.converter.util.ORMConverterUtil.NEW_LINE;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_BRACES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.SPACE;

public class ConstructorSnippet implements Snippet {

    private final String className;
    private final AccessModifierType accessModifier;
    private final List<VariableDefSnippet> variableSnippets;
    

    public ConstructorSnippet(String className, AccessModifierType accessModifier, List<VariableDefSnippet> variableSnippets) {
        this.className = className;
        this.accessModifier = accessModifier;
        this.variableSnippets = variableSnippets;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
                StringBuilder builder = new StringBuilder();
                if(accessModifier!=AccessModifierType.DEFAULT){
                    builder.append(accessModifier.getValue()).append(SPACE);
                }
                builder.append(className).append(OPEN_PARANTHESES);
                StringBuilder varAssign = new StringBuilder();
                for(VariableDefSnippet variableSnippet : variableSnippets){
                    builder.append(variableSnippet.getType()).append(SPACE).append(variableSnippet.getName()).append(COMMA);
                    varAssign.append(String.format("this.%s=%s;", variableSnippet.getName(),variableSnippet.getName())).append(NEW_LINE);
                }
                builder.setLength(builder.length()-1);
                builder.append(CLOSE_PARANTHESES);
                
                builder.append(OPEN_BRACES).append(NEW_LINE);
                builder.append(varAssign);
                builder.append(CLOSE_BRACES);
                
        return builder.toString();
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        return Collections.EMPTY_LIST;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

}
