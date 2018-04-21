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
package io.github.jeddict.jpa.spec.extend;

import org.netbeans.modeler.properties.type.Enumy;

/**
 *
 * @author jGauravGupta
 */
public enum PaginationType implements Enumy {
    
    NO("no", "No"), 
    PAGER("pager", "Pager"), 
    PAGINATION("pagination", "Pagination"), 
    INFINITE_SCROLL("infinite-scroll", "Infinite Scroll");

    private final String keyword;
    private final String title;

    private PaginationType(String keyword, String title) {
        this.keyword = keyword;
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }
    
    @Override
    public String getDisplay() {
        return title;
    }

    @Override
    public Enumy getDefault() {
        return NO;
    }

}
