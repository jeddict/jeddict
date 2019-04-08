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

import static io.github.jeddict.jcode.JPAConstants.NAMED_QUERIES;
import static io.github.jeddict.jcode.JPAConstants.NAMED_QUERIES_FQN;

public class NamedQueriesSnippet extends SnippetContainer<NamedQuerySnippet> {

    public NamedQueriesSnippet(boolean repeatable) {
        super(repeatable);
    }

    @Override
    public String getContianerName() {
        return NAMED_QUERIES;
    }

    @Override
    public String getContianerFQN() {
        return NAMED_QUERIES_FQN;
    }

}