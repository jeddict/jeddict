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

import java.util.Collection;
import java.util.Collections;
import static io.github.jeddict.jcode.jpa.JPAConstants.CACHEABLE;
import static io.github.jeddict.jcode.jpa.JPAConstants.CACHEABLE_FQN;
import io.github.jeddict.settings.code.CodePanel;

public class CacheableDefSnippet implements Snippet {

    private Boolean status;

    public CacheableDefSnippet(Boolean status) {
        this.status = status;
    }
    
    
    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@").append(CACHEABLE);
        if(status!=null && !status){
            builder.append("(false)");
        } else if (CodePanel.isGenerateDefaultValue()) {
            builder.append("(true)");
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singleton(CACHEABLE_FQN);
    }
}
