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
package org.netbeans.orm.converter.compiler.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.bean.validation.constraints.Constraint;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.compiler.Snippet;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ConstraintSnippet<T extends Constraint> implements Snippet {

    protected final T constraint;
    public ConstraintSnippet(T constraint) {
        this.constraint = constraint;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (constraint.getMessage() == null) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append("(message=\"");
        builder.append(constraint.getMessage());
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add("javax.validation.constraints." + getAPI());
        return importSnippets;
    } 
   
    protected abstract String getAPI();
}
