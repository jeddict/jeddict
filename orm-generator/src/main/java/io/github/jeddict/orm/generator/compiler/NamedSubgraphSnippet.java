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

import static io.github.jeddict.jcode.JPAConstants.NAMED_SUBGRAPH;
import static io.github.jeddict.jcode.JPAConstants.NAMED_SUBGRAPH_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isBlank;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedSubgraphSnippet implements Snippet {

    private List<NamedAttributeNodeSnippet> namedAttributeNodes = Collections.<NamedAttributeNodeSnippet>emptyList();
    private String name;
    private ClassHelper classHelper = new ClassHelper();

    /**
     * @return the namedAttributeNode
     */
    public List<NamedAttributeNodeSnippet> getNamedAttributeNode() {
        return namedAttributeNodes;
    }

    /**
     * @param namedAttributeNode the namedAttributeNode to set
     */
    public void setNamedAttributeNode(List<NamedAttributeNodeSnippet> namedAttributeNode) {
        this.namedAttributeNodes = namedAttributeNode;
    }

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

    public String getType() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setType(String type) {
        classHelper.setClassName(type);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name) || namedAttributeNodes.isEmpty()) {
            throw new InvalidDataException(
                    "EntityGraph data missing, Name:" + name + " NamedAttributeNode: " + namedAttributeNodes);
        }

        return annotate(
                NAMED_SUBGRAPH,
                attribute("name", name),
                attribute("type", getType(), val -> classHelper.getClassName() != null),
                attributes("attributeNodes", namedAttributeNodes)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(NAMED_SUBGRAPH_FQN);
        if (classHelper.getPackageName() != null) {
            imports.add(classHelper.getFQClassName());
        }
        if (namedAttributeNodes != null && !namedAttributeNodes.isEmpty()) {
            imports.addAll(namedAttributeNodes.get(0).getImportSnippets());
        }
        return imports;
    }

}
