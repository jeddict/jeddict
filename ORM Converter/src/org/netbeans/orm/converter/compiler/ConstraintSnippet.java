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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ConstraintSnippet implements Snippet {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    @Override
    public String getSnippet() throws InvalidDataException {
        if (getMessage() == null) {
            return "@" + getAPI();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(getAPI()).append("(message=\"");
        builder.append(getMessage());
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
