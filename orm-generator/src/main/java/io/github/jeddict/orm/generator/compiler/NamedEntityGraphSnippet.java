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

import static io.github.jeddict.jcode.JPAConstants.NAMED_ENTITY_GRAPH;
import static io.github.jeddict.jcode.JPAConstants.NAMED_ENTITY_GRAPH_FQN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedEntityGraphSnippet implements Snippet {

    private String name;

    private List<NamedAttributeNodeSnippet> namedAttributeNodes = Collections.<NamedAttributeNodeSnippet>emptyList();

    private List<NamedSubgraphSnippet> subgraphs = Collections.<NamedSubgraphSnippet>emptyList();

    private List<NamedSubgraphSnippet> subclassSubgraphs = Collections.<NamedSubgraphSnippet>emptyList();

    private Boolean includeAllAttributes;

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
     * @return the namedAttributeNode
     */
    public List<NamedAttributeNodeSnippet> getNamedAttributeNodes() {
        return namedAttributeNodes;
    }

    /**
     * @param namedAttributeNodes the namedAttributeNode to set
     */
    public void setNamedAttributeNodes(List<NamedAttributeNodeSnippet> namedAttributeNodes) {
        this.namedAttributeNodes = namedAttributeNodes;
    }

    public void addNamedAttributeNode(NamedAttributeNodeSnippet namedAttributeNode) {
        if (namedAttributeNodes.isEmpty()) {
            namedAttributeNodes = new ArrayList<>();
        }
        namedAttributeNodes.add(namedAttributeNode);
    }

    /**
     * @return the subgraph
     */
    public List<NamedSubgraphSnippet> getSubgraphs() {
        return subgraphs;
    }

    /**
     * @param subgraph the subgraph to set
     */
    public void setSubgraphs(List<NamedSubgraphSnippet> subgraph) {
        this.subgraphs = subgraph;
    }

    public void addSubgraph(NamedSubgraphSnippet subgraph) {
        if (subgraphs.isEmpty()) {
            subgraphs = new ArrayList<>();
        }
        subgraphs.add(subgraph);
    }

    /**
     * @return the subclassSubgraph
     */
    public List<NamedSubgraphSnippet> getSubclassSubgraphs() {
        return subclassSubgraphs;
    }

    /**
     * @param subclassSubgraph the subclassSubgraph to set
     */
    public void setSubclassSubgraphs(List<NamedSubgraphSnippet> subclassSubgraph) {
        this.subclassSubgraphs = subclassSubgraph;
    }

    public void addSubclassSubgraph(NamedSubgraphSnippet subclassSubgraph) {
        if (subclassSubgraphs.isEmpty()) {
            subclassSubgraphs = new ArrayList<>();
        }
        subclassSubgraphs.add(subclassSubgraph);
    }

    /**
     * @return the includeAllAttributes
     */
    public Boolean getIncludeAllAttributes() {
        return includeAllAttributes;
    }

    /**
     * @param includeAllAttributes the includeAllAttributes to set
     */
    public void setIncludeAllAttributes(Boolean includeAllAttributes) {
        this.includeAllAttributes = includeAllAttributes;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        return annotate(
                NAMED_ENTITY_GRAPH,
                attribute("name", getName()),
                attributes("attributeNodes", namedAttributeNodes),
                attributes("subgraphs", subgraphs),
                attributes("subclassSubgraphs", subclassSubgraphs),
                attributeExp("includeAllAttributes", includeAllAttributes)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();

        imports.add(NAMED_ENTITY_GRAPH_FQN);

        if (namedAttributeNodes != null && !namedAttributeNodes.isEmpty()) {
            imports.addAll(namedAttributeNodes.get(0).getImportSnippets());
        }
        if (subgraphs != null && !subgraphs.isEmpty()) {
            for (NamedSubgraphSnippet subgraph : subgraphs) {
                imports.addAll(subgraph.getImportSnippets());
            }
        }
        if (subclassSubgraphs != null && !subclassSubgraphs.isEmpty()) {
            for (NamedSubgraphSnippet subclassSubgraph : subclassSubgraphs) {
                imports.addAll(subclassSubgraph.getImportSnippets());
            }
        }

        return imports;
    }
}
