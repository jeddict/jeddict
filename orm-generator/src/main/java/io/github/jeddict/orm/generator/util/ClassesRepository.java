/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.jeddict.orm.generator.spec.WritableSnippet;

public class ClassesRepository {

    private static ClassesRepository instance = new ClassesRepository();

    private Map<ClassType, List<WritableSnippet>> repository
            = new HashMap<>();

    private ClassesRepository() {
    }

    public static ClassesRepository getInstance() {
        return instance;
    }

    public void addWritableSnippet(ClassType classType, WritableSnippet writableSnippet) {

        List<WritableSnippet> writableSnippets = repository.get(classType);

        if (writableSnippets == null) {
            writableSnippets = new ArrayList<>();
        }

        writableSnippets.add(writableSnippet);

        repository.put(classType, writableSnippets);
    }

    public void clear() {
        repository = new HashMap<>();
    }

    public WritableSnippet getWritableSnippet(ClassHelper classHelper) {

        for (List<WritableSnippet> values : repository.values()) {

            WritableSnippet writableSnippet = searchSnippet(
                    values, classHelper);

            if (writableSnippet != null) {
                return writableSnippet;
            }
        }

        return null;
    }

    public List<WritableSnippet> getWritableSnippets(ClassType classType) {
        List<WritableSnippet> writableSnippets = repository.get(classType);

        if (writableSnippets == null) {
            return new ArrayList<>();
        }

        return writableSnippets;
    }

    private WritableSnippet searchSnippet(
            List<WritableSnippet> writableSnippets,
            ClassHelper classHelper) {

        for (WritableSnippet writableSnippet : writableSnippets) {

            ClassHelper snippetClassHelper = writableSnippet.getClassHelper();

            if (snippetClassHelper.getFQClassName().equals(classHelper.getFQClassName())) {
                return writableSnippet;
            }
        }

        return null;
    }
}
