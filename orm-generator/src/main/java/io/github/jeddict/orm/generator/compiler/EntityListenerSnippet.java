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

import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.Collection;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class EntityListenerSnippet {

    private final ClassHelper classHelper = new ClassHelper();

//    private List<CallbackSnippet> callbacks = Collections.EMPTY_LIST;

    public String getClassName() {
        return classHelper.getClassName();
    }

    public void setClassName(String className) {
        classHelper.setClassName(className);
    }

//    public void addCallback(CallbackSnippet callback) {
//
//        if (callbacks.isEmpty()) {
//            callbacks = new ArrayList<CallbackSnippet>();
//        }
//
//        callbacks.add(callback);
//    }
//
//    public List<CallbackSnippet> getCallbacks() {
//        return callbacks;
//    }
//
//    public void setCallbacks(List<CallbackSnippet> listeners) {
//        if (callbacks != null) {
//            this.callbacks = listeners;
//        }
//    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public ClassHelper getClassHelper() {
        return classHelper;
    }

    public String getSnippet() throws InvalidDataException {
        return classHelper.getClassNameWithClassSuffix();
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        if (classHelper.getPackageName() == null) {
            return emptySet();
        }
        return singleton(classHelper.getFQClassName());
    }
}
