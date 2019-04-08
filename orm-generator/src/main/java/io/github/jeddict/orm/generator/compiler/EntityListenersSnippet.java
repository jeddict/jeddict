/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.jcode.JPAConstants.ENTITY_LISTENERS;
import static io.github.jeddict.jcode.JPAConstants.ENTITY_LISTENERS_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityListenersSnippet implements Snippet {

    private List<EntityListenerSnippet> entityListeners = Collections.<EntityListenerSnippet>emptyList();

    @Override
    public String getSnippet() throws InvalidDataException {
        if (entityListeners.isEmpty()) {
            throw new InvalidDataException("entity listeners is empty");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(ENTITY_LISTENERS)
                .append(OPEN_PARANTHESES)
                .append(OPEN_BRACES);

        for (EntityListenerSnippet entityListener : entityListeners) {
            builder.append(entityListener.getSnippet())
                    .append(COMMA);
        }

        return builder.substring(0, builder.length() - 1) + CLOSE_BRACES + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (entityListeners.isEmpty()) {
            return Collections.<String>emptySet();
        }

        Set<String> imports = new HashSet<>();
        imports.add(ENTITY_LISTENERS_FQN);
        for (EntityListenerSnippet entityListener : entityListeners) {
            imports.addAll(entityListener.getImportSnippets());
        }

        return imports;
    }

    public void addEntityListener(EntityListenerSnippet entityListener) {
        getEntityListeners().add(entityListener);
    }

    public List<EntityListenerSnippet> getEntityListeners() {
        if (entityListeners == null) {
            entityListeners = new ArrayList<>();
        }
        return entityListeners;
    }

    public void setEntityListeners(List<EntityListenerSnippet> listeners) {
        this.entityListeners = listeners;
    }
}
