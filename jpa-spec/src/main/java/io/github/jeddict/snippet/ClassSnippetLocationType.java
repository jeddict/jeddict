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
package io.github.jeddict.snippet;

public enum ClassSnippetLocationType implements SnippetLocation {

    DEFAULT("Default"),
    BEFORE_FIELD("Before Field"),
    AFTER_FIELD("After Field"),
    BEFORE_METHOD("Before Method"),
    AFTER_METHOD("After Method"),
    BEFORE_CLASS("Before Class"),
    AFTER_CLASS("After Class"),
    BEFORE_PACKAGE("Before Package"),
    AFTER_PACKAGE("After Package"),
    TYPE_JAVADOC("Class Javadoc"),
    IMPORT("Import");
    
    private final String title;

    
    private ClassSnippetLocationType(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
}
