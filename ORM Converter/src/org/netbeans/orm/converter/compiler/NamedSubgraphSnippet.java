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
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_SUBGRAPH;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_SUBGRAPH_FQN;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedSubgraphSnippet implements Snippet {

    private List<NamedAttributeNodeSnippet> namedAttributeNodes = Collections.EMPTY_LIST;
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

        if (name == null || namedAttributeNodes.isEmpty()) {
            throw new InvalidDataException(
                    "EntityGraph data missing, Name:" + name + " NamedAttributeNode: " + namedAttributeNodes);
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(NAMED_SUBGRAPH).append("(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (classHelper.getClassName() != null) {
            builder.append("type=");
            builder.append(getType());
            builder.append(ORMConverterUtil.COMMA);
        }

        builder.append("attributeNodes={");
        for (NamedAttributeNodeSnippet namedAttributeNode : namedAttributeNodes) {
            builder.append(namedAttributeNode.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(ORMConverterUtil.CLOSE_BRACES);
        builder.append(ORMConverterUtil.COMMA);

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(NAMED_SUBGRAPH_FQN);
        if (classHelper.getPackageName() != null) {
            importSnippets.add(classHelper.getFQClassName());
        }
        if (namedAttributeNodes != null && !namedAttributeNodes.isEmpty()) {
            importSnippets.addAll(namedAttributeNodes.get(0).getImportSnippets());
        }

        return importSnippets;
    }

}
