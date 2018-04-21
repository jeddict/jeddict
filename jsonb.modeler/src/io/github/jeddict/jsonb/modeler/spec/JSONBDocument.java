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
package io.github.jeddict.jsonb.modeler.spec;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import java.util.stream.IntStream;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;

public class JSONBDocument extends FlowNode {

    private JavaClass javaClass;
    private WorkSpace workSpace;

    private List<JSONBNode> nodes = new LinkedList<>();

    public JSONBDocument(JavaClass javaClass, WorkSpace workSpace) {
        this.javaClass = javaClass;
        this.workSpace = workSpace;
        this.javaClass.addLookup(JSONBDocument.class, this);
    }

    public void loadAttribute() {
        List<JSONBNode> nodes = new LinkedList<>();
        List<Attribute> attributes = this.javaClass.getAttributes().getAllAttribute();
        List<Attribute> propertyOrder = this.javaClass.getJsonbPropertyOrder();
        if (!propertyOrder.isEmpty()) {
            Map<String, Integer> attributesMap = IntStream.range(0, propertyOrder.size())
                    .boxed()
                    .collect(toMap(i -> propertyOrder.get(i).getId(), identity()));
            attributes.sort(Comparator.comparing(attr -> attributesMap.get(attr.getId())));
        }

        for (Attribute attribute : attributes) {
            JSONBNode node = null;

            if (attribute instanceof RelationAttribute && ((RelationAttribute) attribute).getConnectedEntity()!= null) {
                if (workSpace == null || workSpace.hasItem(((RelationAttribute) attribute).getConnectedEntity())) {
                    node = new JSONBBranchNode(attribute);
                }
            } else if (attribute instanceof Embedded && ((Embedded) attribute).getConnectedClass() != null) {
                if (workSpace == null || workSpace.hasItem(((Embedded) attribute).getConnectedClass())) {
                    node = new JSONBBranchNode(attribute);
                }
            } else if (attribute instanceof ElementCollection && ((ElementCollection) attribute).getConnectedClass() != null) {
                if (workSpace == null || workSpace.hasItem(((ElementCollection) attribute).getConnectedClass())) {
                    node = new JSONBBranchNode(attribute);
                }
            } else {
                node = new JSONBLeafNode(attribute);
            }
            if (node != null) {
                nodes.add(node);
            }
        }
        this.nodes = nodes;
    }

    @Override
    public String getId() {
        return javaClass.getId();
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return javaClass.getClazz();
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        javaClass.setClazz(name);
    }

    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    /**
     * @return the nodes
     */
    public List<JSONBNode> getNodes() {
        return nodes;
    }

}
