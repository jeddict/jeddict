/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_TYPE_ADAPTER;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_TYPE_ADAPTER_FQN;
import org.netbeans.jpa.modeler.spec.extend.ReferenceClass;
import org.netbeans.orm.converter.compiler.*;
import org.netbeans.orm.converter.util.ClassHelper;
import static org.netbeans.orm.converter.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static org.netbeans.orm.converter.util.ORMConverterUtil.OPEN_PARANTHESES;

public class TypeAdapterSnippet implements Snippet {

    private final ClassHelper type;

    public TypeAdapterSnippet(ReferenceClass referenceClass) {
        this.type = new ClassHelper(referenceClass.getName());
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(JSONB_TYPE_ADAPTER);
        builder.append(OPEN_PARANTHESES).append(type.getClassNameWithClassSuffix()).append(CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(JSONB_TYPE_ADAPTER_FQN);
        importSnippets.add(type.getFQClassName());
        return importSnippets;
    }
}
