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

import static io.github.jeddict.jcode.JPAConstants.NAMED_ATTRIBUTE_NODE;
import static io.github.jeddict.jcode.JPAConstants.NAMED_ATTRIBUTE_NODE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import java.util.Collection;
import static java.util.Collections.singleton;
import static io.github.jeddict.util.StringUtils.isBlank;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedAttributeNodeSnippet implements Snippet {

    private String name;
    private String subgraph;
    private String keySubgraph;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the subgraph
     */
    public String getSubgraph() {
        return subgraph;
    }

    /**
     * @param subgraph the subgraph to set
     */
    public void setSubgraph(String subgraph) {
        this.subgraph = subgraph;
    }

    /**
     * @return the keySubgraph
     */
    public String getKeySubgraph() {
        return keySubgraph;
    }

    /**
     * @param keySubgraph the keySubgraph to set
     */
    public void setKeySubgraph(String keySubgraph) {
        this.keySubgraph = keySubgraph;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(getName())) {
            return null;
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(NAMED_ATTRIBUTE_NODE)
                .append(OPEN_PARANTHESES);

        if (getSubgraph() != null && getKeySubgraph() != null) {  //todo ??
            builder.append("");
        } else {
            builder.append("value=");
        }

        builder.append(QUOTE)
                .append(getName())
                .append(QUOTE)
                .append(COMMA);

        builder.append(attribute("subgraph", getSubgraph()))
                .append(attribute("keySubgraph", getKeySubgraph()));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(NAMED_ATTRIBUTE_NODE_FQN);
    }

}
