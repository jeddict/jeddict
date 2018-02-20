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
package io.github.jeddict.orm.generator.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.jcode.jpa.JPAConstants.PERSISTENCE_PACKAGE_PREFIX;
import io.github.jeddict.orm.generator.util.ImportSet;

public class MethodDefSnippet implements Snippet {

    private String methodName = null;

    private List<CallbackSnippet> callbacks = Collections.<CallbackSnippet>emptyList();

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void addCallback(CallbackSnippet callback) {

        if (callbacks.isEmpty()) {
            callbacks = new ArrayList<>();
        }

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public List<CallbackSnippet> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<CallbackSnippet> callbacks) {
        if (callbacks != null) {
            this.callbacks = callbacks;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (callbacks.isEmpty()) {
            return Collections.<String>emptyList();
        }

        ImportSet importSnippets = new ImportSet();

        for (CallbackSnippet callback : callbacks) {
            importSnippets.add(PERSISTENCE_PACKAGE_PREFIX + callback.getCallbackType());
        }

        return importSnippets;
    }
}
