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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class MethodDefSnippet implements Snippet {

    private static final String IMPORT_PREFIX = "javax.persistence.";

    private String methodName = null;

    private List<CallbackSnippet> callbacks = Collections.EMPTY_LIST;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void addCallback(CallbackSnippet callback) {

        if (callbacks.isEmpty()) {
            callbacks = new ArrayList<CallbackSnippet>();
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

    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (callbacks.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Collection<String> importSnippets = new TreeSet<String>();

        for (CallbackSnippet callback : callbacks) {
            importSnippets.add(IMPORT_PREFIX + callback.getCallbackType());
        }

        return importSnippets;
    }
}
