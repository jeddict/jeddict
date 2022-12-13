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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.INHERITANCE;
import static io.github.jeddict.jcode.JPAConstants.INHERITANCE_FQN;
import static io.github.jeddict.jcode.JPAConstants.INHERITANCE_TYPE;
import static io.github.jeddict.jcode.JPAConstants.INHERITANCE_TYPE_FQN;
import static java.util.Arrays.asList;
import java.util.Collection;

public class InheritanceSnippet implements Snippet {

    public static enum Type {
        SINGLE_TABLE,
        JOINED,
        TABLE_PER_CLASS
    };

    private Type statergy = Type.SINGLE_TABLE;

    public Type getStatergy() {
        return statergy;
    }

    public void setStatergy(Type statergy) {
        this.statergy = statergy;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        return annotate(
                INHERITANCE,
                attributeExp("strategy", INHERITANCE_TYPE + "." + statergy)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return asList(
                INHERITANCE_FQN,
                INHERITANCE_TYPE_FQN
        );
    }
}
