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
package io.github.jeddict.jsonb.generator.compiler;

import static io.github.jeddict.jcode.JSONBConstants.JSONB_TYPE_DESERIALIZER;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_TYPE_DESERIALIZER_FQN;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TypeDeserializerSnippet implements Snippet {

    private final ClassHelper type;

    public TypeDeserializerSnippet(ReferenceClass referenceClass) {
        this.type = new ClassHelper(referenceClass.getName());
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(JSONB_TYPE_DESERIALIZER)
                .append(OPEN_PARANTHESES)
                .append(type.getClassNameWithClassSuffix())
                .append(CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(JSONB_TYPE_DESERIALIZER_FQN);
        imports.add(type.getFQClassName());
        return imports;
    }
}
