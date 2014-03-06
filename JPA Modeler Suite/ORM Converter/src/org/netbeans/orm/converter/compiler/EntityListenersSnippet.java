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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EntityListenersSnippet implements Snippet {

    private List<EntityListenerSnippet> entityListeners = Collections.EMPTY_LIST;

    public String getSnippet() throws InvalidDataException {

        if (entityListeners.isEmpty()) {
            throw new InvalidDataException("entity listeners is empty");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@EntityListeners({");

        for (EntityListenerSnippet entityListener : entityListeners) {
            stringBuilder.append(entityListener.getSnippet());
            stringBuilder.append(ORMConverterUtil.COMMA);
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1)
                + ORMConverterUtil.CLOSE_BRACES + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (entityListeners.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.EntityListeners");

        for (EntityListenerSnippet entityListener : entityListeners) {
            importSnippets.addAll(entityListener.getImportSnippets());
        }

        return importSnippets;
    }

    public void addEntityListener(EntityListenerSnippet entityListener) {

        if (entityListeners.isEmpty()) {
            entityListeners = new ArrayList<EntityListenerSnippet>();
        }

        entityListeners.add(entityListener);
    }

    public List<EntityListenerSnippet> getEntityListeners() {
        return entityListeners;
    }

    public void setEntityListeners(List<EntityListenerSnippet> listeners) {
        if (entityListeners != null) {
            this.entityListeners = listeners;
        }
    }
}
