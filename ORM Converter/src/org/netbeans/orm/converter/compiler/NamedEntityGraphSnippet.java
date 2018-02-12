/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_ENTITY_GRAPH;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_ENTITY_GRAPH_FQN;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedEntityGraphSnippet implements Snippet {

    private String name = null;
    private List<NamedAttributeNodeSnippet> namedAttributeNodes = Collections.<NamedAttributeNodeSnippet>emptyList();
    private List<NamedSubgraphSnippet> subgraphs = Collections.<NamedSubgraphSnippet>emptyList();
    private List<NamedSubgraphSnippet> subclassSubgraphs = Collections.<NamedSubgraphSnippet>emptyList();
    private Boolean includeAllAttributes;

    @Override
    public String getSnippet() throws InvalidDataException {

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(NAMED_ENTITY_GRAPH).append("(name=\"");
        builder.append(getName());
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (!namedAttributeNodes.isEmpty()) {
            builder.append("attributeNodes={");
            for (NamedAttributeNodeSnippet namedAttributeNode : namedAttributeNodes) {
                builder.append(namedAttributeNode.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);

        }

        if (!subgraphs.isEmpty()) {
            builder.append("subgraphs={");
            for (NamedSubgraphSnippet subgraph : subgraphs) {
                builder.append(subgraph.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);

        }

        if (!subclassSubgraphs.isEmpty()) {
            builder.append("subclassSubgraphs={");
            for (NamedSubgraphSnippet subclassSubgraph : subclassSubgraphs) {
                builder.append(subclassSubgraph.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);

        }

        if (includeAllAttributes != null) {
            builder.append("includeAllAttributes= ");
            builder.append(includeAllAttributes);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(NAMED_ENTITY_GRAPH_FQN);

        if (namedAttributeNodes != null && !namedAttributeNodes.isEmpty()) {
            importSnippets.addAll(namedAttributeNodes.get(0).getImportSnippets());
        }
        if (subgraphs != null && !subgraphs.isEmpty()) {
            for (NamedSubgraphSnippet subgraph : subgraphs) {
                importSnippets.addAll(subgraph.getImportSnippets());
            }
        }
        if (subclassSubgraphs != null && !subclassSubgraphs.isEmpty()) {
            for (NamedSubgraphSnippet subclassSubgraph : subclassSubgraphs) {
                importSnippets.addAll(subclassSubgraph.getImportSnippets());
            }
        }

        return importSnippets;
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
}
