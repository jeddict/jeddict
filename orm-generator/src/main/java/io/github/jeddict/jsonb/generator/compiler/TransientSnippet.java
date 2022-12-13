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
package io.github.jeddict.jsonb.generator.compiler;

import static io.github.jeddict.jcode.JSONBConstants.JSONB_TRANSIENT;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_TRANSIENT_FQN;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import java.util.Collection;
import static java.util.Collections.singleton;

public class TransientSnippet implements Snippet {

    @Override
    public String getSnippet() throws InvalidDataException {
        return AT + JSONB_TRANSIENT;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(JSONB_TRANSIENT_FQN);
    }
}
